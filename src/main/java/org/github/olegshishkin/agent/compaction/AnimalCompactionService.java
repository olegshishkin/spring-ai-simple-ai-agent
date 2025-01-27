package org.github.olegshishkin.agent.compaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnimalCompactionService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String VECTOR_FILE_NAME = "animal-compaction-vectors.json";

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;

    @Value("classpath:animal-compaction-source.json")
    private Resource animalCompactionSource;

    @SneakyThrows
    @PostConstruct
    void setUp() {
        // Файл для сохранения сформированных векторов, чтобы не рассчитывать их при последующих запусках.
        var vectors = new File(TEMP_DIR + VECTOR_FILE_NAME);
        if (vectorStore instanceof SimpleVectorStore && vectors.exists()) {
            log.info("Найден файл векторов: {}. Загружаем данные в векторное хранилище", vectors);
            ((SimpleVectorStore) vectorStore).load(vectors);
            return;
        }

        log.info("Загружаем данные в векторное хранилище из {}", animalCompactionSource.getURI());
        vectorStore.add(new JsonReader(animalCompactionSource).get());

        if (vectorStore instanceof SimpleVectorStore) {
            if (!vectors.createNewFile()) {
                throw new FileNotFoundException();
            }
            log.info("Сохраняем векторное представление в файл {}: ", vectors);
            ((SimpleVectorStore) vectorStore).save(vectors);
        }
    }

    @SneakyThrows
    public AnimalCompaction getCompaction(AnimalCompaction compaction) {

        // Формируем объект запроса в векторное хранилище.
        var rq = SearchRequest.builder()
                .topK(1) // ищем один ближайший
                .similarityThresholdAll()
                .query(objectMapper.writeValueAsString(compaction))
                .build();

        // Семантический поиск в векторном хранилище.
        return Optional.ofNullable(vectorStore.similaritySearch(rq))
                .flatMap(docs -> docs.stream().findAny())
                .map(this::readAnimalCompaction)
                .orElseThrow();
    }

    @SneakyThrows
    private AnimalCompaction readAnimalCompaction(Document doc) {
        var text = doc.getText();
        if (text == null || text.length() <= 2) {
            throw new IllegalArgumentException();
        }
        var map = Splitter.on(", ")
                .withKeyValueSeparator("=")
                .split(text.substring(1, text.length() - 1));
        return objectMapper.convertValue(map, AnimalCompaction.class);
    }
}

package org.github.olegshishkin.agent;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.github.olegshishkin.agent.compaction.AnimalCompaction;
import org.github.olegshishkin.agent.compaction.AnimalCompactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Slf4j
@Configuration
public class MyTools {

    @Bean
    @Description("Этот метод вызывает эндпоинт для расчета площади комнаты")
    public Function<GetSquareRq, Double> getSquare() {
        return rq -> {
            log.info("Площадь комнаты для {} * {}", rq.width, rq.length);
            return rq.width * rq.length;
        };
    }

    @Bean
    @Description("Этот метод определяет цвет комнаты")
    public Supplier<String> getColor() {
        return () -> {
            log.info("Цвет комнаты");
            return "зеленый";
        };
    }

    @Bean
    @Description("Этот метод возвращает цвет слона")
    public Supplier<String> getElephantColor() {
        return () -> {
            log.info("Цвет слона");
            return "зеленый";
        };
    }

    @Bean
    @Description("Этот метод возвращает площадь слона")
    public Function<GetElephantSizeRq, String> getElephantSize() {
        return rq -> {
            log.info("Размер слона. Bash-скрипт поиска файла {}: {}", rq.fileName, rq.script);
            return "7 квадратных метров";
        };
    }

    @Bean
    @Description("Этот метод возвращает длину слона")
    public Supplier<String> getElephantLength() {
        return () -> {
            log.info("Длина слона");
            return "3 метра";
        };
    }

    @Bean
    @Description("Этот метод возвращает ширину слона")
    public Supplier<String> getElephantWidth() {
        return () -> {
            log.info("Ширина слона");
            return "1 метр";
        };
    }

    @Bean
    @Description("Метод возвращает компактность животных - способность уплотниться, чтобы больше поместилось в ограниченном пространстве")
    public Function<AnimalCompaction, AnimalCompaction> getAnimalCompaction(
            AnimalCompactionService animalCompactionService) {
        return rq -> {
            log.info("Компактность животного: {}", rq);
            var compaction = animalCompactionService.getCompaction(rq);
            log.info("Компактность животного для {}: {}", rq, compaction);
            return compaction;
        };
    }

    @Bean
    @Description("Континент обитания слонов")
    public Supplier<String> getArea() {
        return () -> {
            log.info("Континент обитания");
            return "Африка";
        };
    }

    public record GetSquareRq(@Description("Ширина комнаты") Double width,
                              @Description("Длина комнаты") Double length) {

    }

    public record GetElephantSizeRq(@Description("Имя файла") String fileName,
                                    @Description("Контент shell-скрипта для поиска файла") String script) {

    }
}

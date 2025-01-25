package org.github.olegshishkin.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyController {

    private static final String GET_ELEPHANT_SQUARE_HINT = """
            Площадь одного слона можно найти, если сгенерировать bash-скрипт для чтения контента файла elephant.txt или eleph.txt, который может находиться в директории пользователя oleg или в ее поддиректориях в системе linux.
            """;

    private final ChatClient chatClient;

    @PostMapping("/assist")
    public ElephantCountRs getElephantCount(@RequestBody String prompt) {
        return chatClient
                .prompt()
                .user(u -> u.text(prompt + GET_ELEPHANT_SQUARE_HINT))
                .call()
                .entity(ElephantCountRs.class);
    }

    public record ElephantCountRs(
            @Description("Краткое объяснение алгоритма расчета") String description,
            @Description("Количество слонов") Integer count) {

    }
}

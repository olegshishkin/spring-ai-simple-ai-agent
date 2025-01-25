package org.github.olegshishkin.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class MyController {

    public static final String GET_ELEPHANT_SQUARE_HINT = " Площадь одного слона можно найти, если "
            + "сгенерировать bash-скрипт для чтения контента файла elephant.txt или eleph.txt, "
            + "который может находиться в директории пользователя oleg или в ее поддиректориях в "
            + "системе linux";

    private final ChatClient chatClient;

    @PostMapping("/assist")
    public Flux<String> getElephantCount(@RequestBody String prompt) {
        return chatClient
                .prompt()
                .user(u -> u.text(prompt + GET_ELEPHANT_SQUARE_HINT))
                .stream()
                .content();
    }
}

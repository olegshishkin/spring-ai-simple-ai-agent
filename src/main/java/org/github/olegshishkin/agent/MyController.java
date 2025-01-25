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

    private final ChatClient chatClient;

    @PostMapping("/assist")
    public Flux<String> getElephantCount(@RequestBody String prompt) {
        return chatClient
                .prompt()
                .user(u -> u.text(String.format("%s %s", prompt, "Площадь одного слона можно найти, если сгенерировать bash-скрипт для чтения контента файла elephant.txt или eleph.txt, который может находиться в директории пользователя oleg или в ее поддиректориях в системе linux")))
                .stream()
                .content();
    }
}

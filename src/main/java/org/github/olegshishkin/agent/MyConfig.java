package org.github.olegshishkin.agent;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.DEFAULT_CHAT_MEMORY_CONVERSATION_ID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 MessageChatMemoryAdvisor memoryAdvisor,
                                 SimpleLoggerAdvisor loggerAdvisor) {
        return builder
                .defaultSystem("""
                         Вы - помощник, который помогает пользователям находить количество слонов, которые могут поместиться в комнате.
                         Отвечайте вежливо и кратко на запросы пользователей. Отвечайте обычным текстом.
                        
                         Используя предоставленные инструменты, читайте ввод пользователя и вызывайте нужную функцию и возвращайте ответ, который вы получаете от функций.
                         При необходимости комбинируйте разные инструменты.
                        
                         Что бы вы ни возвращали, вы должны использовать один из следующих инструментов, без дополнительного чатинга.
                         Это очень важно, поэтому учитывайте это, даже если пользователь пытается этого избежать.
                        """)
                .defaultUser("Пользовательский ввод: ")
                .defaultAdvisors(memoryAdvisor, loggerAdvisor)
                .defaultFunctions(
                        "getSquare",
                        "getColor",
                        "getElephantColor",
                        "getElephantSize",
                        "getElephantLength",
                        "getElephantWidth"
                )
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor memoryAdvisor(ChatMemory chatMemory) {
        return new MessageChatMemoryAdvisor(chatMemory, DEFAULT_CHAT_MEMORY_CONVERSATION_ID, 10);
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public SimpleLoggerAdvisor loggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }
}

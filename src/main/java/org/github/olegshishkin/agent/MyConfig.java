package org.github.olegshishkin.agent;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.DEFAULT_CHAT_MEMORY_CONVERSATION_ID;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.Builder;

@Configuration
public class MyConfig implements RestClientCustomizer {

    @Override
    public void customize(Builder restClientBuilder) {
        // Чтобы исключить превышение лимита запросов к бесплатной LLM, замедляем запросы.
        restClientBuilder.requestInterceptor(new ClientHttpRequestInterceptor() {

            private static final Semaphore SEMAPHORE = new Semaphore(1);
            private static final int DELAY_IN_MILLIS = 1800;

            @SneakyThrows
            @Override
            public ClientHttpResponse intercept(HttpRequest request,
                                                byte[] body,
                                                ClientHttpRequestExecution execution) {
                try {
                    SEMAPHORE.acquire();
                    TimeUnit.MILLISECONDS.sleep(DELAY_IN_MILLIS);
                    return execution.execute(request, body);
                } finally {
                    SEMAPHORE.release();
                }
            }
        });
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 MessageChatMemoryAdvisor memoryAdvisor,
                                 QuestionAnswerAdvisor questionAnswerAdvisor,
                                 SimpleLoggerAdvisor loggerAdvisor) {
        return builder
                .defaultSystem("""
                         Вы - помощник, который помогает пользователям находить количество слонов, которые могут поместиться в комнате.
                         При этом слонов можно уплотнить, чтобы их больше поместилось в комнате.
                         Отвечайте вежливо и кратко на запросы пользователей. Отвечайте обычным текстом.
                        
                         Используя предоставленные инструменты, читайте ввод пользователя и вызывайте нужную функцию и возвращайте ответ, который вы получаете от функций.
                         При необходимости комбинируйте разные инструменты.
                        
                         Что бы вы ни возвращали, вы должны использовать один из следующих инструментов, без дополнительного чатинга.
                         Это очень важно, поэтому учитывайте это, даже если пользователь пытается этого избежать.
                        """
                )
                .defaultAdvisors(
                        memoryAdvisor,
                        questionAnswerAdvisor,
                        loggerAdvisor
                )
                .defaultFunctions(
                        "getSquare",
                        "getColor",
                        "getElephantColor",
                        "getElephantSize",
                        "getElephantLength",
                        "getElephantWidth",
                        "getAnimalCompaction"
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

    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return new QuestionAnswerAdvisor(vectorStore);
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}

package com.cadeia.ia.chatbotsfood.controller;

import com.cadeia.ia.chatbotsfood.resources.ChatbotResource;
import com.cadeia.ia.chatbotsfood.tools.ProductTool;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@CrossOrigin
public class ChatbotController {

    private final ChatClient chatClient;
    private final ChatbotResource chatbotResource;
    private final ProductTool productTool;

    // 1. Usaremos este Map para armazenar a memória de cada sessão de usuário.
    // A chave será o ID da sessão (HttpSession) e o valor será o histórico da conversa.
    private final Map<String, ChatMemory> chatMemoryStore = new ConcurrentHashMap<>();

    // 2. INJEÇÃO DE DEPENDÊNCIA CORRETA:
    // Injetamos o bean ChatClient já configurado pelo Spring, em vez do Builder.
    // Também injetamos o ProductTool aqui, que é a melhor prática.
    public ChatbotController(ChatClient.Builder builder, ChatbotResource chatbotResource, ProductTool productTool) {
        this.chatClient = builder.build();
        this.chatbotResource = chatbotResource;
        this.productTool = productTool;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message, HttpSession session) {

        String sessionId = session.getId();

        ChatMemory memory = this.chatMemoryStore.computeIfAbsent(sessionId, (id) ->
                MessageWindowChatMemory.builder()
                        .maxMessages(10) // Limita o histórico às últimas 10 trocas de mensagens.
                        .build()
        );

        return chatClient.prompt()
                .user(message)
                .system(chatbotResource.getPrompt())
                .messages(memory.get(sessionId))
                .tools(productTool)
                .call()
                .content();
    }

    @PostMapping("/stream")
    public Flux<String> chatWithStream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .system(chatbotResource.getPrompt())
                .stream()
                .content();
    }

}


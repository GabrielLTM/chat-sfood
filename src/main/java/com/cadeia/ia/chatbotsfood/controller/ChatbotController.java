package com.cadeia.ia.chatbotsfood.controller;

import com.cadeia.ia.chatbotsfood.entity.Chat;
import com.cadeia.ia.chatbotsfood.prompt.PromptResource;
import com.cadeia.ia.chatbotsfood.repository.ChatRepository;
import com.cadeia.ia.chatbotsfood.tools.ProductTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class ChatbotController {

    private static final Logger log = LoggerFactory.getLogger(ChatbotController.class);
    private final ChatClient chatClient;
    private final ProductTool productTool;
    private final ChatRepository chatRepository;

    private final PromptResource promptResource;


    public ChatbotController(ChatClient.Builder builder, ChatRepository chatRepository, ProductTool productTool, PromptResource promptResource) {
        this.chatClient = builder.build();
        this.chatRepository = chatRepository;
        this.productTool = productTool;
        this.promptResource = promptResource;
    }

    public record ChatRequest(String chatId, String message){}

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {

        var chatId = request.chatId();
        var message = request.message();

        Chat userMessage = new Chat();
        userMessage.setChatId(chatId);
        userMessage.setRole(Chat.Role.USER);
        userMessage.setText(message);
        chatRepository.save(userMessage);

        List<Chat> history = chatRepository.findByChatIdOrderByTimestampAsc(chatId);

        List<Message> messages = history.stream()
                .map(m -> m.getRole() == Chat.Role.USER
                        ? new UserMessage(m.getText())
                        : new AssistantMessage(m.getText()))
                .collect(Collectors.toList());



        var response = chatClient.prompt()
                        .system(promptResource.getPrompt())
                        .tools(productTool)
                        .user(message)
                        .messages(messages)
                        .call()
                        .content();

        Chat assistantMessage = new Chat();
        assistantMessage.setChatId(chatId);
        assistantMessage.setRole(Chat.Role.ASSISTANT);
        assistantMessage.setText(response);
        chatRepository.save(assistantMessage);

        return response;
    }

    @PostMapping("/stream")
    public Flux<String> chatWithStream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .system(promptResource.getPrompt())
                .stream()
                .content();
    }

}


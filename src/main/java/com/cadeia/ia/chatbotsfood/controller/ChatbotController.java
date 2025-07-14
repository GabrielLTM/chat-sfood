package com.cadeia.ia.chatbotsfood.controller;

import com.cadeia.ia.chatbotsfood.entity.Chat;
import com.cadeia.ia.chatbotsfood.repository.ChatRepository;
import com.cadeia.ia.chatbotsfood.tools.ProductTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class ChatbotController {

    private final ChatClient chatClient;
    private final ProductTool productTool;
    private final ChatRepository chatRepository;

    @Value("classpath:/prompts/chatbot.st")
    private Resource chatbotResource;


    public ChatbotController(ChatClient.Builder builder, ChatRepository chatRepository, ProductTool productTool) {
        this.chatClient = builder.build();
        this.chatRepository = chatRepository;
        this.productTool = productTool;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String chatId, @RequestParam String message) {

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
                        .user(message)
                        .system(chatbotResource)
                        .messages(messages)
                        .tools(productTool)
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
                .system(chatbotResource)
                .stream()
                .content();
    }

}


package com.cadeia.ia.chatbotsfood.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String chatId;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String text;

    @Column
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum Role{
        USER, ASSISTANT
    }

    public Chat() {
    }

    public Chat(String chatId, Role role, String text) {
        this.chatId = chatId;
        this.role = role;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

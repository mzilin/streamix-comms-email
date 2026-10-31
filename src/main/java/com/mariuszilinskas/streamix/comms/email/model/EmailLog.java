package com.mariuszilinskas.streamix.comms.email.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "email_log", schema = "streamix_email")
public class EmailLog {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private EmailLogStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public EmailLog(UUID userId, String type, EmailLogStatus status) {
        this.userId = userId;
        this.type = type;
        this.status = status;
        this.createdAt = Instant.now();
    }
}
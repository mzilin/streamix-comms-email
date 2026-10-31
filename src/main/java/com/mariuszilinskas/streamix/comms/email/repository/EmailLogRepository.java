package com.mariuszilinskas.streamix.comms.email.repository;

import com.mariuszilinskas.streamix.comms.email.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
}
package com.mariuszilinskas.streamix.comms.email.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WelcomeEmailRequest extends EmailRequest {

    public WelcomeEmailRequest(UUID userId, String firstName, String email) {
        super("welcome", userId, firstName, email);
    }
}

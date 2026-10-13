package com.mariuszilinskas.streamix.comms.email.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WelcomeEmailRequest extends EmailRequest {

    public WelcomeEmailRequest(String firstName, String email) {
        super("welcome", firstName, email);
    }
}

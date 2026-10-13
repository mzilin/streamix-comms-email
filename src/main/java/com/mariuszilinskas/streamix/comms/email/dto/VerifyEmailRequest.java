package com.mariuszilinskas.streamix.comms.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.CANNOT_BE_BLANK;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequest extends EmailRequest {

    @NotBlank(message = "verificationToken " + CANNOT_BE_BLANK)
    private String verificationToken;

}


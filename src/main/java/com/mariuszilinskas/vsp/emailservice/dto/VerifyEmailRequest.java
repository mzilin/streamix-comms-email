package com.mariuszilinskas.vsp.emailservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequest extends EmailRequest {

    @NotBlank(message = "passcode cannot be blank")
    private String passcode;
}


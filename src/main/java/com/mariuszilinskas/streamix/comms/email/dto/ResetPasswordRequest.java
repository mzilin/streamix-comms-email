package com.mariuszilinskas.streamix.comms.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest extends EmailRequest {

    @NotBlank(message = "resetToken cannot be blank")
    private String resetToken;

}


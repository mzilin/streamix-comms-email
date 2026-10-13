package com.mariuszilinskas.streamix.comms.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest extends EmailRequest {

    @NotBlank(message = "resetToken " + CANNOT_BE_BLANK)
    private String resetToken;

}

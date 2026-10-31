package com.mariuszilinskas.streamix.comms.email.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WelcomeEmailRequest.class, name = "welcome"),
        @JsonSubTypes.Type(value = VerifyEmailRequest.class, name = "verify"),
        @JsonSubTypes.Type(value = ResetPasswordRequest.class, name = "reset")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class EmailRequest {

    @NotBlank(message = "type " + CANNOT_BE_BLANK)
    private String type;

    @NotNull(message = "userId " + CANNOT_BE_NULL)
    private UUID userId;

    @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
    private String firstName;

    @NotBlank(message = "email " + CANNOT_BE_BLANK)
    @Email(message = INVALID_EMAIL)
    private String email;

}
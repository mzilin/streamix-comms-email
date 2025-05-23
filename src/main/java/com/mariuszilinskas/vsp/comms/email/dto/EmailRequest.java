package com.mariuszilinskas.vsp.comms.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotBlank(message = "type cannot be blank")
    private String type;

    @NotBlank(message = "firstName cannot be blank")
    private String firstName;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email should be valid")
    private String email;

}



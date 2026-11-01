package com.mariuszilinskas.streamix.comms.email.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {

    VERIFY_ACCOUNT("verifyAccount.html", "Verify Your Account"),
    WELCOME("welcome.html", "Welcome to Streamix!"),
    RESET_PASSWORD("resetPassword.html", "Reset Your Password");

    private final String template;
    private final String subject;

}

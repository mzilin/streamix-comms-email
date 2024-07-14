package com.mariuszilinskas.vsp.emailservice.service;

import com.mariuszilinskas.vsp.emailservice.dto.*;

public interface EmailService {

    void sendVerifyAccountEmail(VerifyEmailRequest request);

    void sendWelcomeEmail(EmailRequest request);

    void sendResetPasswordEmail(ResetPasswordRequest request);

}

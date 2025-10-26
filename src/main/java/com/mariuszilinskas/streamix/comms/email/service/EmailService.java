package com.mariuszilinskas.streamix.comms.email.service;

import com.mariuszilinskas.streamix.comms.email.dto.*;

public interface EmailService {

    void sendVerifyAccountEmail(VerifyEmailRequest request);

    void sendWelcomeEmail(EmailRequest request);

    void sendResetPasswordEmail(ResetPasswordRequest request);

}

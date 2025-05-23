package com.mariuszilinskas.vsp.comms.email.service;

import com.mariuszilinskas.vsp.comms.email.dto.*;

public interface EmailService {

    void sendVerifyAccountEmail(VerifyEmailRequest request);

    void sendWelcomeEmail(EmailRequest request);

    void sendResetPasswordEmail(ResetPasswordRequest request);

}

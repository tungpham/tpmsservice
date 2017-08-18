package com.ethan.morephone.twilio.email;

import org.springframework.mail.SimpleMailMessage;

/**
 * Created by truongnguyen on 8/18/17.
 */
public interface EmailService {

    void sendSimpleMessage(String to,
                           String subject,
                           String text);

    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        SimpleMailMessage template,
                                        String... templateArgs);

    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);
}

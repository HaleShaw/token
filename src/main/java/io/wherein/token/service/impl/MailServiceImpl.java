package io.wherein.token.service.impl;

import io.wherein.token.service.MailService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Mail service implement.
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

  @Resource
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromAddr;


  /**
   * Send text mail.
   *
   * @param toAddr addressee's address.
   * @param subject mail subject.
   * @param content mail content.
   */
  @Override
  public void sendMail(String toAddr, String subject, String content) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddr);
    message.setTo(toAddr);
    message.setSubject(subject);
    message.setText(content);

    try {
      mailSender.send(message);
      log.info("The mail has been sent.");
    } catch (Exception e) {
      log.error("Send mail error!", e);
    }
  }
}

package io.wherein.token.service;

/**
 * Mail Service.
 */
public interface MailService {

  /**
   * Send text mail.
   *
   * @param toAddr addressee's address.
   * @param subject mail subject.
   * @param content mail content.
   */
  void sendMail(String toAddr, String subject, String content);
}

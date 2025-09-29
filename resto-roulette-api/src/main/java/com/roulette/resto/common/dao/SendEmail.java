package com.roulette.resto.common.dao;

import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.roulette.resto.common.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class SendEmail {
	private static final String DOMAIN = "test-eqvygm0mwkzl0p7w.mlsender.net";
	private static final String ISSUER_NAME = "Admin";
	@Value("${mail.token}")
	private String token;

	@Value("${mail.verification_url}")
	public static String verificationUrl;
	public void sendEmail(EmailContent emailContent) {
		Email email = new Email();
		email.setFrom(ISSUER_NAME, ISSUER_NAME+"@"+DOMAIN);
		email.addRecipient(emailContent.getRecipientName(), emailContent.getRecipientEmail());
		email.setSubject(emailContent.getSubject());

		email.setPlain(emailContent.getBody());
//		email.setHtml("<p>This is the HTML content</p>");
		MailerSend ms = new MailerSend();
		ms.setToken(token);
		try {
			MailerSendResponse response = ms.emails().send(email);
			System.out.println(response.messageId);
		} catch (MailerSendException e) {
			e.printStackTrace();
		}

	}
}

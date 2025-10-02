package com.roulette.resto.common.service;

import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.common.dao.EmailContent;
import com.roulette.resto.common.dao.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

	private final SendEmail sendEmail;

	public MailService(SendEmail sendEmail) {
		this.sendEmail = sendEmail;
	}

	public EmailContent verificationMailContent(Account account, String verificationurl) {
		EmailContent emailContent = new EmailContent();
		emailContent.setRecipientEmail(account.getUserInfo().getEmail());
		emailContent.setRecipientName(account.getUsername());
		StringBuilder sb =  new StringBuilder();
		sb.append("Bonjour et bienvenu ").append(account.getUsername()).append(" ! \n");
		sb.append("Nous sommes ravis de te voir parmis nous.").append("\n");
		sb.append("Tu reçois ce mail pour vérifier ton compte.").append("\n");
		sb.append("Clique sur ce lien ").append(verificationurl).append("\n");
		sb.append("puis entre ce code : ").append(account.getVerificationToken()).append(".\n");
		sb.append("Et bon appétit :p");
		emailContent.setBody(sb.toString());
		emailContent.setSubject("Bienvenue sur resto-roulette");
		return emailContent;
	}

	public void sendVerificationMail(Account account) {
		log.info("Sending verification mail");
		EmailContent emailContent = this.verificationMailContent(account, "https://todo.todo/todo");
		try {
			sendEmail.sendEmail(emailContent);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}

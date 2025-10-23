package com.roulette.resto.common.service;

import com.roulette.resto.common.dao.EmailContent;
import com.roulette.resto.common.dao.SendEmail;
import com.roulette.resto.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

	private final SendEmail sendEmail;

	public MailService(SendEmail sendEmail) {
		this.sendEmail = sendEmail;
	}

	public void sendVerificationMail(Account account) {
		log.info("Sending verification mail");
		EmailContent emailContent = this.verificationMailContent(account, "https://todo.todo/todo");
		try {
			sendEmail.sendEmail(emailContent);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public EmailContent verificationMailContent(Account account, String verificationurl) {
		EmailContent emailContent = new EmailContent();
		emailContent.setRecipientEmail(account.getUserInfo().getEmail());
		emailContent.setRecipientName(account.getUsername());
		String sb = "Bonjour et bienvenu " + account.getUsername() + " ! \n" +
				"Nous sommes ravis de te voir parmis nous." + "\n" +
				"Tu reçois ce mail pour vérifier ton compte." + "\n" +
				"Clique sur ce lien " + verificationurl + "\n" +
				"puis entre ce code : " + account.getVerificationToken() + ".\n" +
				"Et bon appétit :p";
		emailContent.setBody(sb);
		emailContent.setSubject("Bienvenue sur resto-roulette");
		return emailContent;
	}

	public void sendSecurityCode(Account account) {
		log.info("Sending verification mail");
		EmailContent emailContent = this.verificationCodeMailContent(account);
		try {
			sendEmail.sendEmail(emailContent);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public EmailContent verificationCodeMailContent(Account account) {
		EmailContent emailContent = new EmailContent();
		emailContent.setRecipientEmail(account.getUserInfo().getEmail());
		emailContent.setRecipientName(account.getUsername());
		String sb = "Bonjour et bienvenu " + account.getUsername() + " ! \n" +
				"Tu as initié une action qui requiert un code de sécurité." +
				"\n" +
				"Le code de sécurité est : " + account.getVerificationToken() + ".\n" +
				"A la prochaine, et d'ici là, bonnes dégustations ;)";
		emailContent.setBody(sb);
		emailContent.setSubject("Ton code de sécurité");
		return emailContent;
	}
}

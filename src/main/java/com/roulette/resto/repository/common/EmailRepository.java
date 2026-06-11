package com.roulette.resto.repository.common;

import com.roulette.resto.dao.common.SendEmailDao;
import com.roulette.resto.data.common.dto.EmailContent;
import com.roulette.resto.data.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Repository
@Slf4j
public class EmailRepository {
	final SendEmailDao sendEmailDao;
	private final TemplateEngine templateEngine;

	public EmailRepository(SendEmailDao sendEmailDao, TemplateEngine templateEngine) {
		this.sendEmailDao = sendEmailDao;
		this.templateEngine = templateEngine;
	}

	public void sendEmail(EmailContent emailContent) {
		sendEmailDao.sendEmail(emailContent);
	}

	public EmailContent veryMailContent(Account account, String verificationurl) {
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

	public EmailContent fillSecurityCodeMail(Account account) {
		Context context = new Context();
		context.setVariable("username", account.getUsername());
		context.setVariable("token", account.getVerificationToken());
		context.setVariable("logoUrl", "https://resto-roulette.app/medias/logo");
		String htmlBody = templateEngine.process("security-code", context);

		EmailContent emailContent = new EmailContent();
		emailContent.setRecipientEmail(account.getUserInfo().getEmail());
		emailContent.setRecipientName(account.getUsername());
		emailContent.setSubject("Ton code de sécurité");
		emailContent.setBody(htmlBody);

		return emailContent;
	}
}

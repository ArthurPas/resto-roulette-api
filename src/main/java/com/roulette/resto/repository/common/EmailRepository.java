package com.roulette.resto.repository.common;

import com.roulette.resto.dao.common.SendEmailDao;
import com.roulette.resto.data.common.dto.EmailContent;
import com.roulette.resto.data.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class EmailRepository {
	@Autowired
    final SendEmailDao sendEmailDao;

	public EmailRepository(SendEmailDao sendEmailDao) {
		this.sendEmailDao = sendEmailDao;
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

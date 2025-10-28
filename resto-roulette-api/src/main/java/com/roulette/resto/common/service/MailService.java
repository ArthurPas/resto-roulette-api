package com.roulette.resto.common.service;

import com.roulette.resto.common.dto.EmailContent;
import com.roulette.resto.common.repository.EmailRepository;
import com.roulette.resto.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

	private final EmailRepository emailRepository;

	public MailService(EmailRepository emailRepository) {
		this.emailRepository = emailRepository;
	}

	public void sendVerificationMail(Account account) {
		log.info("Sending verification mail");
		EmailContent emailContent = this.fillVerificationMailContent(account, "https://callback.todo/todo");
		try {
			emailRepository.sendEmail(emailContent);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public EmailContent fillVerificationMailContent(Account account, String verificationurl) {
		return emailRepository.veryMailContent(account, verificationurl);
	}

	public void sendSecurityCode(Account account) {
		log.info("Sending security code mail");
		EmailContent emailContent = this.fillSecurityCodeMail(account);
		try {
			emailRepository.sendEmail(emailContent);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public EmailContent fillSecurityCodeMail(Account account) {
		return emailRepository.fillSecurityCodeMail(account);
	}
}

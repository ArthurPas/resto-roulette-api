package com.roulette.resto.dao.common;

import com.roulette.resto.data.common.dto.EmailContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SendEmailDao {
	private static final String ISSUER_NAME = "L'équipe resto-roulette";
	private static final String ISSUER_ADDRESS = "noreply@resto-roulette.app"; //TODO replace with custom domain
	private static final String API_URL = "https://api.brevo.com/v3/smtp/email";
	private final RestTemplate restTemplate = new RestTemplate();
	@Value("${mail.token}")
	private String API_KEY;

	public void sendEmail(EmailContent emailContent) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.set("api-key", API_KEY.trim());

		Map<String, Object> body = new HashMap<>();
		Map<String, String> sender = Map.of(
				"name", ISSUER_NAME,
				"email", ISSUER_ADDRESS
		);
		Map<String, String> recipient = Map.of(
				"email", emailContent.getRecipientEmail(),
				"name", emailContent.getRecipientName()
		);

		body.put("sender", sender);
		body.put("to", List.of(recipient));
		body.put("subject", emailContent.getSubject());
		body.put("htmlContent", emailContent.getBody());


		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		restTemplate.postForEntity(API_URL, request, String.class);

	}
}

package com.roulette.resto.data.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailContent {
	private String recipientName;
	private String recipientEmail;

	private String subject;
	private String body;
}

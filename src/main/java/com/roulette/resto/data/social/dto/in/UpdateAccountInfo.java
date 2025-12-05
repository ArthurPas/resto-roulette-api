package com.roulette.resto.data.social.dto.in;

import com.roulette.resto.data.social.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountInfo {
	String login;
	UserRole newRole;
}

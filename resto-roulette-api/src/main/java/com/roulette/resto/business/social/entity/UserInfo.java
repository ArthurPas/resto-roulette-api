package com.roulette.resto.business.social.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
	String lastName;
	String firstName;
	String email;
	UserRole role;

}

package com.roulette.resto.business.social.dto.out;

import com.roulette.resto.business.social.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse extends BasicAuthDto {
	private UserInfo userInfo;
}

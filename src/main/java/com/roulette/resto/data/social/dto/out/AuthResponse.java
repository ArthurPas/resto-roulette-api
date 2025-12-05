package com.roulette.resto.data.social.dto.out;

import com.roulette.resto.data.social.entity.UserInfo;
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

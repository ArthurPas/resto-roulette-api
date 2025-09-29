package com.roulette.resto.business.social.dto.out;

import com.roulette.resto.business.social.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
	private UserInfo basicUserInfo;
	private List<UserInteraction>  userInteractions;

}

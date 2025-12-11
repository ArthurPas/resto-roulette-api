package com.roulette.resto.data.social.dto.out;

import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.social.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
	private String login;
	private UserInfo userInfo;
	private List<UserInteraction> userInteractions;
	private List<MediaResponse> medias;

}

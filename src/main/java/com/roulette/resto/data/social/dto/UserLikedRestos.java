package com.roulette.resto.data.social.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.social.entity.Resto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLikedRestos {
	int accountId;
	Set<Integer> restoIds;
	Set<Resto> restos;
}

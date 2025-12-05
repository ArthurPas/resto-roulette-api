package com.roulette.resto.data.social.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resto {
	public int id;
	public String displayName;
	public String type; //TODO: transform in enum
	public Account owner;

}

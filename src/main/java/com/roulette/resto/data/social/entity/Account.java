package com.roulette.resto.data.social.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account implements UserDetails {
	//account
	@JsonIgnore
	private int accountId;
	private String login;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private String verificationToken;
	@JsonIgnore
	private Timestamp createdAt;

	//user_info
	private UserInfo userInfo;
	private boolean isDeleted;

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}
	@JsonIgnore
	@Override
	public String getUsername() {
		return this.login;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return !isDeleted;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return !isDeleted;
	}
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return !isDeleted;
	}
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return !isDeleted;
	}

}

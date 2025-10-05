package com.roulette.resto.common.configuration;

import com.roulette.resto.business.social.dto.out.AuthResponse;
import com.roulette.resto.business.social.dto.out.BasicAuthDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.services.AccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.security.auth.login.AccountNotFoundException;

import static io.jsonwebtoken.io.Decoders.*;

@Service
public class JwtService {
	@Value("${security.jwt.secret-key}")
	private String secretKey;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;

	private final AccountService accountService;

	public JwtService(AccountService accountService) {
		this.accountService = accountService;
	}

	public int getAccountIdAuthenticated(Authentication authentication) {
		Account a = (Account) authentication.getPrincipal();
		return a.getAccountId();
	}

	public String getAccountLoginAuthenticated(Authentication authentication) {
		Account a = (Account) authentication.getPrincipal();
		return a.getLogin();
	}
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	public long getExpirationTime() {
		return jwtExpiration;
	}

	private String buildToken(
			Map<String, Object> extraClaims,
			UserDetails userDetails,
			long expiration
	) {
		return Jwts
				.builder().claims(extraClaims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey())
				.compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parser() // parser() is still valid in 0.13.0
				.verifyWith((SecretKey) getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}


	private Key getSignInKey() {
		byte[] keyBytes = BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public AuthResponse buildAuthResponse(UserDetails userDetails, int accountId, UserInfo userInfo) throws AccountNotFoundException {
		BasicAuthDto basicAuthDto = buildAuthResponse(userDetails, accountId);
		AuthResponse authResponse = new AuthResponse();
		authResponse.setExpiresIn(basicAuthDto.getExpiresIn());
		authResponse.setToken(basicAuthDto.getToken());
		authResponse.setUserInfo(userInfo);
		return authResponse;
	}
	public BasicAuthDto buildAuthResponse(UserDetails userDetails, int accountId) {
		BasicAuthDto authResponse = new BasicAuthDto();
		Map<String, Object> accountIdJwt = new HashMap<>();
		accountIdJwt.put("userId", accountId);
		String jwtToken = this.generateToken(accountIdJwt,userDetails);
		authResponse.setToken(jwtToken);
		authResponse.setExpiresIn(this.getExpirationTime());
		return authResponse;
	}

}
package com.taqwa.gowaqaf.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final String secret;

	public JwtService(@Value("${jwt.secret}") String secret) {
		super();
		this.secret = secret;
	}

	public String generateToken(AccountUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		claims.put("id", userDetails.getId().toString());
		claims.put("accountType", userDetails.getAccountType().name());

		List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(authority -> authority.startsWith("ROLE_")).toList();

		claims.put("authorities", authorities);

		return Jwts.builder().claims().add(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)).and().signWith(getSigningKey())
				.compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		final Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public UUID extractId(String token) {
		return extractClaim(token, claims -> {
			String id = claims.get("id", String.class);
			return UUID.fromString(id);
		});
	}

	public AccountType extractAccountType(String token) {
		return extractClaim(token, claims -> {
			String accountType = claims.get("accountType", String.class);
			return AccountType.valueOf(accountType);
		});
	}

	public List<String> extractAuthorities(String token) {
		return extractClaim(token, claims -> {
			List<?> authorities = claims.get("authorities", List.class);

			return authorities.stream().map(Object::toString).toList();
		});
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean isTokenValid(String token) {
		return !isTokenExpired(token);
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}

	@SuppressWarnings("unused")
	private void generateSecretKey() {
		SecretKey key = Jwts.SIG.HS256.key().build();

		String encodedKey = Encoders.BASE64.encode(key.getEncoded());

		System.out.println(encodedKey);
	}

}

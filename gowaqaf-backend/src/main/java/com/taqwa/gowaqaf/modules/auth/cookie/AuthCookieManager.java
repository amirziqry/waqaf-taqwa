package com.taqwa.gowaqaf.modules.auth.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthCookieManager {

	@Value("${cookie.secure}")
	private boolean secure;

	@Value("${cookie.same-site}")
	private String sameSite;

	public ResponseCookie createAccessTokenCookie(String token, long maxAge) {

		// Create/Build new cookie builder (JWT > accessToken).
		// Secure false for non-HTTPs.
		// Path valid for entire domain "/".
		ResponseCookie cookie = ResponseCookie.from("accessToken", token).httpOnly(true).secure(secure).path("/")
				.sameSite(sameSite).maxAge(maxAge).build();

		return cookie;
	}

	public ResponseCookie clearAccessTokenCookie() {

		ResponseCookie cookie = ResponseCookie.from("accessToken", "").httpOnly(true).secure(secure).path("/").maxAge(0)
				.sameSite(sameSite).build();

		return cookie;
	}
}

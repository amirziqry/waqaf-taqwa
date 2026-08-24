package com.taqwa.gowaqaf.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taqwa.gowaqaf.security.account.AccountType;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		/*
		 * HttpOnly, readable by server (Back-end). Unreadable for JavaScript
		 * (Front-end).
		 * 
		 */

		String token = null;
		String username = null;

		try {
			token = getTokenFromCookie(request);

			// Extract username from JWT (subject or custom claim containing username).
			if (token != null) // Validate token exists.
				username = jwtService.extractUsername(token);

			// Check if authentication (Saved user) is already set.
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				if (jwtService.isTokenValid(token)) { // Validate token.

					UUID id = jwtService.extractId(token);
					AccountType accountType = jwtService.extractAccountType(token);

					List<? extends GrantedAuthority> authorities = jwtService.extractAuthorities(token).stream()
							.map(SimpleGrantedAuthority::new).toList();

					JwtUserDetails principal = new JwtUserDetails(id, username, accountType, authorities);

					// Create Authentication object, JWT replaces password (null);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal,
							null, authorities);

					// Adds extra metadata: IP address, session details, request info, etc.
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// Store authentication in SecurityContext.
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

		} catch (JwtException | IllegalArgumentException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("""
					    {"error": "Invalid or expired token"}
					""");

			return;

		}

		filterChain.doFilter(request, response);

	}

	private String getTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) { // Loop through cookies.
				if ("accessToken".equals(cookie.getName())) // JWT stored in a cookie named "accessToken".
					return cookie.getValue();
			}
		}

		return null;
	}

}

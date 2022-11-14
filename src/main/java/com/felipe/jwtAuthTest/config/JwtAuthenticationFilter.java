package com.felipe.jwtAuthTest.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.jwtAuthTest.DTOs.LoginRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationProvider authProvider;

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequest.class);

            return authProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        String accessToken = JWT
            .create()
            .withSubject(userDetails.getUsername())
            .withClaim(
                "authorities",
                userDetails
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
            )
            .withClaim("id", userDetails.getId())
            .withExpiresAt(Instant.now().plusMillis(AuthConstants.ACCESS_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(AuthConstants.SECRET));

        String refreshToken = JWT
            .create()
            .withSubject(userDetails.getUsername())
            .withExpiresAt(Instant.now().plusMillis(AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(AuthConstants.SECRET));

        Map<String, String> jsonRes = new HashMap<>();
        jsonRes.put("accessToken", accessToken);
        jsonRes.put("refreshToken", refreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(jsonRes));
    }
}

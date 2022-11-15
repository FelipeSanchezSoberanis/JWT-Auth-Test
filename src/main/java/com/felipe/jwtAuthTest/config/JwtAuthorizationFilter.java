package com.felipe.jwtAuthTest.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private AuthenticationProvider authProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AuthConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(authHeader);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
        String token = authHeader.replace(AuthConstants.TOKEN_PREFIX, "");

        DecodedJWT jwt = JWT
            .require(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()))
            .build()
            .verify(token);

        String username = jwt.getSubject();
        Long userId = jwt.getClaim("id").as(Long.class);
        List<String> authoritiesAsString = jwt.getClaim("authorities").asList(String.class);
        List<SimpleGrantedAuthority> authorities = authoritiesAsString
            .stream()
            .map(auth -> new SimpleGrantedAuthority(auth))
            .collect(Collectors.toList());

        UserDetailsImpl userDetails = new UserDetailsImpl(userId, username, authorities, "");

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}

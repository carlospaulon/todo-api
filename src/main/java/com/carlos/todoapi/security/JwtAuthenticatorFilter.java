package com.carlos.todoapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticatorFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticatorFilter.class);


    public JwtAuthenticatorFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing JWT for URI: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        //verifica se o header vem como Bearer Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7); //pega o token (tira o Bearer)

        username = jwtUtil.extractUsername(token); //pega o user

        //verifica se ainda não está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // pega os dados do user
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //valida o token
            if (jwtUtil.validateToken(token, userDetails)) {
                // autentica com spring security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //regitra user como autenticado
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }

        // continua filtros
        filterChain.doFilter(request, response);

    }
}

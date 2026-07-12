package com.tommy.identity.infrastructure.security;

import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException{

        // 1. Get Authorization string from Header of HTTP Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final UUID userId;

        // 2. If the header is blank or doesn't meet the "Bearer" standard, just skip it

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // 3. Remove the first 7 characters of "Bearer" to get the original Token string
        jwt = authHeader.substring(7);

        try{
            // 4. Check if token is valid and if system has not recorded any login for this request
            boolean isTokenValid = jwtTokenProvider.isTokenValid(jwt);
            boolean isNotAuthentication = SecurityContextHolder.getContext().getAuthentication() == null;
            if(isTokenValid && isNotAuthentication){
                userId = jwtTokenProvider.extractUserId(jwt);

                Account account = accountRepository.findById(userId).orElse(null);

                // If user not existed, block!
                if (account == null ||
                        account.getStatus() == AccountStatus.DELETED ||
                        account.getStatus() == AccountStatus.LOCKED ||
                        account.getStatus() == AccountStatus.SUSPENDED)
                {
                    log.warn("Block access: Account {} is in the state {}", userId, account != null ? account.getStatus() : "NOT EXISTED!");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"code\": 401, \"message\": \"Your account has been disabled or blocked. Please contact ADMIN !!!\"}"
                    );
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, // Định danh User ID
                        null,
                        Collections.emptyList()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Save information to system context to acknowledge that this request has been successfully authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.error("Error in decoding JWT encrypted string: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}

package com.example.Bank.Config.Security;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JWTUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private Logger logger = LogManager.getLogger(JWTUsernamePasswordAuthenticationFilter.class);

    //VALIDATE CREDENTIALS
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

//                try {
//
//            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue
//                    (request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
//
//            Authentication auth = new UsernamePasswordAuthenticationToken(
//                    authenticationRequest.getUsername(),
//                    authenticationRequest.getPassword()
//            );
//
//            return authenticationManager.authenticate(auth);
//        }
//        catch (IOException e){
//            throw new RuntimeException(e);
//        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("Username is {}", username);
        logger.info("Password is {}", password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }


    //CREATE TOKEN
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String key = "secretkey12345678910secretkey12345678910secretkey12345678910secretkey12345678910secretkey12345678910";
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(Keys.hmacShaKeyFor(key.getBytes()))
                .compact();

//        LogoutService.LOGG_IN = true;
        response.setHeader("Authentication", "Bearer "+token);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", token);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        chain.doFilter(request, response);
    }
}

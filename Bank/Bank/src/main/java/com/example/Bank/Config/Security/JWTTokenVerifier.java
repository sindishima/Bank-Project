package com.example.Bank.Config.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class JWTTokenVerifier extends OncePerRequestFilter {
    private static final String ONE_HOUR = "3600";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletResponse res = (HttpServletResponse) response;

//        String path = request.getServletPath();

        String authorizationHeader = request.getHeader("Authorization");

        if(request.getServletPath().startsWith("/login") || request.getServletPath().startsWith("/user/admin")){
            res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            res.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
            res.setHeader("Access-Control-Max-Age", ONE_HOUR);
            res.setHeader("Access-Control-Request-Headers", "authorization,content-type");
            res.setHeader("Access-Control-Allow-Headers", "X-Requested- With,Origin,Content-Type, Accept, x-device-user-agent, Content-Type");

            filterChain.doFilter(request, response);
        } else{

            String token = authorizationHeader.replace("Bearer ", "");

            if(!authorizationHeader.startsWith("Bearer ") || token==null){
                filterChain.doFilter(request, response);
            }
            else {
                try {
                    String key = "secretkey12345678910secretkey12345678910secretkey12345678910secretkey12345678910secretkey12345678910";

                    Jws<Claims> claimJws = Jwts.parser()
                            .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
                            .parseClaimsJws(token);

                    String username = claimJws.getBody().getSubject();

                    var authorities =  (List<Map<String, String>>) claimJws.getBody().get("authorities");
                    Set<SimpleGrantedAuthority> authoritySet = authorities.stream()
                            .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                            .collect(Collectors.toSet());

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authoritySet
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("Security "+SecurityContextHolder.getContext());
                    filterChain.doFilter(request, response);
                }
                catch (JwtException e){
                    response.setStatus(403);
                }
            }
        }
    }
}

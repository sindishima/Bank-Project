package com.example.Bank.Config.Security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @CrossOrigin(origins = {"http://localhost:3000"})
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new JWTUsernamePasswordAuthenticationFilter(authenticationManagerBean());
        usernamePasswordAuthenticationFilter.setFilterProcessesUrl("/login");   //sets the URL that determines if authentication is required

        http.cors()
//                .configurationSource(new CorsConfigurationSource() {
//            @Override
//            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                return new CorsConfiguration().applyPermitDefaultValues();
//            }
//        })
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login/**", "/user/admin/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(usernamePasswordAuthenticationFilter)
                .addFilterBefore(new JWTTokenVerifier(), UsernamePasswordAuthenticationFilter.class);
//                .addFilterAfter(new JWTTokenVerifier(), JWTUsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",   //this URLs are not secured
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }
}
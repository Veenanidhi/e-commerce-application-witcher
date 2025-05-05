package com.witcher.e_commerce.application.witcher.security;

import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import com.witcher.e_commerce.application.witcher.service.CustomSuccessHandler;
import com.witcher.e_commerce.application.witcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomSuccessHandler customSuccessHandler;

    @Autowired
    public SecurityConfig(CustomSuccessHandler customSuccessHandler) {
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public VerificationToken verificationToken() {
        return new VerificationToken();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService){

        DaoAuthenticationProvider auth= new DaoAuthenticationProvider();
        auth.setUserDetailsService((UserDetailsService) userService);
        auth.setPasswordEncoder(bCryptPasswordEncoder());

        return auth;
    }

    @Bean
    public WebSecurityCustomizer securityCustomizer(){
        return (web) ->
                web.ignoring().requestMatchers("/css/**", "/img/**", "/js/**", "/static/**", "/uploads/**",
                        "/templates/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http.authorizeHttpRequests(auth->
                        auth.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "OIDC_USER")
                                .requestMatchers("/addToCart/**").hasAnyAuthority("ROLE_USER", "OIDC_USER")
                                .requestMatchers("/user/wishlist/add/**").hasAnyAuthority("ROLE_USER", "OIDC_USER")
                                .requestMatchers("/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                                form
                                        .loginPage("/login")
                                        .loginProcessingUrl("/authenticateTheUser")
                                        .usernameParameter("email")
                                        .successHandler(customSuccessHandler).permitAll()


                )

                .logout(logout->
                        logout.permitAll()

                )
                .sessionManagement(session-> session
                        .maximumSessions(2)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired")


                )
                .exceptionHandling(configurer->
                        configurer.accessDeniedPage("/access-denied")
                );

       //oauth2 configuration
        http.oauth2Login(oauth->{
             oauth.loginPage("/login");
             oauth.successHandler(customSuccessHandler);
               });



        return http.build();

    }









}

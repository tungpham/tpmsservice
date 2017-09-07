package com.ethan.morephone;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by truongnguyen on 9/5/17.
 */
@EnableWebSecurity
@Configuration
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Value("${auth0.issuer}")
    private String issuer;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.secret}")
    private String secret;


    /**
     * Not required for the Spring Security implementation, but offers Auth0 API access
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtWebSecurityConfigurer
                .forRS256(audience, issuer)
                .configure(http)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/phone-number").hasAnyAuthority("write:phone-number")
                .antMatchers(HttpMethod.GET, "/api/v1/phone-number").hasAnyAuthority("read:phone-numbers")
                .antMatchers(HttpMethod.DELETE, "/api/v1/phone-number/**").hasAnyAuthority("delete:phone-number")
                .antMatchers(HttpMethod.POST, "/api/v1/phone-number/pool").hasAnyAuthority("write:pool-phone-number")
                .antMatchers(HttpMethod.GET, "/api/v1/phone-number/pool").hasAnyAuthority("read:pool-phone-numbers")
                .antMatchers(HttpMethod.GET, "/api/v1/phone-number/**").hasAnyAuthority("read:phone-number")
                .antMatchers(HttpMethod.PUT, "/api/v1/phone-number/**").hasAnyAuthority("write:forward-phone-number")
                .antMatchers(HttpMethod.PUT, "/api/v1/phone-number/**").hasAnyAuthority("write:expire-phone-number")
                .antMatchers(HttpMethod.POST, "/api/v1/user").hasAnyAuthority("write:user")
                .antMatchers(HttpMethod.PUT, "/api/v1/user/**").hasAnyAuthority("write:user-token")
                .antMatchers(HttpMethod.POST, "/api/v1/call/token").hasAnyAuthority("write:call-token")
                .antMatchers(HttpMethod.GET, "/api/v1/call/records").hasAnyAuthority("read:records")
                .antMatchers(HttpMethod.GET, "/api/v1/call/logs").hasAnyAuthority("read:call-logs")
                .antMatchers(HttpMethod.POST, "/api/v1/message/send-message").hasAnyAuthority("write:send-message")
                .antMatchers(HttpMethod.GET, "/api/v1/message/retrieve").hasAnyAuthority("read:messages")
                .antMatchers(HttpMethod.GET, "/api/v1/usage/**").hasAnyAuthority("read:usage")
                .antMatchers(HttpMethod.POST, "/api/v1/purchase").hasAnyAuthority("write:purchase")
                .anyRequest().authenticated();
    }

}
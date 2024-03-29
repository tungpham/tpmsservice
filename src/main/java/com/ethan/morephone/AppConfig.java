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
                .antMatchers(HttpMethod.POST, "/api/v1/call/events").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/call/dial").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/call/leave-message").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/call/handle-recording").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/call/record-event").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/message/receive-message").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/message/callback").permitAll()
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
                .antMatchers(HttpMethod.POST, "/api/v1/contact").hasAnyAuthority("write:contact")
                .antMatchers(HttpMethod.GET, "/api/v1/contact").hasAnyAuthority("read:contact")
                .antMatchers(HttpMethod.PUT, "/api/v1/contact").hasAnyAuthority("write:contact")
                .antMatchers(HttpMethod.DELETE, "/api/v1/contact").hasAnyAuthority("delete:contact")
                .antMatchers(HttpMethod.POST, "/api/v1/group").hasAnyAuthority("write:group")
                .antMatchers(HttpMethod.GET, "/api/v1/group").hasAnyAuthority("read:group")
                .antMatchers(HttpMethod.PUT, "/api/v1/group").hasAnyAuthority("write:group")
                .antMatchers(HttpMethod.DELETE, "/api/v1/group").hasAnyAuthority("delete:group")
                .antMatchers(HttpMethod.GET, "/api/v1/phonenumber/country").hasAnyAuthority("read:country")
                .antMatchers(HttpMethod.GET, "/api/v1/phonenumber/available/local").hasAnyAuthority("read:phone-available")
                .antMatchers(HttpMethod.GET, "/api/v1/phonenumber/available/mobile").hasAnyAuthority("read:phone-available")
                .antMatchers(HttpMethod.GET, "/api/v1/phonenumber/available/tollfree").hasAnyAuthority("read:phone-available")
                .anyRequest().authenticated();
    }

}
package com.img.resource.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/user/info")
                .hasAuthority("SCOPE_read")
                .antMatchers(HttpMethod.POST, "/api/filter")
                .hasAuthority("SCOPE_write")
                .antMatchers(HttpMethod.POST, "/api/test")
                .hasAuthority("SCOPE_write")
                .antMatchers(HttpMethod.GET, "/api/filter/ret")
                .hasAuthority("SCOPE_read")
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                ;
//        http.headers().httpStrictTransportSecurity().disable();
        http.headers().httpStrictTransportSecurity()
                .maxAgeInSeconds(0)
                .includeSubDomains(true);
    }//@formatter:on

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/test");
    }

}

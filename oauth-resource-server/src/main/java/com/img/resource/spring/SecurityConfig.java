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
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                ;
    }//@formatter:on

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/test");
    }
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8089", "http://localhost:8083"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

}

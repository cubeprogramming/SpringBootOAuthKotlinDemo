package com.cubeprogramming.oauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
class SecurityConfiguration {
    @Bean
    @Throws(Exception::class)
    @SuppressWarnings
    fun filterChain(http: HttpSecurity): SecurityFilterChain? = run {
        http.also { httpSec ->
            httpSec.authorizeRequests(
                Customizer { it.antMatchers("/", "/error", "/webjars/**").permitAll() //NOSONAR
                    .anyRequest().authenticated()
                }
            )
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .logout {
                it.logoutSuccessUrl("/").permitAll()
                it.deleteCookies("JSESSIONID")
            }
            .csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .oauth2Login()
        }.build()
    }
}

package ru.gb.gbthymeleaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(
                (requests) ->  {
                    requests.antMatchers("/", "/product/all").permitAll();
                    requests.antMatchers(HttpMethod.POST, "/product").hasRole("ADMIN");
                    requests.mvcMatchers(HttpMethod.GET, "/product/{productId}").permitAll();
                }
        );

        http.authorizeRequests((requests) -> {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) requests.anyRequest()).authenticated();
        });
        http.formLogin();
        http.httpBasic();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user")
//                .password("$2a$10$k/YomPIfS0R/6y8fcOGfZ.VrV9QOmKmOHADIju.ig2Gzje1FBA57.")
//                .roles("USER")
//                .and()
//                .withUser("admin")
//                .password("$2a$10$vJXhq/i01E8R31smFN1YSe5k/eB2mrgCq3DdrWM95Q.SOnm55/4..")
//                .roles("ADMIN");
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("user")
//                .roles("USER")
//                .build();
//
//        UserDetails adminDetails = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(adminDetails, userDetails);
//    }
}

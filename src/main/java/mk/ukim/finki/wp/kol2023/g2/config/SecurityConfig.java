package mk.ukim.finki.wp.kol2023.g2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class is used to configure user login on path '/login' and logout on path '/logout'.
 * The only public page in the application should be '/'.
 * All other pages should be visible only for a user with role 'ROLE_ADMIN'.
 * Furthermore, in the "list.html" template, the 'Edit', 'Delete', 'Add' buttons should only be
 * visible for a user with role 'ROLE_ADMIN'.
 * The 'Vote for Movie' button should only be visible for a user with role 'ROLE_USER'.
 * <p>
 * For login inMemory users should be used. Their credentials are given below:
 * [{
 * username: "user",
 * password: "user",
 * role: "ROLE_USER"
 * },
 * <p>
 * {
 * username: "admin",
 * password: "admin",
 * role: "ROLE_ADMIN"
 * }]
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2**"); // do not remove this line
        // TODO: If you are implementing the security requirements, remove this following line

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/movies").permitAll()
                .antMatchers("/movies/*/vote").hasRole("USER")
                .anyRequest().hasRole("ADMIN")
                .and()
                .formLogin()
                .failureUrl("/login?error=BadCredentials")
                .defaultSuccessUrl("/movies", true)
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/movies");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser("user").
                password(passwordEncoder.encode("user")).
                authorities("ROLE_USER").
                and()
                .withUser("admin")
                .password(passwordEncoder.encode("admin"))
                .authorities("ROLE_ADMIN");
    }
}

package INT365.webappchatbot.Configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.cors().disable();
        httpSecurity.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/api/authenticate").permitAll();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/api/reports", "/api/reports/").permitAll();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/api/reports/createReport").permitAll();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/api/news", "/api/news/").permitAll();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/api/viewFileByPath").permitAll();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/api/viewImage/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/api/chat", "/api/chat/**","/api/webhook/test").permitAll();
        // dont authenticate this particular request
        //.authorizeRequests().antMatchers("/authenticate").permitAll().
        // all other requests need to be authenticated
        httpSecurity.authorizeRequests().anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //        httpSecurity.httpBasic().disable();
        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors();
    }
}

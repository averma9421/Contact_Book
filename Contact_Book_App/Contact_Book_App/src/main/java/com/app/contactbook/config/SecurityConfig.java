package com.app.contactbook.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.app.contactbook.service.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource dataSource;
	@Autowired
    UserDetailsServiceImpl userDetailsService;
	

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
	//

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
     
     
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { 
 
        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());     
 
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		  http.csrf().disable();
		  
	 
		
		http.authorizeRequests().anyRequest().authenticated()
						.and()
							.formLogin()
								.loginPage("/login")
								.permitAll()
						.and()
							.rememberMe()
								.rememberMeCookieName("javasampleapproach-remember-me")
								.tokenValiditySeconds(24 * 60 * 60) // expired time = 1 day
								.tokenRepository(persistentTokenRepository())
						.and()
							.logout()
							 .logoutUrl("/logout") 
							.logoutSuccessUrl("/login.html")
							.invalidateHttpSession(true)
							.deleteCookies("JSESSIONID")
							.permitAll();
	}

	
	/*@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
	}	*/
	
	
}
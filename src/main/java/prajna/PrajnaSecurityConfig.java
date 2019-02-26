package prajna;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import prajna.services.AccountService;

@ComponentScan(basePackages= "prajna")
@Configuration
@EnableWebSecurity
public class PrajnaSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LogManager.getLogger(PrajnaSecurityConfig.class.getName() );
	
	//@Autowired
	//private DataSource dataSource;
	
	private final UserDetailsService userDetailsService;
	
    @Autowired
    public PrajnaSecurityConfig(UserDetailsService userDetailsService) {
        super();
        this.userDetailsService = userDetailsService;
    }
	   
	@Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }
 
	/**
	 *  TIPS: 
	 *     1: 'antMatchers("/doc/**")' must be after '/doc/editor'.hasRole("ADMIN")
	 *     2:  Custom login failureUrl("xx").successForwardUrl("xx")
	 *     3:  '/draft/' insteadof '//draft'
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/css/**", "/js/**").permitAll()
			.antMatchers("/", "/signin", "/signup").permitAll()
			.antMatchers("/admin/**", "/doc/editor/**", "/draft/", "/uploader/upload/", "/edoc/", "/filebrowser/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/doc/**").permitAll()
			.antMatchers("/doc/**").hasRole("USER")
			.antMatchers(HttpMethod.POST,"/account").hasRole("USER")
			//.antMatchers("/doc/comment/*").hasRole("USER")
			.and().formLogin().loginPage("/signin").successForwardUrl("/signin/success")
							  .usernameParameter("account")
							  .passwordParameter("passwd")
							  .permitAll()
			.and().logout().logoutUrl("/signout")
			.and().httpBasic();

		http.csrf().disable();
		http.headers().frameOptions().sameOrigin();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(AccountService.BCryptStrength);
	}
    
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService);
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}
}

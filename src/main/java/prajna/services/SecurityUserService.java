package prajna.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import prajna.models.Account;
import prajna.models.SecurityPrincipal;
import prajna.repos.AccountRepo;

@Service
@Scope("application")
public class SecurityUserService implements UserDetailsService  {
	private static final Logger logger = LogManager.getLogger(SecurityUserService.class.getName());
	
	@Autowired
	AccountRepo  accountRepo;  
	
	@Override
	public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
		if (!account.isEmpty()) {
			//logger.info("loadUserByUsername, account:" + account);
			Account usr = accountRepo.findByAccountIgnoreCase(account);
			if (usr != null) {
				SecurityPrincipal principal = new SecurityPrincipal(usr, getGrantedAuthorities(usr.getRoles()));
				return principal;	
			}
		}
		throw new UsernameNotFoundException("User not found by name: " + account);
	}
	
	public void signIn(Account account) {
		SecurityPrincipal principal = new SecurityPrincipal(account, getGrantedAuthorities(account.getRoles()));

	    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, getGrantedAuthorities(account.getRoles()));
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	private List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		List<GrantedAuthority>  authorities = new ArrayList<GrantedAuthority>(roles.size());
		for (String role: roles) {
			authorities.add(new SimpleGrantedAuthority(role));
			//logger.info("getAuthorities, role:" + role);
		}
		return authorities;
	}
}

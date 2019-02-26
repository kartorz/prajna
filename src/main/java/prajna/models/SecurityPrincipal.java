package prajna.models;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class SecurityPrincipal extends User{

	private static final long serialVersionUID = 3499986616988767477L;

	public SecurityPrincipal(Account account, List<GrantedAuthority> authorities) {
		super(account.getAccount() , account.getPassword(), authorities);
	}
}

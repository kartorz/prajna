package prajna.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.Account;

public interface AccountRepo extends CrudRepository<Account, String> {
	Account findByAccount(String account);
	Account findByAccountIgnoreCase(String account);
	List<Account> findByName(String name);
	List<Account> findByAccountOrName(String account, String name);

	@Transactional
	@Modifying
	@Query("UPDATE Accounts ac SET ac.password = ?2 WHERE ac.account = ?1")
	int updatePasswordByAccount(String account, String passwd);
}

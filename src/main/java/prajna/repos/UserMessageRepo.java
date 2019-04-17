package prajna.repos;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.UserMessage;

public interface UserMessageRepo  extends PagingAndSortingRepository<UserMessage, Integer> {
	@Transactional
	@Modifying
	@Query("UPDATE UserMessages m SET m.text = ?2 WHERE m.id = ?1")
	int updateTextById(int id, String text);
	
	@Transactional int deleteByIdAndAccount(int id, String account);

	@Transactional
	int deleteById(int id);
}

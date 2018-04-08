package prajna.repos;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.Question;

public interface QuestionRepo  extends PagingAndSortingRepository<Question, Integer> {
	Page<Question> findByQidIn(Set<Integer> ids, Pageable pageable);
	
	long countByQidIn(Set<Integer> ids);
	
	@Transactional
	@Modifying
	@Query("UPDATE questions q SET q.invitees = ?3 WHERE q.qid = ?1 AND q.account = ?2")
	int updateInvitees(int qid, String account, String invitees);
	
	@Transactional
	@Modifying
	@Query("UPDATE questions q SET q.invitees = ?2 WHERE q.qid = ?1")
	int updateInvitees(int qid, String invitees);
}

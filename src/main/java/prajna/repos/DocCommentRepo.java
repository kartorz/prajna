package prajna.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.DocComment;

public interface DocCommentRepo  extends PagingAndSortingRepository<DocComment, Integer> {
	Page<DocComment> findByDid(int did, Pageable pageable);
	Long countByDid(int did);
	
	@Transactional
	@Modifying
	@Query("UPDATE doccomment d SET d.text = ?3 WHERE d.id = ?1 AND d.account = ?2")
	int updateTextById(int resid, String account, String text);
	
	@Transactional int deleteByIdAndAccount(int id, String account);
}

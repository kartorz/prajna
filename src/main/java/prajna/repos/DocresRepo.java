package prajna.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.Docres;
import prajna.repos.projection.*;

public interface DocresRepo  extends PagingAndSortingRepository<Docres, Integer> {

	List<Id> findByDid(int did);
	
	Docres findByDraftId(int draftId);
	
	@Transactional
	@Modifying
	@Query("update Docres d set d.did = ?1 where d.did = ?2")
	int updateDid(int to , int from);
	
	@Transactional
	@Modifying
	@Query("update Docres d set d.did = ?1 where d.id = ?2")
	int updateDidById(int did , int resid);
	
	@Transactional
	@Modifying
	@Query("update Docres d set d.draftId = ?1 where d.draftId = ?2")
	int updateDraftId(int to, int from);
	
	@Transactional int deleteByDraftId(int draftId);
	@Transactional int deleteByDidAndDraftId(int did, int draftId);
	@Transactional int deleteByDid(int did);
	
	@Transactional List<Docres> removeByDid(int did);
}

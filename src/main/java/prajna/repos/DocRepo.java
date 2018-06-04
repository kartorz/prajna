package prajna.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.models.Doc;
import prajna.repos.projection.DocOpaque;

public interface DocRepo  extends PagingAndSortingRepository<Doc, Integer> {
	Doc findById(int id);
	DocOpaque findOpaqueById(int id);
	Page<Doc> findByCid(int cid, Pageable pageable);
	Page<Doc> findByOnTop(boolean onTop, Pageable pageable);
	Page<Doc> findAll(Pageable pageable);
	Page<Doc> findByCidGreaterThan(int cid, Pageable pageable);
	
	//@Query("SELECT d FROM Doc AS d ORDER BY d.onTop DESC, d.mdate DESC")
	//Page<Doc> findAll(Pageable pageable);
	Page<Doc> findAllByOrderByOnTopDescMdateDesc(Pageable pageable);
	Page<Doc> findByCidOrderByMdateDesc(int cid, Pageable pageable);


	@Transactional
	@Modifying
	@Query("update Docs d set d.views = ?1 where d.id = ?2")
	int updateViews(int views , int id);
	
	long count();
	long countByCid(int cid);
	long countByOnTop(boolean onTop);
}

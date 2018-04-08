package prajna.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import prajna.models.QuestionAnswer;

public interface QuestionAnswerRepo  extends PagingAndSortingRepository<QuestionAnswer, Integer> {
	Page<QuestionAnswer> findByQid(int qid, Pageable pageable);
	Long countByQid(int qid);
}

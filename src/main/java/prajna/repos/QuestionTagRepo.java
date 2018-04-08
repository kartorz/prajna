package prajna.repos;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import prajna.repos.projection.*;
import prajna.models.QuestionTag;
import prajna.models.QuestionTagId;

public interface QuestionTagRepo  extends CrudRepository<QuestionTag, QuestionTagId> {
	Set<IdQid> findByIdTagIn(Set<String> tags);
	Set<IdTag> findByIdQid(int qid);
	
	@Transactional int deleteByIdQid(int qid);
}

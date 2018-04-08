package prajna.repos;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import prajna.models.TagItem;

public interface TagRepo  extends CrudRepository<TagItem, Integer> {
	Set<TagItem> findAll();
}

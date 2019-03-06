package prajna.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import prajna.models.DocTreeItem;

public interface DocTreeRepo extends CrudRepository<DocTreeItem, Integer> {
	List<DocTreeItem> findByPid(int pid);
}

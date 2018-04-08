package prajna.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import prajna.models.MenuItem;

public interface MenutreeRepo extends CrudRepository<MenuItem, Integer> {
	List<MenuItem> findByLevel(int level);
}

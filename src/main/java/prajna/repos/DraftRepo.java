package prajna.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import prajna.models.Account;
import prajna.models.Draft;

public interface DraftRepo  extends CrudRepository<Draft, Integer> {
	Draft findByAccountAndDid(String account, int did);
	List<Draft> findAllByCid(int cid);
}

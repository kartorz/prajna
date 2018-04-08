package prajna.repos;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import prajna.models.AccountTagId;
import prajna.models.MyTag;
import prajna.repos.projection.IdTag;

//The tags user don't want.
public interface MyTagRepo  extends CrudRepository<MyTag, AccountTagId> {
	Set<IdTag> findByIdAccount(String account);
}

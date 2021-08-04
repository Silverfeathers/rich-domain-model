package cc.codeasy.ddd.sample.richdomainmodel.repository;

import cc.codeasy.ddd.sample.richdomainmodel.model.Account;
import cc.codeasy.ddd.sample.richdomainmodel.model.Owner;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findById(long id);


    Account findByOwner(Owner tom);
}

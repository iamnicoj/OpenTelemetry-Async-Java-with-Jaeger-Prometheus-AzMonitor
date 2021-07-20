package entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMessageRepository extends CrudRepository<OrderMessage, String> {
}

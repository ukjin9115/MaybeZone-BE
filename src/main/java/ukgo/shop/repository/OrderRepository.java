package ukgo.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ukgo.shop.entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}

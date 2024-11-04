package ukgo.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ukgo.shop.entity.ShopItem;

import java.util.List;

public interface ItemRepository extends JpaRepository<ShopItem, Integer> {
    @Query(value = "SELECT * FROM shop_items WHERE MATCH(title) AGAINST(?1)", nativeQuery = true)
    List<ShopItem> searchItems(String text);
}

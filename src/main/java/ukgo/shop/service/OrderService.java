package ukgo.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ukgo.shop.entity.Order;
import ukgo.shop.entity.ShopItem;
import ukgo.shop.entity.User;
import ukgo.shop.repository.OrderRepository;
import ukgo.shop.repository.ItemRepository;
import ukgo.shop.repository.UserRepository;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order createOrder(Long userId, Integer itemId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ShopItem item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        int totalPrice = item.getPrice() * quantity;

        Order order = new Order();
        order.setUser(user);
        order.setItem(item);
        order.setQuantity(quantity);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}

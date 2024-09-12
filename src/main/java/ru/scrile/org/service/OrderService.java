package ru.scrile.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scrile.org.model.Order;
import ru.scrile.org.model.User;
import ru.scrile.org.model.exception.UserNotFoundException;
import ru.scrile.org.payload.request.NewOrderRequest;
import ru.scrile.org.repository.OrderRepository;
import ru.scrile.org.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;


    @Autowired
    public OrderService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void makeNewOrder(String userName, NewOrderRequest newOrderRequest) {
        User user = userRepository.findByName(userName).
                orElseThrow(() -> new UserNotFoundException(userName));
        Order order = Order.builder()
                .productName(newOrderRequest.getProductName())
                .price(newOrderRequest.getPrice())
                .user(user)
                .paymentTime(newOrderRequest.getLocalDateTime() != null ? newOrderRequest.getLocalDateTime() : LocalDateTime.now())
                .build();

        orderRepository.save(order);
    }

    public Page<Order> findOrdersByUser(String username, Pageable pageable) {
        return orderRepository.findByUserName(username, pageable);
    }

    public List<Order> findAllOrdersByUser(String username) {
        return orderRepository.findByUserName(username);
    }
}

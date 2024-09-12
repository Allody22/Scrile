package ru.scrile.org.controller;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.scrile.org.model.Order;
import ru.scrile.org.payload.request.NewOrderRequest;
import ru.scrile.org.service.OrderService;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {


    private final OrderService orderService;

    @PostMapping("/new")
    public ResponseEntity<String> newOrder(@Valid @RequestBody NewOrderRequest newOrderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        orderService.makeNewOrder(username, newOrderRequest);

        return ResponseEntity.ok().body("Order made");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUserOrders(@RequestParam(required = false) Boolean all, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (Boolean.TRUE.equals(all)) {
            List<Order> orders = orderService.findAllOrdersByUser(username);
            return ResponseEntity.ok(orders);
        } else {
            Page<Order> orders = orderService.findOrdersByUser(username, pageable);
            return ResponseEntity.ok(orders);
        }
    }
}

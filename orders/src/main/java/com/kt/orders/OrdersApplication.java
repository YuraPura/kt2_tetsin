package com.kt.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.springframework.http.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/orders")
public class OrdersApplication {
    private final Map<Long, Order> orders = new HashMap<>();
    private long idCounter = 1;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String USER_SERVICE_URL = "http://localhost:8081/api/users/";

    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, "--server.port=8082");
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        order.setId(idCounter++);
        orders.put(order.getId(), order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Order order = orders.get(id);
        if (order == null) return ResponseEntity.notFound().build();

        try {
            UserDto user = restTemplate.getForObject(USER_SERVICE_URL + order.getUserId(), UserDto.class);
            return ResponseEntity.ok(Map.of("order", order, "user", user));
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Сервис пользователей недоступен", "orderId", id));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Пользователь не найден"));
        }
    }
}

class Order {
    private Long id; private Long userId; private String product;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
}
class UserDto {
    public Long id; public String name; public String email;
}

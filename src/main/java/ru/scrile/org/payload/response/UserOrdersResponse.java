package ru.scrile.org.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.scrile.org.model.Order;

import java.util.List;

@Data
@AllArgsConstructor
public class UserOrdersResponse {

    private List<Order> orders;
}

package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.model.Order;
import com.stocktrading.stocktradingapp.service.databaseOperations.OrdersTableOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrdersController {

    private final OrdersTableOperationService ordersTableOperationService;

    @Autowired
    public OrdersController(OrdersTableOperationService ordersTableOperationService) {
        this.ordersTableOperationService = ordersTableOperationService;
    }

    // GET /orders/user/{userId}
    @GetMapping("/user/{userId}")
    public List<Order> getAllOrdersByUserId(@PathVariable int userId) {
        return ordersTableOperationService.getAllOrdersByUserId(userId);
    }
    // Example output:
    // [{"orderId":29,"userId":2,"stockSymbol":"3034.KL","quantity":5,"price":3.5,"orderType":"BUY"}]

    // DELETE /orders/delete-order
    @DeleteMapping("/{orderId}")
    public void removeOrderByOrderId(@RequestBody Map<String, Integer> orderId) {
        ordersTableOperationService.removeOrderByOrderId(orderId.get("order_id"));
    }
    // Example input (json):
    // {
    //     "order_id": 29
    // }
}

package jpabook.jpashop.repository.order.simplequery;

import java.time.LocalDateTime;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

@Data
public class OrderSimpleQueryDto {
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;
	
	//Dto가 엔티티에 의존하는거는 ㄱㅊ
	public OrderSimpleQueryDto(Order order) {
		orderId = order.getId();
		name = order.getMember().getName();
		orderDate = order.getOrderDate();
		orderStatus = order.getStatus();
		address = order.getDelivery().getAddress();
	}
	
	public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.address = address;
	}
}

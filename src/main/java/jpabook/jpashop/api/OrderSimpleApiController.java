package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.api.OrderSimpleApiController.SimpleOrderDto;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * xToOne(ManyToOne, OneToOne) 에서의 최적화
 * 
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
	
	private final OrderRepository orderRepository;
	
	//1.  Order -> Member -> Order -> Member 무한루프에 빠진다.
	//양방향이 걸리는 곳은 @JsonIgnore로 방지해야함.
	//2. 고치고 나서도 또 다른 에러가 발생하게 됨. org.hibernate.proxy.pojo.bytebuddy...
	//하이버네이트는 지연로딩일 때, member = new ByteBuddyInterceptor() 가 대신 들어가있음.
	//Jackson 라이브러리는 ByteBuddyInterceptor라는 애를 어떻게 처리해야 할지 알지 못함. 그래서 오류 발생.
	//hibernate5Module을 사용해서 초기화 되지 않은 프록시는 노출하지 않도록 설정하자.
	//Json 생성할 때, Lazy Loading해서 가져오도록 설정할 수도 있음.
	//성능상 매우 안 좋음. 연관된 모든 애들을 Lazy Loading으로 다 가져옴.
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1(){
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
//		for(Order order : all) {
//			order.getMember().getName(); //Lazy 강제 초기화
//			order.getDelivery().getAddress(); //Lazy 강제 초기화
//		}
		return all;
	}
	
	// 기본에 충실했지만, Lazy loading으로 인해 너무 많은 쿼리가 발생한다는 문제점이 있음.
	// N+1 문제가 발생. 1번의 쿼리 결과가 N개면 N번의 Lazy Loading이 발생. 여기서는 2N(회원, 배송) +1번 레이지 로딩이 발생.
	// 착각하지 말것! EAGER로 바꿔도 똑같이 문제가 발생함. 
	@GetMapping("/api/v2/simple-orders")
	public OrderResult<List<SimpleOrderDto>> ordersV2(){
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<SimpleOrderDto> orderDto = orders.stream().map(o -> new SimpleOrderDto(o)) 	//SimpleOrderDto::new
						.collect(Collectors.toList());
		
		return new OrderResult<List<SimpleOrderDto>>(orderDto);
	}
	
	//fetch join을 사용한다. Order를 가져올 때, Member와 Delivery를 join을 이용해서 한 번에 가져옴. 따라서 쿼리는 1개만 발생!
	@GetMapping("/api/v3/simple-orders")
	public OrderResult<List<SimpleOrderDto>> ordersV3(){
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		List<SimpleOrderDto> orderDto = orders.stream().map(SimpleOrderDto::new)
		.collect(Collectors.toList());
		
		return new OrderResult<List<SimpleOrderDto>>(orderDto);
	}
	
	@Data
	@AllArgsConstructor
	static class OrderResult<T>{
		private T data;
	}
	
	@Data
	static class SimpleOrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		
		//Dto가 엔티티에 의존하는거는 ㄱㅊ
		public SimpleOrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
		}
	}
}

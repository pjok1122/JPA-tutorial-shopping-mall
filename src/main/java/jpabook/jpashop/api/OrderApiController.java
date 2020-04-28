package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
	
	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;
	/**
	 * 계속 반복되는 예제.
	 * Jackson 라이브러리는 getXXX() 메서드를 호출해서 get을 떼고 소문자로 만든 후, 필드값으로 사용한다. 
	 * 따라서 getTotalPrice()를 호출해서 totalPrice가 필드로 들어있음.
	 */
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1(){
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for(Order order : all) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.forEach(o->o.getItem().getName());
		}
		return all;
	}
	/**
	 * 모든 오더 조회 쿼리 1번 (2개. 오더1, 오더2)
	 * 오더1의 Member 조회 1번
	 * 오더1의 Delivery 조회 1번
	 * 오더1의 OrderItems 조회 1번 (2개. 상품1, 상품2)
	 * 오더1에서 상품1 조회 1번
	 * 오더1에서 상품2 조회 1번
	 * 오더2에 대해서도 동일하므로 총 11번의 쿼리 발생 예측.
	 * 
	 * OrderDto로 껍데기만 Dto로 감싸는 것이 아니라 내부에 있는 OrderItem 또한 OrderItemDto로 만들어야 한다.
	 */
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2(){
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderDto> result = orders.stream().map(o->new OrderDto(o))
								.collect(Collectors.toList());
		
		return result;
	}
	/**
	 * 장점 : 페치 조인을 이용해서 쿼리 1번 발생.
	 * 단점 : 1:N 관계를 페치 조인하면 페이징이 불가능하다. (메모리에서 페이징 처리하기 때문에 절대 해서는 안됨)
	 * 
	 * Order가 OrderItem과 1:N 관계이기 때문에, SQL 질의 결과에서 Order가 중복되어 나타날 수 있다.
	 * Order가 중복되는 경우, 객체 자체가 동일하게 된다. (JPA는 식별자가 같으면 같은 객체)
	 * 
	 * 이 중복을 제거하기 위해서는 distinct를 추가해야 한다. JPA가 어플리케이션 레벨에서 한 번 더 제거해줌.
	 */
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3(){
		List<Order> orders = orderRepository.findAllWithItem();
		List<OrderDto> result = orders.stream().map(o->new OrderDto(o))
							.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	* V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
	* - ToOne 관계만 우선 모두 페치 조인으로 최적화
	* - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
	*/
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> ordersV3_paging(
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "100") int limit)
	{
		
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		List<OrderDto> result = orders.stream().map(o->new OrderDto(o))
							.collect(Collectors.toList());
		
		return result;
	}
	/**
	 * 1+N번의 쿼리가 발생한다.
	 * 별도의 OrderQueryDto를 정의해야 한다.
	 * 이전에 정의해둔 OrderDto를 사용하게 되면 OrderQueryRepository가 컨트롤러에 의존하게 되기 때문.
	 */
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> ordersV4(){
		return orderQueryRepository.findOrderQueryDtos();
	}
	
	/**
	 * DTO 최적화 버전.
	 * 쿼리가 총 2번 발생한다.
	 * 1. 오더를 먼저 조회해서 가져온다.
	 * 2. 오더 리스트로부터 오더 아이디만을 추출해 리스트로 만든다.
	 * 3. 오더 아이디 리스트를 가지고 IN 쿼리를 보내 연관된 모든 OrderItem을 가져온다.
	 * 4. OrderItem에 있는 ID를 기준으로 Map으로 변환한다. (Collectors.groupingBy)
	 * 5. 각 오더에 대해서 setOrderItems를 이용해 OrderItems를 추가해준다.
	 */
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV5(){
		return orderQueryRepository.findAllByDto();
	}
	
	/**
	 * DTO 최적화 버전.
	 * 쿼리가 총 1번 발생한다.
	 * 
	 * 1. OrderFlatDto를 만들어서 데이터베이스의 row를 한 줄 한 줄 그대로 받아 저장한다.
	 * 2. OrderFlatDto를 Map<OrderQueryDto, List<OrderItemQueryDto>>로 만들어준다. (groupingBy, mapping)
	 * 3. Map으로부터 List<OrderQueryDto>를 만들어준다.
	 * 
	 */
	@GetMapping("/api/v6/orders")
	public List<OrderQueryDto> ordersV6(){
		List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
		
		Map<OrderQueryDto, List<OrderItemQueryDto>> collect = flats.stream().collect(Collectors.groupingBy(o-> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
				Collectors.mapping(o-> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
				));
		
		return collect.entrySet().stream()
				.map(e-> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
				.collect(Collectors.toList());
	}
	
	
	@Data
	static class OrderDto{
		
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems;
		
		public OrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
			orderItems = order.getOrderItems().stream().map(o-> new OrderItemDto(o))
						.collect(Collectors.toList());
		}
		
	}
	
	@Data
	static class OrderItemDto {
		private String itemName; //상품명
		private int orderPrice;  //주문 가격
		private int count;		//주문 수량
		
		public OrderItemDto(OrderItem orderItem) {
			itemName = orderItem.getItem().getName();
			orderPrice = orderItem.getItem().getPrice();
			count = orderItem.getCount();
		}
	}
}

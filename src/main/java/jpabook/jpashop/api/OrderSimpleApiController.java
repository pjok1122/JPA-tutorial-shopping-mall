package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
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
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;
	
	/**
	* V1. 엔티티 직접 노출
	* - Hibernate5Module 모듈 등록, LAZY=null 처리
	* - 양방향 관계 문제 발생 -> @JsonIgnore
	*/
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1(){
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for(Order order : all) {
			order.getMember().getName(); //Lazy 강제 초기화
			order.getDelivery().getAddress(); //Lazy 강제 초기화
		}
		return all;
	}
	/**
	* V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
	* - 단점: 지연로딩으로 쿼리 N번 호출
	*/
	@GetMapping("/api/v2/simple-orders")
	public OrderResult<List<OrderSimpleQueryDto>> ordersV2(){
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderSimpleQueryDto> orderDto = orders.stream().map(o -> new OrderSimpleQueryDto(o)) 	//OrderSimpleQueryDto::new
						.collect(Collectors.toList());
		
		return new OrderResult<List<OrderSimpleQueryDto>>(orderDto);
	}
	
	/**
	* V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
	* - fetch join으로 쿼리 1번 호출
	* 단점 : select 절에 불필요한 데이터가 포함되기 때문에 네트워크 트래픽이 조금 더 높음.
	* 장점 : 재활용성이 높음. 다른 API에서도 사용할 수 있음.
	*/
	@GetMapping("/api/v3/simple-orders")
	public OrderResult<List<OrderSimpleQueryDto>> ordersV3(){
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		List<OrderSimpleQueryDto> orderDto = orders.stream().map(OrderSimpleQueryDto::new)
		.collect(Collectors.toList());
		
		return new OrderResult<List<OrderSimpleQueryDto>>(orderDto);
	}
	
	/**
	* V4. JPA에서 DTO로 바로 조회
	* - 쿼리 1번 호출
	* - select 절에서 원하는 데이터만 선택해서 조회
	* - Repository에서 DTO를 조회한다는 것 자체가 Repository의 기능에 맞지 않음.
	* - 따라서 repository.order.simplequery 패키지를 두고 OrderSimpleQueryRepository를 새로 만드는 게 좋음.
	* 장점 : 성능 최적화
	* 단점 : DTO를 조회했기 때문에 재활용이 거의 불가능하고 JPA에서 관리하는 객체가 아님. 코드도 복잡.
	*/
	@GetMapping("/api/v4/simple-orders")
	public OrderResult<List<OrderSimpleQueryDto>> ordersV4(){
		return  new OrderResult<>(orderSimpleQueryRepository.findOrderDtos());
	}
	
	@Data
	@AllArgsConstructor
	static class OrderResult<T>{
		private T data;
	}
	

}

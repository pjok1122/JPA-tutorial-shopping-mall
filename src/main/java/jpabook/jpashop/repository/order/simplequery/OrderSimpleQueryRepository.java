package jpabook.jpashop.repository.order.simplequery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
	
	private final EntityManager em;
	
	/**
	 * DTO로 바로 조회하는 로직.
	 * 엔티티를 생성자로 넘겨주면 엔티티의 pk값이 넘어가기 때문에 새로운 생성자를 만들었음.
	 */
	public List<OrderSimpleQueryDto> findOrderDtos() {
		return em.createQuery(
				"select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
				" join o.member m" +
				" join o.delivery d", OrderSimpleQueryDto.class
				)
		.getResultList();
		
	}
}

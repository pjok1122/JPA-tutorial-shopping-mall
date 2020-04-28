package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceTest {

	@Autowired EntityManager em;
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;
	
	@Test
	public void 상품주문() throws Exception{
		//given
		Member member = createMember();
		Item book = createBook("시골 JPA", 10000, 10);
		
		int orderCount = 2;
		//when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		Order getOrder = orderRepository.findOne(orderId);
		
		System.out.println(getOrder.getTotalPrice());
		
		//상품 주문시 상태는 ORDER여야 한다.
		assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus());
		//주문한 상품 종류 수가 정확해야 한다.
		assertThat(1).isEqualTo(getOrder.getOrderItems().size());
		//주문 가격은 가격 * 수량이다.
		assertThat(10000* orderCount).isEqualTo(getOrder.getTotalPrice());
		//주문 수량만큼 재고가 줄어야 한다.
		assertThat(8).isEqualTo(book.getStockQuantity());
	}


	
	
	@Test(expected = NotEnoughStockException.class)
	public void 상품주문_재고수량초과() throws Exception{
		//given
		Member member = createMember();
		Item book = createBook("시골 JPA", 10000, 10);
		int orderCount = 11;
		
		//when
		orderService.order(member.getId(), book.getId(), orderCount);
		
		//then
		fail("재고 수량 부족 예외가 발생해야 한다.");
	}

	@Test
	public void 주문취소() throws Exception {
		//given
		Member member = createMember();
		Item item = createBook("시골 JPA", 10000, 10);
		
		int orderCount = 2;
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

		//when
		orderService.cancelOrder(orderId);
		//then
		Order getOrder = orderRepository.findOne(orderId);
		
		//주문 상태는 CACNLE이 되어야 한다.
		assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());
		
		//주문이 취소된 상품은 그만큼 재고가 증가해야 한다.
		assertThat(item.getStockQuantity()).isEqualTo(10);
	}
	
	private Item createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}
	
	
	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("서울", "ㅇㅇ", "123-123"));
		em.persist(member);
		return member;
	}
}

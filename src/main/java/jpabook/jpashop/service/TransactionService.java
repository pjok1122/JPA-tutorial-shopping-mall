package jpabook.jpashop.service;



import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionService {
	
	private final EntityManager em;
	private final Transaction2Service ts;
	
	@Transactional				//   프록시객체
	public void outerTransaction() {
		ts.externalTransaction();		//requries_new
		System.out.println("==================");
		ts.externalTransaction2();		//requires_new
		System.out.println(ts.getClass());
		System.out.println("Outer : "+ em.getDelegate());			//영속성컨텍스트가 짠!
//		System.out.println("Outer : "+ em.find(Member.class, 1L));	//Select 쿼리 발생.
		innerTransaction();
	}
	
	@Transactional						//트랜잭션을 무조건 새로 생성! (같은 클래스에서는 동작X)
	public void innerTransaction() {
		System.out.println("Inner : "+ em.getDelegate());			//영속성 컨텍스트 공유가 됨.
//		System.out.println("Inner : "+ em.find(Member.class, 1L));	//1차캐시에서 조회.
	}
	
}

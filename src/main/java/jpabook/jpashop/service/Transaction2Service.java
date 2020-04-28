package jpabook.jpashop.service;


import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Transaction2Service {
	
	private final EntityManager em;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void externalTransaction() {
		System.out.println("External : "+ em.getDelegate());			//동일한 영속성 컨텍스트 사용
//		System.out.println("External : "+ em.find(Member.class, 1L));	//쿼리가 발생X
		externalTransaction2();
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void externalTransaction2() {
		System.out.println("External : "+ em.getDelegate());			//동일한 영속성 컨텍스트 사용
//		System.out.println("External : "+ em.find(Member.class, 1L));	//쿼리가 발생X
	}
}

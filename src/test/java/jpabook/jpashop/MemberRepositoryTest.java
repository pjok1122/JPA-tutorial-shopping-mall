package jpabook.jpashop;

import static org.junit.Assert.*;

import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MemberRepositoryTest {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Test
	@Transactional
	@Rollback(false)
	public void testMember() throws Exception{
		//given
		Member member = new Member();
		member.setName("memberA");
		
		//when
		memberRepository.save(member);
		Member findMember = memberRepository.findOne(member.getId());
		
		//then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
		Assertions.assertThat(findMember).isEqualTo(member);

	}
}

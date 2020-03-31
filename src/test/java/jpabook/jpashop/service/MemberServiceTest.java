package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@RunWith(SpringRunner.class) //Junit 실행할 때, Spring이랑 함께 실행한다는 뜻.
@SpringBootTest				//SpringBoot와 함께 실행.
@ActiveProfiles("test")
@Transactional				//Transaction 걸고 테스트한 후 전부 롤백.(테스트에서만 롤백함)
public class MemberServiceTest {

	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	
	
//	@Autowired EntityManager em;
	
//	@Rollback(false)
	@Test
	public void 회원가입() throws Exception{
		//given
		Member member = new Member();
		member.setName("kim");
		
		//when
		Long savedId = memberService.join(member);
		
//		em.flush(); //DB에 INSERT 쿼리가 들어가게 됨.
		Member findMember = memberRepository.findOne(savedId);
		
		//then
		assertThat(member).isEqualTo(findMember);
	}
	
	@Test(expected = IllegalStateException.class)
	public void 중복_회원_예외() throws Exception{
		//given
		Member member1 = new Member();
		member1.setName("kim");
		Member member2 = new Member();
		member2.setName("kim");
		
		//when
		memberService.join(member1);
		memberService.join(member2); //예외가 발생해야 한다.
		
		//then
		fail("예외가 발생해야 한다.");
		
	}
}

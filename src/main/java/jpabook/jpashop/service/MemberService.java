package jpabook.jpashop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor //final이 붙어있는 필드를 인자로 받아서 생성.
public class MemberService {

	//변경할 이유가 없으니 final을 붙여주는 것이 좋다.
	private final MemberRepository memberRepository;
	
	//생성자로 주입을 받으면 테스트할 때 MemberRepository에 대한 Mock을 넘겨줄 수 있어서 더 좋다.
//	public MemberService(MemberRepository memberRepository) {
//		this.memberRepository = memberRepository;
//	}
	
	/*
	 * 회원  가입
	 */
	@Transactional
	public Long join(Member member) {
		validateDuplicateMember(member); //중복 회원 검증
		memberRepository.save(member);
		return member.getId();
	}
	
	//두명이 동시에 접근하면 유효성 검증을 통과하게 됨. 따라서 이름을 유니크 제약조건을 잡아주는게 더 안전함.
	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if(!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}
	
	//회원 전체 조회
	public List<Member> findMembers(){
		return memberRepository.findAll();
	}
	
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}
}

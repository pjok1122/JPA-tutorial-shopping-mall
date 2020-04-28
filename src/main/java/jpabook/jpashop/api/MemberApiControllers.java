package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.api.MemberApiController.CreateMemberResponse;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiControllers {
	
	private final MemberRepository memberRepository;
	
	@GetMapping("/api/v1.1/members")
	public List<Member> getMembersV1(){
		return memberRepository.findAll();
	}
	
	public List<MemberDto> getMembersV2(){
		List<Member> members = memberRepository.findAll();
		return members.stream().map(m->new MemberDto(m.getName(), m.getAddress()))
		.collect(Collectors.toList());
	}
	

	
	@Data
	public static class Result<T>{
		private T data;
	}
	
	@Data
	@AllArgsConstructor
	static class MemberDto{
		private String name;
		private Address address;
	}
}

package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
	
	private final MemberService memberService;
	
	//엔티티의 모든 정보가 노출됨.
	//@JsonIgnore를 이용하면 불필요한 데이터를 제거할 수는 있다.. 근데 그 데이터를 다른 api에서 필요로 한다면^^?
	//@JsonIgnore를 쓰면 엔티티에 프레젠테이션 로직이 들어가기 때문에 또 문제가 생김.
	//엔티티의 필드명이 바뀌면 API 스펙이 변경됨.
	//List를 반환하며 Generic을 사용하고 있으므로, count를 추가해달라고 하면 추가할 수가 없음.
	@GetMapping("/api/v1/members")
	public List<Member> membersV1(){
		return memberService.findMembers();
	}
	
	@GetMapping("/api/v2/members")
	public Result memberV2() {
		//자바8 스펙.
		List<Member> findMembers = memberService.findMembers();
		List<MemberDto> collect = findMembers.stream()
					.map(m -> new MemberDto(m.getName()))
					.collect(Collectors.toList());
		
		return new Result(collect.size(), collect);
	}
	
	//T타입을 써야 확장성이 좋다고 함.
	@Data
	@AllArgsConstructor
	static class Result<T>{
		private int count;
		private T data;
	}
	
	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String name;
	}
	
	//프레젠테이션의 검증로직(화면 검증 로직)이 Member 엔티티에 들어가있음.
	//엔티티의 필드이름이 바뀌거나 하면 API 스펙 자체가 바뀌어버림..;;
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	
	//api 스펙에 맞는 별도의 객체(dto)를 만드는 것이 정석.
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());
		
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	
	//Put은 같은 수정을 여러번 호출해도 변하지 않음.
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(
			@PathVariable("id") Long id,
			@RequestBody @Valid UpdateMemberRequest request)
	{
		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);
		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}
	
	//엔티티에는 애노테이션을 최대한 자제하고
	//DTO는 lombok 애노테이션 막 갈겨도 크게 문제없음ㅋ
	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse{
		private Long id;
		private String name;
	}
	
	@Data
	static class UpdateMemberRequest{
		private String name;
	}
	
	@Data
	static class CreateMemberRequest{
		@NotEmpty
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class CreateMemberResponse {
		private Long id;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

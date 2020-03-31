package jpabook.jpashop.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
	
	private final MemberService memberService;
	
	@GetMapping("/members/new")
	public String createForm(Model model) {
		model.addAttribute("memberForm", new MemberForm());
		return "members/createMemberForm";
	}
	
	//화면에 Fit한 MemberForm을 만드는게 좋음.
	//넘어오는 데이터 검증과 비즈니스 로직 검증이 다를 수도 있음.
	//코드가 복잡해질 수도 있음.
	@PostMapping("/members/new")
	public String create(@Valid MemberForm form, BindingResult result) {
		
		if(result.hasErrors()) {
			return "members/createMemberForm";
		}
		
		Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
		
		Member member = new Member();
		member.setName(form.getName());
		member.setAddress(address);
		
		memberService.join(member);
		return "redirect:/";
	}
	
	//서버와 화면이 통합되어있기 때문에 Member 자체를 반환해도 크게 문제가 되진 않음.
	//하지만 가급적 MemberDto를 만들어서 반환하는 것이 좋다.
	// API를 개발한다면, 무조건!! DTO를 만들어 반환해야 한다. (API는 스펙이기 때문에 Entity에 의존해서는 안됨)
	@GetMapping("/members")
	public String list(Model model) {
		List<Member> members = memberService.findMembers();
		model.addAttribute("members", members);
		return "members/memberList";
	}
}

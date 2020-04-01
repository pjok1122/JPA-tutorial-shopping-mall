package jpabook.jpashop.controller;

import static jpabook.jpashop.controller.BookForm.createBookForm;
import static jpabook.jpashop.domain.Book.createBook;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {
	
	private final ItemService itemService;
	
	@GetMapping("/items/new")
	public String createForm(Model model) {
		model.addAttribute("form", new BookForm());
		return "items/createItemForm";
	}
	
	//setter를 다 제거하고 createBook 같은 static 생성 메서드를 만드는 게 더 깔끔하다.
	@PostMapping("/items/new")
	public String create(BookForm form) {
		Book book = createBook(form);
		
		itemService.saveItem(book);
		return "redirect:/items";
	}
	
	@GetMapping("/items")
	public String list(Model model) {
		List<Item> items = itemService.findItems();
		model.addAttribute("items", items);
		return "items/itemList";
	}
	
	@GetMapping("items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		Book item = (Book) itemService.findOne(itemId);
		BookForm form = createBookForm(item);
		
		model.addAttribute("form", form);
		return "items/updateItemForm";
	}
	
	//컨트롤러에서는 엔티티를 생성하지 말자!
	//BookForm과 같은 객체는 서비스 계층으로 넘기지 말자!
	@PostMapping("items/{itemId}/edit")
	public String updateItem(@ModelAttribute("form") BookForm form, Model model) {
		//준영속 상태의 Book. 
		Book book = createBook(form);
		itemService.saveItem(book);
		
		//제일 좋은 전략
		//updateItem(itemId, price, ...) 너무 길어질 경우
		//updateItem(UpdateItemDto itemDto)
		return "redirect:/items";
	}
}

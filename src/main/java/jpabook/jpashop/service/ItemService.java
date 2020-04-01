package jpabook.jpashop.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate.Param;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
	
	private final ItemRepository itemRepository;
	
	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}
	
	public List<Item> findItems(){
		return itemRepository.findAll();
	}
	
	public Item findOne(Long itemId) {
		return itemRepository.findOne(itemId);
	}
	
	//일반적인 방법. merge는 넘어오는 모든 객체를 변경하기 때문에 직접 변경감지를 사용하는 것이 좋다.
	@Transactional
	public void updateItem(Long itemId, Book book) {
		Item findItem = itemRepository.findOne(itemId);
		
		//findItem.change(price,name, stockQuantity)
		findItem.setPrice(book.getPrice());
		findItem.setName(book.getName());
		findItem.setStockQuantity(book.getStockQuantity());
		
	}
}

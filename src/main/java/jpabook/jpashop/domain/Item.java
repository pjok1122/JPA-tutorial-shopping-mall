package jpabook.jpashop.domain;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import jpabook.jpashop.domain.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
	@Id @GeneratedValue
	@Column(name="item_id")
	private Long id;
	
	private String name;
	
	private int price;
	
	private int stockQuantity;
	
	@ManyToMany(mappedBy = "items")
	private List<Category> categories;
	
	//==비즈니스 로직==//
	/*
	 * stock 증가
	 */
	public void addStock(int quantity) {
		this.stockQuantity += quantity;
	}
	
	
	/*
	 * stock 감소
	 */
	public void removeStock(int quantity) {
		if(this.stockQuantity < quantity) {
			throw new NotEnoughStockException("need more stock");
		}
		this.stockQuantity -= quantity;
	}
	
}

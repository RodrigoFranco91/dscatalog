package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;


public class ProductDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String description;
	private String price;
	private String imgUrl;
	private Instant date;
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO() {
		
	}

	public ProductDTO(Long id, String name, String description, String price, String imgUrl, Instant date) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
		this.date = date;
	}
	
	
	//Vamos usar para o findAll() de todos produtos;
	//Para todos não vamos mostrar as categorias.
	public ProductDTO(Product entity) {
		this.id = entity.getId();
		this.name = entity.getName();;
		this.description = entity.getDescription();;
		this.price = entity.getPrice();;
		this.imgUrl = entity.getImgUrl();;
		this.date = entity.getDate();;
	}
	
	//Vamos usar para o findById() de um produto;
	//Agora vamos mostrar as categorias.
	//this(entity) vai chamar o consturtor anterior, pois ele tb recebe Prodcut
	public ProductDTO(Product entity, Set<Category> categories) {
		this(entity);
		categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}
	
	
	
}

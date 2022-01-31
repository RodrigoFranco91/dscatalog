package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private Long id;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception{
		id = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	

	
	@Test
	public void deleteShouldDeleteObjcetWhenIdExists() {
				
		//Ação
		repository.deleteById(id);
		Optional<Product> result = repository.findById(id);
		
		//Comparação
		Assertions.assertFalse(result.isPresent());
		
		
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNoesNotExist() {
		
		//Comparação
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
						
			//Ação
			repository.deleteById(nonExistingId);
			
		});
		
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1L, product.getId());
		
	}
	
	@Test
	public void findByIdShouldReturnAOptionalNoEmptyWithIdExists() {
		
		Optional<Product> optional = repository.findById(id);
		
		Assertions.assertTrue(optional.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnAOptionalEmptyWithIdDoesNotExists() {
		
		Optional<Product> optional = repository.findById(nonExistingId);
		
		Assertions.assertTrue(optional.isEmpty());
	}

}

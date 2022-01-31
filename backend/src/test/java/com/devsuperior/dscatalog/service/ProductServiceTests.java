package com.devsuperior.dscatalog.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Long categoryIdExisting;
	private Category category;
	private ProductDTO productDTO;
	
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		categoryIdExisting = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		productDTO = Factory.createProductDTO();
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		
		//Configurando o comportamento simulado do objeto ProductRepository. 
		// Não esqueça que para cada cenário as vezes temos que mudar o argumento que passamos
		//Essa configuração é para metodos que retornam Void (usamos .do)
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);

		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(categoryRepository.getOne(categoryIdExisting)).thenReturn(category);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		
		Assertions.assertDoesNotThrow(() ->{
			service.delete(existingId);
		});
		
		//Verificando se o método deleteById() foi chamado, no caso do nosso objeto simulado (mockado)
		Mockito.verify(repository).deleteById(existingId);
		
		//Verificando se o método deleteById() foi chamado duas vezes
		//Mockito.verify(repository, Mockito.times(2)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNoExist() {
		
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository).deleteById(nonExistingId);

	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		
		
		Assertions.assertThrows(DataBaseException.class, () ->{
			service.delete(dependentId);
		});
		
		Mockito.verify(repository).deleteById(dependentId);

	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
		
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			ProductDTO result = service.findById(nonExistingId);
			
		});
	}
	
	@Test
	public void updateShouldReturnProructDTOWhenIdExistis() {
		
		ProductDTO result = service.update(existingId, productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			ProductDTO result = service.update(nonExistingId, productDTO);
		});
		
	}
	
}

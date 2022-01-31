package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception{
				
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
		.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(countTotalProducts))
			.andExpect(jsonPath("$.content").exists())
			.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
	
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExcist() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		//Precisamos enviar o corpo da Request em formado JSON:
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		String expcetedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		
		mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(existingId))
					.andExpect(jsonPath("$.name").value(expcetedName))
					.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExcist() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		//Precisamos enviar o corpo da Request em formado JSON:
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		String expcetedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		
		mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound());
					
	}
	
	@Test
	public void insertShouldReturnProductDTO() throws Exception {
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		//Precisamos enviar o corpo da Request em formado JSON:
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(countTotalProducts + 1));
		
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExcist() throws Exception{
		
		mockMvc.perform(delete("/products/{id}", nonExistingId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound());			
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExcist() throws Exception{
		
		mockMvc.perform(delete("/products/{id}", existingId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNoContent());			
	}

}

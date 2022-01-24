package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository productRepository;

	// Com isso eu garanto que todas operações no método findAll será executado na
	// mesma transação.
	// ReadOnly em true eu não dou Lock no banco, ou seja, como só vou ler o banco
	// ele continua operando.
	// e melhora o desemepenho.
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll() {
		List<Product> list = repository.findAll();
		List<ProductDTO> dto = list.stream().map(ProductDTO::new).collect(Collectors.toList());
		return dto;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Categoria de id " + id + " não foi encontrada."));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product product = new Product();
		copyDtoToEntity(dto, product);
		product = repository.save(product);
		return new ProductDTO(product);
	}



	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product product = repository.getOne(id);
			copyDtoToEntity(dto, product);
			repository.save(product);
			return new ProductDTO(product);
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Não há categoria com id "+ id);
		}

	}

	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Não há categoria com id "+ id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Há produtos com essa categoria, por isso não pode deletá-la!");
		}
	}

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);
		//Page já é Stream, logo não precisamos .steam()
		Page<ProductDTO> dto = list.map(ProductDTO::new);
		return dto;
	}


	private void copyDtoToEntity(ProductDTO dto, Product product) {
		
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setDate(dto.getDate());
		product.setImgUrl(dto.getImgUrl());
		product.setPrice(dto.getPrice());
		
		//Garantir que não tem Categoria na lista
		product.getCategories().clear();
		
		//Convertendo CategoriaDTO em categoria e depois colocando no Product
		dto.getCategories().forEach(productDTO -> {
			Category categoryEntity = productRepository.getOne(productDTO.getId());
			product.getCategories().add(categoryEntity);
		});
		
	}
}
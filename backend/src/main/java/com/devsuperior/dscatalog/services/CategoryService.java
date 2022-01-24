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

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dto.CategoryDTO;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	// Com isso eu garanto que todas operações no método findAll será executado na
	// mesma transação.
	// ReadOnly em true eu não dou Lock no banco, ou seja, como só vou ler o banco
	// ele continua operando.
	// e melhora o desemepenho.
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		List<CategoryDTO> dto = list.stream().map(CategoryDTO::new).collect(Collectors.toList());
		return dto;
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Categoria de id " + id + " não foi encontrada."));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category category = new Category();
		category.setName(dto.getName());
		category = repository.save(category);
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category category = repository.getOne(id);
			category.setName(dto.getName());
			repository.save(category);
			return new CategoryDTO(category);
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
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
		Page<Category> list = repository.findAll(pageRequest);
		//Page já é Stream, logo não precisamos .steam()
		Page<CategoryDTO> dto = list.map(CategoryDTO::new);
		return dto;
	}



}
// Referente ao método findAll():
// 		List<CategoryDTO> dto = list.stream().map(CategoryDTO::new).collect(Collectors.toList());  Pode ser substituido por:

//		List<CategoryDTO> dto = list.stream().map(category -> new CategoryDTO(category)).collect(Collectors.toList()); que pode ser substituido por:

/*

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		List<CategoryDTO> dto = list.stream().map(category -> {
			return new CategoryDTO(category);
		}).collect(Collectors.toList());
		return dto;
	}

*/
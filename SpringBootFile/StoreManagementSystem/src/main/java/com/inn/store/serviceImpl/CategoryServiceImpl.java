package com.inn.store.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.inn.store.JWT.JwtFilter;
import com.inn.store.POJO.Category;
import com.inn.store.constents.storeConstants;
import com.inn.store.dao.CategoryDao;
import com.inn.store.service.CategoryService;
import com.inn.store.utils.storeUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

	
	
	@Autowired
	CategoryDao categoryDao;
	
	
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		
		try {
			
			if(jwtFilter.isAdmin()) {
				if(validateCategoryMap(requestMap,false)) {
					categoryDao.save(getCategoryFromMap(requestMap,false));
					return  storeUtils.getResponseEntity("Category Added Successfully",HttpStatus.OK);

					
				}
				
			}
			else {
				return  storeUtils.getResponseEntity(storeConstants.UNAUTHORIZES_ACCESS,HttpStatus.UNAUTHORIZED);

			}
			
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

		
		
		
	}

	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId) {
				return true;
			}else if (!validateId) {
				return true;
			}
		}
		return false;
	}
	
	private Category getCategoryFromMap(Map<String,String>requestMap,Boolean isAdd) {
		Category category =new Category();
		if(isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
		
		
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
		try {
			if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				logger.info("Inside if");
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
			}
			else  {
			return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
			}

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(validateCategoryMap(requestMap,true)) {
					Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						categoryDao.save(getCategoryFromMap(requestMap, true));
						return storeUtils.getResponseEntity("Category Updated Sussfully",HttpStatus.OK);

						
					}else {
						return storeUtils.getResponseEntity("Category id does not exis",HttpStatus.OK);
					}
				}	
				return  storeUtils.getResponseEntity(storeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);

			}else {
				return  storeUtils.getResponseEntity(storeConstants.UNAUTHORIZES_ACCESS,HttpStatus.UNAUTHORIZED);

			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	

}

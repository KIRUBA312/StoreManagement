package com.inn.store.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.inn.store.POJO.Category;
import com.inn.store.constents.storeConstants;
import com.inn.store.rest.CategoryRest;
import com.inn.store.service.CategoryService;
import com.inn.store.utils.storeUtils;


@RestController
public class CategoryRestImpl implements CategoryRest {
	
	
	
	@Autowired
	CategoryService categoryService;
	
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {

		try {
			return categoryService.addNewCategory(requestMap);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {

		try {
			return categoryService.getAllCategory(filterValue);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			
			return categoryService.updateCategory(requestMap);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

package com.inn.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.inn.store.POJO.Category;
import com.inn.store.POJO.User;
import com.inn.store.wrapper.UserWrapper;

public interface CategoryDao extends JpaRepository<Category,Integer> {
	
	List<Category> getAllCategory();
	
	
	
	

}

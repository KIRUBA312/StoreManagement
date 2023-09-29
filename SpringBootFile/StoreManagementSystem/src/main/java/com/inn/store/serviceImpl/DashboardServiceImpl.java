package com.inn.store.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inn.store.dao.BillDao;
import com.inn.store.dao.CategoryDao;
import com.inn.store.dao.ProductDao;
import com.inn.store.service.DashboardService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service

public class DashboardServiceImpl implements DashboardService {

	
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	ProductDao productDao;

	@Autowired
	BillDao billDao;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String,Object>map= new HashMap<String,Object>();
		map.put("category", categoryDao.count());
		map.put("product", productDao.count());
		map.put("bill", billDao.count());
		return new ResponseEntity<>(map,HttpStatus.OK);

	}
	

}

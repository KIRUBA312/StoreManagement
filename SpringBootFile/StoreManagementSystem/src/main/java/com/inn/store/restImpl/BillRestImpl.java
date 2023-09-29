package com.inn.store.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.inn.store.POJO.Bill;
import com.inn.store.constents.storeConstants;
import com.inn.store.rest.BillRest;
import com.inn.store.service.BillService;
import com.inn.store.utils.storeUtils;


@RestController
public class BillRestImpl implements BillRest {

	
	
	@Autowired
	BillService billService;
	
	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		try {
			return billService.getBills();
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
		
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		try {
			return billService.getPdf(requestMap);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			return billService.deleteBill(id);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}



}

package com.inn.store.serviceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.inn.store.JWT.CustomerUsersDetailsService;
import com.inn.store.JWT.JwtFilter;
import com.inn.store.JWT.JwtUtil;
import com.inn.store.POJO.User;
import com.inn.store.constents.storeConstants;
import com.inn.store.dao.UserDao;
import com.inn.store.service.UserService;
import com.inn.store.utils.storeUtils;
import com.inn.store.utils.EmailUtils;
import com.inn.store.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;





@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	
	@Autowired
	
	UserDao userDao;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	CustomerUsersDetailsService customerUsersDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;
	
	@Override
	public ResponseEntity<String> signup(Map<String, String> requestMap) {
		final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

		logger.info("Inside signup{}",requestMap);
		try {
		
		if(validateSignUpMap(requestMap)) {
			
			User user  =userDao.findByEmailId(requestMap.get("email"));
			if(Objects.isNull(user)) {
				userDao.save(getUserFromMap(requestMap));
				return storeUtils.getResponseEntity("Successfully Registed.", HttpStatus.OK);
				
				
			}
			else {
				return storeUtils.getResponseEntity("Email already exist",HttpStatus.BAD_REQUEST);
			}
			
			
		}
		else {
			return storeUtils.getResponseEntity(storeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
		}
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	
	private boolean validateSignUpMap(Map<String,String>requestMap) {
		
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") 
				&& requestMap.containsKey("email") && requestMap.containsKey("password")) {
			
			return true;
			
		}
		return false;
		
	}
	private User getUserFromMap(Map<String,String>requestMap) {
		User user=new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
		
	}


	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

		logger.info("Inside login");
		try {
			
			org.springframework.security.core.Authentication auth=  authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
					);
			if(auth.isAuthenticated()) {
				if(customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>("{\"token\":\""+
				jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
						customerUsersDetailsService.getUserDetail().getRole())+"\"}",
							HttpStatus.OK);
				}
				else {
					return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}",
							HttpStatus.BAD_REQUEST);
				}
			}
			
		}catch(Exception ex) {
			logger.error("{}",ex);
		}
		return new ResponseEntity<String>("{\",essage\":\""+"Bad Credentials"
				+ "."+"\"}",
				HttpStatus.BAD_REQUEST);
	}


	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
				
			}else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				
				if(!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
					
					return storeUtils.getResponseEntity("User  Status Updated Successfully",HttpStatus.OK);

				}else{
					
					return storeUtils.getResponseEntity("User id doesn't not exist",HttpStatus.OK);
				}
				
			}else {
				return  storeUtils.getResponseEntity(storeConstants.UNAUTHORIZES_ACCESS,HttpStatus.UNAUTHORIZED);
				
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}


	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if(status!=null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER:- "+user+" \n approved by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
			
		}else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:- "+user+"\n disabled by \n ADMIN:-" + jwtFilter.getCurrentUser(),allAdmin);

			
		}
		
	}


	@Override
	public ResponseEntity<String> checkToken() {
		return storeUtils.getResponseEntity("true", HttpStatus.OK);
	}


	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		
		try {
			User userObj=userDao.findByEmail(jwtFilter.getCurrentUser());
			if(!userObj.equals(null)) {
				if(userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return  storeUtils.getResponseEntity("Password Update Successfully",HttpStatus.OK);

					
				}
				return  storeUtils.getResponseEntity("Incorrect Old Password",HttpStatus.BAD_REQUEST);
				
				
			}
			return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

			
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user=userDao.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotMail(user.getEmail(),"Credentials by Store management system", user.getPassword());
			}
			return  storeUtils.getResponseEntity("Check Your Mail for Credentials",HttpStatus.OK);
 
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

	}


	
	
	
	

}

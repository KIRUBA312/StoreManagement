package com.inn.store.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inn.store.JWT.JwtFilter;
import com.inn.store.POJO.Bill;
import com.inn.store.constents.storeConstants;
import com.inn.store.dao.BillDao;
import com.inn.store.service.BillService;
import com.inn.store.utils.storeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class BillServiceImpl implements BillService {
	
	
	@Autowired
	BillDao billDao;
	
	
	@Autowired
	JwtFilter jwtFilter;


	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		//log.info("Inside generateReport");
		
		try {
			String fileName;
			if(validateRequestMap(requestMap)) {
				if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")) {
					fileName = (String) requestMap.get("uuid");
				}else {
					 fileName = storeUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}
				String data ="Name:"+requestMap.get("name")+"\n"+
						"Contact Number:"+requestMap.get("contactNumber")+"\n"+
						"Email:"+requestMap.get("email")+"\n"+
						"Payment Method:"+requestMap.get("paymentMethod");
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(storeConstants.STORE_LOCATION+"\\"+fileName+".pdf"));
				
				document.open();
				setRectangleInPdf(document);
				
				Paragraph chunk = new Paragraph("Store Management System",getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);
				
				Paragraph paragraph = new Paragraph(data+"\n \n",getFont("Data"));
				document.add(paragraph);
				
				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);
				
				JSONArray jsonArray = storeUtils.getJsonArrayFromString((String)requestMap.get("productDetails"));
				for(int i=0;i<jsonArray.length();i++) {
					addRows(table,storeUtils.getMapFromJson(jsonArray.getString(i)));
				}
				document.add(table);
				
				Paragraph footer = new Paragraph("Total:"+requestMap.get("totalAmount")+"\n"
						
						+"Thankyou for visiting ,please visite again!!",getFont("Data"));
				document.add(footer);
				document.close();
				return  new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);

				
				
				
					
			}
			return  storeUtils.getResponseEntity("Request data not found",HttpStatus.OK);
				
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return  storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
		
	}


	private void addRows(PdfPTable table, Map<String, Object> data) {
		//log.info("Inside rows");
		table.addCell((String)data.get("name"));
		table.addCell((String)data.get("category"));
		table.addCell((String)data.get("quantity"));
		table.addCell(Double.toString((Double)data.get("price")));
	    table.addCell(Double.toString((Double)data.get("total")));
	    

	}


	private void addTableHeader(PdfPTable table) {
		//log.info("Inside addTableHeader");
		Stream.of("Name","Category","Quantity","Price","Sub Total")
			.forEach(columnTitle ->  {
				PdfPCell header = new PdfPCell();
				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
				header.setBorderWidth(2);
				header.setPhrase(new Phrase(columnTitle));
				header.setBackgroundColor(BaseColor.YELLOW);
				header.setHorizontalAlignment(Element.ALIGN_CENTER);
				header.setVerticalAlignment(Element.ALIGN_CENTER);
				table.addCell(header);
			});
		
		
	}


	private Font getFont(String type) {
		
		switch(type) {
			case "Header":
				Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
				headerFont.setStyle(Font.BOLD);
				return headerFont;
			case"Data":
				Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
				dataFont.setStyle(Font.BOLD);
				return dataFont;
				
			default:
				return new Font();
		}
		
		
	}


	private void setRectangleInPdf(Document document) throws DocumentException {
		Rectangle rect = new Rectangle(577,825,18,15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		document.add(rect);

		
		
	}


	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill =new Bill();
			bill.setUuid((String)requestMap.get("uuid"));
			bill.setName((String)requestMap.get("name"));
			bill.setEmail((String)requestMap.get("email"));
			bill.setContactNumber((String)requestMap.get("contactNumber"));
			bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String)requestMap.get("totalAmount")));
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
			
			


			
		}catch(Exception ex){
			ex.printStackTrace();
		}	
	}


	private boolean validateRequestMap(Map<String, Object> requestMap) {
		return requestMap.containsKey("name") && 
				requestMap.containsKey("contactNumber") && 
				requestMap.containsKey("email") &&
				requestMap.containsKey("paymentMethod")  &&
				requestMap.containsKey("productDetails") &&
				requestMap.containsKey("totalAmount"); 
	}


	@Override
	public ResponseEntity<List<Bill>>getBills() {
		List<Bill>list = new ArrayList<>();
		if(jwtFilter.isAdmin()) {
			list = billDao.getAllBills();
			
		}else {
			list = billDao.getBillByUserName(jwtFilter.getCurrentUser());	
		}
		return new ResponseEntity<>(list,HttpStatus.OK);
	}


	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);
		logger.info("inside get PDF:request {}",requestMap); 
		try {
			byte[]byteArray =new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) 
				return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
			
			String filepath = storeConstants.STORE_LOCATION+"\\"+(String)requestMap.get("uuid")+".pdf";
			if(storeUtils.isFileExist(filepath)) {
				byteArray =getByteArray(filepath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}else {
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray=getByteArray(filepath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}


	private byte[] getByteArray(String filepath) throws Exception {
		File  inititalFile = new File(filepath);
		InputStream targerStream = new FileInputStream(inititalFile);
		byte[] byteArray = IOUtils.toByteArray(targerStream);
		targerStream.close();
		return byteArray;
	}


	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				billDao.deleteById(id);
				return storeUtils.getResponseEntity("Bill deleted successfully",HttpStatus.OK);
 
				
			}
			return storeUtils.getResponseEntity("Bill id does not exist",HttpStatus.OK);
			
			//return billService.deleteProduct(id);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storeUtils.getResponseEntity(storeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

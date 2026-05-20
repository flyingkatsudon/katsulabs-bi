package com.shinhan.biz.ga.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlnest.ANALYTICS_BDP_API;
import com.owlnest.util.IpCheckExternal;
import com.shinhan.biz.ga.GeneralAnalyticsBiz;
import com.shinhan.vo.ParamGaVO;

public class GeneralAnalyticsBizImpl implements GeneralAnalyticsBiz {

	private static final Logger logger = LoggerFactory.getLogger(GeneralAnalyticsBizImpl.class);
	
	@Override
	public Map<String, Object> getAnalyticsDocs(ParamGaVO param) throws Throwable {
		// TODO Auto-generated method stub
		
		Map<String, Object> map = new HashMap<String, Object>();

		//String currentSvr = "143.248.208.110"; // dev
		//String currentSvr = "13.209.126.208"; //aws
		String currentSvr = "10.10.30.27"; // private
		
		IpCheckExternal ipc = new IpCheckExternal();
		String userName = System.getProperty("user.name");
		String hostName = InetAddress.getLocalHost().getHostName();
		InetAddress addr = InetAddress.getLocalHost();
		String hostIp = null;
		
		try {
			hostIp = ipc.getIp();
		} catch (UnknownHostException e) {
			hostIp = addr.getHostAddress();
		}
		
		try {
			Date startTime = new Date();
			
			int limitCnt = 0;
			int pageCnt = 0;
			int currentPage = 0;
			
			if (param.getLimitCnt() == null) 
				limitCnt = 10;
			else 
				limitCnt = Integer.parseInt(param.getLimitCnt());
			
			if (param.getPageCnt() == null) 
				pageCnt = 20;
			else 
				pageCnt = Integer.parseInt(param.getPageCnt());
			
			if (param.getCurrentPage() == null)
				currentPage = -1;  
			else 
				currentPage = Integer.parseInt(param.getCurrentPage());
			
			String output = "";
			Integer port  = 43001;
			
			switch(param.getAnalyticsCode()) {
			case "A001":
				output = ANALYTICS_BDP_API.A001(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A002":
				output = ANALYTICS_BDP_API.A002(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A003":
				output = ANALYTICS_BDP_API.A003(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A004":
				output = ANALYTICS_BDP_API.A004(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A005":
				output = ANALYTICS_BDP_API.A005(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A006":
				output = ANALYTICS_BDP_API.A006(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A007":
				output = ANALYTICS_BDP_API.A007(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode(), param.getVersion()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A008":
				output = ANALYTICS_BDP_API.A008(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A009":
				output = ANALYTICS_BDP_API.A009(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			case "A010":
				output = ANALYTICS_BDP_API.A010(currentSvr, port, param.getSearchPattern(), param.getStartDate(), param.getEndDate()
						, Integer.parseInt(param.getSearchData()), param.getBusinessCode(), param.getCategoryCode()
						, userName, hostName, hostIp, param.getSection(), param.getPress(), limitCnt, pageCnt, currentPage);
				break;
			}

			Date endTime = new Date();
			long lTime = (endTime.getTime() - startTime.getTime());

			System.out.println("\n# Return Length : " + output.length());
			System.out.println("\n# Network Turnaround : " + lTime + " ms");
			
			// 조회 성공 but 결과값 없음
			if (output.trim().isEmpty()) {
				map.put("data", new ArrayList<Object>());
				map.put("hasDocs", false);
			} else {
				// 조회 성공
				//map.put("data",  new Gson().fromJson(output, Object.class));
				map.put("data",  output);
				map.put("hasDocs", true);
			}
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}
	
	@Override
	public void getAnalyticsXlsx(ParamGaVO param, HttpServletResponse res) throws Exception {

		String output = "";
		
		try {
			output = getAnalyticsDocs(param).get("data").toString();

			// 파싱
			JSONObject json = new JSONObject(output);
			JSONObject response = (JSONObject) json.get("response");
			JSONArray docs = (JSONArray) response.get("docs");
			
			ArrayList<Object> rows = new ArrayList<Object>(); // 데이터 리스트
			ArrayList<Object> columns = new ArrayList<Object>(); // column명 리스트에 저장
			
			for(Object o : docs) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject vo = (JSONObject) o;
				Iterator<String> keys = vo.keys();

				while(keys.hasNext()) {
				    String key = keys.next();
			    	if (!columns.contains(key)) {
			    		columns.add(key);
			    	}
			    	map.put(key, vo.get(key));
				}
				rows.add(map);
			}
			
			// Create a Workbook
	        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

	        /* CreationHelper helps us create instances of various things like DataFormat, 
	           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way*/ 
	        CreationHelper createHelper = workbook.getCreationHelper();

	        // Create a DataFormatter to format and get each cell's value as String
	        DataFormatter dataFormatter = new DataFormatter();
	        
	        // Create a Sheet
	        Sheet sheet = workbook.createSheet("sheet");

	        // Create a Row
	        Row headerRow = sheet.createRow(0);

	        // Create cells
	        for(int i = 0; i < columns.size(); i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(String.valueOf(columns.get(i)));
	        }

	        // Create Other rows and cells with employees data
	        // list: 데이터 row, columns: column 명 list
	        // headerRow가 있으니 row는 1번부터 데이터 채움
	        for(int i=0; i<rows.size(); i++) {
	            Row row = sheet.createRow(i+1);
	            
	        	Map<String, String> data = (Map<String, String>) rows.get(i);

	            for(int j=0; j<columns.size(); j++) {	

	                Cell cell = row.createCell(j);
	                cell.setCellValue(String.valueOf(data.get(columns.get(j))));

	                String cellValue = dataFormatter.formatCellValue(cell);
	 	            cell.setCellValue(cellValue);
	            }
	        }

			// Resize all columns to fit the content size
	        for(int i = 0; i < columns.size(); i++) {
	            sheet.autoSizeColumn(i);
	        }

	        // Write the output to a file
	        String d = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String fileName = param.getAnalyticsCode() + "_" + d + ".xlsx";
	        File file = new File(FilenameUtils.getName(fileName));
	        logger.debug("file -> {}", file);
	                
	        res.setHeader("Set-Cookie", "fileDownload=true; path=/");
	        res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getPath() + "\";"); 
	        //res.setContentType("application/octet-stream");
	        res.setContentType("application/x-msexcel");
	        //res.setHeader("Content-type", "application/json");
	        workbook.write(res.getOutputStream());

	        // Closing the workbook
	        workbook.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

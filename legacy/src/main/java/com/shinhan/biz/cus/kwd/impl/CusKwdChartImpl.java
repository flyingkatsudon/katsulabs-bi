package com.shinhan.biz.cus.kwd.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cboard.dto.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.shinhan.biz.cus.kwd.CusKwdChart;
import com.shinhan.dao.CusKwdChartDAO;
import com.shinhan.vo.DailyKwdTrendCntV2VO;
import com.shinhan.vo.ParamVO;

public class CusKwdChartImpl implements CusKwdChart {
	
	@Autowired
	private CusKwdChartDAO cusKwdChartDAO;

	// Bar 차트
	@Override
	public Map<String, Object> getKwd(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<Object> list = new ArrayList<Object>();
		
		switch(param.getPeriod()) {
		case "day":
			list = cusKwdChartDAO.getKwd(param);
			break;
		case "week":
			list = cusKwdChartDAO.getWeeklyKwd(param);
			break;
		case "mon":
			list = cusKwdChartDAO.getMonthlyKwd(param);
			break;
		case "quarter":
			list = cusKwdChartDAO.getQuarterlyKwd(param);
			break;
		case "year":
			list = cusKwdChartDAO.getYearlyKwd(param);
			break;
		default:
			list = cusKwdChartDAO.getKwd(param);
		}
		
		try {
			map.put("data", setKwd(list));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}

	// 주차별 키워드의 연관어 (다음레포트에서 가져옴)
	@Override
	public Map<String, Object> getKwdAssoV2(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<Object> list = new ArrayList<Object>();
		
		switch(param.getPeriod()) {
		case "day":
			break;
		case "week":
			list = cusKwdChartDAO.getWeeklyKwdAssoV2(param);
			break;
		case "mon":
			break;
		case "quarter":
			break;
		case "year":
			break;
		default:
			list = cusKwdChartDAO.getWeeklyKwdAssoV2(param);
		}
		
		list = cusKwdChartDAO.getWeeklyKwdAssoV2(param);
		
		try {
			map.put("data", setKwd(list));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	public ArrayList<Map<String, Object>> setKwd(ArrayList<Object> list) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		if(list != null && list.size() > 0) {
			
			for(Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if(vo.getDocDate() != null) map.put("docDate", vo.getDocDate());   
				if(vo.getKwdA() != null) map.put("kwdA", vo.getKwdA());
				if(vo.getKwdB() != null) map.put("kwdB", vo.getKwdB());
				
				map.put("docCntBoth", vo.getDocCntBoth());
				
				result.add(map);
			}
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> getUserInfo(HttpSession session) {
		User user = (User)session.getAttribute("user");
		System.out.println(user);
		Map<String, Object> userMap = new HashMap<String, Object>();
		return userMap;
	}
}

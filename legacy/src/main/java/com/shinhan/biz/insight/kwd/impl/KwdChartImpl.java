package com.shinhan.biz.insight.kwd.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.shinhan.biz.insight.kwd.KwdChart;
import com.shinhan.dao.KwdChartDAO;
import com.shinhan.vo.DailyKwdTrendCntV2VO;
import com.shinhan.vo.DocSentimentVO;
import com.shinhan.vo.ParamVO;

public class KwdChartImpl implements KwdChart {

	@Autowired
	protected KwdChartDAO kwdChartDAO;

	@Override
	public Map<String, Object> getKwdAsso(ParamVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>(); // 결과 map
		ArrayList<Object> list = new ArrayList<Object>(); // 쿼리 결과 list

		if (param.getCnt() == null || Integer.parseInt(param.getCnt()) < 10)
			param.setCnt("10");

		if (param.getKwd() != null) {
			ArrayList<String> kwdList = new ArrayList<String>(); // 키워드 리스트 String을 list로 구분하여 저장
			String[] arr = param.getKwd().split(",");
			
			for (int i = 0; i < arr.length; i++) {
				kwdList.add(arr[i].trim());
			}
			
			param.setKwdList(kwdList);
		}
		
		switch(param.getPeriod()) {
		case "day":
			list = kwdChartDAO.getKwdAsso(param);
			break;
		case "week":
			list = kwdChartDAO.getWeeklyKwdAsso(param);
			break;
		case "mon":
			list = kwdChartDAO.getMonthlyKwdAsso(param);
			break;
		case "quarter":
			list = kwdChartDAO.getQuarterlyKwdAsso(param);
			break;
		case "year":
			list = kwdChartDAO.getYearlyKwdAsso(param);
			break;
		default:
			list = kwdChartDAO.getKwdAsso(param);
		}

		try {
			map.put("data", setKwdAsso(list));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();

		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}

	// 기간별 연관어 차트 (주차별 조회만)
	@Override
	public Map<String, Object> getKwdAssoV2(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<Object> list = new ArrayList<Object>();
		/*
		switch(param.getPeriod()) {
		case "day":
			list = kwdChartDAO.getDailyKwdAssoV2(param);
			break;
		case "week":
			list = kwdChartDAO.getWeeklyKwdAssoV2(param);
			break;
		case "mon":
			list = kwdChartDAO.getMonthlyKwdAssoV2(param);
			break;
		case "quarter":
			list = kwdChartDAO.getQuarterlyKwdAssoV2(param);
			break;
		case "year":
			list = kwdChartDAO.getYearlyKwdAssoV2(param);
			break;
		default:
			list = kwdChartDAO.getWeeklyKwdAssoV2(param);
		}
		*/
		// 조회일 기준 -1달 간의 주별 조회만 가능하도록
		list = kwdChartDAO.getWeeklyKwdAssoV2(param);
		
		try {
			map.put("data", setKwdAsso(list));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();

		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	// 긍부정차트
	public Map<String, Object> getEmoAsso(ParamVO param){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		ArrayList<Object> list = new ArrayList<Object>();
		
		switch(param.getPeriod()) {
		case "day":
			//list = kwdChartDAO.getEmoAsso(param);
			list = kwdChartDAO.getEmoAssoV2(param);
			break;
		case "week":
			//list = kwdChartDAO.getWeeklyEmoAsso(param);
			list = kwdChartDAO.getWeeklyEmoAssoV2(param);
			break;
		case "mon":
			//list = kwdChartDAO.getMonthlyEmoAsso(param);
			list = kwdChartDAO.getMonthlyEmoAssoV2(param);
			break;
		case "quarter":
			//list = kwdChartDAO.getQuarterlyEmoAsso(param);
			list = kwdChartDAO.getQuarterlyEmoAssoV2(param);
			break;
		case "year":
			//list = kwdChartDAO.getYearlyEmoAsso(param);
			list = kwdChartDAO.getYearlyEmoAssoV2(param);
			break;
		default:
			//list = kwdChartDAO.getEmoAsso(param);
			list = kwdChartDAO.getEmoAssoV2(param);
		}
		
		try {
			map.put("data", setEmoAsso(list));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();

		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}
	
	@Override
	public Map<String, Object> getDisclosureInfo(ParamVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Object> list = new ArrayList<Object>();

		if (param.getCnt() == null) param.setCnt("10");
		
		list = kwdChartDAO.getDisclosureInfo(param);
		
		try {
			map.put("data", list);
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();

		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}

	@Override
	public Map<String, Object> getExpertOpinion(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map.put("data", kwdChartDAO.getExpertOpinion(param));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
	
		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}

	public ArrayList<Map<String, Object>> setKwdAsso(ArrayList<Object> list) {
		
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		if (list != null && list.size() > 0) {

			for (Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if (vo.getRn() != 0)
					map.put("rn", vo.getRn());
				if (vo.getDocDate() != null)
					map.put("docDate", vo.getDocDate());
				if (vo.getDocCntBoth() != 0)
					map.put("docCntBoth", vo.getDocCntBoth());
				if (vo.getKwdA() != null)
					map.put("kwdA", vo.getKwdA());
				if (vo.getKwdB() != null)
					map.put("kwdB", vo.getKwdB());
				if (vo.getChannel() != null)
					map.put("channel", vo.getChannel());
				if (vo.getSite() != null)
					map.put("site", vo.getSite().toLowerCase());

				result.add(map);
			}
		}

		return result;
	}
	
	public ArrayList<Map<String, Object>> setEmoAsso(ArrayList<Object> list) {
		
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		if (list != null && list.size() > 0) {
			
			for (Object o : list) {
				DocSentimentVO vo = (DocSentimentVO) o;
				Map<String, Object> map = new HashMap<String, Object>();
				
				if (vo.getKwdA() != null)
					map.put("kwdA", vo.getKwdA());
				if (vo.getKwdB() != null)
					map.put("kwdB", vo.getKwdB());
				if (vo.getDocDate() != null)
					map.put("docDate", vo.getDocDate());
				if (vo.getKwd() != null)
					map.put("kwd", vo.getKwd());
				if (vo.getSentiFlag() != null)
					map.put("sentiFlag", vo.getSentiFlag());
				
				//map.put("kwdSentiValue", (vo.getKwdSentiValue()*10000)/10000.0000);
				map.put("kwdSentiValue", vo.getKwdSentiValue());
	
				result.add(map);
			}
		}

		return result;
	}
}

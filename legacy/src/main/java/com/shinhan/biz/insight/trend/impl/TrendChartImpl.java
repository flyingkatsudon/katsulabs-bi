package com.shinhan.biz.insight.trend.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.shinhan.biz.insight.trend.TrendChart;
import com.shinhan.dao.TrendChartDAO;
import com.shinhan.vo.DailyKwdTrendCntV2VO;
import com.shinhan.vo.DailyStockDataVO;
import com.shinhan.vo.ParamVO;

public class TrendChartImpl implements TrendChart {

	private static final Logger logger = LoggerFactory.getLogger(TrendChartImpl.class);
	
	@Autowired
	private TrendChartDAO trendChartDAO;

	// 트렌드차트 new
	@Override
	public Map<String, Object> getKwdTrendV2(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>(); // 결과 map
		ArrayList<String> cnames = new ArrayList<String>(); // 키워드 리스트 String을 list로 구분하여 저장
		
		ArrayList<Object> list = new ArrayList<Object>(); // 쿼리 결과 list
		
		String bankList = "신한은행, 국민은행, 우리은행, 하나은행";
		//String bankList = "신한은행";
		String cardList = "신한카드, 삼성카드, 국민카드, 현대카드, 롯데카드, BC카드";
		String investList = "신한금융투자, 미래에셋대우, NH투자증권, 삼성증권, 한국투자증권, KB증권, 하나금융투자, 대신증권, 한화투자증권, 키움증권, DB금융투자, 유안타증권, 메리츠종금증권, 유진투자증권";
		
		String life = "신한생명";
		String lifeList = "한화생명, ABL생명, 삼성생명, 흥국생명, 교보생명, DGB생명, 미래에셋생명, KDB생명, DB생명, 동양생명, 메트라이프생명, 푸르덴셜생명, 처브라이프생명, 오렌지라이프생명, 하나생명, KB생명, BNP파리바카디프생명, 현대라이프생명, 라이나생명, AIA생명, IBK연금, NH농협생명, 교보라이프플래닛생명";
		String damageList = "메리츠화재, 한화손보, 롯데손보, MG손보, 흥국화재, 삼성화재, 현대해상, KB손보, DB손보, NH농협손보, AXA손보, 더케이손보, BNP파리바카디프손보, AIG손보, 에이스아메리칸화재";
		
		boolean isDiseaseRequest = param.getFid() != null && param.getFid().equals("BDPC04040503") && param.getKwdA() == null;
		boolean isDiseaseCustom  = param.getNerInfoA() == null;
		
		System.out.println("isDiseaseRequest : " + isDiseaseRequest);
		System.out.println("isDiseaseCustom : " + isDiseaseCustom);
		if (param.getBusinessCode() != null) {
			String companyList = null;
			
			// 그룹사코드별 경쟁사 설정
			switch (param.getBusinessCode().toUpperCase()) {
			case "SH":
				companyList = bankList;
				break;
			case "LC":
				companyList = cardList;
				break;
			case "GS":
				if(param.getFid().equals("BDPC04030204")) {
					companyList = "코스피, 코스닥";
					break;
				}
				companyList = investList;
				break;
			case "SL":
				// 0 or null or 기타: all, 1: 생명보험, 2: 손해보험 
				if(param.getFlag() != null) {
					switch(param.getFlag()) {
					case "0":
						companyList = life.concat(", " + lifeList).concat(", " + damageList);  
						break;
					case "1":
						companyList = life.concat(", " + lifeList);  
						break;
					case "2":
						companyList = life.concat(", " + damageList);  
						break;
					default:
						companyList = life.concat(", " + lifeList).concat(", " + damageList);  
					}
				} else {
					companyList = life.concat(", " + lifeList).concat(", " + damageList);  
				}
				
				
				// fid로 분기
				// 생명-상품브리핑
				/*if (param.getFid().equals("BDPC04040203")) {
					companyList = ""; 
				}
				*/
				// 생명-건강브리핑
				if (param.getFid().equals("BDPC04040403")) {
					//companyList = "비만, 다이어트, 임신, 출산, 육아, 감기, 독감, 탈모, 식중독, 영양, 운동, 야외활동, 환절기, 흡연, 음주";
					companyList = "건강";
				}
				
				// 생명-질병브리핑
				if (param.getFid().equals("BDPC04040503")) {
					//companyList = "심장병, 뇌혈관, 당뇨, 뇌졸중, 편도염, 루게릭, 신부전, 백내장, 녹내장, 유방암, 고액암";
					companyList = "질병";
					if (param.getKwdA() != null) companyList = param.getKwdA();
				}
				break;
			default:
				companyList = bankList;
			}
			
			// kwd를 리스트로 만들어줌
			String[] arr = companyList.split(",");

			for (int i = 0; i < arr.length; i++) {
				cnames.add(arr[i].trim());
			}
			
			param.setKwdList(cnames);
		}

		try {
			
			logger.debug("Select Query by Period : " + param.getPeriod());
			// 조회 단위 구분
			switch (param.getPeriod()) {
			case "day":
				//list = trendChartDAO.getKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getKwdTrendV3(param);
				break;
			case "week":
				//list = trendChartDAO.getWeeklyKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getWeeklyKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getWeeklyKwdTrendV3(param);
				break;
			case "mon":
				//list = trendChartDAO.getMonthlyKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getMonthlyKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getMonthlyKwdTrendV3(param);
				break;
			case "quarter":
				//list = trendChartDAO.getQuarterlyKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getQuarterlyKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getQuarterlyKwdTrendV3(param);
				break;
			case "year":
				//list = trendChartDAO.getYearlyKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getYearlyKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getYearlyKwdTrendV3(param);
				break;
			default:
				//list = trendChartDAO.getKwdTrendV2(param);
				if (isDiseaseRequest && !isDiseaseCustom)
					list = trendChartDAO.getKwdDiseaseTrend(param);
				else
					list = trendChartDAO.getKwdTrendV3(param);
			}

			setKwdTrendV2(map, list, cnames);
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	// 채널별확산 new
	@Override
	public Map<String, Object> getTrendV2(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<Object> list = new ArrayList<Object>();
		
		switch (param.getPeriod()) {
		case "day":
			if (param.getFid() != null && param.getFid().equals("BDPC04030205")) {
				list = trendChartDAO.getMarketTrend(param);
			} else {
				list = trendChartDAO.getTrendV2(param);
			}
			break;
		case "week":
			list = trendChartDAO.getTrendV2(param);
			break;
		case "mon":
			list = trendChartDAO.getTrendV2(param);
			break;
		case "quarter":
			list = trendChartDAO.getTrendV2(param);
			break;
		case "year":
			list = trendChartDAO.getTrendV2(param);
			break;
		case "time":
			if (param.getFid() != null && param.getFid().equals("BDPC04030205")) {
				list = trendChartDAO.getNrtMarketTrend(param);
			}
			break;
		default:
			list = trendChartDAO.getTrendV2(param);
		}
			
		try {
			if (param.getFid() != null && param.getFid().equals("BDPC04030205")) {
				String[] cnames = {"KOSPI", "KOSDAQ"};
				map.put("cnames", cnames);
				map.put("data", setMarketTrend(list));
			} else {
				String[] cnames = {"TWITTER", "INSTAGRAM", "COMMUNITY"};
				map.put("cnames", cnames);
				map.put("data", setTrend(list));
			}
			
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}
	
	// 종목브리핑 - 연관종목
	@Override
	public Map<String, Object> getRelStock(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Object> list = new ArrayList<Object>(); // 쿼리 결과 list
		ArrayList<Object> isuRelKwds = new ArrayList<Object>(); // 이슈 종목 연관키워드 리스트
		ArrayList<Object> stockKwds = new ArrayList<Object>(); // 이슈 종목 - 연관종목 3개 리스트
		
		ArrayList<String> cnames = new ArrayList<String>(); // 키워드 리스트 String을 list로 구분하여 저장
		ArrayList<String> fidList = param.getFidList();	
		
		cnames.add(param.getKwdA()); // 종목 브리핑 1위 키워드
		
		boolean isComplete = true;
		// 조회 단위 구분
		switch (param.getPeriod()) {
		case "day":
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					System.out.println("KEYWORD#1 : " + param.getKwdA());
					
					param.setKwdList(trendChartDAO.getRelStock(param));
					
					if (param.getKwdList() == null || param.getKwdList().size() == 0) {
						System.out.println("KEYWORD#1.5 : kwdList is NULL!!");
						isComplete = false;
						break;
					}
					
					System.out.println("KEYWORD#2 : " + param.getKwdList());
					stockKwds = trendChartDAO.getStockKwdTrendV3(param);
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getIsuRelKwds(param);
				}
			}
			break;
			
		case "week":
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					param.setKwdList(trendChartDAO.getWeeklyRelStock(param));
					stockKwds = trendChartDAO.getWeeklyStockKwdTrendV2(param);
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getWeeklyIsuRelKwds(param);
				}
			}
			break;
			
		case "mon":
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					param.setKwdList(trendChartDAO.getMonthlyRelStock(param));
					stockKwds = trendChartDAO.getMonthlyStockKwdTrendV2(param);
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getMonthlyIsuRelKwds(param);
				}
			}
			break;
		case "quarter":
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					param.setKwdList(trendChartDAO.getQuarterlyRelStock(param));
					stockKwds = trendChartDAO.getQuarterlyStockKwdTrendV2(param);
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getQuarterlyIsuRelKwds(param);
				}
			}
			break;
		case "year":
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					param.setKwdList(trendChartDAO.getYearlyRelStock(param));
					stockKwds = trendChartDAO.getYearlyStockKwdTrendV2(param);
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getYearlyIsuRelKwds(param);
				}
			}
			break;
		default:
			for (String fid : fidList) {
				if (fid.equals("BDPC04030305")) {
					param.setFid(fid);
					param.setKwdList(trendChartDAO.getRelStock(param));
					stockKwds = trendChartDAO.getStockKwdTrendV2(param);
					
				} else {
					param.setFid(fid); // 306
					isuRelKwds = trendChartDAO.getIsuRelKwds(param);
				}
			}
		}

		mergeList(list, isuRelKwds);
		mergeList(list, stockKwds);
		
		try {
			// cnames 저장
			
			if (!isComplete) {
				setKwdTrendV2(map, new ArrayList<Object>(), new ArrayList<String>());
				map.put("isSuccess", true);
				map.put("requestDate", new Timestamp(System.currentTimeMillis()));
				return map;
			}

			logger.info("{}", param.getKwdList());
			for (String stock : param.getKwdList()) {
				cnames.add(stock);
			}
			
			setKwdTrendV2(map, list, cnames);
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));
		
		return map;
	}
	
	public ArrayList<Object> mergeList(ArrayList<Object> list, ArrayList<Object> merge){

		if (merge != null && merge.size() > 0) {
			for (Object o : merge) {	
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				list.add(vo);
			}
		}
		
		return list;
	}
	
	// 트렌드차트
	public Map<String, Object> setKwdTrendV2(Map<String, Object> map, ArrayList<Object> list, ArrayList<String> cnames) {

		if(list != null && list.size() > 0) {
			
			int max = ((DailyKwdTrendCntV2VO) list.get(0)).getDocCntBoth();
			int min = ((DailyKwdTrendCntV2VO) list.get(0)).getDocCntBoth();
			
			Date date = ((DailyKwdTrendCntV2VO) list.get(list.size() - 1)).getDocDate();
	
			ArrayList<DailyKwdTrendCntV2VO> tmp = new ArrayList<DailyKwdTrendCntV2VO>(); // 쿼리 결과
	
			for (Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				
				// max, min
				if (vo.getDocCntBoth() >= max)
					max = vo.getDocCntBoth();
				if (vo.getDocCntBoth() <= min)
					min = vo.getDocCntBoth();
	
				// cname
				if (vo.getDocDate().compareTo(date) == 0) {
					if (vo.getRn() == 1)
						tmp.add(vo);
				}
			}
	
			map.put("data", setKwdTrend(list));
	
			map.put("max", max);
			map.put("min", min);
	
			// cname 추가
			int cnameMax = 0;
			if (tmp.size() > 0) cnameMax = tmp.get(0).getDocCntBoth();
			int loc = 0;
	
			for (int j = 0; j < tmp.size(); j++) {
				if (cnameMax <= tmp.get(j).getDocCntBoth()) {
					cnameMax = tmp.get(j).getDocCntBoth();
					loc = j;
				}
			}
			
			DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) list.get(0);
			map.put("cname", vo.getKwdA());
			if (tmp.size() > 0) map.put("cname", tmp.get(loc).getKwdA());
			
		} else {
			map.put("data", new ArrayList<DailyKwdTrendCntV2VO>());
	
			map.put("max", 0);
			map.put("min", 0);
			
			map.put("cname", null);
		}
		
		map.put("cnames", cnames);
		
		return map;
	}
	
	// 트렌드차트 데이터세팅
	public ArrayList<Map<String, Object>> setKwdTrend(ArrayList<Object> list) {
		
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (list != null && list.size() > 0) {
			
			for (Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if(vo.getJobId() != null)
					map.put("jobId", vo.getJobId());
				
				if(vo.getBusinessCode() != null)
					map.put("businessCode", vo.getBusinessCode());
				
				if(vo.getCategoryCode() != null)
					map.put("categoryCode", vo.getCategoryCode());
				
				if(vo.getcId() != null)
					map.put("cId", vo.getcId());
				
				if(vo.getDocDate() != null)
					map.put("docDate", vo.getDocDate());
				
				if(vo.getKwdA() != null)
					map.put("kwdA", vo.getKwdA());
				
				if(vo.getKwdB() != null)
					map.put("kwdB", vo.getKwdB());
				
				if(vo.getLoadDate() != null)
					map.put("loadDate", vo.getLoadDate());
				
				if(vo.getUpdateDate() != null)
					map.put("updateDate", vo.getUpdateDate());
				
				if(vo.getChannel() != null)
					map.put("channel", vo.getChannel());
				
				if(vo.getYear() != null)
					map.put("year", vo.getYear());
				
				if(vo.getQuarter() != null)
					map.put("quarter", vo.getQuarter());
				
				if(vo.getMonth() != null)
					map.put("month", vo.getMonth());
				
				if(vo.getWeek() != null)
					map.put("week", vo.getWeek());
				
				// 초기화된 수치들은 강제로 저장
				map.put("rn", vo.getRn());
				map.put("docCntA", vo.getDocCntA());
				map.put("docCntB", vo.getDocCntB());
				map.put("docCntBoth", vo.getDocCntBoth());
				map.put("posCntBoth", vo.getPosCntBoth());
				map.put("negCntBoth", vo.getNegCntBoth());
				map.put("neuCntBoth", vo.getNeuCntBoth());
				
				map.put("posPercent", vo.getPosPercent());
				map.put("negPercent", vo.getNegPercent());
				map.put("neuPercent", vo.getNeuPercent());
				result.add(map);
			}
		}
	
		return result;
	}

	// 채널별 확산 데이터세팅
	public ArrayList<Map<String, Object>> setTrend(ArrayList<Object> list) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (list != null && list.size() > 0) {
			
			for (Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if(vo.getDocDate() != null)
					map.put("docDate", vo.getDocDate());
				if(vo.getCategoryCode() != null)
					map.put("categoryCode", vo.getCategoryCode());
				if(vo.getcId() != null)
					map.put("cId", vo.getcId());
				if(vo.getChannel() != null)
					map.put("channel", vo.getChannel());
				
				map.put("docCntBoth", vo.getDocCntBoth());
				
				result.add(map);
			}
		}
		
		return result;
	}
	
	// 시장브리핑	
	public ArrayList<Map<String, Object>> setMarketTrend(ArrayList<Object> list) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (list != null && list.size() > 0) {
			
			// docdate, cid, channel, doccntboth
			for (Object o : list) {
				DailyStockDataVO vo = (DailyStockDataVO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				map.put("cId", vo.getIsuCd());
				map.put("channel", vo.getIsuNm());
				map.put("docDate", vo.getIsuCurDate());
				map.put("docCntBoth", vo.getCurPr());
				
				result.add(map);
			}
		}
		
		return result;
	}

	@Override
	public Map<String, Object> getStockInfo(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<Object> list = new ArrayList<Object>();
		
		switch (param.getPeriod()) {
		case "day":
			list = trendChartDAO.getStockInfo(param);
			break;
		case "week":
			list = trendChartDAO.getStockInfo(param);
			break;
		case "mon":
			list = trendChartDAO.getStockInfo(param);
			break;
		case "quarter":
			list = trendChartDAO.getStockInfo(param);
			break;
		case "year":
			list = trendChartDAO.getStockInfo(param);
			break;
		case "time":
			list = trendChartDAO.getNrtStockInfo(param);
			break;
		default:
			list = trendChartDAO.getStockInfo(param);
		}
			
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
}

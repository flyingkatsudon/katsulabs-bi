package com.shinhan.biz.insight.docs.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.owlnest.PR_BDA_SE_0005_02;
import com.shinhan.biz.insight.docs.DocTable;
import com.shinhan.dao.DocTableDAO;
import com.shinhan.vo.ParamVO;
import com.shinhan.vo.ScriptRelDocsStdInfoVO;

public class DocTableImpl implements DocTable {

	@Autowired
	private DocTableDAO docTableDAO;

	@Override
	public Map<String, Object> getAbsScript(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();
		
		// fid 파싱
		if(param.getFid() != null) {
			ArrayList<String> fidList = new ArrayList<String>(); // 키워드 리스트 String을 list로 구분하여 저장
			String[] arr = param.getFid().split(",");
	
			for (int i = 0; i < arr.length; i++) {
				fidList.add(arr[i].trim());
			}
	
			param.setFidList(fidList);
		}

		// top n이 없으면 기본값을 5로 할당
		if (param.getCnt() == null || param.getCnt().equals(""))
			param.setCnt("5");
		
		try {
			//map.put("data", setAbsScript(docTableDAO.getAbsScript(param)));
			map.put("data", setAbsScript(param));
			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	@Override
	public Map<String, Object> getFullSrc(ParamVO param) throws Throwable {

		//String currentSvr = "143.248.208.110"; // 대전
		String currentSvr = "13.209.126.208"; // public
		//String currentSvr = "10.10.30.27"; // private
		Integer currentPort = Integer.parseInt("40001");

		String output = "";

		if (param.getCnt() == null || Integer.parseInt(param.getCnt()) <= 10)
			param.setCnt("10");

		// And 조건만 검색 (2018.10.16 기준)
		if (param.getOperator() == null || param.getOperator().equals(""))
			param.setOperator("OR"); //param.setOperator("AND");
			

		if (param.getDomainOpt() == null || param.getDomainOpt().equals(""))
			param.setDomainOpt("01");

		switch (param.getDomainOpt()) {
		case "01":
			param.setDomainOpt("news");
			break;
		case "02":
			param.setDomainOpt("instagram");
			break;
		case "03":
			param.setDomainOpt("twitter");
			break;
		default:
			param.setDomainOpt("news");
		}

		int pageCnt = 20; // 페이지 당 갯수 설정
		int currentPage = -1; // 0이하면 전체 호출, 1 이상이면 각 페이지 별 갯수에 맞게 호출

		// 2018101 배포 버전에 신규 추가된 내용
		String andQuery = ""; // Operator가 AND일 경우, AND로 작동 (Query는 반드시 포함된 결과 도출)
		String orQuery = ""; // Operator가 OR일 경우, OR로 작동 (Query는 반드시 포함된 결과 도출)
		String notQuery = ""; // 제외 키워드
		
		//orQuery = param.getQuery();

		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			// 검색어나 script_uid 둘 다 이용해서 검색가능하도록 수정_2018.09.18
			Date startTime = new Date();
			// output = PR_BDA_SE_0005.PR_BDA_SE_0005(currentSvr, currentPort,
			// param.getStartDate(), param.getEndDate(), Operator, param.getQuery(),
			// limitCnt, pageCnt, currentPage, param.getDomainOpt()).trim();

			output = PR_BDA_SE_0005_02.PR_BDA_SE_0005_02(currentSvr, currentPort, param.getStartDate(),
					param.getEndDate(), param.getOperator(), param.getQuery(), andQuery, orQuery, notQuery,
					Integer.parseInt(param.getCnt()), pageCnt, currentPage, param.getDomainOpt());

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
				map.put("data", output);
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
	public Map<String, Object> getSmmryExtv(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (param.getCnt() == null)
			param.setCnt("10");
		if (param.getDate() == null) {
			param.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}

		try {

			if(param.getFid().equals("BDPC04030302")) map.put("data", setSmmryExtv(docTableDAO.getStockSmmryExtv(param)));
			else map.put("data", setSmmryExtv(docTableDAO.getSmmryExtv(param)));

			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	public ArrayList<Map<String, Object>> mergeList(ArrayList<Map<String, Object>> result, ArrayList<Object> list){

		if (list != null && list.size() > 0) {
			for (Object o : list) {	
				ScriptRelDocsStdInfoVO vo = (ScriptRelDocsStdInfoVO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if (vo.getCnt() != 0)
					map.put("cnt", vo.getCnt());
	
				if (!vo.getScriptUid().equals(""))
					map.put("scriptUid", vo.getScriptUid());
				
				if (!vo.getCategoryCode().equals(""))
					map.put("categoryCode", vo.getCategoryCode());
				
				if (!vo.getScriptTitle().equals(""))
					map.put("scriptTitle", vo.getScriptTitle());
				
				if (vo.getScriptWeight() != 0)
					map.put("scriptWeight", Math.round(vo.getScriptWeight() * 100.00) / 100.00);
				
				if (vo.getScriptRank() != 0)
					map.put("scriptRank", vo.getScriptRank());
				
				if (vo.getUpdateDate() != null)
					map.put("updateDate", vo.getUpdateDate());
				
				if (vo.getScriptDate() != null)
					map.put("scriptDate", vo.getScriptDate());
				
				if (vo.getCId() != null)
					map.put("cId", vo.getCId());
				
				if (vo.getScriptLabelL1() != null)
					map.put("scriptLabelL1", vo.getScriptLabelL1());

				if (vo.getScriptLabelL2() != null)
					map.put("scriptLabelL2", vo.getScriptLabelL2());
				
				if (vo.getScriptLabelL3() != null)
					map.put("scriptLabelL3", vo.getScriptLabelL3());
				
				if (vo.getScriptLabelL4() != null)
					map.put("scriptLabelL4", vo.getScriptLabelL4());
				
				result.add(map);
			}
		}
		
		return result;
	}
	
	public ArrayList<Map<String, Object>> setAbsScript(ParamVO param) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		// fid로 쿼리 구분
		for (String fid : param.getFidList()) {
					
			// 종목브리핑-Top5 이슈 종목
			if (fid.equals("BDPC04030301")) {
				param.setFid(fid);
				mergeList(result, docTableDAO.getIsuStock(param));
			} 
			
			// 일반
			else {
				param.setFid(fid);
				mergeList(result, docTableDAO.getAbsScript(param));
			}
		}
		
		return result;
	}
	
	public ArrayList<Map<String, Object>> setSmmryExtv(ArrayList<Object> list) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (list != null && list.size() > 0) {
			
			for (Object o : list) {	
				ScriptRelDocsStdInfoVO vo = (ScriptRelDocsStdInfoVO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if (!vo.getScriptUid().equals(""))
					map.put("scriptUid", vo.getScriptUid());
				
				if (vo.getScriptTitle() != null)
					map.put("scriptTitle", vo.getScriptTitle());
	
				if (vo.getRelDocUid() != null)
					map.put("relDocUid", vo.getRelDocUid());
				
				if (vo.getRelDocSummary() != null)
					map.put("relDocSummary", vo.getRelDocSummary());
				
				map.put("sumRate", vo.getSumRate() * 10000 / 10000.0000);

				result.add(map);
			}
		}

		return result;
	}
}

package com.shinhan.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinhan.vo.ParamVO;

public class SetData {
	
	private static final Logger logger = LoggerFactory.getLogger(SetData.class);

	public ParamVO setParam(ParamVO vo) {

		ParamVO param = new ParamVO();
		
		if (vo != null) {
			if (vo.getCnt() == null || vo.getCnt().equals("")) param.setCnt(null);
			else param.setCnt(vo.getCnt());
			
			if (vo.getDate() == null || vo.getDate().equals("")) param.setDate(null);
			else param.setDate(vo.getDate());
			
			if (vo.getStartDate() == null || vo.getStartDate().equals("")) param.setStartDate(null);
			else param.setStartDate(vo.getStartDate());
			
			if (vo.getEndDate() == null || vo.getEndDate().equals("")) param.setEndDate(null);
			else param.setEndDate(vo.getEndDate());
			
			if (vo.getKwd() == null || vo.getKwd().equals("")) param.setKwd(null);
			else param.setKwd(vo.getKwd().trim());

			if (vo.getKwdA() == null) param.setKwdA(null);
			else param.setKwdA(vo.getKwdA().trim());
			
			if (vo.getKwdB() == null) param.setKwdB(null);
			else param.setKwdB(vo.getKwdB().trim());
			
			if (vo.getQuery() == null || vo.getQuery().equals("")) param.setQuery(null);
			else param.setQuery(vo.getQuery());
			
			if (vo.getDomainOpt() == null || vo.getDomainOpt().equals("")) param.setDomainOpt(null);
			else param.setDomainOpt(vo.getDomainOpt());
			
			if (vo.getNerInfoA() == null || vo.getNerInfoA().equals("")) param.setNerInfoA(null);
			else param.setNerInfoA(vo.getNerInfoA());
			/** 
			 * startDate, endDate 중 어느 하나만 있으면: 동일한 날짜
			 * 아무 값도 없다면: 오늘 날짜
			*/
			String defDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String d  = param.getDate();
			String sd = param.getStartDate();
			String ed = param.getEndDate();
			
			if (d == null) {
				if (ed == null) {
					param.setDate(defDate);
					param.setEndDate(defDate);
				} else {
					param.setDate(ed);
				}
				d = param.getDate();
				ed = param.getEndDate();
			}
			
			if (sd != null && ed == null) param.setEndDate(sd);
			if (ed != null && sd == null) param.setStartDate(ed);

			if (vo.getBusinessCode() == null || vo.getBusinessCode().equals("")) param.setBusinessCode(null);
			else param.setBusinessCode(vo.getBusinessCode());
			
			// 조회 단위의 기본 값은 '일(00)'
			if (vo.getPeriod() == null || vo.getPeriod().equals("")) param.setPeriod("day");
			else param.setPeriod(vo.getPeriod());

			if (vo.getCategoryCode() == null || vo.getCategoryCode().equals("")) {
				param.setCategoryCode(null);
			} else {
				switch(vo.getCategoryCode()) {
				case "1":
					param.setCategoryCode("08201");
					break;
				case "2":
					param.setCategoryCode("08202");
					break;
				case "3":
					param.setCategoryCode("08203");
					break;
				case "4":
					param.setCategoryCode("08204");
					break;
				case "5":
					param.setCategoryCode("08205");
					break;
				default:
					param.setCategoryCode(null);
				}
			}
			
			if(vo.getScriptUid() == null || vo.getScriptUid().equals("")) {
				param.setScriptUid(null);
			} else {
				param.setScriptUid(vo.getScriptUid());
			}
			
			if (vo.getJobId() == null || vo.getJobId().equals("")) {
				param.setJobId(null);
			} else {
				param.setJobId(vo.getJobId());
			}
			
			if(vo.getFid() == null || vo.getFid().equals("")) {
				param.setFid(null);
			} else {
				param.setFid(vo.getFid());
			}
			
			if(vo.getFlag() == null || vo.getFlag().equals("")) {
				param.setFlag(null);
			} else {
				param.setFlag(vo.getFlag());
			}
	
			if(vo.getFidList() == null) {
				param.setFidList(null);
			} else {
				param.setFidList(vo.getFidList());
			}
		}
		
		logger.info("jobId: " + param.getJobId());
		logger.info("cnt: " + param.getCnt());
		logger.info("date: " + param.getDate());
		logger.info("startDate: " + param.getStartDate());
		logger.info("endDate: " + param.getEndDate());
		logger.info("kwd: " + param.getKwd());
		logger.info("kwdA: " + param.getKwdA());
		logger.info("query: " + param.getQuery());
		logger.info("domainOpt: " + param.getDomainOpt());
		logger.info("categoryCode: " + param.getCategoryCode());
		logger.info("businessCode: " + param.getBusinessCode());
		logger.info("flag: " + param.getFlag());
		logger.info("fid: " + param.getFid());
		logger.info("period:" + param.getPeriod());
		logger.info("nerInfoA: " + param.getNerInfoA());
		
		if (param.getFidList() != null)
			logger.info("fidList: " + param.getFidList().get(0) + "\n");

		return param;
	}
}

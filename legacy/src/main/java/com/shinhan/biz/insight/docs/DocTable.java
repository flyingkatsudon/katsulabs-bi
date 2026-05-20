package com.shinhan.biz.insight.docs;

import java.util.Map;

import com.shinhan.vo.ParamVO;

public interface DocTable {
	
	public Map<String, Object> getAbsScript(ParamVO param);

	public Map<String, Object> getFullSrc(ParamVO param) throws Throwable;

	public Map<String, Object> getSmmryExtv(ParamVO param);
	
}

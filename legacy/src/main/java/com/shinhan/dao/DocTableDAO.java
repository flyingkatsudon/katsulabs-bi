package com.shinhan.dao;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shinhan.vo.ParamVO;

@Repository
public interface DocTableDAO {
	
	ArrayList<Object> getAbsScript(@Param("param") ParamVO param);

	ArrayList<Object> getIsuStock(@Param("param") ParamVO param);
	
	ArrayList<Object> getSmmryExtv(@Param("param") ParamVO param);
	
	ArrayList<Object> getStockSmmryExtv(@Param("param") ParamVO param);
}

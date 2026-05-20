package org.cboard.services.role;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cboard.dto.DashboardMenu;
import org.cboard.dto.User;
import org.cboard.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yfyuan on 2016/12/21.
 */
@Repository
@Aspect
public class MenuRoleService {

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${admin_user_id}")
    private String adminUserId;

    @Around("execution(* org.cboard.services.MenuService.getMenuList(..))")
    public Object getMenuList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        
        User user = authenticationService.getCurrentUser();
        String roleId = user.getRoleId();
        
        int  rId = -99;
        boolean parsable = true;
        
        try {
            rId = Integer.parseInt(roleId);
        } catch (NumberFormatException e){
            parsable = false;
        }
        
        if (roleId == null || !parsable || (roleId.equals("1") || roleId.equals("2"))) {
        	return proceedingJoinPoint.proceed();
        }
        
        final ArrayList<Long> menuIdList = user.getResTypeList();
        List<DashboardMenu> list = (List<DashboardMenu>) proceedingJoinPoint.proceed();
        return new ArrayList<DashboardMenu>(Collections2.filter(list, new Predicate<DashboardMenu>() {
            @Override
            public boolean apply(@Nullable DashboardMenu dashboardMenu) {
            	
            	if(dashboardMenu.getPartition().equals("bdp") && menuIdList.contains(dashboardMenu.getMenuId())) {
            		return true;
            	} else if(dashboardMenu.getPartition().equals("cboard")) {
            		return false;
            	} else{
            		return false;
            	}
            }
        }));
    }
}

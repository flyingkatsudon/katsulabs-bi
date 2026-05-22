package org.cboard.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cboard.dto.User;
import org.cboard.services.SDIIUserService;
import org.cboard.util.CheckPwd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	@Autowired
	private SDIIUserService userService;
	
	@PostMapping(value = "/")
	public String login(HttpServletRequest request) {
		return "redirect:/login.jsp";
	}

	@PostMapping(value = {"/fail", "/process"})
	public ModelAndView failMsg(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mav = new ModelAndView();
		mav.addObject("msg", request.getAttribute("msg"));
		mav.setViewName("login.jsp");
		return mav;
	}
	
	@Value("${initPwd}")
	private String initPwd;

	@PostMapping(value = "/auth")
	public String auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Object o = request.getSession().getAttribute("isInitPwd");
		if (o == null) return "redirect:/logout";
		else return !((boolean) o) ? "redirect:/login.jsp" : "redirect:/changePwd.jsp"; 
	}

	@PostMapping(value = "/starter")
	public String starter() {
		return "cboard/starter.jsp";
	}
	
	@PostMapping(value = "/update")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request, @RequestBody User user) {
		
		Map<String, Object> map = new HashMap<String, Object>();

		// 영문 대소문자, 특수문자 포함 8~20자리
		Pattern pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[#$^+=!*()@%&]).{8,20}$");

        Matcher matcher = pattern.matcher(user.getUserPassword());

        User curUser = (User) request.getSession().getAttribute("user");
        User exUser = userService.getUser(user);

		String convPwd = CheckPwd.passwordEncoder.encodePassword(user.getUserPassword(), "").toUpperCase();
		String exPwd = exUser.getUserPassword();
		
		if (!curUser.getUserId().equals(user.getUserId())) {
			map.put("msg", "아이디 변경 감지");
			map.put("location", "logout");
			return map;
		} 
		
		if (!user.getUserPassword().equals(user.getCheckPassword())) {
			map.put("msg", "비밀번호가 일치하지 않습니다");
			map.put("location", null);
			return map;
		}

		if (!matcher.find()) {
			map.put("msg", "비밀번호는 8자리 이상, 대소문자, 특수문자를 포함하여 입력하세요");
			map.put("location", null);
			return map;
		} else if (convPwd.equals(initPwd)) {
			map.put("msg", "초기 비밀번호로 변경할 수 없습니다");
			map.put("location", null);
			return map;
		} else if (convPwd.equals(exPwd)) {
			map.put("msg", "이전 비밀번호로 변경할 수 없습니다");
			map.put("location", null);
			return map;
		}
        	
		try {
			request.setAttribute("changePwd", null);
			
    		user.setUserPassword(convPwd);
    		user.setOldUserPassword(exPwd);
    		
			userService.updatePwd(user);
			
			map.put("msg", "변경이 완료되었습니다");
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "관리자에게 문의하세요");
		}

		map.put("location", "logout");
		
		return map;
	}
}
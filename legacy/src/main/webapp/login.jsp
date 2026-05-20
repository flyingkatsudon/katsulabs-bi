<%@page import="org.cboard.dto.User"%>
<%@page import="org.cboard.pojo.DashboardRole"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Shinhan | BDP</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<link rel="stylesheet" href="cboard/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="cboard/css/font-awesome.min.css">
<link rel="stylesheet" href="cboard/css/ionicons.min.css">
<link rel="stylesheet" href="cboard/dist/css/AdminLTE.css">
<!-- iCheck -->
<link rel="stylesheet" href="cboard/plugins/iCheck/square/blue.css">
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<%
	User user = (User) request.getSession().getAttribute("user");
	String changePwd = (String) request.getSession().getAttribute("changePwd");

	if (user != null && user.getUserId() != null) {
		
		if (changePwd != null && changePwd.equals("changePwd")){
			request.getSession().invalidate();
			response.sendRedirect("/bdp/logout");
		} else {
			request.getSession().setAttribute("starter", "starter");
			request.getSession().setAttribute("changePwd", null);
			
			String businessCode = user.getBusinessCode();
		
			/* if(businessCode != null) {
				if(businessCode.equals("SY")) response.sendRedirect("report/sh.html");
				else response.sendRedirect("report/" + businessCode.toLowerCase() + ".html");
			} */
			response.sendRedirect("cboard/starter.jsp");
		}
	}
%>

<script type="text/javascript">

$(document).ready(function(){
	var msg = '<c:out value="${msg}"/>';
	
	if (msg != null && msg != "") {
		alert(msg);
		window.location.href="logout";
	}

	var businessCode = getCookie('businessCode');
	var codes = $('#codes').val().split(',');
	var names = $('#names').val().split(',');
	var options = '';
	
	$('#businessCode').change(function(){
		$('input[name=v1]').val('');
		$('input[name=v2]').val('');
		setCookie('businessCode', $(this).val(), 7); /* name=Ethan, 7일 뒤 만료됨 */
	});
	
	for(var i=0; i<codes.length; i++){
		if (codes[i] == businessCode)
			options += '<option value="' + codes[i] + '" selected>' + decodeURI(escape(names[i])) + '</option>';
		else
			options += '<option value="' + codes[i] + '">' + decodeURI(escape(names[i])) + '</option>';
	}
	
	$('#businessCode').html(options);

	function setCookie(cookie_name, value, days) {

		  var exdate = new Date();
		  exdate.setDate(exdate.getDate() + days * 60 * 60 * 24 * 1000);

		  var cookie_value = escape(value) + ((days == null) ? '' : ';    expires=' + exdate.toUTCString());
		  document.cookie = cookie_name + '=' + cookie_value;

	}

	function getCookie(cookie_name) {
	  var x, y;
	  var val = document.cookie.split(';');

	  for (var i = 0; i < val.length; i++) {
	    x = val[i].substr(0, val[i].indexOf('='));
	    y = val[i].substr(val[i].indexOf('=') + 1);
	    x = x.replace(/^\s+|\s+$/g, ''); // 앞과 뒤의 공백 제거하기

	    if (x == cookie_name) {
	      return unescape(y); // unescape로 디코딩 후 값 리턴

	    }

	  }
	}
});
</script>
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<input type="hidden" id="codes" value="<spring:eval expression="@properties['business.codes']"></spring:eval>"/>
		<input type="hidden" id="names" value="<spring:eval expression="@properties['business.names']"></spring:eval>"/>
		<div class="login-logo">
		<span><b>Shinhan</b></span>&nbsp;|&nbsp;<span>BDP</span>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body">
			<!-- <p class="login-box-msg">Sign in to start your session</p> -->
			<form action="/bdp/process" method="post">
				<div class="form-group has-feedback">
					<select id="businessCode" class="form-control" name="v0">
					</select>
				</div>
				<div class="form-group has-feedback">
					<input type="text" class="form-control" placeholder="사&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;번" name="v1"> 
					<span class="glyphicon glyphicon-user form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" class="form-control" placeholder="비밀번호" name="v2"> 
						<span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<input type="submit" class="btn btn-primary btn-block btn-flat"
							value="로그인" />
					</div>
					<!-- /.col -->
				</div>
			</form>
			<!-- /.col -->
		</div>
		<!-- /.login-box-body -->
	</div>
	<!-- /.login-box -->

	<!-- jQuery 2.2.3 -->
	<script src="cboard/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="cboard/bootstrap/js/bootstrap.min.js"></script>
	<!-- iCheck -->
	<script src="cboard/plugins/iCheck/icheck.min.js"></script>
	<script>
		$(function() {
			$('input').iCheck({
				checkboxClass : 'icheckbox_square-blue',
				radioClass : 'iradio_square-blue',
				increaseArea : '20%' // optional
			});
		});
	</script>
</body>
</html>

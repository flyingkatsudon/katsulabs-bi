<%@page import="org.cboard.dto.User"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cboard.pojo.DashboardRole"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
<script>

$(document).ready(function(){
	
	$('#mod').click(function(e){

		var param = {
				userId: $("input[name=userId]").val(),
				userPassword: $("input[name=userPassword]").val(),
				checkPassword: $("input[name=checkPassword]").val(),
				businessCode: $("input[name=businessCode]").val()
		}
		
		$.ajax({
			url: 'update',
			type : 'POST',
        	contentType: 'application/json; charset=utf-8',
			data : JSON.stringify(param),
			success: function(response){
				alert(response.msg);
				if (response.location == null) return false; 
				window.location.href = response.location;
			}, 
			error: function(response) {
				console.log(response);
			},
			complete: function() {
			}
		});
	});
});

</script>

</head>
<%
	request.getSession().setAttribute("changePwd", "changePwd");

	String msg = (String) request.getAttribute("msg");
	String starter = (String) request.getSession().getAttribute("starter");
	User user = (User) request.getSession().getAttribute("user");
	
	String userId = "";
	String userPassword = "";
	String businessCode = "";

	if (user == null) {
		request.getSession().invalidate();
		response.sendRedirect("/bdp/logout");
	} else {
		userId = user.getUserId();
		userPassword = user.getUserPassword();
		businessCode = user.getBusinessCode();
	}

	if (starter != null && starter.equals("starter")) {
		request.getSession().setAttribute("changePwd", null);
		response.sendRedirect("/bdp/login.jsp");
	}
	if (userId == null || userPassword == null) {
		request.getSession().invalidate();
		response.sendRedirect("/bdp/logout");
	}
%>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<% if (msg == null) { %>
			<span><b>Shinhan</b></span>&nbsp;|&nbsp;<span>BDP</span>
			<% } else { %>
			<span><b><%=msg%></b></span>
			<% } %>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body">
			<center>
				<span id="msg">초기 비밀번호는 반드시 변경이 필요합니다</span></p>
				<h6 style="color: red">* 8자리 이상, 대소문자, 특수문자를 포함하여 입력하세요</h6>
			</center>
			<div class="form-group has-feedback">
				<input type="text" class="form-control" name="userId" value="<%=userId %>" readOnly>
				<input type="hidden" name="businessCode" value="<%=businessCode %>"/>
				<span class="glyphicon glyphicon-user form-control-feedback"></span>
			</div>
			<div class="form-group has-feedback">
				<input type="password" class="form-control" placeholder="새 비밀번호" name="userPassword" autocomplete="new-password">
				<span class="glyphicon glyphicon-lock form-control-feedback"></span>
			</div>
			<div class="form-group has-feedback">
				<input type="password" class="form-control" placeholder="비밀번호 확인" name="checkPassword" autocomplete="new-password"> 
				<span class="glyphicon glyphicon-lock form-control-feedback"></span>
			</div>
			<div class="row">
				<!-- /.col -->
				<div class="col-xs-6">
					<input type="button" id="mod" class="btn btn-primary btn-block btn-flat" value="변경하기"/>
				</div>
				<div class="col-xs-6">
					<a href="/bdp/logout" class="btn btn-primary btn-block btn-flat">취소</a>
				</div>
				<!-- /.col -->
			</div>
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
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
  });
</script>
</body>
</html>

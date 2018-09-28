<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>

</head>
<body>
	
	<form action="${url}" method="post" >
      <input type="text" name="data" value="${data}" />
      <input type="text" name="signature" value="${signature}" />
      <input type="submit" name="" value="提交" />
</form>
</body>
</html>
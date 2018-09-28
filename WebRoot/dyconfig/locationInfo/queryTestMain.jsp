<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@ page import="com.godoing.rose.http.common.*"%>
<%@ page import="com.care.common.lang.*"%>
<%@ page import="com.care.common.config.Config"%>
<%@ page import="com.care.app.LoginUser"%>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic"%>
<%@ page autoFlush="true"%>
<%
	/*页面属性*/
	PagePys pys = (PagePys) request.getAttribute("PagePys");
	LoginUser loginUser = (LoginUser)request.getSession().getAttribute(Config.SystemConfig.LOGINUSER); 
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<title>无标题文档</title>
		<link href="<%=request.getContextPath()%>/css/tbls.css"
			rel="stylesheet" type="text/css">
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/public/public.js"></script>   <!-- 调用此方法 -->
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/public/My97DatePicker/WdatePicker.js"></script>
	</head>
	<script language="javascript">
function finds(){
   /*  var st = new Date(frmGo.startTime.value.replace(/-/g,'/'));
	var et = new Date(frmGo.endTime.value.replace(/-/g,'/'));
	if(Date.parse(st) - Date.parse(et)>0){
		alert("开始时间不能大于结束时间!");
		return false;
	} */
	   frmGo.submit();
}
function c(){
    document.all.imei.value="";
    document.all.sn.value="";
    document.all.version.value="";
   /*  document.all.endTime.value="";
    document.all.startTime1.value="";
    document.all.endTime1.value="";
    document.all.toUserName.value="";
    document.all.content.value="";
    document.all.fromUserName.value="";
    document.all.serieNo.value="";
    document.all.belongProject.value="";
    document.getElementById("statusSelect").options[0].selected = true; */
} 

</script>
	<body>
		<span class="title_1"></span>
		<form name="frmGo" method="post" action="doLocationInfo.do?method=queryTestInfo">
			<table width="100%" class="table" border=0 cellpadding="0" cellspacing="1">
               <tr>
                <th colspan="20" nowrap="nowrap" align="left">
               测试信息
                </th>
                </tr>
                 <tr class="title_3">
                       <td colspan="20">
						IMEI
						<input id="imei" name="imei" type="text" class="txt_1" 
						    value="<%CommUtils.printReqByAtt(request,response,"imei");%>" size="20">
						    	SN
						<input id="sn" name="sn" type="text" class="txt_1" 
						    value="<%CommUtils.printReqByAtt(request,response,"sn");%>" size="20">
						      	version
						<input id="version" name="version" type="text" class="txt_1" 
						    value="<%CommUtils.printReqByAtt(request,response,"version");%>" size="20">
					
					
						<input name="find1" type="button" class="but_1" accesskey="f"
							tabindex="f" value="搜 索" onclick="javascript:finds()">
					     <input name="clear" type="button" class="but_1" accesskey="c"
						    tabindex="c"  value="清除搜索" onclick="c()">
				</tr> 
				<%int i=1; %>
                  <tr class="title_2">                 	
                  	<td width="8%">IMEI</td>                                   	                 	
                  	<td width="6%">SN</td>                                   	                 	
                  	<td width="6%">LED</td>
                  	<td width="6%">MIC</td>
                  	<td width="6%">Bluetooth</td>
                  	<td width="6%">Wifi</td>
                  	<td width="6%">Phone</td>
                  	<td width="6%">Key</td>
                  	<td width="6%">Voltage</td>
                  	<td width="6%">Network</td>
                  	<td width="6%">RadioCalibration</td>
                  	<td width="6%">Charging</td>
                  	<td width="6%">Version</td>
                  	<td width="10%">UploadTime</td>
                  </tr>	
                  
                  <logic:iterate id="element" name="pageList">
					<tr class="tr_5" onmouseover='this.className="tr_4"' onmouseout='this.className="tr_5"' >						
							
							<td><bean:write name="element" property="imei"/></td>
							<td><bean:write name="element" property="sn" /></td>
							<td><bean:write name="element" property="led" /></td>
							<td><bean:write name="element" property="mic" /></td>
							<td><bean:write name="element" property="bluetooth" /></td>
							<td><bean:write name="element" property="wifi" /></td>
							<td><bean:write name="element" property="phone" /></td>
							<td><bean:write name="element" property="keyinfo" /></td>
							<td><bean:write name="element" property="voltage" /></td>
							<td><bean:write name="element" property="network" /></td>
							<td><bean:write name="element" property="radio_calibration" /></td>
							<td><bean:write name="element" property="charging" /></td>
							<td><bean:write name="element" property="version" /></td>
							<td><bean:write name="element" property="upload_time" /></td>
							
					</tr>
				</logic:iterate>
				
				<tr class="title_3">
					<td colspan="20" align="left" >  
						<%
							pys.printGoPage(response, "frmGo");
						%>
					</td>
				</tr> 
			</table>
		</form>
	</body>
</html>
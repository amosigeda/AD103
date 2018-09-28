package com.care.sys.interfaces;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.care.common.config.ServiceBean;
import com.care.common.http.BaseAction;
import com.care.common.lang.Constant;
import com.care.sys.deviceactiveinfo.domain.DeviceActiveInfo;
import com.care.sys.directiveinfo.domain.DirectiveInfo;
import com.care.sys.directiveinfo.domain.logic.DirectiveInfoFacade;
import com.care.sys.msginfo.domain.MsgInfo;
import com.godoing.rose.lang.DataMap;
import com.godoing.rose.log.LogFactory;

public class GjservicesService extends BaseAction{

	Log logger = LogFactory.getLog(GjservicesService.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DirectiveInfoFacade facade = ServiceBean.getInstance().getDirectiveInfoFacade();
		String cc="\"display:none\"";
		String ress="<html><head><body><div style="+cc+">###0-0-0-0###</div></body></head></html>";
		String bb="<html><head><body><div style="+cc+">###no###</div></body></head></html>"; 
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			DirectiveInfo vo = new DirectiveInfo();
			String imsi = request.getParameter("imsi");
			String resource = request.getParameter("resource");
			System.out.println("解析----------------------------------------------------------------------");
			logger.info("imsi="+imsi+"-"+"resource="+resource);
			vo.setCondition("imsi='"+imsi+"' limit 1");
			List<DataMap> list = facade.getImsiAndResourceInfo(vo);
			if(list.size()<=0){
				ress=bb;
			}
				vo.setAlarmChange(imsi);
				vo.setBelongProject(resource);
                vo.setClock(formatter.format(new Date()));
                facade.insertLog(vo);
			 
		}catch(Exception e){
			e.printStackTrace();	
			StringBuffer sb = new StringBuffer();
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			Throwable cause = e.getCause();		
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String resultSb = writer.toString();
			sb.append(resultSb);
			
			logger.error(e);
			result = Constant.EXCEPTION_CODE;
		}
		response.setCharacterEncoding("UTF-8");	
        
		response.getWriter().write(ress);
		return null;
	}
}

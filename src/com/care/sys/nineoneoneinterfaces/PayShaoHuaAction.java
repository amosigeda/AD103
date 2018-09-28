package com.care.sys.nineoneoneinterfaces;

/**
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.care.common.config.ServiceBean;
import com.care.common.http.BaseAction;
import com.care.common.lang.Constant;
import com.care.sys.funcinfo.domain.FuncInfo;
import com.care.sys.funcinfo.domain.logic.FuncInfoFacade;
import com.godoing.rose.log.LogFactory;

public class PayShaoHuaAction extends BaseAction {
	Log logger = LogFactory.getLog(PayShaoHuaAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		 request.setCharacterEncoding("UTF-8");
	  
		ServletInputStream input = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuffer sb = new StringBuffer();
		String online = "";
		while ((online = reader.readLine()) != null) {
			sb.append(online);
		}


		String[] aa = sb.toString().split("&");

		String orderAmount = aa[0].split("=")[1];// 金额
		String orderDate = aa[1].split("=")[1];// 订单日期
	
		String signType = "MD5";// 加密方式
		String prdOrdNo = aa[1].split("=")[1];
	

		

		String orderNo = aa[1].split("=")[1];// 订单编号
		String merchParam = aa[2].split("=")[1];
		String tradeSummary = aa[3].split("=")[1];
		String choosePayType = aa[4].split("=")[1];// 支付类型
		String tradeDate = aa[5].split("=")[1];// 交易日期yyyyMMdd
		String bankCode = aa[6].split("=")[1];// 银行编号

		SortedMap<String, String> map = new TreeMap<String, String>();
		map.put("accNoType", "A");
		map.put("accountType", "0");
		map.put("asynNotifyUrl", "http://localhost:8090/merchant_order_demo/decryptVerifyResultServlet");
		map.put("currency", "RMB");
		map.put("merId", "100520637");
		
		double money =Double.valueOf(orderAmount);
		money=money*100;
		 long lnum = Math.round(money);
		 
		 if((int)lnum>200){
			 map.put("orderAmount", lnum+"");
		 }else{
			 map.put("orderAmount", "200");
		 }
		
/*
		pnum=1&prdAmt=1&prdDesc=充值卡&prdDisUrl=http://www.icardpay.com&prdName=100元移动充值卡&
		 prdOrdNo=02262534155600&prdShortName=充值卡&receivableType=T01&signType=MD5&synNotifyUrl=https://123.sogou.com/&
		 tranChannel=308&transType=008&versionId=1.0&key=EYl7GuhFSGGp
		 */
		map.put("orderDate", orderDate);
		map.put("payMode", "00020");
		map.put("pnum", "1");
		map.put("prdDesc", "1");
		map.put("prdName", "abc");
		map.put("prdOrdNo", prdOrdNo);
		map.put("receivableType", "T01");
		map.put("signType", "MD5");
//		map.put("synNotifyUrl", "https://123.sogou.com/");http://www.kaiweiforex.com/pay/PaySuccess.html 
		map.put("synNotifyUrl", "http://www.kaiweiforex.com/pay/PaySuccess.html"); 

		if("ICBC".equals(bankCode)){
			map.put("tranChannel", "102");
		}else if("ABC".equals(bankCode)){
			map.put("tranChannel", "103");
		}else if("CCB".equals(bankCode)){
			map.put("tranChannel", "105");
		}else if("CEBB".equals(bankCode)){
			map.put("tranChannel", "303");
		}else if("GDB".equals(bankCode)){
			map.put("tranChannel", "306");
		}else if("SPABANK".equals(bankCode)){
			map.put("tranChannel", "307");
		}else if("CMB".equals(bankCode)){
			map.put("tranChannel", "308");
		}else if("CIB".equals(bankCode)){
			map.put("tranChannel", "309");
		}else if("SPDB".equals(bankCode)){
			map.put("tranChannel", "310");
		}else if("PSBC".equals(bankCode)){
			map.put("tranChannel", "403");
		}else if("BOC".equals(bankCode)){
			map.put("tranChannel", "104");
		}else if("BCOM".equals(bankCode)){
			map.put("tranChannel", "301");
		}else if("ECITIC".equals(bankCode)){
			map.put("tranChannel", "302");
		}else if("HXB".equals(bankCode)){
			map.put("tranChannel", "304");
		}else if("CMBC".equals(bankCode)){
			map.put("tranChannel", "305");
		}else if("BOB".equals(bankCode)){
			map.put("tranChannel", "313");
		}else if("SHB".equals(bankCode)){
			map.put("tranChannel", "325");
		}
		
		map.put("transType", "008");
		map.put("versionId", "1.0");
		String sign = createSign(map, "EYl7GuhFSGGp");
		map.put("signData", sign);
	
		
		FuncInfoFacade info = ServiceBean.getInstance().getFuncInfoFacade();
		FuncInfo vo = new FuncInfo();
		try {
			System.out.println("amt=" + orderAmount + "-orderNo=" + orderNo
					+ "-merchParam=" + merchParam + "-tradeSummary="
					+ tradeSummary + "-choosePayType=" + choosePayType
					+ "-tradeDate=" + tradeDate + "-bankCode=" + bankCode
					+ "-signType=" + signType);

			String a = "amt=" + orderAmount + "-orderNo=" + orderNo
					+ "-merchParam=" + merchParam + "-tradeSummary="
					+ tradeSummary + "-choosePayType=" + choosePayType
					+ "-tradeDate=" + tradeDate + "-bankCode=" + bankCode
					+ "-signType=" + signType+"--"+prdOrdNo;
			vo.setFuncDesc(a);
			vo.setFuncCode(new Date() + "");

		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer sb1 = new StringBuffer();
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			Throwable cause = e.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String resultSb = writer.toString();
			sb1.append(resultSb);
			logger.error(e);
			result = Constant.EXCEPTION_CODE;
		}
		// json.put(Constant.RESULTCODE, result);
		// json.put("request", href);

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.getWriter()
				.append(buildRequest(
						"http://online.atrustpay.com/payment/PayApply.do", map))
				.close();
		return null;
	}

	public static String buildRequest(String actionUrl,
			SortedMap<String, String> paramMap) {
		StringBuilder html = new StringBuilder();
		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"")
				.append(actionUrl).append("\" method=\"post\">\n");
		for (String key : paramMap.keySet()) {
			if (paramMap.get(key) != null)
				html.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + paramMap.get(key) + "\">\n");
		}
		html.append("</form>\n");
		return html.toString();
	}
	
	public static String buildRequestPost(String actionUrl,String data,String signature) {
		StringBuilder html = new StringBuilder();
		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"")
				.append(actionUrl).append("\" method=\"post\">\n");
				html.append("<input type=\"text\" data=\"" + data + "\" id=\""
						+ data + "\" value=\"" + data + "\">\n");
				html.append("<input type=\"text\" signature=\"" + signature + "\" id=\""
						+ signature + "\" value=\"" + signature + "\">\n");
		html.append("</form>\n");
		return html.toString();
	}

	@SuppressWarnings("rawtypes")
	private String createSign(SortedMap<String, String> packageParams,
			String partnerKey) {

		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (StringUtils.isNotEmpty(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + partnerKey);
		String sign = null;
		try {
			sign = DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"))
					.toUpperCase();
			System.out.println("签名原串:" + sb.toString() + " ；签名结果：" + sign);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}
	
	
	@SuppressWarnings("unused")
	private static String getItemID( int n )
    {
        String val = "";
        Random random = new Random();
        for ( int i = 0; i < n; i++ )
        {
            String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
            if ( "char".equalsIgnoreCase( str ) )
            { // 产生字母
                int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
                // System.out.println(nextInt + "!!!!"); 1,0,1,1,1,0,0
                val += (char) ( nextInt + random.nextInt( 26 ) );
            }
            else if ( "num".equalsIgnoreCase( str ) )
            { // 产生数字
                val += String.valueOf( random.nextInt( 10 ) );
            }
        }
        return val;
    }
	
	public static boolean isNumber2(String str) {// 判断小数，与判断整型的区别在与d后面的小数点（红色）  
	    return str.matches("\\d+\\.\\d+$");  
	}  
	

}

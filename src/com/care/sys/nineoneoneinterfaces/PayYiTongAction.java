package com.care.sys.nineoneoneinterfaces;

/**
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
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

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.care.common.config.ServiceBean;
import com.care.common.http.BaseAction;
import com.care.common.lang.Constant;
import com.care.sys.LocRadiusPoiResponse.domain.RemoteUtil;
import com.care.sys.funcinfo.domain.FuncInfo;
import com.care.sys.funcinfo.domain.logic.FuncInfoFacade;
import com.care.utils.Base64Utils;
import com.care.utils.MyURLConnection;
import com.care.utils.RSAUtil;
import com.godoing.rose.log.LogFactory;

public class PayYiTongAction extends BaseAction {
	Log logger = LogFactory.getLog(PayYiTongAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		 request.setCharacterEncoding("UTF-8");
		 JSONObject json = new JSONObject();
		 JSONObject json1 = new JSONObject();
			String href = request.getServletPath();
		ServletInputStream input = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuffer sb = new StringBuffer();
		String online = "";
		while ((online = reader.readLine()) != null) {
			sb.append(online);
		}

       
		String[] aa = sb.toString().split("&");

		String orderAmount = aa[0].split("=")[1];// 金额
		
		String prdOrdNo = aa[1].split("=")[1]+getItemID(10);
	
		
		String bankCode = aa[6].split("=")[1];// 银行编号

	
	System.out.println("银行编号bankCode="+bankCode+"-金额="+orderAmount);
		
		double money =Double.valueOf(orderAmount);
		money=money*100;
		 long lnum = Math.round(money);
		 String moneyy="500";
		 json1.put("account", "DE18971350870");
		 if((int)lnum>500){
			 json1.put("amount",  lnum+"");
			 moneyy= lnum+"";
		 }else{
			 json1.put("amount",  "500");
			 moneyy= "500";
		 }
		 
		

		 String banktype="";
		if("ICBC".equals(bankCode)){
			//map.put("tranChannel", "102");
			json1.put("banktype",  "01000017");
			banktype="01000017";
		}else if("ABC".equals(bankCode)){
			//map.put("tranChannel", "103");
			json1.put("banktype",  "01000001");
			banktype="01000001";
		}else if("CCB".equals(bankCode)){
			//map.put("tranChannel", "105");
			json1.put("banktype",  "01050000");
			banktype="01050000";
		}else if("CEBB".equals(bankCode)){
			//map.put("tranChannel", "303");
			json1.put("banktype",  "01000005");
			banktype="01000005";
		}else if("GDB".equals(bankCode)){
			//map.put("tranChannel", "306");
			json1.put("banktype",  "01000008");
			banktype="01000008";
		}else if("SPABANK".equals(bankCode)){
			//map.put("tranChannel", "307");
			json1.put("banktype",  "01000009");
			banktype="01000009";
		}else if("CMB".equals(bankCode)){
			//map.put("tranChannel", "308");
			json1.put("banktype",  "01000010");
			banktype="01000010";
		}else if("CIB".equals(bankCode)){
		//	map.put("tranChannel", "309");
			json1.put("banktype",  "01000011");
			banktype="01000011";
		}else if("SPDB".equals(bankCode)){
		//	map.put("tranChannel", "310");
			json1.put("banktype",  "01000012");
			banktype="01000012";
		}else if("PSBC".equals(bankCode)){
			//map.put("tranChannel", "403");
			json1.put("banktype",  "01000000");
			banktype="01000000";
		}else if("BOC".equals(bankCode)){
			//map.put("tranChannel", "104");
			json1.put("banktype",  "01000002");
			banktype="01000002";
		}else if("BCOM".equals(bankCode)){
			//map.put("tranChannel", "301");
			json1.put("banktype",  "01000003");
			banktype="01000003";
		}else if("ECITIC".equals(bankCode)){
			//map.put("tranChannel", "302");
			json1.put("banktype",  "01000004");
			banktype="01000004";
		}else if("HXB".equals(bankCode)){
		//	map.put("tranChannel", "304");
			json1.put("banktype",  "01000006");
			banktype="01000006";
		}else if("CMBC".equals(bankCode)){
			//map.put("tranChannel", "305");
			json1.put("banktype",  "01000007");
			banktype="01000007";
		}
		json1.put("banktype",  "01000007");
		json1.put("callback_url",  URLEncoder.encode("http://koow763614984.6655.la/HBKPayDemo","utf-8"));
		json1.put("notify_url",  URLEncoder.encode("http://koow763614984.6655.la/HBKPayDemo/H5Asyn","utf-8"));
		json1.put("orderId",  prdOrdNo);
		json1.put("type",  "kozl");
		
		String callback_url="http://www.hao123.com";
		String notify_url="http://www.hao123.com";
		String userid="DE18971350870";
		  String type="kozl"; //请求类型  kozl为直连网银
		  String orderId="hbpay"+System.currentTimeMillis()+new Random().nextInt(999999); //交易订单号
		String ming = "{\"account\":\""+userid+"\","
				+ "\"amount\":\""+moneyy+"\"," + "\"banktype\":\""+banktype+"\"," + "\"callback_url\":\""+URLEncoder.encode(callback_url,"UTF-8")+"\","
				+ "\"notify_url\":\""+URLEncoder.encode(notify_url,"UTF-8")+"\"," + "\"orderId\":\""+orderId+"\","
				+ "\"type\":\""+type+"\"" + "}";
		System.out.println("明文="+ming);
		RSAPublicKey publicKey = (RSAPublicKey) RSAUtil
				.loadPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1SoZsImxXPzuTMJmip0hnUEvIFIjS/2mltvXBmjtIsh7kyE9S++xWHwwi/n+d/cQbenxmUpX2vAd2h44KjTBZ+6kM0jB0aR4oFQeKzZWUlY79PhYqhBsaNRu8RfPb5jwccjv9s+WZbO0LFO1AN6We6mfpEGu/PKjq6YgW8oNwAYhLMzBF73xGpoNFVtggJLjmCzqSmeuqANC4jOyRBb65MpAoNDSHCCIKHXUyoV0wxnu/nOPdh1epqQe08aD7z1N1mV27hZvN0JC5EsRFvODP2sIZA7TVN4UccJstDXzYBpVd/x2QzvzKNCOyBjaSkESI5BqgcO4H81SvOCkdKql1QIDAQAB");//需使用的通道公钥
		
		
		RSAPrivateKey privateKey=(RSAPrivateKey) RSAUtil.loadPrivateKey(
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuge8St4wU4aQh"
+"14xRbPaO0Iye7P8ixKEIrbgGKbcZOB1QIq0YVFfItwSvIp12z+cFo4qybFeAhZbL"
+"WtqFo+wOUEF/8OXq8UQVNTK4nT94mrnaIIMExxY/HHD69dbcwvFYvFJe3a87YbyI"
+"TZjT+p2Rs6DBAu1MZTPViqARZl8qOM1UdsO2AWJUlZGCbnxTnCNxycvM8w8iXFdE"
+"P3kv4GlAnwkG0BnkExgcZi0OyGQ2lZSFZ51Lnc890V2MG9Lby4t7YuUtPJdMk70o"
+"9JFqEoWU45tsUya+r9QP9srLSiHcfU5bqyG63itsdgjCVaSAioBX7oiLv3eybM30"
+"3l7hbt2zAgMBAAECggEAeZPm1xhvjODDdBxzEwuiH0+mmNauzi9FrgExs4F+oiBZ"
+"M8pk5A1m91yi881e/TbNUj1lVPdsYwsvStAqbG8O983sRFG2LWAXudrNUpejb8gE"
+"cbay8TIvCpsrD30VS8q5UMQrcxJpkip+qku9jPoOq0MFXVNHtjDP0xd4e4biCExR"
+"0IQrVRAgYxnO1nKeJXhIXzf+i5ZKAi3gfsJRq7yjOIcOcP03SkLe3s09Ivq42kTE"
+"CzeFTiat/s9qpB6GbRVU/9kZ1qGFyrzjCBPcLd1MlQ/T9ltLySw5Fynf428jj8nD"
+"m6R4iea8bAKof6HQPFmUpNVQrjl0GpH/wGsN0DpNuQKBgQDdrv/cVWwJ6CLTyycr"
+"fDGzLl7uyunfryC2ZNG9Bq239DNkS6Xm9AC9US0SpzosBXgeok1b+TiVaTp3fJBi"
+"7WP+IQxslq6FA8zD6Ak97pKYGDr6gj1dEH6T4M/6xzIUsvKgzeiIjJwfCs67qqx8"
+"pOwrC6Ajwv6W6K+9F8QuUmtebQKBgQDJhWqMHUnJMQg0/M+yiJUAH/sD1Ob4LQUU"
+"9SUOuHA13UO2UFAPXC5FJG0j2FkS4mdT/RY6AFFdqR4mBRZMHJsRGZnwbz5p3Fl5"
+"eaqEERSRCOM5kyUZYNGaaXTrmvBJxDEUUXKTzqwMYJUJeRf5dfrUCuohXu+XiQqT"
+"63qrfFkYnwKBgQC1p3xESb9RUuaPFO7RBGEZigsCtEX3JOOycmUn9zwM/E/MhOy6"
+"lwGwIL/FhED357dpquenUU6bqHHfjbQnTI3epagvpz31A4sckZxZIpD/jwNtWaH9"
+"C9IdTWBACFCyg3ysZm6Azm/0Q//p6k6TiPV5xHoCM/w5Hw067ym0J5eR9QKBgA+G"
+"eMvsobefUGuMoo+1mA0XaaZE67po9YBE1qMIsJRlxIayL0jPqDzpboMZcttSl/VP"
+"hTvYtGwL0GfLcolQlt29ZC+y0lMjh70lE27zRetajO7tmCkZXWyQ/VH9cM+uDr3q"
+"/HHItVaKxlChhKhSX42gfaMuhjTdePQmax/1B4QvAoGABqOH/D3AH1QehQUseRTr"
+"5MWlsu2ytTvVLlDJIHyezJDRWG27HmbnUoQvYsEF/Xbv59YYBPAsTm2kYxBKVuiK"
+"gBPQGprQWozpekHGS50UkmREoixABroD7IzGR3dpc6HgWYbq3dOBQbcgPDHGsN+Y"
+"h4t9Ndo+5taPdw8yEAtrfj8=");
		
		//String data = json1.toString();
		//System.out.println("data="+data);
		byte[] data1=RSAUtil.encryptData((ming).getBytes(), publicKey);	
		byte[] signature=RSAUtil.privatesign(ming,privateKey);
		
		
		
		String dataA=Base64Utils.encode(data1);
		String dataB=Base64Utils.encode(signature);
	
		try {
			
			System.out.println("data="+dataA);
			System.out.println("signature="+dataB);
			result = 1;
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
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.getWriter()
		.append(buildRequestPost(
				"http://www.ddcoinspay.com/Platform/pay/ZLPay.do", dataA, dataB))
		.close();
		
		
	/*	request.setAttribute("signature", Base64Utils.encode(signature));
		request.setAttribute("data",Base64Utils.encode(data1));
		System.out.println(request.getAttribute("signature"));
		System.out.println(request.getAttribute("data"));
		request.setAttribute("url", "http://www.ddcoinspay.com/Platform/pay/ZLPay.do");
		request.getRequestDispatcher("H5KOnline.jsp").forward(request, response);
		
	//	json.put(Constant.RESULTCODE, result);
		//json.put("request", href);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json.toString());*/
		return null;
	}


	
	
	public static String buildRequestPost(String actionUrl,String data,String signature) {
		StringBuilder html = new StringBuilder();
		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"")
		.append(actionUrl).append("\" method=\"post\">\n");
				html.append("<input type=\"text\" name=\"" + "data" + "\"  value=\"" + data + "\">\n");
				
				html.append("<input type=\"text\" name=\"" + "signature" + "\"  value=\"" + signature + "\">\n");
		html.append("</form>\n");
		return html.toString();
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

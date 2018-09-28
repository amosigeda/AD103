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

public class PayabcAction extends BaseAction {
	Log logger = LogFactory.getLog(PayabcAction.class);

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

		// EYI7GuhFSGGp 秘钥

		String[] aa = sb.toString().split("&");

		// String versionId = "1.0";//
		String orderAmount = aa[0].split("=")[1];// 金额
		String orderDate = aa[1].split("=")[1];// 订单日期
		// String currency = "RMB";
		// String accNoType="A";
		// String accountType = "0";
		// String transType = "008";
		String asynNotifyUrl = "http://localhost:8090/merchant_order_demo/decryptVerifyResultServlet";
		String synNofifyUrl = "http://m.test.foodmall.com";
		String signType = "MD5";// 加密方式
		// String merId = "100520637";
		String prdOrdNo = aa[1].split("=")[1];
		// String payMode = "00020";
		// String tranChannel = "";// 银行编码
		// String receivableType = "DOO";
		// String prdName = "abc";
		// String prdDesc = "test";
		// String pnum = "1";

		/*
		 * accountType=0& asynNotifyUrl=http://ipgsit& currency=RMB&
		 * merId=100519132& orderAmount=10300& orderDate=20170925035916&
		 * payMode=00020& pnum=1& prdAmt=10300& prdDesc=abc& prdName=abc&
		 * prdOrdNo=3941193333& receivableType=D00& signType=MD5&
		 * synNotifyUrl=http://pg47sit.jcjsf.top/pg47/rs.html& tranChannel=308&
		 * transType=008& versionId=1.0& key=3au7WQpaWBia
		 */

		String orderNo = aa[1].split("=")[1];// 订单编号
		String merchParam = aa[2].split("=")[1];
		String tradeSummary = aa[3].split("=")[1];
		String choosePayType = aa[4].split("=")[1];// 支付类型
		String tradeDate = aa[5].split("=")[1];// 交易日期yyyyMMdd
		String bankCode = aa[6].split("=")[1];// 银行编号

		SortedMap<String, String> map = new TreeMap<String, String>();
		map.put("accountType", "0");
		map.put("asynNotifyUrl", asynNotifyUrl);
		map.put("currency", "RMB");
		map.put("merId", "100519132");
		map.put("orderAmount", orderAmount);
		map.put("orderDate", orderDate);
		map.put("payMode", "00020");
		map.put("pnum", "1");
		map.put("prdDesc", "1");
		map.put("prdName", "abc");
		map.put("prdOrdNo", prdOrdNo);
		map.put("receivableType", "D00");
		map.put("signType", "MD5");
		map.put("synNotifyUrl", synNofifyUrl);
		map.put("tranChannel", "103");
		map.put("transType", "008");
		map.put("versionId", "1.0");
		map.put("key", "EYI7GuhFSGGp");

		String sign = createSign(map, "EYI7GuhFSGGp");
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
					+ "-signType=" + signType;
			vo.setFuncDesc(a);
			vo.setFuncCode(new Date() + "");
			info.insertPayForInfo(vo);

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
	/*
	 * public static void main(String[] args) { List<String> words = new
	 * ArrayList<String>(); words.add("ABC"); words.add("dog");
	 * words.add("address"); words.add("Bananer"); Collections.sort(words,new
	 * Comparator<String>() { public int compare(String o1, String o2) { return
	 * o1.compareToIgnoreCase(o2); } });
	 * 
	 * System.out.println(words);//输出[ABC, address, Bananer, dog] }
	 */

}

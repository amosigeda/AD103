package com.care.sys.nineoneoneinterfaces;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.care.common.config.ServiceBean;
import com.care.common.http.BaseAction;
import com.care.common.lang.Constant;
import com.care.sys.deviceLogin.domain.DeviceLogin;
import com.care.sys.deviceLogin.domain.logic.DeviceLoginFacade;
import com.care.sys.deviceactiveinfo.domain.DeviceActiveInfo;
import com.care.sys.deviceactiveinfo.domain.logic.DeviceActiveInfoFacade;
import com.care.sys.devicephoneinfo.domain.DevicePhoneInfo;
import com.care.sys.locationinfo.domain.LocationInfo;
import com.care.sys.locationinfo.domain.logic.LocationInfoFacade;
import com.care.sys.phoneinfo.domain.PhoneInfo;
import com.care.sys.phoneinfo.domain.logic.PhoneInfoFacade;
import com.godoing.rose.http.common.PagePys;
import com.godoing.rose.lang.DataMap;
import com.godoing.rose.log.LogFactory;

public class GetTestInfoAction extends BaseAction {
	Log logger = LogFactory.getLog(GetTestInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		String href = request.getServletPath();
		Date start = new Date();
		ServletInputStream input = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuffer sb = new StringBuffer();
		String online = "";
		while ((online = reader.readLine()) != null) {
			sb.append(online);
		}
		JSONObject object = JSONObject.fromObject(sb.toString());
		JSONObject locationData = new JSONObject();
		JSONArray trackData = new JSONArray();
		
		String imei = object.getString("imei");
		String serieNo = object.getString("sn");
		String ledTest = object.getString("LEDTest");
		String micTest = object.getString("MICTest");
		String bluetoothTest = object.getString("BluetoothTest");
		String wifiTest = object.getString("WifiTest");
		String phoneTest = object.getString("PhoneTest");
		String keyTest = object.getString("KeyTest");
		String voltageTest = object.getString("VoltageTest");
		String networkTest = object.getString("NetworkTest");
		String radioCalibrationTest = object.getString("RadioCalibrationTest");
		String chargingTest = object.getString("ChargingTest");
		String version = object.getString("version");
		
		try {
			LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();
			LocationInfo vo = new LocationInfo(); 
			vo.setImei(imei);
			vo.setLedTest(ledTest);
			vo.setMicTest(micTest);
			vo.setBluetoothTest(bluetoothTest);
			vo.setWifiTest(wifiTest);
			vo.setPhoneTest(phoneTest);
			vo.setKeyTest(keyTest);
			vo.setVoltageTest(voltageTest);
			vo.setNetworkTest(networkTest);
			vo.setRadioCalibrationTest(radioCalibrationTest);
			vo.setChargingTest(chargingTest);
			vo.setSerieNo(serieNo);
			vo.setVersion(version);
			vo.setUploadTime(new Date());
			
			info.insertTestInfo(vo);
			
			
			result = Constant.SUCCESS_CODE;
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
		json.put(Constant.RESULTCODE, result);
		json.put("request", href);
		
		/*insertVisit(href, bg, imei, 1, start, new Date(), json.toString()
				.getBytes("utf-8").length);*/
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json.toString());
		return null;
	}

	// double 相加
	public double jia(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}

}

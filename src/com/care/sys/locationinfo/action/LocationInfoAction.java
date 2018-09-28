package com.care.sys.locationinfo.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.care.app.LoginUser;
import com.care.common.config.Config;
import com.care.common.config.ServiceBean;
import com.care.common.http.BaseAction;
import com.care.common.lang.CommUtils;
import com.care.sys.companyinfo.domain.CompanyInfo;
import com.care.sys.companyinfo.domain.logic.CompanyInfoFacade;
import com.care.sys.companyinfo.form.CompanyInfoForm;
import com.care.sys.dynamicInfo.domain.DynamicInfo;
import com.care.sys.dynamicInfo.form.DynamicInfoForm;
import com.care.sys.feedbackinfo.domain.FeedBackInfo;
import com.care.sys.locationinfo.domain.LocationInfo;
import com.care.sys.locationinfo.domain.logic.LocationInfoFacade;
import com.care.sys.locationinfo.form.LocationInfoForm;
import com.care.sys.projectinfo.domain.ProjectInfo;
import com.godoing.rose.http.common.HttpTools;
import com.godoing.rose.http.common.PagePys;
import com.godoing.rose.http.common.Result;
import com.godoing.rose.lang.DataList;
import com.godoing.rose.lang.DataMap;
import com.godoing.rose.lang.SystemException;
import com.godoing.rose.log.LogFactory;

public class LocationInfoAction extends BaseAction{
	
	Log logger = LogFactory.getLog(LocationInfoAction.class);
	
	public ActionForward queryLocationInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date start = new Date();
		String href= request.getServletPath();		 
		Result result = new Result();//���
		PagePys pys = new PagePys();//ҳ������
		DataList list = null; //����ҳ��List  ��logic itrate��ȡ��
		StringBuffer sb = new StringBuffer();//�����ַ�����
		LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();//����userApp������ȡ��user�ֵ䣩
		try {
			LoginUser loginUser = (LoginUser)request.getSession().getAttribute(Config.SystemConfig.LOGINUSER);
			if (loginUser == null) {
				return null;
			}
			String companyInfoId = loginUser.getCompanyId();
			String projectInfoId = loginUser.getProjectId();
			
			LocationInfoForm form = (LocationInfoForm) actionForm;
			LocationInfo vo = new LocationInfo(); 
			String startTime = request.getParameter("startTime");
			String endTime   = request.getParameter("endTime");	
			String serieNo = request.getParameter("serieNo");
			String locationType = request.getParameter("locationType");
			String status = request.getParameter("statusSelect");
			String projectId = request.getParameter("projectId");

            form.setOrderBy("id"); 
            form.setSort("1"); 
            if(!projectInfoId.equals("0")){
				sb.append("belong_project in(" + projectInfoId + ")");
			}else{
				if(!companyInfoId.equals("0")){
					ProjectInfo pro = new ProjectInfo();
					pro.setCondition("company_id in(" + companyInfoId + ")");
					List<DataMap> proList = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
					if(proList.size() > 0){
						int num = proList.size();
						String str = "";
						for(int i=0; i<num; i++){
							Integer id = (Integer)proList.get(i).getAt("id");
							str += String.valueOf(id);
							if(i != num-1){
								str += ",";
							}
						}
						sb.append("l.belong_project in(" + str + ")");
					}					
				}
			}
            if(startTime == null && endTime == null){
            	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            	String dTime = format.format(new Date());
            	if(sb.length() > 0){
					sb.append(" and ");
				}
            	sb.append("substring(l.upload_time,1,10) = '" + dTime + "'");
            	request.setAttribute("fNow_date", dTime+" 00:00:00");
    		    request.setAttribute("now_date", dTime+" 23:59:59");
            }
			if(startTime != null && !"".equals(startTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.upload_time >= '"+startTime+"'");
				request.setAttribute("fNow_date", startTime);
			}
			if(endTime != null && !"".equals(endTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.upload_time <= '"+endTime+"'");
				request.setAttribute("now_date", endTime);
			}
			if(serieNo != null && !"".equals(serieNo)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.serie_no like '%"+serieNo+"%'");
			}
			if(locationType != null && !"".equals(locationType)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.location_type ='"+locationType+"'");
				request.setAttribute("locationType", CommUtils.getSelect(
						"locationType", Integer.parseInt(locationType)));
			}
			if(status != null && !"".equals(status)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.s_t ='"+status+"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", Integer.parseInt(status)));
			}
			if(status == null){
				if(sb.length()>0){
					sb.append("and");
				}
				String statu = "1";
				sb.append(" l.s_t ='"+ statu +"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", 1));
			}
			if(projectId != null && !"".equals(projectId)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("p.id ='"+projectId+"'");
			}
			ProjectInfo pro = new ProjectInfo();
			List<DataMap> pros = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
			request.setAttribute("project", pros);
			
		    request.setAttribute("serieNo", serieNo);
		    request.setAttribute("projectId", projectId);
			vo.setCondition(sb.toString());
			
         	BeanUtils.copyProperties(vo,form);			
         	list = info.getLocationInfoListByVo(vo);  
			BeanUtils.copyProperties(pys, form); 
			pys.setCounts(list.getTotalSize());   
			/* ���û������ֶ� */ 
			 
		} catch (Exception e) { 
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(Config.ABOUT_PAGE); /* ����Ϊ����ҳ�棬���Գ������ת��ϵͳĬ��ҳ�� */
			if (e instanceof SystemException) { /* ����֪�쳣���н��� */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* ��δ֪�쳣���н�������ȫ�������δ֪�쳣 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException"); 
			}
		} finally {
			request.setAttribute("result", result);
			request.setAttribute("pageList", list);
			request.setAttribute("PagePys", pys);
		}
		CommUtils.getIntervalTime(start, new Date(), href); 
		return mapping.findForward("queryLocationInfo");
	}
	public ActionForward queryNineOneOneLocationInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date start = new Date();
		String href= request.getServletPath();		 
		Result result = new Result();//���
		PagePys pys = new PagePys();//ҳ������
		DataList list = null; //����ҳ��List  ��logic itrate��ȡ��
		StringBuffer sb = new StringBuffer();//�����ַ�����
		LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();//����userApp������ȡ��user�ֵ䣩
		try {
			LoginUser loginUser = (LoginUser)request.getSession().getAttribute(Config.SystemConfig.LOGINUSER);
			if (loginUser == null) {
				return null;
			}
			String companyInfoId = loginUser.getCompanyId();
			String projectInfoId = loginUser.getProjectId();
			
			LocationInfoForm form = (LocationInfoForm) actionForm;
			LocationInfo vo = new LocationInfo(); 
			String startTime = request.getParameter("startTime");
			String endTime   = request.getParameter("endTime");	
			String serieNo = request.getParameter("serieNo");
			String locationType = request.getParameter("locationType");
			String status = request.getParameter("statusSelect");
			String projectId = request.getParameter("projectId");

            form.setOrderBy("OBJ_ID"); 
            form.setSort("1"); 
            if(!projectInfoId.equals("0")){
				sb.append("belong_project in(" + projectInfoId + ")");
			}else{
				if(!companyInfoId.equals("0")){
					ProjectInfo pro = new ProjectInfo();
					pro.setCondition("company_id in(" + companyInfoId + ")");
					List<DataMap> proList = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
					if(proList.size() > 0){
						int num = proList.size();
						String str = "";
						for(int i=0; i<num; i++){
							Integer id = (Integer)proList.get(i).getAt("id");
							str += String.valueOf(id);
							if(i != num-1){
								str += ",";
							}
						}
						sb.append("l.belong_project in(" + str + ")");
					}					
				}
			}
            if(startTime == null && endTime == null){
            	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            	String dTime = format.format(new Date());
            	if(sb.length() > 0){
					sb.append(" and ");
				}
            	sb.append("substring(INSERT_TIME,1,10) = '" + dTime + "'");
            	request.setAttribute("fNow_date", dTime+" 00:00:00");
    		    request.setAttribute("now_date", dTime+" 23:59:59");
            }
			if(startTime != null && !"".equals(startTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("INSERT_TIME >= '"+startTime+"'");
				request.setAttribute("fNow_date", startTime);
			}
			if(endTime != null && !"".equals(endTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("INSERT_TIME <= '"+endTime+"'");
				request.setAttribute("now_date", endTime);
			}
			if(serieNo != null && !"".equals(serieNo)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("DEVICE_SERIAL_NUMBER like '%"+serieNo+"%'");
			}
			if(locationType != null && !"".equals(locationType)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.location_type ='"+locationType+"'");
				request.setAttribute("locationType", CommUtils.getSelect(
						"locationType", Integer.parseInt(locationType)));
			}
			if(status != null && !"".equals(status)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.s_t ='"+status+"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", Integer.parseInt(status)));
			}
			/*if(status == null){
				if(sb.length()>0){
					sb.append("and");
				}
				String statu = "1";
				sb.append(" l.s_t ='"+ statu +"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", 1));
			}*/
			if(projectId != null && !"".equals(projectId)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("p.id ='"+projectId+"'");
			}
			ProjectInfo pro = new ProjectInfo();
			List<DataMap> pros = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
			request.setAttribute("project", pros);
			
		    request.setAttribute("serieNo", serieNo);
		    request.setAttribute("projectId", projectId);
			vo.setCondition(sb.toString());
			
         	BeanUtils.copyProperties(vo,form);			
         	list = info.getNineOneOneLocationInfoListByVo(vo);  
			BeanUtils.copyProperties(pys, form); 
			pys.setCounts(list.getTotalSize());   
			/* ���û������ֶ� */ 
			 
		} catch (Exception e) { 
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(Config.ABOUT_PAGE); /* ����Ϊ����ҳ�棬���Գ������ת��ϵͳĬ��ҳ�� */
			if (e instanceof SystemException) { /* ����֪�쳣���н��� */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* ��δ֪�쳣���н�������ȫ�������δ֪�쳣 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException"); 
			}
		} finally {
			request.setAttribute("result", result);
			request.setAttribute("pageList", list);
			request.setAttribute("PagePys", pys);
		}
		CommUtils.getIntervalTime(start, new Date(), href); 
		return mapping.findForward("queryNineOneOneLocationInfo");
	}
	
	public ActionForward queryQuDaoInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date start = new Date();
		String href= request.getServletPath();		 
		Result result = new Result();//���
		PagePys pys = new PagePys();//ҳ������
		DataList list = null; //����ҳ��List  ��logic itrate��ȡ��
		StringBuffer sb = new StringBuffer();//�����ַ�����
		LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();//����userApp������ȡ��user�ֵ䣩
		try {
			LoginUser loginUser = (LoginUser)request.getSession().getAttribute(Config.SystemConfig.LOGINUSER);
			if (loginUser == null) {
				return null;
			}
			
			
			LocationInfoForm form = (LocationInfoForm) actionForm;
			LocationInfo vo = new LocationInfo(); 
			String startTime = request.getParameter("startTime");
			String endTime   = request.getParameter("endTime");
			String gameName   = request.getParameter("gamename");
			String qudaonumber   = request.getParameter("qudaonumber");
			
			String serieNo = request.getParameter("serieNo");
			String locationType = request.getParameter("locationType");
			String status = request.getParameter("statusSelect");
			String projectId = request.getParameter("projectId");

            form.setOrderBy("id"); 
            form.setSort("1"); 
           
         
			if(startTime != null && !"".equals(startTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("createtime >= '"+startTime+"'");
				request.setAttribute("startTime", startTime);
			}
			if(endTime != null && !"".equals(endTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("createtime <= '"+endTime+"'");
				request.setAttribute("endTime", endTime);
			}
			
			if(gameName != null && !"".equals(gameName)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("gameName like '%"+gameName+"%'");
				request.setAttribute("gamename", gameName);
			}
			
			if(qudaonumber != null && !"".equals(qudaonumber)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("qudaonumber like '%"+qudaonumber+"%'");
				request.setAttribute("qudaonumber", qudaonumber);
			}
			
			
			if(serieNo != null && !"".equals(serieNo)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("DEVICE_SERIAL_NUMBER like '%"+serieNo+"%'");
			}
			if(locationType != null && !"".equals(locationType)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.location_type ='"+locationType+"'");
				request.setAttribute("locationType", CommUtils.getSelect(
						"locationType", Integer.parseInt(locationType)));
			}
			if(status != null && !"".equals(status)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.s_t ='"+status+"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", Integer.parseInt(status)));
			}
			/*if(status == null){
				if(sb.length()>0){
					sb.append("and");
				}
				String statu = "1";
				sb.append(" l.s_t ='"+ statu +"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", 1));
			}*/
			if(projectId != null && !"".equals(projectId)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("p.id ='"+projectId+"'");
			}
			ProjectInfo pro = new ProjectInfo();
			List<DataMap> pros = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
			request.setAttribute("project", pros);
			
		    request.setAttribute("serieNo", serieNo);
		    request.setAttribute("projectId", projectId);
			vo.setCondition(sb.toString());
			
         	BeanUtils.copyProperties(vo,form);			
         	list = info.getQuDaoInfoListByVo(vo);  
			BeanUtils.copyProperties(pys, form); 
			pys.setCounts(list.getTotalSize());   
			/* ���û������ֶ� */ 
			 
		} catch (Exception e) { 
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(Config.ABOUT_PAGE); /* ����Ϊ����ҳ�棬���Գ������ת��ϵͳĬ��ҳ�� */
			if (e instanceof SystemException) { /* ����֪�쳣���н��� */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* ��δ֪�쳣���н�������ȫ�������δ֪�쳣 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException"); 
			}
		} finally {
			request.setAttribute("result", result);
			request.setAttribute("pageList", list);
			request.setAttribute("PagePys", pys);
		}
		CommUtils.getIntervalTime(start, new Date(), href); 
		return mapping.findForward("queryQuDaoInfo");
	}
	
	public ActionForward deleteQuDaoInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) {
		Result result = new Result();
		try {
			LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();
			String id = request.getParameter("id");
			LocationInfo vo = new LocationInfo(); 
				vo.setCondition("id ='"+id+"'");    
			
				info.deleteQuDaoInfo(vo);  
			
			result.setBackPage(HttpTools.httpServletPath(request,
					"queryQuDaoInfo"));
			result.setResultCode("deletes");
			result.setResultType("success");
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(HttpTools.httpServletPath(request,
					"queryQuDaoInfo"));
			if (e instanceof SystemException) { /* 锟斤拷锟斤拷知锟届常锟斤拷锟叫斤拷锟斤拷 */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* 锟斤拷未知锟届常锟斤拷锟叫斤拷锟斤拷锟斤拷锟斤拷全锟斤拷锟斤拷锟斤拷锟轿粗拷斐?*/
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException");
			}
		} finally {
			request.setAttribute("result", result);
		}
		return mapping.findForward("result");
	}
	
	public ActionForward initInsert(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		return mapping.findForward("addQuDaoInfo");
	}
	
	public ActionForward insertQuDaoInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) {
		
		Result result = new Result();
		try {
			LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();
			LocationInfo vo = new LocationInfo(); 
			
			vo.setAddress(request.getParameter("createtime"));
			vo.setAltitude(request.getParameter("gamename"));
			vo.setAngle(request.getParameter("qudaonumber"));
			vo.setBattery(Integer.valueOf(request.getParameter("insertuser")));
			vo.setLiveuser(Integer.valueOf(request.getParameter("liveuser")));
			vo.setPayuser(Integer.valueOf(request.getParameter("payuser")));
			vo.setRunwater(request.getParameter("runwater"));
			vo.setPayarpu(request.getParameter("payarpu"));
			vo.setLivearpu(request.getParameter("livearpu"));
			vo.setPayrate(request.getParameter("payrate"));
			vo.setErkeep(request.getParameter("erkeep"));
			vo.setSankeep(request.getParameter("sankeep"));
			vo.setQikeep(request.getParameter("qikeep"));
			info.inserQuDaoInfo(vo);
			
			result.setBackPage(HttpTools.httpServletPath(request,  //插入成功后，跳转到原先页面
					"queryQuDaoInfo"));
			result.setResultCode("inserts");    //设置插入Code
			result.setResultType("success");    //设置插入成功
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(HttpTools.httpServletPath(request,
					"initInsert"));
			if (e instanceof SystemException) { /* 对已知异常进行解析 */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* 对未知异常进行解析，并全部定义成未知异常 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException");
			}
		} finally {
			request.setAttribute("result", result);
		}
		return mapping.findForward("result");
	}
	
	public ActionForward initQuDaoInfoUpdate(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();
		LocationInfo vo = new LocationInfo(); 
		vo.setCondition("id=" + id);
		vo.setOrderBy("id");
		vo.setSort("1");
		vo.setFrom(0);
		vo.setPageSize(1);
		List<DataMap> list = info.getQuDaoInfo(vo);

		if (list == null || list.size() == 0) {
			Result result = new Result();
			result.setBackPage(HttpTools.httpServletPath(request,
					"queryQuDaoInfo"));
			result.setResultCode("rowDel");
			result.setResultType("success");
			return mapping.findForward("result");
		}
		request.setAttribute("dynamicInfo", list.get(0)); // 把获取的信息用userInfo字符串相关联,在跳转getSession中获取userInfo中的属性值
																// ID
		return mapping.findForward("updateQuDaoInfo");
	}
	
	public ActionForward updateQuDaoInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) {
		
		Result result = new Result();
		try {
			LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();
			LocationInfo vo = new LocationInfo(); 
			vo.setCondition("id='" +request.getParameter("id")+ "'");
			vo.setAddress(request.getParameter("createtime"));
			vo.setAltitude(request.getParameter("gamename"));
			vo.setAngle(request.getParameter("qudaonumber"));
			vo.setBattery(Integer.valueOf(request.getParameter("insertuser")));
			vo.setLiveuser(Integer.valueOf(request.getParameter("liveuser")));
			vo.setPayuser(Integer.valueOf(request.getParameter("payuser")));
			vo.setRunwater(request.getParameter("runwater"));
			vo.setPayarpu(request.getParameter("payarpu"));
			vo.setLivearpu(request.getParameter("livearpu"));
			vo.setPayrate(request.getParameter("payrate"));
			vo.setErkeep(request.getParameter("erkeep"));
			vo.setSankeep(request.getParameter("sankeep"));
			vo.setQikeep(request.getParameter("qikeep"));
			
			info.updateQuDaoInfo(vo);
			
			result.setBackPage(HttpTools.httpServletPath(request,
					"queryQuDaoInfo"));
			result.setResultCode("updates");
			result.setResultType("success");
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(HttpTools.httpServletPath(request,
					"queryQuDaoInfo"));
			if (e instanceof SystemException) { /* ����֪�쳣���н��� */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* ��δ֪�쳣���н�������ȫ�������δ֪�쳣 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException");
			}
		} finally {
			request.setAttribute("result", result);
		}
		return mapping.findForward("result");
	}
	
	public ActionForward queryTestInfo(ActionMapping mapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date start = new Date();
		String href= request.getServletPath();		 
		Result result = new Result();
		PagePys pys = new PagePys();
		DataList list = null; 
		StringBuffer sb = new StringBuffer();
		LocationInfoFacade info = ServiceBean.getInstance().getLocationInfoFacade();//����userApp������ȡ��user�ֵ䣩
		try {
			/*LoginUser loginUser = (LoginUser)request.getSession().getAttribute(Config.SystemConfig.LOGINUSER);
			if (loginUser == null) {
				return null;
			}*/
			
			
			LocationInfoForm form = (LocationInfoForm) actionForm;
			LocationInfo vo = new LocationInfo(); 
			String startTime = request.getParameter("startTime");
			String endTime   = request.getParameter("endTime");
			String gameName   = request.getParameter("gamename");
			String qudaonumber   = request.getParameter("qudaonumber");
			
			String imei = request.getParameter("imei");
			String locationType = request.getParameter("locationType");
			String status = request.getParameter("statusSelect");
			String projectId = request.getParameter("projectId");
			String sn = request.getParameter("sn");
			String version = request.getParameter("version");
            form.setOrderBy("id"); 
            form.setSort("1"); 
           
         
			if(startTime != null && !"".equals(startTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("createtime >= '"+startTime+"'");
				request.setAttribute("startTime", startTime);
			}
			if(endTime != null && !"".equals(endTime)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("createtime <= '"+endTime+"'");
				request.setAttribute("endTime", endTime);
			}
			
			if(gameName != null && !"".equals(gameName)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("gameName like '%"+gameName+"%'");
				request.setAttribute("gamename", gameName);
			}
			
			if(qudaonumber != null && !"".equals(qudaonumber)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("qudaonumber like '%"+qudaonumber+"%'");
				request.setAttribute("qudaonumber", qudaonumber);
			}
			
			
			if(imei != null && !"".equals(imei)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("imei like '%"+imei+"%'");
			}
			
			if(sn != null && !"".equals(sn)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("sn like '%"+sn+"%'");
			}
			
			if(version != null && !"".equals(version)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("sn version '%"+version+"%'");
			}
			
			if(locationType != null && !"".equals(locationType)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.location_type ='"+locationType+"'");
				request.setAttribute("locationType", CommUtils.getSelect(
						"locationType", Integer.parseInt(locationType)));
			}
			if(status != null && !"".equals(status)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("l.s_t ='"+status+"'");
				request.setAttribute("statusSelect", CommUtils.getSelectMess("statusSelect", Integer.parseInt(status)));
			}
			
			if(projectId != null && !"".equals(projectId)){
				if(sb.length() > 0){
					sb.append(" and ");
				}
				sb.append("p.id ='"+projectId+"'");
			}
			ProjectInfo pro = new ProjectInfo();
			List<DataMap> pros = ServiceBean.getInstance().getProjectInfoFacade().getProjectInfo(pro);
			request.setAttribute("project", pros);
			
		    request.setAttribute("imei", imei);
		    request.setAttribute("sn", sn);
		    request.setAttribute("version", version);
		    request.setAttribute("projectId", projectId);
			vo.setCondition(sb.toString());
			
         	BeanUtils.copyProperties(vo,form);			
         	list = info.getTestInfoListByVo(vo);  
			BeanUtils.copyProperties(pys, form); 
			pys.setCounts(list.getTotalSize());   
			/* ���û������ֶ� */ 
			 
		} catch (Exception e) { 
			e.printStackTrace();
			logger.debug(request.getQueryString() + "  " + e);
			result.setBackPage(Config.ABOUT_PAGE); /* ����Ϊ����ҳ�棬���Գ������ת��ϵͳĬ��ҳ�� */
			if (e instanceof SystemException) { /* ����֪�쳣���н��� */
				result.setResultCode(((SystemException) e).getErrCode());
				result.setResultType(((SystemException) e).getErrType());
			} else { /* ��δ֪�쳣���н�������ȫ�������δ֪�쳣 */
				result.setResultCode("noKnownException");
				result.setResultType("sysRunException"); 
			}
		} finally {
			request.setAttribute("result", result);
			request.setAttribute("pageList", list);
			request.setAttribute("PagePys", pys);
		}
		CommUtils.getIntervalTime(start, new Date(), href); 
		return mapping.findForward("queryTestInfo");
	}
	
}

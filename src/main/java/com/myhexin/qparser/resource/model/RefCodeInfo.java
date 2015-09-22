package com.myhexin.qparser.resource.model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.RefCode;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;

public class RefCodeInfo implements ResourceInterface{
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private static RefCodeInfo instance = new RefCodeInfo();
	public static RefCodeInfo getInstance() {
		return instance;
	}
	
	private RefCodeInfo(){
	}
	
	private Set<String> shouldNotConvert2IdIndexNames = new HashSet<String>();

	private Map<Integer, List<RefCode>> refcodeMap;
	//最大句式个数
	private Integer maxSyntactNum;
	private List<String> filterWords;
	//private boolean debug = false;
	private String hostName;
	private String ip;
	
	public List<RefCode> get(Integer id) {
		if(refcodeMap==null) return null;
		return refcodeMap.get(id);
	}
	
	public List<String> getFilterWords() {
		return filterWords;
	}
	
	//attr_id=1006, 不要转成ID的指标名称
	public boolean shouldConvert2Id(String indexName) {
		return !shouldNotConvert2IdIndexNames.contains(indexName);
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		loadHostName();
		
		List<RefCode> refCodes = mybatisHelp.getDateParserInfoMapper().getRefCode();
		Map<Integer, List<RefCode>> temp_refCodeMap = new HashMap<Integer, List<RefCode>>();
		for(RefCode code : refCodes) {
			List<RefCode> refs = temp_refCodeMap.get(code.getAttr_id());
			if(refs==null) {
				refs = new ArrayList<RefCode>();
				temp_refCodeMap.put(code.getAttr_id(), refs);
			}
			refs.add(code);
		}
		refcodeMap = temp_refCodeMap;
		
		//最大句式个数
		List<RefCode> synNumList = refcodeMap.get(1004);
		if(synNumList!=null && synNumList.size()>0) {
			try{
				maxSyntactNum = new Integer(synNumList.get(0).getCode_value());
			}catch(Exception e){}
		}
		if(maxSyntactNum==null) maxSyntactNum=10;
		
		
		//信息领域过滤词
		List<RefCode> newsFilterWords = refcodeMap.get(1005);
		List<String> temp_filterWords = new ArrayList<String>();
		for(RefCode r : newsFilterWords) {
			temp_filterWords.add(r.getCode_value());
		}
		filterWords = temp_filterWords;
		
		
		//是否打印DEBUG 日志
		/*List<RefCode> debugIps = refcodeMap.get(1006);
		if(debugIps!=null && ip!=null) {
			for(RefCode r : debugIps) {
				if(r!=null && r.getCode_value().equals(ip) && "Y".equals(r.getCode_short_desc()) ) {
					debug = true;
					break;
				}
			}
		}*/
		
		//不要转成ID的指标名称
		shouldNotConvert2IdIndexNames.clear();
		List<RefCode> index_names = refcodeMap.get(1006);
		if(index_names!=null) {
			for(RefCode r : index_names) {
				if(r.getCode_value()!=null) shouldNotConvert2IdIndexNames.add(r.getCode_value());
			}
		}
	}
	
	private void loadHostName() {
		try{
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
			ip = addr.getHostAddress().toString();
		}catch(Exception e){
			hostName = "localhost";
			ip= "127.0.0.1";
		}
		
		//System.out.println("Running on " + hostName + "," + ip);
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public String getIp() {
		return ip;
	}
	
	/*public boolean isdebug() {
    	return debug;
    }*/
    
    public Integer getMaxSyntactNum() {
    	return maxSyntactNum;
    }
}

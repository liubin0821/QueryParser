/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-10-28 上午10:33:18
 * @description:   	
 * 
 */
package com.myhexin.qparser.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;

public class OnlineIpInfo {

	public static boolean isOnlineIp() {	
		try {
			String ip = InetAddress.getLocalHost().getHostAddress().toString();
			return isOnlineIp(ip);
		} catch (UnknownHostException e) {}

		return false;
	}
	
	public static boolean isOnlineIp(String ip){
		MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
		if (mybatisHelp.getOnlineIpMapper().searchOnlineIp(ip) != null) 
			return true;
		
		return false;
	}
}

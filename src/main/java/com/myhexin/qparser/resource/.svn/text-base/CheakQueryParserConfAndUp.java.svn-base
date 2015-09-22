package com.myhexin.qparser.resource;

/*
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
*/

/**
 * 
 * 这个不要了,这么做不合理,增加服务的负担,应该通过外部任务调用更新服务来更新配置文件
 * 比如cron任务,curl http://xxxx:9100/fileUpdateService
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-26
 *
 */
public class CheakQueryParserConfAndUp {// extends Thread{
	/*
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CheakQueryParserConfAndUp.class.getName());
	private final static  ThreadLocal configFileLocal = new ThreadLocal(); //ThreadLocal解决多线程并发访问的问题。
	private ArrayList<configFileInfo> configFileList = new ArrayList<configFileInfo>(); //存放注册文件的详细信息。
	private ArrayList<String> configFileRealNameList = new ArrayList<String>();  //新系统传进来的真实文件名数组
	private static long cheakAllConfSleepTime = 60000; //每次自动更新所等待的时间
	private static long cheakSingleConfSleepTime = 100; //处理单个配置文件需要等待的时间
	private static boolean masterSwitch = true; //配置自动更新的开关
    byte buffer[] = new byte[1024];
    
	public CheakQueryParserConfAndUp(ArrayList<String> configFileRealNameList){
		this.configFileRealNameList = configFileRealNameList;
	}
	
	private class configFileInfo{
		String strName; //文件名
		String strRealName; //文件的真实路径加名字
		String strMd5;  //根据文件内容计算的Md5值
		long lastChangeTime;  //文件最后修改的时间
	}
	
	public void run() { 
		//初始化，先读取本地上配置文件的信息。
		initMap();
        while (masterSwitch) {        	
        	 try {
        		 logger_.info("配置开始自动检测");
        		 cheakQueryParserConf();
        		 //等待1分钟，节约内存和CPU开销
        	     Thread.sleep(cheakAllConfSleepTime);    	    
        	 } catch(InterruptedException e) {
        	       return;
        	  }
        } 
   } 
	//初始化，先获得当前配置文件的详细信息。
	public void initMap(){
		//根据文件真实路径和文件名来计算文件的最后修改时间和MD5信息，并存在configFileList。
		for(String confStrRealName:configFileRealNameList){
			configFileInfo config = new configFileInfo();							
			config.strRealName = confStrRealName;
			File file = new File(confStrRealName);
			config.strName = file.getName();
			config.strMd5 = getFileMD5(file, buffer);
			config.lastChangeTime = file.lastModified();
			configFileList.add(config);
		}		
		
	}
	
	public void cheakQueryParserConf(){	
		int i;
		int len = configFileList.size();
		for(i=0; i<len; i++){
			try {
				//读取每个配置文件中间添加等待时间，便于文件的处理。
				Thread.sleep(cheakSingleConfSleepTime);
			} catch (InterruptedException e) { ;}
			//获得经过注册的配置文件的详细信息。
			configFileInfo conf = configFileList.get(i);
			File file = new File(conf.strRealName);
			//如果文件最后修改时间没有发生变化则不用更新。
			long newFileTime = file.lastModified();
			if(newFileTime <= conf.lastChangeTime)
				continue;
			//根据Md5值来确定文件内容是否更改，如果内容没有发生变化也不需要更新
			String newMd5 = getFileMD5(file, buffer);
			if(newMd5 != null && newMd5.equals(conf.strMd5))
			{
				//当文件内容没有更改，但修改时间改变的时候，需要将文件的最后修改时间替换。
				conf.lastChangeTime = newFileTime;
				continue;
			}
			//重新加载配置文件，然后将对应的配置文件中的最后修改时间和Md5值进行替换
			if(updateFileMemory()){
					 conf.lastChangeTime = newFileTime;
					 conf.strMd5 = newMd5;
					 logger_.info("["+conf.strName+"] 自动更新完成");
			}else{
				logger_.info("自动更新失败");
			}
		}
	}
	
	//根据文件信息来将配置文件更新到服务上去
	private  boolean updateFileMemory(){
		try {
			Resource.reloadData();
		} catch (DataConfException e) {
			e.printStackTrace();
			return false;
		} catch (UnexpectedException e) {
			e.printStackTrace();
			return false;
		} catch (NotSupportedException e) {
			e.printStackTrace();
			return false;
		}
         return true;
	}
		
	//根据文件的内容来算MD5
	//@param fileName
	//@return
	private String getFileMD5(File file, byte[] buffer){
        MessageDigest digest = null;
	    FileInputStream in=null;
	    if(buffer == null)
	    	buffer = new byte[1024];
	    int len;
	    try {
	      digest = MessageDigest.getInstance("MD5");
	      in = new FileInputStream(file);
	      while ((len = in.read(buffer, 0, 1024)) != -1) {
	        digest.update(buffer, 0, len);
	      }
	      in.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    }
	    BigInteger bigInt = new BigInteger(1, digest.digest());
	    return bigInt.toString(16);
	}
	*/
}

package com.myhexin.qparser.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MemoryUtil {
	static class MemoryInfo {
		public String className;
		public double runTimeUse;
		
		//[Code Cach=4.66MB][Eden Spac=14.34MB][Survivor Spac=3.69MB]
		//[Tenured Ge=54.77MB][Perm Ge=11.85MB][Perm Gen [shared-ro=4.48MB][Perm Gen [shared-rw=6.33MB]
		public long codeCache;
		public long edenSpace;
		public long survivorSpac;
		public long tenuredGe;
		public long permGe;
		static double unit = 1024*1024;
		
		
		public String toString() {
			String s = String.format("[%50s] %.2fMB  %.2fMB  %.2fMB  %.2fMB  %.2fMB %.2fMB",  className, runTimeUse, edenSpace/unit, codeCache/unit, survivorSpac/unit ,tenuredGe/unit, permGe/unit) ;
			return s;
		}
	};
	
	private final static boolean DEBUG = false;
	public static List<MemoryInfo> memInfos = new ArrayList<MemoryInfo>();
	public static void getMemoryInfo(String className) {
		//if(Consts.DEBUG == false) return;
		
		//双保险
		//if(DEBUG == false) return;
		
		//query by ManagementFactory
		MemoryInfo info = new MemoryInfo();
		info.className = className;
		
		List<MemoryPoolMXBean> ms = ManagementFactory.getMemoryPoolMXBeans();
		//StringBuilder buf = new StringBuilder();
		
		if (ms != null) {
			for (MemoryPoolMXBean mp : ms) {
				String objectName = mp.getObjectName().toString();
				int idx = objectName.indexOf("name=");
				if(idx>0) objectName = objectName.substring(idx+5, objectName.length()-1);
				MemoryUsage mu = mp.getUsage();
				
				if(objectName.equals("Code Cach")) {
					info.codeCache = mu.getUsed();
				}else if(objectName.equals("Eden Spac")) {
					info.edenSpace = mu.getUsed();
				}else if(objectName.equals("Survivor Spac")) {
					info.survivorSpac = mu.getUsed();
				}else if(objectName.equals("Tenured Ge")) {
					info.tenuredGe  = mu.getUsed();
				}else if(objectName.equals("Perm Ge")) {
					info.permGe = mu.getUsed();
				}
			}
		}
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		//long max = Runtime.getRuntime().maxMemory();
		//String s = String.format("used=%.2fMB, used2=%.2f\n %s",  (total-free)/unit, total1/unit, buf.toString() ) ;
		info.runTimeUse =  (total-free)/MemoryInfo.unit;
		//System.out.println(s);
		//logs.add(className + "\n" + s);
		memInfos.add(info);
		//return buf.toString();		
	}
	
	public static void print() {
		try{
		MemoryInfo preMemInfo = null;
		OutputStream fos = new FileOutputStream("e:/aaaaa.txt");
		List<String> changes = new ArrayList<String>();
		Map<String, Integer> changeMap = new HashMap<String, Integer>();
		fos.write( (new java.util.Date().toString()+ "\n") .getBytes());
		String s1 = String.format("[%50s] %s  %s  %s  %s  %s %s",  "ClassName", "rt", "eden", "codeCache", "survivorSpac" ,"tenuredGe", "permGe") ;
		System.out.println(s1);
		fos.write( (s1+ "\n") .getBytes());
		for(MemoryInfo memInfo : memInfos) {
			System.out.println(memInfo.toString());
			if("## Text Normalize".equals(memInfo.className) ) {
				for(String s : changes) {
					System.out.println(s);
					fos.write( (s+"\n") .getBytes());
				}
				changes.clear();
				fos.write( ("\n") .getBytes());
			}
			fos.write( (memInfo.toString()+"\n") .getBytes());
			
			if(preMemInfo!=null) {
				double change = memInfo.edenSpace -  preMemInfo.edenSpace;
				//System.out.println("change1=" + change1 + ",change2=" + change2);
				if(Math.abs(change)>0) {
					String s = String.format("%.2fMB : %s", change/MemoryInfo.unit, memInfo.className) ;
					changes.add(s);
					Integer count = changeMap.get(memInfo.className);
					if(count==null) {
						changeMap.put(memInfo.className, 1);
					}else{
						changeMap.put(memInfo.className, count+ 1);
					}
					//fos.write( (s+"\n") .getBytes());
				}
			}
			preMemInfo = memInfo;
		}
		
		Iterator<String> it = changeMap.keySet().iterator();
		while(it.hasNext()) {
			String  k = it.next();
			Integer  v = changeMap.get(k);
			fos.write( (k + ": " + v + "\n") .getBytes());
		}
		
		fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		memInfos.clear();
	}
}

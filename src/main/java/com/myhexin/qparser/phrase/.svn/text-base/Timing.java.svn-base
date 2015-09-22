package com.myhexin.qparser.phrase;


/**
 * 计时器封装
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-31
 *
 */
public class Timing {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Timing.class.getName());
	private final static long TOO_MUCH_TIME = 10; //ms

	private long start;
	private long end;
	//private double seconds;
	private long mills;
	public void start() {
		reset();
		start = System.currentTimeMillis();
	}
	
	public void end(){
		end = System.currentTimeMillis();
		mills = end -start;
		//seconds = mills/1000.0;
	}
	

	public double second() {
		return 0.0; //seconds;
	}
	
	public long mills() {
		return mills;
	}
	
	public boolean isBigtime() {
		if( mills>=TOO_MUCH_TIME) {
			return true;
		}else{
			return false;
		}
	}
	
	public void print(String msg ) {
		//System.out.println(msg + " : " + (end-start) + "ms");
		//String name  = Thread.currentThread().getId() + "-" +  Thread.currentThread().getName();
		//logger_.info(name + " : " + msg + " : " + (end-start) + "ms");
	}
	public void log(String msg ) {
		//String name  = Thread.currentThread().getId() + "-" +  Thread.currentThread().getName();
		//logger_.info(name + " : " + msg + " : " + (end-start) + "ms");
	}
	
	public void reset() {
		start = 0;
		end = 0;
		//seconds = 0;
		mills = 0;
	}
}

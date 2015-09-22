package com.myhexin.qparser.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * 监控解析线程
 * 1. 如果线程执行超过WARN_THREAD_RUNNING_TIME秒,那么打印线程stacktrace到日志
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-6-11
 *
 */
public class ThreadMonitor {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ThreadMonitor.class.getSimpleName());
	private ThreadMonitor(){
		
		new Thread(new Monitor()).start();
	}
	private static ThreadMonitor instance = new ThreadMonitor();
	public static ThreadMonitor getInstance(){
		return instance;
	}
	
	
	//线程信息
	static class TInfo {
		private long start; //线程开始时间
		private Thread thread; //线程句柄
		private String query; //对应问句
		private int printTick=0;
		
		public TInfo(long start, Thread thread, String query) {
			this.start = start;
			this.thread = thread;
			this.query = query;
		}
		
		
		/**
		 * 线程ID
		 * 问句
		 * 用了多少时间
		 * 线程stack dump
		 * 
		 * @param ts
		 * @return
		 */
		public String toString(long ts) {
			try{
				if(thread!=null) {
					StringBuilder buf = new StringBuilder();
					buf.append(String.format("[DeadLoop]tid=%s,query=%s,timespend=%d\n", thread.getId(), query, ts));
					StackTraceElement[] elems = thread.getStackTrace();
					if(elems!=null ){
						for(int i=0;i<elems.length && i<STACK_TRACE_LEVEL;i++) {
							StackTraceElement e = elems[i];
							buf.append(String.format("\t[DeadLoop] %s[%s] %s.%s\n", e.getFileName(), e.getLineNumber(),e.getClassName(), e.getMethodName()));
						}
					}
					return buf.toString();
				}
			}catch(Exception e){
				logger_.info("ERROR: " + e.getMessage());
			}
			return query + "," + thread + ","+start;
		}
	}
	
	private final static int WARN_THREAD_RUNNING_TIME = 10000; //超过10秒的问句,记日志
	private final static int WARN_TIME_VALUE = 3000; //超过2秒的问句,报个警
	private final static int MAX_THREAD_SIZE = 300; //最多往里放多少线程
	private final static int STACK_TRACE_LEVEL = 50; //thread dump多少层
	private final static int CHECK_TIME_INTERVAL = 1000; //每1秒检查一次
	
	//用linked list
	private final Map<Long, TInfo> tMap = new HashMap<Long, TInfo>(MAX_THREAD_SIZE);
	
	
	//加入线程
	public void addThread(Thread thread, String query) {
		if(tMap.size()>=MAX_THREAD_SIZE) {
			logger_.warn("ThreadInfoMap size exceed 200!!!" );
		}else{
			synchronized(instance) {
				tMap.put(thread.getId(), new TInfo(System.currentTimeMillis(), thread, query));
			}
		}
		
		//debug
		//print();
	}
	
	/*private void print() {
		synchronized(instance) {
			if(tMap.size()>0) {
				long current = System.currentTimeMillis();
				Set<Entry<Long, TInfo>> entries = tMap.entrySet();
				for(Entry<Long, TInfo> e : entries) {
					TInfo tInfo= e.getValue();
					long ts = current - tInfo.start;
					System.out.println(tInfo.thread.getId() + ", " + ts);
				}
			}
		}
	}*/
	
	
	//移除线程
	public void removeThread(Thread thread) {
		synchronized(instance) {
			TInfo tInfo = tMap.get(thread.getId());
			if(tInfo!=null) {
				long end = System.currentTimeMillis();
				long ts = end-tInfo.start;
				if(ts>WARN_TIME_VALUE) {
					logger_.warn("TIME OUT=" + ts +"ms, query=" + tInfo.query);
				}
				tMap.remove(thread.getId());
			}
		}
	}
	
	class Monitor implements Runnable {
		@Override
		public void run() {
			logger_.info("ThreadMonitor thread start");
			while(true){
				//遍历tMap,找出超时严重的线程,打出stackTrace
				synchronized(instance) {
					if(tMap.size()>0) {
						long current = System.currentTimeMillis();
						Set<Entry<Long, TInfo>> entries = tMap.entrySet();
						for(Entry<Long, TInfo> e : entries) {
							TInfo tInfo= e.getValue();
							long ts = current - tInfo.start;
							if(ts>WARN_THREAD_RUNNING_TIME*(tInfo.printTick+1)) { //每10秒打印一次日志
								logger_.warn(tInfo.toString(ts));
								tInfo.printTick=tInfo.printTick+1;
							}
						}
					}
				}
				
				
				
				//每1秒检查一次
				try {
					Thread.sleep(CHECK_TIME_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

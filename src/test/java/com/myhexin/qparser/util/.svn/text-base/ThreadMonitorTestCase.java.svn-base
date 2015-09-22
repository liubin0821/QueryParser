package com.myhexin.qparser.util;

public class ThreadMonitorTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Thread(new A(1000)).start();
		new Thread(new A(12000)).start();
		new Thread(new A(13000)).start();
		new Thread(new A(4000)).start();
		new Thread(new A(5000)).start();
		new Thread(new A(6000)).start();
		new Thread(new A(7000)).start();
		new Thread(new A(8000)).start();
		new Thread(new A(9000)).start();
		new Thread(new A(100000000)).start();
	}

	
	static class A implements Runnable {
		ThreadMonitor m = ThreadMonitor.getInstance();
		private int s;
		public A(int s) {
			this.s = s;
		}
		
		@Override
		public void run() {
			m.addThread(Thread.currentThread(), "");
			System.out.println("in A");
			
			try {
				Thread.sleep(s);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("out A");
			m.removeThread(Thread.currentThread());
		}
		
	}
}

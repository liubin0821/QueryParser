package com.myhexin.server.vo;

public abstract class ParserResultAbstract implements Result {
	private Object result;
	private StringBuilder log;
	private int errno;
	private StringBuilder errmsg;

	@Override
	public Object getResult() {
		return this.result;
	}
	
	@Override
	public void setResult(Object result) {
		this.result = result;
	}

	public StringBuilder getLog() {
		return log;
	}

	public void setLog(StringBuilder log) {
		if(log == null)
			return;
		else if(this.log == null || this.log == log)
			this.log = log;
		else
			this.log.append(log);
	}

	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public StringBuilder getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(StringBuilder errmsg) {
		if (errmsg == null)
			return;
		else if (this.errmsg == null || this.errmsg == errmsg)
			this.errmsg = errmsg;
		else
			this.log.append(errmsg);
	}
}

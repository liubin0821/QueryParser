package com.myhexin.server.vo.impl;

import com.myhexin.qparser.node.Environment;
import com.myhexin.server.vo.ParserParamAbstract;

/**
 * @author 徐祥
 * @createDataTime 2014-5-13 上午10:25:16
 * @description 解析参数
 */
public class ParserParam extends ParserParamAbstract {
	private Object preResult;
	private Environment preEnv;
	private StringBuilder preLog;
	
	public Object getPreResult() {
		return preResult;
	}

	public void setPreResult(Object preResult) {
		this.preResult = preResult;
	}

	public Environment getPreEnv() { 
		return preEnv;
	}

	public void setEnv(Environment preEnv) {
		this.preEnv = preEnv;
	}

	public StringBuilder getPreLog() {
		return preLog;
	}

	public void setPreLog(StringBuilder preLog) {
		this.preLog = preLog;
	}
	
}

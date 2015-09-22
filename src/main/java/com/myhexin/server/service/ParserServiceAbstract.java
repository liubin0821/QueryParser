package com.myhexin.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.server.processor.ParserProcessorAbstract;
import com.myhexin.server.vo.ParserParamAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:08:03
 * @description 解析服务抽象类
 */
public abstract class ParserServiceAbstract implements Service {

	protected Logger logger;
	protected ParserProcessorAbstract processor;
	
	public ParserServiceAbstract() {
		super();
		
		logger = LoggerFactory.getLogger(this.getClass().getName());
	}

	@Override
	public Object serve(Object param) {
		return serve((ParserParamAbstract) param);
	}
	
	/**
	 * @descrption 解析服务抽象服务函数
	 */
	public abstract ParserResult serve(ParserParam param);
	
	// set和get方法
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public ParserProcessorAbstract getProcessor() {
		return processor;
	}

	public void setProcessor(ParserProcessorAbstract processor) {
		this.processor = processor;
	}
}

package com.myhexin.server.processor;

import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-14 上午10:32:59
 * @description 解析处理抽象类
 */
public abstract class ParserProcessorAbstract implements Processor{

	@Override
	public Object process(Object param) {
		return process((ParserParam) param);
	}
	
	/**
	 * @descrption 解析处理抽象函数
	 */
	public abstract ParserResult process(ParserParam param);

}

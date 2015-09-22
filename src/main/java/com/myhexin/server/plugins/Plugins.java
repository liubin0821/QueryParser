package com.myhexin.server.plugins;

import com.myhexin.qparser.node.Environment;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:17:10
 * @description 组合插件接口
 */
public interface Plugins {
	Object work(Object obj);
}

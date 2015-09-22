/**
 * @author 吴永行
 * @createTime 2014-04-08 11:07:42
 * @description	
 */
package com.myhexin.qparser.ApplicationContext;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description 加载spring容器
 * @author 徐祥
 * @createDataTime 2014-7-31 下午3:56:29
 * @description change by wyh 2014.08.13
 */
public class ApplicationContextHelper implements ApplicationContextAware,DisposableBean {

	private static org.slf4j.Logger logger_ = LoggerFactory.getLogger(ApplicationContextHelper.class.getName());

	private static ApplicationContext context  = null;
	
	private static String defalutApplicationContext = "applicationContext.xml";

	
	/**
	 * @description 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
	 * @param ApplicationContext
	 * @throws BeansException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		if (null == applicationContext)
			logger_.warn("SpringContextHolder中的applicationContext[{}]被覆盖",applicationContext);
		ApplicationContextHelper.context  = applicationContext;
	}


	/**
	 * @description 加载applicationContext.xml，且不重复加载
	 */
	public static final void loadApplicationContext() {		
		loadApplicationContext(defalutApplicationContext);
	}
	
	/**
	 * @description 重新加载（加载或替换）applicationContext.xml
	 */
	public static final void reloadloadApplicationContext() {
		reloadloadApplicationContext(defalutApplicationContext);
	}
	
	public static synchronized void loadApplicationContext(String configLocation) {
		if (null != context ) {
			logger_.warn("SpringContextHolder中的applicationContext[{}]已初始化",context );
			return;
		}
		context  = new ClassPathXmlApplicationContext(configLocation);
	}

	public static synchronized void reloadloadApplicationContext(String configLocation) {
		if (null != context )
			logger_.warn("SpringContextHolder中的applicationContext[{}]被覆盖",context );
		context  = new ClassPathXmlApplicationContext(configLocation);
	}

	private static final void checkContextInited() {
		if (null == context ){
			loadApplicationContext();
			logger_.error("SpringContextHolder中的applicationContext为null，调用默认位置\"" +defalutApplicationContext+"\"进行初始化");
		}
	}

	public static ApplicationContext getApplicationContext() {
		checkContextInited();
		return context ;
	}

	@SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
		checkContextInited();
		return (T) context .getBean(name);
	}

	public static <T> T getBean(Class<T> requiredType) {
		checkContextInited();
		return context .getBean(requiredType);
	}

	public static void clearHolder() {
		logger_.debug("清除SpringContextHolder中的applicationContext[{}]", context );
		context  = null;
	}


	@Override
	public void destroy() throws Exception {
		ApplicationContextHelper.clearHolder();
	}


	public void setDefalutApplicationContext(String defalutApplicationContext) {
    	ApplicationContextHelper.defalutApplicationContext = defalutApplicationContext;
    }
	

}

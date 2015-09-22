package com.myhexin;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 该测试类是所有测试类的基类，该类的作用是对容器的一些操作。
 * <p>
 * 由于只涉及到查询操作，因此不需要支持容器管理的事务。
 * 
 * @author huangmin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appContext.xml" })
public class BaseTest extends AbstractJUnit4SpringContextTests {

	/**
	 * 在测试集开始前做一些事情。
	 */
	@BeforeClass
	public static void beforeClass() {
		// do something for start test.
	}

	/**
	 * 在测试集结束后做一些事情。
	 */
	@AfterClass
	public static void afterClass() {
		// do something for end test.
	}

	/**
	 * 获取spring容器。
	 * 
	 * @return spring的增强式容器，一般情况不应该破坏容器的内部结构和依赖。
	 */
	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 从spring容器中根据class类型获取一个bean。
	 * 
	 * @param beanType
	 * @return
	 */
	protected <T> T getBean(Class<T> beanType) {
		return applicationContext.getBean(beanType);
	}

	/**
	 * 从spring容器中根据指定的名称获取一个bean。
	 * 
	 * @param beanName
	 * @return
	 */
	protected Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
}

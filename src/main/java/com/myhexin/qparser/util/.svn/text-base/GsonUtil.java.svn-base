package com.myhexin.qparser.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class GsonUtil {

	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(GsonUtil.class.getName());

	private static Gson gson_;

	/**
	 * @description 空值
	 */
	public static final String EMPTY = "";
	/**
	 * @description 空的 {@code JSON} 数据 - <code>"{}"</code>。
	 */
	public static final String EMPTY_JSON = "{}";
	/**
	 * 空的 {@code JSON} 数组(集合)数据 - {@code "[]"}。
	 */
	public static final String EMPTY_JSON_ARRAY = "[]";
	/**
	 * 默认的 {@code JSON} 日期/时间字段的格式化模式。
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";//"yyyy-MM-dd HH:mm:ss SSS";
	/**
	 * {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.0}。
	 */
	public static final Double SINCE_VERSION_10 = 1.0d;
	/**
	 * {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.1}。 
	 */
	public static final Double SINCE_VERSION_11 = 1.1d;
	/**
	 * {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.2}。
	 */
	public static final Double SINCE_VERSION_12 = 1.2d;
	
	static {  
        if (null == gson_) {  
        	gson_ = new GsonBuilder()
        			//.excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性 
        			.enableComplexMapKeySerialization()
        			.serializeNulls()
        			.setDateFormat(DEFAULT_DATE_PATTERN)
        			.setVersion(SINCE_VERSION_10)
        			.create();
        }  
    }
	
	/**
	 * @description 将给定的目标对象根据指定的条件参数转换成 {@code JSON} 格式的字符串。
	 * <strong>该方法转换发生错误时，不会抛出任何异常。若发生错误时，曾通对象返回 <code>"{}"</code>； 集合或数组对象返回
	 * <code>"[]"</code></strong>
	 * @usingWarning 
	 * @param obj
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object src) {
		return toJson(src, null);
	}
	
	/**
	 * @description 将给定的目标对象根据指定的条件参数转换成 {@code JSON} 格式的字符串。
	 * <strong>该方法转换发生错误时，不会抛出任何异常。若发生错误时，曾通对象返回 <code>"{}"</code>； 集合或数组对象返回
	 * <code>"[]"</code></strong>
	 * @usingWarning 
	 * @param obj
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object src, Type typeOfSrc) {
		String jsonStr = EMPTY;
		try {
			if (null != typeOfSrc) {
				jsonStr = gson_.toJson(src, typeOfSrc);
			} else {
				jsonStr = gson_.toJson(src);
			}
		} catch (Exception ex) {
			logger_.warn("目标对象 " + src.getClass().getName() + " 转换 JSON 字符串时，发生异常！", ex);
			if (src instanceof Collection || src instanceof Iterator || src instanceof Enumeration || src.getClass().isArray()) {
				jsonStr = EMPTY_JSON_ARRAY;
			} else {
				jsonStr = EMPTY_JSON;
			}
		}
		return jsonStr;
	}

	/**
	 * @description 将给定的 {@code JSON} 字符串转换成指定的类型对象。
	 * @usingWarning 不会抛出任何异常
	 * @param json
	 * @param typeToken
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, TypeToken<T> typeToken) {
		T obj = null;
		if (null != json || null == typeToken) {
			try {
				obj = (T) gson_.fromJson(json, typeToken.getType());
			} catch (Exception ex) {
				logger_.error(json + " 无法转换为 " + typeToken.getRawType().getName() + " 对象!", ex);
			}
		}
		return obj;
	}
	
	/**
	 * @description 将给定的 {@code JSON} 字符串转换成指定的类型对象。<strong>此方法通常用来转换普通的 {@code JavaBean}
     * 对象。</strong>
	 * @usingWarning 不会抛出任何异常
	 * @param json
	 * @param classOfT
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	public static <T> T fromJson(String json, Class<T> classOfT) {
		T obj = null;
		if (null != json || null == classOfT) {
			try {
				obj = gson_.fromJson(json, classOfT);
			} catch (Exception ex) {
				logger_.error(json + " 无法转换为 " + classOfT.getName() + " 对象!", ex);
			}
		}
		return obj;
	}
	
	/**
	 * @description 将json格式转换成list对象
	 * @usingWarning 
	 * @param json
	 * @return
	 */
	public static <T> List<T> jsonToList(String json) {
		return fromJson(json, new TypeToken<List<T>>(){});
	}
	
	
	
	/**
	 * @description 将json格式转换成map对象
	 * @usingWarning 
	 * @param json
	 * @return
	 */
	public static <K, V> Map<K, V> jsonToMap(String json) {
		return fromJson(json, new TypeToken<Map<K, V>>(){});
	}

	/**unicode解码*/
	public static String unicodeEsc2Unicode(String unicodeStr) {
		if (unicodeStr == null) {
			return null;
		}
		StringBuffer retBuf = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if ((i < maxLoop - 5)
						&& ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
								.charAt(i + 1) == 'U')))
					try {
						retBuf.append((char) Integer.parseInt(
								unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						retBuf.append(unicodeStr.charAt(i));
					}
				else
					retBuf.append(unicodeStr.charAt(i));
			} else {
				retBuf.append(unicodeStr.charAt(i));
			}
		}
		return retBuf.toString();
	}
}

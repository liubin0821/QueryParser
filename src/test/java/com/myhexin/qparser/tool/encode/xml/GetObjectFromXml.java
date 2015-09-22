package com.myhexin.qparser.tool.encode.xml;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



/*
 * 根据指定的XML文件(必须是按照本程序模板设定格式)信息,转换生成成对应的类对象
 * 使用条件:
 * 1. 相应对象必须有默认的"无参构造函数"
 * 2. 对于static final的属性是不能修改的
 * 3. xml文件里的id不能有重名,重名会出现覆盖, 重名且出现在后面的id,被前面的覆盖
 * 
 *    示例:new GetObjectFromXml("文件名称").getObject("XML文件里对应的id");
 */
public class GetObjectFromXml {
	private static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(GetObjectFromXml.class.getName());   
	private Map<String, Object> bufferRead = new HashMap<String, Object>();
	private Element root;
	private String fileName_ = "example.xml";
	private String mainObjectId = "object1";

	/*
	 * 函数调用接口, 内部不使用
	 *     首先检查类型为bean的是否包含改目标"getBean(id)", 找到则结束
	 *     然后检查类型为container的是否包含"getContainer(id)"
	 */
	public Object getObject(String id) {
		mainObjectId = id;
		Object o = getBean(mainObjectId);
		if (o != null) {
			return o;
		} else {
			return getContainer(mainObjectId);
		}
	}
	
	public Object getObject(){
		return getObject(mainObjectId);
	}

	public GetObjectFromXml(String fileName) {
		if (fileName == null || fileName.equals("")) {
			System.err.println("读取的文件名为空");
			return;
		}
		fileName_ = fileName;
		try {
			root = new SAXReader().read(fileName_).getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	public final Object getList(String id){
		return getList(id,root);
	}
	
	//只能获取list里面装bean的情况
	@SuppressWarnings("unchecked")
	public Object getList(String id,Element node) {

		if (bufferRead.containsKey(id)) {
			return bufferRead.get(id);
		}

		for (Iterator<?> i = node.elementIterator("container"); i.hasNext();) {
			Element element = (Element) i.next();
			if (element.attributeValue("id").equals(id)) {

				try {
					String clazz = element.attributeValue("class"); // get
					if (Class.forName(clazz).getInterfaces()[0] != List.class)
					{
						throw new Exception(clazz+"不是list,不能调用getList");
					}
					Object o = Class.forName(clazz).newInstance();
					bufferRead.put(id, o); // put into the map
					for (Iterator<?> j = element.elementIterator("bean"); j
							.hasNext();) {
						Element val = (Element) j.next();

						try {
							clazz = val.attributeValue("class"); // get
							Object subObject = Class.forName(clazz).newInstance();
							((List<Object>)o).add(subObject);
							for (Iterator<?> k = val.elementIterator("property"); k
									.hasNext();) {
								Element attr = (Element) k.next();
								String name = attr.attributeValue("name");
								String ref = attr.attributeValue("ref");
								String refType = attr.attributeValue("refType");
								String value = attr.getStringValue();
								String valueClass = attr.attributeValue("class");
								Object pro = null;
								Class<?> type2 = null;
								if (ref != null && !ref.equals("")) {
									if (refType.equals("bean")) {
										pro = getBean(ref);
									} else if (refType.equals("container")) {
										pro = getContainer(ref);
									} else {
										throw new Exception(
												"格式错误ref和refType必须同时存在");
									}
									type2 = pro.getClass();
								} else {
									type2 = getClassType(valueClass);
									pro = changeStringToOther(type2, value);
								}
								ReflectionUtils.setFieldValue(subObject,name,pro);							

							}							
						} catch (InstantiationException e) {
							logger_.error("配置文件中,class对应的类没有 \"无参构造函数\"");
							logger_.error(e.getMessage());
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							logger_.error("对应变量的setXX函数不存在");
							logger_.error(e.getMessage());
							e.printStackTrace();	
						}											
					}
					return o;		
				} catch (Exception e) {
					logger_.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	
	}
	public final Object getBean(String id){
		return getBean(id,null);
	}
	
	public Object getBean(String id,Object outerObject) {
		if (bufferRead.containsKey(id)) {
			return bufferRead.get(id);
		}
		for (Iterator<?> i = root.elementIterator("bean"); i.hasNext();) {
			Element element = (Element) i.next();
			if (element.attributeValue("id").equals(id)) {
				String clazz = null;
				try {
					clazz = element.attributeValue("class"); // get
					Class<?> temp = Class.forName(clazz);
					//是接口或者抽象类
					if (temp.isInterface()
							|| Modifier.isAbstract(temp.getModifiers())) {
						return null;
					}
					Object o = null;
					//访问私有构造函数
					Constructor<?> con = null;
					try {
						con = temp.getDeclaredConstructor();
						con.setAccessible(true);
						o = con.newInstance();
					} catch (NoSuchMethodException e) {
						try {
							if (outerObject==null) 
								throw e;
							//非静态内部类处理
							con = temp.getDeclaredConstructor(outerObject
									.getClass());
							con.setAccessible(true);
							o = con.newInstance(outerObject);
						} catch (NoSuchMethodException e1) {
							throw new InstantiationException();
						}

					}
														
					//Class.forName(clazz).newInstance();
					bufferRead.put(id, o); // put into the map
					for (Iterator<?> j = element.elementIterator("property"); j
							.hasNext();) {
						Element attr = (Element) j.next();
						String name = attr.attributeValue("name");
						String ref = attr.attributeValue("ref");
						String refType = attr.attributeValue("refType");
						String value = attr.getStringValue();
						String valueClass = attr.attributeValue("class");
						String enumClass = attr.attributeValue("enumClass");//新添加处理EnumSet
						Object pro = null;
						Class<?> type2 = null;
						if (ref != null && !ref.equals("")) {
							if (refType.equals("bean")) {
								pro = getBean(ref,o);//为了处理非静态内部类  所以要传入父类
							} else if (refType.equals("container")) {
								pro = getContainer(ref,o);
							} else {
								throw new Exception("格式错误ref和refType必须同时存在");
							}
							type2 = pro.getClass();
						} else if (enumClass == null || enumClass.equals("")) {
							type2 = getClassType(valueClass);
							pro = changeStringToOther(type2, value);
						} else {
							pro = getEnumSet(Class.forName(enumClass), value);
						}
						ReflectionUtils.setFieldValue(o, name, pro);//设置值
					}
					return o;

				} catch (InstantiationException e) {
					logger_.error("配置文件中," + clazz + "对应的类没有 \"无参构造函数\"");
					logger_.error(e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					logger_.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-19 下午12:18:49
	 * @description:  得到getEnumSet类型的对象,配合GetXmlFromObject使用 	
	 * @param type2
	 * @param value
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	private Object getEnumSet(Class<?> enumClass, String values) {
		boolean init = false;
		Object o = null;
		for (String value : values.split(","))
			if (!init)
				o = java.util.EnumSet.of(Enum.valueOf(
						enumClass.asSubclass(Enum.class), value));
			else
				((EnumSet) o).add(Enum.valueOf(
						enumClass.asSubclass(Enum.class), value));
		return o;
	}
	
	public final Object getContainer(String id){
		return getContainer(id,null);
	}
	public Object getContainer(String id,Object outerObject) {
		if (bufferRead.containsKey(id)) {
			return bufferRead.get(id);
		}

		for (Iterator<?> i = root.elementIterator("container"); i.hasNext();) {
			Element element = (Element) i.next();
			if (element.attributeValue("id").equals(id)) {
				try {
					String clazz = element.attributeValue("class"); // get
					Object o = Class.forName(clazz).newInstance();
					bufferRead.put(id, o); // put into the map
					for (Iterator<?> j = element.elementIterator("value"); j
							.hasNext();) {
						Element val = (Element) j.next();

						String keyRef = val.attributeValue("keyRef");
						String keyRefType = val.attributeValue("keyRefType");
						String keyClass = val.attributeValue("keyClass");
						String keyValue = val.attributeValue("keyValue");
						String valueRef = val.attributeValue("valueRef");
						String valueRefType = val
								.attributeValue("valueRefType");
						String valueClass = val.attributeValue("valueClass");
						String valueValues = val.attributeValue("valueValues");
						Class<?> typeKey = null, typeValue = null;
						Object key = null;
						if (keyRef != null && !keyRef.equals("")) {
							if (keyRefType.equals("bean")) {
								key = getBean(keyRef,outerObject);
							} else if (keyRefType.equals("container")) {
								key = getContainer(keyRef,outerObject);
							} else {
								throw new Exception(
										"格式错误keyRef和keyRefType必须同时存在");
							}
							typeKey = key.getClass();
						} else {
							typeKey = Class.forName(keyClass);
							key = changeStringToOther(typeKey, keyValue);
						}

						Object values = null;
						if (valueRef != null && !valueRef.equals("")) {

							if (valueRefType.equals("bean")) {
								values = getBean(valueRef,outerObject);
							} else if (valueRefType.equals("container")) {
								values = getContainer(valueRef,outerObject);
							} else {
								throw new Exception(
										"格式错误valueRef和valueRefType必须同时存在");
							}
							typeValue = values.getClass();
						} else if (valueClass != null) {
							typeValue = Class.forName(valueClass);
							values = changeStringToOther(typeValue, valueValues);
						}

						String methodName = "add";

						if (o.getClass().getInterfaces()[0] == Map.class) {
							methodName = "put";
							Class<?>[] types = { Object.class, Object.class };
							Method m = o.getClass()
									.getMethod(methodName, types);
							m.invoke(o, key, values);
						} else {
							Class<?>[] types = { Object.class };
							Method m = o.getClass()
									.getMethod(methodName, types);
							m.invoke(o, key);
						}
					}
					return o;				
				} catch (Exception e) {
					logger_.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// 根据字符串返回对应基础数据类型
	public Class<?> getClassType(String type) throws ClassNotFoundException {
		if (type.equals("int")) {
			return int.class;
		} else if (type.equals("double")) {
			return double.class;
		} else if (type.equals("boolean")) {
			return boolean.class;
		} else if (type.equals("float")) {
			return float.class;
		} else if (type.equals("long")) {
			return long.class;
		} else if (type.equals("shot")) {
			return short.class;
		} else if (type.equals("char")) {
			return char.class;
		} else if (type.equals("byte")) {
			return byte.class;
		} else if (type.equals("String")) {
			return String.class;
		}
		return Class.forName(type);
	}


	@SuppressWarnings("unchecked")
	private Object changeStringToOther(Class<?> type, String value) {
		 if (type == StringBuilder.class ) {
			return new StringBuilder(value);
		} else if (value == null || value.equals("")) {
			return null;
		} else if (type == String.class) {
			return value;
		} else if (type == Integer.class || type == int.class) {
			return Integer.parseInt(value);
		} else if (type == Double.class || type == double.class) {
			return Double.parseDouble(value);
		} else if (type == Boolean.class || type == boolean.class) {
			return Boolean.parseBoolean(value);
		} else if (type == Float.class || type == float.class) {
			return Float.parseFloat(value);
		} else if (type == Short.class || type == short.class) {
			return Short.parseShort(value);
		} else if (type == Long.class || type == long.class) {
			return Long.parseLong(value);
		} else if (type == Character.class || type == char.class) {
			return new Character(value.charAt(0));
		} else if (type.isEnum()) {
			return Enum.valueOf(type.asSubclass(Enum.class), value);
		}else {
			// 尚未支持的类型
			return value;
		}

	}
	
//	public static void main(String[] args){
//		new GetObjectFromXml("-MatchSyntacticPatternsBreadthFirstAfter.xml").getList("list1");
//	}
}

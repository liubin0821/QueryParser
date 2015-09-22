package xml;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.myhexin.qparser.tool.encode.xml.ReflectionUtils;



/*
 * 根据指定的XML文件(必须是按照本程序模板设定格式)信息,转换生成成对应的类对象
 * 使用条件:
 * 1. 相应对象必须有默认的"无参构造函数"
 * 2. 相应的属性必须有对应的setXX函数
 * 3. xml文件里的id不能有重名,重名会出现覆盖, 重名且出现在后面的id,被前面的覆盖
 * 
 *    示例:new GetObjectFromXml("文件名称").getObject("XML文件里对应的id");
 */
public class GetObjectFromXml111 {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(GetObjectFromXml111.class.getName());   
	private Map<String, Object> bufferRead = new HashMap<String, Object>();
	private Element root;
	private String fileName_ = "example.xml";

	/*
	 * 函数调用接口, 内部不使用
	 *     首先检查类型为bean的是否包含改目标"getBean(id)", 找到则结束
	 *     然后检查类型为container的是否包含"getContainer(id)"
	 */
	public Object getObject(String id) {
		Object o = getBean(id);
		if (o != null) {
			return o;
		} else {
			return getContainer(id);
		}
	}

	public GetObjectFromXml111(String fileName) {
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

		for (Iterator<?> i = root.elementIterator("bean"); i.hasNext();) {
			Element element = (Element) i.next();
			getBean(element.attributeValue("id"));

		}
	}

	public Object getList(String id){
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
//								String methodName = "set"
//										+ name.substring(0, 1).toUpperCase()
//										+ name.substring(1);
//
//								Class<?>[] types = { type2 };
//								Method m = null;
//								try {
//									m = subObject.getClass().getMethod(methodName, types);
//								} catch (NoSuchMethodException e) {
//									types[0] = getObjectOrPrimType(type2);
//									m = subObject.getClass().getMethod(methodName, types);
//								}
//								if (pro != null) {
//									m.invoke(subObject, pro);
//								}

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
	public Object getBean(String id) {
		if (bufferRead.containsKey(id)) {
			return bufferRead.get(id);
		}
		for (Iterator<?> i = root.elementIterator("bean"); i.hasNext();) {
			Element element = (Element) i.next();
			if (element.attributeValue("id").equals(id)) {
				try {
					String clazz = element.attributeValue("class"); // get
					Object o = Class.forName(clazz).newInstance();
					bufferRead.put(id, o); // put into the map
					for (Iterator<?> j = element.elementIterator("property"); j
							.hasNext();) {
						Element attr = (Element) j.next();
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
						ReflectionUtils.setFieldValue(o,name,pro);

					}
					return o;

				} catch (InstantiationException e) {
					logger_.error("配置文件中,class对应的类没有 \"无参构造函数\"");
					logger_.error(e.getMessage());
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					logger_.error("对应变量的setXX函数不存在");
					logger_.error(e.getMessage());
					e.printStackTrace();	
				}catch (Exception e) {
					logger_.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Object getContainer(String id) {
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
								key = getBean(keyRef);
							} else if (keyRefType.equals("container")) {
								key = getContainer(keyRef);
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
								values = getBean(valueRef);
							} else if (valueRefType.equals("container")) {
								values = getContainer(valueRef);
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

	/*
	 * 为检查是否有set函数时使用: 
	 * 		如果为基础类型转换为对应的对象类型 
	 * 		如果为对象类型,转换为对应的基础类型
	 */
	public Class<?> getObjectOrPrimType(Class<?> type) {
		if (type == int.class) {
			return Integer.class;
		} else if (type == double.class) {
			return Double.class;
		} else if (type == boolean.class) {
			return Boolean.class;
		} else if (type == float.class) {
			return Float.class;
		} else if (type == long.class) {
			return Long.class;
		} else if (type == short.class) {
			return Short.class;
		} else if (type == char.class) {
			return Character.class;
		} else if (type == byte.class) {
			return Byte.class;
		} else if (type == Integer.class) {
			return int.class;
		} else if (type == Double.class) {
			return double.class;
		} else if (type == Boolean.class) {
			return boolean.class;
		} else if (type == Float.class) {
			return float.class;
		} else if (type == Long.class) {
			return long.class;
		} else if (type == Short.class) {
			return short.class;
		} else if (type == Character.class) {
			return char.class;
		} else if (type == Byte.class) {
			return byte.class;
		} else {
			return type;
		}
	}

	@SuppressWarnings("unchecked")
	private Object changeStringToOther(Class<?> type, String value) {
		if (value == null || value.equals("")) {
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
		} else {
			// 尚未支持的类型
			return value;
		}

	}
}

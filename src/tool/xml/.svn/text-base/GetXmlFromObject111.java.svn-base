package xml;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/*
 * 把内存里的对象转换成xml文件,
 * 
 *    示例:new GetXmlFromObject("目标文件名称").createXML(已经存在的对象);
 */
public class GetXmlFromObject111 {
	private Map<Object, String> bufferWrite = new HashMap<Object, String>();
	private String fileName_ = "example_result.xml";
	private int beanId = 1;
	private int containerId = 1;
	private int listId = 1;
	private int setId = 1;

	private String createXML(Element root, Object o) {
		if (o == null) {
			System.out.println("传入对象为空");
			return null;
		}
		Class<?> type = o.getClass().getInterfaces().length > 0 ? o.getClass()
				.getInterfaces()[0] : null;

		if (type != null && type == Map.class) {
			createXMLMap(root, o);
		} else if (type != null && type == List.class) {
			createXMLList(root, o);
		} else if (type != null && type == Set.class) {
			createXMLSet(root, o);
		} else {
			createXMLBean(root, o);
		}
		return null;
	}

	private String createXMLBean(Element root, Object o) {
		if (bufferWrite.containsKey(o)) {
			return bufferWrite.get(o);
		}

		Element bean = DocumentHelper.createElement("bean");
		String id = "bean" + beanId++;
		bufferWrite.put(o, id);
		bean.addAttribute("id", id);
		bean.addAttribute("class", o.getClass().getName());
		ArrayList<Field> fields = new ArrayList<Field>();

		Class<?> temp = o.getClass();
		while (temp != Object.class) {
			fields.addAll(Arrays.asList(temp.getDeclaredFields()));
			temp = temp.getSuperclass();
		}
		for (int i = 0, len = fields.size(); i < len; i++) {
			String varName = fields.get(i).getName();
			//临时添加
			//if(varName.equals("this$0")) continue;
			try {
				boolean accessFlag = fields.get(i).isAccessible();
				fields.get(i).setAccessible(true);
				Object o1 = fields.get(i).get(o);
				// 如果属性为空不写入xml
				if (o1 == null) {
					continue;
				}
				fields.get(i).setAccessible(accessFlag);
				Element property = DocumentHelper.createElement("property");
				Class<?> t = o1.getClass();
				property.addAttribute("name", varName);
				if (t == Integer.class || t == Double.class || t == Float.class
						|| t == Short.class || t == Boolean.class
						|| t == Character.class || t == Long.class
						|| t == String.class || t == int.class
						|| t == short.class || t == long.class
						|| t == float.class || t == double.class
						|| t == char.class || t == boolean.class) {
					property.addAttribute("class", t.getName());
					property.setText(o1.toString());
				} else if (o1 instanceof Enum) {
					property.addAttribute("class", t.getName());
					property.setText(((Enum) o1).name());
				} else {
					Class<?> t3 = t.getInterfaces().length < 1 ? null : t
							.getInterfaces()[0];
					if (t3 == Map.class) {
						property.addAttribute("refType", "container");
						property.addAttribute("ref", createXMLMap(root, o1));
					} else if (t3 == List.class) {
						property.addAttribute("refType", "container");
						property.addAttribute("ref", createXMLList(root, o1));
					} else if (t3 == Set.class) {
						property.addAttribute("refType", "container");
						property.addAttribute("ref", createXMLSet(root, o1));
					} else {
						property.addAttribute("refType", "bean");
						property.addAttribute("ref", createXMLBean(root, o1));
					}

				}
				bean.add(property);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
		root.add(bean);
		return id;

	}

	private String createXMLList(Element root, Object o) {
		if (bufferWrite.containsKey(o)) {
			return bufferWrite.get(o);
		}

		Element list = DocumentHelper.createElement("container");
		String id = "list" + listId++;
		bufferWrite.put(o, id);
		list.addAttribute("id", id);
		list.addAttribute("class", o.getClass().getName());

		List l = (List) o;
		for (Iterator i = l.iterator(); i.hasNext();) {
			Object os = i.next();
			Element value = DocumentHelper.createElement("value");
			Class<?> t = os.getClass();
			if (t == String.class || t == Integer.class || t == Double.class
					|| t == Float.class || t == Short.class
					|| t == Boolean.class || t == Character.class
					|| t == Long.class) {
				value.addAttribute("keyClass", t.getName());
				value.addAttribute("keyValue", os.toString());
			} else if (os instanceof Enum) {
				value.addAttribute("class", t.getName());
				value.addAttribute("keyValue", ((Enum) os).name());
			} else {
				Class<?> t3 = t.getInterfaces().length < 1 ? null : t
						.getInterfaces()[0];
				if (t3 == Map.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLMap(root, os));
				} else if (t3 == List.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLList(root, os));
				} else if (t3 == Set.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLSet(root, os));
				} else {
					value.addAttribute("keyRefType", "bean");
					value.addAttribute("keyRef", createXMLBean(root, os));
				}
			}
			list.add(value);
		}
		root.add(list);

		return id;

	}

	private String createXMLSet(Element root, Object o) {
		if (bufferWrite.containsKey(o)) {
			return bufferWrite.get(o);
		}

		Element set = DocumentHelper.createElement("container");
		String id = "set" + setId++;
		bufferWrite.put(o, id);
		set.addAttribute("id", id);
		set.addAttribute("class", o.getClass().getName());

		Set l = (Set) o;
		for (Iterator i = l.iterator(); i.hasNext();) {
			Object os = i.next();
			Element value = DocumentHelper.createElement("value");
			Class<?> t = os.getClass();
			if (t == String.class || t == Integer.class || t == Double.class
					|| t == Float.class || t == Short.class
					|| t == Boolean.class || t == Character.class
					|| t == Long.class) {
				value.addAttribute("keyClass", t.getName());
				value.addAttribute("keyValue", os.toString());
			} else if (os instanceof Enum) {
				value.addAttribute("class", t.getName());
				value.addAttribute("keyValue", ((Enum) os).name());
			} else {
				Class<?> t3 = t.getInterfaces().length < 1 ? null : t
						.getInterfaces()[0];
				if (t3 == Map.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLMap(root, os));
				} else if (t3 == List.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLList(root, os));
				} else if (t3 == Set.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLSet(root, os));
				} else {
					value.addAttribute("keyRefType", "bean");
					value.addAttribute("keyRef", createXMLBean(root, os));
				}
			}
			set.add(value);
		}
		root.add(set);
		return id;

	}

	private String createXMLMap(Element root, Object o) {
		if (bufferWrite.containsKey(o)) {
			return bufferWrite.get(o);
		}

		Element map = DocumentHelper.createElement("container");
		String id = "container" + containerId++;
		bufferWrite.put(o, id);
		map.addAttribute("id", id);
		map.addAttribute("class", o.getClass().getName());

		Map m = (Map) o;
		Set set = m.keySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Object keyObject = iterator.next();
			Element value = DocumentHelper.createElement("value");
			Class<?> t = keyObject.getClass();
			if (t == String.class || t == Integer.class || t == Double.class
					|| t == Float.class || t == Short.class
					|| t == Boolean.class || t == Character.class
					|| t == Long.class) {
				value.addAttribute("keyClass", t.getName());
				value.addAttribute("keyValue", keyObject.toString());
			} else if (keyObject instanceof Enum) {
				value.addAttribute("class", t.getName());
				value.addAttribute("keyValue", ((Enum) keyObject).name());
			} else {
				Class<?> t3 = t.getInterfaces().length < 1 ? null : t
						.getInterfaces()[0];
				if (t3 == Map.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLMap(root, keyObject));
				} else if (t3 == List.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLList(root, keyObject));
				} else if (t3 == Set.class) {
					value.addAttribute("keyRefType", "container");
					value.addAttribute("keyRef", createXMLSet(root, keyObject));
				} else {
					value.addAttribute("keyRefType", "bean");
					value.addAttribute("keyRef", createXMLBean(root, keyObject));
				}
			}

			Object valueObject = m.get(keyObject);
			Class<?> t2 = valueObject.getClass();
			if (t2 == String.class || t2 == Integer.class || t2 == Double.class
					|| t2 == Float.class || t2 == Short.class
					|| t2 == Boolean.class || t2 == Character.class
					|| t2 == Long.class) {
				value.addAttribute("valueClass", t2.getName());
				value.addAttribute("valueValues", valueObject.toString());
			} else if (valueObject instanceof Enum) {
				value.addAttribute("class", t2.getName());
				value.addAttribute("valueValues", ((Enum) valueObject).name());
			} else {
				Class<?> t3 = t2.getInterfaces().length < 1 ? null : t2
						.getInterfaces()[0];
				if (t3 == Map.class) {
					value.addAttribute("valueRefType", "container");
					value.addAttribute("valueRef",
							createXMLMap(root, valueObject));
				} else if (t3 == List.class) {
					value.addAttribute("valueRefType", "container");
					value.addAttribute("valueRef",
							createXMLList(root, valueObject));
				} else if (t3 == Set.class) {
					value.addAttribute("valueRefType", "container");
					value.addAttribute("valueRef",
							createXMLSet(root, valueObject));
				} else {
					value.addAttribute("valueRefType", "bean");
					value.addAttribute("valueRef",
							createXMLBean(root, valueObject));
				}
			}
			map.add(value);
		}
		root.add(map);

		return id;
	}

	public GetXmlFromObject111(String fileName) {
		super();
		if (fileName != null && !fileName.equals("")) {
			fileName_ = fileName;
		}

	}

	public void createXML(Object o) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("beans");

		createXML(root, o);
		try {
			// 注意这里使用的是FileOutputStream，而不是FileWriter,因为涉及到编码问题
			org.dom4j.io.XMLWriter xmlWriter = new org.dom4j.io.XMLWriter(
					new FileOutputStream(fileName_));
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

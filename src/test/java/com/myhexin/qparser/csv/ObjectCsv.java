package com.myhexin.qparser.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectCsv {
	/**
	 * 根据对象生成csv文件
	 * 
	 * @param classObj
	 *            对象的class
	 * @param csvPath
	 *            csv文件路径
	 */
	public void ObjectToCsv(Class classObj, String csvPath) {
		try {
			File file = new File(csvPath);
			if (!file.isFile()) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file), "UTF-8"));
				bw.write(",flag,col");
				bw.newLine();
				Field[] fields = classObj.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					String fieldName = fields[i].getName();
					String type = fields[i].getType().toString();
					// 忽略成员变量
					if (!fields[i].toString().contains("static")) {
						// 对List的处理
						if (type.endsWith("List")) {
							String genericType = fields[i].getGenericType()
									.toString();
							String tClassName = genericType.substring(
									genericType.indexOf("<") + 1,
									genericType.indexOf(">"));
							if (tClassName.contains("<")) {
								bw.write(fieldName + ",N");
							} else if (tClassName.contains("String")) {
								bw.write(fieldName + ",Y,param1;param2;...");
							} else if (tClassName.contains("$")) {
								bw.write(fieldName + ",N");
							} else if (!tClassName.contains("java")) {
								bw.write(fieldName + ",N");
								String writePath = genericObjToCsv(fieldName,
										tClassName, csvPath);
								bw.write("," + writePath + "@1;2...");
							}
						}
						// 对Map的处理
						else if (type.endsWith("Map")) {
							bw.write(fieldName + ",N");
							String genericType = fields[i].getGenericType()
									.toString();
							String kClassName = genericType.substring(
									genericType.indexOf("<") + 1,
									genericType.indexOf(","));
							String vClassName = genericType.substring(
									genericType.indexOf(",") + 2,
									genericType.indexOf(">"));
							if (!genericType.contains("$")) {
								boolean stringKey = kClassName
										.endsWith("String");
								boolean stringValue = vClassName
										.endsWith("String");
								if (stringKey) {
									if (stringValue) {
										bw.write(",key1:value1;key2:value2;...");
									} else if (!vClassName.contains("java")) {
										String writePath = genericObjToCsv(
												fieldName, vClassName, csvPath);
										bw.write(",key1:" + writePath
												+ "@1;key2:2;...");
									}
								} else {
									if (stringValue) {
										String writePath = genericObjToCsv(
												fieldName, kClassName, csvPath);
										bw.write("," + writePath
												+ "@1:value1;2:value2;...");
									} else if (!kClassName.contains("java")) {
										String writePathKey = genericObjToCsv(
												fieldName, kClassName, csvPath);
										String writePathValue = genericObjToCsv(
												fieldName, vClassName, csvPath);
										bw.write("," + writePathKey + "@1:"
												+ writePathValue + "@1;2:2;...");
									}
								}
							}
						}
						// 对枚举的处理
						else if (type.contains("$")) {
							bw.write(fieldName + ",N");
						}
						// 如果是一个对象类型，则生成一个新对象的csv
						else if (type.contains("class")
								&& !type.contains("java") && type.contains(".")) {
							bw.write(fieldName + ",N");
							String writePath = getPath(fieldName, csvPath,
									"write");
							bw.write("," + writePath + "@1");
							String path = getPath(fieldName, csvPath);
							String className = type.substring(type
									.lastIndexOf(" ") + 1);
							ObjectToCsv(Class.forName(className), path);
						} else {
							bw.write(fieldName + ",Y");
						}
						bw.newLine();
					}
				}
				bw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private String genericObjToCsv(String fieldName, String className,
			String csvPath) {
		String writePath = null;
		try {
			String name = className.substring(className.lastIndexOf(".") + 1);
			writePath = getPath(name, csvPath, "write");
			String path = getPath(name, csvPath);
			ObjectToCsv(Class.forName(className), path);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return writePath;
	}

	private String getPath(String name, String csvPath) {
		return getPath(name, csvPath, null);
	}

	private String getPath(String name, String csvPath, String flag) {
		if (flag == null) {
			String path = csvPath.substring(0, csvPath.lastIndexOf("/") + 1)
					+ name + ".csv";
			return path;
		} else if (flag.equals("write")) {
			String writePath = csvPath.substring(csvPath.indexOf("src"),
					csvPath.lastIndexOf("/") + 1) + name + ".csv";
			return writePath;
		} else {
			return null;
		}
	}

	/**
	 * 缺省参数index，则取第一列数据
	 * 
	 * @param classObj
	 * @param csvPath
	 * @return
	 */
	public Object CsvToObject(Class classObj, String csvPath) {
		return CsvToObject(classObj, csvPath, 1);
	}

	/**
	 * 根据csv文件生成对象
	 * 
	 * @param classObj
	 *            对象的class
	 * @param csvPath
	 *            csv文件路径
	 * @param index
	 *            序列数，取第几列数据
	 * @return
	 */
	public Object CsvToObject(Class classObj, String csvPath, int index) {
		try {
			Object obj = classObj.newInstance();
			Field[] fields = classObj.getFields();
			Method[] methods = classObj.getMethods();
			ReadCsv csv = new ReadCsv(csvPath);
			for (int i = 1; i < csv.getRowNum(); i++) {
				String csvRow = csv.getRow(i);
				String[] csvRows = csvRow.split(",");
				// 如果flag为N 则不赋值
				if (csvRows[1].equals("Y")) {
					String fieldName = csvRows[0];
					String value = csvRows[1 + index];
					setParam(value, fields, obj, fieldName);
					setParam(value, methods, obj, fieldName);
				}
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private void setParam(String value, Method[] methods, Object obj,
			String fieldName) {
		try {
			String name = "set" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			for (Method method : methods) {
				if (method.getName().equals(name)) {
					if (value.equals("null")) {
						value = "";
					}
					Class[] parameterC = method.getParameterTypes();
					if (parameterC[0] == int.class) {
						method.invoke(obj, (Integer.valueOf(value)).intValue());
						break;
					} else if (parameterC[0] == float.class) {
						method.invoke(obj, (Float.valueOf(value)).floatValue());
						break;
					} else if (parameterC[0] == double.class) {
						method.invoke(obj,
								(Double.valueOf(value)).doubleValue());
						break;
					} else if (parameterC[0] == byte.class) {
						method.invoke(obj, (Byte.valueOf(value)).byteValue());
						break;
					} else if (parameterC[0] == boolean.class) {
						Boolean bo = booleanFunction(value);
						method.invoke(obj, (bo).booleanValue());
						break;
					} else if (parameterC[0].toString().endsWith("List")) {
						String genericString = method.toGenericString();
						String tClassName = genericString.substring(
								genericString.indexOf("<") + 1,
								genericString.indexOf(">"));
						List list = new ArrayList();
						if (tClassName.endsWith("String")) {
							String[] listValues = value.split(";");
							for (int k = 0; k < listValues.length; k++) {
								list.add(listValues[k]);
							}
							method.invoke(obj, list);
							break;
						} else if (tClassName.contains("$")) {
							break;
						} else {
							String classPath = System.getProperty("user.dir")
									+ "/"
									+ value.substring(0, value.indexOf("@"));
							String[] classIndexLists = value.substring(
									value.indexOf("@") + 1).split(";");
							for (int k = 0; k < classIndexLists.length; k++) {
								int classIndex = Integer
										.parseInt(classIndexLists[k]);
								Object classObject = CsvToObject(
										Class.forName(tClassName), classPath,
										classIndex);
								list.add(classObject);
							}
							method.invoke(obj, list);
							break;
						}
					} else if (parameterC[0].toString().endsWith("Map")) {
						String genericString = method.toGenericString();
						String kClassName = genericString.substring(
								genericString.indexOf("<") + 1,
								genericString.indexOf(","));
						String vClassName = genericString.substring(
								genericString.indexOf(",") + 2,
								genericString.indexOf(">"));
						boolean stringKey = kClassName.endsWith("String");
						boolean stringValue = vClassName.endsWith("String");
						Map map = new HashMap();
						if (kClassName.contains("$")
								|| vClassName.contains("$")) {
							break;
						}
						if (stringKey) {
							if (stringValue) {
								String[] listMaps = value.split(";");
								for (String listMap : listMaps) {
									String[] keyValue = listMap.split(":");
									map.put(keyValue[0], keyValue[1]);
								}
								method.invoke(obj, map);
								break;
							} else {
								String pathValue = value.substring(
										value.indexOf(":") + 1,
										value.indexOf("@"));
								String classPathValue = System
										.getProperty("user.dir")
										+ "/"
										+ pathValue;
								value = value.replace(pathValue + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexValue = Integer
											.parseInt(classKeyValue[1]);
									Object classObjectValue = CsvToObject(
											Class.forName(vClassName),
											classPathValue, classIndexValue);
									map.put(classKeyValue[0], classObjectValue);
								}
								method.invoke(obj, map);
								break;
							}
						} else {
							if (stringValue) {
								String pathKey = value.substring(0,
										value.indexOf("@"));
								String classPathKey = System
										.getProperty("user.dir")
										+ "/"
										+ pathKey;
								value = value.replace(pathKey + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexKey = Integer
											.parseInt(classKeyValue[0]);
									Object classObjectKey = CsvToObject(
											Class.forName(kClassName),
											classPathKey, classIndexKey);
									map.put(classObjectKey, classKeyValue[1]);
								}
								method.invoke(obj, map);
								break;
							} else {
								String pathKey = value.substring(0,
										value.indexOf("@"));
								String pathValue = value.substring(
										value.indexOf(":") + 1,
										value.indexOf("@"));
								String classPathKey = System
										.getProperty("user.dir")
										+ "/"
										+ pathKey;
								String classPathValue = System
										.getProperty("user.dir")
										+ "/"
										+ pathValue;
								value = value.replace(pathKey + "@", "");
								value = value.replace(pathValue + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexKey = Integer
											.parseInt(classKeyValue[0]);
									int classIndexValue = Integer
											.parseInt(classKeyValue[1]);
									Object classObjectKey = CsvToObject(
											Class.forName(kClassName),
											classPathKey, classIndexKey);
									Object classObjectValue = CsvToObject(
											Class.forName(vClassName),
											classPathValue, classIndexValue);
									map.put(classObjectKey, classObjectValue);
								}
								method.invoke(obj, map);
								break;
							}
						}
					} else if (parameterC[0].toString().contains("$")) {
						break;
					} else if (parameterC[0].toString().contains("class")
							&& !parameterC[0].toString().endsWith("String")) {
						String classValue = parameterC[0].toString();
						String classPath = System.getProperty("user.dir") + "/"
								+ value.substring(0, value.indexOf("@"));
						int classIndex = Integer.parseInt(value.substring(value
								.indexOf("@") + 1));
						Object classObject = CsvToObject(
								Class.forName(classValue.substring(classValue
										.indexOf((" ")) + 1)), classPath,
								classIndex);
						method.invoke(obj, classObject);
						break;
					} else {
						method.invoke(obj, parameterC[0].cast(value));
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void setParam(String value, Field[] fields, Object obj,
			String fieldName) {
		try {
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					if (value.equals("null")) {
						value = "";
					}
					Class type = field.getType();
					if (type == int.class) {
						field.set(obj, (Integer.valueOf(value)).intValue());
						break;
					} else if (type == float.class) {
						field.set(obj, (Float.valueOf(value)).floatValue());
						break;
					} else if (type == double.class) {
						field.set(obj, (Double.valueOf(value)).doubleValue());
						break;
					} else if (type == byte.class) {
						field.set(obj, (Byte.valueOf(value)).byteValue());
						break;
					} else if (type == boolean.class) {
						Boolean bo = booleanFunction(value);
						field.set(obj, (bo).booleanValue());
						break;
					} else if (type.toString().endsWith("List")) {
						String genericString = field.getGenericType()
								.toString();
						String tClassName = genericString.substring(
								genericString.indexOf("<") + 1,
								genericString.indexOf(">"));
						List list = new ArrayList();
						if (tClassName.endsWith("String")) {
							String[] listValues = value.split(";");
							for (int k = 0; k < listValues.length; k++) {
								list.add(listValues[k]);
							}
							field.set(obj, list);
							break;
						} else {
							String classPath = System.getProperty("user.dir")
									+ "/"
									+ value.substring(0, value.indexOf("@"));
							String[] classIndexLists = value.substring(
									value.indexOf("@") + 1).split(";");
							for (int k = 0; k < classIndexLists.length; k++) {
								int classIndex = Integer
										.parseInt(classIndexLists[k]);
								Object classObject = CsvToObject(
										Class.forName(tClassName), classPath,
										classIndex);
								list.add(classObject);
							}
							field.set(obj, list);
							break;
						}
					} else if (type.toString().endsWith("Map")) {
						String genericString = field.getGenericType()
								.toString();
						String kClassName = genericString.substring(
								genericString.indexOf("<") + 1,
								genericString.indexOf(","));
						String vClassName = genericString.substring(
								genericString.indexOf(",") + 2,
								genericString.indexOf(">"));
						boolean stringKey = kClassName.endsWith("String");
						boolean stringValue = vClassName.endsWith("String");
						Map map = new HashMap();
						if (stringKey) {
							if (stringValue) {
								String[] listMaps = value.split(";");
								for (String listMap : listMaps) {
									String[] keyValue = listMap.split(":");
									map.put(keyValue[0], keyValue[1]);
								}
								field.set(obj, map);
								break;
							} else {
								String pathValue = value.substring(
										value.indexOf(":") + 1,
										value.indexOf("@"));
								String classPathValue = System
										.getProperty("user.dir")
										+ "/"
										+ pathValue;
								value = value.replace(pathValue + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexValue = Integer
											.parseInt(classKeyValue[1]);
									Object classObjectValue = CsvToObject(
											Class.forName(vClassName),
											classPathValue, classIndexValue);
									map.put(classKeyValue[0], classObjectValue);
								}
								field.set(obj, map);
								break;
							}
						} else {
							if (stringValue) {
								String pathKey = value.substring(0,
										value.indexOf("@"));
								String classPathKey = System
										.getProperty("user.dir")
										+ "/"
										+ pathKey;
								value = value.replace(pathKey + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexKey = Integer
											.parseInt(classKeyValue[0]);
									Object classObjectKey = CsvToObject(
											Class.forName(kClassName),
											classPathKey, classIndexKey);
									map.put(classObjectKey, classKeyValue[1]);
								}
								field.set(obj, map);
								break;
							} else {
								String pathKey = value.substring(0,
										value.indexOf("@"));
								String pathValue = value.substring(
										value.indexOf(":") + 1,
										value.indexOf("@"));
								String classPathKey = System
										.getProperty("user.dir")
										+ "/"
										+ pathKey;
								String classPathValue = System
										.getProperty("user.dir")
										+ "/"
										+ pathValue;
								value = value.replace(pathKey + "@", "");
								value = value.replace(pathValue + "@", "");
								String[] classKeyValueLists = value.split(";");
								for (String classKeyValueList : classKeyValueLists) {
									String[] classKeyValue = classKeyValueList
											.split(":");
									int classIndexKey = Integer
											.parseInt(classKeyValue[0]);
									int classIndexValue = Integer
											.parseInt(classKeyValue[1]);
									Object classObjectKey = CsvToObject(
											Class.forName(kClassName),
											classPathKey, classIndexKey);
									Object classObjectValue = CsvToObject(
											Class.forName(vClassName),
											classPathValue, classIndexValue);
									map.put(classObjectKey, classObjectValue);
								}
								field.set(obj, map);
								break;
							}
						}
					} else if (type.toString().contains("$")) {
						break;
					} else if (type.toString().contains("class")
							&& !type.toString().endsWith("String")) {
						String classValue = type.toString();
						String classPath = System.getProperty("user.dir") + "/"
								+ value.substring(0, value.indexOf("@"));
						int classIndex = Integer.parseInt(value.substring(value
								.indexOf("@") + 1));
						Object classObject = CsvToObject(
								Class.forName(classValue.substring(classValue
										.indexOf((" ")) + 1)), classPath,
								classIndex);
						field.set(obj, classObject);
						break;
					} else {
						field.set(obj, type.cast(value));
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private Boolean booleanFunction(String value) {
		if (value.equals("true")) {
			boolean b = true;
			Boolean bo = new Boolean(b);
			return bo;
		} else if (value.equals("false")) {
			boolean b = false;
			Boolean bo = new Boolean(b);
			return bo;
		} else {
			return null;
		}
	}

}

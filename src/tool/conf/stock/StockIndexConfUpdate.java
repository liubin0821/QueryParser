package conf.stock;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.util.Util;

public class StockIndexConfUpdate {

	/**
	 * @param args
	 */

	public String getIndexTitle(Element eIndex) {
		if (eIndex.getNodeName().equals("index") == false) {
			System.err.println("[" + eIndex.getNodeName() + "]"
					+ " is not a [index] node.");
			System.exit(-1);
		}
		return eIndex.getAttribute("title");
	}

	public String getParamDefualtVal(Element eParam) {
		if (eParam.getNodeName().equals("param") == false) {
			System.err.println("[" + eParam.getNodeName() + "]"
					+ " is not a [param] node.");
			System.exit(-1);
		}
		return eParam.getAttribute("default_val");
	}

	public String getParamListName(Element eParam) {
		if (eParam.getNodeName().equals("param") == false) {
			System.err.println("[" + eParam.getNodeName() + "]"
					+ " is not a [param] node.");
			System.exit(-1);
		}
		return eParam.getAttribute("list_name");
	}

	/***
	 * 将document中的内容写入文件中
	 * 
	 * @param doc
	 * @param fileName
	 * @return
	 */
	public boolean writeToXML(Document doc, String fileName) {
		boolean flag = true;
		if (doc == null || fileName == null || fileName.equals(""))
			return false ;
		try {
			/** 将document中的内容写入文件中 */
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			/** 编码 */
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
			
			
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/***
	 * 更新配置时间
	 * 
	 * @param doc
	 * @param stockIndex
	 */
	public void updateXMLDate(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList indexList = root.getElementsByTagName("index");
		for (int i = 0; i < indexList.getLength(); ++i) {
			Element eIndex = (Element) (indexList.item(i));
			String sIndexTitile = this.getIndexTitle(eIndex);
			if (StockIndex.INDEX_DATE_MAP.containsKey(sIndexTitile)) {
				// 需要更新的前两种类型的指标
				String sTime = StockIndex.geneDate(StockIndex.INDEX_DATE_MAP
						.get(sIndexTitile)); // 获取更新时间
				this.updateIndexValue(eIndex, "param", "default_val", sTime);
			} else {
				// 更新第三种类型的指标
				NodeList paramList = eIndex.getElementsByTagName("param");
				for (int j = 0; j < paramList.getLength(); ++j) {
					Element eParam = (Element) paramList.item(j);
					if (this.getParamListName(eParam).equals(
							StockIndex.LIST_NAME)) {
						String sTime = StockIndex.geneDate(DateType.OTHERS);
						this.updateParamValue(eParam, "default_val", sTime);
					}
				}
			}
		}
	}

	/***
	 * 
	 * @param eIndex
	 *            需要更新的Element，如index
	 * @param sTage
	 *            Element的子标签，如param
	 * @param sName
	 *            需要更新值的Name 如name="FD"，sName为“name”
	 * @param sValue
	 *            需要更新的值
	 */
	private void updateIndexValue(Element eIndex, String sTage, String sName,
			String sValue) {
		if (eIndex.getNodeName().equals("index") == false) {
			System.err.println("传递的参数不为Index Element");
			System.exit(-1);
		}
		NodeList paramList = eIndex.getElementsByTagName(sTage);
		if (eIndex.hasChildNodes())
			for (int i = 0; i < paramList.getLength(); ++i) {
				Element eParm = (Element) paramList.item(i);
				eParm.setAttribute(sName, sValue);
			}
	}

	private void updateParamValue(Element eParam, String sName, String sValue) {
		if (eParam.getNodeName().equals("param") == false) {
			System.err.println("传递的参数不为Index Element");
			System.exit(-1);
		}
		eParam.setAttribute(sName, sValue);
	}

	/***
	 * 给XML文件添加value节点，含最大值，最小值
	 * 
	 * @param doc
	 * @param indexTitle
	 * @param maxval
	 * @param minval
	 * @return
	 */
	public String addValueNode(Document doc, String indexTitle, String maxval,
			String minval) {
		if (doc == null) {
			// System.out.println("doc==null");
			return "NULL";
		}
		Element root = doc.getDocumentElement();
		NodeList nodelist = root.getElementsByTagName("index");

		if (nodelist.getLength() == 0) {
			return "false: 添加失败，读取错误，XML index节点数为零";
		}

		for (int i = 0; i < nodelist.getLength(); ++i) {
			Element eCur = (Element) nodelist.item(i); // 当前元素
			if (eCur.getAttribute("title").equals(indexTitle)) {
				// System.out.println("title: " + eCur.getAttribute("title"));
				NodeList children = eCur.getElementsByTagName("value"); // 当前index的所有value节点
				if (children.getLength() == 1) {
					Element eVal = (Element) children.item(0);
					// System.out.println("原maxval: " +
					// eVal.getAttribute("max_val"));
					// System.out.println("原minval: " +
					// eVal.getAttribute("min_val"));
					if (eVal.getAttribute("max_val").equals(maxval)
							&& eVal.getAttribute("min_val").equals(minval)) {
						// XML中的index已存在maxval和minval
						return "true: 存在相同的[" + indexTitle
								+ "]的maxval和minval值，无需添加";
					} else {
						eVal.setAttribute("max_val", maxval);
						eVal.setAttribute("min_val", minval);
						return "true: 添加成功";
					}
				}

				Element eNew = doc.createElement("value");
				eNew.setAttribute("max_val", maxval);
				eNew.setAttribute("min_val", minval);
				nodelist.item(i).appendChild(eNew);
				return "true: 添加成功";
			}
		}
		return "false: 添加失败，找不到title=[" + indexTitle + "]的指标";
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		 StockIndexConfUpdate update = new StockIndexConfUpdate() ;
		 Document doc = Util.readXMLFile(".\\data\\stock\\stock_index.xml");
		// // Timer timer = new Timer() ;
		//
		// String res = update.addValueNode(doc, "所属概念", "100", "10") ;
		// System.out.println(res);

		 update.updateXMLDate(doc) ;
		 update.writeToXML(doc, "updated_stock_index.xml") ;
		 


		// Element root = doc.getDocumentElement() ;
		// NodeList index_list = root.getElementsByTagName("index") ;
		// Element first_index_root = (Element)index_list.item(0) ;
		// update.updateIndexValue(first_index_root,"param", "title", "????") ;
		// NodeList param_list = first_index_root.getElementsByTagName("param")
		// ;
		//
		// for(int index = 0; index < param_list.getLength(); index++)
		// {
		// Element e = (Element)param_list.item(index) ;
		// // System.out.println(update.getParamDefualtVal(e) + "    " +
		// update.getParamListName(e));
		// // System.out.println(update.getParamListName(e));
		// if(update.getParamListName(e).equals("P00007"))
		// update.updateParamValue(e , "name" , "XXX") ;
		// // System.out.println(e.getNodeName());
		// // System.out.println(e.getAttribute("title"));
		// }
	}
}

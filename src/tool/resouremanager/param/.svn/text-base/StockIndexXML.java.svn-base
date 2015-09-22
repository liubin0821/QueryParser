package resouremanager.param;

import org.w3c.dom.Document;

import resouremanager.pub.DataCarrier;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

/**
 * stock_index.xml
 * 单例模式，内存中只能存在一份stock_index.xml
 * @author Administrator
 *
 */
public class StockIndexXML extends DataCarrier {
	private static Document xmlDoc = null  ;
	private StockIndexXML(String fileName){
		super(fileName) ;
		try {
			xmlDoc = Util.readXMLFile(fileName) ;
		} catch (DataConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Document getInstance(String fileName) throws DataConfException{
		if(xmlDoc == null)
			xmlDoc = Util.readXMLFile(fileName) ;		
		return xmlDoc ;
		
	}
}

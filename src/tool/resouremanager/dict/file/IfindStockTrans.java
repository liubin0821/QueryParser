package resouremanager.dict.file;

import java.util.List;

import resouremanager.pub.DataCarrier;
import resouremanager.util.RMUtil;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;


/**
 * ifind_stock_trans.dict
 * @author Administrator
 * 
 */
public class IfindStockTrans extends DataCarrier {

	private static List<String> IfindStockTransList = null;

	private IfindStockTrans(String fileName) {
		super(fileName);
		if (IfindStockTransList == null) {
			try {
				IfindStockTransList = Util.readTxtFile(fileName);
			} catch (DataConfException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> getInstance(String fileName){
		if(IfindStockTransList == null)
			new IfindStockTrans(fileName) ;
		return IfindStockTransList ;
	}
	
	/**
	 * 写回操作
	 */
	public boolean writeBack(){
		if(IfindStockTransList == null)
			return false ;
		return RMUtil.write2Txt(IfindStockTransList, fileName) ;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}

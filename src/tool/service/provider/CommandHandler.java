package service.provider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import bench.BenchManager;
import bench.BenchQuery;
import bench.ParserAgent;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Util;

import conf.tool.PubValue;



class CommandHandler {

	/**
	 * @rm.param args
	 */
	private final String cmdStr_ ;
	private BenchQuery bq_ ;
	private static BenchManager benchMgr_ ;

	/***
	 * 构造函数
	 * 
	 * @author Administrator
	 * 
	 */
	CommandHandler(String cmdStr, BenchManager benchMgr) {
		this.cmdStr_ = cmdStr;
		if (benchMgr_ != null)
			benchMgr_ = benchMgr;
	}
	
	

	
	public String handleCommand() {
		// TODO Auto-generated method stub
		if(cmdStr_ == null || !cmdStr_.contains("\t"))
			return String.valueOf(false) ;
		
		if(ParserAgent.queryParser == null)
			return String.valueOf(false) + " QueryParser is uninitiallized !" ;					
		
		int pos = cmdStr_.indexOf("\t");
		String cmd = cmdStr_.substring(0, pos);
		String text = cmdStr_.substring(pos + 1);
		
		if(bq_ == null)
			bq_ = new BenchQuery(text) ;
		Query query = new Query(text) ;
		
		
		CommandType type = toCmdType(cmd);
		String strRtn = "";
		switch (type) {
		case QUERY_PATTERN:
			strRtn = getPtn(query, benchMgr_) ;
			break;
		case QUERY_STOCK_CLASSIFY:
			strRtn = getStockClassifier(text, ParserAgent.queryParser) ;
			break;
		case QUERY_INDEXES:
			strRtn = getIndexes(query) ;
			break;
		case QUERY_PRINT_LOG:
			strRtn = getLog(query) ;
			break;
		case INDEX_ADD:
			strRtn = addIndex(text) ;  //undo
			break;
		case OTHER:
			strRtn = String.valueOf(false) + " 请输入正确的请求类型";
			break;
		default:
			strRtn = String.valueOf(false) + " 请输入正确的请求类型" ;
		}
		return strRtn;
	}
	
	/***
	 * 获取问句的specificPtn
	 * 
	 * @rm.param query
	 * @rm.param benchMgr
	 * @return
	 */
	private String getPtn(Query query, BenchManager benchMgr) {
		//return ParserAgent.parseQueryPattern(query, benchMgr);
		//暂时不用
		return "" ;
	}
	
	
	private String getStockClassifier(String text, QueryParser qp){
		String rtn ;
		if(PubValue.stockXMLDoc == null)
			try {
				PubValue.stockXMLDoc = Util.readXMLFile("stock.xml");				
			} catch (DataConfException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Element root = PubValue.stockXMLDoc.getDocumentElement();
		Node rootNode = (Node) root ;
		StockQueryClassifierByIndex.init() ;
		rtn = StockQueryClassifierByIndex.parse(text, qp, rootNode);
		return rtn ;
	}
	
	/***
	 * 获取问句的
	 * @rm.param query
	 * @return
	 */
	private String getIndexes(Query query){
		ParserAgent.queryParser.parse(query) ;
		StringBuilder sb = new StringBuilder() ;
		for(SemanticNode sn : query.getNodes()){
			if(sn.type == NodeType.CLASS){
				sb.append(" " + sn.text) ;
			}
		}
		return sb.toString().trim() ;
	}
	
	/***
	 * 获取问句解析的Log
	 * @rm.param query
	 * @return
	 */
	private String getLog(Query query){
		ParserAgent.queryParser.parse(query) ;
		return query.getLog().toString();
	}
	private String addIndex(String text){
		return String.valueOf(false) ;
	}

	
	/***
	 * 命令类型的枚举
	 * @author Administrator
	 *
	 */
	static enum CommandType{
		QUERY_PATTERN,           //问句的泛化
		QUERY_STOCK_CLASSIFY,          //问句的分类
		QUERY_INDEXES,           //问句的指标
		QUERY_PRINT_LOG,         //打印问句的log
		INDEX_ADD ,              //在stock_index.xml中添加一个指标
		
		OTHER ;					 //默认
	}
	public CommandType toCmdType(String cmd) {
		// TODO Auto-generated method stub
		if (cmd.equals("getPtn")) {
			return CommandType.QUERY_PATTERN;
		} else if (cmd.equals("getStockClassify")) {
			return CommandType.QUERY_STOCK_CLASSIFY;
		} else if (cmd.equals("getIndexes")) {
			return CommandType.QUERY_INDEXES;
		} else if (cmd.equals("log")) {
			return CommandType.QUERY_PRINT_LOG;
		} else if (cmd.equals("addIndex")) {
			return CommandType.INDEX_ADD;
		} else {
			return CommandType.OTHER;
		}
	}
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

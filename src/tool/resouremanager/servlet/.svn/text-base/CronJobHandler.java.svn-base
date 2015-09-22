package resouremanager.servlet;

import java.sql.SQLException;

import resouremanager.pattern.PatternCorpusUpdate;
import resouremanager.pub.RMHandler;

public class CronJobHandler implements RMHandler {
	
	private final CronJobCommand cmd;
	public CronJobHandler(String strCmd){
		cmd = CronJobCommand.fromString(strCmd) ;
	}
	@Override
	public CronJobResult handle() {
		// TODO Auto-generated method stub
		
		CronJobResult result = null ;
		
		switch (cmd) {
		
		case STOCK_INDEX_UPDATE:
			// stock_index.xml的更新
			break ;
		case TRADE_DATE_UPDATE:
			// 交易日期的更新
			break ;
		case STOCK_FUND_INFO_UPDATE:
			// 股票基金信息的更新
			break ;
		case PATTERN_CORPUS_UPADATE :
			// 更新Pattern库
			boolean rtn = false ;
			try {
				rtn = PatternCorpusUpdate.handle() ;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = new CronJobResult("updated the standard pattern corpus " + rtn + "!") ;
			break ;
		case PATTERN_CORPUS_UPADATE_ALL :
			// 更新Pattern库
			boolean rtn_upall = false;
			try {
				rtn = PatternCorpusUpdate.handleAll();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = new CronJobResult("updated the standard pattern corpus all "
					+ rtn_upall + "!");
			break ;
		default: 
			result = new CronJobResult(null) ;
			break ;
		}
		return result ;
	}

	public static enum CronJobCommand {
		// the cronjob cmd type
		STOCK_INDEX_UPDATE ,		//stock_index.xml的更新
		TRADE_DATE_UPDATE ,			//交易日期的更新
		STOCK_FUND_INFO_UPDATE ,	//
		PATTERN_CORPUS_UPADATE ,	//pattern库的更新
		PATTERN_CORPUS_UPADATE_ALL ,//pattern库全部更新 
		OTHER ;						//其他
		
		static CronJobCommand fromString(String strCmd){
			if(strCmd == null)
				return OTHER ;
			if(strCmd.equals(""))
				return STOCK_INDEX_UPDATE ;
			else if(strCmd.equals(""))
				return TRADE_DATE_UPDATE ;
			else if(strCmd.equals(""))
				return STOCK_FUND_INFO_UPDATE ;
			else if(strCmd.equals("PatternUpdate"))
				return PATTERN_CORPUS_UPADATE ;
			else if(strCmd.equals("PatternUpdateAll"))
				return PATTERN_CORPUS_UPADATE_ALL ;
			else 
				return OTHER ;
		}
	}

}

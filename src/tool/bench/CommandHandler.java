package bench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;



import bench.BenchQuery.QueryType;
import bench.BenchQuery.Status;
import bench.CommandResult.ResultGroup;
import bench.CommandResult.ResultItem;

import com.myhexin.qparser.Query;

/**
 * the Class CommandHandler
 *
 */
public class CommandHandler {
    private BenchManager benchMgr_;
    
    /**
     * @rm.param benchMgr_
     * @rm.param parser_
     */
    public CommandHandler(BenchManager benchMgr_){
    	this.benchMgr_ = benchMgr_;
    }
    
    /**
     * @rm.param commandResult
     * @return CommandResult
     */
    public CommandResult handleSave(CommandResult commandResult){
    	commandResult.msg = String.format("本次处理%s组问句，保存结果如下：\n",
        		commandResult.rltGroups.size());
    	commandResult.cmd = Command.SAVE;
    	
    	for(ResultGroup resultGroup : commandResult.rltGroups){
    		for(int gid = resultGroup.items.size()- 1; gid >=0; gid--){
    			ResultItem item = resultGroup.items.get(gid);
    			Command cmd = Command.fromStr(item.oper);
    			if(cmd == null || cmd == Command.BAD_CMD){
//    				resultGroup.items.remove(gid);
    				continue ;
    			}
    			
    			BenchQuery bq = new BenchQuery(item.text);
        		Query query = ParserAgent.parse(bq);
        		ParserAgent.parseBenchQueryFileds(query, bq, benchMgr_);
        		
    			if(cmd == Command.DEL){
    				bq.status = Status.DEL;
    				if(benchMgr_.delBenchQuery(bq)){
    					commandResult.msg += item.text + "：删除成功；\n";
    					resultGroup.items.remove(gid);
    				}else{
    					commandResult.msg += item.text + "：删除失败；\n";
    				}
    			}else if(cmd == Command.ADD || cmd == Command.ADD_FORCE){
    				bq.status = Status.STD;
    				try {
						if(benchMgr_.addBenchQuery(bq, cmd == Command.ADD_FORCE)){
							commandResult.msg += item.text + "：添加成功；\n";
							item.set2Old();
						}else{
							commandResult.msg += item.text + "：添加失败；\n";
						}
					} catch (CommandException e) {
						commandResult.msg += item.text + String.format("：添加失败（%s）；\n", e.getMessage());
					}
    			}
    		}
    		resultGroup.size = resultGroup.items.size();
    	}
		return commandResult;
    }
    
    /**
     * @rm.param line
     * @return CommandResult
     * @throws CommandException
     * @throws IOException
     */
	public CommandResult handleCommand(String line) {
		int pos = line.indexOf(' ');
		String strCmd = line, arg = "";
		
		/**分离cmd和问句*/
		if (pos > 0) {
			strCmd = line.substring(0, pos);
			arg = line.substring(pos + 1);
		}

		Command cmd = Command.fromStr(strCmd);
		String msg = "处理失败";
		try {
			switch (cmd) {
			case ADD:
				return handleAdd_(arg);
			case ADD_FORCE:
				return handleAddForce_(arg);
			case DOWN_LOAD:
				return handleDownLoad(arg);
			case SHOW:
				return handleShow_(arg);
			case SHOW_ALL:
				return handleShowAll();
				
			case SAVE:
				return handleSaveAll();
			case DEL:
				return handleDel_(arg);
			case DISCARD:
				return handleDiscard(arg);
			case HELP:
			case BAD_CMD:
				return handleHelp_();
			default:
				break;
			}
		} catch (CommandException e) {
			e.printStackTrace();
			msg = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return new CommandResult(Command.BAD_CMD, msg);
	}
    
	/**
	 * @rm.param arg
	 * @return
	 */
	private CommandResult handleDownLoad(String arg) {
		CommandResult rtn = new CommandResult(Command.DOWN_LOAD,
				"");
		QueryType type = QueryType.UNKNOWN;
		if(arg.equals("stock")){
			type = QueryType.STOCK;
		}else if(arg.equals("fund")){
			type = QueryType.FUND;
		}
		else if(arg.equals("all")){
			type = QueryType.ALL;
		}
		
		
//		if(arg.equals("stock")){
//			type = QueryType.STOCK;
//		}else if(arg.equals("stockinfo")){
//			type = QueryType.STOCK_STR;
//		}else if(arg.equals("stockcaiwu")){
//			type = QueryType.STOCK_CAIWU;
//		}else if(arg.equals("fund")){
//			type = QueryType.FUND;
//		}else if(arg.equals("fundinfo")){
//			type = QueryType.FUND_INFO;
//		}else if(arg.equals("fundcaiwu")){
//			type = QueryType.FUND_CAIWU;
//		}
		
		
		/** old code : download all the queries*/
//		for (Entry<Integer, SimiQuerySet> pair : benchMgr_.getCode2SimiQuery()
//				.entrySet()) {
//			SimiQuerySet simSet = pair.getValue();
//			ArrayList<BenchQuery> stdQuerys = simSet.getStdQuery();
//			for (BenchQuery bq : stdQuerys) {
//				boolean flag = type == bq.type || type == QueryType.UNKNOWN;
//				if(type == QueryType.STOCK){
//					flag |= (bq.type == QueryType.STOCK_INFO || bq.type == QueryType.STOCK_CAIWU);
//				}else if(type == QueryType.FUND){
//					flag |= (bq.type == QueryType.FUND_INFO || bq.type == QueryType.FUND_CAIWU);
//				}
//				if(flag){
//					rtn.msg += bq.type + "\t" + bq.text+"\n";
//				}
//			}
//		}
		
		/***
		 * new code : only one query is download for each sepcificPtn
		 * @author lu
		 */
		if(type == QueryType.UNKNOWN)
			return rtn ;
//		final int NEIGHBOUR = 2 ;
//		final double DIS_WEIGHT = 0.3 ;
		StringBuilder downloadSb = new StringBuilder() ;
		HashMap<QueryType , StringBuilder> downloadMap = new HashMap<QueryType , StringBuilder>(4) ;
		for (Entry<Integer, SimiQuerySet> pair : benchMgr_.getCode2SimiQuery().entrySet()){
			SimiQuerySet simSet = pair.getValue();
			for (Entry<Integer, ArrayList<BenchQuery>> en : simSet.getSubCode2ListMap().entrySet()){
//				boolean hasAddFirstBq = false ;
				for (BenchQuery bq : en.getValue()) {					
					boolean flag = false ;
					if(type == QueryType.STOCK){
						flag |= (bq.type == QueryType.STOCK_STR || bq.type == QueryType.STOCK_CAIWU
								|| bq.type == QueryType.STOCK_TEC) ;
					}else if(type == QueryType.FUND){
						flag |= (bq.type == QueryType.FUND_STR || bq.type == QueryType.FUND_CAIWU);
					}
					
					if(flag == true ){
//					rtn.msg += bq.type + "\t" + bq.text+"\n";
					if(downloadMap.containsKey(bq.type))
						downloadMap.get(bq.type).append(bq.type + "\t" + bq.text+"\n") ;
					else downloadMap.put(bq.type, new StringBuilder(bq.type + "\t" + bq.text+"\n")) ;					
					break ; //一个pattern只添加一个
				}
					
//					if(bq.text.length() <= 5){
//						if(flag == true && hasAddFirstBq == false ){
//							if(downloadMap.containsKey(bq.type))
//								downloadMap.get(bq.type).append(bq.type + "\t" + bq.text+"\n") ;
//							else downloadMap.put(bq.type, new StringBuilder(bq.type + "\t" + bq.text+"\n")) ;	
//							hasAddFirstBq = true ;
//						}	
//					}
//					else {
//						boolean isAddByDis = isAdd2Download(bq, en.getValue(), NEIGHBOUR, DIS_WEIGHT) ;
//						if(flag == true && isAddByDis == true){
////							rtn.msg += bq.type + "\t" + bq.text+"\n";
//							if(downloadMap.containsKey(bq.type))
//								downloadMap.get(bq.type).append(bq.type + "\t" + bq.text+"\n") ;
//							else downloadMap.put(bq.type, new StringBuilder(bq.type + "\t" + bq.text+"\n")) ;					
////							break ; //一个pattern只添加一个
//							hasAddFirstBq = true ;
//						}
//					}
				}
			}
		}
		for(StringBuilder tmp : downloadMap.values()){
			downloadSb.append(tmp) ;
		}		
		rtn.msg += downloadSb.toString() ;
		return rtn;
	}

	/**
	 * @return
	 */
	private CommandResult handleSaveAll() {
		CommandResult commandResult = new CommandResult(Command.SAVE, "保存全部问句\n");
		Collection<SimiQuerySet> simiSets = benchMgr_.getCode2SimiQuery().values();
		for(SimiQuerySet simiSet : simiSets){
			ResultGroup group = new ResultGroup();
			group.treeCode = simiSet.getTreeCode();
			group.tree = simiSet.getTree();
			int allCnt = 0;
			for(BenchQuery bq : simiSet.getStdQuery()){
				try {
					if(benchMgr_.saveBenchQuery(bq)){
						allCnt++;
					}
				} catch (CommandException e) {
				}
			}
			group.size = allCnt;
			commandResult.rltGroups.add(group);
		}
		return commandResult;
	}
	
	private CommandResult handleShowAll(){
		 CommandResult rtn = new CommandResult(Command.SHOW_ALL, "全部问句的显示结果如下：\n");
		 benchMgr_.addNewComingStdQuery();
		 for(Entry<Integer, SimiQuerySet> pair : benchMgr_.getCode2SimiQuery().entrySet()){
			 CommandResult.ResultGroup rg = new CommandResult.ResultGroup();
             rg.treeCode = pair.getKey();
			 rg.tree = benchMgr_.getTree(pair.getKey());
			 BenchQuery bestCand = null;		//最好例句
			 
			 SimiQuerySet simSet = pair.getValue();
			 ArrayList<BenchQuery> stdQuerys = simSet.getStdQuery();
             for(BenchQuery item : stdQuerys){
            	 if(item.trans.equals("") && item.patterns.equals("") 
            			 && item.unknowns.equals("")){
            		 bestCand = item;
            		 break;
            	 }else if(item.trans.equals("") && item.patterns.equals("")){
            		 bestCand = item;
            	 }else if(bestCand == null){
            		 bestCand = item;
            	 }
             }
             if(bestCand != null){
				CommandResult.ResultItem ri = convertTo(bestCand);
				rg.items.add(ri);
				rg.size = stdQuerys.size();
				rtn.rltGroups.add(rg);
             }
		 }
		 return rtn;
	}


    private CommandResult handleDiscard(String arg) throws CommandException {
    	if(arg.length() != 0) {
            throw new CommandException(Command.DISCARD, "accepts no arg, got [%s]", arg);
    	}    	
    	ArrayList<BenchQuery> discarded = benchMgr_.discard();
        return new CommandResult(Command.DISCARD, "如下问句被放弃保存：\n%s",
                joinBenchQueryList(discarded, true));
    }
    
    private CommandResult handleDel_(String arg) throws CommandException, IOException {
    	if(arg.length() == 0) {
            throw new CommandException(Command.DEL, "accepts 1 arg");
    	}
    	
    	StringBuilder msgBuilder = new StringBuilder();
    	String[] queries = arg.split("\r?\n");
        for(String q : queries) {
        	q = q.trim();
        	if(!q.equals("")){
        		BenchQuery bq = new BenchQuery(q);
            	if(benchMgr_.delBenchQuery(bq)){
            		msgBuilder.append(String.format("删除成功:%s\n", q));
            	} else {
            		msgBuilder.append(String.format("不存在:%s\n", q)); 
            	}
        	}
        }
        return new CommandResult(Command.DEL, msgBuilder.toString());
    }
    
    private CommandResult handleHelp_() {
        return new CommandResult(Command.HELP, Command.getHelpStr());
    }
    
    private CommandResult handleShow_(String text) throws CommandException{
    	if(text == null || text.length() == 0){
    		throw new CommandException(Command.SHOW, "请输入问句");
    	}
    	CommandResult commandResult = new CommandResult(Command.SHOW, "问句：%s 相似解析树的检索结果如下\n", text);
    	String[] queries = text.split("\r?\n");
    	
    	for(String queryText : queries){
    		queryText = queryText.trim();
    		if(queryText.equals("")){
    			continue ;
    		}
    		BenchQuery bq = new BenchQuery(queryText);
    		bq.status = Status.DEL;
    		Query query = ParserAgent.parse(bq);
    		ParserAgent.parseBenchQueryFileds(query, bq, benchMgr_);
    		ResultItem item = convertTo(bq);
    		
			SimiQuerySet simiSet = benchMgr_.getSimiQuerySet(bq.treeCode);
			ArrayList<BenchQuery> subList = simiSet.getSpecificPtnBqList(item.patternCode);
			boolean sameFlag = simiSet.isExactlySame(bq, subList);
			ResultGroup resultGroup = null;
			for(ResultGroup group : commandResult.rltGroups){
				if(group.treeCode == bq.treeCode){
					resultGroup = group;
					break;
				}
			}
			if(resultGroup == null){
				resultGroup = new ResultGroup();
				resultGroup.treeCode = bq.treeCode;
				resultGroup.tree = benchMgr_.getTree(bq.treeCode);
				
				boolean add = true;
				ArrayList<BenchQuery>stdQuerys = simiSet.getStdQuery();
				for(BenchQuery benchQuery : stdQuerys){
					ResultItem tmp = convertTo(benchQuery);
					if(item.qid.equals(tmp.qid)){
						resultGroup.items.add(0, tmp);
						add = false;
						continue ;
					}
					resultGroup.items.add(tmp);
				}
				if(add){
					resultGroup.items.add(0, item);
				}
				commandResult.rltGroups.add(resultGroup);
				resultGroup.size = resultGroup.items.size();
			}else if(!sameFlag){
				resultGroup.items.add(0, item);
				resultGroup.size = resultGroup.items.size();
			}
    	}
    	return commandResult;
    }
    /**
     * 将新添加的问句保存到BenchManager里。如果解析出错则不保存
     * @rm.param arg
     * @return
     * @throws CommandException
     */
    private CommandResult handleAdd_(String arg) throws CommandException {
        if(arg.length() == 0) {
            throw new CommandException(Command.ADD, "请输入问句");
        }
        String[] queries = arg.split("\r?\n");
        ArrayList<String> listQuery = new ArrayList<String>(queries.length);
        for(String q : queries) {
        	q = q.trim();
        	if(!q.equals("")){
        		listQuery.add(q); 
        	}
        }
        return doAdd_(listQuery, Command.ADD);
    }
    
    private CommandResult doAdd_(ArrayList<String> queries, Command cmd) {
        CommandResult rtn = new CommandResult(cmd, "本次处理%s个问句，结果如下：\n",
                queries.size());
        for(String queryText : queries){
    		BenchQuery bq = new BenchQuery(queryText);
    		Query query = ParserAgent.parse(bq);
    		ParserAgent.parseBenchQueryFileds(query, bq, benchMgr_);
    		if(query.hasFatalError()){
				rtn.msg += String.format("%s：添加失败，解析出错！\n", queryText);
				continue ;
			}
    		
			try {
				benchMgr_.addBenchQuery(bq, cmd == Command.ADD_FORCE);
					rtn.msg += String.format("添加成功：%s\n", queryText);
			} catch (CommandException e) {
				String msg = e.getMessage();
				rtn.msg += msg;
			}
			
			//大量添加时，直接添加，不返回结果集
			if(queries.size() > 20)
				continue ;
			
			ResultGroup resultGroup = null;
			for(ResultGroup group : rtn.rltGroups){
				if(group.treeCode == bq.treeCode){
					resultGroup = group;
					break;
				}
			}
			
			ResultItem item = convertTo(bq);
			if(resultGroup == null){
				resultGroup = new ResultGroup();
				resultGroup.treeCode = bq.treeCode;
				resultGroup.tree = benchMgr_.getTree(bq.treeCode);
				SimiQuerySet simiSet = benchMgr_.getSimiQuerySet(bq.treeCode);
				for(BenchQuery benchQuery : simiSet.getStdQuery()){
					ResultItem tmp = convertTo(benchQuery);
					if(tmp.qid.equals(item.qid)){
						resultGroup.items.add(0, tmp);
					}else{
						resultGroup.items.add(tmp);
					}
				}
				resultGroup.size = resultGroup.items.size();
				rtn.rltGroups.add(resultGroup);
			}else{
				resultGroup.items.add(0, item);
				resultGroup.size = resultGroup.items.size();
			}
    	}
        return rtn;
    }

    private CommandResult handleAddForce_(String arg) throws CommandException, IOException {
        if(arg.length() == 0) {
            throw new CommandException(Command.ADD_FORCE, "请输入问句");
        }
        String[] queries = arg.split("\r?\n");
        ArrayList<String> listQuery = new ArrayList<String>(queries.length);
        for(String q : queries) { listQuery.add(q); }
        return doAdd_(listQuery, Command.ADD_FORCE);
    }
        
    private static String joinBenchQueryList(ArrayList<BenchQuery> queries, boolean isNew) {
        StringBuilder sb = new StringBuilder();
        for(BenchQuery bq : queries) {
            sb.append(isNew ? "NEW" : "OLD").append('\t')
                    .append(bq.text).append('\n');
        }
        return sb.toString();
    }
    
	/**
     * @rm.param bq
     * @return ResultItem
     */
    private ResultItem convertTo(BenchQuery bq){
    	ResultItem rtn = new ResultItem();
    	rtn.set2New();
    	if(bq.status != Status.DEL){
    		rtn.set2Old();
    	}
    	rtn.qid = bq.qid ;
    	rtn.text = bq.text;
    	rtn.date = bq.date;
    	rtn.trans = bq.trans;
    	rtn.unknowns = bq.unknowns;
    	rtn.patterns = bq.patterns;
    	rtn.patternCode = bq.specificPtnCode;
    	rtn.queryPattern = benchMgr_.getPattern(bq.specificPtnCode);
    	rtn.queryType = bq.type.toString();
    	return rtn;
    }

    /**
     * the Class Command
     *
     */
    public static enum Command {
        ADD, ADD_FORCE, ADD_FILE, DEL, SAVE, DISCARD, HELP, EXIT, /** */
        BAD_CMD, SIM_STRUCT, SIM_INDEX, SHOW_ALL, SHOW, DOWN_LOAD , GET_PATTERN;
        
        /**
         * @rm.param cmdStr
         * @return Command
         */
        public static Command fromStr(String cmdStr) {
            if("help".equals(cmdStr)) { return HELP; }
            if("add".equals(cmdStr)) { return ADD; }
            if("addforce".equals(cmdStr)) { return ADD_FORCE; }
            if("addfile".equals(cmdStr)) { return ADD_FILE; }
            if("del".equals(cmdStr)) { return DEL; }
            if("save".equals(cmdStr)) { return SAVE; }
            if("exit".equals(cmdStr)) { return EXIT; }
            if("discard".equals(cmdStr)) { return DISCARD; }
            if("simquery".equals(cmdStr)){return SIM_STRUCT;}
            if("simindex".equals(cmdStr)){return SIM_INDEX;}
            if("showall".equals(cmdStr)){return SHOW_ALL;}
            if("show".equals(cmdStr)){return SHOW;}
            if("download".equals(cmdStr)){return DOWN_LOAD;}
            if("getpattern".equals(cmdStr)){return GET_PATTERN; }
            return BAD_CMD;
        }
        
        /**
         * @return help
         */
        public static String getHelpStr() {
            StringBuilder sb = new StringBuilder();
            sb.append("add query-text      :添加一个新问句\n");
            sb.append("addforce query-text :强制添加一个新问句，除非完全重复\n");
            sb.append("addfile query-file  :从文件添加一批问句\n");
            sb.append("del query-text      :删除之前添加的问句\n");
            sb.append("save                :保存自上次保存以来添加的问句\n");
            sb.append("exit                :退出服务\n");
            sb.append("help                :打印本信息\n");
            return sb.toString();
        }
    }
    
    private static double strDis(String str1, String str2){
    	int longger = str1.length() > str2.length() ? str1.length() : str2.length() ;
    	int shortter = str1.length() < str2.length() ? str1.length() : str2.length() ;   	   
    	
    	System.out.println("longger:" + longger);
    	System.out.println("shortter:" + shortter);
    	return (double)(longger - shortter) / shortter ;
    }
    
    /***
     * 一个benchQuery与其周围的benchQuery是否相似。不相似则需要下载。
     * @rm.param bq
     * @rm.param bqList
     * @rm.param neighbour
     * @rm.param weight
     * @return
     */
    private boolean isAdd2Download(BenchQuery bq , ArrayList<BenchQuery> bqList, 
    		int neighbour, double weight){
    	if(bqList == null || bqList.isEmpty())
    		return true ;
    	
    	if(bq == null)
    		return false ;
    	
    	int pos = bqList.indexOf(bq) ;
    	if(pos == -1) 
    		return true ;
    	int begin = pos - neighbour ;
    	int end = pos + neighbour ;
    	if(begin < 0)
    		begin = 0 ;
    	if(end >= bqList.size()-1)
    		end = bqList.size()-1 ;
    	
    	for(int idx = begin; idx <= end; ++idx){
    		if(strDis(bqList.get(idx).text, bq.text) > weight)
    			return true ;
    	}    	
    	return false ;
    }

	/**
	 * 关闭Manager
	 */
	public void close() {
		benchMgr_.close();
	}
	public static void main(String[] args){
//		double d = strDis("123", "56933") ;
//		System.out.println(d);
	}

}

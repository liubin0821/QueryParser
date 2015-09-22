package tool.task;





class CommandHandler   {
	
	
	
	
	public String handleCommand(String cmdStr) {
		// TODO Auto-generated method stub
		CommandType type = toCmdType(cmdStr);
		String strRtn = "";
		switch (type) {
		case UPDATE_INDEX_PARAM:
			break;
		case UPDATE_CORRECT_CORPUS:
			break;
		case UPDATE_MAX_MIN:
			break;
		case OTHER:
			strRtn = String.valueOf(false);
			break;
		default:
			strRtn = String.valueOf(false);
		}
		return strRtn;
	}

	private String updateIndexParam() {
		return String.valueOf(false);
	}

	private String updateCorrectCorpus() {
		return String.valueOf(false);
	}

	private String updateMaxMin() {
		return String.valueOf(false);
	}
	
	
	static enum CommandType{
		UPDATE_INDEX_PARAM ,
		UPDATE_CORRECT_CORPUS,
		UPDATE_MAX_MIN ,
		OTHER ;
	}
	
	public CommandType toCmdType(String cmd) {
		if (cmd.equals("updateIndexParam")) {
			return CommandType.UPDATE_INDEX_PARAM;
		} else if (cmd.equals("updateCorrectCorpus")){
			return CommandType.UPDATE_CORRECT_CORPUS;
		}else if (cmd.equals("updateMaxMin")){
			return CommandType.UPDATE_MAX_MIN;
		}else {
			return CommandType.OTHER;
		}
	}

}

package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.except.DataConfException;

/**
 * 在语义绑定过程中，有些词可作为绑定终止或跨过的标记<br>
 * 或者作为显示同义词转换提示中可忽略的词<br>
 * 这些词统称为“标记词”<br>
 * 以这些词作用的不同可将其分为不同的类型，称为“标记词类型”<br>
 * 一个“标记词”可能属于多个“标记词类型”<br>
 */
public class SpecialWords {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SpecialWords.class.getName());
    //从标记词到标记词类型的映射
    private static Map<String, EnumSet<SpecialWordType>> specWord2Types_ = null;
    private static Map<String, EnumSet<SpecialWordType>> patternWord2Types_ = null;
    
    /**
     * 解析标记词信息文件
     * @param lines 按行读取出的标记词信息文件
     * @throws DataConfException 
     */
    public static void loadSpecWordInfo(ArrayList<String> lines)
            throws DataConfException {
        HashMap<String, SpecialWordType> tagTypeDef = 
            new HashMap<String, SpecialWordType>();
        Map<String, EnumSet<SpecialWordType>> tagInfo = 
            new HashMap<String, EnumSet<SpecialWordType>>();
        
        loadTagInfo(lines, tagTypeDef, tagInfo);
        specWord2Types_ = tagInfo;
    }
    
    public static boolean hasWord(String word, SpecialWordType tagType) {
        if (specWord2Types_.containsKey(word) 
                && specWord2Types_.get(word).contains(tagType)) {
            return true;
        }else if(patternWord2Types_.containsKey(word) 
        		&& patternWord2Types_.get(word).contains(tagType)){
        	return true;
        }
        return false;
    }
    
    /**不考虑special word的类型
     * 可以用于判断该sepcial word 是否是已识别的词
     * @param word
     * @return
     */
    public static boolean hasWord( String word ){
        return patternWord2Types_.containsKey(word)
        		||specWord2Types_.containsKey(word);
    }
    
    /**
     * 获取一个词语的SpecialWordType列表
     * @param word
     * @return SpecialWordType列表,不存在则返回一个empty的list
     */
    public static EnumSet<SpecialWordType> getTypesOf(String word){
    	EnumSet<SpecialWordType> rtn = EnumSet.noneOf(SpecialWordType.class);
    	if(specWord2Types_==null) {
    		return rtn;
    	}else{
    		logger_.error("[Warning]specWord2Types_ 未初始化");
    	}
    	
    	EnumSet<SpecialWordType> list = specWord2Types_.get(word);
    	if(list != null) rtn.addAll(list);
    	
    	if(patternWord2Types_!=null){
    		list = patternWord2Types_.get(word);
    	}else{
        	logger_.error("[Warning]patternWord2Types_ 未初始化");
    	}
    	if(list != null) rtn.addAll(list);
    	
    	return rtn;
    }
    
    private static void loadTagInfo(
            ArrayList<String> lines, 
            Map<String,SpecialWordType> tagTypeDef, 
            Map<String,EnumSet<SpecialWordType>> tagInfo
            )
            throws DataConfException {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("define")) {
                parseTypeInfo(line, tagTypeDef, i);
            } else {
                parseTagWordInfo(line, tagInfo, tagTypeDef, i);
            }
        }
    }

    private static void parseTagWordInfo(
            String line,
            Map<String,EnumSet<SpecialWordType>> tagInfo,
            Map<String,SpecialWordType> tagTypeDef, 
            int pos) throws DataConfException {
        int posOf = line.lastIndexOf("=");
        if (posOf < 0) {
            throw new DataConfException(Param.SPECIAL_WORDS_FILE,
                    pos, "文件中格式错误: 行未以=分隔");
        }
        String infosStr = line.substring(posOf + 1);
        String infoWord = line.substring(0,posOf);
        String[] types = infosStr.split(" ");
        for (String type : types) {
            SpecialWordType tagType = tagTypeDef.get(type);
            if (tagType == null) {
                throw new DataConfException(Param.SPECIAL_WORDS_FILE, pos,
                        "文件中信息错误: 类型“%s”未在读取本条信息之前定义，请检查该类型定义信息的位置",
                        type);
            }
            //向 word to tag 哈希表中存放
            if(!tagInfo.containsKey(infoWord)){
                tagInfo.put(infoWord, EnumSet.of(tagType));
            }else{
                tagInfo.get(infoWord).add(tagType);
            }
        }
    }

    private static void parseTypeInfo(String line,
            Map<String, SpecialWordType> tagTypeDef, int pos)
            throws DataConfException {
        int posOf = line.indexOf(":");
        if (posOf < 0) {
            throw new DataConfException(Param.SPECIAL_WORDS_FILE, pos,
                    "文件中格式错误: 行未以:分隔");
        }
        String infosStr = line.substring(posOf + 1);
        String[] infos = infosStr.split(";");
        for (String info : infos) {
            String[] strs = info.split("=");
            if (strs.length != 2) {
                throw new DataConfException(Param.SPECIAL_WORDS_FILE, pos,
                        "文件中格式错误: 信息“%s”格式错误", info);
            }
            String role = info.split("=")[0];
            String type = info.split("=")[1];
            /*if (tagTypeDef.containsKey(role)) {
                QueryParser.logger_.warn("文件中信息错误: 信息“%s”中的“%s”重复", info, role);
            }*/
            SpecialWordType tagType = getTagType(type, pos, info);
            tagTypeDef.put(role, tagType);
        }
    }

    private static SpecialWordType getTagType(String type, int pos, String info)
            throws DataConfException {
        if (type.equals("tb_common_skip")) {
            return SpecialWordType.TB_COMMON_SKIP;
        } else if (type.equals("tb_common_stop")) {
            return SpecialWordType.TB_COMMON_STOP;
        } else if (type.equals("fuzzy_skip")) {
            return SpecialWordType.FUZZY_SKIP;
        } else if (type.equals("fuzzy_stop")) {
            return SpecialWordType.FUZZY_STOP;
        } else if (type.equals("ignore_trans")) {
            return SpecialWordType.IGNORE_TRANS;
        } else if (type.equals("ignore_skip")) {
            return SpecialWordType.IGNORE_SKIP;
        } else if (type.equals("interval_index")) {
            return SpecialWordType.INTERVAL_INDEX;
        } else if (type.equals("forecast_index")) {
            return SpecialWordType.FORECAST_INDEX;
        } else if (type.equals("not_copy_params_index")) {
            return SpecialWordType.NOT_COPY_PARAMS_INDEX;
        } else if (type.equals("trigger_skip")) {
            return SpecialWordType.TRIGGER_SKIP;
        } else if (type.equals("trigger_stop")) {
            return SpecialWordType.TRIGGER_STOP;
        } else if (type.equals("hidden_left_tag")){
        	return SpecialWordType.HIDDEN_LEFT_TAG;
        } else if (type.equals("hidden_right_tag")){
        	return SpecialWordType.HIDDEN_RIGHT_TAG;
        } else {
            throw new DataConfException(Param.SPECIAL_WORDS_FILE, pos,
                    "文件中信息错误: 信息“%s”中的“%s”未定义", info, type);
        }
    }
    
	public static void addPtnRuleWordTypes(
			Map<String, EnumSet<SpecialWordType>> patternWord2Types) {
		if(patternWord2Types_ == null){
			patternWord2Types_ = new HashMap<String, EnumSet<SpecialWordType>>();
		}
		for(Entry<String, EnumSet<SpecialWordType>> pair: patternWord2Types.entrySet()){
			String word = pair.getKey();
			EnumSet<SpecialWordType> typeList = patternWord2Types_.get(word);
			if(typeList == null){
				patternWord2Types_.put(word, EnumSet.copyOf(pair.getValue()));
			}else{
				typeList.addAll(pair.getValue());
			}
		}
	}

}

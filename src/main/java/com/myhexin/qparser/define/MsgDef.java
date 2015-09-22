package com.myhexin.qparser.define;

import java.lang.reflect.Field;
import java.util.List;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.DataConfException;

/**
 * 定义一些错误提示信息常量。常量命名除一般命名规范外，还需要遵守
 * 如此约定：该常量内容若用于<code>String.format</code>，以<code>_FMT</code>
 * 结尾；若直接使用，则以<code>_STR</code>结尾 
 * 注意此类中定义的信息内容，如果在配置文件msg_define.txt中亦有，以配置文件为准
 */
public class MsgDef {
    
    /**
     * 智能回答时给出的提示
     */
    public static String SEE_DOCTOR_STR = "投资有风险，入市须谨慎。关于该股票，请参考我们的牛叉诊股的结果。";
    public static String NO_DATA_CMD_STR = "问句解析失败：无法产生数据查询指令";
    /**
     * 查询指标数大于25，给出的警告。
     */
    public static String TOO_MANY_INDEX_STR = "您所查询的信息范围太广，请尝试适当减小范围";
    /**
     * 在开始绑定时，和绑定结束时，对问句中的信息进行检查。若未识别任何指标，则给出警告。
     */
    public static String NO_COND_FOUND_STR = "未能识别出任何指标条件";
    /**
     * 在问句解析发生内部错误时，给用户的警告。一般是程序出现bug
     */
    public static String UNEXPECTED_STR = "对不起，未能正确处理您的问句 - 系统发生未知错误";
    /**
     * 数字的单位与指标的单位不符时，给用户这种提示。
     */
    public static String UNIT_NOT_MATCH_INDEX_HAS_UNIT_FMT = "指标“%s”的系统默认单位为“%s”，与数字“%s”的单位不符";
    /**
     * 指标本身没有指定单位，但数字有单位。两者因此无法关联时，给用户这种提示。
     */
    public static String UNIT_NOT_MATCH_INDEX_NO_UNIT_FMT = "指标“%s”与数字“%s”的单位不符,请去掉数字的单位再试";
    /**
     * 当数字单位为“元”或“股”，但不符合推理指标的条件时，给用户这种提示
     */
    public static String NUM_NOT_FIND_INDEX_FMT = "我们不知道数字“%s”的单位或未找到其所描述的指标";
    /**
     * 当数字符合推理指标的条件时，给用户这种提示
     */
    public static String NUM_INDEX_DEF_BY_UNIT_FMT = "数字“%s”推出指标 “%s”";
    /**
     * 在读取系统配置文件出现问题时给出的警告。
     */
    public static String BAD_DICT_INFO_STR = "系统出现内部语义数据错误";
    /**
     * 句中的平均或排序操作未找到操作对象时，给用户这种提示
     */
    public static String REST_AVG_AND_SORT_FMT = "未理解%s操作“%s”的操作对象是什么";
    /**
     * 现在翻译指令阶段暂不支持多重计算。故若遇到如A+B*C这种计算，则在翻译时给出如下警告
     */
    public static String MULTILAYER_CALCULATION_STR = "需要至少两次算术运算";
    /**
     * 在绑定结束后，发现最终是一个逻辑下只有1个或者没有其他指标或操作，则判定句子的逻辑有问题。如“并且股价大于3”
     */
    public static String WRONG_LOGIC_STR = "问句的逻辑关系描述不准确，请尝试修改";
    /**
     * 在指令翻译阶段，发现一个操作下是相同时间的相同指标进行比较，则发出如下警告。如“今天流通股本大于今天流通股本”，例子比较极端……
     */
    public static String OP_BETWEEN_SAME_INDEX_WITH_SAME_DATE_PARAM_FMT = "指标“%s”使用相同时间的值比较";
    /**
     * 对于如问句“今日收阳较高”的，如果没有对“收盘价-开盘价 较高”给出定义，则无法确定过滤条件。此时给用户如下警告。
     */
    public static String NO_DEF_VAL_INFO_FMT = "我们尚无“%s”对应的“%s”默认值信息";
    /**
     * 当输入的问句过长时，记录此错误信息。在网页，该限制为150;在信息频道，为8。
     */
    public static String INFO_QUERY_OUT_OF_RANGE_STR = "INFO_QUERY_OUT_OF_RANGE";
    /**
     * 当输入的问句过长时，给用户如下警告。在网页，该限制为150;在信息频道，为8。
     */
    public static String QUERY_TOO_LONG_FMT = "问句过长，暂时无法处理 - 您的问句长度%d个字符，能处理的长度不超过%d个字符。";
    /**
     * 当对指标的多个参数同时进行平均或排序，如“最早股价最高的股票”，就是同时对“交易日期”和“数值”进行排序。例句不靠谱，但是意思是这个意思。
     */
    public static String AVG_OR_SORT_FMT = "暂不支持对多个不同参数的指标“%s”进行同时平均或排序操作";
    /**
     * 在翻译指令阶段，如果碰到一句话里有多个排序，如“收盘价最高，开盘价最低的股票”，此时只会采用一个排序，并给用户这种提示
     */
    public static String TOO_MANY_SORT_FMT = "尚不支持多个排序，以下忽略对“%s”的排序。";
    /**
     * 当一句话有多个平均需要计算，如“平均库存大于平均XX”，XX是个指标。这种情况，暂不支持。碰到就会给出这种提示
     */
    public static String TOO_MANY_AVG_OP_STR = "多个求平均的运算";
    /**
     * 例如“A-B平均大于3”，这种需要对计算结果进行平均的，暂不支持。遇到了就给出这种提示。
     */
    public static String NEED_AVG_ON_OP_RESULT_STR = "对计算结果求平均";
    /**
     * 例如“A-B最大”，这种需要对计算结果进行排序的，暂不支持。遇到了就给出这种提示。
     */
    public static String NEED_SORT_ON_OP_RESULT_STR = "计算结果进行排序";
    /**
     * 例如“最大的A比C大”，这种情况暂不支持。遇到了就给出这种提示。
     */
    public static String USE_SORT_AS_VAL_STR = "您对计算操作和排序操作的描述有错误：排序操作无法作为值与其他指标进行比较。";
    /**
     * 当说“8月之前A增长”，这种无法确定是跟啥时候比较的，给出这种提示
     */
    public static String SINGLE_DATE_CANNOT_GET_EARLIER_FMT = "您所说的日期“%s”无法取得可以与其比较的较早时间";
    /**
     * 若时间还不认识，解析不出来，就给这种提示
     */
    public static String NOT_SUPPORTED_DATE_FMT = "暂不支持“%s”的时间解析";
    /**
     * 若数字还不认识，还不是如“较多”这种模糊数字，就给这种提示
     */
    public static String UNKNOW_NUM_FMT = "数字“%s”未能识别";
    /**
     * 若数字不认识，解析不出来，就给这种提示
     */
    public static String NOT_SUPPORTED_NUM_FMT = "暂不支持“%s”的数字解析";
    /**
     * 若解析出的时间不合法，如“2011年4月31日”，则给出这种提示
     */
    public static String WRONG_DATE_FMT = "“%s”的时间信息错误";
    /**
     * 如果发现操作的比较值和操作对象的单位无法匹配的时候，给出这种提示。如“收盘价-开盘价>5家”。
     */
    public static String OP_STD_UNIT_NOT_MATCH_FMT = "操作“%s”的比较值“%s”与指标“%s”的单位不符";
    /**
     * 现在都默认操作必须有比较值。故当操作的比较值为空的时候，给出这种提示。一般是bug。
     */
    public static String OP_STD_MISS_FMT = "操作“%s”的比较值为空";
    /**
     * 
     */
    public static String AVG_ONLY_STR = "查询指标的平均值。";
    /**
     * 如果一个针对一个对象的操作下面还是一个操作，如“(A-B)增长”,这样的暂无法处理。碰到了给这种提示
     */
    public static String NOT_SUPPORTED_SPLIT_OP_HAS_OP_SON_FMT = "暂不对多重操作进行拆分";
    /**
     * 当一个指标需要与自身较早数据相比较的时，若该指标的时间参数的值不能拆分，或者压根就没有时间参数，这个指标就不能跟“往期”比较了。遇到这种句子，
     * 给出这种提示。
     */
    public static String NOT_SUPPORTED_SPLIT_INDEX_CAN_NOT_COMPARE_FMT = "指标“%s”对自身往期的操作";
    /**
     * 暂不支持预测型指标与往期的操作。如果遇到了，给出这种提示。
     */
    public static String NOT_SUPPORTED_PREDICT_INDEX_CAN_NOT_COMPARE_STR = "预测型指标对自身往期的操作";
    /**
     * 当操作的操作对象的值既是连续的时间，但又不能拆分成多个，则给出这种提示。如“连续1天大股东持股增长1%”
     */
    public static String DATE_CAN_NOT_SPLIT_FMT = "操作“%s”的操作指标“%s”的时间参数描述不是很合理，您可以尝试“连续三%s”";
    /**
     * 当一个指标有多个值的时候，提示用户都有哪些值，取的值又是哪个
     */
    public static String INDEX_HAS_VALS_FMT = "%s都与指标“%s”相关，现在我们只取%s";
    /**
     * 在提取本体文件内信息时未成功，则记录此错误信息
     */
    public static String NOT_EXIST_IN_ONTO_FMT = "“%s”在本体词典文件中不存在";
    /**
     * 于平均值比较的信息都已经记录在平均节点内部，在翻译时，提取的比较类型未知，则记录此错误信息
     */
    public static String UNKNOW_AVG_CMP_TYPE = "未知的平均比较类型";
    /**
     * 如“A-B>6”，若A的时间参数是报告期，而B的是年度，则两者暂无法比较。遇到这种情况，给用户提示。
     */
    public static String DIFF_DATE_PARAM_FMT = "指标“%s”与指标“%s”的时间参数不同，暂不支持两者间的计算";
    /**
     * ifind客户端不支持排序。则若是来自客户端的问句有排序操作，就给出这种提示。
     */
    public static String SORT_NOT_ALLOWED_STR = "我们还不能处理与排序相关的问句。您可在这里尝试：http://search.10jqka.com.cn/stockpick";
    /**
     * ifind客户端不支持平均。则若是来自客户端的问句有平均操作，就给出这种提示。
     */
    public static String AVG_NOT_ALLOWED_STR = "我们还不能处理与平均相关的问句。您可在这里尝试：http://search.10jqka.com.cn/stockpick";
    /**
     * 语义树绑定结束后并没有绑定成功，树里还有多个根节点时，给出这种提示。
     */
    public static String TREE_ROOT_NOT_UNIQUE_STR = "我们部分地理解了句中成分，但未能整体理解成分间的关系";
    /**
     * 在解析字符串节点时，发现词典里指定的可关联指标都失效，则给出这种提示
     */
    public static String STR_HAS_NO_OFWHAT_SRV_FMT = "尚不支持有关数据“%s”的指标%s。";
    /**
     * 来着客户端的问句不支持查询客户端“待选范围”中的信息 ，在解析字符串节点的时候，发现字符串为“待选范围”中的信息，则给出这种提示
     */
    public static String STR_HAS_NO_OFWHAT_CLT_FMT = "尚不支持有关数据“%s”的指标“%s”。您可尝试使用待选范围或http://search.10jqka.com.cn/stockpick";
    /**
     * 来着客户端的问句不支持查询客户端“待选范围”中的信息，在解析指标节点的时候 ，发现该指标是为“待选范围”中的指标，则给出这种提示
     */
    public static String INDEX_NOT_AVAIL_CLT_FMT = "尚不支持指标“%s”。您可尝试使用待选范围或http://search.10jqka.com.cn/stockpick";
    /**
     * 在绑定的时候，发现有操作没有找到操作对象的时候，给出这种提示
     */
    public static String MISS_OPERAND_FMT = "对不起，未能找到计算符“%s”的所有待计算指标。";
    
    /**
     * 检查指标值时，发现值大于指标默认最大值，则给用户这种提示
     */
    public static String MORE_THAN_MAX_FMT = "指标的值%s超出了该指标可能的最大值%s，可能导致查找不到结果";
    /**
     * 检查指标值时，发现值小于指标默认最小值，则给用户这种提示
     */
    public static String LESS_THAN_MIN = "指标的值%s小于该指标可能的最小值%s，可能导致查找不到结果";
    /**
     * 同义词替换的提示
     */
    public static String WEB_TRANS_TIP_STR = "经过同义词替换后";
    /**
     * 基金（股票）指标没找到，可能是股票（基金）的指标，此时给出提示
     */
    public static String ONTO_NOT_FOUND_FMT = "貌似“%s”与%s相关，您可选中“%s选项”后再次尝试。";
    /**
     * 对指标值的浮动提示
     */
    public static String MOVE_VAL_TIP_FMT = "“%s”的值有%s%.2f%s的浮动。";
    /**
     * 暂不支持一个字符串指标的值既有包含也有不包含，遇到时，给出提示
     */
    public static String STR_CMP_NOT_SUPPORTED_STR = "对文字型值同时进行“包含”和“不包含”过滤";
    /**
     * 若对相对时间，如“前一天”为调整成功，则给出这种提示。
     */
    public static String NOT_ADJUST_RELATIVE_DATE_FMT = "对相对时间“%s”调整失败，故未调整该时间";
    /**
     * 暂不支持某些字符串指标的值有不包含的操作，遇到时，给出提示
     */
    public static String NOT_CONTAIN_NOT_SUPPORTED_FMT = "不支持对指标“%s”进行“不包含”过滤。";

    /**
     * 暂不支持股票简称有多个条件，包含逻辑处理。遇到时，给出这种提示。
     */
    public static String STK_NAME_NO_LOGIC_STR = "“股票简称”也要进行“且或”的逻辑，让我先考虑考虑呢！";
    /**
     * 时间单位错误，一般是bug
     */
    public static String UNKNOW_DATE_UNIT_FMT = "该时间单位未知：%s";
    
    /******************技术操作相关提示信息***********************/
    /**
     * 技术指标日期提示
     */
    public static String QUOTE_TIP = "依据技术指标\"%s\"执行选股。";
    /**
     * 技术指标的转换提示
     */
    public static String QUOTE_DEFTIP = "默认将行情术语\"%s\"识别为\"%s\"";
    /**
     * 技术指标的比较值浮动提示
     */
    public static String QUOTE_FLOATTIP = "“左右”和“等于”等操作，我们已为您将数值上下浮动10%查询.";
    
    /** 技术指标没有默认操作 */
    public static String QUOTE_CLASS_NO_DEFOP = "技术指标“%s”没有对应的操作";
    /** 技术指标不支持的分析周期 */
    public static String TECH_MIN_PERIOD = "技术指标分析周期“%s”";
    /** 技术指标不支持的指标形态组合 */
    public static String TECH_IDX_TECHOP_NOT_SUITABLE = "未找到形态“%s”和指标“%s”的对应关系";
    
    
    /** 技术指标不支持的指标形态组合,后接原因 */
    public static String TECH_IDX_TECHOP_NOT_SUITABLE_FMT = "技术指标“%s”的形态“%s”公式:%s";
    
    /** 技术指标不支持多周期 */
    public static String TECH_MULTI_PERIOD_NOT_SUPPORT = "目前暂不能回答“多个周期”的技术指标问题，我们正在努力解决中";
    /** 技术形态没有找到对应操作的指标 */
    public static String TECHOP_NO_IDX_BIND = "技术形态“%s”没有对应操作的指标";
    /** 技术指标没有找到对应操作 */
    public static String TECHIDX_NO_OP_BIND = "技术指标“%s”没有对应操作信息";
    /** 技术指标的数值参数不支持比较 */
    public static String TECH_PARAM_MUST_EQ = "技术指标“%s”的参数“%s”不支持比较操作";
    /** 不支持两个多线指标形态 */
    public static String TECH_OP_FOR_2_IDX = "不支持技术指标“%s和%s”的形态“%s”";
    /** 不支持多个参数 */
    public static String TECH_OP_FOR_MORE_THAN_2_LINE = "形态“%s”不支持大于2个指标参数";
    
    public static String TIP_PARSE_TEXT_BEG = "理解为：<em>";
    public static String TIP_PARSE_TEXT_END = "</em> 。 ";
    
    /** 目前仅支持hbase指标的count操作*/
    public static final String COUNT_NOT_SUPPORT = "暂不支持句子的某些‘统计操作’";
    /**伪指标大多为数字型，但用定义的语义信息替换后，可能无法被操作。此部分在绑定完善之前，暂不支持*/
    public static final String NOT_SUPPORTED_REPLACE_FAKE_INST_AS_OP_SON_STR = "暂不支持操作下的伪指标替换";
    /**
     * 暂不支持“五天内有2到3次”，可支持“五天内有2次”，可支持“五天内有超过2次”
     */
    public static String NOT_SUPPORTED_NUM_RANGE_FREQUENCY_INFO_STR = "暂不支持如“五天内有2到3次”";
    
    /**
     * 问新股指标时，不支持问其他指标
     */
    public static String NEWSTOCKINDEX_WITH_OTHER = "同时问新股指标和其他指标。";
    public static String NEWSTOCKINDEX = "新股相关指标查询。";
    
    public static String SEGGER_CONN_FAILED_STR = 
        "连接分词服务网络错误";
    public static String SEGGER_CLOSE_FAILED_STR = 
        "关闭分词服务网络错误";
    
    /** 已识别某指标，但我们尚没有相关数据 */
    public static String INDEX_NOT_AVIL_STR = "我们尚未准备“%s”的相关数据，您可向我们的客服反映。";
    
    /**当一个操作的两个操作对象都有形如“近5天内有3天”的时间信息时，给出此提示*/
    public static String  NOT_SUPPORTED_TWO_FREQUENCY_INFO_STR = "[建议]减少一个时间条件";
    
    /**当Double型数值遇到了一个单位符合但只能绑定整形的数值时，如“推荐买入家数1.5家”，给出提示*/
    public static String  LONG_ONLY_STR = "“%s”只能与整数关联";
    /**指标没有定义“复合增长”、“预测增长”、“累积增长”等对应指标时，给出提示*/
    public static String NOT_SUPPORTED_CHANGE_DATE_TYPE_FMT = "指标“%s”没有定义“%s”";
    
    public static String TAGGED_BY_SELF = "" ;
    
    public static final String NOT_SUPPORTED_TIME_FMT = "暂不支持“%s”的时间(Time)解析";
    
    /**技术面 n条线未配置提示*/
    public static String UNEXPECTED_TECH_MOD_FMT="未根据指标“%s”及提取key“%s”找到对应默认定义";
    
    public static final String UNEXPECTED_NODE_TYPE_FMT = "Unexpected Node Type:%s";
    
    public static void loadMessage(List<String> lines) throws DataConfException {
        
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if(line.length() == 0 || line.charAt(0) == '#') continue;
            
            int sepPos = line.indexOf('=');
            if(sepPos < 1) {
                throw new DataConfException(Param.MSG_DEF_FILE, i+1,
                        "Seperator \"=\" not found: %s", line);
            }
            
            String msgName = line.substring(0, sepPos).trim();
            String msgValue = line.substring(sepPos + 1).trim();
            
            try {
                Field field = MsgDef.class.getField(msgName);
                field.set(null, msgValue);
            } catch (NoSuchFieldException e) {
                //QueryParser.logger_.error(String.format("Unknown msg name [%s] at %s:%d",msgName, Param.MSG_DEF_FILE, i+1));
            } catch (Exception e) {
                throw new InternalError(e.getMessage());
            } 
        }
    }
}

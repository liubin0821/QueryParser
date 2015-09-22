package resouremanager.dict;

/**
 * 对词典的检查工作：
 * 1. 统一使用小写及半角
 * 2. 对于所有trans 词典中的词条A trans:B，一是若B在词典中未定义，给出警告；
 *	    二是若B trans: C，则直接改为A trans:C（即尽量减少多链转换） 
 * @author Administrator
 *
 */
public class DictCheck extends DictHandler {

}

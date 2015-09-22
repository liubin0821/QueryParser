package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.FakeNumType;
import com.myhexin.qparser.define.EnumDef.WebAction;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.util.Pair;

public class IndexInfo {
    /** 指标名到其信息的映射 */
    private static HashMap<String, IndexInfo> stkIndex_ = null;
    private static HashMap<String, IndexInfo> fundIndex_ = null;
    private static HashMap<String, HashMap<String, IndexInfo>> aliasIndex_ = null;

    public IndexInfo(String titleStr) {
        name = title = titleStr;
    }

    public static void loadStockIndexInfo(Document doc) throws DataConfException {
        stkIndex_ = IndexXmlReader.loadIndex(doc, Query.Type.STOCK);
        IndexInfo.createAlias();
    }

    public static void loadFundIndexInfo(Document doc) throws DataConfException {
        fundIndex_ = IndexXmlReader.loadIndex(doc, Query.Type.FUND);
    }

    /**
     * 获得指定类型的指标信息实例
     * @param indexTitle
     * @param type 指标类型
     * @return
     */
    public static IndexInfo getIndex(String indexTitle, Query.Type type) {
        if(type == Query.Type.FUND) {
            return fundIndex_.get(indexTitle);
        } else {
            return stkIndex_.get(indexTitle);
        }
    }

    public IFindParam getParamByTitle(String paramTitle) {
        for (IFindParam ifp : params) {
            if (ifp.title.equals(paramTitle))
                return ifp;
        }
        return null;
    }

    /**
     * 生成别名map
     *
     * @return
     */
    public static void createAlias()
    {
        Iterator<Entry<String, IndexInfo>> it = stkIndex_.entrySet().iterator();
        Entry<String, IndexInfo> entry;
        aliasIndex_ = new HashMap<String, HashMap<String, IndexInfo>>();

        while(it.hasNext())
        {
            entry = it.next();
            String name = entry.getKey();
            IndexInfo info = entry.getValue();
            //System.out.print("key: "+name+"\n");

            IndexInfo.putAliasInfo(name, name, info);

            Iterator<String> aliasit = info.getAliases().iterator();
            String aliasName;
            while(aliasit.hasNext())
            {
                aliasName = aliasit.next();
                IndexInfo.putAliasInfo(aliasName, name, info);
            }
        }        
        System.out.print("alias MAP create over\n");
    }

    public static void putAliasInfo(String aliasName, String name, IndexInfo info)
    {
        try{
        HashMap<String, IndexInfo> aliasItem = aliasIndex_.get(aliasName);
        if(aliasItem == null)
        {
            //System.out.print("\taliasName\t"+aliasName + "=="+name+"\n");
            aliasItem = new HashMap<String, IndexInfo>();
            aliasIndex_.put(aliasName, aliasItem);
        }
        aliasItem.put(name, info);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Set<String> getAliases()
    {
        return aliases.keySet();
    }
    
    public ArrayList<String> getAliasNode(String aliasName)
    {
        return aliases.get(aliasName);
    }

    public void putAliasNode(String aliasName)
    {
        ArrayList<String> alias;
        alias = aliases.get(aliasName);
        if(alias == null)
        {
            alias = new ArrayList<String>();
            aliases.put(aliasName, alias);
        }
    }

    public void putAliasNode(String aliasName, String confictsWords)
    {
        ArrayList<String> alias;
        alias = aliases.get(aliasName);
        if(alias == null)
        {
            alias = new ArrayList<String>();
            aliases.put(aliasName, alias);
        }
        alias.add(confictsWords);
    }
    
    /**
     * 取别名相关指标列表
     *
     * @param indexName 要查找的指标名或别名
     * @return 以指标标准名为key,指标结构为value的HashMap
     */
    public static HashMap<String, IndexInfo> getAliasesList(String indexName)
    {
        return aliasIndex_.get(indexName);
    }

    /**
     * 取别名相关解决冲突词汇
     *
     * @param aliasName 别名
     * @return 解决冲突词汇列表(ArrayList)
     */
    public ArrayList<String> getAliasConflicts(String aliasName)
    {
        return getAliasNode(aliasName);
    }


    /**
     * 取指标的缺省值
     */
    public String getValueDefault()
    {
        return valDefault;
    }
    public String getValueType()
    {
        return valType;
    }
    public String getValueUnit()
    {
        return valUnit;
    }
    public SemanticNode getValueRange()
    {
        // TODO
        return null;
    }

    public String id = null;
    public String name = null;
    public String title = null;
    public String unitList = null;

    public String description = null;
    public String valType = null;
    public String valUnit = null;
    public String valDefault = null;
    public String valMatchType = "String";
    public String peroidicity = "day";

    public String pubUnit = null;
    public String pubTitle = null;
    public SemanticNode defVal = null;
    public SemanticNode maxVal = null;
    public SemanticNode minVal = null;
    public NumNode movableRange = null;
    public boolean valIsMovable = true;
    public ArrayList<IFindParam> params = new ArrayList<IFindParam>();
    public HashMap<FakeNumType,SemanticNode> vagueNumInfo = new HashMap<FakeNumType,SemanticNode>();
    public HashMap<String,SemanticNode> vagueDateInfo = new HashMap<String,SemanticNode>();
    public HashMap<String, Pair<ClassNodeFacade, PropNodeFacade>> changes = 
        new HashMap<String, Pair<ClassNodeFacade, PropNodeFacade>>();
    public ArrayList<WebAction> webActions = new ArrayList<WebAction>();
    public HashMap<String, ArrayList<String>> aliases = new HashMap<String, ArrayList<String>>();
    

}

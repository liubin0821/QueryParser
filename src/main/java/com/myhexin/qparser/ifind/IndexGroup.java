package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public class IndexGroup {
    private static HashMap<String, IndexGroup> group_ = new HashMap<String, IndexGroup>();
    private IndexGroup() {}
    
    public static void loadInfo(Document doc) throws DataConfException{
        HashMap<String, IndexGroup> groupInfos = new HashMap<String, IndexGroup>();
        Element root = doc.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        if (infonodes.getLength() == 0) {
            throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1, "XML文件为空");
        }
       
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node indexI = infonodes.item(i);
            if (indexI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }else if (!indexI.getNodeName().equals("index-group")) {
                throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1,
                        "XML文件中未知节点：%s", indexI.getNodeName());
            }

            String title = indexI.getAttributes().getNamedItem("title")
                    .getNodeValue();
            if (groupInfos.containsKey(title)) {
                throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1,
                        "XML文件中节点重复：%s", title);
            }

            String defaultIndexStr = indexI.getAttributes()
                    .getNamedItem("default").getNodeValue();
            ClassNodeFacade defaultIndex = null;
            try {
                defaultIndex = MemOnto.getOnto(defaultIndexStr,
                        ClassNodeFacade.class, Query.Type.STOCK);
            } catch (UnexpectedException e) {
                throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1, e.getLogMsg());
            }
            
            
            IndexGroup newAddGI = new IndexGroup();
            getSingleGroupInfo(title,defaultIndex,indexI,newAddGI);
            groupInfos.put(title, newAddGI);
        }
        group_ =groupInfos;
    }
    
    private static void getSingleGroupInfo(String title,
            ClassNodeFacade defaultIndex, Node index, IndexGroup groupInfo) throws DataConfException {
        checkGroup(defaultIndex,title);
        groupInfo.defIndex = defaultIndex;
        NodeList infoNodes = index.getChildNodes();
        ArrayList<ClassNodeFacade> groupMembers = new ArrayList<ClassNodeFacade>();
        for (int i = 0; i < infoNodes.getLength(); i++) {
            Node infoI = infoNodes.item(i);

            if (infoI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!infoI.getNodeName().equals("member")) {
                throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1,
                        "XML文件中未知节点：%s", infoI.getNodeName());
            }
            String groupStr = infoI.getFirstChild().getNodeValue();
            ClassNodeFacade addIndex;
            try {
                addIndex = MemOnto.getOnto(groupStr, ClassNodeFacade.class, Query.Type.STOCK);
            } catch (UnexpectedException e) {
                throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1, e.getLogMsg());
            }
            checkGroup(addIndex,title);
            groupMembers.add(addIndex);
        }
        groupMembers.trimToSize();
        groupInfo.group=groupMembers.size()==0?null:groupMembers;
    }


    private static void checkGroup(ClassNodeFacade defaultIndex,String title) throws DataConfException {
        assert(defaultIndex!=null);
        String indexText = defaultIndex.getText();
        IndexInfo indexInfo = IndexInfo.getIndex(indexText, Query.Type.STOCK);
        if(indexInfo==null){
            throw new DataConfException(Param.STOCK_INDEX_GROUP_FILE, -1,
                    "IndexInfo中没有指标“%s”的信息",indexText);
        }
        indexInfo.pubTitle = title;
    }


    public static IndexGroup getGroupInfo(String title) {
        return group_.get(title);
    }

    public static ArrayList<ClassNodeFacade> getGroup(String title) {
        IndexGroup gi = getGroupInfo(title);
        if (gi == null)
            return null;
        return gi.group;
    }
    
    public static ArrayList<String> getMembersTitle(String title) {
        ArrayList<ClassNodeFacade> groupMembers = getGroup(title);
        if (groupMembers == null)
            return null;
        ArrayList<String> membersTitles = new ArrayList<String>();
        for (int i = 0; i < groupMembers.size(); i++) {
            membersTitles.add(groupMembers.get(i).getText());
        }
        return membersTitles;
    }

    public static ClassNodeFacade getDefIndex(String title) {
        IndexGroup gi = getGroupInfo(title);
        if (gi == null)
            return null;
        return gi.defIndex;
    }

    public static int getIndexGroupsSize() {
        if(group_==null){
            return -1;
        }
        return group_.size();
    }

    public static ArrayList<ClassNodeFacade> getAlterConds(String memberTitle){
        String indexGroupName = IndexInfo.getIndex(memberTitle,
                Query.Type.STOCK).pubTitle;
        String key = indexGroupName == null ? memberTitle : indexGroupName;
        return  getGroup(key);
    }
    
    public ClassNodeFacade defIndex = null;
    public ArrayList<ClassNodeFacade> group = null;
}

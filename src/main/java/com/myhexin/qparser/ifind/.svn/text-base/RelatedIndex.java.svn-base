package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;

public class RelatedIndex {
    private static HashMap<IndexInfo, IndexInfo[]> index2Related_ =
        new HashMap<IndexInfo, IndexInfo[]>();
    private RelatedIndex()  {}
    
    public static void loadRelatedIndex(ArrayList<String> lines) throws DataConfException {
        HashMap<IndexInfo, IndexInfo[]> index2Related =
            new HashMap<IndexInfo, IndexInfo[]>();
       for(int iLine = 0; iLine < lines.size(); iLine++) {
            String line = lines.get(iLine).trim();
            if(line.length() == 0 || line.charAt(0) == '#') { continue; }
            String[] fromTo = line.split("=");
            if(fromTo.length != 2) {
                throw new DataConfException("ifind_related_index", iLine,
                        "Failed to split using `='", line);
            }
            String from = fromTo[0].trim();
            IndexInfo iiFrom = IndexInfo.getIndex(from, Query.Type.STOCK);
            if(iiFrom == null) {
                throw new DataConfException("ifind_related_index", iLine,
                        "Not an iFind index: %s", from);
            }
            String[] tos = fromTo[1].split("\\|");
            ArrayList<IndexInfo> iiTo = new ArrayList<IndexInfo>();
            for(int i = 0; i < tos.length; i++) {
                String to = tos[i].trim();
                IndexInfo ii = IndexInfo.getIndex(to, Query.Type.STOCK);
                if(ii == null) {
                    throw new DataConfException("ifind_related_index", iLine,
                            "Not an iFind index: {}", to);
                }
                iiTo.add(ii);
            }
            index2Related.put(iiFrom, iiTo.toArray(new IndexInfo[0]));
       }
       index2Related_ = index2Related;
    }
    
    public static IndexInfo[] getRelatedIndex(String indexTitle) {
        IndexInfo ii = IndexInfo.getIndex(indexTitle, Query.Type.STOCK);
        if(ii == null) return null;
        return index2Related_.get(ii);
    }
}

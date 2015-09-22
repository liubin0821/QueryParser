package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef;
import com.myhexin.qparser.define.EnumDef.FakeNumType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;


public class FakeNumDefInfo {
    
    public FakeNumDefInfo(String curLabel) {
        this.label = curLabel;
    }


    public static void loadInfo(ArrayList<String> lines)
            throws DataConfException {
        HashMap<String,FakeNumDefInfo> fakeNumDefInfoTmp = new HashMap<String,FakeNumDefInfo>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty() || lines.get(i).startsWith("#")) {
                continue;
            }
            String[] infos = lines.get(i).split(";");
            if (infos.length != 3) {
                throw new DataConfException(Param.FAKE_NUM_DEF_INFO_FILE, i,
                        "格式错误");
            }
            String labelStr = infos[0];
            String fakeNumTypeStr = infos[1];
            String isDescendingStr = infos[2];
            boolean isBad = !labelStr.startsWith("label=");
            isBad |= !fakeNumTypeStr.startsWith("fakeNumType=");
            isBad |= !isDescendingStr.startsWith("isDescending=");
            if (isBad) {
                throw new DataConfException(Param.FAKE_NUM_DEF_INFO_FILE, i,
                        "格式错误");
            }
            labelStr = labelStr.replace("label=", "");
            fakeNumTypeStr = fakeNumTypeStr.replace("fakeNumType=", "");
            isDescendingStr = isDescendingStr.replace("isDescending=", "");
           
            FakeNumType fakeNumType = parseFakeNumType(fakeNumTypeStr);
            if (fakeNumType == null) {
                throw new DataConfException(Param.FAKE_NUM_DEF_INFO_FILE, i,
                        "fakeNumType格式错误:%s",fakeNumTypeStr);
            }
            boolean isDescending;
            try {
                isDescending = parseBooleanStr(isDescendingStr);
            } catch (UnexpectedException e) {
                throw new DataConfException(Param.FAKE_NUM_DEF_INFO_FILE, i,
                        e.getLogMsg());
            }
            
            String[] labels = labelStr.split("\\|");
            for (int k = 0; k < labels.length; k++) {
                String curLabel = labels[k];
                if (fakeNumDefInfo.containsKey(labelStr)) {
                    throw new DataConfException(Param.FAKE_NUM_DEF_INFO_FILE,
                            i, "信息重复");
                }
                FakeNumDefInfo addInfo = new FakeNumDefInfo(curLabel);
                addInfo.setDescending(isDescending);
                addInfo.setFakeNumType(fakeNumType);
                fakeNumDefInfoTmp.put(curLabel, addInfo);
            }
        }
        fakeNumDefInfo = fakeNumDefInfoTmp;
    }
    

    private static boolean parseBooleanStr(String booleanStr) throws UnexpectedException {
        if (booleanStr == null || !booleanStr.matches("^true|false$")) {
            throw new UnexpectedException("Unexpected boolean string:%s",
                    booleanStr);
        }
        return booleanStr.equals("true") ? true : false;
    }


    public static FakeNumType parseFakeNumType(String fakeNumTypeStr) {
        return fakeNumTypeStr.equals("more") ? FakeNumType.MORE
                : fakeNumTypeStr.equals("less") ? FakeNumType.LESS
                : fakeNumTypeStr.equals("flat") ? FakeNumType.FLAT
                : fakeNumTypeStr.equals("more_minus") ? FakeNumType.MORE_MINUS
                : fakeNumTypeStr.equals("less_minus") ? FakeNumType.LESS_MINUS
                : fakeNumTypeStr.equals("flat_minus") ? FakeNumType.FLAT_MINUS
                : null;
    }


    public void setFakeNumType(FakeNumType fakeNumType) {
        this.fakeNumType = fakeNumType;
    }

    public FakeNumType getFakeNumType() {
        return fakeNumType;
    }

    public void setDescending(boolean isDescending) {
        this.isDescending = isDescending;
    }

    public boolean isDescending() {
        return isDescending;
    }


    public static FakeNumDefInfo getFakeNumDefInfoByLabel(String label) {
        return fakeNumDefInfo.get(label);
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    private String label = null;
    private FakeNumType fakeNumType = null;
    private boolean isDescending = false;
    
    

    private static HashMap<String,FakeNumDefInfo> fakeNumDefInfo = new HashMap<String,FakeNumDefInfo>();
}

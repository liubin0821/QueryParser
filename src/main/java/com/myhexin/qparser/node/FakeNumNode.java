package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.FakeNumDefInfo;
import com.myhexin.qparser.define.EnumDef.FakeNumType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;

public class FakeNumNode extends NumNode {
	private FakeNumNode(){}
    public FakeNumNode(String text) {
        super(text);
    }

    public void parseNode(HashMap<String, String> k2v, Query.Type qtype)
            throws BadDictException {
        FakeNumDefInfo info = FakeNumDefInfo.getFakeNumDefInfoByLabel(text);
        if (info == null) {
            throw new BadDictException("伪数字未在配置文件中定义", NodeType.NUM, text);
        }
        fakeNumType = info.getFakeNumType();
        isDescending = info.isDescending();
    }

    public void exchangeFakeNumType() {
        isDescending = !isDescending;
        if (fakeNumType == null) {
            return;
        }
        fakeNumType = fakeNumType == FakeNumType.MORE ? FakeNumType.MORE_MINUS
                : fakeNumType == FakeNumType.MORE_MINUS ? FakeNumType.MORE
                        : fakeNumType;
        fakeNumType = fakeNumType == FakeNumType.LESS ? FakeNumType.LESS_MINUS
                : fakeNumType == FakeNumType.LESS_MINUS ? FakeNumType.LESS
                        : fakeNumType;
        fakeNumType = fakeNumType == FakeNumType.FLAT ? FakeNumType.FLAT_MINUS
                : fakeNumType == FakeNumType.FLAT_MINUS ? FakeNumType.FLAT
                        : fakeNumType;
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


    private FakeNumType fakeNumType = null;
    private boolean isDescending = false;
}

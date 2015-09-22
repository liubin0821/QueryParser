package com.myhexin.qparser.onto;

import java.util.HashMap;
import java.util.List;

import com.myhexin.qparser.except.UnexpectedException;

class UserClassNode extends ClassNode {

    public UserClassNode(String text) {
        super(text);
    }
    
    
    public UserPropNode getFakePropByID(String idStr)
            throws UnexpectedException {
        return this.getAllFakePropInfo().get(idStr);
    }
    
    public HashMap<String, UserPropNode> getAllFakePropInfo()
            throws UnexpectedException {
        List<PropNode> allProps = this.props;
        HashMap<String, UserPropNode> fakePropInfo = new HashMap<String, UserPropNode>();
        for (int i = 0; i < allProps.size(); i++) {
            PropNode curProp = allProps.get(i);
            if (!curProp.isFake()) {
                throw new UnexpectedException("FakeClass“%s”的属性“%s”非伪属性", getText(),
                        curProp.getText());
            }
            UserPropNode curFakeProp = (UserPropNode) curProp;
            String curID = curFakeProp.getIdByFakeClass(this);
            if (fakePropInfo.containsKey(curID)) {
                throw new UnexpectedException("FakeClass“%s”的属性“%s”ID“%s”重复",
                		getText(), curProp.getText(), curID);
            }
            fakePropInfo.put(curID, curFakeProp);
        }
        return fakePropInfo;
    }
}

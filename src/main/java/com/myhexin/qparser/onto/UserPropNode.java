package com.myhexin.qparser.onto;

import java.util.HashMap;

import com.myhexin.qparser.except.UnexpectedException;

class UserPropNode extends PropNode {

    public UserPropNode(String text) {
        super(text);
    }

    public void addID(UserClassNode fakeClass, String idNumStr)
            throws UnexpectedException {
        if (id.containsKey(fakeClass) && id.get(fakeClass) != idNumStr) {
            throw new UnexpectedException(
                    "Already has: %s with different id :%d", fakeClass.getText(),
                    idNumStr);
        }
        id.put(fakeClass, idNumStr);
    }

    public String getIdByFakeClass(UserClassNode fakeClass) {
        if (!id.containsKey(fakeClass)) {
            return null;
        }
        return id.get(fakeClass);
    }

    private HashMap<UserClassNode, String> id = new HashMap<UserClassNode, String>();

}

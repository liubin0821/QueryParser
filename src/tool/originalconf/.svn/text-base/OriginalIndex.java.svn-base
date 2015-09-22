package originalconf;

import java.util.ArrayList;
import java.util.List;

public class OriginalIndex {
    private String name;
    private String title;
    private String type;
    private String parent;
    private List<OriginalParam> params = new ArrayList<OriginalParam>();
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public String getParent() {
        return parent;
    }
    
    public String toString(){
        return String.format("title:%s;parent:%s", title,parent);
    }
    public void setParams(List<OriginalParam> params) {
        this.params = params;
    }
    public OriginalParam getOriginalParamByParamName(String paramName){
        if(params.isEmpty()){
            return null;
        }
        for(OriginalParam param:params){
            if(param.getTitle().equals(paramName)){
                return param;
            }
        }
        return null;
    }
    
    public void addParam(OriginalParam param){
        if(getOriginalParamByParamName(param.getTitle())==null){
            this.params.add(param);
        }
    }
    
    public List<OriginalParam> getParams() {
        return params;
    }
}

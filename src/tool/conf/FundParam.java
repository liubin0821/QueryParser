package conf;

public class FundParam{
	//<param name="F14" type="dt_integer" title="基金分类" param_list="P00152" default="1" />
	private String param_name;
	private String param_type;
	private String param_title;
	private String param_list;
	private String param_default;
	public String getParam_name() {
		return param_name;
	}
	public void setParam_name(String param_name) {
		this.param_name = param_name;
	}
	public String getParam_type() {
		return param_type;
	}
	public void setParam_type(String param_type) {
		this.param_type = param_type;
	}
	public String getParam_title() {
		return param_title;
	}
	public void setParam_title(String param_title) {
		this.param_title = param_title;
	}
	public String getParam_list() {
		return param_list;
	}
	public void setParam_list(String param_list) {
		this.param_list = param_list;
	}
	public String getParam_default() {
		return param_default;
	}
	public void setParam_default(String param_default) {
		this.param_default = param_default;
	}
	
}
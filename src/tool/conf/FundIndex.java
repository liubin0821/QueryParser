package conf;
import java.util.ArrayList;

public class FundIndex {
	private String name;
	private String title;
	private String type;
	private String unit;
	private String unit_list;
	public ArrayList<FundParam> paramList = new ArrayList<FundParam>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getUnit_list() {
		return unit_list;
	}
	public void setUnit_list(String unit_list) {
		this.unit_list = unit_list;
	}    
}

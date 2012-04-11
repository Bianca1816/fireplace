package com.fireplace.software;

public class ItemSkel {
	private String id;
	private String label;
	private String path;
	private String icon;
	private String description;
	private String ptype;
	private String devel;
	private String status;
	
	public ItemSkel(){
		
	}
	
	public ItemSkel(String ID, String Label, String Path, String Description, String Ptype, String Developer, String Status) {
		this.id = ID;
		this.label = Label;
		this.path = Path;
		this.description = Description;
		this.ptype = Ptype;
		this.devel = Developer;
		this.status = Status;
	}
	
	public String getId() {
		return id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getDeveloper() {
		return devel;
	}
	public void setDeveloper(String developer) {
		this.devel = developer;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

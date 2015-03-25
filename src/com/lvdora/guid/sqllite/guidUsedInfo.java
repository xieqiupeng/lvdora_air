package com.lvdora.guid.sqllite;
/**
 * 本class用于操作menu表
 * @author Eagle
 *
 */
public class guidUsedInfo {
	
	public static final String ID="id";
	//版本号
	public static final String ISSUE="issue";
	public static final String USED="used";
	
	public int id;
	public String issue;
	public int used;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIssue(String issue){
		this.issue=issue;
	}
	
	public String getIssue(){
		return this.issue;
	}
	
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
}

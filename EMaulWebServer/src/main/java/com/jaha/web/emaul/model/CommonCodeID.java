package com.jaha.web.emaul.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class CommonCodeID implements Serializable{

	private String code_group;
	private String code;
	private static final long serialVersionUID = 1L;
	
	public CommonCodeID(){}
	
	public CommonCodeID(String code_group, String code){
		this.code_group = code_group;
		this.code = code;
	}
	
    public boolean equals(Object o) { 
        return ((o instanceof CommonCodeID) && code_group == ((CommonCodeID)o).getCode_group() && code == ((CommonCodeID) o).getCode());
    }
    
	public int hashCode() { 
        return code_group.hashCode() + code.hashCode(); 
    }	

    public String getCode_group() {
		return code_group;
	}

	public void setCode_group(String code_group) {
		this.code_group = code_group;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

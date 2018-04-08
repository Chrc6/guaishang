package com.houwei.guaishang.bean;

import java.io.Serializable;

public class NameIDBean  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1719961789235879146L;
	private String id;
	private String name;

	


	public NameIDBean(){
		
	}
	
	
	public NameIDBean(String id,String name){
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}



}

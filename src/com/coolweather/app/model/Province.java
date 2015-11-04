package com.coolweather.app.model;

public class Province {
	
	private int id;
	private String provinceName;
	private String provinceCode;
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setName(String name){
		provinceName=name;
	}
	
	public void setCode(String code){
		provinceCode=code;
	}
	
	public int getId(){
		return id;
	}
	
	public String getProvinceName(){
		return provinceName;
	}
	
	public String getProvinceCode(){
		return provinceCode;
	}
}

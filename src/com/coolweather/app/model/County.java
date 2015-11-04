package com.coolweather.app.model;

public class County {

	private int id;
	private String countyName;
	private String countyCode;
	private int cityId;
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setCountyName(String name){
		countyName=name;
	}
	
	public String getCountyName(){
		return countyName;
	}
	
	public void setCountyCode(String code){
		countyCode=code;
	}
	
	public String getCountyCode(){
		return countyCode;
	}
	
	public void setCityId(int id){
		cityId=id;
	}
	
	public int getCityId(){
		return cityId;
	}
}

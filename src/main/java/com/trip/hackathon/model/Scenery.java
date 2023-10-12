package com.trip.hackathon.model;

public class Scenery implements Comparable<Scenery>{

	private String poid;
	private String name;
	private String cityName;
	private String cityId;
	private int hot;
	private double lng;
	private double lat;
	private double visitDay;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getVisitDay() {
		return visitDay;
	}

	public void setVisitDay(double visitDay) {
		this.visitDay = visitDay;
	}

	@Override
	public int compareTo(Scenery s1) {
		if (this.getHot() >= s1.getHot()) {
			return 1;
		}else{
			return -1;
		}
	}


}

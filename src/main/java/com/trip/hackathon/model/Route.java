package com.trip.hackathon.model;

import java.util.ArrayList;

public class Route{

	private int poid;
	private String name;
	private String cityName;
	private int cityId;
	private int hot;
	private double lng;
	private double lat;
	private double visitDay;
	private ArrayList<Scenery> sceneryList;
	private double score;
	private double maxDay;
	private double minDay;

	public Route(){
		sceneryList = new ArrayList<Scenery>();
	}

	public int getPoid() {
		return poid;
	}

	public void setPoid(int poid) {
		this.poid = poid;
	}

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

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
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

	public ArrayList<Scenery> getSceneryList() {
		return sceneryList;
	}

	public void setSceneryList(ArrayList<Scenery> sceneryList) {
		this.sceneryList = sceneryList;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(double maxDay) {
		this.maxDay = maxDay;
	}

	public double getMinDay() {
		return minDay;
	}

	public void setMinDay(double minDay) {
		this.minDay = minDay;
	}
}

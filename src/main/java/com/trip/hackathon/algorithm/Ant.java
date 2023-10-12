package com.trip.hackathon.algorithm;

import com.trip.hackathon.model.*;
import com.trip.hackathon.util.DistanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Ant {
	/**
	 * 景点对象列表
	 */
	List<Scenery> sceneryList;
	/**
	 * 蚂蚁的路径
	 */
	private int[] tour;

	private double Q;

	/**
	 * 存储是否访问过某一城市，1代表访问过
	 */
	private int[] tabu;

	/**
	 * 城市被访问的概率
	 */
	private double[] p;

	/**
	 * 概率的总和
	 */
	private double pSum = 0.0;

	/**
	 * 蚂蚁当前走过的距离
	 */
	private double length;

	/**
	 * 游玩的天数上边界 默认为3
	 */
	double maxDay;
	double minDay;

	/**
	 * 记录当前路线的天数总和
	 */
	double curVisitDay;

	/**
	 * 景点个数
	 */
	private int count;

	/**
	 * 公式中的参数alpha
	 */
	private double alpha = 1.0;
	/**
	 * 公式中的参数beta
	 */
	private double beta = 2.0;

	/**
	 * 获得蚂蚁当前的路线
	 *
	 * @return
	 */
	public int[] getTour() {
		return tour;
	}

	/**
	 * 获得蚂蚁当前的长度
	 *
	 * @return
	 */
	public double getLength() {
		return length;
	}

	public Ant() {

	}

	/**
	 * 初始化蚂蚁的起始路径
	 *
	 */
	public void init(List<Scenery> sceneList, double Q, double minDay, double maxDay) {
		this.sceneryList = sceneList;
		this.count = sceneList.size();
		this.Q = Q;
		this.minDay = minDay;
		this.maxDay = maxDay;
		this.pSum = 0.0;
		this.p = new double[count];
		this.tabu = new int[count];
		this.tour = new int[count];
		for (int i = 0; i < count; i++) {
			tabu[i] = 0;
			tour[i] = -1;
			p[i] = 0.0;
		}
		int random = 0;
		p[random] = 0.0;
		tabu[random] = 1;
		tour[0] = random;
		curVisitDay = sceneList.get(random).getVisitDay();
	}

	/**
	 * 计算蚂蚁选择景点的概率
	 *
	 * @param pheromone
	 *            信息素
	 * @param score
	 *            热度
	 */
	public void calcProb(double[] pheromone, double[] score) {
		this.pSum = 0.0;
		double sum = 0.0;// 信息素概率总和
		// 公式中得分母部分
		for (int i = 0; i < count; i++) {
			if (tabu[i] == 0) {
				sum += Math.pow(pheromone[i], this.alpha)
						* (Math.pow(score[i], this.beta));
			}
		}
		// 公式中的分子部分
		for (int i = 0; i < count; i++) {
			if (tabu[i] == 1) {
				p[i] = 0.0;
			} else {
				p[i] = Math.pow(pheromone[i], this.alpha)
						* (Math.pow(score[i], this.beta)) / sum;
				pSum += p[i];
			}
		}
	}

	/**
	 * 通过信息素和距离计算轮盘赌注概率，选择下一个城市
	 *
	 * @param index
	 *            下一个城市在tour数组中的id
	 * @return 如果满足一切约束条件，则返回true；否则返回false
	 */
	public boolean selectNextCity(int index) {
		int select = getRandomCity(p);
		if(select==-1){
			return false;
		}
		double day = this.sceneryList.get(select).getVisitDay();
		// 检查当前路线的游玩时间是否合法
//		if(day<0.05){
//			return false;
//		}
		if (this.curVisitDay + day > maxDay) {
			return false;
		}
		this.curVisitDay += day;
		tour[index] = select;
		tabu[select] = 1;
		pSum -= p[select];
		p[select] = 0.0; // 选择过的城市概率设为0，以后就不会被选择到
		return true;
	}

	/**
	 * 使用轮盘赌注选择城市
	 *
	 * @param p
	 * @return
	 */
	private int getRandomCity(double[] p) {
		double selectP = new Random(System.currentTimeMillis()).nextDouble()
				* pSum;
		double sumSel = 0.0;
		for (int i = 0; i < count; i++) {
			sumSel += p[i];
			if (sumSel > selectP)
				return i;
		}
		return -1;
	}

//	/**
//	 * 计算蚂蚁当前走过的距离总和
//	 *
//	 */
//	public void calcTourLength(List<Scenery> sceneList) {
//		length = 0;
//		double viewCount = 0.0;
//		double days = 0.0;
//		for (int i = 0; i < count; i++) {
//			int tourId = tour[i];
//			if (tourId == -1) {
//				break;
//			}
//			Scenery scene = sceneList.get(tourId);
//			viewCount += scene.getHot();
//			days += scene.getVisitDay();
//		}
//
//		if (days <= minDay || days > maxDay) {
//			return;
//		}
//
//	}

	/**
	 * 计算蚂蚁当前走过的距离总和
	 *
	 */
	public void calcTourLength(List<Scenery> sceneList) {
		length = 0;
		double hot = 0.0;
		double days = 0.0;
		double dis = 0.0;
		double lng = 0.0;
		double lat = 0.0;
		for (int i = 0; i < count; i++) {
			int tourId = tour[i];
			if (tourId == -1) {
				break;
			}
			Scenery scene = sceneList.get(tourId);
			hot += scene.getHot();
			days += scene.getVisitDay();
			if(i != 0){
				dis += DistanceUtil.getDistance(lng, lat, scene.getLng(), scene.getLat());
			}
			lng = scene.getLng();
			lat =scene.getLat();
		}

		if (days <= minDay || days > maxDay) {
			return;
		}
		double rho = 0.9;
//		double fx = (1.0 - rho) * Math.pow(1.0 / (10.0 + price), 1.0);
//		double gx = rho * Math.pow(1.0 / (10.0 + this.Q - viewCount), 1.0 / 3.0);
		double fx = (1 - rho)*(10000.0 / (dis));
		double gx =  rho * Math.pow(hot, 1.0/3.0);
		this.length = fx + gx;

	}
}

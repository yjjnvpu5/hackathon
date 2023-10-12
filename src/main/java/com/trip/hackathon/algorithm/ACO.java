package com.trip.hackathon.algorithm;

import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.model.Route;
import com.trip.hackathon.util.DistanceUtil;

import java.util.*;

public class ACO {

	/**
	 * 景点的列表
	 */
	List<Scenery> sceneList;
	
	double minDay;
	/**
	 * 游玩的天数
	 */
	double maxDay;
	/**
	 * 蚂蚁对象数组
	 */
	Ant[] ants;
	/**
	 * 蚂蚁的数量
	 */
	int antCount;
	/**
	 * ant-cycle模型中的Q,信息素的总量
	 */
	private double Q ;
	/**
	 * 景点的信息素 公式中的tao
	 */
	double[] pheromone;
	/**
	 * 景点的权重分数
	 */
	double[] score;
	/**
	 * 景点的数量
	 */
	int sceneCount;
	/**
	 * 最好的蚂蚁id
	 */
	int bestAntId = 0;
	/**
	 * 最优蚂蚁走过的路线
	 */
	int[] bestTour;
	/**
	 * 最优蚂蚁走过的长度
	 */
	double bestLength;

	public ACO() {
		this.maxDay = 3.0;
	}

	/**
	 * 初始化蚁群
	 * 
	 * @param sceneList
	 *            景点的列表
	 * @param antCount
	 *            蚂蚁的数量
	 * @param maxDay
	 *            游玩的天数
	 */
	public void init(List<Scenery> sceneList, int antCount, double minDay, double maxDay) {
		this.sceneList = sceneList;
		this.antCount = antCount;
		this.minDay = minDay;
		this.maxDay = maxDay;
		
		ants = new Ant[antCount];
		sceneCount = sceneList.size();
		// 初始化信息素
		pheromone = new double[sceneCount];
		score = new double[sceneCount];
		
		//select max hot of all scenery
		this.Q = sceneList.get(0).getHot();
		double disTop =0.0;
		for (int i = 0; i < sceneCount; i++) {
			double tmpHot = sceneList.get(i).getHot();
			double tmpDis = DistanceUtil.getDistance(sceneList.get(0).getLng(),sceneList.get(0).getLat(),sceneList.get(i).getLng(),sceneList.get(i).getLat());
			if(tmpDis>disTop){
				disTop=tmpDis;
			}
			if(tmpHot > this.Q){
				this.Q = tmpHot;
			}
		}

		//initialize the pheromone and score
		for (int i = 0; i < sceneCount; i++) {
			double dis = DistanceUtil.getDistance(sceneList.get(0).getLng(),sceneList.get(0).getLat(),sceneList.get(i).getLng(),sceneList.get(i).getLat());
			pheromone[i] = disTop/dis;
			score[i] = (double) sceneList.get(i).getHot() / this.Q;
		}
		
		bestLength = Integer.MAX_VALUE;
		bestTour = new int[sceneCount];
		for (int i = 0; i < antCount; i++) {
			ants[i] = new Ant();
			ants[i].init(sceneList, this.Q, minDay, maxDay);
		}
	}

	/**
	 * 蚁群算法的运行入口
	 * 
	 * @param maxgen
	 *            运行最大的代数
	 */
	public ArrayList<Route> run(int maxgen) {
		//save the local ant tour route
		ArrayList<int[]> antTourList  = new ArrayList<int[]>();
		//save the local scene score
		ArrayList<Double> scoreList = new ArrayList<Double>();
		for (int gen = 0; gen < maxgen; gen++) {
			// 每一只蚂蚁的移动过程
			for (int i = 0; i < antCount; i++) {
				// 对该蚂蚁进行城市路线选择
				ants[i].calcProb(pheromone, score);
				for (int j = 1; j < sceneCount; j++) {
					// select需要增加一个返回值
					if (!ants[i].selectNextCity(j)) {
						break;
					}
				}
				// 计算该蚂蚁爬过的路线总长度
				ants[i].calcTourLength(sceneList);
				// 判断是否为最优路线
				if (ants[i].getLength() < bestLength) {
					// 保存最优代
					bestAntId = i;
					bestLength = ants[i].getLength();
					int[] tmpTour = new int[sceneList.size()];
					
//					System.out.println("第" + gen + "代, 蚂蚁" + i + "，发现新的解为："
//							+ bestLength);
					for (int j = 0; j < sceneCount; j++) {
						bestTour[j] = ants[i].getTour()[j];
						tmpTour[j] = bestTour[j];
						if (bestTour[j] != -1) {
							//System.out.print(sceneList.get(bestTour[j]).getCityName()+":"+sceneList.get(bestTour[j]).getName() + " "+sceneList.get(bestTour[j]).getVisitDay()+" ");
						}
					}
					antTourList.add(bestTour.clone());
					scoreList.add(bestLength);
					
					System.out.println();
				}
			}
			// 更新信息素
			updatePheromone();
			// 蚂蚁重新初始化
			for (int i = 0; i < antCount; i++) {
				ants[i].init(sceneList, this.Q, minDay, maxDay);
			}
		}
		Arrays.sort(this.pheromone);
		System.out.println("end");
		
		return this.decodeRoute(antTourList, scoreList);
	}

	/**
	 * 更新信息素,使用ant-cycle模型 <br/>
	 * 公式1: T_ij(t+1) = (1-r)*T_ij(t) + delta_T_ij(t) <br/>
	 * 公式2: delta_T_ij(t) = Q/L_k Q为常数，L_k为蚂蚁走过的总长度
	 */
	private void updatePheromone() {
		double rho = 0.5;
		// 信息素的衰减
		for (int i = 0; i < sceneCount; i++) {
			pheromone[i] *= (1 - rho);
		}
//		//普通蚁群算法，所有蚂蚁都留信息素，被访问过的城市信息素增加
//		for (int i = 0; i < antCount; i++) {
//			for (int j = 0; j < sceneCount; j++) {
//				int curId = ants[i].getTour()[j];
//				if (curId != -1) {
//					// 如果改城市被访问过
//					pheromone[curId] += Q/ants[i].getLength();
//				} else {
//					return;
//				}
//			}
//		}

		// 最大最小蚂蚁, 只有最优化蚂蚁才留信息素
		for (int i = 0; i < sceneCount; i++) {
			int curId = bestTour[i];
			if (curId != -1) {
				// 如果改城市被访问过
				pheromone[curId] += ants[bestAntId].getLength() / Q;
				if(pheromone[curId] <= 0.0001){
					pheromone[curId] = 0.0001;
				}
			} else {
				return;
			}
		}
	}

	public ArrayList<Route> decodeRoute(ArrayList<int[]> antTourList, ArrayList<Double> scoreList){
		int len = antTourList.size();
		HashMap<String, Integer> routeMap = new HashMap<String, Integer>();
		ArrayList<Route> routeList = new ArrayList<Route>();

		for(int i = 0; i < len; i++){
			int[] tmpTour = antTourList.get(i);
			double score = scoreList.get(i); //need to save ant object
			double days = 0.0;
			int hot = 0;
			Route route = new Route();
			ArrayList<Scenery> sList = new ArrayList<Scenery>();
			for (int j = 0; j < tmpTour.length; j++) {
				if(tmpTour[j] == -1){
					break;
				}
				Scenery tmpScene = this.sceneList.get(tmpTour[j]);
				days += tmpScene.getVisitDay();
				hot += tmpScene.getHot();
				sList.add(tmpScene);
			}
			route.setMaxDay(maxDay);
			route.setMinDay(minDay);
			route.setVisitDay(days);
			route.setScore(score);
			route.setHot(hot);
			route.setSceneryList(sList);
			routeList.add(route);

		}
		
		//sort the routeList
		Collections.sort(routeList, new Comparator<Route>() {

			@Override
			public int compare(Route o1, Route o2) {
				if(o1.getScore() < o2.getScore()){
					return 1;
				}else{
					return -1;
				}
			}
		});
		
		System.out.println("decode end");
		return routeList;
	}
	
	

	/**
	 * 打印路径长度
	 */
	public void reportResult() {
		System.out.println("最优路径长度是" + bestLength);
		for (int j = 0; j < sceneCount; j++) {
			if (bestTour[j] != -1) {
				System.out.print(sceneList.get(bestTour[j]).getName() + " ");
			} else {
				return;
			}
		}
	}
}

package com.trip.hackathon.tsp;

import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.Route;
import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.service.RoutService;
import com.trip.hackathon.util.DistanceUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
//		ACO aco = new ACO();
//		Map<String, List<Scenery>> stringListMap = DataReader.readScenery("./热门城市部分POI数据0926.csv");
//		List<Scenery> list = stringListMap.get("294");
//		list.addAll(stringListMap.get("293"));
//		List<Scenery> collect = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList()).subList(0,10);
//		Map<String,String> distanceMap = DataReader.readDis("./热门城市部分小交通信息0926v1.csv");
//		double[][] distance = DistanceUtil.distance(distanceMap,collect);
//		aco.init(collect,500,distance);
//		aco.run(500);
//		aco.reportResult();
//		List<Long> result = aco.getResult();

		RoutService routService=new RoutService();
		routService.route(4.0, 6.0, Arrays.asList("Japan"), false);
	}
}

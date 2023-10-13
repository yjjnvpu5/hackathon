package com.trip.hackathon.algorithm;

import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.*;
import com.trip.hackathon.service.RoutService;
import com.trip.hackathon.util.DistanceUtil;

import java.util.*;
import java.util.stream.Collectors;

public class MMASMain {

	public static void main(String[] args) {
		testOne();
//		RoutService routService=new RoutService();
//		List<Route> route = routService.route(4.0, 6.0, Arrays.asList("Japan"), false);
//		System.out.println();
	}

	public static void testOne() {
		System.out.println("begin");
		long beginT = System.currentTimeMillis();
		long beginM = Runtime.getRuntime().freeMemory();

		List<Scenery> list =DataReader.readScenery("./热门城市部分POI数据0926.csv").get("2");
		Map<String, List<String>> stringListMap = DataReader.readCountry("./全球热门城市列表0926.csv");
		List<Scenery> collect = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
		ACO aco = new ACO();
		aco.init(collect,list.size()*2, 4.0, 6.0);
		ArrayList<Route> routeList = aco.run(1000);
		// aco.reportResult();

		System.out.println("-----split-------");
		int subLen = routeList.size();
		subLen = subLen > 20 ? 20 : subLen - 1;

		List<Route> topNList = routeList.subList(0, subLen);

		long tmpDelay = System.currentTimeMillis() - beginT;
		long tmpMem = (beginM - Runtime.getRuntime().freeMemory())
				/ (1024 * 1024);
		System.out.print("最优解：" + routeList.get(0).getScore());
		System.out.println("  耗时：" + tmpDelay + "ms  内存：" + tmpMem + "M");
	}
}

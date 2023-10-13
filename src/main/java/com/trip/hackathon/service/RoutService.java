package com.trip.hackathon.service;

import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.tsp.ACO;
import com.trip.hackathon.util.DistanceUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangqiwei
 * @Description
 */

@Component
public class RoutService {
    public Map<String, List<Scenery>> sceneryList;
    public Map<String, List<String>> countryMap;

    public Map<String, String> distanceMap;

    public void init() {
        sceneryList = DataReader.readScenery("./热门城市部分POI数据0926.csv");
        countryMap = DataReader.readCountry("./全球热门城市列表0926.csv");
        distanceMap = DataReader.readDis("./热门城市部分小交通信息0926v1.csv");
    }

    public List<Long> route(double minDay, double maxDay, List<String> ids, boolean isCity) {
        if (CollectionUtils.isEmpty(sceneryList)) {
            init();
        }
        ACO aco = new ACO();
        List<Scenery> list = getScenery(ids,isCity, minDay, maxDay);

        List<Scenery> sortList = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
        double[][] distance = DistanceUtil.distance(distanceMap, sortList);
        aco.init(sortList, 500, distance);
        aco.run(100);
//        return aco.getResult();
        aco.reportResult();
        return null;
    }

    private List<Scenery> getScenery(List<String> ids,boolean isCity, double minDay, double maxDay) {
        List<Scenery> list = new ArrayList<>();
        if (isCity) {
            ids.forEach(id -> {
                list.addAll(sceneryList.get(id));
            });
        } else {
            ids.forEach(id -> {
                List<String> cityIds = countryMap.get(id);
                for (String cityId : cityIds) {
                    list.addAll(sceneryList.get(cityId));
                }
            });
        }
        List<Scenery> sortList = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
        return choicePoi(sortList,minDay,maxDay,isCity);
    }

    private List<Scenery> choicePoi(List<Scenery> sceneryList, double minDay, double maxDay, boolean isCity) {
        List<Scenery> list=new ArrayList<>();
        double[][] distance = DistanceUtil.distance(distanceMap, sceneryList);
        double day=0.0;
        for (int i = 0; i <sceneryList.size() ; i++) {
            if(distance[0][i]<150000*minDay){
                day +=sceneryList.get(i).getVisitDay();
                if(day>maxDay/2.5){
                    break;
                }
                list.add(sceneryList.get(i));
            }
        }
        return list;
    }

}

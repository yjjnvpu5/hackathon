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

    public List<List<String>> route(double minDay, double maxDay, List<String> ids, boolean isCity) {
        Map<String,Integer> visitCityMap =new HashMap<>();
        if(isCity){
            return Collections.singletonList(handleOneWay(visitCityMap,minDay, maxDay, ids, isCity));
        }
        List<List<String>> list=new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            List<String> way = handleOneWay(visitCityMap,minDay, maxDay, ids, isCity);
            if(!CollectionUtils.isEmpty(way)){
                list.add(way);
            }
        }
        return list;
    }


    public List<String> handleOneWay(Map<String,Integer> visitCityMap,double minDay, double maxDay, List<String> ids, boolean isCity) {
        if (CollectionUtils.isEmpty(sceneryList)) {
            init();
        }
        ACO aco = new ACO();
        List<Scenery> list = getScenery(visitCityMap,ids,isCity, minDay, maxDay);
        if(CollectionUtils.isEmpty(list)){
            System.out.println("getScenery empty");
            return Collections.emptyList();
        }
        List<Scenery> sortList = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
        double[][] distance = DistanceUtil.distance(distanceMap, sortList);
        aco.init(sortList, 500, distance);
        aco.run(100);
        return aco.getResult();
    }

    private List<Scenery> getScenery(Map<String,Integer> visitCityMap,List<String> ids,boolean isCity, double minDay, double maxDay) {
        List<Scenery> list = new ArrayList<>();
        if (isCity) {
            ids.forEach(id -> {
                list.addAll(sceneryList.get(id));
            });
            System.out.println("getScenery prepare city size: " + ids.size());
        } else {
            ids.forEach(id -> {
                List<String> cityIds = countryMap.get(id);
                for (String cityId : cityIds) {
                    list.addAll(sceneryList.get(cityId));
                }
            });
        }
        List<Scenery> sortList = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
        System.out.println("geScenery prepare size： " + sortList.size());
        return choicePoi(visitCityMap,sortList,minDay,maxDay,isCity);
    }

    private List<Scenery> choicePoi(Map<String,Integer> visitCityMap,List<Scenery> sceneryList, double minDay, double maxDay, boolean isCity) {
        List<Scenery> list=new ArrayList<>();
        int selectCity =0;
        for (int i = 0; i < sceneryList.size(); i++) {
            if(!visitCityMap.containsKey(sceneryList.get(i).getCityId())){
                selectCity =i;
                visitCityMap.put((sceneryList.get(i).getCityId()),1);
                break;
            }
        }
        double[][] distance = DistanceUtil.distance(distanceMap, sceneryList);
        double day=0.0;
        int cityNum =0;
        int num =0;
        Map<String,Integer> map =new HashMap<>();
        for (int i = 0; i <sceneryList.size() ; i++) {
            if(distance[selectCity][i]<50000*minDay){
                String poid = sceneryList.get(i).getCityId();
                if(map.containsKey(poid)){
                    if(day>maxDay/2.5){
                        break;
                    }
                    if(num<maxDay ||map.get(poid)*2<num){
                        day +=sceneryList.get(i).getVisitDay();
                        list.add(sceneryList.get(i));
                        map.put(poid,map.get(poid)+1);
                        num++;
                    }

                }else {
                    if(cityNum <2){
                        cityNum++;
                        day +=sceneryList.get(i).getVisitDay();
                        list.add(sceneryList.get(i));
                        map.put(poid,1);
                        num++;
                        visitCityMap.put(sceneryList.get(i).getCityId(),1);
                    }
                }
            }
        }
        return list;
    }


}

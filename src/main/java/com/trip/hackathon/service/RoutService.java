package com.trip.hackathon.service;

import com.trip.hackathon.algorithm.ACO;
import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.Route;
import com.trip.hackathon.model.Scenery;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangqiwei
 * @Description
 */

@Component
public class RoutService {
    public Map<String, List<Scenery>> sceneryList;
    public Map<String,List<String>> countryMap;

    public void init(){
        sceneryList = DataReader.readScenery("./热门城市部分POI数据0926.csv");
        countryMap = DataReader.readCountry("./全球热门城市列表0926.csv");
    }

    public List<Route> route(double minDay,double maxDay,List<String> ids,boolean isCity){
        if(CollectionUtils.isEmpty(sceneryList)){
            init();
        }
        ACO aco = new ACO();
        List<Scenery> list=new ArrayList<>();
        if(isCity){
            ids.forEach(id->{
                list.addAll(sceneryList.get(id));
            });
        }else {
            ids.forEach(id->{
                List<String> cityIds = countryMap.get(id);
                for (String cityId : cityIds) {
                    list.addAll(sceneryList.get(cityId));
                }
            });
        }
        List<Scenery> sortList = list.stream().sorted(Comparator.comparing(Scenery::getHot).reversed()).collect(Collectors.toList());
        aco.init(sortList,sortList.size()*2, minDay, maxDay);
        ArrayList<Route> routeList = aco.run(1000);
        return routeList;
    }
}

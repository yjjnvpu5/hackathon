package com.trip.hackathon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.model.dto.DayRouteInfoDTO;
import com.trip.hackathon.service.DataInfoService;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author chenyanghuang
 * @create 2023-10-14 10:33
 * @Description
 */

@RestController
@RequestMapping("route")
public class RouteController {
  @Autowired
  DataInfoService dataInfoService;

  // city中文名，cityId
  static Map<String, String> CITY_CODE_NAME_DICT = new HashMap<>();
  private static Map<String, String> countryNameMap;

  @PostConstruct
  public void init0() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    CITY_CODE_NAME_DICT = DataReader.readScenery("./热门城市部分POI数据0926.csv").values().stream().flatMap(e -> e.stream())
        .collect(Collectors.toMap(e -> e.getCityName().trim(), e -> e.getCityId(), (k1, k2) -> k1));
    System.out.println("CITY_CODE_NAME_DICT Size=" + CITY_CODE_NAME_DICT.size());
    countryNameMap = mapper.readValue(new File("国家中英文对照.json"), new TypeReference<Map<String, String>>() {});
    System.out.println("countryNameMap Size=" + countryNameMap.size());
  }

  @ToString
  static class Req {
    double minDay;
    double maxDay;
    // 可能全都是城市中文名，或者都是国家英文名
    List<String> targetNames;

    public double getMinDay() {
      return minDay;
    }

    public void setMinDay(double minDay) {
      this.minDay = minDay;
    }

    public double getMaxDay() {
      return maxDay;
    }

    public void setMaxDay(double maxDay) {
      this.maxDay = maxDay;
    }

    public List<String> getTargetNames() {
      return targetNames;
    }

    public void setTargetNames(List<String> targetNames) {
      this.targetNames = targetNames;
    }
  }

  @PostMapping("run")
  public List<List<DayRouteInfoDTO>> queryRouteList(@RequestBody Req req) {
    System.out.println(req.toString());
    List<String> realCityIdList = new ArrayList<>();
    boolean isCity = false;
    for (String c : req.getTargetNames()) {
      if (CITY_CODE_NAME_DICT.containsKey(c)) {
        isCity = true;
        realCityIdList.add(CITY_CODE_NAME_DICT.get(c));
      }
    }
    if (isCity) {
      req.targetNames = realCityIdList;
    } else {
      req.targetNames = req.targetNames.stream().map( n->countryNameMap.get(n)).filter(Objects::nonNull).collect(Collectors.toList());
    }
    System.out.println("before invoke service: CITY_CODE size="+ CITY_CODE_NAME_DICT.size() + ", iscity=" + isCity + ", targetNames=" + req.getTargetNames().toString() +"## "+"key: " +CITY_CODE_NAME_DICT.keySet());
    List<List<DayRouteInfoDTO>> lists = dataInfoService.queryRoute(req.minDay, req.maxDay, req.targetNames, isCity);
    return lists;
  }
}

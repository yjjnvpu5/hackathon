package com.trip.hackathon.controller;

import com.trip.hackathon.model.dto.DayRouteInfoDTO;
import com.trip.hackathon.service.DataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chenyanghuang
 * @create 2023-10-14 10:33
 * @Description
 */

@Controller
public class RouteController {
  @Autowired
  DataInfoService dataInfoService;

  @PostMapping("/queryRoute")
  public List<List<DayRouteInfoDTO>> queryRoute(
          @RequestParam("minDay") double minDay,
          @RequestParam("maxDay") double maxDay,
          @RequestParam("targetNames") List<String> targetNames,
          @RequestParam("isCity") boolean isCity) {
    return dataInfoService.queryRoute(minDay, maxDay, targetNames, isCity);
  }
}

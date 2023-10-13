package com.trip.hackathon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.hackathon.model.Route;
import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.model.basePojo.CityInfo;
import com.trip.hackathon.model.basePojo.PoiInfo;
import com.trip.hackathon.model.basePojo.PoiTrafficInfo;
import com.trip.hackathon.model.dto.DayRouteInfoDTO;
import com.trip.hackathon.service.DataInfoService;
import com.trip.hackathon.service.RoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@SpringBootTest
class HackathonApplicationTests {
    @Autowired
    DataInfoService dataInfoService;

    @Test
    void contextLoads() {
    }

    @Test
    void loadJsonFiles(){

        Map<Long, CityInfo> cityInfoMap;
        Map<Long, PoiInfo> poiInfoMap;
        Map<Long, Map<Long, PoiTrafficInfo>> poiTrafficInfoMap;
        ObjectMapper mapper = new ObjectMapper();
        try {
            cityInfoMap = mapper.readValue(new File("全球热门城市列表0926.json"), new TypeReference<Map<Long, CityInfo>>(){});
            poiInfoMap = mapper.readValue(new File("热门城市部分POI数据0926.json"), new TypeReference<Map<Long, PoiInfo>>(){});
            poiTrafficInfoMap = mapper.readValue(new File("热门城市部分小交通信息0926v1.json"), new TypeReference<Map<Long, Map<Long, PoiTrafficInfo>>>(){});
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSplitRoute(){
        // 拆分路线
        List<List<DayRouteInfoDTO>> multiRoutes = dataInfoService.queryRoute(1L);
        System.out.println(multiRoutes);
    }

}

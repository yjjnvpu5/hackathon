package com.trip.hackathon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.hackathon.model.POI;
import com.trip.hackathon.model.Route;
import com.trip.hackathon.model.Scenery;
import com.trip.hackathon.model.Traffic;
import com.trip.hackathon.model.basePojo.CityInfo;
import com.trip.hackathon.model.basePojo.PoiInfo;
import com.trip.hackathon.model.basePojo.PoiTrafficInfo;
import com.trip.hackathon.model.dto.DayRouteInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chenyanghuang
 * @create 2023-10-12 20:52
 * @Description
 */

@Slf4j
@Service
public class DataInfoService {
  @Autowired
  RoutService routeService;

  private static final double MAX_PLAY_HOUR = 9;

  /**
   * 热门城市信息
   */
  private static Map<Long, CityInfo> cityInfoMap;

  /**
   * 热门POI信息
   */
  private static Map<Long, PoiInfo> poiInfoMap;

  /**
   * poi之间的交通信息
   */
  private static Map<Long, Map<Long, PoiTrafficInfo>> poiTrafficInfoMap;

  /**
   * 载入
   */
  static {
    ObjectMapper mapper = new ObjectMapper();
    try {
      cityInfoMap = mapper.readValue(new File("全球热门城市列表0926.json"), new TypeReference<Map<Long, CityInfo>>() {});
      poiInfoMap = mapper.readValue(new File("热门城市部分POI数据0926.json"), new TypeReference<Map<Long, PoiInfo>>() {});
      poiTrafficInfoMap = mapper.readValue(new File("热门城市部分小交通信息0926v1.json"),
          new TypeReference<Map<Long, Map<Long, PoiTrafficInfo>>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 根据用户给的城市id（或国家、景点等）调用其炜哥的接口，拿到路线并填充信息
   * 
   * @param cityId
   * @return
   */
  public List<DayRouteInfoDTO> queryRoute(Long cityId) {
    // 调用其炜哥接口拿路线List 先mock一下
    List<Long> route = routeService.route(4.0, 6.0, Arrays.asList("Japan"), false);
    if (route.isEmpty()) {
      log.warn("无可用路线");
      return null;
    }
    // 拆分路线
    List<DayRouteInfoDTO> dayRouteInfoDTOList = splitRoute(route);

    return dayRouteInfoDTOList;
  }

  /**
   * 整体路线按天拆分并填充信息-其炜接口
   */
  private List<DayRouteInfoDTO> splitRoute(List<Long> sceneryList) {
    // 总路线列表
    List<DayRouteInfoDTO> dayRouteList = new ArrayList<>();

    List<PoiInfo> poiList = sceneryList.stream().map(scenery -> {
      PoiInfo poiInfo = poiInfoMap.getOrDefault(scenery, null);
      return poiInfo;
    }).filter(poiInfo -> poiInfo != null)
        // 游玩时间有问题的直接在这一步就过滤掉
        .filter(poiInfo -> poiInfo.getMinRecommendedPlayHour() != 0 && poiInfo.getMaxRecommendedPlayHour() != 0)
        .collect(Collectors.toList());

    // 按天数拆分
    int dayCount = 1;

    for (int i = 0; i < poiList.size();) {
      PoiInfo poiInfo = poiList.get(i);
      PoiInfo nextPoiInfo = i == poiList.size() - 1 ? null : poiList.get(i + 1);
      double avgPlayTime = (poiInfo.getMinRecommendedPlayHour() + poiInfo.getMaxRecommendedPlayHour()) / 2;

      if (avgPlayTime >= MAX_PLAY_HOUR) {
        // 超过一天的话当天就只玩该景点
        DayRouteInfoDTO singlePoiDayRoute = new DayRouteInfoDTO();
        singlePoiDayRoute.setTitle(generateTitle(dayCount));
        singlePoiDayRoute.setName(poiInfo.getCityName());
        List<POI> poiDetailList = new ArrayList<>();
        poiDetailList.add(poiMapper(poiInfo, nextPoiInfo));
        singlePoiDayRoute.setDetail(poiDetailList);
        dayRouteList.add(singlePoiDayRoute);
        // 天数 + 1
        dayCount++;
        i++;
      } else {
        // 从当前Poi作为起点向后遍历，构建一天的行程

        List<POI> poiDetailList = new ArrayList<>();
        // 总用时
        double totalUseTime = avgPlayTime;

        while (totalUseTime < MAX_PLAY_HOUR) {
          // 加入当天的POI列表
          poiDetailList.add(poiMapper(poiInfo, nextPoiInfo));

          // 没有后续POI则结束
          i++;
          if (i >= poiList.size() - 1) {
            break;
          }
          poiInfo = poiList.get(i);
          nextPoiInfo = poiList.get(i + 1);

          avgPlayTime = (poiInfo.getMinRecommendedPlayHour() + poiInfo.getMaxRecommendedPlayHour()) / 2;

          PoiTrafficInfo poiTrafficInfo =
              poiTrafficInfoMap.get(poiInfo.getPoiId()).getOrDefault(nextPoiInfo.getPoiId(), null);
          Long driveTime;
          if (poiTrafficInfo != null) {
            driveTime = poiTrafficInfo.getDriveTime();
          } else {
            driveTime = caculateDriveTime(poiInfo, nextPoiInfo);
          }
          totalUseTime = totalUseTime + avgPlayTime + second2hour(driveTime);
        }

        DayRouteInfoDTO singlePoiDayRoute = new DayRouteInfoDTO();
        singlePoiDayRoute.setTitle(generateTitle(dayCount));
        singlePoiDayRoute.setName(generateCityName(poiDetailList)); // 同一天涉及多城市的话 上海-苏州
        singlePoiDayRoute.setDetail(poiDetailList);
        dayRouteList.add(singlePoiDayRoute);
        dayCount++;
      }
    }
    return dayRouteList;
  }

  /**
   * 基于按天拆分好的路线填充信息-德储接口
   * 
   * @return
   */
  private List<DayRouteInfoDTO> fillRouteDetails(List<List<Long>> sceneryList) {
    // 总路线列表
    List<DayRouteInfoDTO> dayRouteList = new ArrayList<>();

    for (int i = 0; i < sceneryList.size(); i++) {
      // 获取第i+1天下的景点信息列表
      List<PoiInfo> poiIdList = sceneryList.get(i).stream().map(id -> {
        PoiInfo poiInfo = poiInfoMap.getOrDefault(id, null);
        return poiInfo;
      }).filter(poiInfo -> poiInfo != null).collect(Collectors.toList());

      // 对每一天下包含的POI列表进行填充
      List<POI> poiDetailList = new ArrayList<>();
      for (int j = 0; j < poiIdList.size(); j++) {
        PoiInfo poiInfo = poiIdList.get(j);
        PoiInfo nextPoiInfo = j == poiIdList.size() - 1 ? null : poiIdList.get(j + 1);
        poiDetailList.add(poiMapper(poiInfo, nextPoiInfo));
      }

      DayRouteInfoDTO dayRouteInfoDTO = new DayRouteInfoDTO();
      dayRouteInfoDTO.setTitle(generateTitle(i+1));
      dayRouteInfoDTO.setName(generateCityName(poiDetailList));
      dayRouteInfoDTO.setDetail(poiDetailList);
      dayRouteList.add(dayRouteInfoDTO);
    }
    return dayRouteList;
  }


  private String generateTitle(int day) {
    return String.format("第%s天", day);
  }

  private String generateCityName(List<POI> poiList) {
    return poiList.stream().map(p -> p.getDestinationName()).distinct().collect(Collectors.joining("-"));
  }

  private POI poiMapper(PoiInfo info, PoiInfo nextPoi) {
    POI poi = new POI();
    poi.setDestinationName(info.getCityName());
    poi.setCountry(Optional.ofNullable(cityInfoMap.get(info.getCityId()).getRegion()).orElse(""));
    poi.setDestinationId(info.getCityId());
    poi.setPoiId(info.getPoiId());
    poi.setPoiName(info.getPoiName());
    poi.setPoiEName(info.getPoiEnName());
    poi.setGrade(info.getHotScore());
    poi.setMinPlayTime(info.getMinRecommendedPlayHour());
    poi.setMaxPlayTime(info.getMaxRecommendedPlayHour());
    poi.setTraffic(trafficCreator(info, nextPoi));
    poi.setOpenDateInfoList(info.getOpenDateInfo());
    return poi;
  }

  /**
   * 计算从一个POI到下一个POI的信息
   * 
   * @param info
   * @param next
   * @return
   */
  private Traffic trafficCreator(PoiInfo info, PoiInfo next) {
    if (next == null) {
      return null;
    }
    PoiTrafficInfo poiTrafficInfo = poiTrafficInfoMap.get(info.getPoiId()).get(next.getPoiId());
    if (poiTrafficInfo == null) {
      return null;
    }
    Traffic traffic = new Traffic();
    traffic.setDepartPoiId(poiTrafficInfo.getFromPoiId());
    traffic.setArrivePoiid(poiTrafficInfo.getToPoiId());
    traffic.setDriveTime(poiTrafficInfo.getDriveTime());
    traffic.setDriveDistance(poiTrafficInfo.getDriveDistance());
    traffic.setWalkTime(poiTrafficInfo.getWalkTime());
    traffic.setWalkDistance(poiTrafficInfo.getWalkDistance());
    return traffic;
  }

  private double second2hour(Long seconds) {
    return Double.valueOf(seconds) / 3600;
  }

  /**
   * 基于谷歌经纬度计算行驶时间 60km/h 进行估计
   * 
   * @param from
   * @param to
   * @return
   */
  private Long caculateDriveTime(PoiInfo from, PoiInfo to) {
    GlobalCoordinates source = new GlobalCoordinates(from.getGoogleLatitude(), from.getGoogleLongitude());
    GlobalCoordinates target = new GlobalCoordinates(to.getGoogleLatitude(), to.getGoogleLongitude());
    // 单位是米
    double distance =
        new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
    // 60km/h = 16.66666666666m/s
    double usedTime = distance / 16.666666d;
    return (long) usedTime;
  }

}

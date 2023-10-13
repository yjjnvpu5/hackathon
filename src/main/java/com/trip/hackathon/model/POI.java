package com.trip.hackathon.model;

import com.trip.hackathon.model.basePojo.OpenDateRange;
import lombok.Data;

import java.util.List;

/**
 * @author chenyanghuang
 * @create 2023-10-12 20:59
 * @Description 单个景点POI的相关信息
 */
@Data
public class POI {
  /**
   * 目的地城市名称
   */
  private String destinationName;

  /**
   * 目的地地区名称（国家 or 城市,国家）
   */
  private String country;

  /**
   * 目的地城市ID
   */
  private Long destinationId;

  /**
   * 景点ID
   */
  private Long poiId;

  /**
   * 景点名称
   */
  private String poiName;

  /**
   * 景点英文名
   */
  private String poiEName;

  /**
   * 热度值
   */
  private Double grade;

  /**
   * 最小游玩时间
   */
  private Double minPlayTime;

  /**
   * 最大游玩时间
   */
  private Double maxPlayTime;

  /**
   * 交通信息
   */
  private Traffic traffic;

  /**
   * 开放日期信息
   */
  private List<OpenDateRange> openDateInfoList;
}

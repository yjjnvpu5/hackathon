package com.trip.hackathon.model.basePojo;

import java.util.List;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 15:50
 * @Description 景点POI开放时间
 */
@Data
public class OpenDateRange {
  /**
   * 开放日期范围 mm/dd "openDateRange":"01/01-06/30"
   */
  private String openDateRange;
  /**
   * 开放时间范围 mm:ss "openTimeRange":"09:00-18:00"
   */
  private String openTimeRange;

  /**
   * 每星期开放时间
   */
  private List<Integer> openWeekRange;

  /**
   * 计划游玩时间 mm:ss "planTimeRange":"17:30-17:30"
   */
  private String planTimeRange;

  /**
   * 规则类型
   */
  private String rule;

  /**
   * 规则ID
   */
  private Integer ruleId;
}

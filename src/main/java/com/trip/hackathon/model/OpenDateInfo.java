package com.trip.hackathon.model;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 21:04
 * @Description
 */
@Data
public class OpenDateInfo {
    /**
     * 开放日期
     */
    private String openDateRange;

    /**
     * 开放时间
     */
    private String openTimeRange;

    /**
     * 每周开放哪几天
     */
    private String openWeekRange;

    /**
     * 计划游玩时间
     */
    private String planTimeRange;

    private String rule;
    private String ruleId;
}

package com.trip.hackathon.model.dto;

import com.trip.hackathon.model.POI;
import lombok.Data;

import java.util.List;

/**
 * @author chenyanghuang
 * @create 2023-10-12 20:53
 * @Description 一天内的路线详情
 */
@Data
public class DayRouteInfoDTO {
    /**
     * 第n天
     */
    private String title;

    /**
     * 母线路目的地名称
     */
    private String name;

    /**
     * 线路下每个POI的具体详情信息
     */
    private List<POI> detail;

}

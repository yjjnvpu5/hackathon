package com.trip.hackathon.model.basePojo;

import java.util.List;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 15:43
 * @Description
 */

@Data
public class PoiInfo {
    /**
     * poi的目的地城市ID
     */
    private Long cityId;

    /**
     * poi的目的地名
     */
    private String cityName;

    /**
     * poi的Id
     */
    private Long poiId;

    /**
     * poi景点名称
     */
    private String poiName;

    /**
     * 景点英文名称
     */
    private String poiEnName;

    /**
     * 热度分
     */
    private Double hotScore;

    /**
     * 最小建议游玩小时数
     */
    private Double minRecommendedPlayHour;

    /**
     * 最大建议游玩小时数
     */
    private Double maxRecommendedPlayHour;

    /**
     * 百度经度
     */
    private Double baiduLongitude;

    /**
     * 百度纬度
     */
    private Double baiduLatitude;

    /**
     * 高德经度
     */
    private Double gaodeLongitude;

    /**
     * 高德纬度
     */
    private Double gaodeLatitude;

    /**
     * 谷歌经度
     */
    private Double googleLongitude;

    /**
     * 谷歌纬度
     */
    private Double googleLatitude;

    /**
     * 开放时间
     */
    private List<OpenDateRange> openDateInfo;
}

package com.trip.hackathon.model.basePojo;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 20:04
 * @Description POI之间交通信息
 */
@Data
public class PoiTrafficInfo {
    /**
     * 出发poiId
     */
    private Long fromPoiId;

    /**
     * 到达poiId
     */
    private Long toPoiId;

    /**
     * 驾车距离
     */
    private Long driveDistance;

    /**
     * 驾车时间
     */
    private Long driveTime;

    /**
     * 公交距离
     */
    private Long publicTransDistance;

    /**
     * 公交时间
     */
    private Long publicTransTime;

    /**
     *  步行距离
     */
    private Long walkDistance;

    /**
     * 步行时间
     */
    private Long walkTime;
}

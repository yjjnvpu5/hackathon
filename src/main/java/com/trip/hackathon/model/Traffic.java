package com.trip.hackathon.model;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 21:03
 * @Description
 */
@Data
public class Traffic {
    private Long departPoiId;
    private Long arrivePoiid;
    private Long driveDistance;
    private Long driveTime;
    private Long walkDistance;
    private Long walkTime;
}

package com.trip.hackathon.model.basePojo;

import lombok.Data;

/**
 * @author chenyanghuang
 * @create 2023-10-12 15:09
 * @Description
 */

@Data
public class CityInfo {
    /**
     * 目的地城市名
     */
    private String name;

    /**
     * 所属地区 Taiwan, China 或 China
     */
    private String region;

    /**
     * 目的地ID
     */
    private Long cityId;
}

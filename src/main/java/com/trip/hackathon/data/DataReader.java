package com.trip.hackathon.data;

import com.trip.hackathon.model.Scenery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataReader {

    public static Map<String,List<Scenery>> readScenery(String filename) {
        List<Scenery> sceneryList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                /**
                 * 目的地id	目的地名	poiid	poiname	poiename	热度分	最小建议游玩小时数	最大建议游玩小时数	百度纬度	百度经度	高德纬度	高德经度	谷歌纬度	谷歌经度	开放时间
                 * 0          1       2       3        4         5         6                  7            8       9       10       11     12     13       14
                  */
                Scenery scenery =new Scenery();
                scenery.setPoid(values[2]);
                scenery.setName(values[3]);
                scenery.setCityName(values[1]);
                scenery.setCityId(values[0]);
                scenery.setHot((int) (Double.parseDouble(values[5])*10));
                scenery.setLng(Double.parseDouble(values[12]));
                scenery.setLat(Double.parseDouble(values[13]));
                scenery.setVisitDay(values[6].equals("NULL")?0.5:new BigDecimal(values[6]).divide(BigDecimal.valueOf(12),2, BigDecimal.ROUND_HALF_UP).doubleValue());
                sceneryList.add(scenery);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sceneryList.stream().collect(Collectors.groupingBy(Scenery::getCityId));
    }

    public static Map<String,List<String>> readCountry(String filename) {
        Map<String,List<String>> map =new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                /**
                 *DestinationNameCN	Country/Region	目的地id
                 * 0                      1               2
                 */
              if(map.containsKey(values[1])){
                  map.get(values[1]).add(values[2]);
              }else {
                  List<String> countryList = new ArrayList<>();
                  countryList.add(values[2]);
                  map.put(values[1],countryList);
              }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}

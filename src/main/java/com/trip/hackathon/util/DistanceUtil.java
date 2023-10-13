package com.trip.hackathon.util;

import com.trip.hackathon.data.DataReader;
import com.trip.hackathon.model.Scenery;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangqiwei
 * @Description
 */
public class DistanceUtil {

    public static void main(String[] args) {
        System.out.println("经纬度距离计算结果：" + getDistance(31.1420032, 121.6655344, 31.1403808, 121.6746721) + "米");
    }

    public static double getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
        GlobalCoordinates source = new GlobalCoordinates(latitudeFrom, longitudeFrom);
        GlobalCoordinates target = new GlobalCoordinates(latitudeTo, longitudeTo);
        return new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
    }

    public static double getDistance(Scenery scenery1, Scenery scenery2) {
        GlobalCoordinates source = new GlobalCoordinates(scenery1.getLat(), scenery1.getLng());
        GlobalCoordinates target = new GlobalCoordinates(scenery2.getLat(), scenery2.getLng());
        return new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
    }

    public static double[][] distance(Map<String, String> map,List<Scenery> sceneryList){
        double[][] dis=new double[sceneryList.size()][sceneryList.size()];
        for (int i = 0; i < sceneryList.size(); i++) {
            for (int j = 0; j < sceneryList.size(); j++) {
                if(sceneryList.get(i).getPoid().equals(sceneryList.get(j).getPoid())){
                    dis[i][j]=0.0;
                }
                String key = sceneryList.get(i).getPoid()+"_"+sceneryList.get(j).getPoid();
                if(map.containsKey(key)){
                    String v = map.get(key);
                    if(!v.equals("NULL")){
                        dis[i][j]=Double.parseDouble(v);
                    }else {
                        dis[i][j]=getDistance(sceneryList.get(i),sceneryList.get(j));
                    }
                }else {
                    dis[i][j]=getDistance(sceneryList.get(i),sceneryList.get(j));
                }
            }
        }
        return dis;
    }
}

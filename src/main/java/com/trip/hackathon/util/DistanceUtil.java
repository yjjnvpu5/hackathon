package com.trip.hackathon.util;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

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

}

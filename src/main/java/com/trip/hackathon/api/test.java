package com.trip.hackathon.api;

import com.trip.hackathon.model.Route;
import com.trip.hackathon.service.RoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangqiwei
 * @Description
 */

@Controller
public class test {
    @Autowired
    RoutService routService;

    @RequestMapping(method = RequestMethod.GET,value = "/route")
    public void testRoute(){
        List<Route> route = routService.route(4.0, 6.0, Arrays.asList("Japan"), false);
    }
}

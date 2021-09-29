package com.lessons.services;

import com.lessons.models.GetDashboardDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service("com.lessons.services.DashboardService")
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);


    /**
     * Get all chart data for 3 charts
     *
     * @return GetDashboardDataDTO wrapper object that holds data for all charts
     */
    public GetDashboardDataDTO getAllChartData() {
        logger.debug("getAllChartData() starated.");

        List<Map<String, Object>> data1 = getListOfMapsForChart1();
        List<Map<String, Object>> data2 = getListOfMapsForChart2();
        List<Map<String, Object>> data3 = getListOfMapsForChart3();

        // Create the DTO to that holds all of the chart data and return it
        GetDashboardDataDTO dto = new GetDashboardDataDTO(data1, data2, data3);
        return dto;
    }


    /**
     * Returns data that looks like this on the front-end
     *    let chartData1 =  [
     *       {
     *         name: "Item 3",
     *         y: 989
     *       },
     *       {
     *         name: "Item 3R",
     *         y: 249
     *       }
     *     ];
     *
     * Get data for chart1
     * @return List of maps
     */
    private List<Map<String, Object>> getListOfMapsForChart1() {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "Item 3");
        map1.put("y", 989);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "Item 3R");
        map2.put("y", 249);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "Item 5");
        map3.put("y", 1035);

        Map<String, Object> map4 = new HashMap<>();
        map4.put("name", "Item 5R");
        map4.put("y", 324);

        listOfMaps.add(map1);
        listOfMaps.add(map2);
        listOfMaps.add(map3);
        listOfMaps.add(map4);

        return listOfMaps;
    }


    /**
     * Returns data that looks like this on the front-end
     *     let chartData2 = [
     *       {
     *         name: 'T3',
     *         legendIndex: 1,
     *         data: [300, 5500, 1800, 1600, 1200, 1500, 1000, 800, 500, 400, 1000]
     *       },
     *       {
     *         name: 'T3R',
     *         legendIndex: 2,
     *         data: [2, 2, 100, 2, 1]
     *       }
     *     ];
     *
     * Get data for chart2
     * @return List of maps
     */
    private List<Map<String, Object>> getListOfMapsForChart2() {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "T3");
        map1.put("legendIndex", 1);
        map1.put("data", Arrays.asList(300, 5500, 1800, 1600, 1200, 1500, 1000, 800, 500, 400, 1000) );

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "T3R");
        map2.put("legendIndex", 2);
        map2.put("data", Arrays.asList(2, 2, 100, 2, 1) );

        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "R5");
        map3.put("legendIndex", 3);
        map3.put("data", Arrays.asList(25, 500, 551, 600, 400, 300, 200, 500, 100, 100, 1200) );

        Map<String, Object> map4 = new HashMap<>();
        map4.put("name", "T5R");
        map4.put("legendIndex", 4);
        map4.put("data", Arrays.asList(200, 190, 190, 100, 50, 12, 37, 42, 98, 50, 600) );

        listOfMaps.add(map1);
        listOfMaps.add(map2);
        listOfMaps.add(map3);
        listOfMaps.add(map4);

        return listOfMaps;
    }


    /**
     *  NOTE:  Epoch Time on the front-end is in MilliSeconds
     *         Epoch Time in Java is in seconds.  So, we must multiply the java epoch time * 1000
     *
     * Returns data that looks like this on the front-end
     *    let chartData3 =  [{
     *       name: 'T3',
     *       data: [
     *         [1559361600000, 110],                 1559361600000 is 6/1/2019
     *         [1561939200000, 145],                 1561939200000 is 7/1/2019
     *         [1564617600000, 135],                 1564617600000 is 8/1/2019
     *         [1567296000000, 140],                 1567296000000 is 9/1/2019
     *       ]
     *     },
     *     {
     *       name: 'T3R',
     *       data: [
     *         [1559361600000, 175],
     *         [1561939200000, 155],
     *         [1564617600000, 100],
     *         [1567296000000, 115],
     *       ]
     *     }];
     *
     * Get data for chart3
     * @return List of maps
     */
    private List<Map<String, Object>> getListOfMapsForChart3() {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();


        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "T3");
        map1.put("data", Arrays.asList(
                Arrays.asList( 1559361600000L, 110),
                Arrays.asList( 1561939200000L, 145),
                Arrays.asList( 1564617600000L, 135),
                Arrays.asList( 1567296000000L, 140),
                Arrays.asList( 1569891600000L, 100),
                Arrays.asList( 1572570000000L, 110),
                Arrays.asList( 1575162000000L, 100),
                Arrays.asList( 1577840400000L,  85),
                Arrays.asList( 1580518800000L,  70),
                Arrays.asList( 1583024400000L,  65),
                Arrays.asList( 1585702800000L,  60),
                Arrays.asList( 1588294800000L,  60)
        ));


        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "T3R");
        map2.put("data", Arrays.asList(
                Arrays.asList( 1559361600000L, 175),
                Arrays.asList( 1561939200000L, 155),
                Arrays.asList( 1564617600000L, 100),
                Arrays.asList( 1567296000000L, 115),
                Arrays.asList( 1569891600000L, 87),
                Arrays.asList( 1572570000000L, 90),
                Arrays.asList( 1575162000000L, 88),
                Arrays.asList( 1577840400000L,  86),
                Arrays.asList( 1580518800000L,  75),
                Arrays.asList( 1583024400000L,  60),
                Arrays.asList( 1585702800000L,  60),
                Arrays.asList( 1588294800000L,  45)
                ));

        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "T5");
        map3.put("data", Arrays.asList(
                Arrays.asList( 1559361600000L,  230),
                Arrays.asList( 1561939200000L,  225),
                Arrays.asList( 1564617600000L,  205),
                Arrays.asList( 1567296000000L,  210),
                Arrays.asList( 1569891600000L,  212),
                Arrays.asList( 1572570000000L,  185),
                Arrays.asList( 1575162000000L,  187),
                Arrays.asList( 1577840400000L,  150),
                Arrays.asList( 1580518800000L,  105),
                Arrays.asList( 1583024400000L,  85),
                Arrays.asList( 1585702800000L,  85),
                Arrays.asList( 1588294800000L,  70)
        ));


        Map<String, Object> map4 = new HashMap<>();
        map4.put("name", "T5R");
        map4.put("data", Arrays.asList(
                Arrays.asList( 1559361600000L,  240),
                Arrays.asList( 1561939200000L,  238),
                Arrays.asList( 1564617600000L,  205),
                Arrays.asList( 1567296000000L,  200),
                Arrays.asList( 1569891600000L,  160),
                Arrays.asList( 1572570000000L,  155),
                Arrays.asList( 1575162000000L,  148),
                Arrays.asList( 1577840400000L,  140),
                Arrays.asList( 1580518800000L,  120),
                Arrays.asList( 1583024400000L,  85),
                Arrays.asList( 1585702800000L,  75),
                Arrays.asList( 1588294800000L,  125)
        ));

        listOfMaps.add(map1);
        listOfMaps.add(map2);
        listOfMaps.add(map3);
        listOfMaps.add(map4);

        return listOfMaps;
    }



}

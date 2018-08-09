/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.sprinkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.sprinkler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

/**
 * check the configuration
 * @author wf
 *
 */
public class TestConfiguration {

  @Test
  public void testConfiguration() throws Exception {
    Configuration conf=new Configuration();
    Location loc=Location.byName("Knickelsdorf/DE");
    conf.setLocation(loc);
    conf.setAreaSizeSquareMeter(100);
    conf.setEarliestSprinkleHour("07:30");
    conf.setLatestSprinkleHour("20:30");
    conf.setlEvaporationPerDay(200);
    conf.setSprinklesPerDay(2);
    conf.setMmPerHour(22.8); // 2280 l per hour
    Configuration conf2=new Configuration();
    Location loc2=new Location();
    Coord coord=new Coord(50.94,6.958);
    loc2.setCoord(coord);
    loc2.setName("Krefeld");
    loc2.setCountry("DE");
    loc2.setId(Location.byName("Krefeld/DE").getId());
    conf2.setLocation(loc2);
    Location loc3=Location.byId(loc2.getId());
    // check results
    assertEquals(loc3.getName(),loc2.getName());
    assertEquals("DE",loc3.getCountry()); 
    assertEquals(100,conf.getAreaSizeSquareMeter(),0.1);
    assertEquals("07:30",conf.getEarliestSprinkleHour());
    assertEquals("20:30",conf.getLatestSprinkleHour());
    assertEquals(200,conf.getlEvaporationPerDay(),0.01);
    assertEquals(2,conf.getSprinklesPerDay());
    assertEquals(22.8,conf.getMmPerHour(),0.01);
    assertNotNull(conf.getLocation());
  }

}

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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

import com.google.gson.Gson;

/**
 * test getting the city list from the open weather map api
 * @author wf
 *
 */
public class TestCityList {

  @Test
  public void testCityList() throws Exception {
    Location.debug=TestSuite.debug;
    Location[] locations=Location.getLocations();
    if (TestSuite.debug)
      System.out.println("Found "+locations.length+" cities");
    // as of 2018-08 there are more than 200.000 cities
    assertTrue(locations.length>20000);
    if (TestSuite.debug) {
      Gson gson=new Gson();
    
      for (int i=0;i<=1;i++) {
        System.out.println(gson.toJson(locations[i]));
      }
    }
    Location kndorf=Location.byName("Knickelsdorf/DE");
    assertNotNull(kndorf);
    assertEquals(2887186,kndorf.getId());
    Coord coord = kndorf.getCoord();
    if (TestSuite.debug) {
      System.out.println(String.format("lat: %4.2f lon: %4.2f", coord.getLat(),coord.getLon()));
    }
    Location kndorf2=Location.byId(2887186);
    assertEquals(kndorf.getCoord(),kndorf2.getCoord());
    
    Location willich=Location.byName("Willich/DE");
    assertEquals(2808559,willich.getId());
    
    Location moscow=Location.byId(524901);
    assertEquals("Moscow",moscow.getName());
  }
}

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
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openweathermap.weather.Location;
import org.openweathermap.weather.OpenWeatherMapApi;
import org.openweathermap.weather.Weather;
import org.openweathermap.weather.WeatherForecast;
import org.openweathermap.weather.WeatherHistory;
import org.openweathermap.weather.WeatherReport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * test getting a weather report from the openweather map api
 * @author wf
 *
 */
public class TestWeatherReport {

  @BeforeClass 
  /**
   * make sure we run in demo mode - e.g. to get dummy history data
   */
  public static void enableDemo() {
    OpenWeatherMapApi.enableDemo();
  }
  
  @Test
  public void testWeatherReport() throws Exception {
    OpenWeatherMapApi.debug=true;
    Location location=Location.byName("Cairns/AU");
    WeatherReport.debug=TestSuite.debug;
    WeatherReport report=WeatherReport.getByLocation(location);
    assertNotNull(report);
   
    if (TestSuite.debug) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      System.out.println(gson.toJson(report));
    }
    assertEquals(-16.92,report.coord.getLat(),0.001);
    assertEquals(145.77,report.coord.getLon(),0.001);
    assertEquals("stations",report.base);
    assertEquals(2172797,report.id);
    assertEquals("Cairns",report.name);
    assertEquals(200,report.cod);
    Weather[] weatherList=report.weather;
    assertEquals(1,weatherList.length);
    Weather weather=weatherList[0];
    assertEquals(802,weather.id);
    assertEquals("Clouds",weather.main);
    assertEquals("scattered clouds",weather.description);
    assertEquals("03n",weather.icon);
    assertNull(report.rain);
  }
  
  @Test
  public void testWeatherForecast() throws Exception {
    //TestSuite.debug=true;
    Location moscow=Location.byId(524901);
    assertNotNull("Moscow should be found",moscow);
    WeatherForecast.debug=TestSuite.debug;
    WeatherForecast forecast=WeatherForecast.getByLocation(moscow);
    assertNotNull(forecast);
   
    if (TestSuite.debug) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      System.out.println(gson.toJson(forecast));
    }
    assertEquals("Moscow",forecast.city.getName());
    assertEquals(40,forecast.list.length);
    // assertEquals(2.645,forecast.totalPrecipitation(3),0.001);
  }
  
  @Test
  public void testWeatherHistory() throws Exception {
    TestSuite.debug=true;
    Location london=Location.byId(2643743);
    assertNotNull("london should be found",london);
    assertEquals("London",london.getName());
    assertEquals("GB",london.getCountry());
    WeatherHistory history=WeatherHistory.getByLocation(london);
    assertNotNull("history for London should be available",history);
    if (TestSuite.debug) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      System.out.println(gson.toJson(history));
    }
    assertEquals(3,history.list.length);
  }

}

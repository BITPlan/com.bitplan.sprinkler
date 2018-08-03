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
package org.openweathermap.weather;

/**
 * Weather Report from openweathermap.org
 * 
 * @author wf
 *
 */
public class WeatherReport extends OpenWeatherMapApi {
  /**
   * members of the Weather report
   */
  public Coord coord;
  public Weather[] weather;
  public Main main;
  public Clouds clouds;
  public Rain rain;
  public Wind wind;
  public Sys sys;
  public String base;
  public long id;
  public long dt;
  public String name;
  public long cod;
  
  /**
   * get a Weather report by location
   * @param location
   * @return - the weather report
   */
  public static WeatherReport getByLocation(Location location) {
    WeatherReport report=(WeatherReport) OpenWeatherMapApi.getByLocation(location, "weather", WeatherReport.class);
    return report;
  }

  
  
}
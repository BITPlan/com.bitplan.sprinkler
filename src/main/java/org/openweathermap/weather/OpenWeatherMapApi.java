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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.util.JsonUtil;
import com.google.gson.Gson;

public class OpenWeatherMapApi {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("org.openweathermap.weather");
  
  // set to true to debug
  public static boolean debug=false;

  // base url of the weather api service (sample mode)
  protected static String baseurl="https://samples.openweathermap.org";

  // appid - sample mode
  // you might want to call enableProduction to use the production/non - sample version of the api
  public static String appid="b6907d289e10d714a6e88b30761fae22";
  
  // units - in sample mode this parameter is empty
  public static String units="";
  
  /**
   * enable production with the given appid
   * @param pAppid
   */
  public static void enableProduction(String pAppid) {
    appid=pAppid;
    baseurl="https://api.openweathermap.org";
    units="&units=metric";
  }

  /**
   * get the weather report for the given location
   * 
   * @param location
   * @return - the weather report / forecast and so on
   */
  public static OpenWeatherMapApi getByLocation(Location location, String type, Class<? extends OpenWeatherMapApi> clazz) {
    long id = location.getId();
    String url = String.format(
        "%s/data/2.5/%s?id=%d&appid=%s%s",
        baseurl,type,id, appid,units);
    try {
      String json = JsonUtil.read(url);
      if (debug)
        LOGGER.log(Level.INFO, url+"="+json);
      Gson gson = new Gson();
      OpenWeatherMapApi result = gson.fromJson(json, clazz);
      return result;
    } catch (Throwable th) {
      String msg=th.getMessage();
      LOGGER.log(Level.SEVERE, msg, th);
      return null;
    }
  }
}

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

import org.openweathermap.weather.Location;
import org.openweathermap.weather.OpenWeatherMapApi;
import org.openweathermap.weather.WeatherForecast;

import com.bitplan.javafx.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Sprinkler extends Main {

  private static Sprinkler sprinkler;

  @Override
  public String getSupportEMail() {
    return "support@bitplan.com";
  }

  @Override
  public String getSupportEMailPreamble() {
    String javaversion = System.getProperty("java.version");
    String os = System.getProperty("os.name");
    return String.format(
        "Dear sprinkler support\nI am using version %s of the software on %s using Java %s\n",
        VERSION, os, javaversion);
  }

  @Override
  public void work() throws Exception {
    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    }
    Configuration configuration=Configuration.getConfiguration("default");
    if (configuration==null) {
      System.err.println(String.format("There is no configuration file %s yet.\nYou might want to create on as outlined in http://wiki.bitplan.com/index.php/Sprinkler#Configuration",Configuration.getJsonFile("default").getPath()));
      if (!testMode)
        System.exit(1);
    } else {
      Location location=configuration.getLocation();
      OpenWeatherMapApi.enableProduction(configuration.appid);
      WeatherForecast forecast=WeatherForecast.getByLocation(location);
      System.out.println(String.format("The forecast for the total precipitation for the next 5 days is %3.1f mm",forecast.totalPrecipitation(5)));
      if (this.debug) {
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(forecast));
      }
    }
  }
  
  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    sprinkler = new Sprinkler();
    int result = sprinkler.maininstance(args);
    if (!testMode) {
      // LOGGER.log(Level.INFO, "System exit " + result);
      System.exit(result);
    }
  }

}

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

import org.kohsuke.args4j.Option;
import org.openweathermap.weather.Forecast;
import org.openweathermap.weather.Location;
import org.openweathermap.weather.OpenWeatherMapApi;
import org.openweathermap.weather.WeatherForecast;
import org.openweathermap.weather.WeatherService;

import com.bitplan.appconfig.Preferences;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * main program to control Sprinkler via command line
 * 
 * @author wf
 *
 */
public class Sprinkler extends Main {

  private static Sprinkler sprinkler;

  @Option(name = "-rf", aliases = {
      "--rainforecast" }, usage = "rain\nshow the rainforecast")
  protected boolean rainforecast = false;

  @Option(name = "--lang", aliases = {
      "--language" }, usage = "language\nlanguage to use: de/en")
  protected String lang = null;

  @Option(name = "-l", aliases = {
      "--location" }, usage = "location\nuse/lookup the given location name")
  protected String locationName;

  @Option(name = "-n", aliases = {
      "--nogui" }, usage = "nogui\ndo not show the graphical user interfaces")
  protected boolean nogui = false;

  Configuration configuration;

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

  /**
   * exit with the given error message
   * 
   * @param msg
   */
  private void error(String msg) {
    System.err.println(msg);
    if (!testMode)
      System.exit(1);
  }

  /**
   * get a weather Service for the default location
   * 
   * @return
   * @throws Exception
   */
  public WeatherService getWeatherService() throws Exception {
    // @TODO - cleanup side effect
    configuration = Configuration.getConfiguration("default");
    WeatherService service = null;
    if (configuration == null) {
      error(String.format(
          "There is no configuration file %s yet.\nYou might want to create one as outlined in http://wiki.bitplan.com/index.php/Sprinkler#Configuration",
          Configuration.getJsonFile("default").getPath()));
    } else {
      Location location = configuration.getLocation();
      if (locationName != null) {
        location = Location.byName(locationName);
        if (location == null)
          error("Could not find location " + locationName);
      }
      OpenWeatherMapApi.enableProduction(configuration.appid);
      service = new WeatherService(location);
    }
    return service;
  }

  @Override
  public void work() throws Exception {
    Translator.APPLICATION_PREFIX="sprinkler";
    if (lang == null) {
      Preferences preferences = Preferences.getInstance();
      lang = preferences != null ? preferences.getLanguage().name() : "en";
    }
    Translator.initialize("sprinkler", lang);
    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    }
    WeatherService weatherService = getWeatherService();
    if (weatherService != null) {
      WeatherForecast forecast = weatherService.getWeatherForecast();
      System.out.println(String.format(
          "The forecast for the total precipitation at %s/%s id: %8d for the next 5 days is %4.1f mm",
          forecast.city.getName(), forecast.city.getCountry(),
          forecast.city.getId(), forecast.totalPrecipitation(5 * 24)));
      if (rainforecast) {
        for (Forecast threehours : forecast.list) {
          double rain = 0.0;
          if (threehours.rain != null)
            rain = threehours.rain.mm;
          System.out
              .println(String.format("%s %4.1f mm", threehours.dt_txt, rain));
        }
      }
      if (this.debug) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(forecast));
      }
    }
    if (!nogui) {
      SprinklerApp gApp = SprinklerApp.getInstance(this);
      gApp.show();
      gApp.waitOpen();
      gApp.waitClose();
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

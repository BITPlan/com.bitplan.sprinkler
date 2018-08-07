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

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.openweathermap.weather.Location;
import org.openweathermap.weather.OpenWeatherMapApi;
import org.openweathermap.weather.WeatherForecast;
import org.openweathermap.weather.WeatherService;

import com.bitplan.i18n.Translator;
import com.bitplan.javafx.SampleApp;
import com.bitplan.javafx.WaitableApp;
import com.bitplan.sprinkler.javafx.WeatherPlot;

/**
 * test the graphical user interface
 * 
 * @author wf
 *
 */
public class TestGUI {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.sprinkler");

  static int SHOW_TIME = 4000;

  @Before
  public void initGUI() {
    WaitableApp.toolkitInit();
    Translator.initialize("sprinkler", "en");
  }

  @Test
  public void testWeatherPlot() throws Exception {
    Sprinkler sprinkler=new Sprinkler();
    WeatherService weatherService=sprinkler.getWeatherService();
    WeatherForecast forecast=weatherService.getWeatherForecast();
    if (forecast!=null) {
      WeatherPlot weatherPlot = new WeatherPlot("5 day Weather Forecast",
          "Date", "mm Rain", forecast);
      SampleApp sampleApp = new SampleApp("WeatherPlot",
          weatherPlot.getBarChart());
      sampleApp.show();
      sampleApp.waitOpen();
      Thread.sleep(SHOW_TIME*4);
      sampleApp.close();
    }
  }

  @Test
  public void testGUI() throws Exception {
    SprinklerApp gApp = SprinklerApp.getInstance(new Sprinkler());
    gApp.show();
    gApp.waitOpen();
    Thread.sleep(SHOW_TIME);
    gApp.close();
  }

}

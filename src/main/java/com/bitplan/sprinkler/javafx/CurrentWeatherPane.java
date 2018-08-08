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
package com.bitplan.sprinkler.javafx;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openweathermap.weather.WeatherReport;

import com.bitplan.i18n.I18n;
import com.bitplan.javafx.ConstrainedGridPane;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.TickLabelLocation;
import javafx.geometry.Pos;

/**
 * a Weather Dashboard
 * 
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class CurrentWeatherPane extends ConstrainedGridPane {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler.javafx");

  private FGauge framedWindGauge;
  private Gauge windGauge;
  public Gauge temperatureGauge;

  private Gauge rainGauge;

  private Gauge windSpeedGauge;

  /**
   * create a Pane for the current Weather
   * 
   * @param report
   *          - the report to show
   */
  public CurrentWeatherPane(WeatherReport report) {
    // LcdFont lcdFont=LcdFont.STANDARD;
    // LcdDesign lcdDesign=LcdDesign.SECTIONS;
    if (report != null) {
      windGauge = GaugeBuilder.create()
          .autoScale(false)
          .angleRange(360)
          .startAngle(180.0)
          .tickLabelDecimals(0)
          .decimals(0)
          .majorTickSpace(90.0)
          .minorTickSpace(22.5)
          .minValue(0)
          .maxValue(360.0)
          .autoScale(false)
          .animated(true)
          //.shadowsEnabled(true)
          //.sectionsVisible(true)
          //.sections(
          //    new Section( 0, 90, Color.rgb(195, 128, 102, 0.5)),
          //    new Section(90,180, Color.rgb(195, 196, 102, 0.5))
          // )
          //.majorTickMarkColor(Color.rgb(241, 161, 71))
          // .minorTickMarkColor(Color.rgb(0, 175, 248))
          // .majorTickMarkType(TickMarkType.TRAPEZOID)
          //.mediumTickMarkType(TickMarkType.DOT)
          //.minorTickMarkType(TickMarkType.LINE)
          .tickLabelLocation(TickLabelLocation.INSIDE).title(I18n.get("Wind"))
          .unit(I18n.get("degree")).lcdDesign(LcdGauge.lcdDesign)
          //.lcdVisible(true).lcdFont(LcdGauge.lcdFont)
          .knobType(KnobType.METAL)
          .innerShadowEnabled(true)
          .needleSize(NeedleSize.THICK)
          .build();

      windGauge.setValue(report.wind.deg);
      
      framedWindGauge = new FGauge(windGauge, GaugeDesign.SHINY_METAL,
          GaugeBackground.TRANSPARENT);
      
      windSpeedGauge = LcdGauge.createGauge(I18n.get("wind speed"),
          I18n.get("km/h"));
      windSpeedGauge.setValue(report.wind.speed);
      
      temperatureGauge = LcdGauge.createGauge(I18n.get("temperature"),
          I18n.get("°C"));
      rainGauge = LcdGauge.createGauge(I18n.get("rain"),
          I18n.get("mm"));
      if (report.rain!=null) {
        rainGauge.setValue(report.rain.mm);
      }
      temperatureGauge.setValue(report.main.temp);
   
      ConstrainedGridPane temperaturePane = new ConstrainedGridPane();
      temperaturePane.add(temperatureGauge, 0, 0);
      temperaturePane.add(rainGauge,0,1);
      temperaturePane.setAlignment(Pos.CENTER);
      temperaturePane.fixRowSizes(1,50,50);
      temperaturePane.fixColumnSizes(1,100);
      
      this.add(temperaturePane, 0, 0);
      this.add(framedWindGauge, 1, 0);  
      this.add(windSpeedGauge, 2,0);

      // 75= 80 - 5% (5% for extra gap)
      fixRowSizes(1,100);
      fixColumnSizes(1,30,40,30);
    } else {
      LOGGER.log(Level.WARNING, "report is null for CurrentWeatherPane");
    }
  }

}

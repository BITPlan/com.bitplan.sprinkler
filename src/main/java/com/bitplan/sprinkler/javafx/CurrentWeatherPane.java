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
import com.bitplan.sprinkler.SprinklerI18n;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickMarkType;
import eu.hansolo.medusa.skins.LcdSkin;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

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

  private Gauge minTemperatureGauge;

  private Gauge maxTemperatureGauge;

  /**
  public class WindSpeedHelper extends Helper {
    @Override
    public String getTextForCounter(Locale locale, String tickLabelFormatString,
        Double counter) {
      if (counter < 90)
        return "N";
      if (counter < 180)
        return "E";
      if (counter < 270)
        return "S";
      return "W";
    }
  }*/

  /**
   * create a Pane for the current Weather
   * 
   * @param report
   *          - the report to show
   */
  public CurrentWeatherPane(WeatherReport report) {
    // LcdFont lcdFont=LcdFont.STANDARD;
    // LcdDesign lcdDesign=LcdDesign.SECTIONS;
    // Marker north=new Marker(0.0,"North",Color.BLUE,MarkerType.TRIANGLE);
    // Marker east=new Marker(90.0,"E",Color.BLUE,MarkerType.STANDARD);
    if (report != null) {
      double opacity=2.0/3.0;
      windGauge = GaugeBuilder.create().angleRange(360).startAngle(180.0)
          .tickLabelDecimals(0).decimals(0).minorTickSpace(22.5).minValue(0)
          .maxValue(359.999).tickLabelsVisible(true)
          // .markers(north,east,new Marker(180.0,"S"),new Marker(270.0,"W"))
          // .markersVisible(true)
          // .animated(true)
          // .shadowsEnabled(true)
          .sectionsVisible(true)
          // http://www.color-hex.com/color-palette/42837
          .sections(
           new Section(  0, 90, Color.rgb(208, 223, 255, opacity)),
           new Section( 90,180, Color.rgb(193, 212, 251, opacity)),
           new Section(180,270, Color.rgb(168, 194, 251, opacity)),
           new Section(270,360, Color.rgb(134, 171, 249, opacity))
          )
          // .majorTickMarkColor(Color.rgb(241, 161, 71))
          // .minorTickMarkColor(Color.rgb(0, 175, 248))
          .majorTickMarkType(TickMarkType.TRAPEZOID)
          // .mediumTickMarkType(TickMarkType.DOT)
          // .minorTickMarkType(TickMarkType.LINE)
          .tickLabelLocation(TickLabelLocation.INSIDE).title(I18n.get(SprinklerI18n.WIND_DIRECTION))
          .unit(I18n.get(SprinklerI18n.WIND_DEGREE)).lcdDesign(LcdDesign.STANDARD)
          .lcdVisible(false).lcdFont(LcdFont.DIGITAL).knobType(KnobType.METAL)
          .innerShadowEnabled(true).needleSize(NeedleSize.THICK)
          .autoScale(false).build();

      framedWindGauge = new FGauge(windGauge, GaugeDesign.SHINY_METAL,
          GaugeBackground.TRANSPARENT);

      windGauge.setMajorTickSpace(45);
      // we intend to "enhance" the original Gauge with a different helper
      // GaugeSkin skin = (GaugeSkin) windGauge.getSkin();
      // skin.helper = new WindSpeedHelper();
      windGauge.setValue(report.wind.deg);
      
      windSpeedGauge = LcdGauge.createGauge(I18n.get(SprinklerI18n.WIND_SPEED),
          I18n.get("km/h"));
      windSpeedGauge.setValue(report.wind.speed * 3.6); // m/s to km/h

      minTemperatureGauge = LcdGauge.createGauge(I18n.get(SprinklerI18n.MIN_TEMPERATURE),
          I18n.get("°C"));
      maxTemperatureGauge = LcdGauge.createGauge(I18n.get(SprinklerI18n.MAX_TEMPERATURE),
          I18n.get("°C"));
      temperatureGauge = LcdGauge.createGauge(I18n.get(SprinklerI18n.TEMPERATURE),
          I18n.get("°C"));
      rainGauge = LcdGauge.createGauge(I18n.get(SprinklerI18n.RAIN), I18n.get("mm"));
      rainGauge.setDecimals(1);
      if (report.rain != null) {
        rainGauge.setValue(report.rain.mm);
      }
      Gauge[] tempGauges= {temperatureGauge,minTemperatureGauge,maxTemperatureGauge};
      for (Gauge tempGauge:tempGauges) {
        tempGauge.setMaxValue(60.0);
        tempGauge.setMinValue(-90.0);
      }
      temperatureGauge.setValue(report.main.temp);
      minTemperatureGauge.setValue(report.main.temp_min);
      maxTemperatureGauge.setValue(report.main.temp_max);
      int colSpace=3;
      int rowSpace=3;
      ConstrainedGridPane temperaturePane = new ConstrainedGridPane();
      temperaturePane.add(minTemperatureGauge, 0, 0);
      temperaturePane.add(temperatureGauge, 0, 1);
      temperaturePane.add(maxTemperatureGauge, 0, 2);
      temperaturePane.setAlignment(Pos.CENTER);
      temperaturePane.fixRowSizes(rowSpace, 33,33,33);
      temperaturePane.fixColumnSizes(colSpace, 100);
      
      ConstrainedGridPane otherPane = new ConstrainedGridPane();
      otherPane.add(rainGauge, 0, 1);
      otherPane.add(windSpeedGauge,0,2);
      otherPane.fixRowSizes(rowSpace, 33,33,33);
      otherPane.fixColumnSizes(colSpace, 100);
      
      this.add(temperaturePane, 0, 0);
      this.add(framedWindGauge, 1, 0);
      this.add(otherPane, 2, 0);

      // 75= 80 - 5% (5% for extra gap)
      fixRowSizes(rowSpace, 100);
      fixColumnSizes(colSpace, 30, 40, 30);
    } else {
      LOGGER.log(Level.WARNING, "report is null for CurrentWeatherPane");
    }
  }

}

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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.ToggleSwitch;

import com.bitplan.error.ExceptionHandler;
import com.bitplan.fritzbox.FritzBoxSession;
import com.bitplan.fritzbox.FritzBoxSessionImpl;
import com.bitplan.fritzbox.HomeAutomationImpl;
import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.JFXStopWatch;
import com.bitplan.sprinkler.Configuration;
import com.bitplan.sprinkler.FritzBoxConfig;
import com.bitplan.sprinkler.SprinkleHistory;
import com.bitplan.sprinkler.SprinklePeriod;
import com.bitplan.sprinkler.SprinklePeriod.SprinkleSource;
import com.bitplan.sprinkler.SprinklerI18n;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * pane for the sprinkler
 * 
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class SprinklerPane extends HBox {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler.javafx");
  private SprinkleHistory sprinkleHistory;
  private JFXStopWatch stopWatch;
  private ToggleSwitch onOffButton;
  private ExceptionHandler exceptionHandler;
  private String ain; // FritzBox device ain
  private HomeAutomationImpl homeAutomation;
  private Clock startTimeClock;
  private Clock clock;
  private Clock stopTimeClock;
  private Gauge sprinklerLevel;
  private Gauge energyGauge;
  private Gauge waterGauge;
  private Gauge energyCostGauge;
  private Gauge waterCostGauge;

  /**
   * get a clock for the given title (to be internationalized)
   * 
   * @param i18nTitle
   * @return - the clock
   */
  protected Clock getClock(String i18nTitle, boolean dateVisible,
      boolean running) {
    String title = I18n.get(i18nTitle);
    Clock lClock = ClockBuilder.create().skinType(ClockSkinType.LCD)
        .lcdDesign(LcdDesign.GRAY).title(title).titleVisible(true)
        .secondsVisible(true).alarmsEnabled(false).dateVisible(dateVisible)
        .autoNightMode(true).running(running)
        .locale(Translator.getCurrentLocale()).build();
    return lClock;
  }

  /**
   * create a Pane for the current Sprinkler Period
   * 
   * @param sprinkleHistory
   *          - the history having all Sprinkler Periods
   * @param configuration
   *          -the configuration to use
   * @param exceptionHandler
   *          - the handler for exceptions
   * @throws Exception
   */
  public SprinklerPane(SprinkleHistory sprinkleHistory,
      Configuration configuration, ExceptionHandler exceptionHandler)
      throws Exception {
    this.sprinkleHistory = sprinkleHistory;
    this.exceptionHandler = exceptionHandler;
    // add a normal running clock
    clock = getClock(SprinklerI18n.WATCH_TIME, true, true);
    startTimeClock = getClock(SprinklerI18n.SPRINKLER__BEGIN, false, false);
    startTimeClock.setTime(0);
    startTimeClock.setVisible(false);
    stopTimeClock = getClock(SprinklerI18n.SPRINKLER__END, false, false);
    stopTimeClock.setTime(0);
    stopTimeClock.setVisible(false);
    stopWatch = new JFXStopWatch(I18n.get(SprinklerI18n.SPRINKLER__TIME));
    stopWatch.halt();
    stopWatch.reset();
    // kwH
    energyGauge = LcdGauge.createGauge("SprinklerEnergy", "kWh"); // TODO i18n
    energyGauge.setDecimals(3);
    energyCostGauge=LcdGauge.createGauge("Energy cost", configuration.getCurrency());
    energyCostGauge.setDecimals(2);
    // m3
    waterGauge = LcdGauge.createGauge("Water", "m³");
    waterGauge.setDecimals(3);
    waterCostGauge=LcdGauge.createGauge("Water cost", configuration.getCurrency());
    waterCostGauge.setDecimals(2);
    // Sprinkler Level
    this.sprinklerLevel = GaugeBuilder.create().unit("mm").maxValue(30)
        .skinType(SkinType.LINEAR).title("mm rain equivalent").build(); // TODO
                                                                        // i18n
    // On / Off Button
    onOffButton = new ToggleSwitch(I18n.get(SprinklerI18n.ON));
    onOffButton.setStyle("-fx-font-size: 45;");

    // use vboxes for each row
    VBox vbox1 = new VBox();
    vbox1.getChildren().add(onOffButton);
    vbox1.setAlignment(Pos.CENTER);
    // vbox.setStyle("-fx-content-display: center");
    // make the button same size as clock
    vbox1.prefWidthProperty().bind(clock.widthProperty());
    vbox1.prefHeightProperty().bind(clock.heightProperty());

    setupSprinklerOnOff();
    // ConstrainedGridPane replacement with vboxes
    this.getChildren().add(vbox1);

    VBox vbox2 = new VBox();
    // this.add(clock, 0, 1);
    // this.add(startTimeClock, 0, 2);
    vbox2.getChildren().add(clock);
    vbox2.getChildren().add(startTimeClock);
    vbox2.getChildren().add(this.energyGauge);
    vbox2.getChildren().add(this.energyCostGauge);
    this.getChildren().add(vbox2);
    VBox vbox3 = new VBox();
    // this.add(stopWatch.get(), 1, 1);
    // this.add(stopTimeClock, 1, 2);
    vbox3.getChildren().add(stopWatch.get());
    vbox3.getChildren().add(stopTimeClock);
    vbox3.getChildren().add(this.waterGauge);
    vbox3.getChildren().add(this.waterCostGauge);
    this.getChildren().add(vbox3);
    VBox vbox4 = new VBox();
    vbox4.getChildren().add(sprinklerLevel);
    // this.add(sprinklerLevel, 1, 0);
    this.getChildren().add(vbox4);
    // fixRowSizes(2, 30, 35, 35);
    // fixColumnSizes(2, 50, 50);
    // react on updates
    this.stopWatch.get().timeProperty().addListener((arg, oldVal, newVal) -> {
     
      Platform.runLater(
          () -> {
            double secs = newVal.toInstant().getEpochSecond();
            double mm=secs*configuration.getMmPerHour()/3600.0;
            double m3=mm*configuration.getAreaSizeSquareMeter()/1000.0;
            double waterCost=m3*configuration.getWaterPrice();
            double kWh=secs*configuration.getPumpPower()/3600.0/1000.0;
            double energyCost=kWh*configuration.getEnergyPrice();
            sprinklerLevel.setValue(mm);
            waterGauge.setValue(m3);
            energyGauge.setValue(kWh);
            waterCostGauge.setValue(waterCost);
            energyCostGauge.setValue(energyCost);
            // System.out.println(String.format("%4.1f mm 8.3f m³ 8.3f kWh",mm,m3,kWh));
          });
    });
  }

  /**
   * setup the Sprinkler handling
   * 
   * @throws Exception
   */
  private void setupSprinklerOnOff() throws Exception {

    if (onOffButton != null) {
      FritzBoxConfig fritzBoxConfig = FritzBoxConfig.getInstance();
      if (fritzBoxConfig == null)
        onOffButton.setDisable(true);
      else {
        FritzBoxSession fritzBoxSession = FritzBoxSessionImpl.getInstance();
        if (fritzBoxSession != null) {
          homeAutomation = new HomeAutomationImpl(fritzBoxSession);
          if (ain == null) {
            ain = homeAutomation.getAinForName(fritzBoxConfig.getDeviceName());
            if (ain != null) {
              boolean state = homeAutomation.getSwitchState(ain);
              onOffButton.setSelected(state);
            }
          }
        }
        onOffButton.selectedProperty().addListener(l -> {
          try {
            LOGGER.log(Level.INFO,
                "switch power on " + fritzBoxConfig.getDeviceName() + ": "
                    + onOffButton.isSelected());
            if (ain != null) {
              homeAutomation.setSwitchOnOff(ain, onOffButton.isSelected());
              if (onOffButton.isSelected()) {
                Date now = new Date();
                startTimeClock.setTimeMs(now.getTime());
                startTimeClock.setVisible(true);
                stopTimeClock.setTime(0);
                stopTimeClock.setVisible(false);
                stopWatch.start();
                onOffButton.setText(I18n.get(SprinklerI18n.OFF));
              } else {
                Date now = new Date();
                stopTimeClock.setTimeMs(now.getTime());
                stopTimeClock.setVisible(true);
                stopWatch.halt();
                SprinklePeriod sprinklePeriod = new SprinklePeriod();
                sprinklePeriod.start = Date
                    .from(startTimeClock.getTime().toInstant());
                sprinklePeriod.stop = Date
                    .from(stopTimeClock.getTime().toInstant());
                sprinklePeriod.source = SprinkleSource.Sprinkler;
                if (sprinkleHistory!=null) {
                  sprinkleHistory.add(sprinklePeriod);
                  sprinkleHistory.save();
                }
                onOffButton.setText(I18n.get(SprinklerI18n.ON));
              }
            }
          } catch (Exception e1) {
            exceptionHandler.handleException(e1);
          }
        });
      }
    }
  }
}

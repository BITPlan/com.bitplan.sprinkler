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

import java.util.logging.Level;

import org.openweathermap.weather.Location;
import org.openweathermap.weather.WeatherForecast;
import org.openweathermap.weather.WeatherReport;
import org.openweathermap.weather.WeatherService;

import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.javafx.GenericApp;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.javafx.TaskLaunch;
import com.bitplan.sprinkler.javafx.CurrentWeatherPane;
import com.bitplan.sprinkler.javafx.SprinklerPane;
import com.bitplan.sprinkler.javafx.WeatherPlot;
import com.bitplan.sprinkler.javafx.presenter.ConfigurationModifier;
import com.bitplan.sprinkler.javafx.presenter.FritzBoxConfigModifier;
import com.bitplan.sprinkler.javafx.presenter.LocationConfigModifier;
import com.bitplan.sprinkler.javafx.presenter.PreferencesModifier;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * create a sprinkler App
 * 
 * @author wf
 *
 */
public class SprinklerApp extends GenericApp {
  public static final String RESOURCE_PATH = "com/bitplan/sprinkler/gui";
  public static final String SPRINKLER_APP_PATH = RESOURCE_PATH
      + "/Sprinkler.json";
  private static SprinklerApp instance;
  private int screenPercent;
  private int divX;
  private int divY;

  String title;
  private Sprinkler sprinkler;

  /**
   * create the Sprinkler application
   * 
   * @param app
   * @param sprinkler
   * @param resourcePath
   */
  public SprinklerApp(App app, Sprinkler sprinkler, String resourcePath) {
    super(app, sprinkler, resourcePath);
    this.sprinkler = sprinkler;
    title = softwareVersion.getName() + " " + softwareVersion.getVersion();
    screenPercent = 67;
    divX = 2;
    divY = 2;
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(title);
    VBox vbox = new VBox();
    setRoot(vbox);
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);
    setScene(
        new Scene(getRoot(), sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    stage.setX(sceneBounds.getMinX());
    stage.setY(sceneBounds.getMinY());
    // create a Menu Bar and show it
    setMenuBar(createMenuBar(getScene(), app));
    showMenuBar(getScene(), getMenuBar(), true);
    // add the XY TabPane and set it's growing
    setupXyTabPane();
    // setup the forms
    setup(app);
    try {
      TabPane weatherTabPane = xyTabPane
          .getTabPane(SprinklerI18n.WEATHER_GROUP);
      WeatherService weatherService = sprinkler.getWeatherService();
      if (weatherTabPane != null && weatherService != null) {
        // create the weather Forecast
        Tab forecastTab = xyTabPane.getTab(SprinklerI18n.WEATHER_FORECAST_FORM);
        WeatherForecast forecast = weatherService.getWeatherForecast();
        Location city = forecast.city;
        if (forecast != null) {
          String title = I18n.get(SprinklerI18n.MULTI_DAY_WEATHER_FORECAST, 5,
              city.getName(), city.getCountry(),
              forecast.totalPrecipitation(5 * 24));
          WeatherPlot<String, Number> weatherPlot = new WeatherPlot<String, Number>(
              title, "Date", "mm Rain", city.toString(), forecast);
          Platform.runLater(
              () -> forecastTab.setContent(weatherPlot.getChart("BarChart")));
        }
        Tab historyTab = xyTabPane.getTab(SprinklerI18n.WEATHER_HISTORY_FORM);
        // WeatherHistory history = weatherService.getWeatherHistory();
        SprinkleHistory history = SprinkleHistory.getInstance();
        // FIXME - this calls out to the DWD WFS weather service which might be
        // slow ...
        LocationConfig locationConfig = new LocationConfig();
        if (sprinkler.configuration!=null && history!=null) {
          locationConfig.fromLocation(sprinkler.configuration.getLocation());
          if (locationConfig.theDwdStation != null)
            history.addFromDWDStation(locationConfig.theDwdStation);
        }
        if (history != null) {
          String title = I18n.get(SprinklerI18n.WEATHER_HISTORY, 5,
              city.getName(), city.getCountry(),
              history.totalPrecipitation(5 * 24));
          WeatherPlot<Number, Number> weatherHistoryPlot = new WeatherPlot<Number, Number>(
              title, "Date", "mm Rain", city.toString(), history);
          Platform.runLater(() -> historyTab
              .setContent(weatherHistoryPlot.getChart("LineChart")));
        } else {
          Label msgLabel = new Label("appid not valid for history data");
          Platform.runLater(() -> historyTab.setContent(msgLabel));
        }
        Tab weatherTab = xyTabPane.getTab(SprinklerI18n.CURRENT_WEATHER_FORM);
        WeatherReport report = weatherService.getWeatherReport();
        if (report != null) {
          Platform.runLater(
              () -> weatherTab.setContent(new CurrentWeatherPane(report)));
        }

      }
      Tab sprinklerTab = xyTabPane.getTab(SprinklerI18n.SPRINKLE_FORM);
      if (sprinklerTab != null) {
        SprinklerPane sprinklerPane = new SprinklerPane(
            SprinkleHistory.getInstance(), sprinkler.configuration, this);
        Platform.runLater(() -> sprinklerTab.setContent(sprinklerPane));
      }

      // setup the setting modifiers
      setupSettings();
      this.setActiveTabPane(SprinklerI18n.WEATHER_GROUP);
    } catch (Throwable th) {
      this.handleException(th);
    }
    stage.show();
  }

  /**
   * get the instance for the sprinkler app
   * 
   * @param sprinkler
   * @return
   * @throws Exception
   */
  public static SprinklerApp getInstance(Sprinkler sprinkler) throws Exception {
    if (instance == null) {
      App app = App.getInstance(SPRINKLER_APP_PATH);
      GenericApp.debug = true;
      instance = new SprinklerApp(app, sprinkler, RESOURCE_PATH);
    }
    return instance;
  }

  /**
   * setup the settings
   * 
   * @throws Exception
   */
  private void setupSettings() throws Exception {
    GenericPanel sprinklePanel = this.getPanels()
        .get(SprinklerI18n.SPRINKLE_FORM);
    sprinklePanel.setEditable(true);

    GenericPanel locationConfigPanel = this.getPanels()
        .get(SprinklerI18n.LOCATION_FORM);
    LocationConfig locationConfig = new LocationConfig();
    locationConfig.fromLocation(sprinkler.configuration.location);
    locationConfigPanel
        .setModifier(new LocationConfigModifier(this.stage, this.app, this,
            locationConfigPanel, locationConfig, sprinkler.configuration));

    GenericPanel preferencesPanel = this.getPanels()
        .get(SprinklerI18n.PREFERENCES_FORM);
    com.bitplan.appconfig.Preferences preferences = com.bitplan.appconfig.Preferences
        .getInstance();
    preferencesPanel.setModifier(new PreferencesModifier(this.stage, this.app,
        this, preferencesPanel, preferences));

    GenericPanel configPanel = this.getPanels()
        .get(SprinklerI18n.SETTINGS_FORM);
    Configuration configuration = Configuration.getConfiguration("default");
    if (configuration == null) {
      configuration = new Configuration();
    }
    configPanel.setModifier(new ConfigurationModifier(this.stage, this.app,
        this, configPanel, configuration));

    GenericPanel fritzBoxConfigPanel = this.getPanels()
        .get(SprinklerI18n.FRITZ_BOX_FORM);
    FritzBoxConfig fritzBoxConfig = FritzBoxConfig.getInstance();
    fritzBoxConfigPanel.setModifier(new FritzBoxConfigModifier(this.stage,
        this.app, this, fritzBoxConfigPanel, fritzBoxConfig));
  }

  /**
   * show a message that the given feature is not implemented yet
   * 
   * @param feature
   *          - i18n string code of feature e.g. menuItem
   */
  public void notImplemented(String feature) {
    GenericDialog.showAlert(stage, I18n.get(SprinklerI18n.SORRY),
        I18n.get(SprinklerI18n.WE_ARE_SORRY),
        I18n.get(feature) + " " + I18n.get(SprinklerI18n.NOT_IMPLEMENTED_YET));
  }

  @Override
  public void handle(ActionEvent event) {
    try {
      Object source = event.getSource();
      if (source instanceof MenuItem) {
        MenuItem menuItem = (MenuItem) source;
        switch (menuItem.getId()) {
        case SprinklerI18n.FILE_MENU__QUIT_MENU_ITEM:
          close();
          break;
        case SprinklerI18n.HELP_MENU__ABOUT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHome()));
          showAbout();
          break;
        case SprinklerI18n.HELP_MENU__HELP_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHelp()));
          break;
        case SprinklerI18n.HELP_MENU__FEEDBACK_MENU_ITEM:
          GenericDialog.sendReport(softwareVersion,
              softwareVersion.getName() + " feedback", "...");
          break;
        case SprinklerI18n.HELP_MENU__BUG_REPORT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getFeedback()));
          break;
        case SprinklerI18n.SPRINKLER_MENU__BEGIN_MENU_ITEM:
          this.selectTab(SprinklerI18n.SPRINKLE_FORM);
          this.notImplemented("begin sprinkling");
          break;
        case SprinklerI18n.SPRINKLER_MENU__END_MENU_ITEM:
          this.selectTab(SprinklerI18n.SPRINKLE_FORM);
          this.notImplemented("end sprinkling");
          break;
        case SprinklerI18n.SPRINKLER_MENU__AUTOMATIC_MENU_ITEM:
          this.selectTab(SprinklerI18n.SPRINKLE_FORM);
          this.notImplemented("automatic sprinkling");
          break;
        case SprinklerI18n.SETTINGS_MENU__SPRINKLE_AREA_MENU_ITEM:
          this.selectTab(SprinklerI18n.SETTINGS_FORM);
          break;
        case SprinklerI18n.SETTINGS_MENU__PREFERENCES_MENU_ITEM:
          this.selectTab(SprinklerI18n.PREFERENCES_FORM);
          break;
        case SprinklerI18n.SETTINGS_MENU__FRITZ_BOX_MENU_ITEM:
          this.selectTab(SprinklerI18n.FRITZ_BOX_FORM);
          break;
        case SprinklerI18n.SETTINGS_MENU__WELCOME_MENU_ITEM:
          this.notImplemented("Welcome");
          break;
        case SprinklerI18n.WEATHER_MENU__CURRENT_WEATHER_MENU_ITEM:
          this.selectTab(SprinklerI18n.CURRENT_WEATHER_FORM);
          break;
        case SprinklerI18n.WEATHER_MENU__WEATHER_FORECAST_MENU_ITEM:
          this.selectTab(SprinklerI18n.WEATHER_FORECAST_FORM);
          break;
        case SprinklerI18n.WEATHER_MENU__WEATHER_HISTORY_MENU_ITEM:
          this.selectTab(SprinklerI18n.WEATHER_HISTORY_FORM);
          break;
        default:
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      } else {
        LOGGER.log(Level.INFO,
            "event from " + source.getClass().getName() + " received");
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

}

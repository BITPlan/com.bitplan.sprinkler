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

import java.io.InputStream;
import java.util.logging.Level;

import com.bitplan.error.SoftwareVersion;
import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.javafx.GenericApp;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.TaskLaunch;

import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * create a sprinkler App
 * 
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class SprinklerApp extends GenericApp {
  public static final String RESOURCE_PATH="com/bitplan/sprinkler/gui";
  public static final String SPRINKLER_APP_PATH=RESOURCE_PATH+"/Sprinkler.json";
  private static SprinklerApp instance;
  private int screenPercent;
  private int divX;
  private int divY;

  String title;

  public SprinklerApp(App app, SoftwareVersion softwareVersion,
      String resourcePath) {
    super(app, softwareVersion, resourcePath);
    title = softwareVersion.getName() + " " + softwareVersion.getVersion();
    screenPercent = 67;
    divX = 2;
    divY = 2;
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(title);
    setRoot(new VBox());
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);
    setScene(
        new Scene(getRoot(), sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    stage.setX(sceneBounds.getMinX());
    stage.setY(sceneBounds.getMinY());
    setMenuBar(createMenuBar(getScene(), app));
    showMenuBar(getScene(), getMenuBar(), true);
    setup(app);
    stage.show();
  }

  /**
   * get the instance for the sprinkler app
   * @param sprinkler
   * @return
   * @throws Exception
   */
  public static SprinklerApp getInstance(Sprinkler sprinkler) throws Exception {
    if (instance == null) {
      App app=App.getInstance(SPRINKLER_APP_PATH);
      GenericApp.debug = true;
      instance = new SprinklerApp(app, sprinkler, RESOURCE_PATH);
    }
    return instance;
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
        case SprinklerI18n.FILE_QUIT_MENU_ITEM:
          close();
          break;
        case SprinklerI18n.HELP_ABOUT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHome()));
          showAbout();
          break;
        case SprinklerI18n.HELP_HELP_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHelp()));
          break;
        case SprinklerI18n.HELP_FEEDBACK_MENU_ITEM:
          GenericDialog.sendReport(softwareVersion,
              softwareVersion.getName() + " feedback", "...");
          break;
        case SprinklerI18n.HELP_BUG_REPORT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getFeedback()));
          break;
        /* case SprinklerI18n.SETTINGS_SETTINGS_MENU_ITEM:
          this.notImplemented("Settings");
          // showSettings(false);
          break;*/
        default:
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

}

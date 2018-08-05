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

import com.bitplan.error.SoftwareVersion;
import com.bitplan.gui.App;
import com.bitplan.javafx.GenericApp;

import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
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

  public static SprinklerApp getInstance(Sprinkler sprinkler) throws Exception {
    if (instance == null) {
      String path = "com/bitplan/sprinkler/gui/Sprinkler.json";
      InputStream jsonStream = App.class.getClassLoader()
          .getResourceAsStream(path);
      App app = App.fromJsonStream(jsonStream);
      GenericApp.debug = true;
      instance = new SprinklerApp(app, sprinkler, "com/bitplan/sprinkler/gui");
    }
    return instance;
  }

  @Override
  public void handle(ActionEvent event) {
    // TODO Auto-generated method stub
    
  }

}

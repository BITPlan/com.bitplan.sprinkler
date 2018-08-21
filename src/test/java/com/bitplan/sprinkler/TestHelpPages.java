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

import java.io.File;

import org.junit.Test;

import com.bitplan.javafx.GenericApp;
import com.bitplan.javafx.TaskLaunch;
import com.bitplan.javafx.WaitableApp;
import com.bitplan.javafx.XYTabPane;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * tests which create screenshots for http://can4eve.bitplan.com/index.php/Help
 * as a side effect
 * 
 * @author wf
 *
 */
public class TestHelpPages {
  boolean debug = true;

  private Sprinkler sprinkler;

  /**
   * start the Sprinkler software
   * 
   * @return nothing
   */
  public Void startSprinkler() {
    Sprinkler.testMode = true;
    sprinkler = new Sprinkler();
    String[] args = {};
    sprinkler.maininstance(args);
    return null;
  }

  /**
   * create a snapShot
   * 
   * @param stage
   * @param title
   */
  public void snapShot(Stage stage, String title) {
    // PauseTransition pause = new PauseTransition(Duration.millis(500));
    // pause.setOnFinished(event -> {
    try {
      File snapShotDir = new File("/tmp/sprinkler");
      snapShotDir.mkdirs();
      title = title.replace("/", "");
      File snapShot = new File(snapShotDir, title + ".png");
      if (debug)
        System.out.println(snapShot.getAbsolutePath());
      WaitableApp.saveAsPng(stage, snapShot);
    } catch (Exception e) {
      e.printStackTrace();;
    }

    // });
    // pause.play();
  }

  boolean done = false;

  public void loopTabs(GenericApp display) {
    XYTabPane xyTabPane = display.getXyTabPane();
    ObservableList<Tab> vtabs = xyTabPane.getvTabPane().getTabs();
    for (Tab vtab : vtabs) {
      xyTabPane.getvTabPane().getSelectionModel().select(vtab);
      TabPane hTabPane = xyTabPane.getSelectedTabPane();
      ObservableList<Tab> htabs = hTabPane.getTabs();

      if (vtab.getTooltip() != null) {
        String vTitle = vtab.getTooltip().getText();
        for (Tab htab : htabs) {
          hTabPane.getSelectionModel().select(htab);
          String title = htab.getTooltip().getText();
          System.out.println(vTitle + "_" + title);
          snapShot(display.getStage(), vTitle + "_" + title);
        }
      }
    }
    done = true;

  }

  @Test
  public void testTabs() throws Exception {
    WaitableApp.toolkitInit();
    WaitableApp.testMode=true;
    TaskLaunch.start(() -> startSprinkler());
    while (sprinkler == null || sprinkler.gApp == null)
      Thread.sleep(10);
    GenericApp gApp = sprinkler.gApp;
    gApp.waitOpen();
    Thread.sleep(250);
    Platform.runLater(() -> loopTabs(gApp));
    while (!done)
      Thread.sleep(50);
  }
}

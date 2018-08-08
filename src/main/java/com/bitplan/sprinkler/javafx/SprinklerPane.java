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

import org.controlsfx.control.ToggleSwitch;

import com.bitplan.error.ExceptionHandler;
import com.bitplan.fritzbox.Device;
import com.bitplan.fritzbox.DeviceList;
import com.bitplan.fritzbox.FritzBoxSession;
import com.bitplan.fritzbox.FritzBoxSessionImpl;
import com.bitplan.fritzbox.HomeAutomation;
import com.bitplan.fritzbox.HomeAutomationImpl;
import com.bitplan.i18n.I18n;
import com.bitplan.javafx.ConstrainedGridPane;
import com.bitplan.javafx.GenericControl;
import com.bitplan.javafx.JFXStopWatch;
import com.bitplan.sprinkler.FritzBoxConfig;
import com.bitplan.sprinkler.SprinkleHistory;
import com.bitplan.sprinkler.SprinklerI18n;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.beans.value.ChangeListener;

/**
 * pane for the sprinkler
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class SprinklerPane extends ConstrainedGridPane {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler.javafx");
  private SprinkleHistory sprinkleHistory;
  private JFXStopWatch stopWatch;
  private ToggleSwitch onOffButton;
  private ExceptionHandler exceptionHandler;
  private String ain; // FritzBox device ain
  
  /**
   * create a Pane for the current Sprinkler Period
   * 
   * @param sprinkleHistory - the history having all Sprinkler Periods
   * @param exceptionHandler - the handler for exceptions
   * @throws Exception 
   */
  public SprinklerPane(SprinkleHistory sprinkleHistory, ExceptionHandler exceptionHandler) throws Exception {
    this.sprinkleHistory=sprinkleHistory;
    this.exceptionHandler=exceptionHandler;
    stopWatch = new JFXStopWatch(I18n.get(SprinklerI18n.SPRINKLER_BEGIN));
    stopWatch.halt();
    stopWatch.reset();
    onOffButton=new ToggleSwitch();
    setupSprinklerOnOff();
    this.add(onOffButton, 0,0);
    this.add(stopWatch.get(), 0, 1);
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
        onOffButton.selectedProperty().addListener(l -> {
          try {
            LOGGER.log(Level.INFO, "switch power on "
                + fritzBoxConfig.getDeviceName() + ": " + onOffButton.isSelected());
            FritzBoxSession fritzBoxSession = FritzBoxSessionImpl.getInstance();
            if (fritzBoxSession != null) {
              // @TODO move to com.bitplan.fritzbox as getAinForName()
              final HomeAutomation homeAutomation = new HomeAutomationImpl(
                  fritzBoxSession);
              if (ain == null) {
                DeviceList deviceList = homeAutomation.getDeviceListInfos();
                for (Device device : deviceList.devices) {
                  if (fritzBoxConfig.getDeviceName().equals(device.name))
                    ain = device.getAin();
                }
              }
              if (ain != null) {
                homeAutomation.setSwitchOnOff(ain, onOffButton.isSelected());
                if (onOffButton.isSelected()) {
                  stopWatch.start();
                } else {
                  stopWatch.halt();
                }
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

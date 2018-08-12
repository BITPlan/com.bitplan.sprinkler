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
package com.bitplan.sprinkler.javafx.presenter;

import java.io.IOException;
import java.util.Map;

import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.sprinkler.Configuration;
import com.bitplan.sprinkler.LocationConfig;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class LocationConfigModifier extends BaseModifier<LocationConfig>{
  
  private Configuration configuration;
  private Map<String, Object> prevValues;

  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public LocationConfigModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel configPanel, LocationConfig locationConfig, Configuration configuration) {
    super.init(stage, app, exceptionHandler);
    this.setModel(locationConfig);
    this.setView(configPanel);
    this.configuration=configuration;
    updateView();
  }

  @Override
  public void updateView() {
    prevValues=this.getModel().asMap();
    this.getView().setValues(prevValues);
  }

  @Override
  public LocationConfig updateModel() {
    LocationConfig locationConfig=this.getModel();
    locationConfig.fromMap(this.getView().getValueMap());
    try {
      // set the configurations location
      // check whether a different location was specified
      LocationConfig prevLocation = new LocationConfig();
      prevLocation.fromMap(prevValues);
      if (!prevLocation.getFullName().equals(locationConfig.getFullName())) {
        locationConfig.setId(0L);
      }
      configuration.setLocation(locationConfig.getLocation());
      configuration.save();
      // set back the locationConfig
      locationConfig.fromLocation(configuration.getLocation());
      // and show it
      updateView();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    return locationConfig;
  }

}

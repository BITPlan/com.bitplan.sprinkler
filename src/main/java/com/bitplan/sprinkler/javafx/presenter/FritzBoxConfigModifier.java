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

import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.sprinkler.FritzBoxConfig;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class FritzBoxConfigModifier extends BaseModifier<FritzBoxConfig>{
  
  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public FritzBoxConfigModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel configPanel, FritzBoxConfig config) {
    super.init(stage, app, exceptionHandler);
    this.setModel(config);
    this.setView(configPanel);
    updateView();
  }

  @Override
  public void updateView() {
    this.getView().setValues(this.getModel().asMap());
  }

  @Override
  public FritzBoxConfig updateModel() {
    FritzBoxConfig config=this.getModel();
    config.fromMap(this.getView().getValueMap());
    try {
      config.save();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    return config;
  }

}

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

import com.bitplan.appconfig.Preferences;
import com.bitplan.appconfig.Preferences.LangChoice;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.sprinkler.SprinklerI18n;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class PreferencesModifier extends BaseModifier<Preferences>{
  
  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public PreferencesModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel preferencesPanel, Preferences preferences) {
    super.init(stage, app, exceptionHandler);
    this.setModel(preferences);
    this.setView(preferencesPanel);
    updateView();
  }

  @Override
  public void updateView() {
    this.getView().setValues(this.getModel().asMap());
  }

  @Override
  public Preferences updateModel() {
    Preferences preferences=this.getModel();
    LangChoice lang = preferences.getLanguage();
    preferences.fromMap(this.getView().getValueMap());
    try {
      preferences.save();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    if (!lang.equals(preferences.getLanguage())) {
      Translator.initialize(Translator.APPLICATION_PREFIX, preferences.getLanguage().name());
      GenericDialog.showAlert(getStage(), I18n.get(SprinklerI18n.LANGUAGE_CHANGED_TITLE),
          I18n.get(SprinklerI18n.LANGUAGE_CHANGED), I18n.get(SprinklerI18n.NEWLANGUAGE_RESTART));
    }
    return preferences;
  }

}

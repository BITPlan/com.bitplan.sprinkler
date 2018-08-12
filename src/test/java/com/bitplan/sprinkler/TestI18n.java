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

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;

/**
 * Test the Internationalization
 * @author wf
 *
 */
public class TestI18n extends com.bitplan.i18n.TestI18n {

  @BeforeClass 
  public static void initShow()
  {
    TestI18n.show=true;
  }
  
  /**
   * configure the app
   */
  public App getApp() throws Exception {
    App app = App.getInstance(SprinklerApp.SPRINKLER_APP_PATH);
    return app;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class getI18nClass() {
    return SprinklerI18n.class;
  }

  @Override
  public String getI18nName() {
    return "sprinkler";
  }
  
  @Test
  public void testSpecial(){
    Translator.initialize("sprinkler", "de");
    String text=I18n.get(SprinklerI18n.MULTI_DAY_WEATHER_FORECAST,5,"Knickelsdorf","DE",27.91);
    assertEquals("5 Tage Wettervorhersage für Knickelsdorf/DE: 27,9 mm",text);
    assertEquals("mm Regenersatz",I18n.get(SprinklerI18n.MM_RAIN_EQUIVALENT));
  }
}
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openweathermap.weather.Coord;

import com.bitplan.i18n.Translator;
import com.bitplan.sprinkler.SprinklePeriod.IrrigationEffect;

import de.dwd.geoserver.Station;

/**
 * test the sprinkler history
 * @author wf
 *
 */
public class TestSprinkleHistory {

  @Test
  public void testSprinkleHistory() throws Exception {
    Translator.initialize("sprinkler", "en");
    SprinklePeriod period1=new SprinklePeriod("2018-08-05 08:03",30,11.4,0.7,IrrigationEffect.Sprinkler);
    SprinklePeriod period2=new SprinklePeriod("2018-08-07 20:22",20,6.7,0.47,IrrigationEffect.Sprinkler);
    SprinklePeriod period3=new SprinklePeriod("2018-08-08 04:30",90,9.8,0,IrrigationEffect.Rain);
    
    assertEquals(11.4,period1.mm,0.01);
    assertEquals(0.7,period1.kWh,0.01);
    assertTrue(period1.toString().startsWith("2018-08-05 08:03 - 2018-08-05 08:33"));
    TestSuite.debug=true;
 
    SprinkleHistory sprinkleHistory=new SprinkleHistory(null,period3,period2,period1);
    assertEquals(3,sprinkleHistory.getSprinklePeriods().size());
    if (TestSuite.debug)
    for (SprinklePeriod sprinklePeriod:sprinkleHistory.getSprinklePeriods()) {
      System.out.println(sprinklePeriod);
      System.out.println(sprinkleHistory.getJsonFile().getPath());
    }
    sprinkleHistory.save();
    assertTrue(sprinkleHistory.getJsonFile().getPath().endsWith("SprinkleHistory.json"));
  }
  
  @Test
  public void testSprinkleHistoryFromDWD() throws Exception {
    SprinkleHistory history=new SprinkleHistory();
    Coord duscoord=new Coord(51.296,6.7686);
    Station dusStation=new Station("1078","Düsseldorf",duscoord,18.6);
    history.addFromDWDStation(dusStation);
    assertTrue(history.getSprinklePeriods().size()>=3);
    history.addFromDWDStation(dusStation);
    assertTrue(history.getSprinklePeriods().size()>=3);
  }

}

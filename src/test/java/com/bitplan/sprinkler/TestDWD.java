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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;
import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

import de.dwd.geoserver.DWDStation;
import de.dwd.geoserver.WFS;
import de.dwd.geoserver.WFS.Feature;
import de.dwd.geoserver.WFS.WFSResponse;

public class TestDWD {

  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.sprinkler");

  /**
   * test DWD Data
   * 
   * @throws Exception
   */
  @Test
  public void testWFS() throws Exception {
    TestSuite.debug = true;
    WFS.debug = TestSuite.debug;
    Location location = Location.byName("Knickelsdorf/DE");
    if (TestSuite.debug)
      System.out.println(location.toString());
    Coord coord = location.getCoord();
    assertNotNull(coord);
    WFSResponse wfsresponse = WFS.getHistory(WFS.WFSType.RR,coord, 0.5);
    assertNotNull(wfsresponse);
    assertEquals("FeatureCollection", wfsresponse.type);
    assertEquals(9, wfsresponse.totalFeatures);
    if (TestSuite.debug)
      for (Feature feature : wfsresponse.features) {
        System.out.println(feature.toString());
        System.out.println(String.format("%5.1f km",
            feature.geometry.getCoord().distance(coord)));
        System.out
            .println(String.format("\t%s", feature.properties.toString()));
      }
    DWDStation dusStation = wfsresponse.getClosestStation(coord);
    assertEquals("Düsseldorf(1078) - 18,6 km   51° 17’ 45.60” N   6° 46’  6.96” E", dusStation.toString());
  }
  
  public DWDStation getDUSStation() {
    Coord duscoord=new Coord(51.296,6.7686);
    DWDStation dusStation=new DWDStation("1078","Düsseldorf",duscoord,18.6);
    return dusStation;
  }
  
  @Test
  public void testEvaporationHistoryFromDWDStation() throws Exception {
    WFS.debug=true;
    DWDStation dusStation=getDUSStation();
    WFSResponse wfsResponse=WFS.getEvaporationHistory(dusStation);
    assertNotNull(wfsResponse);
    assertTrue(wfsResponse.totalFeatures>0);
  }
  
  @Test
  public void testRainHistoryFromDWDStation() throws Exception {
    WFS.debug=true;
    DWDStation dusStation=getDUSStation();
    WFSResponse wfsResponse=WFS.getRainHistory(dusStation);
    assertNotNull(wfsResponse);
    assertEquals(3,wfsResponse.totalFeatures);
  }

  @Test
  public void testClosestDWDStations() throws Exception {
    boolean debug = false;
    WFS.debug = debug;
    String names[] = { "Knickelsdorf/DE","Koeln/DE", "Bochum/DE", "Muenchen/DE", "Hamburg/DE", "Regensburg/DE",
        "Aachen/DE", "Puttgarden/DE", "Glasgow/GB", "Chicago/US" };
    String expected[] = {
        "Düsseldorf(1078) - 18,6 km   51° 17’ 45.60” N   6° 46’  6.96” E",
        "Köln-Bonn(2667) - 16,4 km   50° 51’ 52.56” N   7°  9’ 27.00” E",
        "Essen-Bredeney(1303) - 19,4 km   51° 24’ 14.76” N   6° 58’  3.72” E",
        "München-Flughafen(1262) - 29,3 km   48° 20’ 51.72” N  11° 48’ 47.88” E",
        "Hamburg-Fuhlsbüttel(1975) - 9,3 km   53° 37’ 59.52” N   9° 59’ 17.16” E",
        "Regensburg(4104) - 3,1 km   49°  2’ 32.64” N  12°  6’  7.56” E",
        "Aachen-Orsbach(15000) - 4,8 km   50° 47’ 53.88” N   6°  1’ 27.84” E",
        "Fehmarn(5516) - 10,6 km   54° 31’ 42.24” N  11°  3’ 37.80” E", null,
        null };
    int i = 0;
    debug=true;
    for (String name : names) {
      Location location = Location.byName(name);
      if (location != null) {
        if (debug)
          System.out.println(location.toString());
        WFSResponse wfsresponse = WFS.getHistory(WFS.WFSType.RR,location.getCoord(), 0.5);
        DWDStation dwdStation = wfsresponse
            .getClosestStation(location.getCoord());
        if (dwdStation != null && debug) {
          System.out.println("  " + dwdStation.toString());
        }
        if (dwdStation!=null) {
          assertEquals(expected[i], dwdStation.toString());
          SprinkleHistory history=new SprinkleHistory();
          history.addFromDWDStation(dwdStation);
          for (SprinklePeriod period:history.getSprinklePeriods()) {
            System.out.println("    " +period.toString());
          }
        }
        else
          assertNull(expected[i]);
        i++;
      } else
        System.err.println("" + name + " not found");
    }
  }

}

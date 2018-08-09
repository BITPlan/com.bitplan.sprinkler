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

import java.util.logging.Logger;

import org.junit.Test;
import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

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
    WFS.debug=TestSuite.debug;
    Location location = Location.byName("Knickelsdorf/DE");
    if (TestSuite.debug)
      System.out.println(location.toString());
    Coord coord = location.getCoord();
    assertNotNull(coord);
    WFSResponse wfsresponse = WFS.getRainHistory(coord, 0.5);
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
    String dusId = wfsresponse.getClosestId(coord);
    assertEquals(dusId, "1078");
  }

}

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
package de.dwd.geoserver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openweathermap.weather.Coord;

import de.dwd.geoserver.WFS.Feature;
import de.dwd.geoserver.WFS.Property;
import de.dwd.geoserver.WFS.WFSResponse;
import de.dwd.geoserver.WFS.WFSType;

/**
 * a weather station of Deutscher Wetterdienst
 * 
 * @author wf
 *
 */
public class DWDStation {
  String name;
  public String id;
  Coord coord;
  Double distance;

  /**
   * Construct a station of Deutscher Wetterdienst
   * 
   * @param id
   * @param name
   * @param coord
   * @param distance
   */
  public DWDStation(String id, String name, Coord coord, double distance) {
    this.id = id;
    this.name = name;
    this.coord = coord;
    this.distance = distance;
  }

  /**
   * construct me from a feature with the given distance
   * 
   * @param feature
   * @param distance
   */
  public DWDStation(Feature feature, double distance) {
    this(feature.properties.ID, feature.properties.NAME,
        feature.geometry.getCoord(), distance);
  }

  /**
   * construct me from a feature
   * 
   * @param feature
   */
  public DWDStation(Feature feature) {
    this(feature, 0.0);
  }

  public String toString() {
    String text = String.format(Locale.GERMAN, "%s(%s) - %.1f km %s", name, id,
        distance, coord.toString());
    return text;
  }

  /**
   * get a map of all DWD Stations
   * 
   * @return the map of DWD Stations by ID
   * @throws Exception
   */
  public static Map<String, DWDStation> getAllStations() throws Exception {
    Map<String, DWDStation> stations = new HashMap<String, DWDStation>();
    Coord nw = new Coord(47.3, 5.9);
    Coord se = new Coord(55.0, 15.1);
    WFSResponse wfsresponse = WFS.getResponseForBox(WFSType.FF, nw, se);
    for (Feature feature : wfsresponse.features) {
      if (!stations.containsKey(feature.properties.ID)) {
        DWDStation station = new DWDStation(feature);
        stations.put(station.id, station);
      }
    }
    return stations;
  }
}
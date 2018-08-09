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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.utils.URIBuilder;
import org.openweathermap.weather.Coord;

import com.bitplan.util.JsonUtil;
import com.google.gson.Gson;

/**
 * Access Deutscher Wetterdienst WFS Service
 * https://maps.dwd.de/geoserver/ows?service=wfs&version=2.0.0&request=GetCapabilities
 * 
 * @author wf
 *
 */
public class WFS {
  public static String version = "2.0.0";
  // set to true to debug
  public static boolean debug = false;
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("de.dwd.geoserver");

  /**
   * Json WFS response decoding
   * 
   * @author wf
   *
   */
  public class WFSResponse {
    public String type;
    public int totalFeatures;
    public List<Feature> features;

    /**
     * get the id of the closest point
     * 
     * @param coord
     * @return - the closest point
     */
    public String getClosestId(Coord coord) {
      String resultId = null;
      if (totalFeatures > 0) {
        Map<Double, String> distanceMap = new TreeMap<Double, String>();
        for (Feature feature : features) {
          double distance = feature.geometry.getCoord().distance(coord);
          String id = feature.properties.ID;
          distanceMap.put(distance, id);
        }
        Entry<Double, String> first = distanceMap.entrySet().iterator().next();
        resultId = first.getValue();
      }
      return resultId;
    }
  }

  public class Feature {
    public String type;
    public String id;
    public Geometry geometry;
    public String geometry_name;
    public Property properties;

    public String toString() {
      String text = String.format("type: %s id: %s, geeometry: %s/%s", type, id,
          geometry.toString(), geometry_name);
      return text;
    }
  }

  public class Geometry {
    public String type;
    public Double[] coordinates;
    transient Coord coord;

    public Coord getCoord() {
      if (coord == null)
        coord = new Coord(coordinates[1], coordinates[0]);
      return coord;
    }

    public String toString() {
      String text = String.format("type: %s coordinates: %s", type,
          getCoord().toString());
      return text;
    }
  }

  public class Property {
    String ID;
    String NAME;
    Double PRECIPITATION;
    String M_DATE;
    Double[] bbox;

    public String toString() {
      Coord corner1 = new Coord(bbox[1], bbox[0]);
      Coord corner2 = new Coord(bbox[3], bbox[2]);
      String text = String.format(
          "id: %s name: %s precipitation: %5.1f mm, mdate: %s, bbox: %s-%s", ID,
          NAME, PRECIPITATION, M_DATE, corner1.toString(), corner2.toString());
      return text;
    }
  }

  /**
   * get the rain history for a given coordinate
   * 
   * @param coord
   * @param boxMargin
   *          - degrees
   * @return a WFS Response
   * @throws Exception
   */
  public static WFSResponse getRainHistory(Coord coord, double boxMargin)
      throws Exception {
    URIBuilder builder = new URIBuilder();
    builder.setScheme("https");
    builder.setHost("maps.dwd.de");
    builder.setPath("geoserver/dwd/ows");
    builder.addParameter("service", "WFS");
    builder.addParameter("version", version);
    builder.addParameter("request", "GetFeature");
    builder.addParameter("typeName", "dwd:RBSN_RR");
    builder.addParameter("outputFormat", "application/json");
    builder.addParameter("bbox",
        String.format(Locale.ENGLISH, "%5.2f,%5.2f,%5.2f,%5.2f",
            coord.getLat() - boxMargin, coord.getLon() - boxMargin,
            coord.getLat() + boxMargin, coord.getLon() + boxMargin));

    String url = builder.build().toString();
    if (debug)
      LOGGER.log(Level.INFO, url);
    String json = JsonUtil.read(url);
    if (debug) {
      LOGGER.log(Level.INFO, json.replaceAll("\\{", "\n{"));
    }
    Gson gson = new Gson();
    WFSResponse wfsresponse = gson.fromJson(json, WFSResponse.class);
    return wfsresponse;
  }
}

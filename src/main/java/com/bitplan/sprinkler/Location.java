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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

/**
 * https://github.com/BITPlan/com.bitplan.sprinkler/issues/1
 * @author wf
 *
 */
public class Location {
  public static boolean debug=false;
  public static String url="http://bulk.openweathermap.org/sample/city.list.json.gz";
  protected static Map<String,Location> locationsByNameMap=new HashMap<String,Location>();
  protected static Map<Long,Location> locationsByIdMap=new HashMap<Long,Location>();
  protected static Location[] locations;
  
  long id;
  String name;
  String country;
  public static class Coordinate {
    double lat;
    double lon;
    public double getLat() {
      return lat;
    }
    public void setLat(double lat) {
      this.lat = lat;
    }
    public double getLon() {
      return lon;
    }
    public void setLon(double lon) {
      this.lon = lon;
    }
  }
  Coordinate coord;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public Coordinate getCoord() {
    return coord;
  }
  public void setCoord(Coordinate coord) {
    this.coord = coord;
  }
  /**
   * get the locations
   * @return the locations
   * @throws MalformedURLException
   * @throws IOException
   */
  public static Location[] getLocations() throws MalformedURLException, IOException {
    if (locations==null) {
      InputStream urlStream = new URL(url).openStream();
      InputStream gzStream =new GZIPInputStream(urlStream);
      StringWriter jsonWriter = new StringWriter();
      IOUtils.copy(gzStream, jsonWriter, "UTF-8");
      String json = jsonWriter.toString();
      urlStream.close();
      if (debug)
        System.out.println(json.substring(0, 280));
      Gson gson=new Gson();
      locations=gson.fromJson(json,Location[].class);
    }
    return locations;
  }
  
  /**
   * get the locations by name map
   * @return the map of locations by name
   * @throws Exception
   */
  public static Map<String,Location> getLocationsByName() throws Exception {
    if (locationsByNameMap.size()==0) {
      for (Location location:getLocations()) {
        locationsByNameMap.put(location.getName(), location);
      }
    }
    return locationsByNameMap;
  }
  
  /**
   * get a location by name
   * @param name - the name to lookup
   * @return the location
   * @throws Exception 
   */
  public static Location byName(String name) throws Exception {
    Location location=getLocationsByName().get(name);
    return location;
  }
  
  /**
   * get the locations by Id map
   * @return the map of locations by Id
   * @throws Exception
   */
  public static Map<Long,Location> getLocationsById() throws Exception {
    if (locationsByIdMap.size()==0) {
      for (Location location:getLocations()) {
        locationsByIdMap.put(location.getId(), location);
      }
    }
    return locationsByIdMap;
  }
  
  /**
   * get a location by Id
   * @param Id - the id to l ook for
   * @return the location
   * @throws Exception 
   */
  public static Location byId(long Id) throws Exception {
    Location location=getLocationsById().get(Id);
    return location;
  }
}

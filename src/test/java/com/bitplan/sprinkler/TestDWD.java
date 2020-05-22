package com.bitplan.sprinkler;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.openweathermap.weather.Location;


import de.dwd.geoserver.Station;
import de.dwd.geoserver.WFS;
import de.dwd.geoserver.WFS.WFSResponse;

/**
 * test API of DWD (Deutscher Wetterdienst)
 * @author wf
 *
 */
public class TestDWD {
  boolean debug=false;
  
  @Test
  public void testGetLocations()  throws Exception {
    // get the open Weather map locations
    Map<String, Location> locMap = Location.getLocationsByName();
    // there are more than 150.000 locations in the open weather map location table
    assertTrue(150000<locMap.size());
  }
  // https://openweathermap.org/find?q=
  String names[] = { "Knickelsdorf/DE","Köln/DE", "Bochum/DE", "Munich/DE", "Hamburg/DE", "Regensburg/DE",
      "Aachen/DE", "Puttgarden/DE", "Glasgow/GB", "Chicago/US" };

  @Test
  public void testLocationsByName() throws Exception {
    boolean foundAll=true;
    for (String name : names) {
      Location location = Location.byName(name);
      if (location != null) {
        if (debug)
          System.out.println(location.toString());
      } else {
        System.out.println(String.format("%s not found",name));
      }
    }
    assertTrue(foundAll);
  }

  @Test
  public void testClosestDWDStations() throws Exception {
    WFS.debug = debug;
     String expected[] = {
        "Düsseldorf(1078) - 18,6 km   51° 17’ 45.60” N   6° 46’  6.96” E",
        "Köln-Bonn(2667) - 16,4 km   50° 51’ 52.56” N   7°  9’ 27.00” E",
        "Essen-Bredeney(1303) - 19,4 km   51° 24’ 14.76” N   6° 58’  3.72” E",
        "München-Flughafen(1262) - 29,3 km   48° 20’ 51.72” N  11° 48’ 47.88” E",
        "Hamburg-Fuhlsbüttel(1975) - 9,3 km   53° 37’ 59.52” N   9° 59’ 17.16” E",
        "Regensburg(4104) - 3,1 km   49°  2’ 33.00” N  12°  6’  6.84” E",
        "Aachen-Orsbach(15000) - 5,1 km   50° 47’ 53.88” N   6°  1’ 27.84” E",
        "Fehmarn(5516) - 10,6 km   54° 31’ 41.88” N  11°  3’ 38.16” E", null,
        null };
    int i = 0;
    debug=true;
    for (String name : names) {
      Location location = Location.byName(name);
      if (location != null) {
        if (debug)
          System.out.println(location.toString());
        WFSResponse wfsresponse = WFS.getResponseAt(WFS.WFSType.RR,location.getCoord(), 0.5);
        Station dwdStation = wfsresponse
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

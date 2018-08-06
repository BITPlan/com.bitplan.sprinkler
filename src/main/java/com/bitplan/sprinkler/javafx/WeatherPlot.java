package com.bitplan.sprinkler.javafx;

import java.util.logging.Logger;

import org.openweathermap.weather.Forecast;
import org.openweathermap.weather.WeatherForecast;

import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

@SuppressWarnings("restriction")
public class WeatherPlot {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler.javafx");
  public static boolean debug = false;

  String title;
  String xTitle;
  String yTitle;
  private WeatherForecast forecast;

  /**
   * construct me with the given title, xTitle and yTitle
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   */
  public WeatherPlot(String title, String xTitle, String yTitle,
      WeatherForecast forecast) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.forecast = forecast;
  }

  XYChart.Series<String, Number> series;
  private NumberAxis yAxis;

  /**
   * get the BarChart for the cellStates (e.g. Temperature/Voltage)
   * 
   * @return - the barchart
   */
  public BarChart<String, Number> getBarChart() {
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis();
    yAxis = new NumberAxis();
    yAxis.setLabel(yTitle);
    xAxis.setLabel(xTitle);
    // creating the chart
    final BarChart<String, Number> barChart = new BarChart<String, Number>(
        xAxis, yAxis);

    barChart.setTitle(title);
    barChart.setCategoryGap(0);
    barChart.setBarGap(1);
    // defining a series
    series = new XYChart.Series<String, Number>();
    series.setName(forecast.city.getName() + "/" + forecast.city.getCountry());
    ObservableList<Data<String, Number>> seriesData = series.getData();
    for (Forecast forecast : forecast.list) {
      String time=forecast.dt_txt.substring(8,13);
      seriesData.add(new XYChart.Data<String, Number>(time,
          forecast.getPrecipitation()));
    }
    barChart.getData().add(series);
    return barChart;
  }

}

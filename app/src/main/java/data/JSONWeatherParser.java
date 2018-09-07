package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Constants.Constants;
import Weather.Location;
import Weather.WeatherConditions;

public class JSONWeatherParser {

    //return stringBuffer.toString(); //nehmen das, was der stringBuffered speichert und machen Objekte draus
    public static WeatherConditions getWeather(String data) {
        //create JSON Object from data

        WeatherConditions weatherConditions = new WeatherConditions();

        try {
            JSONObject jsonObject = new JSONObject(data);

            Location location = new Location();

            JSONObject coordObj = Constants.getObject("coord", jsonObject); //Koordinaten aus der API, nimmt das object mit tagname coord, also koordinaten
            location.setLatitude(Constants.getFloat("lat", coordObj));
            location.setLongitude(Constants.getFloat("lon", coordObj));

            //Sys Object
            JSONObject sysObject = Constants.getObject("sys", jsonObject); //kein coordObj, weil Coord nur lat und lon hat und der rest wieder ins HauptObjekt kommt
            location.setCountry(Constants.getString("country", sysObject));
            location.setLastUpdate(Constants.getInt("dt", jsonObject)); //jsonObj weil dt is wieder ausserhalb von sys Object
            location.setSunrise(Constants.getInt("sunrise", sysObject));
            location.setSunset(Constants.getInt("sunset", sysObject));
            location.setCity(Constants.getString("name", jsonObject));
            weatherConditions.location = location;

            //get the weather info , weather is an array in the API, hat nur ein Item drin deswegen index 0, alles in API gegeben
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weatherConditions.currentWeather.setWeatherId(Constants.getInt("id", jsonWeather));
            weatherConditions.currentWeather.setDescription(Constants.getString("description", jsonWeather));
            weatherConditions.currentWeather.setCondition(Constants.getString("main", jsonWeather));
            weatherConditions.currentWeather.setIcon(Constants.getString("icon", jsonWeather));

            JSONObject mainObj = Constants.getObject("main", jsonObject);
            weatherConditions.currentWeather.setHumidity(Constants.getInt("humidity", mainObj));
            weatherConditions.currentWeather.setTempMin(Constants.getDouble("temp_min", mainObj));
            weatherConditions.currentWeather.setTempMax(Constants.getDouble("temp_max", mainObj));
            weatherConditions.currentWeather.setTemperature(Constants.getDouble("temperature", mainObj));

            //wind
            JSONObject windObj = Constants.getObject("wind", jsonObject);
            weatherConditions.wind.setSpeed(Constants.getFloat("speeed", windObj));
            weatherConditions.wind.setDegree(Constants.getFloat("deg", windObj));

            //Clouds
            JSONObject cloudObj = Constants.getObject("clouds", jsonObject);
            weatherConditions.clouds.setRain(Constants.getInt("all", cloudObj));


            return weatherConditions;//Because weather hat jetz alles drin


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}

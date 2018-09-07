package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Constants.Constants;

//class where we get the json object //alle Information wird hier vom Netz geholt und in einen Text umgewandelt
public class WeatherHttpClient {

    public String getWeatherData (String location) {

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (new URL(Constants.BASIC_URL + location)).openConnection();
            connection.setRequestMethod("GET"); //das Daten freigegeben werden
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();//Get die Sachen ausm web vom link bzw von der API
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); //bufferedReader kann Daten ausm Web lesen. buffered reader besitzt also die ganzen daten ausm netz
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n"); //soll jede Zeile extra lesen und nacheinander alles oben in StringBuffer speichern
            }


            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString(); //alles muss zu String konvertiert werden


        } catch (IOException e) { //because when no connection bze no Internet you need try and catch
            e.printStackTrace();
        }

        return null;
    }
}


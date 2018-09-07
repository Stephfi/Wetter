package com.example.steffi.weatherapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Constants.Constants;
import Weather.WeatherConditions;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;

public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    WeatherConditions weatherConditions = new WeatherConditions();



    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if(isConnected) {
            humidity.setText("connected");
        } else {
            humidity.setText("not connected");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InternetAvailabilityChecker.init(this);

        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        cityName =(TextView) findViewById(R.id.cityText);
        iconView = (ImageView)findViewById(R.id.icon);
        temp = (TextView) findViewById(R.id.temperatureText);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidityText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        updated = (TextView) findViewById(R.id.lastUpdated);

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        renderWeatherData(cityPreference.getCity());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void renderWeatherData (String city) {

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});//get information fro the Web
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            return dowmloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap dowmloadImage (String code) {

            try {
                URL url = new URL(Constants.ICON + ".png");
                Log.d("Data: ", url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap currentBitmap = BitmapFactory.decodeStream(input);
                return currentBitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Alle Funktionen im Hintergrund, weil durch Sachen ausm Netz rausholen braucht alles viel Zeit aber wir wollen das die App schnell funktionniert und deshab Async Task

    private class WeatherTask extends AsyncTask<String, Void, WeatherConditions>{
        @Override //das alles im Hintergrund läuft
        protected WeatherConditions doInBackground(String... params) {

            //data holt wieder alles ausm StringBuffer
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            weatherConditions.iconData = weatherConditions.currentWeather.getIcon();

            weatherConditions = JSONWeatherParser.getWeather(data);

            Log.v("Data: ", weatherConditions.currentWeather.getDescription());

            new DownloadImageAsyncTask().execute(weatherConditions.iconData);

            return weatherConditions;
        }
        


        @Override //alle Daten von Background dem User zeigen
        protected void onPostExecute(WeatherConditions weatherConditions) {
            super.onPostExecute(weatherConditions);

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weatherConditions.location.getSunrise()));
            String sunsetDate = df.format(new Date(weatherConditions.location.getSunset()));
            String lastUpdate = df.format(new Date(weatherConditions.location.getLastUpdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#"); //#.# double oder float runden

            String tempFormat = decimalFormat.format(weatherConditions.currentWeather.getTemperature());

            int humidityValue = weatherConditions.currentWeather.getHumidity();

            cityName.setText(weatherConditions.location.getCity() + "," + weatherConditions.location.getCountry());
            temp.setText("" + tempFormat + "°C");
            humidity.setText(humidityValue);
            wind.setText("Wind" + weatherConditions.wind.getSpeed() +"mps");
            sunrise.setText("Sonnenaufgang" + sunriseDate);
            sunset.setText("Sonnenuntergang" + sunsetDate);
            updated.setText("Zuletzt aktualisiert" + lastUpdate);
            description.setText("Condition" + weatherConditions.currentWeather.getCondition() + "(" +
                weatherConditions.currentWeather.getDescription() + ")");

        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Andere Stadt");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Regensburg, De");
        builder.setView(cityInput);
        builder.setPositiveButton("Suchen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_cityId) {
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }
}

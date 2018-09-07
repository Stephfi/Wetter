package Weather;

public class WeatherConditions {

    public Location location;
    public String iconData;
    public CurrentWeather currentWeather = new CurrentWeather();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();

}

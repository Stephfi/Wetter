package Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Constants {

    public static final String BASIC_URL ="https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=08b0554d25e979ac4f422baf20a69306";
    public static final String ICON = "http://openweathermap.org/img/w/";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException {
        JSONObject jsonObj = jsonObject.getJSONObject(tagName);
        return jsonObj;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static double getDouble (String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static int getInt (String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt(tagName);
    }
}

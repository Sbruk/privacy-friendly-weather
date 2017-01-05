package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmAddCityRequest implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_add_list_item";

    /**
     * Member variables
     */
    private Context context;
    private PFASQLiteHelper dbHelper;
    //private boolean storePersistently;


    /**
     * Constructor.
     *
     * @param context  The context of the HTTP request.
     */
    public ProcessOwmAddCityRequest(Context context) {
        this.context = context;
        this.dbHelper = PFASQLiteHelper.getInstance(context);
    }

    /**
     * Constructor.
     *
     * @param context  The context of the HTTP request.
     * @param dbHelper The database helper to use.
     */
    public ProcessOwmAddCityRequest(Context context, DatabaseHelper dbHelper, boolean storePersistently) {
        // TODO: remove this constructor
        this.context = context;
        //this.dbHelper = dbHelper;
        this.dbHelper = PFASQLiteHelper.getInstance(context);
        //this.storePersistently = storePersistently;
    }

    /**
     * Converts the response to JSON and updates the database so that the latest weather data are
     * displayed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        IDataExtractor extractor = new OwmDataExtractor();
        // City was found => can now be extracted
        if (extractor.wasCityFound(response)) {
            CurrentWeatherData weatherData = extractor.extractCurrentWeatherData(response);
            int cityId = extractor.extractCityID(response);
            // Error case :/
            if (weatherData == null || cityId == Integer.MIN_VALUE) {
                final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
            } else {
                weatherData.setCity_id(cityId);

                // TODO: we have to delete the existing weatherData first ...
                //dbHelper.getCurrentWeatherByCity(cityId);
                dbHelper.addCurrentWeather(weatherData);
                //if (storePersistently) {
                    // TODO: Update the UI
                    // Show success message
                    //final String SUCCESS_MSG = context.getResources().getString(R.string.dialog_add_added_successfully_template);
                    //Toast.makeText(context, SUCCESS_MSG, Toast.LENGTH_LONG).show();
                //} else {
                    //Intent intent = new Intent(context, CityWeatherActivity.class);
                    //intent.putExtra("weatherData", weatherData);
                    //context.startActivity(intent);
                //}
            }
        }
        // City was not found; sometimes this happens for OWM requests even though the city ID is
        // valid
        else {
            Toast.makeText(context, R.string.activity_main_location_not_found, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(VolleyError error) {

    }

}

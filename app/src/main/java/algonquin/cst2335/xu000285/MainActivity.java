package algonquin.cst2335.xu000285;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import algonquin.cst2335.xu000285.databinding.ActivityMainBinding;

/**
 * Page that prompts user to input City Name and display the weather information.
 * @author Huixin Xu
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    protected String cityName;
    RequestQueue queue = null;
    Button forecastBtn = null;
    EditText cityNameInput = null;
    RequestQueue imgqueue = null;

    Bitmap image;

    TextView temp;
    TextView maxTemp;
    TextView minTemp;
    TextView humid;
    ImageView weatherIcon;
    TextView desc;


    @Override // equals to staic void main(Stirng args[])
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loads the screen
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate( getLayoutInflater() );

        cityNameInput = binding.cityTextField;
        forecastBtn = binding.forecastBtn;
        temp = binding.temp;
        maxTemp = binding.maxTemp;
        minTemp = binding.minTemp;
        humid = binding.humidity;
        desc = binding.description;
        weatherIcon = binding.icon;


        queue = Volley.newRequestQueue(this);
        imgqueue = Volley.newRequestQueue(this);

        forecastBtn.setOnClickListener(click -> {
            cityName = cityNameInput.getText().toString();
            String apiKey = "7e943c97096a9784391a981c4d878b22";
            String url="https://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(cityName)
                    +"&appid="+ apiKey+ "&units=metric";


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    (response) -> {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");
                            JSONArray weather = response.getJSONArray("weather");
                            JSONObject position0 = weather.getJSONObject(0);
                            String objDesc = position0.getString("description");
                            String iconName = position0.getString("icon");
                            double current = mainObject.getDouble("temp");
                            double min = mainObject.getDouble("temp_min");
                            double max = mainObject.getDouble("temp_max");
                            int humidity = mainObject.getInt("humidity");


                            // make sure to call these functions on the main GUI thread
                            // to set and display the temperature and humidity
                            runOnUiThread(()->{
                                temp.setText("The current temperature is " + current);
                                temp.setVisibility(View.VISIBLE);

                                maxTemp.setText("The max temperature is " + max);
                                maxTemp.setVisibility(View.VISIBLE);

                                minTemp.setText("The min temperature is " + min);
                                minTemp.setVisibility(View.VISIBLE);

                                humid.setText("The humidity is " + humidity+"%");
                                humid.setVisibility(View.VISIBLE);

                                desc.setText(objDesc);
                                desc.setVisibility(View.VISIBLE);
                            });


                            String imageUrl = "https://openweathermap.org/img/w/"+ iconName+ ".png";
                            String pathname = getFilesDir()+ "/" + iconName+ ".png";

                            File file = new File(pathname);

                            // download the URL and store it as a bitmap
                            // Here you are making a second HTTP request to the server and the
                            // BitmapFactory.decodeStream() is reading the data from the server.
                            if (file.exists()){
                                image = BitmapFactory.decodeFile(pathname);
                                weatherIcon.setImageBitmap(image);
                            }else{
                                ImageRequest imgReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                            saveImageToFile(bitmap,iconName);
                                        weatherIcon.setImageBitmap(bitmap);
                                        try {
                                            bitmap.compress(Bitmap.CompressFormat.PNG,100,MainActivity.this.openFileOutput(iconName+".png", Activity.MODE_PRIVATE));
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }, 1024, 1024, ImageView.ScaleType.CENTER, null,
                                        (error)->{
                                            Log.d("MainActivity","Image request failure");
                                            error.printStackTrace();}
                                );
                                queue.add(imgReq);
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    },
                    (error) -> {
                        Log.d("MainActivity","Weather request failure");
                        error.printStackTrace(); });
            queue.add(request);// send request to server
                    });
        }

    private void saveImageToFile(Bitmap bitmap, String iconName) {
        FileOutputStream fOut = null;
        try {
            fOut = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
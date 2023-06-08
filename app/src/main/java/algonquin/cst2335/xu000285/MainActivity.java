package algonquin.cst2335.xu000285;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import algonquin.cst2335.xu000285.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding variableBinding;
    //private MainViewModel model;

    private String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "In OnCreate()");

        //view binding
        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        //instaniate a view model
        //model = new ViewModelProvider(this).get(MainViewModel.class);
        variableBinding.emailPrompt.setText(prefs.getString("Email", ""));
        //event listener
        variableBinding.loginBtn.setOnClickListener((v)->{
            Log.e(TAG, "You clicked the button");

            //create next page
            Intent nextPage = new Intent(this, SecondActivity.class);

            //get email and pw from user prompt
            String emailAddress = variableBinding.emailPrompt.getText().toString();
            String pw = variableBinding.pwPrompt.getText().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Email", emailAddress);
            editor.apply();

//            nextPage.putExtra("Email", emailAddress);
//            nextPage.putExtra("Password", pw);

            // next page
            startActivity(nextPage);
        });

    }
}
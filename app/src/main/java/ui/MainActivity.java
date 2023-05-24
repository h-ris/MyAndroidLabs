package ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import algonquin.cst2335.xu000285.R;
import algonquin.cst2335.xu000285.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding variableBinding;
    private MainViewModel model;

    // equals to staic void main(Stirng args[])
    @Override //this is the starting point
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loads the screen
        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());

        TextView theText = findViewById(R.id.textview);

        Button myButton = findViewById(R.id.mybutton);

        model = new ViewModelProvider(this).get(MainViewModel.class);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this gets run when you click the button
                theText.setText("You clicked the button.");
                myButton.setText("Something new here.");
            }
        });
    }
}
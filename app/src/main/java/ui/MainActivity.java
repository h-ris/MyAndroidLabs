package ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import algonquin.cst2335.xu000285.R;
import algonquin.cst2335.xu000285.databinding.ActivityMainBinding;
import data.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding variableBinding;
    private MainViewModel model;

    // equals to staic void main(Stirng args[])
    @Override //this is the starting point
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(this).get(MainViewModel.class);

        //loads the screen
        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());

        variableBinding.mybutton.setOnClickListener(click ->
        {
            model.editString.postValue(variableBinding.myedittext.getText().toString());
            model.editString.observe(this, s -> {
                variableBinding.textview.setText("Your edit text has " + s);
            });
        });

        model.isSelected.observe(this, selected -> {
            variableBinding.myCheckBox.setChecked(selected);
            variableBinding.myRadioButton.setChecked(selected);
            variableBinding.mySwitch.setChecked(selected);

            Context context = getApplicationContext();
            CharSequence text = "The value is now: " + selected;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });

        variableBinding.myCheckBox.setOnCheckedChangeListener( (btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });

        variableBinding.myRadioButton.setOnCheckedChangeListener( (btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });

        variableBinding.mySwitch.setOnCheckedChangeListener( (btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });

        variableBinding.myImageButton.setOnClickListener(click -> {
            int width = variableBinding.myImageButton.getWidth();
            int height = variableBinding.myImageButton.getHeight();

            Context context = getApplicationContext();
            CharSequence text = "The width = " + width + " and height = " + height;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });



//        myButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // this gets run when you click the button
//                myText.setText("You clicked the button.");
//                myButton.setText("Something new here.");
//            }
//        });
    }
}
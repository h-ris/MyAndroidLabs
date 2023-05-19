package algonquin.cst2335.xu000285;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    // equals to staic void main(Stirng args[])
    @Override //this is the starting point
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loads the screen
        setContentView(R.layout.activity_main);

        TextView theText = findViewById(R.id.textview);

        Button myButton = findViewById(R.id.mybutton);

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
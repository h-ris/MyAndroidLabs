package algonquin.cst2335.xu000285;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    // equals to staic void main(Stirng args[])
    @Override //this is the starting point
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loads the screen
        setContentView(R.layout.activity_main);
    }
}
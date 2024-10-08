package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.dieschnittstelle.mobile.android.skeleton.R;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message_alternative);

        welcomeText.setOnClickListener((view) ->
                Toast.makeText(MainActivity.this,"HELLO AGAIN",Toast.LENGTH_SHORT).show()
        );
    }
}

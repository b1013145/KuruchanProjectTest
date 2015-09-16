package com.example.b1013_000.kuruchanpj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by b1013_000 on 2015/09/06.
 */
public class Screen1 extends Activity implements View.OnClickListener{
    private Button bt1, bt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);

        bt1 = (Button) findViewById(R.id.toScreen2);
        bt2 = (Button) findViewById(R.id.toScreen4);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toScreen2:
                Intent toScreen2 = new Intent(Screen1.this, Screen2.class);
                startActivity(toScreen2);
                break;
            case R.id.toScreen4:
                Intent toScreen4 = new Intent(Screen1.this, Screen4.class);
                startActivity(toScreen4);
                break;
            default:
        }

    }
}

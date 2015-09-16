package com.example.b1013_000.kuruchanpj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by b1013_000 on 2015/09/12.
 */
public class Screen0 extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Screen0.this, Screen1.class);
                startActivity(intent);
            }
        }, 2000);
    }
}

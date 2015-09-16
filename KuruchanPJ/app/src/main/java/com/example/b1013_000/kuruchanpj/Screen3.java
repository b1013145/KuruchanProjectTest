package com.example.b1013_000.kuruchanpj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by b1013_000 on 2015/09/06.
 */
public class Screen3 extends Activity implements View.OnClickListener{
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);

        bt = (Button) findViewById(R.id.button6);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button6:
                Intent intent = new Intent(Screen3.this, Screen1.class);
                startActivity(intent);
            default:
        }

    }
}

package com.example.funactivity.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;

public class ChangeNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        Button save=findViewById(R.id.btn_save);
        final EditText name=findViewById(R.id.et_name);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.getText();//得到修改后的名字
            }
        });
    }
    public void back(View view){
        Intent i=new Intent(this,IdAndSercurityActivity.class);
        startActivity(i);
    }
}

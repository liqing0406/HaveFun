package com.example.funactivity.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final EditText oldPassword=findViewById(R.id.et_old_password);
        final EditText newPassword=findViewById(R.id.et_new_password);
        Button btnSave=findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword.getText();
                newPassword.getText();
            }
        });
    }
    public void back(View view){
        Intent i=new Intent(this,IdAndSercurityActivity.class);
        startActivity(i);
    }
}

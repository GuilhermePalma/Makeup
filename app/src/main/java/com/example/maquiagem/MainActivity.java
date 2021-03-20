package com.example.maquiagem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private EditText editType;
    private EditText editBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);
    }



    public void LoadResult(View v){
        Intent it = new Intent(MainActivity.this, WindowResult.class);
        startActivity(it);
    }

}




package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DogInfo extends AppCompatActivity {

    Spinner size, gender;
    String Size, Gender, Name, Birth, Type;
    String result = "";
    String result_code;
    EditText name, birth, type;
    Button save;
    JSONObject json;
    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_info);

        size = findViewById(R.id.sp_dog_size);
        gender = findViewById(R.id.sp_dog_gender);
        name = findViewById(R.id.et_dog_name);
        birth = findViewById(R.id.et_dog_birth);
        type = findViewById(R.id.et_dog_type);

        save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = name.getText().toString().trim();
                Birth = birth.getText().toString().trim();
                Size = size.getSelectedItem().toString();
                Gender = gender.getSelectedItem().toString();
                Type = type.getText().toString().trim();

                json = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("usn", Values.USN);
                    jsonObject.put("dog_name", Name);
                    jsonObject.put("dog_size", Size);
                    jsonObject.put("birth", Birth);
                    jsonObject.put("dog_gender", Gender);
                    jsonObject.put("dog_type", Type);

                    Log.d("asdf1", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.d("asdf2", jsonObject.toString());
                    result = new PostJSON().execute("http://caerang2.esllee.com/dog/regist/process", jsonObject.toString()).get();
                    Log.d("asdf3", result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d("asdf411", e.toString());
                    e.printStackTrace();
                }
                try {
                    JSONObject json_data = new JSONObject(result);
                    Log.d("asdf5", "receive json: " + json_data.toString());
                    result_code = (json_data.optString("result_code"));
                    Log.d("asdf6", "result_code: " + result_code);

                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());
                }
                if(result_code.equals("1")){
                    Toast.makeText(DogInfo.this, "Save your dog Information", Toast.LENGTH_SHORT).show();
                    intent1 =  new Intent(getApplicationContext(), Home.class);
                }
                else {
                    Toast.makeText(DogInfo.this, "Error appeared", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
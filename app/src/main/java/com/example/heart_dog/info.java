package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class info extends AppCompatActivity {

    String result;
    String[] dog_name;
    String[] dog_type;
    String[] dog_size;
    String[] dog_gender;
    String[] dog_birth;
    String[] name;
    String[] phone;
    TextView tv_name, tv_phone, tv_dog_name, tv_dog_type, tv_dog_size, tv_dog_gender, tv_dog_birth;
    Button before, next;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tv_name = findViewById(R.id.text_name);
        tv_phone = findViewById(R.id.text_phone);
        tv_dog_name = findViewById(R.id.tv_dog_name);
        tv_dog_type = findViewById(R.id.tv_dog_type);
        tv_dog_size = findViewById(R.id.tv_dog_size);
        tv_dog_gender = findViewById(R.id.tv_dog_gender);
        tv_dog_birth = findViewById(R.id.tv_dog_birth);

        before = findViewById(R.id.btn_back);
        next = findViewById(R.id.btn_next);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usn", Values.USN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            result = new PostJSON().execute("http://caerang2.esllee.com/user/profile/process", jsonObject.toString()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            JSONArray arr = new JSONObject(result).getJSONArray("message");

            name = new String[arr.length()];
            phone = new String[arr.length()];
            dog_name = new String[arr.length()];
            dog_type = new String[arr.length()];
            dog_size = new String[arr.length()];
            dog_gender = new String[arr.length()];
            dog_birth = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                JSONObject JSON = arr.getJSONObject(i);
                dog_name[i] = JSON.getString("dog_name");
                dog_type[i] = JSON.getString("dog_type");
                dog_size[i] = JSON.getString("dog_size");
                dog_gender[i] = JSON.getString("dog_gender");
                dog_birth[i] = JSON.getString("birth");
                name[i] = JSON.getString("name");
                phone[i] = JSON.getString("phone");
            }
        } catch (Exception e) {
            Log.d("Fail 3", e.toString());
        }
        tv_name.setText(name[0]);
        tv_phone.setText(phone[0]);
        tv_dog_name.setText(dog_name[0]);
        tv_dog_type.setText(dog_type[0]);
        tv_dog_size.setText(dog_size[0]);
        tv_dog_gender.setText(dog_gender[0]);
        tv_dog_birth.setText(dog_birth[0]);

        index = 0;

        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index <= 0) {

                }
                else {
                    index = index - 1;
                    tv_dog_name.setText(dog_name[index]);
                    tv_dog_type.setText(dog_type[index]);
                    tv_dog_size.setText(dog_size[index]);
                    tv_dog_gender.setText(dog_gender[index]);
                    tv_dog_birth.setText(dog_birth[index]);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (index + 1 >= dog_name.length) {
                    Toast.makeText(info.this, "정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    index = index + 1;
                    tv_dog_name.setText(dog_name[index]);
                    tv_dog_type.setText(dog_type[index]);
                    tv_dog_size.setText(dog_size[index]);
                    tv_dog_gender.setText(dog_gender[index]);
                    tv_dog_birth.setText(dog_birth[index]);
                }
            }
        });
    }
}


package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class sign_up_check extends AppCompatActivity {

    String code, result = "";
    String result_code;
    EditText Code;
    Button sign;
    Intent intent1;
    String email;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_check);

        email = getIntent().getStringExtra("email");

        Code = findViewById(R.id.et_code);

        sign = findViewById(R.id.btn_signup);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCode(Code.getText().toString().trim());

                if(getCode().isEmpty()) { // 인증번호를 입력하지 않은 경우
                    Toast.makeText(sign_up_check.this, "인증번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email", email);
                        jsonObject.put("authorized_code", getCode());

                        Log.d("asdf1", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (code.length() > 0) {
                        try {
                            Log.d("asdf2", jsonObject.toString());
                            result = new PostJSON().execute("http://caerang2.esllee.com/user/sign_up/authorizing", jsonObject.toString()).get();
                            Log.d("asdf3", result);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.d("asdf411", e.toString());
                            e.printStackTrace();
                        }
                    }
                    try {
                        JSONObject json_data = new JSONObject(result);
                        Log.d("asdf5", "receive json: " + json_data.toString());
                        result_code = (json_data.optString("result_code"));
                        Log.d("asdf6", "result_code: " + result_code);
                    } catch (Exception e) {
                        Log.e("Fail 3", e.toString());
                    }
                    if (result_code.equals("1")) {
                        Toast.makeText(sign_up_check.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                        intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent1);
                    }
                    else {
                        Toast.makeText(sign_up_check.this, "입력한 인증번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

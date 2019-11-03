package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity{

    EditText email, password, password_check, name, phone;
    String email_str, pw_str, pw_check_str, name_str, phone_str;
    String result = "";
    TextView tv1;
    Button sign;
    String result_code;
    Intent intent1, intent2;

    boolean b1;
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$"); // 8자리 ~ 16자리까지 가능

    public String getEmail () { return email_str; }

    public void setEmail (String email_str) { this.email_str = email_str; }

    public String getName ()
    {
        return name_str;
    }

    public void setName (String name_str)
    {
        this.name_str = name_str;
    }

    public String getPhoneNumber ()
    {
        return phone_str;
    }

    public void setPhoneNumber (String phone_str)
    {
        this.phone_str = phone_str;
    }

    public String getPassword ()
    {
        return pw_str;
    }

    public void setPassword (String pw_str)
    {
        this.pw_str = pw_str;
    }

    public String getCheckPassword ()
    {
        return pw_check_str;
    }

    public void setCheckPassword (String pw_check_str)
    {
        this.pw_check_str = pw_check_str;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_pw);
        password_check = findViewById(R.id.et_pw_check);
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        tv1 = findViewById(R.id.tv_pwInfo);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pw_str = password.getText().toString();
                b1 = validatePassword(pw_str);

                if(b1 == true) {
                    tv1.setText("* 사용 가능한 비밀번호 입니다.");
                }
                else {
                    tv1.setText("* 비밀번호는 반드시 소문자 1개, 숫자 1개,\\n   특수문자 1개, 8-16 의 길이여야 합니다.");
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pw_str = password.getText().toString();
                b1 = validatePassword(pw_str);

                if(b1 == true) { // 일치하는 경우
                    tv1.setTextColor(Color.BLACK);
                    tv1.setText("* 사용 가능한 비밀번호 입니다.");
                }
                else { // 일치하지 않는 경우
                    tv1.setText("* 비밀번호는 반드시 소문자 1개, 숫자 1개,\\n   특수문자 1개, 8-16 의 길이여야 합니다.");
                    tv1.setTextColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { // 같이 입력된 후에 확인
            }
        });

        sign = findViewById(R.id.btn_sign); // Sign 버튼을 누른 경우
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트에 값을 설정
                setEmail(email.getText().toString().trim());
                setPassword(password.getText().toString().trim());
                setCheckPassword(password_check.getText().toString().trim());
                setName(name.getText().toString().trim());
                setPhoneNumber(phone.getText().toString().trim());

                if(getEmail().isEmpty()  || getPassword().isEmpty()  || getCheckPassword().isEmpty()  || getName().isEmpty()  || getPhoneNumber().isEmpty()) {
                    if(getEmail().isEmpty()) {
                        Toast.makeText(SignUp.this, "이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }else if(getPassword().isEmpty()  ){
                        Toast.makeText(SignUp.this, "비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }else if(getCheckPassword().isEmpty() ){
                        Toast.makeText(SignUp.this, "입력하신 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else if(getName().isEmpty()){
                        Toast.makeText(SignUp.this, "이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SignUp.this, "핸드폰 번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //Check Password
                    if(pw_str.equals(pw_check_str)){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", getEmail());
                            jsonObject.put("password", getPassword());
                            jsonObject.put("phone", getPhoneNumber());
                            jsonObject.put("name", getName());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (email.length() > 0) {
                            try {
                        result = new PostJSON().execute("http://caerang2.esllee.com/user/sign_up/process", jsonObject.toString()).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            JSONObject json_data = new JSONObject(result);
                            result_code = (json_data.optString("result_code"));
                        }
                        catch (Exception e) {
                            Log.e("Fail 3", e.toString());
                        }
                        if(result_code.equals("1")) {
                            Toast.makeText(SignUp.this, "입력한 이메일로 인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show();
                            intent1 = new Intent(SignUp.this, sign_up_check.class);
                            intent1.putExtra("email", email.getText().toString().trim());
                            startActivity(intent1);
                        }
                        else {
                            Toast.makeText(SignUp.this, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(SignUp.this, "입력한 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public static boolean validatePassword(String pwStr) { // 값을 비교해주는 함수
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }
}

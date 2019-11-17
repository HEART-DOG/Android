package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class dogList extends AppCompatActivity {

    private ListView m_oListView = null;
    String result = "";
    String dog_name, dsn, result_code;
    int count = 1;
    ArrayList<ItemData> oData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_list);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usn", Values.USN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Values.USN.length() > 0) {
            try {
                result = new PostJSON().execute("http://caerang2.esllee.com/dog/select/process", jsonObject.toString()).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            oData = new ArrayList<>();
            JSONArray arr = new JSONObject(result).getJSONArray("message");

            if (!result.equals(null)) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json_data = arr.getJSONObject(i);

                    dog_name = json_data.getString("dog_name");
                    dsn = json_data.getString("dsn");

                    ItemData oItem = new ItemData(count, dsn, dog_name);

                    oItem.setCount(count);
                    oItem.setDsn(dsn);
                    oItem.setDogName(dog_name);
                    oData.add(oItem);

                    Log.d("List test", count + " " + dsn + " " + dog_name);

                    count ++;
                }
            } else {
                Toast.makeText(dogList.this, "등록된 강아지가 없어요.", Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }

        m_oListView = findViewById(R.id.Listview);
        final ListAdapter oAdapter = new ListAdapter(oData);
        m_oListView.setAdapter(oAdapter);

        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                TextView title = view.findViewById(R.id.textTitle);
                Values.DEVICE_LIST = title.getText().toString().trim();

                TextView number = view.findViewById(R.id.tv_dsn);
                Values.DSN_list = number.getText().toString().trim();
                Intent chart = new Intent(getApplicationContext(), HeartHistory.class);
                startActivity(chart);
            }
        });
        m_oListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                TextView myDSN = findViewById(R.id.tv_dsn);
                Values.DSN = myDSN.getText().toString();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() { // dialog 창을 띄움
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Dialog 에서 yes 버튼을 누른 경우
                                try {
                                    jsonObject.put("dsn", Values.DSN);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (dsn.length() > 0) {
                                    try {
                                        result = new PostJSON().execute("http://caerang2.esllee.com/dog/delete/process", jsonObject.toString()).get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        JSONObject json_data = new JSONObject(result);
                                        result_code = (json_data.optString("result_code"));
                                    } catch (Exception e) {
                                        Log.e("Fail 3", e.toString());
                                    }
                                    if (result_code.equals("1")) {
                                        Toast.makeText(dogList.this, "삭제 완료!", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);

                                    } else if (result_code.equals("1")) {
                                        Toast.makeText(dogList.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // dialog 창에서 no 버튼을 누른 경우
                                Toast.makeText(dogList.this, "취소", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(dogList.this);
                builder.setMessage("정말 삭제하시겠어요 ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });
    }
}


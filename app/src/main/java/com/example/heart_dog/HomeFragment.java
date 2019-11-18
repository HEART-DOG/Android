package com.example.heart_dog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_CANCELED;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    // ------------------- 맵 --------------------
    private float mMapZoomLevel = 18;
    GpsInfo gpsInfo;
    static GoogleMap mMap;
    static View view; //프래그먼트의 뷰 인스턴스
    Context context;
    double temp_lat = 0.0;
    double temp_lon = 0.0;
    String LAT;
    String LNG;

    // ------------------- GPS --------------------
    public HomeFragment(Context context) {
        // Required empty public constructor
        this.context = context;
        this.context = context;
        this.gpsInfo = new GpsInfo(context);
        this.gpsInfo.getLocation();
        if (!this.gpsInfo.isGetLocation()) {
            this.gpsInfo.showSettingsAlert();     // GPS setting Alert
        }
    }

    // ------------------- 제이슨, 기타 --------------------
    ImageButton ibBluetooth;
    TextView heart, status;
    String result;
    JSONObject json;
    ImageView heart_icon;
    boolean walk = false;
    Switch switchButton;
    String result_code;


    // ------------------- 강아지 선택 --------------------
    Button dog_select;
    String dog_list[];
    String dsn[];
    String empty;

    // ------------------- 블루투스 --------------------
    private static final int RESULT_OK = 0;
    final static int BLUETOOTH_REQUEST_CODE = 100;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // -------------------- 날씨 -----------------------
    private String icon = "01d";
    private String temp = "";
    private String description = "";
    TextView temp_z, weatherinfo;
    Button renew;
    ImageView weathericon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ibBluetooth = getActivity().findViewById(R.id.ib_bluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
        }
        ibBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty.equals("강아지 선택")) {
                    Toast.makeText(getActivity(), "강아지를 선택해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "연결되면 창이 꺼집니다.", Toast.LENGTH_LONG).show();
                    listPairedDevices();

                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home_fragment, container, false);

        temp_z = v.findViewById(R.id.tv_temp);
        weatherinfo = v.findViewById(R.id.tv_weatherinfo);
        renew = v.findViewById(R.id.btn_renew);
        weathericon = v.findViewById(R.id.iv_weathericon);

        getWeatherData();

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherData();
                Toast.makeText(getActivity(), "날씨가 새로고침 되었습니다 !", Toast.LENGTH_LONG).show();
            }
        });

        heart_icon = v.findViewById(R.id.iv_heart);

        try {
            view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        } catch (InflateException e) {
        }// Inflate the layout for this fragment

        final MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);


        status = v.findViewById(R.id.tv_status);
        heart = v.findViewById(R.id.tv_heart);

        Log.d("walk", String.valueOf(walk));
        mBluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (walk == true) {
                    if (msg.what == BT_MESSAGE_READ) {
                        String readMessage = null;
                        try {
                            readMessage = new String((byte[]) msg.obj, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String[] value = readMessage.split(",");
                        Log.d("readMessage", value[0]);
                        heart.setText(value[0]);
                    }
                }
                else if(walk == false){
                    if (msg.what == BT_MESSAGE_READ) {
                        String readMessage = null;
                        try {
                            readMessage = new String((byte[]) msg.obj, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String[] value = readMessage.split(",");
                        Log.d("readMessage", value[0]);
                        heart.setText(value[0]);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("usn", Values.USN);
                            jsonObject.put("dsn", Values.DSN);
                            jsonObject.put("heart_rate", value[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            result = new PostJSON().execute("http://caerang2.esllee.com/dog/data_store/heart_rate/process", jsonObject.toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        dog_select = v.findViewById(R.id.btn_dog_select);
        empty = dog_select.getText().toString();
        dog_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("usn", Values.USN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    result = new PostJSON().execute("http://caerang2.esllee.com/dog/select/process", jsonObject.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray arr = new JSONObject(result).getJSONArray("message");

                    dog_list = new String[arr.length()];
                    dsn = new String[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject JSON = arr.getJSONObject(i);
                        dsn[i] = JSON.getString("dsn");
                        dog_list[i] = JSON.getString("dog_name");
                    }
                } catch (Exception e) {
                    Log.d("Fail 3", e.toString());
                }
                if (dog_list == null || dog_list.length == 0) {
                    Toast.makeText(getActivity(), "강아지를 먼저 등록해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("강아지 선택")
                            .setItems(dog_list, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    Values.DSN = dsn[item];
                                    Log.d("dsn", dsn[item]);
                                    dog_select.setText(dog_list[item]);
                                    empty = dog_select.getText().toString();
                                    Log.d("empty test", empty);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        Button refresh = v.findViewById(R.id.btn_map_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Values.DSN.equals("")) {
                    Toast.makeText(getActivity(), "위치를 확인할 강아지를 선택해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("dsn", Values.DSN);
                        Log.d("DSN TEST", Values.DSN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        result = new PostJSON().execute("http://caerang2.esllee.com/dog/select/gps/process", jsonObject.toString()).get();
                        Log.d("map from server", result);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject json_data = new JSONObject(result);
                        result_code = (json_data.optString("result_code"));
                        LAT = (json_data.optString("latitude"));
                        LNG = (json_data.optString("longitude"));

                        Values.LAT = Double.parseDouble(LAT);
                        Values.LNG = Double.parseDouble(LNG);

                        Log.d("location test", Values.LAT + " " + Values.LNG);
                    } catch (Exception e) {
                        Log.e("Fail 3", e.toString());
                    }
                    onMapReady(mMap);
                }
            }
        });

        switchButton = v.findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("산책 모드").setMessage("산책 모드를 활성화 하면 \n심장 데이터가 저장되지 않아요!");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            walk = true;
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            walk = false;
                            switchButton.setChecked(false);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    walk = false;
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("장치 선택")
                        .setPositiveButton("연결하기 전에 미리 페어링 해주세요.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                        dog_select.setEnabled(false);

                        heart_icon.setBackgroundColor(Color.TRANSPARENT);
                        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(heart_icon);
                        Glide.with(getActivity()).load(R.drawable.heart_icon).into(gifImage);

                        if (mThreadConnectedBluetooth != null) {
                            mThreadConnectedBluetooth.write(Values.DSN);
                            Log.d("DSN check", Values.DSN);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getActivity(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for (BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            //mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket = createBluetoothSocket(mBluetoothDevice);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();

            status.setText("connected to " + mBluetoothDevice.getName());
            Values.MAC = mBluetoothDevice.getAddress();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            status.setText("connected failed");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions;
        LatLng changeLocation;

        if(Values.DSN.equals("")) {
            changeLocation = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
            markerOptions = new MarkerOptions()
                    .position(changeLocation)
                    .title("Dog Location");
            mMap.clear();
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoomLevel));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(changeLocation, mMapZoomLevel));
        }
        else {
            changeLocation = new LatLng(Values.LAT, Values.LNG);
            markerOptions = new MarkerOptions()
                    .position(changeLocation)
                    .title("Dog Location");
            mMap.clear();
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoomLevel));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(changeLocation, mMapZoomLevel));
        }
//        if(temp_lat == Values.LAT || temp_lon == Values.LNG){
//            LatLng location = new LatLng(temp_lat, temp_lon);
//
//            mMap.clear();
//            markerOptions = new MarkerOptions()
//                    .position(location)
//                    .title("Your device");
//            mMap.addMarker(markerOptions);
//        }else {
//            if (gpsInfo.getLatitude() != temp_lat || gpsInfo.getLongitude() != temp_lon) {
//                changeLocation = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
//                temp_lat = gpsInfo.getLatitude();
//                temp_lon = gpsInfo.getLongitude();
//            } else {
//                changeLocation = new LatLng(Values.LAT, Values.LNG);
//                temp_lat = Values.LAT;
//                temp_lon = Values.LNG;
//            }
        //}
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getWeatherData() {
        String lat = Double.toString(gpsInfo.getLatitude());
        String lot = Double.toString(gpsInfo.getLongitude());
        Log.e("lat", lat);
        Log.e("lot", lot);

        Retrofit client = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface service = client.create(ApiInterface.class);
        Call<Repo> call = service.repo("metric", "cf4c41ffa55d4ef37c9d73efede7fa12", Double.valueOf(lat), Double.valueOf(lot));
        call.enqueue(new Callback<Repo>() {
            @Override
            public void onResponse(Response<Repo> response) {
                if (response.isSuccess()) {
                    Repo repo = response.body();

                    temp = String.valueOf(repo.getMain().getTemp());
                    Log.e("test1", temp);

                    description = repo.getWeather().get(0).getDescription();
                    description =   changeWeatherData(description);

                    Log.e("test2", description);

                    icon = setWeatherIcon(description);
                    Log.e("test3", icon);

                    Context context = getActivity();
                    String packName = context.getPackageName(); // 패키지명
                    int resID = getResources().getIdentifier(icon, "drawable", packName);

                    weathericon.setImageResource(resID);
                    temp_z.setText(temp + "°C");
                    weatherinfo.setText(description);
                } else {
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public String changeWeatherData(String str){
        if(str.equals("clear sky"))
            return  "맑음";
        if(str.equals("few clouds"))
            return  "구름 적음";
        if(str.equals("overcast clouds"))
            return  "구름 많음";
        if(str.equals("scattered clouds"))
            return  "구름";
        if(str.equals("broken clouds"))
            return  "구름";
        if(str.equals("shower rain"))
            return  "소나기";
        if(str.equals("light rain"))
            return "비";
        if(str.equals("rain"))
            return  "비";
        if(str.equals("moderate rain"))
            return "비";
        if(str.equals("thunderstorm"))
            return  "뇌우";
        if(str.equals("snow"))
            return  "눈";
        if(str.equals("mist"))
            return  "안개";
        if(str.equals("haze"))
            return  "안개";
        return str;
    }

    public String setWeatherIcon(String str){
        if(str.equals("맑음"))
            return  "icon01";
        if(str.equals("구름 적음"))
            return  "icon02";
        if(str.equals("구름"))
            return  "icon03";
        if(str.equals("구름 많음"))
            return  "icon04";
        if(str.equals("소나기"))
            return  "icon09";
        if(str.equals("비"))
            return  "icon10";
        if(str.equals("뇌우"))
            return  "icon11";
        if(str.equals("눈"))
            return  "icon13";
        if(str.equals("안개"))
            return  "icon50";
        return str;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, BT_UUID);
            }
            catch (Exception e) {
            }
        }
        return device.createRfcommSocketToServiceRecord(BT_UUID); }
}

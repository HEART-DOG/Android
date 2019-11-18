package com.example.heart_dog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class HeartHistory extends AppCompatActivity implements OnChartGestureListener,
        OnChartValueSelectedListener {

    private LineChart mChart;
    TextView start, end, avgHeart;
    DatePickerDialog.OnDateSetListener mDateSetListener1;
    DatePickerDialog.OnDateSetListener mDateSetListener2;
    String DATA[];
    String TS[];
    float f[];
    Button view;
    String result, Start, End;
    String result_code;
    int sum, index;

    ArrayList<String> ts = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_history);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = findViewById(R.id.linechart1);
        mChart.setDrawGridBackground(false);
        avgHeart = findViewById(R.id.tv_avgHeart);

        start = findViewById(R.id.tv_startDate);
        Start = start.getText().toString().trim();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        HeartHistory.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener1, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        end = findViewById(R.id.tv_endDate);
        End = end.getText().toString().trim();
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start = start.getText().toString().trim();
                End = end.getText().toString().trim();

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(HeartHistory.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener2, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                start.setText(date);
            }
        };

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                end.setText(date);
            }
        };
        view = findViewById(R.id.btn_view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start = start.getText().toString().trim();
                End = end.getText().toString().trim();

                if (Start.equals("Start Date") || End.equals("End Date")) {
                    Toast.makeText(HeartHistory.this, "Please select date", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("usn", Values.USN);
                        jsonObject.put("dsn", Values.DSN_list);
                        jsonObject.put("start_date", Start + " 00:00:00");
                        jsonObject.put("end_date", End + " 23:59:59");
                        Log.d("json test", String.valueOf(jsonObject));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Values.USN.length() > 0) {
                        try {
                            result = new PostJSON().execute("http://caerang2.esllee.com/dog/select/heart_rate_history/process", jsonObject.toString()).get();
                            Log.d("result debug", result);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        JSONObject json_error = new JSONObject(result);
                        result_code = json_error.getString("result_code");

                        Log.d("result_code", result_code);

                        if(result_code.equals("3")) {
                            Toast.makeText(HeartHistory.this, "저장된 데이터가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            try {
                                JSONArray arr = new JSONObject(result).getJSONArray("message");

                                DATA = new String[arr.length()];
                                TS = new String[arr.length()];

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject json = arr.getJSONObject(i);
                                    DATA[i] = json.getString("heart_rate");
                                    TS[i] = json.getString("datetime");

                                    sum += Integer.parseInt(DATA[i]);

                                    ts.add(TS[i]);
                                }

                                f = new float[DATA.length];

                                index = DATA.length;
                                sum = sum / index;
                                avgHeart.setText(Integer.toString(sum));

                                for (int i = 0; i < DATA.length; i++) {
                                    f[i] = Float.parseFloat(DATA[i]);
                                }

                                for (int i = 0; i < DATA.length; i++) {
                                    entries.add(new Entry(f[i], i));
                                }

                                setData();

                                Legend l = mChart.getLegend();

                                // modify the legend ...
                                // l.setPosition(LegendPosition.LEFT_OF_CHART);
                                l.setForm(Legend.LegendForm.LINE);

                                // no description text
                                mChart.setDescription("Heart data chart");
                                mChart.setNoDataTextDescription("You need to provide data for the chart.");

                                // enable touch gestures
                                mChart.setTouchEnabled(true);

                                // enable scaling and dragging
                                mChart.setDragEnabled(true);
                                mChart.setScaleEnabled(true);
                                // mChart.setScaleXEnabled(true);
                                // mChart.setScaleYEnabled(true);

                                LimitLine upper_limit = new LimitLine(130f, "Upper Limit");
                                upper_limit.setLineWidth(4f);
                                upper_limit.enableDashedLine(10f, 10f, 0f);
                                upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                                upper_limit.setTextSize(10f);

                                LimitLine lower_limit = new LimitLine(-30f, "Lower Limit");
                                lower_limit.setLineWidth(4f);
                                lower_limit.enableDashedLine(10f, 10f, 0f);
                                lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                                lower_limit.setTextSize(10f);

                                YAxis yLAxis = mChart.getAxisLeft();
                                yLAxis.setTextColor(Color.BLACK);

                                YAxis yRAxis = mChart.getAxisRight();
                                yRAxis.setDrawLabels(false);
                                yRAxis.setDrawAxisLine(false);
                                yRAxis.setDrawGridLines(false);

                                // limit lines are drawn behind data (and not on top)
                                mChart.getAxisRight().setEnabled(false);

                                //mChart.getViewPortHandler().setMaximumScaleY(2f);
                                //mChart.getViewPortHandler().setMaximumScaleX(2f);

                                mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

                                //  dont forget to refresh the drawing
                                mChart.invalidate();

                                if(50 < sum && sum < 90) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HeartHistory.this)
                                            .setTitle("검진 요망")
                                            .setMessage("갑상선 기능 저하증, 전해질 불균형 등의\n질병이 의심됩니다. 병원을 방문하세요!")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                else if(200 < sum && sum < 300) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HeartHistory.this)
                                            .setTitle("검진 요망")
                                            .setMessage("고혈압, 몸안 미세출혈\n질병이 의심됩니다. 병원을 방문하세요!")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            } catch (Exception e) {
                                Log.e("Fail 1", e.toString());
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.e("Fail 2", e.toString());
                    }
                }
            }
        });
    }

    private ArrayList<String> setXAxisValues(String s[]) {
        ArrayList<String> xVals = new ArrayList<String>(Arrays.asList(s));
        return xVals;
    }

    private ArrayList<Entry> setYAxisValues(float f[]) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < DATA.length; i++) {
            entries.add(new Entry(f[i], i));
        }

        return entries;
    }

    private void setData() {
        ArrayList<String> xVals = setXAxisValues(TS);

        ArrayList<Entry> yVals = setYAxisValues(f);

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, " ");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) { }
    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) { }
    @Override
    public void onChartLongPressed(MotionEvent me) { }
    @Override
    public void onChartDoubleTapped(MotionEvent me) { }
    @Override
    public void onChartSingleTapped(MotionEvent me) { }
    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) { }
    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) { }
    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) { }
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) { }
    @Override
    public void onNothingSelected() { }
}

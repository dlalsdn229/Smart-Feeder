package com.example.petv20;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MakeroutineActivity extends Activity {

    private static String TAG = "insertphp";
    private static String IP = "13.124.96.113";
    TextView stateview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_makeroutine);

        final Button confirmbtn = (Button) findViewById(R.id.confirm);
        final EditText rnametext = (EditText) findViewById(R.id.rname);
        final TimePicker timepicker = (TimePicker) findViewById(R.id.timePicker);
        stateview = (TextView) findViewById(R.id.statemsg);

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                String rname = rnametext.getText().toString();
                String hour = Integer.toString(timepicker.getHour());
                String min = Integer.toString(timepicker.getMinute());


                Insert_Data task = new Insert_Data();
                task.execute("http://" + IP + "/routine_insert.php",rname,hour,min);
                //에러가 없으면
                //finish();
            }
        });

    }

    class Insert_Data extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MakeroutineActivity.this,
                    "잠시만 기다려주세요.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
            stateview.setText(result);
        }


        @Override
        protected String doInBackground(String... params) {
            //System.out.println(params[0].toString());
            //System.out.println(params[1].toString());
            //System.out.println(params[2].toString());

            String rname = (String) params[1];

            String hour = (String) params[2];
            String min = (String) params[3];


            String serverURL = (String) params[0];
            String postParameters = "rname=" + rname + "&hour=" + hour + "&min=" + min;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);


                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    }
}

package com.example.petv20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String json_impo;
    String  mJsonString;
    private static String IP = "13.124.96.113";
    private static String TAG = "insertphp";

    int select = 1; //0은 routine_read 1은 sensor_read

    private ListView routinelist;
    private ArrayList<String> items;
    private ArrayAdapter adapter;


    private TextView one;
    private TextView two;
    private TextView thr;
    private TextView four;
    private TextView stateview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button deletebtn = (Button) findViewById(R.id.deletebtn);
        final Button manualbtn = (Button) findViewById(R.id.manualbtn);
        final Button refbtn = (Button) findViewById(R.id.refbtn);

        one = (TextView) findViewById(R.id.one);
        two = (TextView) findViewById(R.id.two);
        thr = (TextView) findViewById(R.id.three);
        four = (TextView) findViewById(R.id.four);
        stateview = (TextView) findViewById(R.id.stateview);

        routinelist = (ListView) findViewById(R.id.routinelist);
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        routinelist.setAdapter(adapter);


        GetData task = new GetData();
        select=1;
        task.execute("http://" + IP + "/routine_read.php");

        //사료상태 갱신
        refbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GetData task = new GetData();
                select=2;
                task.execute("http://" + IP + "/sensor_read.php");

            }
        });

        //일정 갱신
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                items.clear();
                adapter.notifyDataSetChanged();

                GetData task = new GetData();
                select=1;
                task.execute("http://" + IP + "/routine_read.php");

            }
        });

        manualbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manu = "1";

                Insert_Data task = new Insert_Data();
                task.execute("http://" + IP + "/routine_insert.php",manu);
            }
        });
    }

    public void add_popup(View v){
        Intent intent = new Intent(this, MakeroutineActivity.class);
        intent.putExtra("data", "Test Popup");
        startActivityForResult(intent, 1);
    }

    class Insert_Data extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "잠시만 기다려주세요.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
            stateview.setText("잠시만 기다려 주세요");
        }


        @Override
        protected String doInBackground(String... params) {
            //System.out.println(params[0].toString());
            //System.out.println(params[1].toString());
            //System.out.println(params[2].toString());

            String manu = (String) params[1];
            //String hour = (String) params[2];
            //String min = (String) params[3];


            String serverURL = (String) params[0];
            String postParameters = "manu=" + manu;// + "&hour=" + hour + "&min=" + min;


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

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            System.out.println(result);

            json_impo = result;

            System.out.println("json_impo loaded");

            if (result == null){

            }
            else {
                mJsonString = result;
                if(select==1){
                    showResult();
                }
                else if(select==2) {
                    showResult2();
                }
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);

                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();


                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();
                httpURLConnection.disconnect();
                return sb.toString().trim();


            } catch (Exception e) {

                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray ee = jsonObject.getJSONArray("result");


            for (int i=0;i<ee.length();i++) {
                JSONObject op = ee.getJSONObject(i);
                String rname = op.getString("rname");
                String date = op.getString("date");
                System.out.println(rname);
                System.out.println(date);

                items.add(rname+ "   "+ date);
                adapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private void showResult2(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray ee = jsonObject.getJSONArray("result");

            JSONObject op = ee.getJSONObject(1);
            String w = op.getString("weight");

            double weight = Double.parseDouble(w);

            if (weight < 300) {
                one.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                thr.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);
            }
            if (weight >= 300) {
                one.setBackgroundColor(Color.GREEN);
                two.setBackgroundColor(Color.WHITE);
                thr.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);
            }
            if (weight > 600) {
                one.setBackgroundColor(Color.GREEN);
                two.setBackgroundColor(Color.GREEN);
                thr.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);
            }
            if (weight > 900) {
                one.setBackgroundColor(Color.GREEN);
                two.setBackgroundColor(Color.GREEN);
                thr.setBackgroundColor(Color.GREEN);
                four.setBackgroundColor(Color.WHITE);
            }
            if (weight > 1200) {
                one.setBackgroundColor(Color.GREEN);
                two.setBackgroundColor(Color.GREEN);
                thr.setBackgroundColor(Color.GREEN);
                four.setBackgroundColor(Color.GREEN);
            }



        } catch (JSONException e) {

            e.printStackTrace();
        }

    }




}

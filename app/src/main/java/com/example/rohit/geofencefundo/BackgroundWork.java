package com.example.rohit.geofencefundo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWork extends Worker {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_OUTPUT_MESSAGE = "output_message";
    JSONObject jsonPart;
    String result;
    private Context mContext;
    public BackgroundWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        //do work and shit
        Log.d("work901","work and shit");
        String title = getInputData().getString(EXTRA_TITLE);
        String text = getInputData().getString(EXTRA_TEXT);
        Log.d("work901tit","title from main is "+title);
        Data output = new Data.Builder()
                .putString(EXTRA_OUTPUT_MESSAGE, "I have come from MyWorker!")
                .build();



        sendNotification(title,text);
        new JsonTask().execute("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-10-20&endtime=2018-10-21&latitude=33.9370804&longitude=135.8284085&maxradiuskm=5000");





        setOutputData(output);
        return Result.SUCCESS;

    }

    private void sendNotification(String title, String text) {



        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "rohit901";

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"My Notification",NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setDescription("rohit901 channel for app test FCM");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.support.compat.R.drawable.notification_icon_background)
                .setTicker("Hearty365")
                .setContentTitle(title)
                .setContentText(text)
                .setContentInfo("info");

        Random random = new Random();
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        m += random.nextInt(100) + 1;

        notificationManager.notify(m,notificationBuilder.build());



    }
    public class JsonTask extends AsyncTask<String,Void,String> {







        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS

            //pd.show();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            //Toast.makeText(FeedBackActivity.this, "DOne", Toast.LENGTH_SHORT).show();
            Log.d("rohit901",s);
            result = s;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String features = jsonObject.getString("features"); //array of features
                //JSONObject jsonObject2 = new JSONObject(features);
                //String geometry = jsonObject2.getString("geometry"); //array of geometry
                //JSONObject jsonObject3 = new JSONObject(geometry);
                //String coordinates = jsonObject3.getString("coordinates");
                JSONArray arr = new JSONArray(features);
                JSONArray locArr;

                Log.d("work901Array",arr.toString());


                for(int i =0;i<arr.length();i++) {





                    jsonPart = arr.getJSONObject(i);
                    //jsonPart2 =
                    locArr = new JSONArray(jsonPart.getJSONObject("geometry").getString("coordinates"));
                    Log.d("geo901","Longitude: "+locArr.getString(0)+", Latitude: "+locArr.getString(1));

                }





            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        @Override
        protected String doInBackground(String... urls) {



            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;


        }
    }
}

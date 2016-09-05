package com.example.username.time;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.1.2.42:8080/";
    private Retrofit retrofit;
    private String USER_ID = "16002";
    private MyApiEndpointInterface apiService;
    private ListView list;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> values;
    private CalendarView cal;
    private ArrayList<String[]> records;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeCalendar();
        loadAPIService();

        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        Date date = new Date();
        getData(20+dateFormat.format(date));




    }

    void getData(final String date){

        Toast.makeText(getApplicationContext(),date, Toast.LENGTH_SHORT).show();
        Call<User[]> call;
        call = apiService.getRecord(USER_ID);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Call<User[]> call, Response<User[]> response) {
            //Log.e("TAG",response.body()[0].checktime);
                values.clear();

                for( User u : response.body() ) {

                    if( u.checktime.contains(date) ) {

                        values.add(u.checktime);
                    }
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<User[]> call, Throwable t) {
                // Log error here since request failed
                String message = t.getMessage();
                Log.e("failure", message);
            }
        });
    }

    void initializeCalendar() {
        cal = (CalendarView)findViewById(R.id.calendarView);
        cal.setShowWeekNumber(false);
        cal.setFirstDayOfWeek(1);

        list = (ListView)findViewById(R.id.list);

        values = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);



        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {


                String newMonth = "";
                String newDay = "";

                if( month < 10 ) {
                    newMonth = "0"+(month+1);
                }
                else {
                    newMonth = month+"";
                }

                if( day < 10 ) {
                    newDay = "0"+day;
                }
                else {
                    newDay = day+"";
                }

                String datetext = (year+"-"+newMonth+"-"+newDay);


                getData(datetext);


            }
        });
    }

    void loadAPIService(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(MyApiEndpointInterface.class);
    }

}

interface MyApiEndpointInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("/attlogs/{username}")
    Call<User[]> getRecord(@Path("username") String username);

}

class User {

    @SerializedName("Userid")
    String userid;

    @SerializedName("Checktime")
    String checktime;

    public User(String userid, String checktime ) {
        this.userid = userid;
        this.checktime = checktime;
    }
}


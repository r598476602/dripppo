package com.works.ci.dripppo;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.works.ci.dripppo.API.apiService;
import com.works.ci.dripppo.Item.Shot;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private MyAdapter myAdapter;
    private ArrayList<Shot> shotArrayList;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= (ListView)findViewById(R.id.listView);
        shotArrayList = new ArrayList<Shot>();
        myAdapter = new MyAdapter(this, shotArrayList);
        listView.setAdapter(myAdapter);

        handler = new Handler() {


            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Gson gson = new Gson();
                Type mapType = new TypeToken< ArrayList<Shot> >() {}.getType();
                shotArrayList = gson.fromJson((String) msg.obj, mapType);
                myAdapter.refresh(shotArrayList);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiService apitest = new apiService();
                    String response = apitest.get("https://api.dribbble.com/v1/shots/?access_token=82b6a13113f4406633b6b7f44972a7a74fa4578fd3f08e4f92a80f65690fd004");
                    Message message = new Message();
                    message.obj = response;

                    Thread.sleep(3000);
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.notifyDataSetChanged();
                Shot temp = (Shot)myAdapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, ShotInformation.class);
                intent.putExtra("url", temp.getHtml_url());
                startActivity(intent);
            }
        });
    }

}

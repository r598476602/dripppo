package com.works.ci.dripppo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.works.ci.dripppo.API.apiService;
import com.works.ci.dripppo.Item.Shot;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView= (ListView)findViewById(R.id.listView);

        apiService apitest = new apiService();
        String response = apitest.get("https://api.dribbble.com/v1/shots/?access_token=82b6a13113f4406633b6b7f44972a7a74fa4578fd3f08e4f92a80f65690fd004");

        Gson gson = new Gson();
        Type mapType = new TypeToken< ArrayList<Shot> >() {}.getType();
        ArrayList<Shot> listItem = gson.fromJson( response, mapType );

        final MyAdapter adapter = new MyAdapter(this, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shot temp = (Shot)adapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, ShotInformation.class);
                intent.putExtra("url", temp.getHtml_url());
                startActivity(intent);
            }
        });
    }

}

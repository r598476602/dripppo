package com.works.ci.dripppo;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    private boolean isloading = false;
    private View footerView;
    private int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= (ListView)findViewById(R.id.listView);
        shotArrayList = new ArrayList<Shot>();
        myAdapter = new MyAdapter(this, shotArrayList);
        listView.setAdapter(myAdapter);

        //LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //ftView = li.inflate(R.layout.footer_view, null);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = layoutInflater.inflate(R.layout.shot_footer, null);

        handler = new Handler() {


            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        //Toast.makeText(getApplicationContext(), "footer", Toast.LENGTH_SHORT).show();
                        listView.addFooterView(footerView);
                        break;
                    case 1:
                        Gson gson = new Gson();
                        Type mapType = new TypeToken< ArrayList<Shot> >() {}.getType();
                        shotArrayList = gson.fromJson((String) msg.obj, mapType);
                        myAdapter.refresh(shotArrayList);
                        isloading = false;
                        //Toast.makeText(getApplicationContext(), "loading", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        };

        Thread thread = new ThreadGetData();
        thread.start();

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(view.getLastVisiblePosition() == totalItemCount-1 && myAdapter.getCount() >=10 && isloading == false ){
                    isloading = true;
                    Thread thread = new ThreadGetData();
                    thread.start();
                }

            }
        });
    }

    public class ThreadGetData extends Thread{
        @Override
        public void run() {
            try {
                handler.sendEmptyMessage(0);

                apiService apitest = new apiService();

                String response = apitest.get("https://api.dribbble.com/v1/shots/?page="+page+"&"+getString(R.string.accessToken));

                Thread.sleep(3000);
                page++;
                Message message = handler.obtainMessage(1, response);
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}

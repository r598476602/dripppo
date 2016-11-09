package com.works.ci.dripppo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private MyAdapter myAdapter;
    private ArrayList<Shot> shotArrayList;
    private Handler handler;
    private boolean isloading = false;
    private View footerView;
    private int page = 1;
    private WaveSwipeRefreshLayout waveSwipeRefreshLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        initToolBar();

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });


        listView= (ListView)findViewById(R.id.listView);
        shotArrayList = new ArrayList<Shot>();
        myAdapter = new MyAdapter(this, shotArrayList);
        listView.setAdapter(myAdapter);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = layoutInflater.inflate(R.layout.shot_footer, null);

        waveSwipeRefreshLayout = (WaveSwipeRefreshLayout)findViewById(R.id.main_swipe);
        waveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                Thread thread = new ThreadGetData();
                thread.start();
            }
        });

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

                        if (page==1)
                            myAdapter.reload(shotArrayList);
                        else
                            myAdapter.refresh(shotArrayList);

                        waveSwipeRefreshLayout.setRefreshing(false);
                        page++;
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
                Shot temp = (Shot)listView.getItemAtPosition(position);

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

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }




    public void initToolBar()
    {
        toolbar = (Toolbar)findViewById(R.id.activity_main_toolbar);
        setTitle("Dripppo");
        toolbar.setLogo(R.mipmap.dribbble_ball);
        setSupportActionBar(toolbar);
    }

    public class ThreadGetData extends Thread{
        @Override
        public void run() {
            try {
                handler.sendEmptyMessage(0);

                apiService apitest = new apiService();

                String response = apitest.get("https://api.dribbble.com/v1/shots/?page="+page+"&"+getString(R.string.accessToken));

                Thread.sleep(3000);
                Message message = handler.obtainMessage(1, response);
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}

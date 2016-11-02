package com.works.ci.dripppo;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.works.ci.dripppo.Item.Shot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jason on 2016/10/31.
 */

public class MyAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<Shot> items;
    private final Map<View, Map<Integer, View>> cache = new HashMap<View, Map<Integer, View>>();

    public MyAdapter(Context context, ArrayList<Shot> items) {
        this.context = context;
        this.items = items;
    }

    public void refresh(ArrayList<Shot> mlist)
    {
        items = mlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(items != null)
            return items.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView likes_count;
        TextView views_count;
        TextView comments_count;
        ImageView iv;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.shot_item, parent, false);
        }
        Map<Integer, View> itemMap = cache.get(v);
        if(itemMap == null)
        {
            itemMap = new HashMap<Integer, View>();
            likes_count = (TextView)v.findViewById(R.id.liks_count);
            views_count = (TextView)v.findViewById(R.id.views_count);
            comments_count = (TextView)v.findViewById(R.id.comments_count);
            iv = (ImageView)v.findViewById(R.id.imageView);
            itemMap.put(R.id.liks_count, likes_count);
            itemMap.put(R.id.views_count, views_count);
            itemMap.put(R.id.comments_count, comments_count);
            itemMap.put(R.id.imageView, iv);
            cache.put(v, itemMap);
        }
        else
        {
            likes_count = (TextView)itemMap.get(R.id.liks_count);
            views_count = (TextView)itemMap.get(R.id.views_count);
            comments_count = (TextView)itemMap.get(R.id.comments_count);
            iv = (ImageView)itemMap.get(R.id.imageView);
        }
        Shot temp = (Shot) getItem(position);
        String like, view, comment;
        like = "Likes: "+String.valueOf(temp.getLikes_count());
        view = "Views: "+String.valueOf(temp.getViews_count());
        comment = "comments: "+String.valueOf(temp.getComments_count());
        likes_count.setText(like);
        views_count.setText(view);
        comments_count.setText(comment);
        Glide.with(context).load(temp.getImages().getNormal()).into(iv);


        return v;
    }
}

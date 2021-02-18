package com.example.sirius;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class CapturedImages extends AppCompatActivity implements Serializable {
    //    String app_folder_path=getExternalFilesDir(null).toString()+"/images";
    ListView listView;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    recylerviewAdapter recylerviewAdapter;

    LinearLayoutManager HorizontalLayout;

    View ChildView;
    int RecyclerViewItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_images);

        //old code
        /*listView = findViewById(R.id.listview);


        ArrayList<String> imagesDataArrayList = getIntent().getStringArrayListExtra("Key");
        String temp = Integer.toString(imagesDataArrayList.size());
        Log.e("ArrayListContent",temp);

        ImageAdapter imageAdapter = new ImageAdapter(this,R.layout.mylistlayout,imagesDataArrayList);

        listView.setAdapter(imageAdapter);*/

        ArrayList<String> imagesDataArrayList = getIntent().getStringArrayListExtra("Key");
        String temp = Integer.toString(imagesDataArrayList.size());
        Log.e("ArrayListContent",temp);

        recyclerView = findViewById(R.id.recyclerview);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        recylerviewAdapter = new recylerviewAdapter(this,imagesDataArrayList);

        HorizontalLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(HorizontalLayout);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recylerviewAdapter);

    }
}
package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    FloatingActionButton cameraOpenButton;
    FirebaseAuth firebaseAuth;

    ArrayList<String> createdPdfList;
    ListView pdfListView;
    private ViewPageAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();

        if(i == R.id.power) {
            FirebaseAuth.getInstance().signOut();

            if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                Toast.makeText(Dashboard.this, "Successful signOut", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Dashboard.this, loginActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(Dashboard.this, "SignOut error", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //signout = findViewById(R.id.buttonsignout);
        cameraOpenButton = findViewById(R.id.floatingActionButton);
        //pdfListView = findViewById(R.id.pdflistview);
        viewPager = findViewById(R.id.viewpager);



        firebaseAuth=FirebaseAuth.getInstance();
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new pdfListFragment(), "PDF LIST");
        viewPagerAdapter.add(new txtListFragment(), "TXT LIST");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

       /* getBatchDirectoryName();
        createdPdfList = new ArrayList<>();
        fetchPDF();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,createdPdfList);
        pdfListView.setAdapter(arrayAdapter);
        Intent intentFromCapturedImages = getIntent();
        int refereshBool = intentFromCapturedImages.getIntExtra("Referesh",0);
        Log.e("refereshBool",Integer.toString(refereshBool));
        if(refereshBool == 1) {
            createdPdfList.clear();
            fetchPDF();
            pdfListView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            *//*finish();
            startActivity(getIntent());*//*
            *//*overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);*//*
        }*/
       //pdfListView.deferNotifyDataSetChanged();
        cameraOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,Camera.class);
                startActivity(intent);
            }
        });

        /*pdfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(getBatchDirectoryName(),createdPdfList.get(position));

                *//*Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setDataAndType(uri, "application/pdf");*//*
                Uri pdfContentFile = FileProvider.getUriForFile(Dashboard.this,BuildConfig.APPLICATION_ID+".provider",file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(pdfContentFile,"application/pdf");


                try {
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(Dashboard.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
       /* signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                    Toast.makeText(Dashboard.this, "Successful signOut", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Dashboard.this, loginActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Dashboard.this, "SignOut error", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void fetchPDF() {

        String path = getBatchDirectoryName();

        File directory = new File(path);
        File[] files = directory.listFiles();

        for(int i=0;i<files.length;i++) {

            String fileName = files[i].getName();
            createdPdfList.add(fileName);
        }


    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        // app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        app_folder_path = getExternalFilesDir(null).toString()+"/pdf";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }
}
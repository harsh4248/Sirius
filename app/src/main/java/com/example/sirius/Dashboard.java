package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 1001;

    FloatingActionButton cameraOpenButton;
    FirebaseAuth firebaseAuth;

    ArrayList<String> createdPdfList;
    ListView pdfListView;
    private ViewPageAdapter viewPagerAdapter;
    //private ViewPager viewPager;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    String pdfPath;
    String txtPath;
    ArrayList<putPDF> uriOfPdf;
    ArrayList<putTXT> uriOfTxt;
    ArrayList<putPDF> uploadedPDF;
    ArrayList<String> nameOfUploadedPdf;
    List<putPDF> uploadedListPDF;
    List<putTXT> uploadedListTXT;
    ArrayList<String> nameOfUploadedTXT;

    StorageReference storageReference;
    DatabaseReference databaseReference;

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

        if(i == R.id.sync) {
            syncMethod();
        }
        return(super.onOptionsItemSelected(item));
    }

    private void syncMethod() {


        /*DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PDF Links").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()) {

                    putPDF putpdf = ds.getValue(com.example.sirius.putPDF.class);
                    uploadedPDF.add(putpdf);
                    nameOfUploadedPdf.add(putpdf.pdfName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        for(int i=0;i<uriOfPdf.size();i++) {
            if(!nameOfUploadedPdf.contains(uriOfPdf.get(i).getPdfName())) {
                uploadFileToStorage(uriOfPdf.get(i));
            }
        }

        for(int i=0;i<uriOfTxt.size();i++) {

            if(!nameOfUploadedTXT.contains(uriOfTxt.get(i).getTxtName())) {
                uploadTXTfileToStorage(uriOfTxt.get(i));
            }
        }
        /*databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PDF Link").setValue(new putPDF("EE","ee"))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Dashboard.this, "Added", Toast.LENGTH_SHORT).show();
            }
        });*/

        //databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PDF Link")

    }

    private void uploadTXTfileToStorage(putTXT putTXT) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is Loading...");
        progressDialog.show();

        StorageReference storageReference1 = storageReference.child("uploadtxt"+System.currentTimeMillis()+".txt");
        Log.e("File path",putTXT.getTxtURL());

        storageReference1.putFile(Uri.fromFile(new File(putTXT.getTxtURL())))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete()) {}
                        Uri uri = uriTask.getResult();

                        com.example.sirius.putTXT putTXT1 = new putTXT(putTXT.getTxtName(),uri.toString());
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TXT Links").child(databaseReference.push().getKey()).setValue(putTXT1);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Dashboard.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploading "+ (int) progress +"%");
            }
        });


    }

    private void uploadFileToStorage(putPDF putPDF) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is Loading...");
        progressDialog.show();

        StorageReference storageReference1 = storageReference.child("uploadPDF"+System.currentTimeMillis()+".pdf");
        Log.e("File path",putPDF.pdfUrRL);
        storageReference1.putFile(Uri.fromFile(new File(putPDF.pdfUrRL)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete()) {}
                        Uri uri = uriTask.getResult();

                        com.example.sirius.putPDF PUTPDFOBJ = new putPDF(putPDF.pdfName,uri.toString());
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PDF Links").child(databaseReference.push().getKey()).setValue(PUTPDFOBJ);
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploading "+ (int) progress +"%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Dashboard.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //signout = findViewById(R.id.buttonsignout);
        cameraOpenButton = findViewById(R.id.floatingActionButton);
        //pdfListView = findViewById(R.id.pdflistview);
        viewPager = findViewById(R.id.viewpager);

        if(allPermissionsGranted()){
            //startCamera(); //start camera if permission has been granted by user

        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        viewPager.setPagingEnabled(false);
        firebaseAuth=FirebaseAuth.getInstance();
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new pdfListFragment(), "PDF LIST");
        viewPagerAdapter.add(new txtListFragment(), "TXT LIST");
        viewPagerAdapter.add(new digitalInkFragment(), "Signature");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        uriOfPdf = new ArrayList<>();
        uriOfTxt = new ArrayList<>();
        uploadedPDF = new ArrayList<>();
        nameOfUploadedPdf = new ArrayList<>();
        uploadedListPDF = new ArrayList<>();
        nameOfUploadedTXT = new ArrayList<>();
        uploadedListTXT = new ArrayList<>();


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        pdfPath = getExternalFilesDir(null).toString()+"/pdf";
        txtPath = getExternalFilesDir(null).toString()+"/word";

        getAllPdf();
        getAllTxt();

        Log.e("Example name",uriOfPdf.get(0).pdfName);


        DatabaseReference drf = FirebaseDatabase.getInstance().getReference("Users");
        drf.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PDF Links").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {

                    putPDF putpdf123 = ds.getValue(com.example.sirius.putPDF.class);
                    //uploadedPDF.add(putpdf);
                    uploadedListPDF.add(putpdf123);
                    //nameOfUploadedPdf.add(putpdf.pdfName);
                }
                String[] tempString = new String[uploadedListPDF.size()];

                for(int i=0;i<tempString.length;i++) {
                    tempString[i]=uploadedListPDF.get(i).getPdfName();
                }
                Log.e("Name ",tempString[0]);
                Log.e("Name of PDF",Integer.toString(uploadedListPDF.size()));
                for(int i=0;i<uploadedListPDF.size();i++) {
                    nameOfUploadedPdf.add(tempString[i]);
                    Log.e("Name of PDF",uploadedListPDF.get(i).getPdfName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference drf2 = FirebaseDatabase.getInstance().getReference().child("Users");
        drf2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TXT Links").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()) {
                    putTXT putTXT = ds.getValue(com.example.sirius.putTXT.class);
                    uploadedListTXT.add(putTXT);

                }
                String[] tempString = new String[uploadedListTXT.size()];
                Log.e("Txt file uploaded",Integer.toString(uploadedListTXT.size()));
                for(int i=0;i<tempString.length;i++) {
                    tempString[i]=uploadedListTXT.get(i).getTxtName();
                    nameOfUploadedTXT.add(tempString[i]);
                    Log.e("Txt file name",tempString[i]);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private void getAllTxt() {
        File directory = new File(txtPath);
        File[] files = directory.listFiles();

        for(int i=0;i<files.length;i++) {
            //String temp = files[i].getPath();
            uriOfTxt.add(new putTXT(files[i].getName(),files[i].toString()));
        }
    }

    private void getAllPdf() {
        File directory = new File(pdfPath);
        File[] files = directory.listFiles();

        for(int i=0;i<files.length;i++) {
            uriOfPdf.add(new putPDF(files[i].getName(),files[i].toString()));
        }

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

    private boolean allPermissionsGranted(){

        for(int i=0;i<REQUIRED_PERMISSIONS.length;i++) {
            if(ContextCompat.checkSelfPermission(this,REQUIRED_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {

                return false;
            }

        }
        return true;


    }
}
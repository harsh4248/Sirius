package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import id.zelory.compressor.Compressor;

public class Camera extends AppCompatActivity   {


    private static final String TAG = "Camera";

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    private Executor executor = Executors.newSingleThreadExecutor();
    PreviewView previewView;
    ImageButton captureImageButton,viewCapturedImagesButton,importImagesButton;
    ArrayList<Images> capturedImagesInBitMap;
    ArrayList<BytesImages> bytesImages;
    //ArrayList<ImagesData> cptImagesBitMap;
    ArrayList<String> captImgName;
    ArrayList<ImagesData> compressedImagesInFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.camera);
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        captureImageButton = findViewById(R.id.capturebutton);
        viewCapturedImagesButton = findViewById(R.id.viewcapturedimages);
        importImagesButton = findViewById(R.id.importimagebutton);

        capturedImagesInBitMap = new ArrayList<>();
        bytesImages = new ArrayList<>();
        captImgName = new ArrayList<>();
        compressedImagesInFile = new ArrayList<>();
        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        importImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }
        });

        viewCapturedImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(capturedImagesInBitMap.isEmpty()) {
                    Toast.makeText(Camera.this, "First Capture Images", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Camera.this,CapturedImages.class);
                    //intent.putExtra("Key",capturedImagesInBitMap);
                    intent.putParcelableArrayListExtra("Key",capturedImagesInBitMap);
                    //intent.putParcelableArrayListExtra("Key",bytesImages);
                    startActivity(intent);
                }*/
                if(captImgName.isEmpty()) {
                    Toast.makeText(Camera.this, "First Capture Images", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Camera.this,CapturedImages.class);
                    //intent.putParcelableArrayListExtra("Key",compressedImagesInFile);
                    intent.putExtra("Key", captImgName);
                    startActivity(intent);
                }
                /*if(cptImagesBitMap.isEmpty()) {
                    Toast.makeText(Camera.this, "First Capture Images", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Camera.this,CapturedImages.class);
                    intent.putExtra("Key",cptImagesBitMap);
                    startActivity(intent);
                }*/
                /*if(captImgName.isEmpty()) {
                    Toast.makeText(Camera.this, "First Capture Images", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Camera.this,CapturedImages.class);
                    intent.putExtra("Key",captImgName);
                    startActivity(intent);
                }*/


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {

            ClipData clipData = data.getClipData();

            if(clipData != null) {
                for(int i=0;i<clipData.getItemCount();i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    String fullpath = getFullPathFromURI(getApplicationContext(),uri);
                    captImgName.add(fullpath);
                    /*File file = new File(Objects.requireNonNull(uri.getPath()));
                    captImgName.add(file.toString());*/
                }
            } else {
                Uri uri = data.getData();
                String fullpath = getFullPathFromURI(getApplicationContext(),uri);
                captImgName.add(fullpath);
               /* File file = new File(Objects.requireNonNull(uri.getPath()));
                captImgName.add(file.toString());*/

            }
        }
    }

    private String getFullPathFromURI(Context context, Uri uri) {

        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {


                    Log.e(TAG, "run: "+ e.getMessage() );
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }

            }
        },ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

       // preview.setSurfaceProvider(previewView.getSurfaceProvider());


        ImageCapture.Builder imageBuilder = new ImageCapture.Builder();

        ImageCapture imageCapture = imageBuilder.setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);


        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {

                        toBitMap(image);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Toast toast = Toast.makeText(Camera.this, "Added",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        super.onCaptureSuccess(image);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(Camera.this, "Error in Image capture", Toast.LENGTH_SHORT).show();
                        super.onError(exception);
                    }
                });*/
                //File file = new File(getBatchDirectoryName());
                String folderName  = getExternalFilesDir(null).toString()+"/images/";
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);String timestamp = mDateFormat.format(new Date());
                File file = new File(getBatchDirectoryName(), timestamp+ ".jpg");
                captImgName.add(folderName+timestamp+ ".jpg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                               // Bitmap bitmap = Compressor.getDefault(Camera.this).compressToBitmap(file);
                                File compressedfile = Compressor.getDefault(Camera.this).compressToFile(file);
                                String cfstring = compressedfile.toString();
                                compressedImagesInFile.add(new ImagesData(/*compressedfile*/cfstring));


                                //capturedImagesInBitMap.add(new Images(bitmap));
                                //cptImagesBitMap.add(new ImagesData(bitmap));
                                //captImgName.add(BitMapToString(bitmap));
                                //Log.e("bitmap",)
                                //captImgName.add(file.toString());
                                final Toast toast = Toast.makeText(Camera.this, "Added",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Toast toast = Toast.makeText(Camera.this, "Error occured",Toast.LENGTH_SHORT);
                                toast.show();
                                Log.e("Error",exception.toString());
                            }
                        });

                    }
                });
            }
        });




    }

    private String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private Bitmap toBitMap(ImageProxy image) {

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        Bitmap bitmap= BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);

        capturedImagesInBitMap.add(new Images(bitmap));
        bytesImages.add(new BytesImages(clonedBytes));
        return bitmap;
    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
       // app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        app_folder_path = getExternalFilesDir(null).toString()+"/images";
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
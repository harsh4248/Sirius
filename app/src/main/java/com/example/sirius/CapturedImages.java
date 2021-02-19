package com.example.sirius;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class CapturedImages extends AppCompatActivity implements Serializable {
    //    String app_folder_path=getExternalFilesDir(null).toString()+"/images";
    ListView listView;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    recylerviewAdapter recylerviewAdapter;

    LinearLayoutManager HorizontalLayout;
    ArrayList<String> imagesDataArrayList;

    //Button cropButton;

    View ChildView;
    int RecyclerViewItemPosition;

    private View.OnClickListener onItemClickListener  = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            //Log.e("Item Click position",Integer.toString(position));
            //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(CapturedImages.this);

            //CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).setGuidelines(CropImageView.Guidelines.ON).start(CapturedImages.this);

            //CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).start(CapturedImages.this);
            RecyclerViewItemPosition = position;
            String item = imagesDataArrayList.get(position);
            String itemDestination = imagesDataArrayList.get(position);
            Uri imageUri = Uri.fromFile(new File(item));
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(CapturedImages.this);
            //Toast.makeText(CapturedImages.this, "Item Position = "+ position, Toast.LENGTH_SHORT).show();

            /*Intent intent = CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).getIntent(CapturedImages.this);
            startActivityForResult(intent,1004);*/
           // UCrop.of(imageUri,imageUri).withAspectRatio(16,9).withMaxResultSize(400,400).start(CapturedImages.this);
            //recyclerView.removeViewAt(position);
            //recyclerView.setAdapter(recylerviewAdapter);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(Objects.requireNonNull(resultUri.getPath()));
                imagesDataArrayList.add(RecyclerViewItemPosition,file.toString());
                recylerviewAdapter.notifyItemChanged(RecyclerViewItemPosition);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error in crop", Toast.LENGTH_SHORT).show();
            }
        }
    }
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

        imagesDataArrayList = getIntent().getStringArrayListExtra("Key");
        String temp = Integer.toString(imagesDataArrayList.size());
        Log.e("ArrayListContent",temp);
        Log.e("arrayLIstCONTENT",imagesDataArrayList.get(0));

        //cropButton = findViewById(R.id.cropbutton);
        recyclerView = findViewById(R.id.recyclerview);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        recylerviewAdapter = new recylerviewAdapter(this,imagesDataArrayList);

        HorizontalLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(HorizontalLayout);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recylerviewAdapter);

        recylerviewAdapter.setOnItemClickListener(onItemClickListener);

       // CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);



    }


}
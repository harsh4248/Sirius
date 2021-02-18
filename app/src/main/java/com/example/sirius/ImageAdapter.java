package com.example.sirius;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<String> {

    public ImageAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        //Images images = getItem(position);
        //BytesImages bytesImages = getItem(position);
        String imageBitMap = getItem(position);
        //Bitmap Images = getItem(position);
       // ImagesData imagesData = getItem(position);


        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mylistlayout, parent, false);

        }

        ImageView displayImages = convertView.findViewById(R.id.displayimage);

        /*if(bytesImages.getmResource() !=null)
            displayImages.setImageBitmap(BitmapFactory.decodeByteArray(bytesImages.getmResource(),0,bytesImages.getmResource().length));
        else
            displayImages.setImageResource(R.drawable.ic_baseline_image_24);*/

        //displayImages.setImageBitmap(images.getmResource());
        /*assert ImageFullpath != null;
        Picasso.with(getContext()).load(new File(ImageFullpath)).into(displayImages);*/
        //displayImages.setImageBitmap(Images);

        //displayImages.setImageBitmap(BitmapFactory.decodeFile(imageBitMap));
        assert imageBitMap != null;
        //Picasso.with(getContext()).load(new File(imageBitMap)).into(displayImages);
        //displayImages.setImageURI(Uri.fromFile(new File(imageBitMap)));
        //Glide.with(getContext()).load(new File(imageBitMap)).into(displayImages);


        Glide.with(getContext()).load(new File(imageBitMap)).into(displayImages);
        return convertView;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}

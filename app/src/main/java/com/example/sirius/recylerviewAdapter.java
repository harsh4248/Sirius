package com.example.sirius;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class recylerviewAdapter extends RecyclerView.Adapter<recylerviewAdapter.myView> {

    public List<String> list;
    Context context;

    public recylerviewAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public myView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylistlayout, parent, false);

        return new myView(itemView);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull myView holder, int position) {
        Glide.with(context).load(new File(list.get(position))).into(holder.displayImage);
    }

    @Override
    public int getItemCount() {
        //return 0;
        return list.size();
    }

    public class myView extends RecyclerView.ViewHolder {

        ImageView displayImage;
        public myView(@NonNull View itemView) {
            super(itemView);

            displayImage = itemView.findViewById(R.id.displayimage);
        }
    }
}

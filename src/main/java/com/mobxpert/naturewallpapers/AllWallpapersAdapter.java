package com.mobxpert.naturewallpapers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobxpert.naturewallpapers.interfaces.OnImageClickListner;

import java.util.ArrayList;
import java.util.List;

public class AllWallpapersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> mGalleryList1 = new ArrayList<>();
    Context mContext1;
    OnImageClickListner imageClickListner;

    public AllWallpapersAdapter(Context context, List<String> myGalleryList, OnImageClickListner imageClickListner) {
        this.mContext1 = context;
        this.mGalleryList1 = myGalleryList;
        this.imageClickListner = imageClickListner;
    }

    private class MyViewHolder1 extends RecyclerView.ViewHolder {
        ImageView thumnail;

        public MyViewHolder1(View itemView) {
            super(itemView);
            thumnail = itemView.findViewById(R.id.thumb);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_wallpapers_adapter_layout, parent, false);
        return new AllWallpapersAdapter.MyViewHolder1(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllWallpapersAdapter.MyViewHolder1 bodyViewHolder = (AllWallpapersAdapter.MyViewHolder1) holder;
        bindBodyComponent(bodyViewHolder, position);
    }

    private void bindBodyComponent(MyViewHolder1 holder, final int position) {

        Glide.with(mContext1)
                .load(mGalleryList1.get(position)).apply(new RequestOptions().placeholder(R.drawable.loading))
                .thumbnail(0.5f)
                .into(holder.thumnail);

        holder.thumnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageClickListner.OnImageClick(mGalleryList1.get(position));

            /*    Intent intent = new Intent(mContext1, FullViewImage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("image_Object", mGalleryList1.get(position));
                intent.putExtras(bundle);
                mContext1.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGalleryList1.size();
    }


}
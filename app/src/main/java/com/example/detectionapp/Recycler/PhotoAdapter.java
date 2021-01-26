package com.example.detectionapp.Recycler;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detectionapp.db.Photo;
import com.example.detectionapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{


    Context context;
    List<Photo> photos;

    public PhotoAdapter(Context ct, List<Photo> list)
    {
        context=ct;
        photos = list;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.photo_list_element, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, final int position) {

        holder.filename.setText(photos.get(position).filename);

//        Bitmap myBitmap = BitmapFactory.decodeFile(photos.get(position).filepath);
//        holder.image.setImageBitmap(myBitmap);

        Picasso.get()
                .load(photos.get(position).filepath)
                .fit()
                .centerInside()
                .into(holder.image);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MovieDetailsActivity.class);
//                intent.putExtra("id", movies.get(position).getId());
//                intent.putExtra("title", movies.get(position).getTitle());
//
//                context.startActivity(intent);
            }
        });


        //holder.image.setImageResource(R.drawable.star_icon);
    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{

        TextView filename;
        TextView filepath;
        ImageView image;
        ConstraintLayout mainLayout;


        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.photo_filename);
            image = itemView.findViewById(R.id.photo_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}

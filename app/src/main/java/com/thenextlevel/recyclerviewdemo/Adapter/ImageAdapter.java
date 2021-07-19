package com.thenextlevel.recyclerviewdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thenextlevel.recyclerviewdemo.ImageDetail;
import com.thenextlevel.recyclerviewdemo.ImagesActivity;
import com.thenextlevel.recyclerviewdemo.Model.Upload;
import com.thenextlevel.recyclerviewdemo.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Upload> uploads) {
        this.mContext = context;
        this.mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());

        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .fitCenter()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageTitle = uploadCurrent.getName();
                String imageUrl = uploadCurrent.getImageUrl();
                Intent intent = new Intent(view.getContext(), ImageDetail.class);
                intent.putExtra("image title", imageTitle);
                intent.putExtra("image url", imageUrl);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewName;
        public ImageView imageView;
        private LinearLayout linearLayout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            linearLayout = itemView.findViewById(R.id.linear_layout);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("select action");
            MenuItem nothing = contextMenu.add(Menu.NONE, 1, 1, "nothing");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            nothing.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setonItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
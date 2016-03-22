package com.example.avitenko.urlloader;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageHolder> {

    class ImageHolder extends RecyclerView.ViewHolder {

        public final ImageView image;

        public ImageHolder(ImageView itemView) {
            super(itemView);
            image = itemView;
        }
    }

    private final List<String> mUrls;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public ImagesAdapter(List<String> urls) {
        mUrls = urls;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView image = new ImageView(parent.getContext());
        return new ImageHolder(image);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        holder.image.setImageBitmap(ImageLoader.getInstance().getBitmap(
                holder.image.getContext(),
                mUrls.get(position),
                new ImageLoader.ImageListener() {

                    @Override
                    public void onLoadSucceed() {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
        ));
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }
}


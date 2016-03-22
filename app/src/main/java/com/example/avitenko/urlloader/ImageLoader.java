package com.example.avitenko.urlloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    public interface ImageListener {

        void onLoadSucceed();

    }

    private static final class SingletonHolder {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    public static ImageLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    ExecutorService service = Executors.newSingleThreadExecutor();

    private ImageLoader() {}

    LruCache<String, Bitmap> mCache = new LruCache<>(40);

    public Bitmap getBitmap(Context context, final String srcUrl, ImageListener listener) {
        Bitmap bitmap = mCache.get(srcUrl);
        if (bitmap == null) {
            loadBitmap(srcUrl, listener);
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
        } else {
            return bitmap;
        }
    }

    private void loadBitmap(final String srcUrl, final ImageListener listener) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(srcUrl);
                    URLConnection urlConnection = url.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();

                    byte [] bitmap = buffer.toByteArray();
                    mCache.put(srcUrl, BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length));
                    listener.onLoadSucceed();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}

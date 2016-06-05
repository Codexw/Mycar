package com.ahstu.mycar.sql;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sky on 2016/5/8.
 */
public class ForListViewImage {
    private ImageView mimageView;
    private String mUrl;

    //创建cache
    private LruCache<String, Bitmap> cache;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mimageView.getTag().equals(mUrl))
                mimageView.setImageBitmap((Bitmap) msg.obj);
        }
    };

    public ForListViewImage() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cachesize = maxMemory / 6;
        cache = new LruCache<String, Bitmap>(cachesize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();
            }

        };
    }

    //增加到缓存
    public void addBitmapTocache(String url, Bitmap bitmap) {
        //判断cache是否存在着当前url所对应的图片
        if (getBitmapfromCache(url) == null) {
            cache.put(url, bitmap);
        }

    }

    //从缓存中获取数据
    public Bitmap getBitmapfromCache(String url) {
        return cache.get(url);
    }

    public void showImageThread(ImageView imageView, final String url) {
        mimageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromURL(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                handler.sendMessage(message);

            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlstring) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlstring);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //使用asynctask异步加载图片
    public void showImageAsyncTask(ImageView imageView, String url) {
        Log.i("shuchu", "<<<<<<<<<<chongxinjiazai");
        Bitmap bitmap = getBitmapfromCache(url);
        Log.i("shuchu", "<<<<<<<<<<chongxinjiazai" + bitmap);
        if (bitmap == null) {
            new AsyncTask(imageView, url).execute(url);
            Log.i("shuchu", "<<<<<<<<<<chongxinjiazai");
        } else {
            imageView.setImageBitmap(bitmap);
        }


    }

    private class AsyncTask extends android.os.AsyncTask<String, Void, Bitmap> {
        private String mUrl;

        private ImageView mImageview;

        public AsyncTask(ImageView imageView, String url) {
            mImageview = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            //从网络获取图片
            Bitmap bitmap = getBitmapFromURL(params[0]);
            if (bitmap != null) {
                //将不再缓存的图片加入缓存
                addBitmapTocache(url, bitmap);
                Log.i("chushu", "1111111111111111111");
            }
            return bitmap;
//            return getBitmapFromURL(params[0]);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageview.getTag().equals(mUrl)) {
                mImageview.setImageBitmap(bitmap);
            }

        }
    }
}

package net.scadsdnd.ponygala;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class artRequest extends AsyncTask<String, Integer, Bitmap> {

    public ImageView outputImgView;
    public ProgressBar outputProgress;
    //public Context UIContext;

    private ImageView imv;
    private String path;

    public artRequest(){

    }

    @Override
    protected void onPreExecute() {
        outputProgress.setIndeterminate(true);
        outputProgress.setMax(100);
        this.imv = outputImgView;
        this.path = imv.getTag().toString();
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... in_url) {

        publishProgress(0,0);

        Bitmap bmImage = null;
        try {
            //outputProgress.setIndeterminate(true);
            URL myURL = new URL(in_url[0]);

            if(!Thread.interrupted() && imv.getTag().toString().equals(path)) {

                publishProgress(1,0);

                final URLConnection myConn = myURL.openConnection();
                myConn.connect();
                Log.v("!C", String.valueOf(myConn.getContentLength()));

                // https://stackoverflow.com/questions/17830092/android-show-progress-bar-while-image-loading-dynamically
                InputStream inStreamHolder = myConn.getInputStream();
                ByteArrayOutputStream outStreamHolder = new ByteArrayOutputStream();
                int fileLength = myConn.getContentLength();
                byte data[] = new byte[fileLength];
                int increment = fileLength / 100;
                int count = -1;
                int progress = 0;

                while ((count = inStreamHolder.read(data, 0, increment))!=-1){
                    progress += count;
                    publishProgress(1,((progress*100)/fileLength));
                    outStreamHolder.write(data, 0, count);
                }

                bmImage = BitmapFactory.decodeByteArray(outStreamHolder.toByteArray(), 0, data.length);

                inStreamHolder.close();
                outStreamHolder.close();

            } else {
                cancel(true);
            }

        } catch (Exception e){
            Log.e("IMG_ERR", e.getLocalizedMessage());
            return null;
        }

        return bmImage;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        outputProgress.setIndeterminate(values[0]==0);
        outputProgress.setProgress(values[1]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        outputImgView.setImageBitmap(bitmap);
        outputProgress.setVisibility(View.INVISIBLE);
        super.onPostExecute(bitmap);
    }
}

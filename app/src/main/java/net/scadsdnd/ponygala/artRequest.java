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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class artRequest extends AsyncTask<String, Integer, Bitmap> {

    public ImageView outputImgView;
    public ProgressBar outputProgress;
    //public Context UIContext;

    @Override
    protected void onPreExecute() {
        outputProgress.setIndeterminate(true);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... in_url) {

        publishProgress(0);

        Bitmap bmImage = null;
        try {
            //outputProgress.setIndeterminate(true);
            URL myURL = new URL(in_url[0]);
            URLConnection myConn = myURL.openConnection();
            myConn.connect();
            Log.v("!C", String.valueOf(myConn.getContentLength()));
            publishProgress(1);
            reportableIS inStream =  new reportableIS(myConn.getInputStream(), outputProgress, myConn.getContentLength());
            BufferedInputStream buffInStream = new BufferedInputStream(inStream);
            bmImage = BitmapFactory.decodeStream(buffInStream);
            buffInStream.close();
            inStream.close();
            //outputProgress.setVisibility(View.INVISIBLE);
        } catch (IOException e){
            Log.e("IMG_ERR", e.getLocalizedMessage());
        }

        return bmImage;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        outputProgress.setIndeterminate(values[0]==0);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        outputImgView.setImageBitmap(bitmap);
        outputProgress.setVisibility(View.INVISIBLE);
        super.onPostExecute(bitmap);
    }
}

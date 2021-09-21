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
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;

public class artRequest extends AsyncTask<String, Integer, Bitmap[]> {

    public ImageView[] outputImgView;
    public ProgressBar[] outputProgress;
    public Boolean retryLoad;

    private Integer maxOutputs;

    private Bitmap loadImageCore(String in_url, int Index){

        Bitmap bmImage = null;

        try {

            URL myURL = new URL(in_url);

            if (!Thread.interrupted()) {

                publishProgress(Index, 1, 0);

                final URLConnection myConn = myURL.openConnection();
                myConn.connect();
                //Log.v("!C", String.valueOf(myConn.getContentLength()));

                // https://stackoverflow.com/questions/17830092/android-show-progress-bar-while-image-loading-dynamically
                InputStream inStreamHolder = myConn.getInputStream();
                ByteArrayOutputStream outStreamHolder = new ByteArrayOutputStream();
                int fileLength = myConn.getContentLength();
                byte data[] = new byte[fileLength];
                int increment = fileLength / 100;
                int count = -1;
                int progress = 0;

                while ((count = inStreamHolder.read(data, 0, increment)) != -1) {
                    progress += count;
                    publishProgress(Index, 1, ((progress * 100) / fileLength));
                    outStreamHolder.write(data, 0, count);
                }

                bmImage = BitmapFactory.decodeByteArray(
                        outStreamHolder.toByteArray(), 0, data.length);

                inStreamHolder.close();
                outStreamHolder.close();

                //Thread.sleep(1000);

            } else {
                cancel(true);
            }

        } catch (IOException e) {
            Log.e("IO_ERR", e.toString());
        } catch (Exception e) {
            Log.e("IMG_ERR", e.toString());
        }

        return bmImage;
    }

    @Override
    protected void onPreExecute() {

        this.maxOutputs = outputImgView.length;

        for (int i=0; i < this.maxOutputs; i++) {
            outputProgress[i].setIndeterminate(true);
            outputProgress[i].setMax(100);
        }

        super.onPreExecute();
    }

    @Override
    protected Bitmap[] doInBackground(String... in_url) {

        Bitmap[] bmImage = new Bitmap[this.maxOutputs];

        for (int i=0; i < this.maxOutputs; i++) {

            //Log.v("arr", String.valueOf(i));

            if(in_url[i]!=null) {

                if (retryLoad) {

                    Bitmap tempImage = loadImageCore(in_url[i], i);

                    // Will hold retry download until image loaded
                    while (tempImage == null) {
                        tempImage = loadImageCore(in_url[i], i);
                        Log.v("RELOAD", "Reload started");
                    } // retry end

                    bmImage[i] = tempImage;


                } else {
                    bmImage[i] = loadImageCore(in_url[i], i);
                }

            }
        }

        return bmImage;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        outputProgress[values[0]].setIndeterminate(values[1] == 0);
        outputProgress[values[0]].setProgress(values[2]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmap) {
        for (int i=0; i < this.maxOutputs; i++) {
            if(bitmap[i]!=null){
                outputImgView[i].setImageBitmap(bitmap[i]);
                outputImgView[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            outputProgress[i].setVisibility(View.GONE);
        }
        super.onPostExecute(bitmap);
    }
}

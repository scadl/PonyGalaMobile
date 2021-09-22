package net.scadsdnd.ponygala;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

// Simple AsynctTask (other thread)
// https://developer.android.com/reference/android/os/AsyncTask
public class WebRequest extends AsyncTask<String,String,Integer> {

    // Java callback registration
    // https://www.fandroid.info/urok-13-osnovy-java-metody-obratnogo-vyzova-callback/

    interface webUICatIf {
        void pCategoryListLoaded(JSONArray jArr);
        void pSelectionDatesLoaded(JSONArray jArr);
    }
    interface webUIGalaIf{
        void pArtListLoaded(JSONArray jArr);
    }

    private webUICatIf CBCatVar;
    private webUIGalaIf CBGalVar;

    public void regCatCb(webUICatIf CBVarIn){
        this.CBCatVar = CBVarIn;
    }
    public void regGalCb(webUIGalaIf CBVarIn){
        this.CBGalVar = CBVarIn;
    }

    // Callback reg end

    private String out;
    public Context UIContext;
    public View pbIndicator;
    private int toastDuration = Toast.LENGTH_SHORT;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... inParams) {

        //Android 6.0 release removes support for the Apache HTTP client.
        // If your app is using this client and targets Android 2.3 (API level 9) or higher, use the HttpURLConnection class instead.
        // https://developer.android.com/about/versions/marshmallow/android-6.0-changes

        URL youServ = null;
        String getParams = null;

        publishProgress(UIContext.getString(R.string.load_form));

        try {
            switch (Integer.valueOf(inParams[0])){
                case 1:
                    if(inParams.length > 1){
                        getParams = "act="+inParams[0]+"&date="+inParams[1];
                    } else {
                        getParams = "act="+inParams[0];
                    }
                    break;
                case 2:
                    if(inParams.length > 2) {
                        getParams = "act=" + inParams[0] + "&cat_id=" + inParams[1]+"&date="+inParams[2];
                    } else {
                        getParams = "act=" + inParams[0] + "&cat_id=" + inParams[1];
                    }
                    break;
                default:
                    getParams = "act="+inParams[0];
                    break;
            }
            youServ = new URL("https://artgala.scadsdnd.net/mods/api.php?"+getParams);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            publishProgress(UIContext.getString(R.string.load_error));
        }

        publishProgress(UIContext.getString(R.string.load_connect));

        HttpURLConnection urlConn = null;
        try {
            urlConn = (HttpsURLConnection) youServ.openConnection();
            urlConn.setRequestMethod("GET");

            // Ho to use POST params
            // https://developer.android.com/reference/java/net/HttpURLConnection#posting-content
            // https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html

        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        }

        try{
            InputStream inS = new BufferedInputStream(urlConn.getInputStream());
            BufferedReader myBuffRead = new BufferedReader(new InputStreamReader(inS, "UTF-8"), 8);
            StringBuilder myStrBuild = new StringBuilder();
            myStrBuild.append(myBuffRead.readLine()+"\n");

            out = myStrBuild.toString();

            publishProgress(UIContext.getString(R.string.load_complete));
            inS.close();

            //readStream(inS)
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(UIContext.getString(R.string.load_error));
        } finally {
            urlConn.disconnect();
        }


        return Integer.valueOf(inParams[0]);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //StatusUI.setText(values[0]);
        Toast.makeText(UIContext, values[0], toastDuration).show();
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer act) {

        Toast.makeText(UIContext, UIContext.getString(R.string.load_process), toastDuration).show();

        try {

            Log.v("LOG", out);

            JSONArray jRows = new JSONArray(out);

            Log.v("JSON", Integer.toString(jRows.length()));

            switch (act){
                case 1:
                    CBCatVar.pCategoryListLoaded(jRows);
                    break;
                case 2:
                    CBGalVar.pArtListLoaded(jRows);
                    break;
                case 4:
                    CBCatVar.pSelectionDatesLoaded(jRows);
                    break;
                default:
                        Log.e("!SRV", "Unknown API request");
                        Toast.makeText(UIContext, "Unknown API request", toastDuration).show();
                   break;
            }

            Toast.makeText(UIContext, UIContext.getString(R.string.load_complete), toastDuration).show();
            pbIndicator.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(UIContext, UIContext.getString(R.string.load_error), toastDuration).show();
        }

        super.onPostExecute(act);
    }
}

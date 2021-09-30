package net.scadsdnd.ponygala;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
        void pCategoryAdded(JSONArray jArr);
        void pAdminPassCheck(JSONArray jArr);
    }
    interface webUIGalaIf{
        void pArtListLoaded(JSONArray jArr);
        void pAdminCatsLoaded(JSONArray jArr);
        void pCatDeletedRenamed(boolean operDel);
        void pArtsDeletedMoved(boolean operDel);
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
    public TextView StatusUI;

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
        String getParams = "act="+inParams[0];

        publishProgress(UIContext.getString(R.string.load_form));

        try {
            switch (Integer.valueOf(inParams[0])){
                case 1:
                    // Get categories list + 5 thumbs each + arts count each (with date, if any)
                    if(inParams.length > 1){
                        getParams += "&date="+inParams[1];
                    }
                    break;
                case 2:
                    // Get arts in category (with date, if any)
                    if(inParams.length > 2) {
                        getParams += "&cat_id=" + inParams[1]+"&date="+inParams[2];
                    } else {
                        getParams += "&cat_id=" + inParams[1];
                    }
                    break;

                case 3:
                    // Get 5 thumbs for category only (with date, if any)
                    // Not used now
                    break;

                case 4:
                    // Get publication dates list
                    // No additional params needed
                    break;
                case 5:
                    // Add a new category
                    getParams += "&newCat=" + inParams[1];
                    break;
                case 6:
                    // Rename existing category
                    getParams += "&newName=" + inParams[1]+"&catid="+inParams[2];
                    break;
                case 7:
                    // Delete existing category (and arts to buffer)
                    getParams += "&catid="+inParams[1];
                    break;
                case 8:
                    // Move art to new category
                    getParams += "&aid="+inParams[1]+"&cat="+inParams[2]+"&date=1-01-1970&dateupd=false";
                    break;
                case 9:
                    // Delete art from system
                    getParams += "&aid="+inParams[1];
                    break;
                case 10:
                    // Check admin password
                    getParams += "&pass="+inParams[1];
                    break;
                case 11:
                    // Get list of categories for admin
                    // no params required
                    break;
                default:

                    break;
            }
            youServ = new URL("https://artgala.scadsdnd.net/mods/api.php?"+getParams);
            Log.v("ATR!", youServ.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
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
            publishProgress(e.getLocalizedMessage());
        } finally {
            urlConn.disconnect();
        }


        return Integer.valueOf(inParams[0]);
    }

    @Override
    protected void onProgressUpdate(String... values) {

        StatusUI.setText(values[0]);
        Log.i("ATP!", values[0]);

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer act) {


         StatusUI.setText( UIContext.getString(R.string.load_process) );


        try {

            JSONArray jRows = new JSONArray(out);

            try {
                Log.v("LOG", out);
                Log.v("JSON", Integer.toString(jRows.length()));
            } catch (Exception e){
                e.printStackTrace();
            }

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
                case 5:
                    CBCatVar.pCategoryAdded(jRows);
                    break;
                case 6:
                    CBGalVar.pCatDeletedRenamed(false);
                    break;
                case 7:
                    CBGalVar.pCatDeletedRenamed(true);
                    break;
                case 8:
                    CBGalVar.pArtsDeletedMoved(false);
                    break;
                case 9:
                    CBGalVar.pArtsDeletedMoved(true);
                    break;
                case 10:
                    CBCatVar.pAdminPassCheck(jRows);
                    break;
                case 11:
                    CBGalVar.pAdminCatsLoaded(jRows);
                    break;

                default:
                        Log.e("!SRV", "Unknown API request");
                        StatusUI.setText("Unknown API request");

                   break;
            }

            StatusUI.setText(UIContext.getString(R.string.load_complete));

            pbIndicator.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
            StatusUI.setText(UIContext.getString(R.string.load_error) + e.getLocalizedMessage());

        }

        super.onPostExecute(act);
    }
}

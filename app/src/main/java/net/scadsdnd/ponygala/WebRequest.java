package net.scadsdnd.ponygala;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

// https://developer.android.com/reference/android/os/AsyncTask
public class WebRequest extends AsyncTask<String,String,String> {

    private String out;
    public ListView OutputView;
    public Context UIContext;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... inParams) {

        //Android 6.0 release removes support for the Apache HTTP client.
        // If your app is using this client and targets Android 2.3 (API level 9) or higher, use the HttpURLConnection class instead.
        // https://developer.android.com/about/versions/marshmallow/android-6.0-changes

        URL youServ = null;

        publishProgress("Building request");

        try {
            youServ = new URL("https://artgala.scadsdnd.net/mods/api.php?"+inParams[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        }

        publishProgress("Executing request");

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

        publishProgress("Reading Data");

        try{
            InputStream inS = new BufferedInputStream(urlConn.getInputStream());

            BufferedReader myBuffRead = new BufferedReader(new InputStreamReader(inS, "UTF-8"), 8);
            StringBuilder myStrBuild = new StringBuilder();
            myStrBuild.append(myBuffRead.readLine()+"\n");

            out = myStrBuild.toString();

            publishProgress("Data loaded");



            //readStream(inS)
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        } finally {
            urlConn.disconnect();
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //StatusUI.setText(values[0]);
        Toast.makeText(UIContext, values[0], Toast.LENGTH_SHORT/2).show();
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String unused) {
        Log.v("LOG", out);
        try {

            JSONArray jRows = new JSONArray(out);
            final String[] catID = new String[jRows.length()];
            String[] catName = new String[jRows.length()];

            Log.v("JSON", Integer.toString(jRows.length()));

            JSONObject jData = null;
            for (int i=0; i < jRows.length(); i++){
                jData = jRows.getJSONObject(i);
                catID[i] = jData.getString("cat_id");
                catName[i] = jData.getString("cat_name");

                //Log.v("JSON", jData.getString("cat_name"));
            }

            // https://developer.android.com/reference/android/widget/ListView
            ArrayAdapter<String> myAdapt = new ArrayAdapter<String>(UIContext, android.R.layout.simple_list_item_1, catName);
            OutputView.setAdapter(myAdapt);

            OutputView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(UIContext, "db_Pos:" + catID[position], Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        }

        super.onPostExecute(unused);
    }
}

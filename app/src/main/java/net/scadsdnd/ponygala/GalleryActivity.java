package net.scadsdnd.ponygala;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends Activity implements WebRequest.webUIGalaIf {

    caheDB dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new caheDB(this);
        db = dbh.getWritableDatabase();

        dbh.onUpgrade(db, 0, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        WebRequest artWebRq = new WebRequest();

        artWebRq.UIContext = this;
        artWebRq.regGalCb(this);
        artWebRq.pbIndicator = (ProgressBar) findViewById(R.id.pbWaitGal);

        Toast.makeText(this, getText(R.string.load_start), Toast.LENGTH_SHORT);

        String selDate = getIntent().getStringExtra("catDate");
        if(selDate!=null){
            artWebRq.execute("2", getIntent().getStringExtra("catId"), selDate);
        } else {
            artWebRq.execute("2", getIntent().getStringExtra("catId"));
        }

    }

    public void pArtListLoaded(JSONArray jArr) {

        ContentValues dbRow = new ContentValues();

        Log.v("!", "Got arts from category");

        String[] artID = new String[jArr.length()];
        final String[] artName = new String[jArr.length()];
        String[] artThumb = new String[jArr.length()];
        final String[] artFull = new String[jArr.length()];
        final String[] artAuthor = new String[jArr.length()];

        JSONObject jData = null;

        try {
            for (int i = 0; i < jArr.length(); i++) {

                jData = jArr.getJSONObject(i);

                artID[i] = jData.getString("aid");
                artName[i] = jData.getString("title");
                artThumb[i] = jData.getString("thumb");
                artFull[i] = jData.getString("file_name");
                artAuthor[i] = jData.getString("author");

                dbRow.put(dbh.COLS[0], i);
                dbRow.put(dbh.COLS[1],artFull[i]);
                dbRow.put(dbh.COLS[2],artName[i]);
                dbRow.put(dbh.COLS[3],artAuthor[i]);
                db.insert(dbh.TAB, null, dbRow);
                dbRow.clear();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
        dbh.close();

        Map<String, String[]> artData = new HashMap<>();
        artData.put("art_name", artName);
        artData.put("art_tb", artThumb);

        GridView outGridVW = (GridView) findViewById(R.id.gvArts);
        ListAdapter grAdapt = new artAdapter(this, artName, artData);
        outGridVW.setAdapter(grAdapt);

        outGridVW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intFull = new Intent(adapterView.getContext(), ImageActivity.class);
                intFull.putExtra("imgMaxInd", artName.length);
                intFull.putExtra("imgIndex", position);
                //intFull.putStringArrayListExtra("imgFull", bFull);
                //intFull.putStringArrayListExtra("imgTitle", bName);
                //intFull.putStringArrayListExtra("imgAuthor", bAuth);
                adapterView.getContext().startActivity(intFull);

            }
        });
    }
}
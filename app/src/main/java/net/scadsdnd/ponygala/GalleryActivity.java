package net.scadsdnd.ponygala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends Activity implements WebRequest.webUIGalaIf {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        WebRequest artWebRq = new WebRequest();

        artWebRq.UIContext = this;
        artWebRq.regGalCb(this);
        artWebRq.StatusUI = (TextView) findViewById(R.id.tvStatus);
        artWebRq.pbIndicator = (ProgressBar) findViewById(R.id.pbWaitGal);

        TextView tv = (TextView) findViewById(R.id.tvStatus);
        tv.setText(getText(R.string.load_start));

        artWebRq.execute(
                2,
                Integer.parseInt(
                        getIntent().getStringExtra("catId")
                )
        );

    }

    public void pArtListLoaded(JSONArray jArr) {

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                intFull.putExtra("imgFull", artFull);
                intFull.putExtra("imgTitle", artName);
                intFull.putExtra("imgAuthor", artAuthor);
                adapterView.getContext().startActivity(intFull);

            }
        });
    }
}
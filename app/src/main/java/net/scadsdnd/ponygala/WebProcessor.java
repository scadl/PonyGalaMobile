package net.scadsdnd.ponygala;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Java callback implement
// https://www.fandroid.info/urok-13-osnovy-java-metody-obratnogo-vyzova-callback/
public class WebProcessor implements WebRequest.callBackInterface {

    public View OutputView;
    public Context UIContext;

    @Override
    public void pCategoryList(JSONArray jRows) {

        Log.v("!","Got Categories");

        final String[] catID = new String[jRows.length()];
        String[] catName = new String[jRows.length()];
        String[] catCounts = new String[jRows.length()];
        String[][] catThumbs = new String[][]{
                new String[jRows.length()],new String[jRows.length()],
                new String[jRows.length()],new String[jRows.length()],
                new String[jRows.length()]
        };
        JSONObject jData = null;

        Map<String, String[]> srvData = new HashMap<>();

        try {
            for (int i = 0; i < jRows.length(); i++) {
                jData = jRows.getJSONObject(i);

                catID[i] = jData.getString("cat_id");
                catName[i] = jData.getString("cat_name");
                catCounts[i] = jData.getString("count");

                if (Integer.valueOf(jData.getString("count")) > 5) {
                    for (int j = 0; j < 5; j++) {
                        catThumbs[j][i] = jData.getString("thumb_" + (j+1));
                    }
                }

                //Log.v("JSON", jData.getString("cat_name"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        srvData.put("ids", catID);
        srvData.put("names", catName);
        srvData.put("counters", catCounts);
        for(int j=0; j<5; j++){
            srvData.put("img_"+j, catThumbs[j]);
        }


        // https://developer.android.com/reference/android/widget/ListView
        //ArrayAdapter<String> myAdapt = new ArrayAdapter<String>(UIContext, android.R.layout.simple_list_item_1, catName);
        //OutputView.setAdapter(myAdapt);

        ListView OutputListVW = (ListView) OutputView;

        ListAdapter listAdapter = new CatAdapter(UIContext, catName, srvData);
        OutputListVW.setAdapter(listAdapter);

        OutputListVW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(UIContext, "db_id:" + catID[position], Toast.LENGTH_SHORT).show();

                // Creating new activity on click
                // https://developer.android.com/training/basics/firstapp/starting-activity#java
                Intent intGala = new Intent(UIContext, GalleryActivity.class);
                intGala.putExtra("catId", catID[position]);
                UIContext.startActivity(intGala);

            }
        });

    }

    @Override
    public void pArtList(JSONArray jArr) {

        Log.v("!", "Got arts from category");

        String[] artID = new String[jArr.length()];
        final String[] artName = new String[jArr.length()];
        String[] artThumb = new String[jArr.length()];
        final String[] artFull =  new String[jArr.length()];
        final String[] artAuthor = new String[jArr.length()];

        JSONObject jData = null;

        try{
            for (int i=0; i < jArr.length(); i++){
                jData = jArr.getJSONObject(i);
                artID[i] = jData.getString("aid");
                artName[i] = jData.getString("title");
                artThumb[i] = jData.getString("thumb");
                artFull[i] = jData.getString("file_name");
                artAuthor[i] = jData.getString("author");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        Map<String, String[]> artData = new HashMap<>();
        artData.put("art_id", artID);
        artData.put("art_name", artName);
        artData.put("art_tb", artThumb);

        GridView outGridVW = (GridView) OutputView;
        ListAdapter grAdapt = new artAdapter(UIContext, artName, artData);
        outGridVW.setAdapter(grAdapt);

        outGridVW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                
                Intent intFull = new Intent(UIContext, ImageFull.class);
                intFull.putExtra("imgFull", artFull[position]);
                intFull.putExtra("imgTitle", artName[position]);
                intFull.putExtra("imgAuthor", artAuthor[position]);
                UIContext.startActivity(intFull);

            }
        });

    }
}

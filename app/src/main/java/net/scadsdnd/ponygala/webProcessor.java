package net.scadsdnd.ponygala;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Java callback implement
// https://www.fandroid.info/urok-13-osnovy-java-metody-obratnogo-vyzova-callback/
public class webProcessor implements WebRequest.callBackInterface {

    public ListView OutputView;
    public Context UIContext;

    @Override
    public void pCategoryList(JSONArray jRows) {

        Log.v("!","CallBack");

        final String[] catID = new String[jRows.length()];
        String[] catName = new String[jRows.length()];
        JSONObject jData = null;

        try {
            for (int i = 0; i < jRows.length(); i++) {
                jData = jRows.getJSONObject(i);
                catID[i] = jData.getString("cat_id");
                catName[i] = jData.getString("cat_name");

                //Log.v("JSON", jData.getString("cat_name"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        // https://developer.android.com/reference/android/widget/ListView
        ArrayAdapter<String> myAdapt = new ArrayAdapter<String>(UIContext, android.R.layout.simple_list_item_1, catName);
        OutputView.setAdapter(myAdapt);

        OutputView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(UIContext, "db_Pos:" + catID[position], Toast.LENGTH_SHORT).show();

                // Creating new activity on click
                // https://developer.android.com/training/basics/firstapp/starting-activity#java
                Intent intGala = new Intent(UIContext, GallaryActivity.class);
                intGala.putExtra("catId", catID[position]);
                UIContext.startActivity(intGala);

            }
        });

    }

    @Override
    public void pArtList(JSONArray jArr) {
        Log.v("!", "Gallary");
    }
}

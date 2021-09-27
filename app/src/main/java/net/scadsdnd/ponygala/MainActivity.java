package net.scadsdnd.ponygala;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements WebRequest.webUICatIf
{

    Integer lvl = 0;
    Boolean isAdmin = false;
    WebRequest lastCatWebRq;
    Boolean lockLoad = false;
    String[] optData = null;
    String selDate = null;
    List<artRequest> asyncList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        SharedPreferences shPrf = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        isAdmin = shPrf.getBoolean("admin_mode", false);

        sectionTask().execute("1");

    }

    private WebRequest sectionTask(){

        ProgressBar pbIndicatorElem = (ProgressBar) findViewById(R.id.pbWaitMain);
        pbIndicatorElem.setVisibility(View.VISIBLE);

        if(lastCatWebRq!= null && lastCatWebRq.getStatus() != AsyncTask.Status.FINISHED) {
            lastCatWebRq.cancel(true);
        }

        WebRequest catWebRq = new WebRequest();

        catWebRq.UIContext = this;
        catWebRq.regCatCb(this);
        catWebRq.pbIndicator = pbIndicatorElem;

        lastCatWebRq = catWebRq;

        return catWebRq;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.mReload:
                sectionTask().execute("1");
                break;
            case R.id.mLogin:
                try {
                    Toast.makeText(this, "Login not available now", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.mExit:
                finish();
                System.exit(0);
                break;
            case R.id.mLockLoad:
                MenuItem mR = item;
                mR.setChecked(!mR.isChecked());
                lockLoad = mR.isChecked();
                break;
            case R.id.mSelDate:

                final AlertDialog.Builder dlgDateTpl = new AlertDialog.Builder(this);
                dlgDateTpl.setTitle(R.string.dlgDateTitle);
                dlgDateTpl.setMessage(R.string.dlgDateMsg);
                dlgDateTpl.setIcon(android.R.drawable.ic_menu_today);

                ArrayAdapter<String> optAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, optData);
                optAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner calDate = new Spinner(this);
                LinearLayout.LayoutParams lyParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                calDate.setLayoutParams(lyParam);
                calDate.setAdapter(optAdapt);
                calDate.setPadding(25,5,25, (int) getResources().getDimension(R.dimen.lblSides));
                calDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selDate = (String) adapterView.getItemAtPosition(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dlgDateTpl.setView(calDate);

                dlgDateTpl.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sectionTask().execute("1", selDate);
                    }
                });
                dlgDateTpl.setNegativeButton(this.getString(R.string.btnCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dlgDate = dlgDateTpl.create();
                dlgDate.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        for (int i=0; i< asyncList.size(); i++ ) {
            asyncList.get(i).cancel(true);
        }

        lvl-=1;
        switch (lvl){
            case -1:
                try {
                    Toast.makeText(
                            MainActivity.this,
                            this.getString(R.string.exitConfirm),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case -2:
                finish();
                System.exit(0);
                break;
            default:
                Log.i("SYS", "Level: " + Integer.toString(lvl));
                break;
        }

    }


    @Override
    public void pCategoryListLoaded(JSONArray jRows) {

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
        Map<String, String> imgQuery = new HashMap<>();

        //String[] imgQuery = new String[jRows.length()*5];

        try {
            for (int i = 0; i < jRows.length(); i++) {
                jData = jRows.getJSONObject(i);

                catID[i] = jData.getString("cat_id");
                catName[i] = jData.getString("cat_name");
                catCounts[i] = jData.getString("count");

                if (Integer.valueOf(jData.getString("count")) > 1) {
                    for (int j = 0; j < 5; j++) {
                        catThumbs[j][i] = jData.getString("thumb_" + (j+1));
                        imgQuery.put("thumb_"+(i+j), jData.getString("thumb_" + (j+1)));
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

        ListView OutputListVW = (ListView) findViewById(R.id.catListView);

        CatAdapter listAdapter = new CatAdapter(this, catName, asyncList);
        listAdapter.catData = srvData;
        listAdapter.isLoadLocked = lockLoad;

        OutputListVW.setDivider(null);
        OutputListVW.setAdapter(listAdapter);

        OutputListVW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Toast.makeText(parent.getContext(), "db_id:" + catID[position], Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }

                // Prevent execution of async tasks, after activity closed
                for (int i=0; i< asyncList.size(); i++ ) {
                    asyncList.get(i).cancel(true);
                }

                // Creating new activity on click
                // https://developer.android.com/training/basics/firstapp/starting-activity#java
                Intent intGala = new Intent(parent.getContext(), GalleryActivity.class);
                intGala.putExtra("catId", catID[position]);
                intGala.putExtra("catDate", selDate);
                parent.getContext().startActivity(intGala);

            }
        });

        sectionTask().execute("4");

        //OutputListVW.setRecyclerListener(new );
    }

    @Override
    public void pSelectionDatesLoaded(JSONArray jRows) {

        Log.v("!","Got Selection Dates");

        try {

            JSONArray jData = jRows.getJSONArray(0);
            optData = new String[jData.length()];

            for (int i = 0; i < jData.length(); i++) {
                optData[i] = jData.getString(i);
                //Log.v("J", jData.getString(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

}

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
import android.widget.EditText;
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

    private Integer lvl = 0;
    private Boolean isAdmin;
    private WebRequest lastCatWebRq;
    private Boolean lockLoad = false;
    private String[] optData = null;
    private String selDate = null;
    private List<artRequest> asyncList = new ArrayList<>();
    private SharedPreferences shPrf;
    private MenuItem mAddCat, mLogout, mLogin;
    private enum dialogTypes { dateSlection, newCatName, adminPass }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);

        shPrf = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        isAdmin = shPrf.getBoolean("admin_mode", false);

        sectionTask(true).execute("1");

    }

    private WebRequest sectionTask(boolean showIndicator){

        ProgressBar pbIndicatorElem = (ProgressBar) findViewById(R.id.pbWaitMain);

        if(showIndicator) {
            pbIndicatorElem.setVisibility(View.VISIBLE);
        }

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

        mAddCat = menu.findItem(R.id.mAddCat);
        mLogout = menu.findItem(R.id.mLogout);
        mLogin= menu.findItem(R.id.mLogin);

        if(isAdmin){
            mLogin.setVisible(false);
            mLogout.setVisible(true);
            mAddCat.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor prfEditor = shPrf.edit();

        switch (item.getItemId()){
            case R.id.mReload:
                sectionTask(true).execute("1");
                break;
            case R.id.mLogin:

                myDialog(dialogTypes.adminPass);

                // --------------------- TEMP

                Toast.makeText(this, "You are admin", Toast.LENGTH_SHORT).show();

                prfEditor.putBoolean("admin_mode", true);
                prfEditor.commit();

                isAdmin = true;

                mLogin.setVisible(false);
                mLogout.setVisible(true);
                mAddCat.setVisible(true);

                break;
            case R.id.mLogout:

                Toast.makeText(this, "You are visitor", Toast.LENGTH_SHORT).show();

                prfEditor.putBoolean("admin_mode", false);
                prfEditor.commit();

                isAdmin = false;

                mLogin.setVisible(true);
                mLogout.setVisible(false);
                mAddCat.setVisible(false);

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

                myDialog(dialogTypes.dateSlection);

                break;

            case R.id.mAddCat:

                myDialog(dialogTypes.newCatName);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void myDialog(final dialogTypes dialogT){

        int dTitle = 0;
        int dIcon = 0;
        View dCont = null;

        switch (dialogT){
            case dateSlection:
                dTitle = R.string.dlgDateTitle;
                dIcon = android.R.drawable.ic_menu_today;

                ArrayAdapter<String> optAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, optData);
                optAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner calDate = new Spinner(this);
                LinearLayout.LayoutParams lyParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                calDate.setLayoutParams(lyParam);
                calDate.setAdapter(optAdapt);
                calDate.setPadding(25,(int) getResources().getDimension(R.dimen.lblSides),
                        25, (int) getResources().getDimension(R.dimen.lblSides));
                calDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selDate = (String) adapterView.getItemAtPosition(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dCont = calDate;

                break;
            case newCatName:

                dTitle = R.string.dlgNewCatTitle;
                dIcon = android.R.drawable.ic_menu_add;

                final EditText edtCat = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lpC = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                edtCat.setPadding(25,(int) getResources().getDimension(R.dimen.lblSides),
                        25, (int) getResources().getDimension(R.dimen.lblSides));
                edtCat.setLayoutParams(lpC);

                dCont = edtCat;

                break;
            case adminPass:

                dTitle = R.string.dlgAdminPassTitle;
                dIcon = android.R.drawable.ic_menu_myplaces;

                final EditText edtPass = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                edtPass.setPadding(25,(int) getResources().getDimension(R.dimen.lblSides),
                        25, (int) getResources().getDimension(R.dimen.lblSides));
                edtPass.setLayoutParams(lpP);

                dCont = edtPass;

                break;
            default:

                break;
        }

        final AlertDialog.Builder dlgDateTpl = new AlertDialog.Builder(this);
        dlgDateTpl.setTitle(dTitle);
        dlgDateTpl.setIcon(dIcon);

        dlgDateTpl.setView(dCont);

        final TextView finalDCont = (TextView) dCont;
        dlgDateTpl.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (dialogT){
                    case dateSlection:
                        sectionTask(true).execute("1", selDate);
                        break;
                    case adminPass:
                        sectionTask(false).execute("10", finalDCont.getText().toString());
                        break;
                    case newCatName:
                        sectionTask(false).execute("5", finalDCont.getText().toString());
                        if(selDate!=null) {
                            sectionTask(true).execute("1", selDate);
                        } else {
                            sectionTask(true).execute("1");
                        }
                        break;
                }

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

        CatAdapter listAdapter = new CatAdapter(this, catName);
        listAdapter.catData = srvData;
        listAdapter.isLoadLocked = lockLoad;
        listAdapter.aL = asyncList;
        listAdapter.adminMode = isAdmin;

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
                intGala.putExtra("isAdmin", isAdmin);
                parent.getContext().startActivity(intGala);

            }
        });

        sectionTask(true).execute("4");

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

package net.scadsdnd.ponygala;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

public class GalleryActivity extends AppCompatActivity implements WebRequest.webUIGalaIf {

    private caheDB dbh;
    private SQLiteDatabase db;
    private List<artRequest> asyncThumbs = new ArrayList<>();
    private boolean isAdmin;
    private String[] catNames = null;
    private String[] catIDs = null;
    private int selCatIDInd;
    private enum myGalaDialogType {CatRename, ArtsMove}
    private int adminOperCount = 0;

    private void loadCategory(int act, String... params){

        WebRequest artWebRq = new WebRequest();

        artWebRq.UIContext = this;
        artWebRq.regGalCb(this);
        artWebRq.pbIndicator = (ProgressBar) findViewById(R.id.pbWaitGal);

        String selDate = getIntent().getStringExtra("catDate");
        String catId = getIntent().getStringExtra("catId");
        switch (act) {
            case 2:
                // Get arts in category (with date, if any)
                if (selDate != null) {
                    artWebRq.execute(String.valueOf(act), catId, selDate);
                } else {
                    artWebRq.execute(String.valueOf(act), catId);
                }
            break;
            case 6:
                // Rename existing category
                artWebRq.execute(String.valueOf(act), params[0], catId);
                break;
            case 7:
                // Delete existing category (and arts to buffer)
                artWebRq.execute(String.valueOf(act), catId);
                break;

            case 8:
                // Move art to new category
                artWebRq.execute(String.valueOf(act), params[0], params[1]);
                break;
            case 9:
                artWebRq.execute(String.valueOf(act), params[0]);
                // Delete art from system
                break;

            case 11:
                // Get list of categories for admin
                artWebRq.execute(String.valueOf(act));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        Toast.makeText(this, getText(R.string.load_start), Toast.LENGTH_LONG).show();

        loadCategory(2);



    }

    public void pArtListLoaded(JSONArray jArr) {

        dbh = new caheDB(this);
        db = dbh.getWritableDatabase();

        dbh.onUpgrade(db, 0, 0);

        ContentValues dbRow = new ContentValues();

        Log.v("!", "Got arts from category");

        String[] artID = new String[jArr.length()];
        String[] artThumb = new String[jArr.length()];
        final String[] artName = new String[jArr.length()];
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
        artData.put("art_id", artID);

        try{

            GridView outGridVW = (GridView) findViewById(R.id.gvArts);
            ListAdapter grAdapt = new artAdapter(this, artName);
            ((artAdapter) grAdapt).allArtInfo = artData;
            ((artAdapter) grAdapt).aL = asyncThumbs;
            ((artAdapter) grAdapt).isAdmin = isAdmin;
            outGridVW.setAdapter(grAdapt);

            outGridVW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Prevent execution of async tasks, after activity closed
                    for (int i=0; i<asyncThumbs.size(); i++  ) {
                        asyncThumbs.get(i).cancel(true);
                    }

                    Intent intFull = new Intent(parent.getContext(), ImageActivity.class);
                    intFull.putExtra("imgMaxInd", artName.length);
                    intFull.putExtra("imgIndex", position);
                    intFull.putExtra("isAdmin", isAdmin);
                    startActivityForResult(intFull, 2);

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }

        if(isAdmin){
            loadCategory(11);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                boolean catChanged = data.getBooleanExtra("artsChanged", false);
                if(catChanged){
                    loadCategory(2);
                }
            }
        }
    }

    @Override
    public void pCatDeletedRenamed(boolean operDel) {

        Toast.makeText(this, R.string.load_conf, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("catChanged", true);
        setResult(RESULT_OK, intent);

        if(operDel){
            onBackPressed();
        }

    }

    @Override
    public void pArtsDeletedMoved(boolean operDel) {

        int msg =0  ;

        if (operDel){
            msg = R.string.load_deleted;
        } else {
            msg = R.string.load_moved;
        }

        adminOperCount--;
        if(adminOperCount==0) {
            Toast.makeText(GalleryActivity.this, msg, Toast.LENGTH_SHORT).show();
            findViewById(R.id.pbWaitGal).setVisibility(View.VISIBLE);
            loadCategory(2);
        }

        Intent intent = new Intent();
        intent.putExtra("catChanged", true);
        setResult(RESULT_OK, intent);
    }


    @Override
    public void pAdminCatsLoaded(JSONArray jArr) {

        Log.v("!","Got Cats list");
        Log.v("JDCL", jArr.toString());

        try {

           catNames = new String[jArr.length()];
           catIDs = new String[jArr.length()];

            JSONObject jData = null;

            for (int i = 0; i < jArr.length(); i++) {

                jData = jArr.getJSONObject(i);

                catNames[i] = jData.getString("cat_name");
                catIDs[i] = jData.getString("cat_id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        for (int i=0; i<asyncThumbs.size(); i++  ) {
            asyncThumbs.get(i).cancel(true);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.galary, menu);

        if (isAdmin){
            menu.findItem(R.id.mRenCat).setVisible(true);
            menu.findItem(R.id.mDelCat).setVisible(true);
            menu.findItem(R.id.mMoveGala).setVisible(true);
            menu.findItem(R.id.mDelGala).setVisible(true);
        } else {
            menu.findItem(R.id.mRenCat).setVisible(false);
            menu.findItem(R.id.mDelCat).setVisible(false);
            menu.findItem(R.id.mMoveGala).setVisible(false);
            menu.findItem(R.id.mDelGala).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void myGalaDialog(final myGalaDialogType galaDialT){

        int dlgTitle = 0;
        int dlgIcon = 0;
        View dlgCont = null;

        switch (galaDialT){
            case ArtsMove:
                dlgTitle = R.string.dlgMoveToTitle;
                dlgIcon = android.R.drawable.ic_menu_today;

                ArrayAdapter<String> optAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catNames);
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
                        selCatIDInd = i;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dlgCont = calDate;

                break;
            case CatRename:
                dlgTitle = R.string.dlgCatRename;
                dlgIcon = android.R.drawable.ic_menu_edit;

                final EditText edtPass = new EditText(this);
                LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                edtPass.setPadding(25,(int) getResources().getDimension(R.dimen.lblSides),
                        25, (int) getResources().getDimension(R.dimen.lblSides));
                edtPass.setLayoutParams(lpP);

                dlgCont = edtPass;
                break;
        }

        final AlertDialog.Builder dlgDateTpl = new AlertDialog.Builder(this);
        dlgDateTpl.setTitle(dlgTitle);
        dlgDateTpl.setIcon(dlgIcon);

        dlgDateTpl.setView(dlgCont);

        final View finalDlgCont = dlgCont;
        dlgDateTpl.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



                switch (galaDialT){
                    case ArtsMove:

                        Toast.makeText(GalleryActivity.this, getString(R.string.load_moving), Toast.LENGTH_SHORT).show();

                        adminOperCount = 0;
                        GridView gridView = (GridView) findViewById(R.id.gvArts);
                        for(int j = 0; j < gridView.getChildCount(); j++) {
                            View vG = gridView.getChildAt(j);
                            CheckBox cbArt = (CheckBox) vG.findViewById(R.id.checkBoxArt);
                            if(cbArt.isChecked()) {

                                String aid = (String) vG.getTag();
                                loadCategory(8, aid, catIDs[selCatIDInd]);

                                TextView artName = (TextView) vG.findViewById(R.id.textTitle);


                                adminOperCount++;

                                Log.v("AID", aid);
                            }

                        }

                        break;

                    case CatRename:

                        Toast.makeText(GalleryActivity.this, R.string.load_proc, Toast.LENGTH_SHORT).show();
                        loadCategory(6, ((TextView) finalDlgCont).getText().toString());

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mReloadGala:
                    loadCategory(2);
                break;

            case R.id.mRenCat:
                    myGalaDialog(myGalaDialogType.CatRename);
                break;

            case R.id.mDelCat:
                    loadCategory(7);
                break;

            case R.id.mMoveGala:
                    myGalaDialog(myGalaDialogType.ArtsMove);
                 break;

            case R.id.mDelGala:

                Toast.makeText(GalleryActivity.this, getString(R.string.load_deleting), Toast.LENGTH_SHORT).show();

                adminOperCount = 0;
                GridView gridView = (GridView) findViewById(R.id.gvArts);
                for(int j = 0; j < gridView.getChildCount(); j++) {
                    View vG = gridView.getChildAt(j);
                    CheckBox cbArt = (CheckBox) vG.findViewById(R.id.checkBoxArt);
                    if(cbArt.isChecked()) {

                        String aid = (String) vG.getTag();
                        loadCategory(9, aid);

                        TextView artName = (TextView) vG.findViewById(R.id.textTitle);


                        adminOperCount++;

                        Log.v("AID", aid);
                    }

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
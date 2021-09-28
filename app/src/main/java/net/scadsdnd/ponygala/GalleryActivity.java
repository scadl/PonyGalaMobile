package net.scadsdnd.ponygala;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
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

    private caheDB dbh;
    private SQLiteDatabase db;
    private List<artRequest> asyncThumbs = new ArrayList<>();
    private boolean isAdmin;

    private void loadCategory(int act, String... params){

        WebRequest artWebRq = new WebRequest();

        artWebRq.UIContext = this;
        artWebRq.regGalCb(this);
        artWebRq.pbIndicator = (ProgressBar) findViewById(R.id.pbWaitGal);

        String selDate = getIntent().getStringExtra("catDate");
        String catId = getIntent().getStringExtra("catId");
        switch (act) {
            case 2:
                if (selDate != null) {
                    artWebRq.execute(String.valueOf(act), catId, selDate);
                } else {
                    artWebRq.execute(String.valueOf(act), catId);
                }
            break;
            case 6:
                artWebRq.execute(String.valueOf(act), params[0], catId);
                break;
            case 7:
                artWebRq.execute(String.valueOf(act), catId);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new caheDB(this);
        db = dbh.getWritableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        Toast.makeText(this, getText(R.string.load_start), Toast.LENGTH_LONG);

        loadCategory(2);

        dbh.onUpgrade(db, 0, 0);

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

        try{

            GridView outGridVW = (GridView) findViewById(R.id.gvArts);
            ListAdapter grAdapt = new artAdapter(this, artName, artData, asyncThumbs);
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
                    parent.getContext().startActivity(intFull);

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void pCatRenamed(JSONArray jArr) {
        Log.v("CR", jArr.toString());
        Toast.makeText(this, R.string.load_conf, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("catChanged", true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void pCatDeleted(JSONArray jArr) {
        Log.v("CD", jArr.toString());

        Intent intent = new Intent();
        intent.putExtra("catChanged", true);
        setResult(RESULT_OK, intent);

        onBackPressed();
    }

    @Override
    public void pArtsMoved(JSONArray jArr) {

    }

    @Override
    public void pArtsDeleted(JSONArray jArr) {

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mReloadGala:
                loadCategory(2);
                break;

            case R.id.mRenCat:

                final AlertDialog.Builder dlgDateTpl = new AlertDialog.Builder(this);
                dlgDateTpl.setTitle(R.string.dlgCatRename);
                dlgDateTpl.setIcon(android.R.drawable.ic_menu_edit);

                final EditText edtPass = new EditText(this);
                LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                edtPass.setPadding(25,(int) getResources().getDimension(R.dimen.lblSides),
                        25, (int) getResources().getDimension(R.dimen.lblSides));
                edtPass.setLayoutParams(lpP);

                dlgDateTpl.setView(edtPass);

                dlgDateTpl.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(GalleryActivity.this, R.string.load_proc, Toast.LENGTH_SHORT).show();
                        loadCategory(6, edtPass.getText().toString());

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

            case R.id.mDelCat:
                loadCategory(7);
                break;

            case R.id.mMoveGala:

                 break;

            case R.id.mDelGala:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
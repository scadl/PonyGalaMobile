package net.scadsdnd.ponygala;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class artAdapter extends ArrayAdapter<String> {

    public Map<String, String[]> allArtInfo;
    public List<artRequest> aL;
    public boolean isAdmin;

    public artAdapter(Context context, String[] objects) {
        super(context, R.layout.art_entry, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.art_entry, parent, false);

        TextView tvAuthor = (TextView) view.findViewById(R.id.textTitle);
        tvAuthor.setText(getItem(position));

        if (isAdmin) {
            final CheckBox chkArt = (CheckBox) view.findViewById(R.id.checkBoxArt);
            chkArt.setVisibility(View.VISIBLE);
        }

        view.setTag(this.allArtInfo.get("art_id")[position]);

        artRequest utilAg = new artRequest();
        utilAg.retryLoad = false;
        utilAg.outputImgView = new ImageView[] {(ImageView) view.findViewById(R.id.artTumbVw)};
        utilAg.outputProgress = new ProgressBar[] {(ProgressBar) view.findViewById(R.id.pbLoadArt)};
        utilAg.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this.allArtInfo.get("art_tb")[position]);
        this.aL.add(utilAg);

        return view;
    }

}

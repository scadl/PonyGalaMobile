package net.scadsdnd.ponygala;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

public class artAdapter extends ArrayAdapter<String> {

    public Map<String, String[]> allArtInfo;

    public artAdapter(Context context, String[] objects, Map<String, String[]> other_objects) {
        super(context, R.layout.art_entry, objects);
        this.allArtInfo = other_objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.art_entry, parent, false);

        TextView tvAuthor = (TextView) view.findViewById(R.id.textTitle);
        tvAuthor.setText(getItem(position));

        artRequest utilAg = new artRequest();
        utilAg.outputImgView = new ImageView[] {(ImageView) view.findViewById(R.id.artTumbVw)};
        utilAg.outputProgress = new ProgressBar[] {(ProgressBar) view.findViewById(R.id.pbLoadArt)};
        utilAg.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this.allArtInfo.get("art_tb")[position], "gala");

        return view;
    }
}

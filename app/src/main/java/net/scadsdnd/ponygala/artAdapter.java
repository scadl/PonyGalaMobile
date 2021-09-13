package net.scadsdnd.ponygala;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        helpersAG utilAg = new helpersAG();

        ImageView imgThumb = (ImageView) view.findViewById(R.id.artTumbVw);
        TextView tvAuthor = (TextView) view.findViewById(R.id.textTitle);

        imgThumb.setImageBitmap(utilAg.loadImage(this.allArtInfo.get("art_tb")[position]));
        tvAuthor.setText(getItem(position));

        return view;
    }
}

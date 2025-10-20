package com.phllp.indiefied.ui.search;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.phllp.indiefied.R;
import com.phllp.indiefied.model.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    public interface OnItemClick { void onClick(Track t); }

    private final LayoutInflater inflater;
    private final OnItemClick onItemClick;
    private final List<Track> data = new ArrayList<>();

    public SearchAdapter(Context ctx, OnItemClick onItemClick) {
        this.inflater = LayoutInflater.from(ctx);
        this.onItemClick = onItemClick;
    }

    public void submit(List<Track> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return data.size(); }
    @Override public Object getItem(int position) { return data.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        VH h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_track_search, parent, false);
            h = new VH(convertView);
            convertView.setTag(h);
        } else {
            h = (VH) convertView.getTag();
        }
        Track t = data.get(pos);
        h.title.setText(t.getTitle());
        h.subtitle.setText(t.getArtist().getName());

        File coversDir = new File(parent.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers");
        String coverName = (t.getAlbum() != null) ? t.getAlbum().getCover() : null;
        File coverFile = (coverName != null) ? new File(coversDir, coverName) : null;

        // Carrega com Glide (com placeholder e cantos levemente arredondados)
        Glide.with(convertView)
                .load(coverFile != null && coverFile.exists() ? coverFile : R.drawable.ic_album_placeholder)
                .thumbnail(0.25f) // preview rápido
                .apply(new RequestOptions()
                        .override(200, 200) // evita carregar full (suficiente p/ 56dp @x3 densidade)
                        .transform(new RoundedCorners(12)))
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(h.cover);

        convertView.setOnClickListener(v -> onItemClick.onClick(t));
        return convertView;
    }

    static class VH {
        TextView title, subtitle;
        ImageView cover;
        VH(View v) {
            cover = v.findViewById(R.id.ivCover);
            title = v.findViewById(R.id.tvTitle);
            subtitle = v.findViewById(R.id.tvSubtitle);
        }
    }
}

package com.phllp.indiefied.ui.home;

import android.os.Environment;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.phllp.indiefied.R;
import com.phllp.indiefied.model.AlbumItem;

import java.io.File;
import java.util.*;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.VH> {

    public interface OnClick { void onClick(AlbumItem a); }

    private final List<AlbumItem> data = new ArrayList<>();
    private final OnClick onClick;

    public AlbumsAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void submit(List<AlbumItem> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album_grid, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH h, int position) {
        AlbumItem a = data.get(position);
        h.tvName.setText(a.getTitle());

        // Força 1:1: altura = metade da largura do RecyclerView
        View rv = (View) h.itemView.getParent();
        if (rv != null) {
            int totalW = rv.getWidth();
            if (totalW > 0) {
                int itemW = totalW / 2 - h.itemView.getPaddingLeft() - h.itemView.getPaddingRight();
                ViewGroup.LayoutParams lp = h.ivCover.getLayoutParams();
                lp.height = itemW;
                h.ivCover.setLayoutParams(lp);
            }
        }

        // Capa local (app-specific Pictures/covers/<coverName>)
        File coversDir = new File(h.itemView.getContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers");
        File coverFile = (a.getCover() != null) ? new File(coversDir, a.getCover()) : null;

        Glide.with(h.itemView)
                .load(coverFile != null && coverFile.exists() ? coverFile : R.drawable.ic_album_placeholder)
                .apply(new RequestOptions().centerCrop())
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(h.ivCover);

        h.itemView.setOnClickListener(v -> onClick.onClick(a));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCover; TextView tvName;
        VH(View v) { super(v);
            ivCover = v.findViewById(R.id.ivCover);
            tvName  = v.findViewById(R.id.tvName);
        }
    }
}

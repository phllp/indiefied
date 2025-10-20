package com.phllp.indiefied.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phllp.indiefied.R;
import com.phllp.indiefied.model.PlaylistDoc;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.VH> {

    public interface OnClick { void onClick(PlaylistDoc p); }

    private final List<PlaylistDoc> data = new ArrayList<>();
    private final OnClick onClick;
    private final String coverUrl; // imagem padrão via URL

    public PlaylistsAdapter(OnClick onClick, String coverUrl) {
        this.onClick = onClick;
        this.coverUrl = coverUrl;
    }

    public void submit(List<PlaylistDoc> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist_grid, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH h, int position) {
        PlaylistDoc p = data.get(position);
        h.tvName.setText(p.getName());

        // força 1:1: altura = metade da largura do RecyclerView (2 colunas)
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

        Glide.with(h.itemView)
                .load(coverUrl) // imagem padrão por URL
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .centerCrop()
                .into(h.ivCover);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCover; TextView tvName;
        VH(View v) {
            super(v);
            ivCover = v.findViewById(R.id.ivCover);
            tvName = v.findViewById(R.id.tvName);
        }
    }
}

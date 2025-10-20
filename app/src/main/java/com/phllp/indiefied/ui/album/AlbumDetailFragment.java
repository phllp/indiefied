package com.phllp.indiefied.ui.album;

import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.phllp.indiefied.R;
import com.phllp.indiefied.databinding.FragmentAlbumDetailBinding;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.ui.search.SearchAdapter;
import com.phllp.indiefied.viewmodel.AlbumDetailViewModel;
import com.phllp.indiefied.viewmodel.PlayerViewModel;

import java.io.File;

public class AlbumDetailFragment extends Fragment {

    private FragmentAlbumDetailBinding binding;
    private AlbumDetailViewModel vm;
    private PlayerViewModel playerVM;
    private SearchAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAlbumDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(AlbumDetailViewModel.class);
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        adapter = new SearchAdapter(requireContext(), this::onTrackClick);
        binding.listTracks.setAdapter(adapter);

        // Args vindos da Home
        String albumTitle = getArguments() != null ? getArguments().getString("albumTitle") : null;
        String albumCover = getArguments() != null ? getArguments().getString("albumCover") : null;
        String artistName = getArguments() != null ? getArguments().getString("artistName") : null;

        // Header: capa + textos
        binding.tvAlbumTitle.setText(albumTitle != null ? albumTitle : "");
        binding.tvArtistName.setText(artistName != null ? artistName : "");

        File coversDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers");
        File coverFile = (albumCover != null) ? new File(coversDir, albumCover) : null;

        Glide.with(this)
                .load(coverFile != null && coverFile.exists() ? coverFile : R.drawable.ic_album_placeholder)
                .apply(new RequestOptions().centerCrop())
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(binding.ivCover);

        // Observers
        vm.isLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE));
        vm.getTracks().observe(getViewLifecycleOwner(), list -> adapter.submit(list));

        // Carrega faixas do álbum
        vm.load(albumTitle);
    }

    private void onTrackClick(Track t) {
        boolean ok = playerVM.startPlayback(t);
        if (!ok) {
            Toast.makeText(requireContext(), "Arquivo não encontrado para id: " + t.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

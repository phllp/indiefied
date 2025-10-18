package com.phllp.indiefied.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.phllp.indiefied.databinding.FragmentPlayerBinding;
import com.phllp.indiefied.util.SimpleSeekBarChange;
import com.phllp.indiefied.viewmodel.PlayerViewModel;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private PlayerViewModel viewModel;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        // Observa faixa atual
        viewModel.getCurrentTrack().observe(getViewLifecycleOwner(), track -> {
            binding.tvTitle.setText(track.getTitle());
            binding.tvArtist.setText(track.getArtist());
//            @todo implementar o carregamento de imagem de capa do album, por hora apenas o placeholder
//            Glide.with(this).load(track.getCoverUrl()).into(binding.ivCover);
        });

        // Controles
        binding.btnPlayPause.setOnClickListener(v -> viewModel.togglePlay());
        binding.btnNext.setOnClickListener(v -> viewModel.next());
        binding.btnPrev.setOnClickListener(v -> viewModel.prev());
        binding.seekBar.setOnSeekBarChangeListener(new SimpleSeekBarChange(pos -> viewModel.seekTo(pos)));
        binding.toggleShuffle.setOnCheckedChangeListener((b, checked) -> viewModel.setShuffle(checked));
    }
}

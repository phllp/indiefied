package com.phllp.indiefied.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.phllp.indiefied.databinding.FragmentPlayerBinding;
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

        // Controles
        binding.btnPlayPause.setOnClickListener(v -> playMusic());
    }

    private void playMusic() {
        boolean found = viewModel.playFromAppMusicDir("morango_do_nordeste.mp3");
        if (!found) {
             Toast.makeText(requireContext(),"Arquivo não encontrado",Toast.LENGTH_SHORT).show();
        }

    }
}

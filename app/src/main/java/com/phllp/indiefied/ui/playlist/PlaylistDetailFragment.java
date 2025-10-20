package com.phllp.indiefied.ui.playlist;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.phllp.indiefied.databinding.FragmentPlaylistDetailBinding;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.ui.search.SearchAdapter;
import com.phllp.indiefied.viewmodel.PlayerViewModel;
import com.phllp.indiefied.viewmodel.PlaylistDetailViewModel;

public class PlaylistDetailFragment extends Fragment {

    private FragmentPlaylistDetailBinding binding;
    private PlaylistDetailViewModel vm;
    private PlayerViewModel playerVM;
    private SearchAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(PlaylistDetailViewModel.class);
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        adapter = new SearchAdapter(requireContext(), this::onTrackClick);
        binding.listTracks.setAdapter(adapter);

        // args
        String playlistName = getArguments() != null ? getArguments().getString("playlistName") : null;
        vm.getTitle().observe(getViewLifecycleOwner(), t -> binding.tvHeader.setText(t));
        vm.isLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE));
        vm.getTracks().observe(getViewLifecycleOwner(), list -> adapter.submit(list));

        vm.start(playlistName);
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

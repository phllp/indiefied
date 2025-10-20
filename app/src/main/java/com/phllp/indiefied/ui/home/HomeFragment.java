package com.phllp.indiefied.ui.home;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.phllp.indiefied.R;
import com.phllp.indiefied.databinding.FragmentHomeBinding;
import com.phllp.indiefied.model.AlbumItem;
import com.phllp.indiefied.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel vm;
    private AlbumsAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.rvAlbums.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new AlbumsAdapter(this::onAlbumClick);
        binding.rvAlbums.setAdapter(adapter);

        vm.isLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE));
        vm.getAlbums().observe(getViewLifecycleOwner(), list -> adapter.submit(list));

        vm.load();
    }

    private void onAlbumClick(AlbumItem a) {
        Bundle b = new Bundle();
        b.putString("albumTitle", a.getTitle());
        b.putString("albumCover", a.getCover());
        b.putString("artistName", a.getArtistName());
        androidx.navigation.fragment.NavHostFragment
                .findNavController(this)
                .navigate(R.id.albumDetailFragment, b);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

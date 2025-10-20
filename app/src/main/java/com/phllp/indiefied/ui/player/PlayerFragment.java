package com.phllp.indiefied.ui.player;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.phllp.indiefied.R;
import com.phllp.indiefied.databinding.FragmentPlayerBinding;
import com.phllp.indiefied.model.PlaylistDoc;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.repository.UserRepository;
import com.phllp.indiefied.viewmodel.FavoritesViewModel;
import com.phllp.indiefied.viewmodel.PlayerViewModel;
import com.phllp.indiefied.viewmodel.ProfileViewModel;

import java.io.File;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private PlayerViewModel playerVM;
    private FavoritesViewModel favVM;
    private ProfileViewModel profileVM; // para ler playlists existentes
    private final UserRepository userRepo = new UserRepository();
    private static final String USER_ID = "admin";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        favVM    = new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        profileVM = new ViewModelProvider(this).get(ProfileViewModel.class);

        // observar a track atual e repassar para favVM
        playerVM.getCurrentTrack().observe(getViewLifecycleOwner(), t -> {
            bindTrack(t);
            favVM.watchTrack(t);
        });

        // observar estado de favorito para trocar ícone
        favVM.isFavorite().observe(getViewLifecycleOwner(), fav -> {
            binding.btnFavorite.setImageResource(Boolean.TRUE.equals(fav)
                    ? R.drawable.ic_favorite_fill
                    : R.drawable.ic_favorite_border);
        });

        // clique de toggle
        binding.btnFavorite.setOnClickListener(v -> {
            Track t = playerVM.getCurrentTrack().getValue();
            favVM.toggleFavorite(t);
        });

        binding.btnAddToPlaylist.setOnClickListener(v -> showAddToPlaylistDialog());


        // escopo da para compartilhar o estado global
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        // Observa a faixa atual e atualiza UI
        playerVM.getCurrentTrack().observe(getViewLifecycleOwner(), this::bindTrack);

        // Observa play/pause para trocar ícone
        playerVM.getIsPlaying().observe(getViewLifecycleOwner(), playing -> {
            binding.btnPlayPause.setImageResource(
                    (playing != null && playing) ? R.drawable.ic_pause : R.drawable.ic_play
            );
        });

        // Botões básicos
        binding.btnPlayPause.setOnClickListener(v -> playerVM.togglePlayPause());
//        binding.btnPrev.setOnClickListener(v -> {  });
//        binding.btnNext.setOnClickListener(v -> {  });

        // Se já havia uma música antes de abrir, bind inicial
        Track t = playerVM.getCurrentTrack().getValue();
        if (t != null) bindTrack(t);
    }

    private void bindTrack(Track t) {
        if (t == null) {
            ;
        }
        binding.tvTitle.setText(t.getTitle() != null ? t.getTitle() : "");
        String artist = (t.getArtist() != null && t.getArtist().getName() != null) ? t.getArtist().getName() : "";
        String album  = (t.getAlbum()  != null && t.getAlbum().getTitle() != null)  ? t.getAlbum().getTitle()  : "";
        binding.tvArtist.setText(artist);

        // Carregar capa local
        String coverName = (t.getAlbum() != null) ? t.getAlbum().getCover() : null;
        File coversDir = new File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "covers");
        File coverFile = (coverName != null) ? new File(coversDir, coverName) : null;

        Glide.with(this)
                .load(coverFile != null && coverFile.exists() ? coverFile : R.drawable.ic_album_placeholder)
                .apply(new RequestOptions().centerCrop())
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(binding.ivCover);
    }

    private void showAddToPlaylistDialog() {
        Track t = playerVM.getCurrentTrack().getValue();
        if (t == null || t.getId() == null || t.getId().isEmpty()) {
            Toast.makeText(requireContext(), "Nenhuma música em reprodução", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obter playlists atuais e filtrar "Favorites"
        java.util.List<PlaylistDoc> current = profileVM.getPlaylists().getValue();
        java.util.List<String> names = new java.util.ArrayList<>();
        if (current != null) {
            for (PlaylistDoc p : current) {
                if (p != null && p.getName() != null && !"Favorites".equals(p.getName())) {
                    names.add(p.getName());
                }
            }
        }

        if (names.isEmpty()) {
            Toast.makeText(requireContext(), "Crie uma playlist primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] items = names.toArray(new CharSequence[0]);
        new AlertDialog.Builder(requireContext())
                .setTitle("Adicionar à playlist")
                .setItems(items, (dialog, which) -> {
                    String chosen = names.get(which); // 'which' é int (índice do item clicado)
                    userRepo.addTrackToPlaylist(
                            USER_ID, chosen, t.getId(),
                            () -> Toast.makeText(requireContext(),
                                    "Adicionada em \"" + chosen + "\"", Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(requireContext(),
                                    "Falha ao adicionar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                    // dialog.dismiss(); // opcional — o setItems já fecha automaticamente
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

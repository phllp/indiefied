package com.phllp.indiefied.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.phllp.indiefied.R;
import com.phllp.indiefied.databinding.ActivityMainBinding;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.viewmodel.PlayerViewModel;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PlayerViewModel playerVM;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nav
        NavHostFragment navHost =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController = navHost.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);


        binding.bottomNav.setOnItemSelectedListener(item -> {
            int destId = item.getItemId();

            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == destId) {
                return true;
            }

            NavOptions opts = new NavOptions.Builder()
                    // limpa tudo acima da raiz do grafo (remove player/trilhas supérfluas)
                    .setPopUpTo(navController.getGraph().getId(), false)
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .build();

            try {
                navController.navigate(destId, null, opts);
                return true;
            } catch (IllegalArgumentException ignore) {
                return false;
            }
        });


        // VM global
        playerVM = new ViewModelProvider(this).get(PlayerViewModel.class);


        // Listeners do mini-player
        binding.miniPlayer.btnMiniPlayPause.setOnClickListener(v -> playerVM.togglePlayPause());
        binding.miniPlayer.getRoot().setOnClickListener(v -> {
            // não navegar se já está no player
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == R.id.playerFragment) return;

            NavOptions opts = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .build();
            navController.navigate(R.id.playerFragment, null, opts);
        });

        // Observa track atual e mostra/oculta o mini
        playerVM.getCurrentTrack().observe(this, track -> {
            boolean show = track != null && !isInPlayerScreen();
            binding.miniPlayer.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
            if (track != null) { // atualiza capa/título/artista
                bindMini(track);
            }
        });

        // Observa play/pause para trocar ícone
        playerVM.getIsPlaying().observe(this, playing -> {
            int icon = (playing != null && playing) ? R.drawable.ic_pause : R.drawable.ic_play;
            binding.miniPlayer.btnMiniPlayPause.setImageResource(icon);
        });

        // Esconder/mostrar quando mudar de destino
        navController.addOnDestinationChangedListener((c, d, a) -> {
            boolean inPlayer = isInPlayerScreen();
            boolean hasTrack = playerVM.getCurrentTrack().getValue() != null;
            binding.miniPlayer.getRoot().setVisibility((hasTrack && !inPlayer) ? View.VISIBLE : View.GONE);
        });
    }

    private boolean isInPlayerScreen() {
        return navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == R.id.playerFragment;
    }

    private void bindMini(Track t) {
        binding.miniPlayer.tvMiniTitle.setText(t.getTitle() != null ? t.getTitle() : "");

        String artist = (t.getArtist() != null && t.getArtist().getName() != null) ? t.getArtist().getName() : "";
        String album  = (t.getAlbum()  != null && t.getAlbum().getTitle() != null)  ? t.getAlbum().getTitle()  : "";
        binding.miniPlayer.tvMiniSubtitle.setText(artist.isEmpty() ? album : (artist + " • " + album));

        // capa local
        String coverName = (t.getAlbum() != null) ? t.getAlbum().getCover() : null;
        File coversDir = new File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "covers");
        File coverFile = (coverName != null) ? new File(coversDir, coverName) : null;

        Glide.with(this)
                .load(coverFile != null && coverFile.exists() ? coverFile : R.drawable.ic_album_placeholder)
                .apply(new RequestOptions().override(200, 200).centerCrop())
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(binding.miniPlayer.ivMiniCover);
    }
}

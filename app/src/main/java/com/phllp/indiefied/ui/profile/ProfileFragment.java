// com.phllp.indiefied.ui.profile.ProfileFragment
package com.phllp.indiefied.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.phllp.indiefied.databinding.FragmentProfileBinding;
import com.phllp.indiefied.model.PlaylistDoc;
import com.phllp.indiefied.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel vm;
    private PlaylistsAdapter adapter;

    // URL de imagem padrão para as playlists
    private static final String DEFAULT_PLAYLIST_IMAGE =
            "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Recycler: grid 2 colunas
        binding.rvPlaylists.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new PlaylistsAdapter(this::onPlaylistClick, DEFAULT_PLAYLIST_IMAGE);
        binding.rvPlaylists.setAdapter(adapter);

        // Observa dados
        vm.getUserName().observe(getViewLifecycleOwner(), name -> {
            binding.tvGreeting.setText("Olá, " + (name != null ? name : "Usuário"));
        });
        vm.getPlaylists().observe(getViewLifecycleOwner(), list -> adapter.submit(list));

        // FAB criar playlist
        binding.fabAdd.setOnClickListener(v -> showCreateDialog());
    }

    private void showCreateDialog() {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        new AlertDialog.Builder(requireContext())
                .setTitle("Nova playlist")
                .setMessage("Digite o nome da playlist")
                .setView(input)
                .setPositiveButton("Criar", (d, w) -> {
                    String name = input.getText() != null ? input.getText().toString().trim() : "";
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "Nome inválido", Toast.LENGTH_SHORT).show();
                    } else {
                        vm.createPlaylist(name);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void onPlaylistClick(PlaylistDoc p) {
        Bundle b = new Bundle();
        b.putString("playlistName", p.getName());
        androidx.navigation.fragment.NavHostFragment
                .findNavController(this)
                .navigate(com.phllp.indiefied.R.id.playlistDetailFragment, b);
    }


}

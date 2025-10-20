package com.phllp.indiefied.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.phllp.indiefied.databinding.FragmentSearchBinding;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.viewmodel.PlayerViewModel;
import com.phllp.indiefied.viewmodel.SearchViewModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel searchVM;
    private PlayerViewModel playerVM;
    private SearchAdapter adapter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(requireContext().getApplicationContext());
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchVM = new ViewModelProvider(this).get(SearchViewModel.class);
        playerVM  = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        adapter = new SearchAdapter(requireContext(), this::onTrackClick);
        binding.listResults.setAdapter(adapter);

        // Observa resultados e atualiza lista
        searchVM.getResults().observe(getViewLifecycleOwner(), list -> {
            binding.progress.setVisibility(View.GONE);
            adapter.submit(list);
        });

        // Busca inicial (listar tudo)
        binding.progress.setVisibility(View.VISIBLE);
        searchVM.loadAllTracks();

        // Filtro ao digitar
        binding.etQuery.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                searchVM.applyFilter(s != null ? s.toString() : "");
            }
        });
    }

    private void onTrackClick(Track t) {
        // Tocar pelo id (id = nome base do arquivo salvo em /files/Music/)
        boolean ok = playerVM.startPlayback(t);
        if (!ok) {
            Toast.makeText(requireContext(), "Arquivo não encontrado para id: " + t.getId(), Toast.LENGTH_SHORT).show();
        }
    }
}

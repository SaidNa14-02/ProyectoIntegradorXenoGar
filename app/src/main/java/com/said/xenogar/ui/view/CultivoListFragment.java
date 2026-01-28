package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.databinding.FragmentCultivoListBinding;
import com.said.xenogar.ui.adapter.CultivoListAdapter;
import com.said.xenogar.ui.viewmodel.CultivoListViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CultivoListFragment extends Fragment implements CultivoListAdapter.OnItemClickListener {

    private CultivoListViewModel viewModel;
    private FragmentCultivoListBinding binding;
    private CultivoListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CultivoListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCultivoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        adapter.setOnItemClickListener(this);
        viewModel.getListaCultivos().observe(getViewLifecycleOwner(), cultivos -> {
            if (cultivos != null) {
                adapter.submitList(cultivos);
            }
        });

        binding.fabAddCultivo.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, CultivoFormFragment.newInstance(null))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        adapter = new CultivoListAdapter();
        binding.recyclerViewCultivos.setAdapter(adapter);
        binding.recyclerViewCultivos.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickCultivo(Cultivo cultivo) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CultivoPagerFragment.newInstance(cultivo.getId()))
                .addToBackStack(null)
                .commit();
    }
}

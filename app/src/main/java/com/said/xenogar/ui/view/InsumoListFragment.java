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
import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.databinding.FragmentInsumoListBinding;
import com.said.xenogar.ui.adapter.InsumoListAdapter;
import com.said.xenogar.ui.viewmodel.InsumoListViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InsumoListFragment extends Fragment implements InsumoListAdapter.OnItemClickListener {

    private InsumoListViewModel viewModel;
    private FragmentInsumoListBinding binding;
    private InsumoListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InsumoListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsumoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        adapter.setOnItemClickListener(this);
        viewModel.getAllInsumos().observe(getViewLifecycleOwner(), insumos -> {
            if (insumos != null) {
                adapter.submitList(insumos);
            }
        });

        binding.fabAddInsumo.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, InsumoFormFragment.newInstance(null))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        adapter = new InsumoListAdapter();
        binding.recyclerViewInsumos.setAdapter(adapter);
        binding.recyclerViewInsumos.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickInsumo(Insumo insumo) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, InsumoFormFragment.newInstance(insumo.getId()))
                .addToBackStack(null)
                .commit();
    }
}
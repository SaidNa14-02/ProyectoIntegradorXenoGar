package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // For placeholder click actions

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager; // Import needed

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Actividad; // Import needed
import com.said.xenogar.databinding.FragmentActividadesBinding;
import com.said.xenogar.ui.adapter.ActividadListAdapter; // Import needed
import com.said.xenogar.ui.viewmodel.ActividadesListViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActividadesFragment extends Fragment implements ActividadListAdapter.OnItemClickListener {

    public static final String ARG_CULTIVO_ID = "cultivo_id";
    private ActividadesListViewModel viewModel;
    private FragmentActividadesBinding binding;
    private Long cultivoId;
    private ActividadListAdapter adapter; // Declare adapter

    public static ActividadesFragment newInstance(Long cultivoId) {
        ActividadesFragment fragment = new ActividadesFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CULTIVO_ID, cultivoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cultivoId = getArguments().getLong(ARG_CULTIVO_ID);
        }
        viewModel = new ViewModelProvider(this).get(ActividadesListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActividadesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        adapter.setOnItemClickListener(this);

        viewModel.getActividades().observe(getViewLifecycleOwner(), actividades -> {
            if (actividades != null) {
                adapter.submitList(actividades);
                binding.emptyListMessage.setVisibility(actividades.isEmpty() ? View.VISIBLE : View.GONE);
                binding.recyclerViewActividades.setVisibility(actividades.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        binding.fabAddActividad.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ActividadFormFragment.newInstance(cultivoId, null))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        adapter = new ActividadListAdapter();
        binding.recyclerViewActividades.setAdapter(adapter);
        binding.recyclerViewActividades.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear binding for memory cleanup
    }

    @Override
    public void onItemClickActividad(Actividad actividad) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ActividadFormFragment.newInstance(actividad.getCultivoId(), actividad.getId()))
                .addToBackStack(null)
                .commit();
    }
}
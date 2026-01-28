package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.said.xenogar.R;
import com.said.xenogar.databinding.FragmentCultivoDetailBinding;
import com.said.xenogar.ui.viewmodel.CultivoDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CultivoDetailFragment extends Fragment {
    public static final String ARG_CULTIVO_ID = "cultivoId";
    private CultivoDetailViewModel viewModel;
    private FragmentCultivoDetailBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CultivoDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCultivoDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initUI() {
        viewModel.getDetalleCultivo().observe(getViewLifecycleOwner(), cultivo -> {
            if (cultivo != null) {
                binding.textViewDetailCultivo.setText(cultivo.getNombre());
                binding.textViewExistenciasCultivo.setText(String.valueOf(cultivo.getExistencias()));
                binding.tipoValue.setText(cultivo.getTipo().toString());
                binding.fechaInicioValue.setText(formatDate(cultivo.getFechaInicio()));

                if (cultivo.getFechaFinalizacion() > 0) {
                    binding.proxCosechaValue.setText(formatDate(cultivo.getFechaFinalizacion()));
                } else {
                    binding.proxCosechaValue.setText("No definida");
                }

                binding.textViewCultivoDescription.setText(cultivo.getDescripcion());
            }
        });
    }

    private void initListeners() {
        binding.imageButtonAumentarExistencias.setOnClickListener(v -> {
            viewModel.increaseStock();
        });

        binding.imageButtonDisminuirExistencias.setOnClickListener(v -> {
            viewModel.decreaseStock();
        });

        binding.editCultivoButton.setOnClickListener(v -> {
            if (viewModel.getDetalleCultivo().getValue() != null) {
                Long cultivoId = viewModel.getDetalleCultivo().getValue().getId();
                // Usamos requireActivity().getSupportFragmentManager() porque este fragmento está anidado
                // y el contenedor fragment_container pertenece al layout de la Activity.
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CultivoFormFragment.newInstance(cultivoId))
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.deleteCultivoButton.setOnClickListener(v -> {
            if(viewModel.getDetalleCultivo().getValue() != null) {
                viewModel.deleteCultivo(viewModel.getDetalleCultivo().getValue());
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private String formatDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }

    public static CultivoDetailFragment newInstance(Long cultivoId){
        CultivoDetailFragment fragment = new CultivoDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CULTIVO_ID, cultivoId);
        fragment.setArguments(args);
        return fragment;
    }
}

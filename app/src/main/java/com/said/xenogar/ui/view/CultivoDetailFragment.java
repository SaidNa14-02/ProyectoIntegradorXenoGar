package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

// import com.bumptech.glide.Glide; // Descomentar si usas Glide
import com.said.xenogar.R;
import com.said.xenogar.databinding.FragmentCultivoDetailBinding;
import com.said.xenogar.ui.viewmodel.CultivoDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CultivoDetailFragment extends Fragment {
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
                binding.proxCosechaValue.setText(formatDate(cultivo.getFechaFinalizacion()));
                binding.textViewCultivoDescription.setText(cultivo.getDescripcion());

                // Actualizar barra de progreso y porcentaje
                // Asumiendo que tu modelo de cultivo tiene un método getProgreso()
                // int progress = cultivo.getProgreso();
                // binding.cultivoProgressBar.setProgress(progress);
                // binding.textViewProgressPercentage.setText(getString(R.string.percentage_format, progress));


                // Cargar la imagen del cultivo usando una librería como Glide
                // Asumiendo que tu modelo tiene un método getImageUrl()
                // Glide.with(this)
                //      .load(cultivo.getImageUrl())
                //      .into(binding.imageViewDetailCultivo);
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

        binding.imageButtonEditCultivoImage.setOnClickListener(v -> {
            // viewModel.onEditImage();
        });
    }

    private String formatDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }
}

package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.databinding.FragmentCultivoFormBinding;
import com.said.xenogar.ui.viewmodel.CultivoFormViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CultivoFormFragment extends Fragment {
    public static final String ARG_CULTIVO_ID = "cultivo_id";

    private FragmentCultivoFormBinding binding;
    private CultivoFormViewModel viewModel;

    public static CultivoFormFragment newInstance(Long cultivoId) {
        CultivoFormFragment fragment = new CultivoFormFragment();
        Bundle args = new Bundle();
        if (cultivoId != null) {
            args.putLong("cultivo_id", cultivoId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CultivoFormViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCultivoFormBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTipoCultivoDropdown();
        initListeners();
        initObservers();

        if (getArguments() != null && getArguments().containsKey(ARG_CULTIVO_ID)) {
            Long cultivoId = getArguments().getLong(ARG_CULTIVO_ID);
            viewModel.cargarCultivo(cultivoId);
            binding.submitCultivoButton.setText("Actualizar");
            binding.formHeader.setText("Actualizar datos del cultivo");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupTipoCultivoDropdown() {
        ArrayAdapter<Cultivo.TipoCultivo> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Cultivo.TipoCultivo.values()
        );
        binding.autocompleteTipoCultivo.setAdapter(adapter);
    }

    private void initListeners() {
        binding.submitCultivoButton.setOnClickListener(v -> viewModel.guardarCultivo());

        binding.inputCultivoName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.nombre.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.autocompleteTipoCultivo.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Cultivo.TipoCultivo) {
                viewModel.tipo.setValue(((Cultivo.TipoCultivo) item).name());
            }
        });

        binding.inputCultivoDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(new Date(selection));
                binding.inputCultivoDate.setText(formattedDate);
                viewModel.fecha.setValue(formattedDate);
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        binding.inputCultivoDateEnd.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de finalización")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(new Date(selection));
                binding.inputCultivoDateEnd.setText(formattedDate);
                viewModel.fechaFin.setValue(formattedDate);
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER_FIN");
        });

        binding.inputCultivoExistencias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.existencias.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.inputCultivoDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.descripcion.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.inputCultivoDateEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.fechaFin.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initObservers() {
        viewModel.nombre.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputCultivoName.getText().toString())) {
                binding.inputCultivoName.setText(s);
            }
        });
        viewModel.tipo.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompleteTipoCultivo.getText().toString())) {
                binding.autocompleteTipoCultivo
                        .setText(s, false);
            }
        });
        viewModel.fecha.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputCultivoDate.getText().toString())) {
                binding.inputCultivoDate.setText(s);
            }
        });
        viewModel.fechaFin.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputCultivoDateEnd.getText().toString())) {
                binding.inputCultivoDateEnd.setText(s);
            }
        });
        viewModel.existencias.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputCultivoExistencias.getText().toString())) {
                binding.inputCultivoExistencias.setText(s);

            }
        });
        viewModel.descripcion.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputCultivoDescripcion.getText().toString())) {
                binding.inputCultivoDescripcion.setText(s);
            }
        });

            viewModel.getNombreError().observe(getViewLifecycleOwner(), error -> binding.layoutInputName.setHelperText(error));
            viewModel.getTipoError().observe(getViewLifecycleOwner(), error -> binding.layoutTipoCultivo.setHelperText(error));
            viewModel.getFechaError().observe(getViewLifecycleOwner(), error -> binding.layoutInputDate.setHelperText(error));
            viewModel.getExistenciasError().observe(getViewLifecycleOwner(), error -> binding.layoutInputExistencias.setHelperText(error));
            viewModel.getDescripcionError().observe(getViewLifecycleOwner(), error -> binding.layoutInputDescripcion.setHelperText(error));
            viewModel.getFechaFinError().observe(getViewLifecycleOwner(), error -> binding.layoutInputDateEnd.setHelperText(error));

            viewModel.getNavegarAtras().observe(getViewLifecycleOwner(), navegar -> {
                if (navegar != null && navegar) {
                    getParentFragmentManager().popBackStack();
                    viewModel.onNavegacionCompleta();
                }
            });
        }
    }

package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.AlertDialog; // Import added
import android.content.DialogInterface; // Import added

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.databinding.FragmentActividadFormBinding;
import com.said.xenogar.ui.viewmodel.ActividadFormViewModel;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActividadFormFragment extends Fragment {


    public static final String ARG_ACTIVIDAD_ID = "actividad_id";
    public static final String ARG_CULTIVO_ID_FORM = "cultivo_id_form";

    private FragmentActividadFormBinding binding;
    private ActividadFormViewModel viewModel;

    public static ActividadFormFragment newInstance(Long cultivoId, @Nullable Long actividadId) {
        ActividadFormFragment fragment = new ActividadFormFragment();
        Bundle args = new Bundle();
        if (cultivoId != null) {
            args.putLong(ARG_CULTIVO_ID_FORM, cultivoId);
        }
        if (actividadId != null) {
            args.putLong(ARG_ACTIVIDAD_ID, actividadId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ActividadFormViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActividadFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            long cultivoId = getArguments().getLong(ARG_CULTIVO_ID_FORM, 0L);

            if (getArguments().containsKey(ARG_ACTIVIDAD_ID)) {
                long actividadId = getArguments().getLong(ARG_ACTIVIDAD_ID);
                viewModel.cargarActividad(actividadId);
                binding.deleteActividadButton.setVisibility(View.VISIBLE); // Show delete button
            } else {
                binding.deleteActividadButton.setVisibility(View.GONE); // Hide delete button for new activity
            }
            if (cultivoId != 0L) {
                viewModel.setCultivoId(cultivoId);
            }
        }

        setupDropdowns();
        initListeners();
        initObservers();

        if (getArguments() != null && getArguments().containsKey(ARG_ACTIVIDAD_ID)) {
            binding.formHeader.setText(R.string.actividad_form_title_edit);
            binding.submitActividadButton.setText(R.string.actividad_form_submitbutton_text_update);
        } else {
            binding.formHeader.setText(R.string.actividad_form_title);
            binding.submitActividadButton.setText(R.string.actividad_form_submitbutton_text);
        }
    }

    private void initObservers() {
        viewModel.tipoActividad.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompleteTipoActividad.getText().toString())) {
                binding.autocompleteTipoActividad.setText(s, false);
            }
        });

        viewModel.prioridad.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompletePrioridadActividad.getText().toString())) {
                binding.autocompletePrioridadActividad.setText(s, false);
            }
        });

        viewModel.fecha.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(String.valueOf(binding.inputActividadFecha.getText()))) {
                binding.inputActividadFecha.setText(s);
            }
        });

        viewModel.estado.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompleteEstadoActividad.getText().toString())) {
                binding.autocompleteEstadoActividad.setText(s, false);
            }
        });

        viewModel.descripcion.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(String.valueOf(binding.inputActividadDescripcion.getText()))) {
                binding.inputActividadDescripcion.setText(s);
            }
        });

        viewModel.getTipoActividadError().observe(getViewLifecycleOwner(), error -> binding.layoutTipoActividad.setHelperText(error));
        viewModel.getPrioridadError().observe(getViewLifecycleOwner(), error -> binding.layoutPrioridadActividad.setHelperText(error));
        viewModel.getFechaError().observe(getViewLifecycleOwner(), error -> binding.layoutInputFecha.setHelperText(error));
        viewModel.getEstadoError().observe(getViewLifecycleOwner(), error -> binding.layoutEstadoActividad.setHelperText(error));
        viewModel.getDescripcionError().observe(getViewLifecycleOwner(), error -> binding.layoutInputDescripcion.setHelperText(error));

        viewModel.getNavegarAtras().observe(getViewLifecycleOwner(), navegar -> {
            if (navegar != null && navegar) {
                getParentFragmentManager().popBackStack();
                viewModel.onNavegacionCompleta();
            }
        });
    }

    private void initListeners() {
        binding.submitActividadButton.setOnClickListener(v -> viewModel.guardarActividad());

        binding.deleteActividadButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro de que desea eliminar esta actividad? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    viewModel.deleteCurrentActividad(); // Call new ViewModel method
                    getParentFragmentManager().popBackStack(); // Navigate back after deletion
                })
                .setNegativeButton("Cancelar", null) // Dismisses dialog on click
                .show();
        });

        binding.autocompleteTipoActividad.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Actividad.TipoActividad) {
                viewModel.tipoActividad.setValue(((Actividad.TipoActividad) item).name());
            }
        });

        binding.autocompletePrioridadActividad.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Actividad.Prioridad) {
                viewModel.prioridad.setValue(((Actividad.Prioridad) item).name());
            }
        });

        binding.inputActividadFecha.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de actividad")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Convert UTC milliseconds to LocalDate in UTC, then format
                LocalDate selectedDate = Instant.ofEpochMilli(selection).atZone(ZoneOffset.UTC).toLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = selectedDate.format(formatter);
                binding.inputActividadFecha.setText(formattedDate);
                viewModel.fecha.setValue(formattedDate);
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER_ACTIVIDAD");
        });

        binding.autocompleteEstadoActividad.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Actividad.Estado) {
                viewModel.estado.setValue(((Actividad.Estado) item).name());
            }
        });

        binding.inputActividadDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.descripcion.setValue(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDropdowns() {
        ArrayAdapter<Actividad.TipoActividad> tipoActividadAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Actividad.TipoActividad.values()
        );
        binding.autocompleteTipoActividad.setAdapter(tipoActividadAdapter);

        ArrayAdapter<Actividad.Prioridad> prioridadAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Actividad.Prioridad.values()
        );
        binding.autocompletePrioridadActividad.setAdapter(prioridadAdapter);

        ArrayAdapter<Actividad.Estado> estadoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Actividad.Estado.values()
        );
        binding.autocompleteEstadoActividad.setAdapter(estadoAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

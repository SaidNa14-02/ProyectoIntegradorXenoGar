package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.databinding.FragmentInsumoFormBinding;
import com.said.xenogar.ui.viewmodel.InsumoFormViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InsumoFormFragment extends Fragment {

    public static final String ARG_INSUMO_ID = "insumo_id";
    private FragmentInsumoFormBinding binding;
    private InsumoFormViewModel viewModel;

    public static InsumoFormFragment newInstance(@Nullable Long insumoId) {
        InsumoFormFragment fragment = new InsumoFormFragment();
        Bundle args = new Bundle();
        if (insumoId != null) {
            args.putLong(ARG_INSUMO_ID, insumoId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InsumoFormViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsumoFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTipoInsumoDropdown();
        setupUnidadMedidaDropdown();
        initListeners();
        initObservers();

        if (getArguments() != null && getArguments().containsKey(ARG_INSUMO_ID)) {
            Long insumoId = getArguments().getLong(ARG_INSUMO_ID);
            viewModel.cargarInsumo(insumoId);
            binding.formTitle.setText("Editar Insumo");
            binding.submitInsumoButton.setText("Actualizar");
        } else {
            binding.formTitle.setText("Nuevo Insumo");
            binding.submitInsumoButton.setText("Guardar");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupTipoInsumoDropdown() {
        ArrayAdapter<Insumo.TipoInsumo> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Insumo.TipoInsumo.values()
        );
        binding.autocompleteTipoInsumo.setAdapter(adapter);
    }

    private void setupUnidadMedidaDropdown() {
        ArrayAdapter<Insumo.UnidadMedida> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Insumo.UnidadMedida.values()
        );
        binding.autocompleteUnidadMedida.setAdapter(adapter);
    }


    private void initListeners() {
        binding.submitInsumoButton.setOnClickListener(v -> viewModel.guardarInsumo());

        binding.inputInsumoName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { viewModel.nombre.setValue(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.autocompleteTipoInsumo.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Insumo.TipoInsumo) {
                viewModel.tipo.setValue(((Insumo.TipoInsumo) item).name());
            }
        });

        binding.inputInsumoCantidadActual.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { viewModel.cantidadActual.setValue(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.autocompleteUnidadMedida.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Insumo.UnidadMedida) {
                viewModel.unidad.setValue(((Insumo.UnidadMedida) item).name());
            }
        });

        binding.inputInsumoPuntoReorden.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { viewModel.puntoReorden.setValue(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void initObservers() {
        viewModel.nombre.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputInsumoName.getText().toString())) {
                binding.inputInsumoName.setText(s);
            }
        });
        viewModel.tipo.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompleteTipoInsumo.getText().toString())) {
                binding.autocompleteTipoInsumo.setText(s, false);
            }
        });
        viewModel.cantidadActual.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputInsumoCantidadActual.getText().toString())) {
                binding.inputInsumoCantidadActual.setText(s);
            }
        });
        viewModel.unidad.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.autocompleteUnidadMedida.getText().toString())) {
                binding.autocompleteUnidadMedida.setText(s, false);
            }
        });
        viewModel.puntoReorden.observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.equals(binding.inputInsumoPuntoReorden.getText().toString())) {
                binding.inputInsumoPuntoReorden.setText(s);
            }
        });

        viewModel.getNombreError().observe(getViewLifecycleOwner(), error -> binding.layoutInputName.setHelperText(error));
        viewModel.getTipoError().observe(getViewLifecycleOwner(), error -> binding.layoutTipoInsumo.setHelperText(error));
        viewModel.getCantidadActualError().observe(getViewLifecycleOwner(), error -> binding.layoutInputCantidadActual.setHelperText(error));
        viewModel.getUnidadError().observe(getViewLifecycleOwner(), error -> binding.layoutUnidadMedida.setHelperText(error));
        viewModel.getPuntoReordenError().observe(getViewLifecycleOwner(), error -> binding.layoutInputPuntoReorden.setHelperText(error));

        viewModel.isUnidadSelectionEnabled.observe(getViewLifecycleOwner(), enabled -> {
            binding.layoutUnidadMedida.setEnabled(enabled);
            binding.autocompleteUnidadMedida.setText(enabled ? viewModel.unidad.getValue() : "", false);
        });

        viewModel.getNavegarAtras().observe(getViewLifecycleOwner(), navegar -> {
            if (navegar != null && navegar) {
                getParentFragmentManager().popBackStack();
                viewModel.onNavegacionCompleta();
            }
        });
    }
}
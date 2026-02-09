package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog; // Import added
import android.widget.Toast; // Import added

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.said.xenogar.R;
import com.said.xenogar.databinding.HomeFragmentBinding;
import com.said.xenogar.util.SummaryGenerator; // Import added

import dagger.hilt.android.AndroidEntryPoint; // Import added
import javax.inject.Inject; // Import added
import java.util.concurrent.Executors; // Import for background thread

@AndroidEntryPoint // Add Hilt AndroidEntryPoint
public class HomeFragment extends Fragment {
    HomeFragmentBinding binding;

    @Inject // Inject SummaryGenerator
    SummaryGenerator summaryGenerator;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.goToCultivosButton.setOnClickListener(v->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CultivoListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Click listener for the new button
        binding.generateSummaryButton.setOnClickListener(v -> {
            // Generate JSON on a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                String jsonSummary = summaryGenerator.generateWeeklySummaryJson();
                
                // Post result to main thread to update UI
                requireActivity().runOnUiThread(() -> {
                    if (jsonSummary != null) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Resumen Semanal de Actividades")
                                .setMessage(jsonSummary)
                                .setPositiveButton("Cerrar", null)
                                .show();
                    } else {
                        Toast.makeText(requireContext(), "Error al generar el resumen.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

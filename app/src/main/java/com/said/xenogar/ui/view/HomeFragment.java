package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.said.xenogar.R;
import com.said.xenogar.databinding.HomeFragmentBinding;

public class HomeFragment extends Fragment {
    HomeFragmentBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.goToCultivosButton.setOnClickListener(v->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new CultivoListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.goToInsumosButton.setOnClickListener(v->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, new InsumoListFragment())
                    .addToBackStack(null)
                    .commit();
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

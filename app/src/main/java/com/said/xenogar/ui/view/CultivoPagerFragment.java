package com.said.xenogar.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.said.xenogar.databinding.FragmentCultivoPagerBinding;
import com.said.xenogar.ui.adapter.CultivoPagerAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CultivoPagerFragment extends Fragment {

    public static final String ARG_CULTIVO_ID = "cultivo_id";
    private FragmentCultivoPagerBinding binding;
    private Long cultivoId;

    public static CultivoPagerFragment newInstance(Long cultivoId) {
        CultivoPagerFragment fragment = new CultivoPagerFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCultivoPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CultivoPagerAdapter pagerAdapter = new CultivoPagerAdapter(this, cultivoId);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Detalles");
                            break;
                        case 1:
                            tab.setText("Actividades");
                            break;
                    }
                }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

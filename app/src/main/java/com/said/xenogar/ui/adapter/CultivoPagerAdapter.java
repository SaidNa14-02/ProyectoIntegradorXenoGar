package com.said.xenogar.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.said.xenogar.ui.view.ActividadesFragment;
import com.said.xenogar.ui.view.CultivoDetailFragment;

public class CultivoPagerAdapter extends FragmentStateAdapter {

    private final Long cultivoId;

    public CultivoPagerAdapter(Fragment fragment, Long cultivoId) {
        super(fragment);
        this.cultivoId = cultivoId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CultivoDetailFragment.newInstance(cultivoId);
            case 1:
                return ActividadesFragment.newInstance(cultivoId);
            default:
                throw new IllegalStateException("Posición de pestaña no válida: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Dos pestañas: Detalles y Actividades
    }
}

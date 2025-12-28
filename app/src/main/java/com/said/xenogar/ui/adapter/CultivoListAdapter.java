package com.said.xenogar.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.databinding.ItemCultivoBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CultivoListAdapter extends ListAdapter<Cultivo, CultivoListAdapter.CultivoViewHolder> {

    public CultivoListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Cultivo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Cultivo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Cultivo oldItem, @NonNull Cultivo newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Cultivo oldItem, @NonNull Cultivo newItem) {
            return oldItem.getNombre().equals(newItem.getNombre()) &&
                    oldItem.getFechaInicio() == newItem.getFechaInicio() &&
                    oldItem.getTipo().equals(newItem.getTipo()) &&
                    oldItem.getExistencias() == newItem.getExistencias();
        }
    };

    @NonNull
    @Override
    public CultivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCultivoBinding binding = ItemCultivoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CultivoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CultivoViewHolder holder, int position) {
        Cultivo currentCultivo = getItem(position);
        holder.bind(currentCultivo);
    }

    public static class CultivoViewHolder extends RecyclerView.ViewHolder {
        private final ItemCultivoBinding binding;

        public CultivoViewHolder(ItemCultivoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Cultivo cultivo) {
            binding.textViewNombreCultivo.setText(cultivo.getNombre());
            binding.textViewTipoCultivo.setText(cultivo.getTipo().toString());

            String existenciasTexto = String.format(Locale.getDefault(), "Existencias: %d", cultivo.getExistencias());
            binding.textViewExistenciasCultivo.setText(existenciasTexto);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = new Date(cultivo.getFechaInicio());
            binding.textViewFechaInicioCultivo.setText(sdf.format(fecha));
        }
    }
}

package com.said.xenogar.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.databinding.ItemActividadBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActividadListAdapter extends ListAdapter<Actividad, ActividadListAdapter.ActividadViewHolder> {

    private OnItemClickListener listener;

    public ActividadListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Actividad> DIFF_CALLBACK = new DiffUtil.ItemCallback<Actividad>() {
        @Override
        public boolean areItemsTheSame(@NonNull Actividad oldItem, @NonNull Actividad newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Actividad oldItem, @NonNull Actividad newItem) {
            return oldItem.getActividad().equals(newItem.getActividad()) &&
                    oldItem.getFecha() == newItem.getFecha() &&
                    oldItem.getPrioridad().equals(newItem.getPrioridad()) &&
                    oldItem.getEstado().equals(newItem.getEstado());
        }
    };

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActividadBinding binding = ItemActividadBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ActividadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad currentActividad = getItem(position);
        holder.bind(currentActividad);
    }

    public class ActividadViewHolder extends RecyclerView.ViewHolder {
        private final ItemActividadBinding binding;

        public ActividadViewHolder(ItemActividadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imageButtonActividad.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClickActividad(getItem(position));
                }
            });
        }

        public void bind(Actividad actividad) {
            binding.textViewTipoActividad.setText(actividad.getActividad().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = new Date(actividad.getFecha());
            binding.textViewFechaActividad.setText(sdf.format(fecha));

            binding.textViewPrioridadActividad.setText(String.format(Locale.getDefault(), "Prioridad: %s", actividad.getPrioridad().toString()));
            binding.textViewEstadoActividad.setText(String.format(Locale.getDefault(), "Estado: %s", actividad.getEstado().toString()));

            binding.imageViewActividad.setImageResource(getImageResourceForActividad(actividad.getActividad()));
        }
    }

    public interface OnItemClickListener {
        void onItemClickActividad(Actividad actividad);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private int getImageResourceForActividad(Actividad.TipoActividad tipoActividad) {
        switch (tipoActividad) {
            case RIEGO:
                return R.drawable.seedling;
            case FUMIGACION:
                return R.drawable.pesticide;
            case PODA:
                return R.drawable.tools;
            case COSECHA:
                return R.drawable.frutal_tree_icon; // Using a fruit tree as a placeholder for harvest
            case OTRO:
            default:
                return R.drawable.other_icon;
        }
    }
}

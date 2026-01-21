package com.said.xenogar.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.databinding.ItemInsumoBinding;

import java.util.Locale;

public class InsumoListAdapter extends ListAdapter<Insumo, InsumoListAdapter.InsumoViewHolder> {

    private OnItemClickListener listener;

    public InsumoListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Insumo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Insumo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Insumo oldItem, @NonNull Insumo newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

            @Override
            public boolean areContentsTheSame(@NonNull Insumo oldItem, @NonNull Insumo newItem) {
                boolean unitsAreEqual;
                if (oldItem.getUnidad() == null && newItem.getUnidad() == null) {
                    unitsAreEqual = true;
                } else if (oldItem.getUnidad() != null && newItem.getUnidad() != null) {
                    unitsAreEqual = oldItem.getUnidad().equals(newItem.getUnidad());
                } else {
                    unitsAreEqual = false;
                }
        
                return oldItem.getNombre().equals(newItem.getNombre()) &&
                        oldItem.getTipo().equals(newItem.getTipo()) &&
                        oldItem.getCantidadActual() == newItem.getCantidadActual() &&
                        unitsAreEqual;
            }    };

    @NonNull
    @Override
    public InsumoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInsumoBinding binding = ItemInsumoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InsumoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InsumoViewHolder holder, int position) {
        Insumo currentInsumo = getItem(position);
        holder.bind(currentInsumo);
    }

    public class InsumoViewHolder extends RecyclerView.ViewHolder {
        private final ItemInsumoBinding binding;

        public InsumoViewHolder(ItemInsumoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imageButtonInsumo.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClickInsumo(getItem(position));
                }
            });
        }

        public void bind(Insumo insumo) {

            binding.textViewNombreInsumo.setText(insumo.getNombre());
            binding.textViewTipoInsumo.setText(insumo.getTipo().toString());
            String unidadStr = insumo.getUnidad() != null ? insumo.getUnidad().toString() : "";
            String cantidadTexto = String.format(Locale.getDefault(), "%.2f %s", insumo.getCantidadActual(), unidadStr);
            binding.textViewCantidadInsumo.setText(cantidadTexto);
            binding.imageViewInsumo.setImageResource(getImageResourceForInsumo(insumo.getTipo()));
        }
    }

    private int getImageResourceForInsumo(Insumo.TipoInsumo tipoInsumo){
        switch (tipoInsumo){
            case HERRAMIENTA:
                return R.drawable.toolbossvg;
            case FERTILIZANTE:
                return R.drawable.fertilizer;
            case SEMILLA:
                return R.drawable.seedling;
            case PESTICIDA:
                return R.drawable.pesticide;
            case OTRO:
                return R.drawable.tools;
            default:
                return R.drawable.tools;
        }
    }

    public interface OnItemClickListener {
        void onItemClickInsumo(Insumo insumo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
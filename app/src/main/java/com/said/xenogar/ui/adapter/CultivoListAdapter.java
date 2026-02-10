package com.said.xenogar.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.databinding.ItemCultivoBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CultivoListAdapter extends ListAdapter<Cultivo, CultivoListAdapter.CultivoViewHolder> {
    private OnItemClickListener listener;

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

    public class CultivoViewHolder extends RecyclerView.ViewHolder {
        private final ItemCultivoBinding binding;

        public CultivoViewHolder(ItemCultivoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imageButtonCultivo.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener !=null && position !=RecyclerView.NO_POSITION){
                    listener.onItemClickCultivo(getItem(position));
                }
            });
        }

        public void bind(Cultivo cultivo) {
            binding.textViewNombreCultivo.setText(cultivo.getNombre());
            binding.textViewTipoCultivo.setText(cultivo.getTipo().toString());

            String existenciasTexto = String.format(Locale.getDefault(), "Existencias: %d", cultivo.getExistencias());
            binding.textViewExistenciasCultivo.setText(existenciasTexto);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = new Date(cultivo.getFechaInicio());
            binding.textViewFechaInicioCultivo.setText(sdf.format(fecha));
            binding.imageViewCultivo.setImageResource(getImageResourceForCultivo(cultivo.getTipo()));
        }
    }



    public interface  OnItemClickListener{
        void onItemClickCultivo(Cultivo cultivo);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public int getImageResourceForCultivo(Cultivo.TipoCultivo tipoCultivo){
        switch (tipoCultivo){
            case FRUTAL:
                return R.drawable.frutal_tree_icon;
            case AROMATICO:
                return R.drawable.aromatic_icon;
            case ORNAMENTAL:
                return R.drawable.decorative_icon;
            case ALIMENTARIA:
                return R.drawable.frutal_tree_icon;
            case MEDICINAL:
                return R.drawable.other_icon;
            case INDEFINIDO:
                return R.drawable.other_icon;
            default:
                return R.drawable.other_icon;

        }
    }
}

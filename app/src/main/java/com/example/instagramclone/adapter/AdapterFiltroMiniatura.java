package com.example.instagramclone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;
import com.example.instagramclone.helper.FilterCustom;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;

public class AdapterFiltroMiniatura extends RecyclerView.Adapter<AdapterFiltroMiniatura.MyViewHolder> {
    private List<FilterCustom> listaFiltro;
    private Bitmap imagem;
    private Context context;

    public AdapterFiltroMiniatura(List<FilterCustom> listaFiltro, Bitmap imagem, Context context) {
        this.listaFiltro = listaFiltro;
        this.imagem = imagem;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagem_filtro, parent, false);
        return new AdapterFiltroMiniatura.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FilterCustom filtro = listaFiltro.get(position);
        Bitmap imagemFiltro;

        //copia a imagem para utilizar o filtro
        imagemFiltro = imagem.copy(imagem.getConfig(), true);
        //recupera o filtro do arraylist
        Filter filter = listaFiltro.get(position).getFilter();

        holder.nomeFiltro.setText(filtro.getName());
        holder.imagemMiniatura.setImageBitmap(filter.processFilter(imagemFiltro));
    }

    @Override
    public int getItemCount() {
        return listaFiltro.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagemMiniatura;
        private TextView nomeFiltro;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemMiniatura = itemView.findViewById(R.id.imageMiniatura);
            nomeFiltro = itemView.findViewById(R.id.textNomeFiltro);
        }
    }
}

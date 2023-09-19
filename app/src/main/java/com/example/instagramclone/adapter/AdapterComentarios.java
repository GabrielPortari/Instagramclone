package com.example.instagramclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Comentario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolder> {
    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentarios(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario_recyclerview, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        if(!comentario.getCaminhoFoto().isEmpty()){
            Uri caminhoFoto = Uri.parse(comentario.getCaminhoFoto());
            Glide.with(context).load(caminhoFoto).into(holder.imagemPerfil);
        }
        holder.nome.setText(comentario.getNomeUsuario());
        holder.comentario.setText(comentario.getComentario());

    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imagemPerfil;
        TextView nome, comentario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemPerfil = itemView.findViewById(R.id.circleImagePerfilComentario);
            nome = itemView.findViewById(R.id.textNomeComentario);
            comentario = itemView.findViewById(R.id.textComentario);
        }
    }
}


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
import com.example.instagramclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {
    private List<Usuario> listaUsuario;
    private Context context;

    public AdapterPesquisa(List<Usuario> listaUsuario, Context context) {
        this.listaUsuario = listaUsuario;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_pesquisa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = listaUsuario.get(position);
        holder.nome.setText(usuario.getNome());
        if(usuario.getFoto().isEmpty()){
            holder.imagemPerfil.setImageResource(R.drawable.perfil_padrao);
        }else{
            Uri url = Uri.parse(usuario.getFoto());
            Glide.with(context).load(url).into(holder.imagemPerfil);
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imagemPerfil;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.circleImagePerfilPesquisa);
            nome = itemView.findViewById(R.id.textNomeItemPesquisa);

        }
    }
}

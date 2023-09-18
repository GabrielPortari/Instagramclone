package com.example.instagramclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Feed;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {
    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_recyclerview, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Feed feed = listaFeed.get(position);

        //carregar dados do feed
        Uri uriFotoUsuario  = Uri.parse(feed.getFotoUsuario());
        Uri uriImagemPostada = Uri.parse(feed.getCaminhoImagem());

        Glide.with(context).load(uriImagemPostada).into(holder.imagemPostada);
        Glide.with(context).load(uriFotoUsuario).into(holder.circleImagePerfil);

        holder.textNome.setText(feed.getNomeUsuario());
        holder.textDescricao.setText(feed.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textDescricao, textQuantidadeLikes, textNome;
        private CircleImageView circleImagePerfil;
        private ImageView imagemPostada, buttonLike, buttonComentarios;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textDescricao = itemView.findViewById(R.id.textDescricaoFeed);
            textQuantidadeLikes = itemView.findViewById(R.id.textQuantidadeLikeFeed);
            textNome = itemView.findViewById(R.id.textNomeFeed);
            buttonLike = itemView.findViewById(R.id.likeButtonFeed);
            buttonComentarios = itemView.findViewById(R.id.commentButtonFeed);
            circleImagePerfil = itemView.findViewById(R.id.circleImagePerfilFeed);
            imagemPostada = itemView.findViewById(R.id.imageFeed);

        }
    }
}

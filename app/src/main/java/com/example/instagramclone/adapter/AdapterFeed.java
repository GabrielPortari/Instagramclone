package com.example.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activity.ComentariosActivity;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Feed;
import com.example.instagramclone.model.PostagemCurtidas;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
        final Feed feed = listaFeed.get(position);
        final boolean[] curtido = {false};
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        //carregar dados do feed
        Uri uriFotoUsuario  = Uri.parse(feed.getFotoUsuario());
        Uri uriImagemPostada = Uri.parse(feed.getCaminhoImagem());

        Glide.with(context).load(uriImagemPostada).into(holder.imagemPostada);
        Glide.with(context).load(uriFotoUsuario).into(holder.circleImagePerfil);

        holder.textNome.setText(feed.getNomeUsuario());
        holder.textDescricao.setText(feed.getDescricao());

        holder.buttonComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtra("idPostagem", feed.getIdPostagem());
                context.startActivity(intent);
            }
        });

        /*estrutura
        postagens-curtidas
            .id_postagem
                .qt_curtidas
                    .id_usuario_que_curtiu
                        nome_usuario
                        caminho_foto
        */

        //recuperar dados da postagem
        DatabaseReference curtidasReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                .child("postagens-curtidas")
                .child(feed.getIdPostagem());
        curtidasReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int qtCurtidas = 0;
                if(snapshot.hasChild("qtCurtidas")){
                    PostagemCurtidas postagemCurtida = snapshot.getValue(PostagemCurtidas.class);
                    qtCurtidas = postagemCurtida.getQtCurtidas();
                }
                if(snapshot.hasChild(usuarioLogado.getId())){
                    curtido[0] = true;
                    holder.buttonLike.setColorFilter(ContextCompat.getColor(context, R.color.light_blue));
                }else{
                    curtido[0] = false;
                    holder.buttonLike.setColorFilter(ContextCompat.getColor(context, R.color.dark_gray));
                }

                PostagemCurtidas curtidas = new PostagemCurtidas();
                curtidas.setFeed(feed);
                curtidas.setUsuario(usuarioLogado);
                curtidas.setQtCurtidas(qtCurtidas);
                holder.textQuantidadeLikes.setText(String.valueOf(curtidas.getQtCurtidas()));

                //evento para evento de curtidas
                holder.buttonLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!curtido[0]){
                            curtido[0] = true;
                            curtidas.salvarNoFirebase();
                            holder.textQuantidadeLikes.setText(String.valueOf(curtidas.getQtCurtidas()));
                            holder.buttonLike.setColorFilter(ContextCompat.getColor(context, R.color.light_blue));
                        }else{
                            curtido[0] = false;
                            curtidas.removerQuantidadeLikes();
                            holder.textQuantidadeLikes.setText(String.valueOf(curtidas.getQtCurtidas()));
                            holder.buttonLike.setColorFilter(ContextCompat.getColor(context, R.color.dark_gray));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textDescricao, textQuantidadeComentarios, textQuantidadeLikes, textNome;
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

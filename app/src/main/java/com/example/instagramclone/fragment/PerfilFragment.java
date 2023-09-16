package com.example.instagramclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activity.EditarPerfilActivity;
import com.example.instagramclone.adapter.AdapterGridView;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Postagem;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView circleImagePerfil;
    private Button buttonAcaoPerfil;
    private GridView gridView;
    private ProgressBar progressBar;
    private AdapterGridView adapterGridView;

    private Usuario usuarioLogado;

    private DatabaseReference usuariosReference;
    private DatabaseReference usuarioLogadoReference;
    private DatabaseReference postagensReference;
    private DatabaseReference postagensUsuarioReference;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        configuracoesIniciais(view);

        //recuperar usuario logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        recuperarDadosUsuarioLogado();

        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditarPerfilActivity.class));
            }
        });

        inicializarImageLoader();
        carregarImagens();

        // Inflate the layout for this fragment
        return view;
    }
    private void configuracoesIniciais(View view){
        usuariosReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                .child("usuarios");
        postagensReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                .child("postagens");

        textPublicacoes = view.findViewById(R.id.textPublicacoesPerfil);
        textSeguidores = view.findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = view.findViewById(R.id.textSeguindoPerfil);
        circleImagePerfil = view.findViewById(R.id.circleImageViewPerfil);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        gridView = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
    }

    private void recuperarDadosUsuarioLogado(){
        //recupera imagem de perfil do usuario
        if(!usuarioLogado.getFoto().isEmpty()){
            Glide.with(getActivity()).load(usuarioLogado.getFoto()).into(circleImagePerfil);
        }
        //recupera seguidores e seguindo do usuario
        usuarioLogadoReference = usuariosReference.child(usuarioLogado.getId());
        valueEventListener = usuarioLogadoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                //recupera dados do usuario
                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());

                textSeguindo.setText(seguindo);
                textSeguidores.setText(seguidores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void carregarImagens(){
        postagensUsuarioReference = postagensReference.child(usuarioLogado.getId());
        postagensUsuarioReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/3;
                gridView.setColumnWidth(tamanhoImagem);

                List<String> urlImagens = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlImagens.add(postagem.getCaminhoImagem());
                }
                int qtPublicacoes = urlImagens.size();
                textPublicacoes.setText(String.valueOf(qtPublicacoes));

                //configuracoes do gridview para listar imagens
                adapterGridView = new AdapterGridView(getActivity(), R.layout.item_gridview_postagem, urlImagens);
                gridView.setAdapter(adapterGridView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosUsuarioLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoReference.removeEventListener(valueEventListener);
    }

}
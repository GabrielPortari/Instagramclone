package com.example.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Postagem;
import com.example.instagramclone.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {
    private TextView textNomeUsuario, textCurtidas, textDescricao, textVerComentarios;
    private ImageView imagemPostada;
    private CircleImageView imagemPerfilUsuario;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);
        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Exibe dados do usuario
            textNomeUsuario.setText(usuario.getNome());
            if(!usuario.getFoto().isEmpty()){
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(VisualizarPostagemActivity.this).load(uri).into(imagemPerfilUsuario);
            }

            Uri postagemUrl = Uri.parse(postagem.getCaminhoImagem());
            Glide.with(VisualizarPostagemActivity.this).load(postagemUrl).into(imagemPostada);
            textDescricao.setText(postagem.getDescricao());

        }
    }
    private void configuracoesIniciais(){
        textNomeUsuario = findViewById(R.id.textNomeItemPesquisa);
        textCurtidas = findViewById(R.id.textViewCurtidasVisualizarPostagens);
        textDescricao = findViewById(R.id.textDescricaoVisualizarPostagens);
        textVerComentarios = findViewById(R.id.textVisualizarComentariosVisualizarPostagens);
        imagemPostada = findViewById(R.id.imageVisualizarPostagem);
        imagemPerfilUsuario = findViewById(R.id.circleImagePerfilPesquisa);

        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
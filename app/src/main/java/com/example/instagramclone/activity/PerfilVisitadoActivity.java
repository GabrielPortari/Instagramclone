package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilVisitadoActivity extends AppCompatActivity {
    private TextView textPostagens, textSeguidores, textSeguindo;
    private CircleImageView circleImagePerfil;
    private Button buttonAcaoPerfil;
    private GridView gridView;
    private AdapterGridView adapterGridView;
    private ProgressBar progressBar;
    private Usuario usuarioSelecionado, usuarioLogado;
    private Toolbar toolbar;

    private DatabaseReference usuarioLogadoReference;
    private DatabaseReference usuariosReference;
    private DatabaseReference amigoReference;
    private DatabaseReference seguidoresReference;
    private DatabaseReference postagensUsuarioVisitadoReference;

    private ValueEventListener listenerPerfilAmigo;

    private String idUsuarioLogado;
    private List<Postagem> postagemLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_visitado);
        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");
            toolbar.setTitle(usuarioSelecionado.getNome());
            postagensUsuarioVisitadoReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                    .child("postagens")
                    .child(usuarioSelecionado.getId());

            //recuperar os dados do usuario selecionado
            String caminhoFotoUsuario = usuarioSelecionado.getFoto();
            if (!caminhoFotoUsuario.isEmpty()) {
                Uri urlFoto = Uri.parse(caminhoFotoUsuario);
                Glide.with(getApplicationContext()).load(urlFoto).into(circleImagePerfil);
            }
        }
        inicializarImageLoader();
        recuperaDadosUsuarioLogado();
        recuperarPostagensUsuarioVisitado();
        
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Postagem postagem = postagemLista.get(position);

                Intent intent = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuarioSelecionado", usuarioSelecionado);
                startActivity(intent);
            }
        });
    }

    public void configuracoesIniciais(){
        usuariosReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios");
        seguidoresReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        postagemLista = new ArrayList<>();

        toolbar = findViewById(R.id.toolbarPrincipal);
        textPostagens = findViewById(R.id.textPublicacoesPerfil);
        textSeguidores = findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = findViewById(R.id.textSeguindoPerfil);
        circleImagePerfil = findViewById(R.id.circleImageViewPerfil);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        gridView = findViewById(R.id.gridViewPerfil);
        progressBar = findViewById(R.id.progressBarPerfil);

        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);

        buttonAcaoPerfil.setText("...");
    }

    private void habilitarBotaoSeguir(boolean seguindo){
        if(seguindo){
            buttonAcaoPerfil.setText("SEGUINDO");
        }else{
            buttonAcaoPerfil.setText("SEGUIR");
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvarSeguindo(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguindo(Usuario usuarioLogado, Usuario usuarioSelecionado){
        /* Estrutura
        * seguidores
        *     .id_usuario
        *         .id_usuario_seguido
        *             .dados_usuario_buscado
        */
        HashMap<String, Object> dadosPerfilBuscado = new HashMap<>();
        dadosPerfilBuscado.put("nome", usuarioLogado.getNome());
        dadosPerfilBuscado.put("foto", usuarioLogado.getFoto());
        DatabaseReference seguidorReference = seguidoresReference
                .child(usuarioSelecionado.getId())
                .child(usuarioLogado.getId());

        //salva a estrutura de seguir no firebase
        seguidorReference.setValue(dadosPerfilBuscado);

        //altera o botao seguir para seguindo
        buttonAcaoPerfil.setText("SEGUINDO");
        buttonAcaoPerfil.setOnClickListener(null);

        //incrementar os seguindo do usuario logado
        int seguindo = usuarioLogado.getSeguindo()+1;
        DatabaseReference atualizaSeguindo = usuariosReference.child(usuarioLogado.getId());
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        atualizaSeguindo.updateChildren(dadosSeguindo);

        //incrementar os seguidores do usuario buscado
        int seguidores = usuarioSelecionado.getSeguidores()+1;
        DatabaseReference atualizaSeguidores = usuariosReference.child(usuarioSelecionado.getId());
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        atualizaSeguidores.updateChildren(dadosSeguidores);
    }

    private void recuperaDadosUsuarioLogado(){
        usuarioLogadoReference = usuariosReference.child(idUsuarioLogado);
        usuarioLogadoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);
                /*
                * Verifica se o usuario esta seguindo o usuario buscado
                * */
                verificarSeSegueUsuario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificarSeSegueUsuario(){
        DatabaseReference seguidorReference = seguidoresReference
                .child(usuarioSelecionado.getId())
                .child(idUsuarioLogado);

        seguidorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //se ja existe, esta seguindo o usuario
                    habilitarBotaoSeguir(true);
                }else{
                    //se nao existe, nao está seguindo
                    habilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void recuperarPostagensUsuarioVisitado(){
        postagensUsuarioVisitadoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/3;
                gridView.setColumnWidth(tamanhoImagem);

                List<String> urlImagens = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlImagens.add(postagem.getCaminhoImagem());
                    postagemLista.add(postagem);
                }

                //configuracoes do gridview para listar imagens
                adapterGridView = new AdapterGridView(getApplicationContext(), R.layout.item_gridview_postagem, urlImagens);
                gridView.setAdapter(adapterGridView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void recuperarDadosPerfilBuscado(){
        amigoReference = usuariosReference.child(usuarioSelecionado.getId());
        listenerPerfilAmigo = amigoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                //recupera dados do usuario

                //String postagens = String.valueOf(usuario.getPostagens());
                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String postagens = String.valueOf(usuario.getPostagens());

                //textPublicacoes.setText(postagens);
                textSeguindo.setText(seguindo);
                textSeguidores.setText(seguidores);
                textPostagens.setText(postagens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilBuscado();
        recuperaDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        amigoReference.removeEventListener(listenerPerfilAmigo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
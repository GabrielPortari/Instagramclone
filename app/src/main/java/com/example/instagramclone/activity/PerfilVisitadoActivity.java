package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilVisitadoActivity extends AppCompatActivity {
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView circleImagePerfil;
    private Button buttonAcaoPerfil;
    private GridView gridView;
    private ProgressBar progressBar;
    private Usuario usuarioSelecionado, usuarioLogado;
    private Toolbar toolbar;

    private DatabaseReference usuarioLogadoReference;
    private DatabaseReference usuariosReference;
    private DatabaseReference amigoReference;
    private DatabaseReference seguidoresReference;
    private ValueEventListener listenerPerfilAmigo;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_visitado);
        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");
            toolbar.setTitle(usuarioSelecionado.getNome());

            //recuperar os dados do usuario selecionado
            String caminhoFotoUsuario = usuarioSelecionado.getFoto();
            if(!caminhoFotoUsuario.isEmpty()){
                Uri urlFoto = Uri.parse(caminhoFotoUsuario);
                Glide.with(getApplicationContext()).load(urlFoto).into(circleImagePerfil);
            }else{
                circleImagePerfil.setImageResource(R.drawable.perfil_padrao);
            }
        }else{
            Log.e("INTENT", "Nao foi possivel recuperar os dados");
            finish();
        }

        recuperaDadosUsuarioLogado();
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
        *         .id_usuario_buscado
        *             .dados_usuario_buscado
        */
        HashMap<String, Object> dadosPerfilBuscado = new HashMap<>();
        dadosPerfilBuscado.put("nome", usuarioSelecionado.getNome());
        dadosPerfilBuscado.put("foto", usuarioSelecionado.getFoto());
        DatabaseReference seguidorReference = seguidoresReference
                .child(usuarioLogado.getId())
                .child(usuarioSelecionado.getId());

        //salva a estrutura de seguir no firebase
        seguidorReference.setValue(dadosPerfilBuscado);

        //altera o botao seguir para seguindo
        buttonAcaoPerfil.setText("SEGUINDO");
        buttonAcaoPerfil.setOnClickListener(null);
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
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());

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

    public void configuracoesIniciais(){
        usuariosReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios");
        seguidoresReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        toolbar = findViewById(R.id.toolbarPrincipal);
        textPublicacoes = findViewById(R.id.textPublicacoesPerfil);
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

    private void recuperarDadosPerfilBuscado(){
        amigoReference = usuariosReference.child(usuarioSelecionado.getId());
        listenerPerfilAmigo = amigoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                //recupera dados do usuario
                String postagens = String.valueOf(usuario.getPostagens());
                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());

                textPublicacoes.setText(postagens);
                textSeguindo.setText(seguindo);
                textSeguidores.setText(seguidores);
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
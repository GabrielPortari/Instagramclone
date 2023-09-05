package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilVisitadoActivity extends AppCompatActivity {
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView circleImagePerfil;
    private Button buttonAcaoPerfil;
    private GridView gridView;
    private ProgressBar progressBar;
    private Usuario usuarioSelecionado;
    private Toolbar toolbar;

    private DatabaseReference usuarioReference;
    private DatabaseReference amigoReference;
    private ValueEventListener listenerPerfilAmigo;

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
    }
    public void configuracoesIniciais(){
        usuarioReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios");

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

        buttonAcaoPerfil.setText("SEGUIR");
    }

    private void recuperarDadosPerfil(){
        amigoReference = usuarioReference.child(usuarioSelecionado.getId());
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
        recuperarDadosPerfil();
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
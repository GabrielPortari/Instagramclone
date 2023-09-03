package com.example.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.instagramclone.R;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {
    private CircleImageView circleImagePerfil;
    private TextView textEditarFoto;
    private TextInputEditText editNome, editEmail;
    private Button botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        //configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);

        //inicia os componentes de interface
        configuracoesIniciais();

        //recuperar usuario atual
        FirebaseUser usuarioLogado = UsuarioFirebase.getUsuarioLogado();

        editNome.setText(usuarioLogado.getDisplayName());
        editEmail.setText(usuarioLogado.getEmail());

    }

    public void configuracoesIniciais(){
        circleImagePerfil = findViewById(R.id.circleImageEditarPerfil);
        textEditarFoto = findViewById(R.id.textEditarFoto);
        editNome = findViewById(R.id.editTextNomeEditarPerfil);
        editEmail = findViewById(R.id.editTextEmailEditarPerfil);
        editEmail.setFocusable(false);
        botaoSalvar = findViewById(R.id.buttonSalvarEditarPerfil);
    }
}
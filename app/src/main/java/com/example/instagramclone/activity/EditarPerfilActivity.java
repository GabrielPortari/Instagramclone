package com.example.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {
    private CircleImageView circleImagePerfil;
    private TextView textEditarFoto;
    private TextInputEditText editNome, editEmail;
    private Button botaoSalvar;
    private Usuario usuarioLogado;


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
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //recuperar usuario atual
        FirebaseUser firebaseUser = UsuarioFirebase.getUsuarioLogado();
        editNome.setText(firebaseUser.getDisplayName());
        editEmail.setText(firebaseUser.getEmail());

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNome.getText().toString();
                if(nomeAtualizado.isEmpty()){
                    Toast.makeText(EditarPerfilActivity.this, "Preencha o campo nome antes de continuar", Toast.LENGTH_SHORT).show();
                }else{
                    //atualizar nome do usuario no auth
                    UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                    //atualizar nome do usuario no database
                    usuarioLogado.setNome(nomeAtualizado);
                    usuarioLogado.atualizarNoFirebase();
                    Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void configuracoesIniciais(){
        circleImagePerfil = findViewById(R.id.circleImageEditarPerfil);
        textEditarFoto = findViewById(R.id.textEditarFoto);
        editNome = findViewById(R.id.editTextNomeEditarPerfil);
        editEmail = findViewById(R.id.editTextEmailEditarPerfil);
        editEmail.setFocusable(false);
        botaoSalvar = findViewById(R.id.buttonSalvarEditarPerfil);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
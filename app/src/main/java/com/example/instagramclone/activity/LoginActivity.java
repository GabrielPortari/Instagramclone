package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends AppCompatActivity {
    private Button buttonLogin;
    private TextView textCadastrar;
    private TextInputEditText editEmail, editSenha;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        firebaseAuth.signOut();*/

        //Configurações Iniciais
        verificarUsuarioLogado();
        editEmail = findViewById(R.id.textInputEmail_loginScreen);
        editEmail.requestFocus();

        editSenha = findViewById(R.id.textInputSenha_loginScreen);
        textCadastrar = findViewById(R.id.textCadastro_loginScreen);
        buttonLogin = findViewById(R.id.buttonEntrar_loginScreen);
        progressBar = findViewById(R.id.progressLogin);

        //clicklistener do botao para validar entradas
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarAutenticacao(v);
            }
        });

        //clicklistener do texto cadastre-se
        textCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSignInActivity(v);
            }
        });
    }

    public void validarAutenticacao(View view){
        //recupera email e senha dos edittext
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        //valida se há campos em branco
        if(!textEmail.isEmpty()){
            if(!textSenha.isEmpty()){
                //cria um usuario, e seta email e senha para fazer login
                Usuario usuario = new Usuario();
                usuario.setEmail(textEmail);
                usuario.setSenha(textSenha);
                fazerLogin(usuario);
            }else{
                Toast.makeText(LoginActivity.this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
        }
    }

    public void fazerLogin(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();

        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //caso o login seja concluido, abre a main activity
                            abrirMainActivity();
                            finish();
                        }else{
                            //caso contrario, retorna um erro
                            String exception;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                exception = "Usuário não cadastrado";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Email ou senha incorreto";
                            }catch (Exception e){
                                exception = "Erro ao cadastrar usuario: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    public void abrirSignInActivity(View view){
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(intent);
    }
    public void abrirMainActivity(){
        //chama a main activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void verificarUsuarioLogado(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        if(firebaseAuth.getCurrentUser() != null){
            abrirMainActivity();
        }
    }
}
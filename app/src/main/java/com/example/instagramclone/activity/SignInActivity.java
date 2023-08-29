package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.helper.Base64Custom;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText editNome, editEmail, editSenha;
    private Button botaoRegistrar;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //configurações iniciais
        editNome = findViewById(R.id.textInputNome_signIn);
        editEmail = findViewById(R.id.textInputEmail_signIn);
        editSenha = findViewById(R.id.textInputSenha_signIn);
        botaoRegistrar = findViewById(R.id.buttonRegistrar_signIn);
        progressBar = findViewById(R.id.progressSignIn);

        progressBar.setVisibility(View.GONE);
        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastro(v);
            }
        });
    }

    public void validarCadastro(View view){
        String textNome = editNome.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        if(!textNome.isEmpty()){
            if(!textEmail.isEmpty()){
                if(!textSenha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);

                    cadastrarUsuario(usuario);
                }else{
                    Toast.makeText(SignInActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SignInActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(SignInActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Log.i("AUTH", "Cadastro de usuário completo");
                            Toast.makeText(SignInActivity.this, "Cadastro completo com sucesso", Toast.LENGTH_SHORT).show();

                            //recupera o email em base64 para usar como identificador
                            String id = Base64Custom.codeBase64(usuario.getEmail());
                            usuario.setId(id);

                            //salva o nome do usuario no firebaseAuth
                            //FirebaseUsuario.atualizaNomeUsuario(usuario.getNome());

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                            //tenta salvar o usuario no banco de dados
                            try{
                                String userId = Base64Custom.codeBase64(usuario.getEmail());
                                usuario.setId(userId);
                                //Quando desejar salvar no firebase, implementar o método dentro da classe User e chamar a função
                                //user.salvarNoFirebase();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            String exception;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                exception = "Digite uma senha mais forte";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Digite um email válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                exception = "Conta já cadastrada";
                            }catch (Exception e){
                                exception = "Erro ao cadastrar usuario: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(SignInActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
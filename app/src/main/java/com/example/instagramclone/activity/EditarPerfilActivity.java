package com.example.instagramclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.Permissao;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {
    private CircleImageView circleImagePerfil;
    private TextView textEditarFoto;
    private TextInputEditText editNome, editEmail;
    private Button botaoSalvar;
    private Usuario usuarioLogado;
    private String idUser;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

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
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        configuracoesIniciais();
        Permissao.validarPermissoes(permissoes, this, 1);

        //recuperar usuario atual
        firebaseUser = UsuarioFirebase.getUsuarioLogado();
        editNome.setText(firebaseUser.getDisplayName());
        editEmail.setText(firebaseUser.getEmail());
        //carregar imagem no circle image view
        Uri url = firebaseUser.getPhotoUrl();
        if(url != null){
            Glide.with(EditarPerfilActivity.this).load(url).into(circleImagePerfil);
        }else{
            circleImagePerfil.setImageResource(R.drawable.perfil_padrao);
        }

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
                    finish();
                }
            }
        });

        textEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //if(intent.resolveActivity(getPackageManager()) != null){
                    galeriaActivityResult.launch(intent);
                //}
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
        idUser = UsuarioFirebase.getIdUsuario();
    }

    private ActivityResultLauncher<Intent> galeriaActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Bitmap imagem = null;
                        try {
                            //seleção da galeria de foto
                            Uri localImagemSelecionado = result.getData().getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionado);

                            if(imagem != null){
                                //salvar imagem na tela do app
                                circleImagePerfil.setImageBitmap(imagem);

                                //recuperar dados para salvar a imagem
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                byte[] dadosImagem = byteArrayOutputStream.toByteArray();

                                //salvar imagem no storage do firebase
                                storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
                                StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("perfil")
                                        .child(idUser + ".jpeg");

                                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditarPerfilActivity.this, "Ocorreu um erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                atualizarFotoUsuario(url);
                                            }
                                        });
                                        Toast.makeText(EditarPerfilActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void atualizarFotoUsuario(Uri url){
        //Atualizar foto no firebase auth
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no firebase database
        usuarioLogado.setFoto(url.toString());
        usuarioLogado.atualizarNoFirebase();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
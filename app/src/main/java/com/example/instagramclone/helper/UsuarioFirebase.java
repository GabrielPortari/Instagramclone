package com.example.instagramclone.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagramclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {
    public static FirebaseUser getUsuarioLogado(){
        //retorna o usuario logado
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        return firebaseAuth.getCurrentUser();
    }

    public static String getIdUsuario(){
        FirebaseUser firebaseUser = getUsuarioLogado();
        return firebaseUser.getUid();
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioLogado();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());
        if(firebaseUser.getPhotoUrl() != null){
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }else{
            usuario.setFoto("");
        }
        return usuario;
    }

    public static void atualizarNomeUsuario(String nome){
        try{
            //recupera o usuario logado
            FirebaseUser usuarioLogado = getUsuarioLogado();

            //utiliza o profile change request pra buildar o novo perfil
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();

            //apos recuperar o usuario logado, utiliza o update profile para atualizar no firebase
            usuarioLogado.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("PERFIL", "Erro ao atualizar nome do perfil");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void atualizarFotoUsuario(Uri url){
        try{
            //recupera o usuario logado
            FirebaseUser usuarioLogado = getUsuarioLogado();

            //utiliza o profile change request pra buildar o novo perfil
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();

            //apos recuperar o usuario logado, utiliza o update profile para atualizar no firebase
            usuarioLogado.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("PERFIL", "Erro ao atualizar foto de perfil");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

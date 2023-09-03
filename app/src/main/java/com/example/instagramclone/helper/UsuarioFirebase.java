package com.example.instagramclone.helper;

import android.util.Log;

import androidx.annotation.NonNull;

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
                        Log.d("PERFIL", "Erro ao atualizar perfil");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

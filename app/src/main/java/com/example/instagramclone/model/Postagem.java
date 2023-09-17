package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Postagem implements Serializable {
    /*
    Modelo de postagem no firebase
    postagens.
        id_usuario.
            id_postagem. (push)
                descricao
                caminho_imagem
                id_usuario
     */

    private String idPostagem;
    private String idUsuario;
    private String descricao;
    private String caminhoImagem;

    public Postagem() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference postagemReference = databaseReference.child("postagens");
        String idPostagem = postagemReference.push().getKey();
        setIdPostagem(idPostagem);
    }
    public boolean salvarPostagemNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference postagensReference = databaseReference
                .child("postagens")
                .child(getIdUsuario())
                .child(getIdPostagem());
        postagensReference.setValue(this);
        return true;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

}

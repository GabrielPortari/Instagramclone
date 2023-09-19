package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Comentario {
    private String idComentario;
    private String idPostagem;
    private String idUsuario;
    private String nomeUsuario;
    private String caminhoFoto;
    private String comentario;

    public Comentario() {
    }

    public String getIdComentario() {
        return idComentario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public boolean salvarNoFirebase(){
        /*
        - comentarios
            .id_postagem
                .id_comentario
                    comentario
         */
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference comentariosReference = databaseReference
                .child("comentarios")
                .child(getIdPostagem());

        String comentarioId = comentariosReference.push().getKey();
        setIdComentario(comentarioId);
        comentariosReference.child(getIdComentario()).setValue(this);

        return true;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
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

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

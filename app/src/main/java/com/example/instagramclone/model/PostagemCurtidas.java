package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtidas {

    public Feed feed;
    public int qtCurtidas = 0;
    public Usuario usuario;

    public PostagemCurtidas() {
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();

        //objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nome", usuario.getNome());
        dadosUsuario.put("foto", usuario.getFoto());

        DatabaseReference postagensCurtidasReference = databaseReference
                .child("postagens-curtidas")
                .child(feed.getIdPostagem())
                .child(usuario.getId());
        postagensCurtidasReference.setValue(dadosUsuario);
        atualizarQuantidadeLikes(1);
    }

    public void atualizarQuantidadeLikes(int quantidade){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference postagensCurtidasReference = databaseReference
                .child("postagens-curtidas")
                .child(feed.getIdPostagem())
                .child("qtCurtidas");
        setQtCurtidas(getQtCurtidas()+quantidade);
        postagensCurtidasReference.setValue(getQtCurtidas());
    }
    public void removerQuantidadeLikes(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();

        DatabaseReference postagensCurtidasReference = databaseReference
                .child("postagens-curtidas")
                .child(feed.getIdPostagem())
                .child(usuario.getId());
        postagensCurtidasReference.removeValue();
        atualizarQuantidadeLikes(-1);
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getQtCurtidas() {
        return qtCurtidas;
    }

    public void setQtCurtidas(int qtCurtida) {
        this.qtCurtidas = qtCurtida;
    }
}

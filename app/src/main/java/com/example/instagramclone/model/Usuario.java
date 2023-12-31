package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String nome;
    private String nome_minusculo;
    private String email;
    private String senha;
    private String id;
    private String foto;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public String getFoto() {
        return foto;
    }

    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference userRef = databaseReference.child("usuarios").child(getId());
        userRef.setValue(this);
    }
    public void atualizarQuantidadePostagens(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference userRef = databaseReference.child("usuarios").child(getId());

        HashMap<String, Object> postagemMap = new HashMap<>();
        postagemMap.put("postagens", getPostagens());
        userRef.updateChildren(postagemMap);

        userRef.updateChildren(recuperarMap());
    }
    public void atualizarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        Map objeto = new HashMap<>();
        objeto.put("/usuarios/" + getId() + "/nome", getNome());
        objeto.put("/usuarios/" + getId() + "/foto", getFoto());

        databaseReference.updateChildren(objeto);

    }
    public Map<String, Object> recuperarMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("nome", getNome());
        usuarioMap.put("nome_minusculo", getNome_minusculo());
        usuarioMap.put("email", getEmail());
        usuarioMap.put("id", getId());
        usuarioMap.put("foto", getFoto());
        usuarioMap.put("seguidores", getSeguidores());
        usuarioMap.put("seguindo", getSeguindo());
        usuarioMap.put("postagens", getPostagens());

        return usuarioMap;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        this.nome_minusculo = nome.toLowerCase();
    }
    public String getNome_minusculo(){
        return this.nome_minusculo;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }
}

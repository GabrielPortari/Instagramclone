package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String id;
    private String foto;

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
    public void atualizarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference userRef = databaseReference.child("usuarios").child(getId());

        userRef.updateChildren(getMap());
    }
    public Map<String, Object> getMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("nome", getNome());
        usuarioMap.put("email", getEmail());
        usuarioMap.put("id", getId());
        usuarioMap.put("foto", getFoto());

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
}

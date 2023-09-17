package com.example.instagramclone.model;

import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    public boolean salvarPostagemNoFirebase(DataSnapshot seguidoresSnapshot){
        Map object = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();

        //referencia para a postagem
        String combinacaoId = "/" + getIdUsuario() + "/" + getIdPostagem();
        object.put("/postagens" + combinacaoId, this);

        //referencia para o feed
        /*
        feed
            .id_seguidor
                .id_postagem
                    .id_usuario_que_postou
         */
        for(DataSnapshot seguidores : seguidoresSnapshot.getChildren()){
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("caminhoImagem", getCaminhoImagem());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("idPostagem", getIdPostagem());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getFoto());

            String idSeguidor = seguidores.getKey();
            String idAtualizacao = "/" + idSeguidor + "/" + getIdPostagem();
            object.put("/feed" + idAtualizacao, dadosSeguidor);
        }

        databaseReference.updateChildren(object);

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

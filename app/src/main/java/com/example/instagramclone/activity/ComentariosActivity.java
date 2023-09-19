package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterComentarios;
import com.example.instagramclone.adapter.AdapterFeed;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Comentario;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editComentario;
    private Button buttonEnviar;
    private RecyclerView recyclerComentarios;
    private AdapterComentarios adapterComentarios;

    private List<Comentario> listaComentarios;
    private String idPostagem;
    private Usuario usuarioLogado;

    private DatabaseReference firebaseReference;
    private DatabaseReference comentariosReference;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }

        //configuracoes recycler view
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterComentarios = new AdapterComentarios(listaComentarios, getApplicationContext());
        recyclerComentarios.setAdapter(adapterComentarios);

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarComentario();
            }
        });
    }
    private void recuperarComentario(){
        comentariosReference = firebaseReference
                .child("comentarios")
                .child(idPostagem);

        valueEventListenerComentarios = comentariosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaComentarios.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listaComentarios.add(dataSnapshot.getValue(Comentario.class));
                }
                adapterComentarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configuracoesIniciais(){
        toolbar = findViewById(R.id.toolbarPrincipal);
        recyclerComentarios = findViewById(R.id.recyclerComentario);
        editComentario = findViewById(R.id.editTextComentario);
        buttonEnviar = findViewById(R.id.buttonEnviarComentario);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();

        listaComentarios = new ArrayList<>();

        //configurações da toolbar
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosReference.removeEventListener(valueEventListenerComentarios);
    }

    private void salvarComentario(){
        String textComentario = editComentario.getText().toString();
        if(textComentario != null && !textComentario.equals("")){
            Comentario comentario = new Comentario();

            comentario.setIdUsuario(usuarioLogado.getId());
            comentario.setCaminhoFoto(usuarioLogado.getFoto());
            comentario.setNomeUsuario(usuarioLogado.getNome());

            comentario.setIdPostagem(idPostagem);
            comentario.setComentario(textComentario);
            if(comentario.salvarNoFirebase()){
                Toast.makeText(ComentariosActivity.this, "Comentario enviado.", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(ComentariosActivity.this, "Digite algo para comentar!", Toast.LENGTH_SHORT).show();
        }
        editComentario.setText("");
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
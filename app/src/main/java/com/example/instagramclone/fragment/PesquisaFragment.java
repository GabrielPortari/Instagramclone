package com.example.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterPesquisa;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {
    private RecyclerView recyclerPesquisa;
    private SearchView searchView;
    private DatabaseReference databaseReference;
    private List<Usuario> listaUsuarioPesquisa;
    private AdapterPesquisa adapterPesquisa;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pesquisa, container, false);

        //inicializa os componentes de interface
        configuracoesIniciais(view);

        //configurar adapter
        adapterPesquisa = new AdapterPesquisa(listaUsuarioPesquisa, getActivity());

        //configurar recycler view
        recyclerPesquisa.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPesquisa.setLayoutManager(layoutManager);
        recyclerPesquisa.setAdapter(adapterPesquisa);

        //configuracoes do searchview
        searchView.setQueryHint("Buscar usuario");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*
                Faz a busca ao usuário selecionar o botao de submit, não utilizado
                 */
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //converte a busca para tudo minusculo
                String textPesquisado = newText.toLowerCase();
                //faz a busca a cada letra que o usuario digita
                pesquisarUsuario(textPesquisado);
                return true;
            }
        });


        return view;
    }
    public void configuracoesIniciais(View view){
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);
        searchView = view.findViewById(R.id.searchPesquisa);
        listaUsuarioPesquisa = new ArrayList<>();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios");
    }
    private void pesquisarUsuario(String s){
        //limpa a lista a cada letra digitada
        listaUsuarioPesquisa.clear();

        //pesquisar usuario
        if(s.length() > 1){
            Query query = databaseReference.orderByChild("nome_minusculo")
                    .startAt(s)
                    .endAt(s + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //limpa a lista a cada letra digitada
                    listaUsuarioPesquisa.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        listaUsuarioPesquisa.add(dataSnapshot.getValue(Usuario.class));
                    }
                    adapterPesquisa.notifyDataSetChanged();
                    int tam_lista = listaUsuarioPesquisa.size();
                    Log.i("Busca", "Busca: " + s);
                    Log.i("ListSize", "Tamanho da lista: " + tam_lista);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
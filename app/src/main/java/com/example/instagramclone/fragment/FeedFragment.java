package com.example.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterFeed;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerFeed;
    private AdapterFeed adapterFeed;
    private List<Feed> listaFeed;

    private String idUsuarioLogado;

    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference feedReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        configuracoesIniciais(view);

        //configuracoes recycler view
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterFeed = new AdapterFeed(listaFeed, getActivity());
        recyclerFeed.setAdapter(adapterFeed);

        return view;
    }
    private void configuracoesIniciais(View view){
        //inicializar componente
        recyclerFeed = view.findViewById(R.id.recyclerFeed);
        listaFeed = new ArrayList<>();

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        feedReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                .child("feed")
                .child(idUsuarioLogado);

    }
    private void recuperarFeed(){
        valueEventListenerFeed = feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    listaFeed.add(ds.getValue(Feed.class));
                }
                Collections.reverse(listaFeed);
                adapterFeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedReference.removeEventListener(valueEventListenerFeed);
    }
}
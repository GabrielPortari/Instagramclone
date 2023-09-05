package com.example.instagramclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instagramclone.R;
import com.example.instagramclone.activity.EditarPerfilActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private CircleImageView circleImagePerfil;
    private Button buttonAcaoPerfil;
    private GridView gridView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        textPublicacoes = view.findViewById(R.id.textPublicacoesPerfil);
        textSeguidores = view.findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = view.findViewById(R.id.textSeguindoPerfil);
        circleImagePerfil = view.findViewById(R.id.circleImageViewPerfil);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        gridView = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);

        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditarPerfilActivity.class));
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
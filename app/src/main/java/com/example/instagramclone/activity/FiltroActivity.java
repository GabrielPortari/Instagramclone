package com.example.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.instagramclone.R;

public class FiltroActivity extends AppCompatActivity {
    private ImageView imagemEscolhida;
    private Bitmap imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        configuracoesIniciais();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //recupera os dados recebidos do fragment postagem, e seta a imageview com a imagem recebida
            byte[] dadosImagem = bundle.getByteArray("imagemEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imagemEscolhida.setImageBitmap(imagem);
        }

    }
    private void configuracoesIniciais(){
        imagemEscolhida = findViewById(R.id.imageEscolhidaFiltro);
    }
}
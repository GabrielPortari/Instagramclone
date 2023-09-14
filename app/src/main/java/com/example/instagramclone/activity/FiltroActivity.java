package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterFiltroMiniatura;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.example.instagramclone.helper.FilterCustom;
import com.example.instagramclone.helper.RecyclerItemClickListener;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.model.Postagem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {
    static{ //carregando a biblioteca de filtros
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imagemEscolhida;
    private Bitmap imagem, imagemFiltro;
    private Toolbar toolbar;
    private TextInputEditText editTextDescricao;

    private AdapterFiltroMiniatura adapterFiltroMiniatura;
    private RecyclerView recyclerFiltroMiniatura;

    private List<FilterCustom> listaFiltros;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);
        configuracoesIniciais();

        //recupera os dados passados da outra intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //recupera os dados recebidos do fragment postagem, e seta a imageview com a imagem recebida
            byte[] dadosImagem = bundle.getByteArray("imagemEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imagemEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            //configuracoes recyclerview e adapter
            adapterFiltroMiniatura = new AdapterFiltroMiniatura(listaFiltros, imagem, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltroMiniatura.setLayoutManager(layoutManager);
            recyclerFiltroMiniatura.setAdapter(adapterFiltroMiniatura);

            //adiciona evento de click no recycler view
            recyclerFiltroMiniatura.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFiltroMiniatura,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //copia a imagem
                                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                                    //recupera o filtro do arraylist
                                    Filter filter = listaFiltros.get(position).getFilter();
                                    //seta a imagem escolhida no imageview
                                    imagemEscolhida.setImageBitmap(filter.processFilter(imagemFiltro));
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );
            //recuperar filtros
            recuperarFiltros();
        }

    }

    private void configuracoesIniciais(){
        imagemEscolhida = findViewById(R.id.imageEscolhidaFiltro);
        recyclerFiltroMiniatura = findViewById(R.id.recyclerFiltro);
        editTextDescricao = findViewById(R.id.inputEditTextDescricaoFiltro);


        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //configurações da toolbar
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_close_24);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
    private void recuperarFiltros(){
        listaFiltros.clear();
        listaFiltros.add(new FilterCustom("Padrao", new Filter()));
        listaFiltros.add(new FilterCustom("StarLit", SampleFilters.getStarLitFilter()));
        listaFiltros.add(new FilterCustom("BlueMess", SampleFilters.getBlueMessFilter()));
        listaFiltros.add(new FilterCustom("AweStruckVibe", SampleFilters.getAweStruckVibeFilter()));
        listaFiltros.add(new FilterCustom("LimeStutter", SampleFilters.getLimeStutterFilter()));
        listaFiltros.add(new FilterCustom("NightWhisper", SampleFilters.getNightWhisperFilter()));
        adapterFiltroMiniatura.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_publicar){
            //publicar a imagem
            publicarPostagem();
        }
        return super.onOptionsItemSelected(item);
    }
    private void publicarPostagem(){

        Postagem postagem = new Postagem();
        String descricao = editTextDescricao.getText().toString();
        String idPostagem = postagem.getIdPostagem();

        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(descricao);

        //recupera dados da imagem para o firebase
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] dadosImagem = byteArrayOutputStream.toByteArray();

        //salvar imagem no storage
        StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        StorageReference imagensReference = storageReference
                .child("imagens")
                        .child("postagens")
                                .child(idPostagem + ".jpeg");

        UploadTask uploadTask = imagensReference.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this, "Ocorreu um erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagensReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        postagem.setCaminhoImagem(url.toString());
                        if(postagem.salvarPostagemNoFirebase()){
                            Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
}
package com.example.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.instagramclone.R;
import com.example.instagramclone.fragment.FeedFragment;
import com.example.instagramclone.fragment.PerfilFragment;
import com.example.instagramclone.fragment.PesquisaFragment;
import com.example.instagramclone.fragment.PostagemFragment;
import com.example.instagramclone.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("InstagramClone");
        setSupportActionBar(toolbar);

        //Configurações iniciais
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        configuraBottomNavigationView(bottomNavigationView);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sair){
            deslogar();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogar(){
        try{
            firebaseAuth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void configuraBottomNavigationView(BottomNavigationView viewEx){
        //Item inicial selecionado
        Menu menu = viewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

        //Habilitando a navegação
        viewEx.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (itemId == R.id.ic_home) {
                    fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                    return true;
                } else if (itemId == R.id.ic_pesquisa) {
                    fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                    return true;
                } else if (itemId == R.id.ic_postagem) {
                    fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                    return true;
                } else if (itemId == R.id.ic_perfil) {
                    fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }
}
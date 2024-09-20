package com.example.mercadinho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void produtoView (View v){
        Button viewProduto = findViewById(R.id.btnProtudoView);
        viewProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, ProdutoActivity.class);
                startActivity(intencao);
            }
        });
    }

    public void clienteView (View v){
        Button viewCliente = findViewById(R.id.btnClienteView);
        viewCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intencao);
            }
        });
    }
}

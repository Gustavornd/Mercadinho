package com.example.mercadinho;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListaCompraActivity extends AppCompatActivity {
    private ListView lvListar;
    SQLiteDatabase banco;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_produto);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            lvListar = findViewById(R.id.lvListar);
            banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);
            montarLista();
            configurarClickListener();
            return insets;
        });
    }
//    @SuppressLint("deprecation")
//    private void montarLista(){
//        Cursor registros = banco.query("Compra", null, null, null, null, null, null, null);
//        String atributos[] = new String[] {"idCliente", "_id"};
//        int atributosTela[] = new int[] {android.R.id.text1, android.R.id.text2};
//        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(),
//                android.R.layout.two_line_list_item, registros, atributos, atributosTela);
//
//        adaptador.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                if (view.getId() == android.R.id.text2) {
//                    // Concatenar "unidade" e "preco"
//                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
//                    @SuppressLint("Range") String cliente = cursor.getString(cursor.getColumnIndex("idCliente"));
//                    @SuppressLint("Range") String valorCompra = cursor.getString(cursor.getColumnIndex("valorCompra"));
//                    String textoConcatenado = "Id: " + id + " | Cliente: " + cliente + " | Total: " + valorCompra;
//
//                    // Definir o texto concatenado no TextView da segunda linha
//                    ((TextView) view).setText(textoConcatenado);
//                    return true; // Informar que o valor foi definido manualmente
//                }
//                return false; // Deixe o adaptador lidar com o restante
//            }
//        });
//        lvListar.setAdapter(adaptador);
//    }

    @SuppressLint("deprecation")
    private void montarLista() {
        // Realizar a consulta com JOIN para obter o nome do cliente
        String query = "SELECT Compra._id, Compra.valorCompra, Cliente.nome " +
                "FROM Compra " +
                "JOIN Cliente ON Compra.idCliente = Cliente._id";

        Cursor registros = banco.rawQuery(query, null);

        // Definir os atributos a serem exibidos na lista
        String[] atributos = new String[]{"nome", "_id"};
        int[] atributosTela = new int[]{android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.two_line_list_item, registros, atributos, atributosTela);

        adaptador.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text2) {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String clienteNome = cursor.getString(cursor.getColumnIndex("nome"));
                    @SuppressLint("Range") String valorCompra = cursor.getString(cursor.getColumnIndex("valorCompra"));

                    // Concatenar o nome do cliente e o valor da compra
                    String textoConcatenado = "Id: " + id + " | Cliente: " + clienteNome + " | Total: " + valorCompra;

                    // Definir o texto concatenado no TextView da segunda linha
                    ((TextView) view).setText(textoConcatenado);
                    return true; // Informar que o valor foi definido manualmente
                }
                return false; // Deixe o adaptador lidar com o restante
            }
        });

        lvListar.setAdapter(adaptador);
    }

    private void configurarClickListener(){
        lvListar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListaCompraActivity.this, DetalhesCompraActivity.class);
                intent.putExtra("_id", id);
                startActivity(intent);
            }
        });
    }
}

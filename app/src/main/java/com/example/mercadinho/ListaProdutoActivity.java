package com.example.mercadinho;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class ListaProdutoActivity extends AppCompatActivity {
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
            return insets;
        });
    }
    @SuppressLint("deprecation")
    private void montarLista(){
        Cursor registros = banco.query("Produto", null, null, null, null, null, null, null);
        String atributos[] = new String[] {"descricao", "_id"};
        int atributosTela[] = new int[] {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.two_line_list_item, registros, atributos, atributosTela);

        adaptador.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text2) {
                    // Concatenar "unidade" e "preco"
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String unidade = cursor.getString(cursor.getColumnIndex("unidade"));
                    @SuppressLint("Range") String preco = cursor.getString(cursor.getColumnIndex("preco"));
                    String textoConcatenado = "Id: " + id + " | Unidade: " + unidade + " | Preço: " + preco;

                    // Definir o texto concatenado no TextView da segunda linha
                    ((TextView) view).setText(textoConcatenado);
                    return true; // Informar que o valor foi definido manualmente
                }
                return false; // Deixe o adaptador lidar com o restante
            }
        });
        lvListar.setAdapter(adaptador);
    }


}

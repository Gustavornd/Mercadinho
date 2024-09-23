package com.example.mercadinho;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class DetalhesCompraActivity extends AppCompatActivity {

    private TextView tvCliente, tvDataCompra, tvValorCompra, tvDataPagamento;
    private ListView lvItensCompra;
    private SQLiteDatabase banco;
    private long idCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compra);

        // Inicializar as views
        tvCliente = findViewById(R.id.tvCliente);
        tvDataCompra = findViewById(R.id.tvDataCompra);
        tvValorCompra = findViewById(R.id.tvValorCompra);
        tvDataPagamento = findViewById(R.id.tvDataPagamento);
        lvItensCompra = findViewById(R.id.lvItensCompra);

        // Receber o idCompra da intent
        idCompra = getIntent().getLongExtra("_id", -1);

        // Inicializar o banco de dados
        banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

        // Buscar os detalhes da compra
        buscarDetalhesCompra(idCompra);
    }

    private void buscarDetalhesCompra(long idCompra) {
        // Buscar os dados da compra
        Cursor cursorCompra = banco.rawQuery("SELECT c.idCliente, c.dataCompra, c.valorCompra, c.dataPagamento, cl.nome FROM Compra c JOIN Cliente cl ON c.idCliente = cl._id WHERE c._id = ?", new String[]{String.valueOf(idCompra)});

        if (cursorCompra.moveToFirst()) {
            // Preencher os campos com os dados da compra
            tvCliente.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("nome")));
            tvDataCompra.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("dataCompra")));
            tvValorCompra.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("valorCompra")));
            tvDataPagamento.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("dataPagamento")));
        }

        // Buscar os itens da compra com a coluna "_id" já disponível
        Cursor cursorItens = banco.rawQuery("SELECT i._id, p.descricao, i.quantidade, i.total FROM itemCompra i JOIN Produto p ON i.idProduto = p._id WHERE i.idCompra = ?", new String[]{String.valueOf(idCompra)});

        // Criar um SimpleCursorAdapter para exibir os itens no ListView
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursorItens,
                new String[]{"descricao", "total"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );
        lvItensCompra.setAdapter(adapter);
    }


}


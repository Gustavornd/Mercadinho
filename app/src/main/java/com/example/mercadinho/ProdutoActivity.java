package com.example.mercadinho;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProdutoActivity extends AppCompatActivity {

    private EditText etDescricao, etUnidade, etPreco, etCodigo;
    private SQLiteDatabase banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_produto);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            etCodigo = findViewById(R.id.etCodigo);
            etDescricao = findViewById(R.id.etDescricao);
            etUnidade = findViewById(R.id.etUnidade);
            etPreco = findViewById(R.id.etPreco);

            banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

            banco.execSQL("CREATE TABLE IF NOT EXISTS Produto (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao TEXT NOT NULL, " +
                    "unidade TEXT NOT NULL, " +
                    "preco REAL NOT NULL);");

            return insets;
        });
    }

    public void salvarProduto(View v){
        ContentValues registro = new ContentValues();
        registro.put("descricao", etDescricao.getText().toString());
        registro.put("unidade", etUnidade.getText().toString());
        registro.put("preco", etPreco.getText().toString());
        banco.insert("Produto", null, registro);
        Toast.makeText(this, "Resgistro Incluído com Sucesso!", Toast.LENGTH_LONG).show();
        limpaTudo();
    }

    public void alterarProduto(View v){
        int key = Integer.parseInt(etCodigo.getText().toString());
        ContentValues registro = new ContentValues();

        registro.put("descricao", etDescricao.getText().toString());
        registro.put("unidade", etUnidade.getText().toString());
        registro.put("preco", etPreco.getText().toString());

        banco.update("Produto", registro, "_id = " + key, null);
        Toast.makeText(this, "Resgistro Alterado com Sucesso!", Toast.LENGTH_LONG).show();
    }


    public void excluirProduto(View v) {
        final EditText etExcluir = new EditText(getApplicationContext());
        etExcluir.setTextColor(Color.BLACK);
        AlertDialog.Builder telaExcluir = new AlertDialog.Builder(this);
        telaExcluir.setTitle("Excluir");
        telaExcluir.setMessage("Código do produto a ser excluído: ");
        telaExcluir.setView(etExcluir);
        telaExcluir.setNegativeButton("Cancelar", null);
        telaExcluir.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int key = Integer.parseInt(etExcluir.getText().toString());

                // Verificar se o produto está relacionado a algum itemCompra
                Cursor cursor = banco.rawQuery("SELECT COUNT(*) FROM itemCompra WHERE idProduto = ?", new String[]{String.valueOf(key)});
                if (cursor != null) {
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cursor.close();

                    if (count > 0) {
                        // Produto relacionado a um itemCompra, não pode ser excluído
                        Toast.makeText(ProdutoActivity.this, "Erro: Produto relacionado a compras, não pode ser excluído!", Toast.LENGTH_LONG).show();
                    } else {
                        // Produto não está relacionado, pode ser excluído
                        int rowsAffected = banco.delete("Produto", "_id = ?", new String[]{String.valueOf(key)});
                        if (rowsAffected > 0) {
                            Toast.makeText(ProdutoActivity.this, "Produto excluído com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProdutoActivity.this, "Erro ao excluir o produto!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        telaExcluir.show();
        limpaTudo();
    }


    public void pesquisarProduto(View v){
        final EditText etPesquisa = new EditText(getApplicationContext());
        etPesquisa.setTextColor(Color.BLACK);
        AlertDialog.Builder telaPesquisa = new AlertDialog.Builder(this);
        telaPesquisa.setTitle("Pesquisar");
        telaPesquisa.setMessage("Código a ser pesquisado: ");
        telaPesquisa.setView(etPesquisa);
        telaPesquisa.setNegativeButton("Cancelar", null);
        telaPesquisa.setPositiveButton("Pesquisar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                executarPesquisa(Integer.parseInt(etPesquisa.getText().toString()));
            }
        });
        telaPesquisa.show();
    }
    @SuppressLint("Range")
    protected void executarPesquisa(int id){
        Cursor registros = banco.query("Produto", null, " _id = " + id,
                null, null, null, null);
        if(registros.moveToNext()){
            String descricaoProduto = registros.getString(registros.getColumnIndex("descricao"));
            String unidadePrduto = registros.getString(registros.getColumnIndex("unidade"));
            String precoProduto = registros.getString(registros.getColumnIndex("preco"));
            etCodigo.setText(String.valueOf(id));
            etDescricao.setText(String.valueOf(descricaoProduto));
            etUnidade.setText(String.valueOf(unidadePrduto));
            etPreco.setText(String.valueOf(precoProduto));
        }
    }

    public void listarProdutos(View v){
        Intent intencao = new Intent(ProdutoActivity.this, ListaProdutoActivity.class);
        startActivity(intencao);
    }

    public void limpaTudo(){
        etCodigo.setText("");
        etDescricao.setText("");
        etUnidade.setText("");
        etPreco.setText("");
    }
}
package com.example.mercadinho;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CompraActivity extends AppCompatActivity {
    private EditText etIdCliente, etDataCompra, etValorCompra, etDataPagamento, etIdCompra, etIdProduto, etQuantidade;
    private SQLiteDatabase banco;

    private List<ItemCompra> itensCompra = new ArrayList<>();

    private ListView lvItensCompra; // Defina essa variável na sua Activity
    private ArrayAdapter<String> adapter;

    double valorCompra = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);


        etIdCliente = findViewById(R.id.etIdCliente);
        etDataCompra = findViewById(R.id.etDataCompra);
        etValorCompra = findViewById(R.id.etValorCompra);
        etDataPagamento = findViewById(R.id.etDataPagamento);
        etIdCompra = findViewById(R.id.etIdCompra);
        etIdProduto = findViewById(R.id.etIdProduto);
        etQuantidade = findViewById(R.id.etQuantidade);
        lvItensCompra = findViewById(R.id.lvItensCompra);

        banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

        //banco.execSQL("DROP TABLE IF EXISTS Compra");
        banco.execSQL("CREATE TABLE IF NOT EXISTS Compra (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "idCliente INTEGER REFERENCES cliente(_id) NOT NULL, " +
                "dataCompra DATE NOT NULL, " +
                "valorCompra REAL NOT NULL, " +
                "dataPagamento DATE);"
        );

        //banco.execSQL("DROP TABLE IF EXISTS itemCompra");
        banco.execSQL("CREATE TABLE IF NOT EXISTS itemCompra (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "idCompra INTEGER REFERENCES compra(_id) NOT NULL, " +
                "idProduto INTEGER REFERENCES produto(_id) NOT NULL, " +
                "unitario REAL NOT NULL, " +
                "quantidade REAL NOT NULL, " +
                "total REAL NOT NULL);"
        );

        //banco.execSQL("UPDATE Compra SET dataPagamento = NULL WHERE dataPagamento = '';\n");

        etIdCliente.setOnClickListener(v->{
            List<Cliente> clientes = getClientes();

            // Cria uma lista de nomes de clientes
            String[] nomesClientes = new String[clientes.size()];
            for (int i = 0; i < clientes.size(); i++) {
                nomesClientes[i] = clientes.get(i).getNome();
            }

            // Cria e mostra o AlertDialog com os nomes dos clientes
            AlertDialog.Builder builder = new AlertDialog.Builder(CompraActivity.this);
            builder.setTitle("Selecione um Cliente")
                    .setItems(nomesClientes, (dialog, which) -> {
                        // Quando um cliente for selecionado, salva o ID dele no EditText
                        int clienteId = clientes.get(which).getId();
                        etIdCliente.setText(String.valueOf(clienteId));
                    });
            builder.show();
        });

        etIdProduto.setOnClickListener(v->{
            List<Produto> produtos = getProdutos();

            String[] nomesProdutos = new String[produtos.size()];
            for (int i = 0; i < produtos.size(); i++) {
                nomesProdutos[i] = produtos.get(i).getDescricao();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(CompraActivity.this);
            builder.setTitle("Selecione um Produto")
                    .setItems(nomesProdutos, (dialog, which) -> {
                        // Quando um cliente for selecionado, salva o ID dele no EditText
                        int produtoId = produtos.get(which).getId();
                        etIdProduto.setText(String.valueOf(produtoId));
                    });
            builder.show();
        });


    }

    public void adicionarItemCompra (View v){
        try{
            int idProduto = Integer.parseInt(etIdProduto.getText().toString());
            double quantidade = Double.parseDouble(etQuantidade.getText().toString());

            double unitario = obterPrecoUnitario(idProduto);
            if(unitario == 0){
                Toast.makeText(this, "Produto não encontrado!", Toast.LENGTH_LONG).show();
                return;
            }

            double total = unitario * quantidade;

            valorCompra += total;

            ItemCompra item = new ItemCompra(idProduto, unitario, quantidade, total);
            itensCompra.add(item);

            atualizarListaItensCompra();

            etIdProduto.setText("");
            etQuantidade.setText("");
            etValorCompra.setText(new String(String.valueOf(valorCompra)));

            Toast.makeText(this, "Item adicionado à compra!", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(this, "Erro ao adicionar item. Verifique os dados", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public double obterPrecoUnitario(int idProduto){
        Cursor cursor = banco.rawQuery("SELECT preco FROM Produto WHERE _id = ?", new String[]{String.valueOf(idProduto)});
        if (cursor.moveToFirst()){
            return cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
        }
        return 0;
    }

    public void salvarCompra(View v){
        ContentValues valoresCompra = new ContentValues();
        valoresCompra.put("idCliente", etIdCliente.getText().toString());
        valoresCompra.put("dataCompra", etDataCompra.getText().toString());
        valoresCompra.put("valorCompra", etValorCompra.getText().toString());

        if(!etDataPagamento.getText().toString().isEmpty()){
            valoresCompra.put("dataPagamento", etDataPagamento.getText().toString());
        } else {
           valoresCompra.putNull("dataPagamento");
        }

        long idCompra = banco.insert("Compra", null, valoresCompra);
        for (ItemCompra item : itensCompra){
            ContentValues valoresItem = new ContentValues();
            valoresItem.put("idCompra", idCompra);
            valoresItem.put("idProduto", item.getIdProduto());
            valoresItem.put("unitario", item.getUnitario());
            valoresItem.put("quantidade", item.getQuantidade());
            valoresItem.put("total", item.getTotal());

            banco.insert("itemCompra", null, valoresItem);
        }

        Toast.makeText(this, "Compra salva com sucesso!", Toast.LENGTH_LONG).show();
        limpaTudo();
    }

    public void excluirCompra(View v) {
        final EditText etExcluir = new EditText(getApplicationContext());
        etExcluir.setTextColor(Color.BLACK);
        AlertDialog.Builder telaExcluir = new AlertDialog.Builder(this);
        telaExcluir.setTitle("Excluir");
        telaExcluir.setMessage("Código a ser excluido: ");
        telaExcluir.setView(etExcluir);
        telaExcluir.setNegativeButton("Cancelar", null);
        telaExcluir.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int key = Integer.parseInt(etExcluir.getText().toString());
                banco.delete("Compra", "_id = ?", new String[]{String.valueOf(key)});
                banco.delete("itemCompra", "idCompra = ?", new String[]{String.valueOf(key)});
                Toast.makeText(CompraActivity.this, "Registro Excluido com Sucesso!", Toast.LENGTH_LONG).show();
            }
        });
        telaExcluir.show();

        Toast.makeText(this, "Compra excluída com sucesso!", Toast.LENGTH_LONG).show();
        limpaTudo();
    }

    public void alterarCompra(View v) {
        try {
            // Obter o ID da compra a ser alterada
            int idCompra = Integer.parseInt(etIdCompra.getText().toString());

            // Atualizar os dados da compra na tabela Compra
            ContentValues valoresCompra = new ContentValues();
            valoresCompra.put("idCliente", etIdCliente.getText().toString());
            valoresCompra.put("dataCompra", etDataCompra.getText().toString());
            valoresCompra.put("valorCompra", etValorCompra.getText().toString());

            if(!etDataPagamento.getText().toString().isEmpty()){
                valoresCompra.put("dataPagamento", etDataPagamento.getText().toString());
            } else {
                valoresCompra.putNull("dataPagamento");
            }

            // Executar o update da compra no banco de dados
            banco.update("Compra", valoresCompra, "_id = ?", new String[]{String.valueOf(idCompra)});

            // Excluir os itens antigos associados à compra
            banco.delete("itemCompra", "idCompra = ?", new String[]{String.valueOf(idCompra)});

            // Inserir os novos itens de compra
            for (ItemCompra item : itensCompra) {
                ContentValues valoresItem = new ContentValues();
                valoresItem.put("idCompra", idCompra);  // Associar o item à compra
                valoresItem.put("idProduto", item.getIdProduto());
                valoresItem.put("unitario", item.getUnitario());
                valoresItem.put("quantidade", item.getQuantidade());
                valoresItem.put("total", item.getTotal());

                // Inserir o novo item na tabela itemCompra
                banco.insert("itemCompra", null, valoresItem);
            }

            Toast.makeText(this, "Compra alterada com sucesso!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao alterar a compra.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void listarCompras(View v) {
        Intent intent = new Intent(CompraActivity.this, ListaCompraActivity.class);
        startActivity(intent);
    }


    private void atualizarListaItensCompra() {
        // Criar uma lista de Strings para exibir os itens
        List<String> listaItens = new ArrayList<>();

        // Preencher a lista de itens a partir dos objetos ItemCompra
        for (ItemCompra item : itensCompra) {
            String descricaoItem = "Produto ID: " + item.getIdProduto()
                    + " | Quantidade: " + item.getQuantidade()
                    + " | Total: R$" + String.format("%.2f", item.getTotal());
            listaItens.add(descricaoItem);
        }

        // Configurar o ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaItens);

        // Definir o adapter no ListView
        lvItensCompra.setAdapter(adapter);
    }

    public void pesquisarCompra(View V){
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

    public void executarPesquisa(int id) {
        try {
            // Pesquisar os dados da compra na tabela Compra
            Cursor cursorCompra = banco.rawQuery("SELECT * FROM Compra WHERE _id = ?", new String[]{String.valueOf(id)});

            if (cursorCompra.moveToFirst()) {
                // Se a compra for encontrada, preencher os campos com os dados
                etIdCompra.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("_id")));
                etIdCliente.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("idCliente")));
                etDataCompra.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("dataCompra")));
                etValorCompra.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("valorCompra")));
                etDataPagamento.setText(cursorCompra.getString(cursorCompra.getColumnIndexOrThrow("dataPagamento")));

                // Agora, pesquisar os itens dessa compra na tabela itemCompra
                Cursor cursorItens = banco.rawQuery("SELECT * FROM itemCompra WHERE idCompra = ?", new String[]{String.valueOf(id)});

                // Limpar a lista de itens para garantir que estamos atualizando com os itens corretos
                itensCompra.clear();

                while (cursorItens.moveToNext()) {
                    // Obter dados de cada item da compra
                    int idProduto = cursorItens.getInt(cursorItens.getColumnIndexOrThrow("idProduto"));
                    double unitario = cursorItens.getDouble(cursorItens.getColumnIndexOrThrow("unitario"));
                    double quantidade = cursorItens.getDouble(cursorItens.getColumnIndexOrThrow("quantidade"));
                    double total = cursorItens.getDouble(cursorItens.getColumnIndexOrThrow("total"));

                    // Criar um novo objeto ItemCompra e adicioná-lo à lista
                    ItemCompra item = new ItemCompra(idProduto, unitario, quantidade, total);
                    itensCompra.add(item);
                }

                // Atualizar visualmente a lista de itens na interface (RecyclerView/ListView)
                atualizarListaItensCompra();

                Toast.makeText(this, "Compra carregada com sucesso!", Toast.LENGTH_LONG).show();

            } else {
                // Caso a compra não seja encontrada
                Toast.makeText(this, "Compra não encontrada.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao pesquisar compra.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void limpaTudo(){
        etValorCompra.setText("");
        etQuantidade.setText("");
        etIdProduto.setText("");
        etDataCompra.setText("");
        etDataPagamento.setText("");
        etIdCompra.setText("");
        etIdCliente.setText("");
        itensCompra.clear();
        valorCompra = 0.0;
        atualizarListaItensCompra();
    }

    private List<Cliente> getClientes() {
        List<Cliente> clientes = new ArrayList<>();

        Cursor cursor = banco.rawQuery("SELECT _id, nome FROM Cliente", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                clientes.add(new Cliente(id, nome));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clientes;
    }

    private List<Produto> getProdutos(){
        List<Produto> produtos = new ArrayList<>();
        Cursor cursor = banco.rawQuery("SELECT _id, descricao FROM Produto", null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                produtos.add(new Produto(id, descricao));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return produtos;
    }

}

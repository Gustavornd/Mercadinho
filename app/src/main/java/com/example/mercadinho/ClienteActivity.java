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
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ClienteActivity extends AppCompatActivity {
    private EditText etCodigo, etNome, etCpf, etEmail, etTelefone, etLogradouro,
                            etNumero, etComplemento, etBairro,
                            etCidade, etEstado, etCep, etDiaVencimento;

    private SQLiteDatabase banco;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->{
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            etCodigo = findViewById(R.id.etCodigo);
            etNome = findViewById(R.id.etNome);
            etCpf = findViewById(R.id.etCpf);
            etEmail = findViewById(R.id.etEmail);
            etTelefone = findViewById(R.id.etTelefone);
            etLogradouro = findViewById(R.id.etLogradouro);
            etNumero = findViewById(R.id.etNumero);
            etComplemento = findViewById(R.id.etComplemento);
            etBairro = findViewById(R.id.etBairro);
            etCidade = findViewById(R.id.etCidade);
            etEstado = findViewById(R.id.etEstado);
            etCep = findViewById(R.id.etCep);
            etDiaVencimento = findViewById(R.id.etDiaVencimento);

            banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

            //banco.execSQL("DROP TABLE IF EXISTS Cliente");
            banco.execSQL("CREATE TABLE IF NOT EXISTS Cliente(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "nome TEXT(50) NOT NULL," +
                    "cpf TEXT(11) NOT NULL," +
                    "email TEXT(100) NOT NULL," +
                    "telefone TEXT(11)," +
                    "logradouro TEXT(50) NOT NULL," +
                    "numero TEXT(10) NOT NULL," +
                    "complemento TEXT(20)," +
                    "bairro TEXT(50) NOT NULL," +
                    "cidade TEXT(50) NOT NULL," +
                    "estado TEXT(2) NOT NULL," +
                    "cep TEXT(8) NOT NULL," +
                    "diaVencimento TEXT(10) NOT NULL);"
            );
            return insets;
        });
    }

    public void salvarCliente(View c){
        ContentValues registros = new ContentValues();

        registros.put("nome", etNome.getText().toString());
        registros.put("cpf", etCpf.getText().toString());
        registros.put("email", etEmail.getText().toString());
        registros.put("telefone", etTelefone.getText().toString());
        registros.put("logradouro", etLogradouro.getText().toString());
        registros.put("numero", etNumero.getText().toString());
        registros.put("complemento", etComplemento.getText().toString());
        registros.put("bairro", etBairro.getText().toString());
        registros.put("cidade", etCidade.getText().toString());
        registros.put("estado", etEstado.getText().toString());
        registros.put("cep", etCep.getText().toString());
        registros.put("diaVencimento", etDiaVencimento.getText().toString());

        banco.insert("Cliente", null, registros);
        Toast.makeText(this, "Registro Incluído com Sucesso!", Toast.LENGTH_LONG).show();
    }

    public void alterarCliente(View v){
        int key = Integer.parseInt(etCodigo.getText().toString());
        ContentValues registros = new ContentValues();

        registros.put("nome", etNome.getText().toString());
        registros.put("cpf", etCpf.getText().toString());
        registros.put("email", etEmail.getText().toString());
        registros.put("telefone", etTelefone.getText().toString());
        registros.put("logradouro", etLogradouro.getText().toString());
        registros.put("numero", etNumero.getText().toString());
        registros.put("complemento", etComplemento.getText().toString());
        registros.put("bairro", etBairro.getText().toString());
        registros.put("cidade", etCidade.getText().toString());
        registros.put("estado", etEstado.getText().toString());
        registros.put("cep", etCep.getText().toString());
        registros.put("diaVencimento", etDiaVencimento.getText().toString());

        banco.update("Cliente", registros, "_id = " + key, null);
        Toast.makeText(this, "Registro Alterado com Sucesso!", Toast.LENGTH_LONG).show();

    }

    public void excluirCliente(View v){
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
                banco.delete("Cliente", "_id = " + key, null);
                Toast.makeText(ClienteActivity.this, "Registro Excluido com Sucesso!", Toast.LENGTH_LONG).show();
            }
        });
        telaExcluir.show();
    }

    public void pesquisarCliente(View v){
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
        Cursor registros = banco.query("Cliente", null, " _id = " + id,
                null, null, null, null);
        if(registros.moveToNext()){
            String nome = registros.getString(registros.getColumnIndex("nome"));
            String cpf = registros.getString(registros.getColumnIndex("cpf"));
            String email = registros.getString(registros.getColumnIndex("email"));
            String telefone = registros.getString(registros.getColumnIndex("telefone"));
            String logradouro = registros.getString(registros.getColumnIndex("logradouro"));
            String numero = registros.getString(registros.getColumnIndex("numero"));
            String complemento = registros.getString(registros.getColumnIndex("complemento"));
            String bairro = registros.getString(registros.getColumnIndex("bairro"));
            String cidade = registros.getString(registros.getColumnIndex("cidade"));
            String estado = registros.getString(registros.getColumnIndex("estado"));
            String cep = registros.getString(registros.getColumnIndex("cep"));
            String diaVencimento = registros.getString(registros.getColumnIndex("diaVencimento"));


            etCodigo.setText(String.valueOf(id));
            etNome.setText(String.valueOf(nome));
            etCpf.setText(String.valueOf(cpf));
            etEmail.setText(String.valueOf(email));
            etTelefone.setText(String.valueOf(telefone));
            etLogradouro.setText(String.valueOf(logradouro));
            etNumero.setText(String.valueOf(numero));
            etComplemento.setText(String.valueOf(complemento));
            etBairro.setText(String.valueOf(bairro));
            etCidade.setText(String.valueOf(cidade));
            etEstado.setText(String.valueOf(estado));
            etCep.setText(String.valueOf(cep));
            etDiaVencimento.setText(String.valueOf(diaVencimento));
        }
    }

    public void listarCliente(View v){
        Intent intencao = new Intent(ClienteActivity.this, ListaClienteActivity.class);
        startActivity(intencao);
    }

}

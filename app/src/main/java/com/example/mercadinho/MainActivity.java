package com.example.mercadinho;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    private SQLiteDatabase banco;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

        Button viewProduto = findViewById(R.id.btnProtudoView);
        viewProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, ProdutoActivity.class);
                startActivity(intencao);
            }
        });


        Button viewCliente = findViewById(R.id.btnClienteView);
        viewCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intencao);
            }
        });


        Button viewCompra = findViewById(R.id.btnCompraView);
        viewCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, CompraActivity.class);
                startActivity(intencao);
            }
        });

        Button viewRelatorio = findViewById(R.id.btnRelatorio);
        viewRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, RelatorioActivity.class);
                startActivity(intencao);
            }
        });



        Button btnCarregarDados = findViewById(R.id.btnCarregarDados);
        btnCarregarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                banco.execSQL("DROP TABLE IF EXISTS Cliente");
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
                        "diaVencimento TEXT(2) NOT NULL);"
                );

                banco.execSQL("DROP TABLE IF EXISTS Produto");
                banco.execSQL("CREATE TABLE IF NOT EXISTS Produto (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "descricao TEXT NOT NULL, " +
                        "unidade TEXT NOT NULL, " +
                        "preco REAL NOT NULL);");

                banco.execSQL("DROP TABLE IF EXISTS Compra");
                banco.execSQL("CREATE TABLE IF NOT EXISTS Compra (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "idCliente INTEGER REFERENCES cliente(_id) NOT NULL, " +
                        "dataCompra DATE NOT NULL, " +
                        "valorCompra REAL NOT NULL, " +
                        "dataPagamento DATE);"
                );

                banco.execSQL("DROP TABLE IF EXISTS itemCompra");
                banco.execSQL("CREATE TABLE IF NOT EXISTS itemCompra (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "idCompra INTEGER REFERENCES compra(_id) NOT NULL, " +
                        "idProduto INTEGER REFERENCES produto(_id) NOT NULL, " +
                        "unitario REAL NOT NULL, " +
                        "quantidade REAL NOT NULL, " +
                        "total REAL NOT NULL);"
                );



                banco.execSQL("INSERT INTO Cliente (nome, cpf, email, telefone, logradouro, numero, complemento, bairro, cidade, estado, cep, diaVencimento) " +
                    "VALUES ('João da Silva', '12345678901', 'joao@gmail.com', '21987654321', 'Rua das Flores', '123', 'Apto 1', 'Centro', 'Rio de Janeiro', 'RJ', '20000000', '10');");

                banco.execSQL("INSERT INTO Cliente (nome, cpf, email, telefone, logradouro, numero, complemento, bairro, cidade, estado, cep, diaVencimento) " +
                    "VALUES ('Maria Oliveira', '23456789012', 'maria@hotmail.com', '21998765432', 'Avenida Brasil', '456', 'Bloco B', 'Zona Sul', 'São Paulo', 'SP', '30000000', '15');");

                banco.execSQL("INSERT INTO Cliente (nome, cpf, email, telefone, logradouro, numero, complemento, bairro, cidade, estado, cep, diaVencimento) " +
                    "VALUES ('Pedro Santos', '34567890123', 'pedro@yahoo.com', '21987654321', 'Rua Almeida', '789', '', 'Bela Vista', 'Curitiba', 'PR', '40000000', '5');");

                banco.execSQL("INSERT INTO Cliente (nome, cpf, email, telefone, logradouro, numero, complemento, bairro, cidade, estado, cep, diaVencimento) " +
                    "VALUES ('Ana Souza', '45678901234', 'ana@gmail.com', '21976543210', 'Travessa da Paz', '321', 'Casa 2', 'Centro', 'Belo Horizonte', 'MG', '50000000', '20');");

                banco.execSQL("INSERT INTO Cliente (nome, cpf, email, telefone, logradouro, numero, complemento, bairro, cidade, estado, cep, diaVencimento) " +
                    "VALUES ('Carlos Lima', '56789012345', 'carlos@gmail.com', '21965432109', 'Rua do Sol', '654', 'Sala 101', 'Jardim', 'Salvador', 'BA', '60000000', '12');");


                banco.execSQL("INSERT INTO Produto (descricao, unidade, preco) " +
                    "VALUES ('Arroz 5kg', 'Pacote', 25.90);");

                banco.execSQL("INSERT INTO Produto (descricao, unidade, preco) " +
                    "VALUES ('Feijão 1kg', 'Pacote', 7.50);");

                banco.execSQL("INSERT INTO Produto (descricao, unidade, preco) " +
                    "VALUES ('Macarrão 500g', 'Pacote', 4.30);");
                banco.execSQL("INSERT INTO Produto (descricao, unidade, preco) " +
                    "VALUES ('Óleo de soja 900ml', 'Garrafa', 9.90);");

                banco.execSQL("INSERT INTO Produto (descricao, unidade, preco) " +
                    "VALUES ('Açúcar 1kg', 'Pacote', 4.80);");



                banco.execSQL("INSERT INTO Compra (idCliente, dataCompra, valorCompra, dataPagamento)" +
                        "VALUES (1, '09/08/2024', 64.30, NULL);");

                banco.execSQL("INSERT INTO itemCompra (idCompra, idProduto, unitario, quantidade, total)" +
                        "VALUES" +
                        "((SELECT _id FROM Compra WHERE idCliente = 1 AND dataCompra = '09/08/2024'), 1, 25.90, 2, 51.80)," +
                        "((SELECT _id FROM Compra WHERE idCliente = 1 AND dataCompra = '09/08/2024'), 2, 7.50, 2, 15.00);");

                banco.execSQL("INSERT INTO Compra (idCliente, dataCompra, valorCompra, dataPagamento)" +
                        "VALUES (1, '20/09/2024', 63.90, NULL);");

                banco.execSQL("INSERT INTO itemCompra (idCompra, idProduto, unitario, quantidade, total)" +
                        "VALUES" +
                        "    ((SELECT _id FROM Compra WHERE idCliente = 1 AND dataCompra = '20/09/2024'), 4, 9.90, 3, 29.70)," +
                        "    ((SELECT _id FROM Compra WHERE idCliente = 1 AND dataCompra = '20/09/2024'), 5, 4.80, 2, 9.60)," +
                        "    ((SELECT _id FROM Compra WHERE idCliente = 1 AND dataCompra = '20/09/2024'), 3, 4.30, 6, 25.80);");

                banco.execSQL("UPDATE Compra SET dataPagamento = NULL WHERE dataPagamento = '';");
            }
        });

    }

}

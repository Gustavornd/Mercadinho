package com.example.mercadinho;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RelatorioActivity extends AppCompatActivity {
    private EditText etIdCliente, etNomeCliente;
    private TextView tvTotalReceber, tvTotalAtrasado;
    SQLiteDatabase banco;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_relatorio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etIdCliente = findViewById(R.id.etIdCliente);
        etNomeCliente = findViewById(R.id.etNomeCliente);
        tvTotalReceber = findViewById(R.id.tvTotalReceber);
        tvTotalAtrasado = findViewById(R.id.tvTotalAtrasado);

        banco = this.openOrCreateDatabase("banco", Context.MODE_PRIVATE, null);

        etIdCliente.setOnClickListener(v->{
            List<Cliente> clientes = getClientes();

            String[] nomesClientes = new String[clientes.size()];
            for (int i = 0; i < clientes.size(); i++) {
                nomesClientes[i] = clientes.get(i).getNome();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(RelatorioActivity.this);
            builder.setTitle("Selecione um Cliente")
                    .setItems(nomesClientes, (dialog, which) -> {
                        int clienteId = clientes.get(which).getId();
                        String nome = clientes.get(which).getNome();
                        etIdCliente.setText(String.valueOf(clienteId));
                        etNomeCliente.setText(nome);
                        carregarTotalReceber(clienteId);
                        carregarComprasAtrasadas(clienteId);
                    });
            builder.show();
        });

        etNomeCliente.setOnClickListener(v->{
            List<Cliente> clientes = getClientes();

            String[] nomesClientes = new String[clientes.size()];
            for (int i = 0; i < clientes.size(); i++) {
                nomesClientes[i] = clientes.get(i).getNome();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(RelatorioActivity.this);
            builder.setTitle("Selecione um Cliente")
                    .setItems(nomesClientes, (dialog, which) -> {
                        int clienteId = clientes.get(which).getId();
                        String nome = clientes.get(which).getNome();
                        etIdCliente.setText(String.valueOf(clienteId));
                        etNomeCliente.setText(nome);
                        carregarComprasAtrasadas(clienteId);
                        carregarTotalReceber(clienteId);
                    });
            builder.show();
        });

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


    // Método para calcular o total a receber do cliente
    private void carregarTotalReceber(int clienteId) {
        Cursor cursor = banco.rawQuery("SELECT SUM(valorCompra) AS totalReceber FROM Compra " +
                "WHERE idCliente = ? AND dataPagamento IS NULL", new String[]{String.valueOf(clienteId)});

        if (cursor.moveToFirst()) {
            // Verificar se o resultado da soma é nulo
            double totalReceber = cursor.isNull(cursor.getColumnIndexOrThrow("totalReceber")) ? 0.0
                    : cursor.getDouble(cursor.getColumnIndexOrThrow("totalReceber"));
            tvTotalReceber.setText("Total a Receber: R$ " + totalReceber);


            Cursor detalheCursor = banco.rawQuery("SELECT valorCompra, dataPagamento FROM Compra " +
                    "WHERE idCliente = ? AND dataPagamento IS NULL", new String[]{String.valueOf(clienteId)});
            if (detalheCursor.moveToFirst()) {
                do {
                    double valorCompra = detalheCursor.getDouble(detalheCursor.getColumnIndexOrThrow("valorCompra"));
                } while (detalheCursor.moveToNext());
            }
            detalheCursor.close();

        } else {
            tvTotalReceber.setText("Total a Receber: R$ 0.0");
        }

        cursor.close();
    }

    @SuppressLint("Range")
    private void carregarComprasAtrasadas(int idCliente) {
        double totalAtrasado = 0.0;
        Cursor cursor = banco.rawQuery("SELECT Compra._id, Compra.idCliente, Compra.dataCompra, Compra.valorCompra, Cliente.nome, Cliente.diaVencimento " +
                "FROM Compra " +
                "JOIN Cliente ON Compra.idCliente = Cliente._id " +
                "WHERE Compra.dataPagamento IS NULL AND Cliente._id = ?", new String[]{String.valueOf(idCliente)});

        // Verifica se o cursor contém dados
        if (cursor != null && cursor.moveToFirst()) {

            List<String> comprasAtrasadas = new ArrayList<>();
            Calendar dataAtual = Calendar.getInstance();

            do {
                int idCompra = cursor.getInt(cursor.getColumnIndex("_id"));
                String dataCompraStr = cursor.getString(cursor.getColumnIndex("dataCompra"));
                int diaVencimento = cursor.getInt(cursor.getColumnIndex("diaVencimento"));
                double valorCompra = cursor.getDouble(cursor.getColumnIndex("valorCompra"));

                // Alterado o formato da data para "dd/MM/yyyy"
                SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date dataCompra = null;
                try {
                    dataCompra = formatoData.parse(dataCompraStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dataCompra != null) {
                    Calendar dataVencimento = Calendar.getInstance();
                    dataVencimento.setTime(dataCompra);
                    dataVencimento.set(Calendar.DAY_OF_MONTH, diaVencimento);

                    if (dataVencimento.before(dataAtual)) {
                        String compraDetalhes = "Id: " + idCompra + " | Valor: R$ " + valorCompra + " | Data Compra: " + dataCompraStr;
                        comprasAtrasadas.add(compraDetalhes);
                        totalAtrasado += valorCompra;
                    }
                }
            } while (cursor.moveToNext());

            // Exibir as compras atrasadas na ListView
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comprasAtrasadas);
            ListView lvComprasAtrasadas = findViewById(R.id.lvComprasAtrasadas);
            lvComprasAtrasadas.setAdapter(adaptador);
            tvTotalAtrasado.setText("Total em Atraso: R$ "+totalAtrasado);

        } else {
            tvTotalAtrasado.setText("Total em Atraso: R$ "+totalAtrasado);
        }

        cursor.close();
    }

}

package com.example.mercadinho;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    ListView lvComprasAtrasadas;
    double totalAtrasado = 0.0, totalReceber =0.0;
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
        lvComprasAtrasadas = findViewById(R.id.lvComprasAtrasadas);

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

        lvComprasAtrasadas.setOnItemClickListener((parent, view, position, id) -> {
            String compraSelecionada = (String) parent.getItemAtPosition(position);

            // Extraindo o ID da compra do texto
            int idCompra = extrairIdCompra(compraSelecionada);

            // Mostrar a janela popup para alterar a data de pagamento
            mostrarPopupAlterarData(idCompra);
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
             totalReceber = cursor.isNull(cursor.getColumnIndexOrThrow("totalReceber")) ? 0.0
                    : cursor.getDouble(cursor.getColumnIndexOrThrow("totalReceber"));


            tvTotalReceber.setText(String.format(Locale.getDefault(), "Total a Receber: R$ %.2f", totalReceber));


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

        Cursor cursor = banco.rawQuery("SELECT Compra._id, Compra.idCliente, Compra.dataCompra, Compra.valorCompra, Cliente.nome, Cliente.diaVencimento " +
                "FROM Compra " +
                "JOIN Cliente ON Compra.idCliente = Cliente._id " +
                "WHERE Compra.dataPagamento IS NULL AND Cliente._id = ?", new String[]{String.valueOf(idCliente)});

        // Verifica se o cursor contém dados
        if (cursor != null && cursor.moveToFirst()) {

            List<String> comprasAtrasadas = new ArrayList<>();
            Calendar dataAtual = Calendar.getInstance();
            totalAtrasado = 0.0;

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

                    if (dataVencimento.before(dataAtual)) {
                        String compraDetalhes = "Id: " + idCompra + " | Valor: R$ " + valorCompra + " | Data Compra: " + dataCompraStr;
                        comprasAtrasadas.add(compraDetalhes);
                        totalAtrasado += valorCompra;
                    }
                }
            } while (cursor.moveToNext());

            // Exibir as compras atrasadas na ListView
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comprasAtrasadas);
            lvComprasAtrasadas.setAdapter(adaptador);
            tvTotalAtrasado.setText(String.format(Locale.getDefault(), "Total em atraso: R$ %.2f", totalAtrasado));

        } else {
            tvTotalAtrasado.setText(String.format(Locale.getDefault(), "Total em atraso: R$ %.2f", totalAtrasado));
        }

        cursor.close();
    }

    private int extrairIdCompra(String compraDetalhes) {
        String[] detalhes = compraDetalhes.split("\\|");
        String idParte = detalhes[0].trim(); // "Id: X"
        return Integer.parseInt(idParte.replace("Id:", "").trim());
    }

    private void mostrarPopupAlterarData(int idCompra) {
        // Pega a data atual para ser o valor padrão
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Quando a data for selecionada, monta a string da data
                    String dataPagamento = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);

                    // Chama a função para atualizar a data de pagamento no banco de dados
                    atualizarDataPagamento(idCompra, dataPagamento);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void atualizarDataPagamento(int idCompra, String dataPagamento) {
        ContentValues valores = new ContentValues();
        valores.put("dataPagamento", dataPagamento);

        // Atualiza a compra com o id especificado
        int linhasAfetadas = banco.update("Compra", valores, "_id = ?", new String[]{String.valueOf(idCompra)});

        if (linhasAfetadas > 0) {
            Toast.makeText(this, "Data de pagamento atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            // Atualiza a lista de compras atrasadas após a alteração
            carregarComprasAtrasadas(Integer.parseInt(etIdCliente.getText().toString()));
            carregarTotalReceber(Integer.parseInt(etIdCliente.getText().toString()));
        } else {
            Toast.makeText(this, "Erro ao atualizar a data de pagamento.", Toast.LENGTH_SHORT).show();
        }
    }



}

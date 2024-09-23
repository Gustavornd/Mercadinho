package com.example.mercadinho;

public class Produto {
    private int id;
    private String descricao;
    private String unidade;
    private double preco;

    // Construtor padrão
    public Produto() {
    }

    public Produto(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    // Construtor com parâmetros
    public Produto(int id, String descricao, String unidade, double preco) {
        this.id = id;
        this.descricao = descricao;
        this.unidade = unidade;
        this.preco = preco;
    }

    // Getters e Setters para cada campo
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}

package com.example.mercadinho;

public class ItemCompra {
    private int idProduto;
    private double unitario;
    private double quantidade;
    private double total;

    public ItemCompra(int idProduto, double unitario, double quantidade, double total) {
        this.idProduto = idProduto;
        this.unitario = unitario;
        this.quantidade = quantidade;
        this.total = total;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public double getUnitario() {
        return unitario;
    }

    public void setUnitario(double unitario) {
        this.unitario = unitario;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}

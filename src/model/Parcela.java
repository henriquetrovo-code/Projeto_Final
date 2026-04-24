package model;

import java.sql.Date;

public class Parcela {
    private int id;
    private int numeroParcela;
    private double valor;
    private Date vencimento;
    private String status;

    public Parcela(int id, int numeroParcela, double valor, Date vencimento, String status) {
        this.id = id;
        this.numeroParcela = numeroParcela;
        this.valor = valor;
        this.vencimento = vencimento;
        this.status = status;
    }

    // Getters para a TableView
    public int getId() { return id; }
    public int getNumeroParcela() { return numeroParcela; }
    public double getValor() { return valor; }
    public Date getVencimento() { return vencimento; }
    public String getStatus() { return status; }
}
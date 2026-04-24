package model;

public class Pagamento {
    private int id;
    private int vendaId;
    private String formaPagamento;
    private double valor;

    public Pagamento() {}

    public Pagamento(String forma, double valor) {
        this.formaPagamento = forma;
        this.valor = valor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVendaId() { return vendaId; }
    public void setVendaId(int vendaId) { this.vendaId = vendaId; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String fp) { this.formaPagamento = fp; }

    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
}
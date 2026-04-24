package model;

public class ItemVenda {
    private String codigoReferencia;
    private String descricao;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    public ItemVenda() {}
    
    // Construtor auxiliar para facilitar a adição no carrinho
    public ItemVenda(String codigo, String desc, int qtd, double preco) {
        this.codigoReferencia = codigo;
        this.descricao = desc;
        this.quantidade = qtd;
        this.precoUnitario = preco;
        this.subtotal = qtd * preco;
    }

    public String getCodigoReferencia() { return codigoReferencia; }
    public void setCodigoReferencia(String cr) { this.codigoReferencia = cr; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int q) { this.quantidade = q; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double p) { this.precoUnitario = p; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double s) { this.subtotal = s; }
}
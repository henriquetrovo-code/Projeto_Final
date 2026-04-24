package model;

public class Produto {
    private int id, quantidadeEstoque, estoqueMinimo;
    private String descricao, referencia, codigoBarras, categoria;
    private double precoCusto, margemLucro, precoVenda;

    public Produto() {}
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int q) { this.quantidadeEstoque = q; }
    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int em) { this.estoqueMinimo = em; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String r) { this.referencia = r; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String cb) { this.codigoBarras = cb; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String c) { this.categoria = c; }
    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double pc) { this.precoCusto = pc; }
    public double getMargemLucro() { return margemLucro; }
    public void setMargemLucro(double ml) { this.margemLucro = ml; }
    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double pv) { this.precoVenda = pv; }
    
}
package model;

import java.util.Date;

public class log {
    private int id;
    private int vendaId;
    private int produtoId;
    private int vendedorCodigo;
    private Date dataHora;
    private String tipoAcao; // Venda, Cancelamento, Entrada Estoque
    private int quantidade;
    private String motivoCancelamento;

    public log() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVendaId() { return vendaId; }
    public void setVendaId(int vi) { this.vendaId = vi; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int pi) { this.produtoId = pi; }

    public int getVendedorCodigo() { return vendedorCodigo; }
    public void setVendedorCodigo(int vc) { this.vendedorCodigo = vc; }

    public Date getDataHora() { return dataHora; }
    public void setDataHora(Date dh) { this.dataHora = dh; }

    public String getTipoAcao() { return tipoAcao; }
    public void setTipoAcao(String ta) { this.tipoAcao = ta; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int q) { this.quantidade = q; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String mc) { this.motivoCancelamento = mc; }
}
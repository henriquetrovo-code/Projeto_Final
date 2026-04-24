package model;
import java.util.Date;

public class Venda {
    private int id;
    private int clienteId;
    private String nomeCliente;
    private int vendedorCodigo;
    private Date dataVenda;
    private double totalVenda;
    private String status;
    private String bandeira;
    private String nsu;
    private int qtdParcelas; 
    
    private String nomeVendedor;
    private String resumoItens;

    public Venda() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int ci) { this.clienteId = ci; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nc) { this.nomeCliente = nc; }
    public int getVendedorCodigo() { return vendedorCodigo; }
    public void setVendedorCodigo(int vc) { this.vendedorCodigo = vc; }
    public Date getDataVenda() { return dataVenda; }
    public void setDataVenda(Date dv) { this.dataVenda = dv; }
    public double getTotalVenda() { return totalVenda; }
    public void setTotalVenda(double totalVenda) { this.totalVenda = totalVenda; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String b) { this.bandeira = b; }
    public String getNsu() { return nsu; }
    public void setNsu(String n) { this.nsu = n; }
    public int getQtdParcelas() { return qtdParcelas; }
    public void setQtdParcelas(int q) { this.qtdParcelas = q; }
    public String getNomeVendedor() { return nomeVendedor; }
    public void setNomeVendedor(String nv) { this.nomeVendedor = nv; }
    public String getResumoItens() { return resumoItens; }
    public void setResumoItens(String ri) { this.resumoItens = ri; }
}
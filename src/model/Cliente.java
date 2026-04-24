package model;

public class Cliente {
    private int id;
    private String nome, tipoPessoa, cpfCnpj, email, telefone, enderecoCompleto, cep, statusConta;
    private double saldoCredito;

    public Cliente() {}
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(String tipoPessoa) { this.tipoPessoa = tipoPessoa; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEnderecoCompleto() { return enderecoCompleto; }
    public void setEnderecoCompleto(String enderecoCompleto) { this.enderecoCompleto = enderecoCompleto; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getStatusConta() { return statusConta; }
    public void setStatusConta(String statusConta) { this.statusConta = statusConta; }
    public double getSaldoCredito() { return saldoCredito; }
    public void setSaldoCredito(double saldoCredito) { this.saldoCredito = saldoCredito; }
}
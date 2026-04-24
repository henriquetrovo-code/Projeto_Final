package model;

public class Sessao {
    private static Usuario usuarioLogado;
    private static Cliente clienteSelecionado;
    private static Produto produtoSelecionado;
    private static boolean descontoAutorizado = false;

    // --- Controle de Autorização de Desconto ---
    public static boolean isDescontoAutorizado() { return descontoAutorizado; }
    public static void setDescontoAutorizado(boolean status) { descontoAutorizado = status; }

    // --- Controle de Produto Selecionado ---
    public static Produto getProdutoSelecionado() { return produtoSelecionado; }
    public static void setProdutoSelecionado(Produto produto) { produtoSelecionado = produto; }

    // --- Controle de Cliente Selecionado ---
    public static Cliente getClienteSelecionado() { return clienteSelecionado; }
    public static void setClienteSelecionado(Cliente cliente) { clienteSelecionado = cliente; }

    // --- Controle de Usuário Logado ---
    public static void setUsuario(Usuario usuario) { usuarioLogado = usuario; }
    public static Usuario getUsuario() { return usuarioLogado; }

    public static String getPerfil() {
        return (usuarioLogado != null) ? usuarioLogado.getPerfil() : "";
    }

    public static void encerrarSessao() {
        usuarioLogado = null;
        clienteSelecionado = null;
        produtoSelecionado = null;
        descontoAutorizado = false;
    }
}
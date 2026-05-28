package com.example.atividadem2;

public class ItemCardapio {
    private String nome;
    private String preco;
    private String urlImagem;
    private String caminhoFotoLocal;
    private String categoria; // <-- LINHA NOVA

    public ItemCardapio() {
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getCaminhoFotoLocal() {
        return caminhoFotoLocal;
    }

    public void setCaminhoFotoLocal(String caminhoLocal) {
        this.caminhoFotoLocal = caminhoLocal;
    }
}
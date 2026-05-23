package com.example.atividadem1;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nomeCompleto;
    private String matricula;
    private String lotacao;
    private String funcao;

    public Usuario(String nomeCompleto, String matricula, String lotacao, String funcao) {
        this.nomeCompleto = nomeCompleto;
        this.matricula = matricula;
        this.lotacao = lotacao;
        this.funcao = funcao;
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public String getMatricula() { return matricula; }
    public String getLotacao() { return lotacao; }
    public String getFuncao() { return funcao; }
}
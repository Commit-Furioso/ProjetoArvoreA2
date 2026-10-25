package com.projeto.arvore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ItemFormula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // A Chave Primária (ID) que o banco vai gerar automaticamente
    
    private String nome;
    private String nomePai;
    private int quantidade;
    private double precoUnitario;

    // O JPA exige um construtor vazio
    public ItemFormula() {}

    public ItemFormula(String nome, String nomePai, int quantidade, double precoUnitario) {
        this.nome = nome;
        this.nomePai = nomePai;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNomePai() { return nomePai; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    
    // Setters
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
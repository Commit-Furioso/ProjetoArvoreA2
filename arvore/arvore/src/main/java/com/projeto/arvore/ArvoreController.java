package com.projeto.arvore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class ArvoreController {

    // Rota 1: Devolve a árvore completa da Caneta (Explosão)
    @GetMapping("/caneta")
    public TreeNode getCaneta() {
        List<ItemFormula> itensCaneta = Arrays.asList(
            new ItemFormula("Caneta", null, 1, 0.00),
            new ItemFormula("Tinta", "Caneta", 1, 0.00),
            new ItemFormula("Pigmento", "Tinta", 1, 0.30),
            new ItemFormula("Solvente", "Tinta", 1, 0.50),
            new ItemFormula("Corpo", "Caneta", 1, 0.00),
            new ItemFormula("Plástico", "Corpo", 1, 0.70),
            new ItemFormula("Clip", "Corpo", 1, 0.30),
            new ItemFormula("Tampa", "Caneta", 1, 0.00),
            new ItemFormula("Plástico", "Tampa", 1, 0.50),
            new ItemFormula("Vedação", "Tampa", 1, 0.20)
        );

        return buildTree(itensCaneta);
    }

    // Rota 2: Devolve apenas o valor total calculado (Implosão)
    @GetMapping("/caneta/total")
    public double getTotalCaneta() {
        TreeNode raiz = getCaneta();
        return calcularTotal(raiz);
    }

    // --- MÉTODOS AUXILIARES (Lógica da Árvore) ---
    private TreeNode buildTree(List<ItemFormula> itens) {
        ItemFormula itemRaiz = itens.stream()
                .filter(i -> i.getNomePai() == null)
                .findFirst()
                .orElse(null);

        if (itemRaiz == null) return null;

        TreeNode raiz = new TreeNode(itemRaiz);
        adicionarFilhosRecursivamente(raiz, itens);
        return raiz;
    }

    private void adicionarFilhosRecursivamente(TreeNode pai, List<ItemFormula> itens) {
        for (ItemFormula item : itens) {
            if (pai.getItem().getNome().equals(item.getNomePai())) {
                TreeNode filho = new TreeNode(item);
                pai.adicionarFilho(filho);
                adicionarFilhosRecursivamente(filho, itens);
            }
        }
    }

    private double calcularTotal(TreeNode node) {
        if (node.getFilhos().isEmpty()) {
            return node.getItem().getPrecoUnitario() * node.getItem().getQuantidade();
        }
        
        double somaCustoFilhos = 0;
        for (TreeNode filho : node.getFilhos()) {
            somaCustoFilhos += calcularTotal(filho);
        }
        
        return somaCustoFilhos * node.getItem().getQuantidade();
    }
}
package com.projeto.arvore;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class ArvoreController {

    // Injeção do Banco de Dados
    private final ItemFormulaRepository repository;

    public ArvoreController(ItemFormulaRepository repository) {
        this.repository = repository;
    }

    // 1. Devolve a árvore completa buscando do BANCO DE DADOS
    @GetMapping("/caneta")
    public TreeNode getCaneta() {
        List<ItemFormula> itens = repository.findAll();
        return buildTree(itens);
    }

    // 2. Devolve apenas o valor total
    @GetMapping("/caneta/total")
    public double getTotalCaneta() {
        TreeNode raiz = getCaneta();
        return raiz != null ? calcularTotal(raiz) : 0;
    }

    // 3. Devolve a lista simples de itens (para o HTML montar o menu de edição)
    @GetMapping("/itens")
    public List<ItemFormula> getTodosItens() {
        return repository.findAll();
    }

    // 4. ROTA CRUD: Atualiza o preço de um componente e salva no banco
    @PostMapping("/atualizar/{id}")
    public void atualizarPreco(@PathVariable Long id, @RequestParam double preco) {
        ItemFormula item = repository.findById(id).orElse(null);
        if (item != null) {
            item.setPrecoUnitario(preco);
            repository.save(item); // Grava a alteração no banco!
        }
    }

    // --- MÉTODOS AUXILIARES ---
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
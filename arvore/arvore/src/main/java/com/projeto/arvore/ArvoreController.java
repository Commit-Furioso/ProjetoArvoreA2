package com.projeto.arvore;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class ArvoreController {

    private final ItemFormulaRepository repository;
    private final DataInitializer dataInitializer;

    public ArvoreController(ItemFormulaRepository repository, DataInitializer dataInitializer) {
        this.repository = repository;
        this.dataInitializer = dataInitializer;
    }

    // 1. Busca a árvore de qualquer produto (Caneta, Computador ou Avião)
    @GetMapping("/produto/{nome}")
    public TreeNode getProduto(@PathVariable String nome) {
        List<ItemFormula> itens = repository.findAll();
        return buildTree(itens, nome);
    }

    // 2. Devolve o valor total unitário do produto escolhido
    @GetMapping("/produto/{nome}/total")
    public double getTotalProduto(@PathVariable String nome) {
        TreeNode raiz = getProduto(nome);
        return raiz != null ? calcularTotal(raiz) : 0;
    }

    // 3. Devolve todos os itens do banco
    @GetMapping("/itens")
    public List<ItemFormula> getTodosItens() {
        return repository.findAll();
    }

    // 4. ROTA CRUD: Atualiza o preço de uma matéria-prima no banco
    @PostMapping("/atualizar/{id}")
    public void atualizarPreco(@PathVariable Long id, @RequestParam double preco) {
        ItemFormula item = repository.findById(id).orElse(null);
        if (item != null) {
            item.setPrecoUnitario(preco);
            repository.save(item);
        }
    }

    // 5. ROTA RESET: Chama o DataInitializer sem repetir código
    @PostMapping("/resetar")
    public void resetarValoresPadrao() {
        dataInitializer.restaurarBancoPadrao();
    }

    // --- MÉTODOS AUXILIARES ---
    private TreeNode buildTree(List<ItemFormula> itens, String nomeProduto) {
        ItemFormula itemRaiz = itens.stream()
                .filter(i -> i.getNomePai() == null && i.getNome().equalsIgnoreCase(nomeProduto))
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
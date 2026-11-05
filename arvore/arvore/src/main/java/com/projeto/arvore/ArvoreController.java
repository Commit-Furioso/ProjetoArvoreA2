package com.projeto.arvore;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class ArvoreController {

    private final ItemFormulaRepository repository;

    public ArvoreController(ItemFormulaRepository repository) {
        this.repository = repository;
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

    // Mantém compatibilidade com a rota antiga
    @GetMapping("/caneta")
    public TreeNode getCaneta() {
        return getProduto("Caneta");
    }

    @GetMapping("/caneta/total")
    public double getTotalCaneta() {
        return getTotalProduto("Caneta");
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

    // 5. ROTA RESET (Também executa automaticamente ao iniciar o servidor)
    @EventListener(ApplicationReadyEvent.class)
    @PostMapping("/resetar")
    public void resetarValoresPadrao() {
        repository.deleteAll();
        repository.saveAll(Arrays.asList(
            // --- FÓRMULA 1: CANETA (Total: R$ 2,50) ---
            new ItemFormula("Caneta", null, 1, 0.00),
            new ItemFormula("Tinta", "Caneta", 1, 0.00),
            new ItemFormula("Pigmento", "Tinta", 1, 0.30),
            new ItemFormula("Solvente", "Tinta", 1, 0.50),
            new ItemFormula("Corpo", "Caneta", 1, 0.00),
            new ItemFormula("Plástico", "Corpo", 1, 0.70),
            new ItemFormula("Clip", "Corpo", 1, 0.30),
            new ItemFormula("Tampa", "Caneta", 1, 0.00),
            new ItemFormula("Plástico", "Tampa", 1, 0.50),
            new ItemFormula("Vedação", "Tampa", 1, 0.20),

            // --- FÓRMULA 2: COMPUTADOR (Total: R$ 3.380,00) ---
            new ItemFormula("Computador", null, 1, 0.00),
            new ItemFormula("Gabinete Montado", "Computador", 1, 0.00),
            new ItemFormula("Placa-Mãe", "Gabinete Montado", 1, 800.00),
            new ItemFormula("Processador", "Gabinete Montado", 1, 1200.00),
            new ItemFormula("Memória RAM", "Gabinete Montado", 2, 300.00),
            new ItemFormula("Kit Periféricos", "Computador", 1, 0.00),
            new ItemFormula("Monitor", "Kit Periféricos", 1, 600.00),
            new ItemFormula("Teclado", "Kit Periféricos", 1, 100.00),
            new ItemFormula("Mouse", "Kit Periféricos", 1, 80.00),

            // --- FÓRMULA 3: AVIÃO (Total: R$ 850.000,00) ---
            new ItemFormula("Avião", null, 1, 0.00),
            new ItemFormula("Fuselagem", "Avião", 1, 0.00),
            new ItemFormula("Estrutura de Alumínio", "Fuselagem", 1, 220000.00),
            new ItemFormula("Trem de Pouso", "Fuselagem", 3, 20000.00),
            new ItemFormula("Para-brisa Blindado", "Fuselagem", 1, 20000.00),
            new ItemFormula("Propulsão", "Avião", 1, 0.00),
            new ItemFormula("Turbina a Jato", "Propulsão", 2, 180000.00),
            new ItemFormula("Sistema de Combustível", "Propulsão", 1, 40000.00),
            new ItemFormula("Cabine de Comando", "Avião", 1, 0.00),
            new ItemFormula("Painel Aviônico", "Cabine de Comando", 1, 120000.00),
            new ItemFormula("Assento de Piloto", "Cabine de Comando", 2, 15000.00)
        ));
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
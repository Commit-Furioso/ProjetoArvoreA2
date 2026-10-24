import java.util.Arrays;
import java.util.List;

public class Main {

    // 1. Constrói a Árvore Genérica ligando Pais e Filhos
    public static TreeNode buildTree(List<ItemFormula> itens) {
        // Encontra a raiz (o item que não tem pai, ou seja, nomePai == null)
        ItemFormula itemRaiz = itens.stream()
                .filter(i -> i.getNomePai() == null)
                .findFirst()
                .orElse(null);

        if (itemRaiz == null) return null;

        TreeNode raiz = new TreeNode(itemRaiz);
        adicionarFilhosRecursivamente(raiz, itens);
        return raiz;
    }

    private static void adicionarFilhosRecursivamente(TreeNode pai, List<ItemFormula> itens) {
        for (ItemFormula item : itens) {
            if (pai.getItem().getNome().equals(item.getNomePai())) {
                TreeNode filho = new TreeNode(item);
                pai.adicionarFilho(filho);
                adicionarFilhosRecursivamente(filho, itens); // Busca os filhos do filho
            }
        }
    }

    // 2. Exibe os detalhes (Explosão)
    public static void printTree(TreeNode node, int nivel) {
        String indentacao = "  ".repeat(nivel);
        System.out.println(indentacao + "- " + node.getItem().getNome() 
                + " (Qtd: " + node.getItem().getQuantidade() + ")");
        
        for (TreeNode filho : node.getFilhos()) {
            printTree(filho, nivel + 1);
        }
    }

    // 3. Remonta o custo do produto (Implosão de Baixo para Cima)
    public static double calcularTotal(TreeNode node) {
        // Se for uma folha (matéria-prima final, não tem filhos), calcula Qtd * Preço
        if (node.getFilhos().isEmpty()) {
            return node.getItem().getPrecoUnitario() * node.getItem().getQuantidade();
        }
        
        // Se tiver filhos, o custo é a soma do custo dos filhos multiplicada pela quantidade deste nó
        double somaCustoFilhos = 0;
        for (TreeNode filho : node.getFilhos()) {
            somaCustoFilhos += calcularTotal(filho);
        }
        
        return somaCustoFilhos * node.getItem().getQuantidade();
    }

    // --- TESTES OBRIGATÓRIOS ---
    public static void main(String[] args) {
        System.out.println("=== TESTE 1: CANETA ===");
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

        TreeNode raizCaneta = buildTree(itensCaneta);
        System.out.println("Árvore da fórmula da caneta (Explosão):");
        printTree(raizCaneta, 0);
        double totalCaneta = calcularTotal(raizCaneta);
        System.out.printf("Valor total de fabricação da caneta: R$ %.2f\n\n", totalCaneta);

        System.out.println("=== TESTE 2: COMPUTADOR ===");
        List<ItemFormula> itensPC = Arrays.asList(
            new ItemFormula("Computador", null, 1, 0.00),
            new ItemFormula("Gabinete", "Computador", 1, 0.00),
            new ItemFormula("Placa-Mãe", "Gabinete", 1, 0.00),
            new ItemFormula("CPU", "Placa-Mãe", 1, 1000.00),
            new ItemFormula("Memória RAM", "Placa-Mãe", 2, 200.00), // Qtd: 2 (200 cada = 400)
            new ItemFormula("Chipset", "Placa-Mãe", 1, 150.00),
            new ItemFormula("Fonte de Alimentação", "Gabinete", 1, 300.00),
            new ItemFormula("Armazenamento", "Gabinete", 1, 0.00),
            new ItemFormula("HDD", "Armazenamento", 1, 250.00),
            new ItemFormula("SSD", "Armazenamento", 1, 400.00),
            new ItemFormula("Monitor", "Computador", 1, 0.00),
            new ItemFormula("Tela LCD", "Monitor", 1, 600.00),
            new ItemFormula("Carcaça", "Monitor", 1, 100.00),
            new ItemFormula("Periféricos", "Computador", 1, 0.00),
            new ItemFormula("Teclado", "Periféricos", 1, 100.00),
            new ItemFormula("Mouse", "Periféricos", 1, 80.00)
        );

        TreeNode raizPC = buildTree(itensPC);
        System.out.println("Árvore da fórmula do computador (Explosão):");
        printTree(raizPC, 0);
        double totalPC = calcularTotal(raizPC);
        System.out.printf("Valor total de fabricação do Computador: R$ %.2f\n", totalPC);
    }
}
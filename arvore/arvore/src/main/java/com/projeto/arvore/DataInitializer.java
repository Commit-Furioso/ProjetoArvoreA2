package com.projeto.arvore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ItemFormulaRepository repository;

    public DataInitializer(ItemFormulaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        restaurarBancoPadrao();
        System.out.println("✅ Base de dados inicializada com Caneta, Computador e Avião!");
    }

    // Método único chamado tanto ao iniciar quanto pelo botão de Reset
    public void restaurarBancoPadrao() {
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
}
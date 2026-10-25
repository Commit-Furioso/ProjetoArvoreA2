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
    public void run(String... args) throws Exception {
        // Só adiciona se a tabela estiver vazia
        if (repository.count() == 0) {
            repository.saveAll(Arrays.asList(
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
            ));
            System.out.println("✅ Base de dados inicializada com a fórmula da Caneta!");
        }
    }
}
package com.projeto.arvore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemFormulaRepository extends JpaRepository<ItemFormula, Long> {
    // O Spring já sabe fazer o CRUD básico só com isto!
}
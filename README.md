# 📦 Sistema de Gestão de Produção (Árvore Genérica)

🔗 **Aceda ao Sistema Online:** [https://projetoarvorea2.onrender.com/](https://projetoarvorea2.onrender.com/)

Projeto desenvolvido para a avaliação (Projeto A2), aplicando conceitos de **Árvores Genéricas** para calcular fórmulas de produção, explosão de materiais e implosão de custos (CRUD).

## 🚀 Funcionalidades Entregues

- **Requisitos Obrigatórios (1,5 pts):**
  - **Resolução do Problema:** Lógica implementada utilizando a estrutura de Árvore Genérica (N-ária).
  - **Testes Validados:** O sistema executa perfeitamente a Caneta (R$ 2,50) e o Computador (R$ 3.380,00).
  - **Tela Gráfica:** Visualização hierárquica e interativa da árvore de produção (Explosão).
  - **Simulação em Lote:** Cálculo automático da quantidade e valor total a produzir.

- **Requisitos Avançados (0,5 pts):**
  - **Front-end Web:** Interface construída com HTML5, CSS3 moderno e JavaScript (Fetch API com Promises).
  - **Persistência em Banco de Dados:** Back-end desenvolvido em **Java com Spring Boot** e integração com o Banco de Dados **H2** através do Spring Data JPA.
  - **CRUD (Implosão):** Painel dinâmico com funcionalidade de pesquisa (Datalist/Select) para atualizar o preço de matérias-primas na base de dados, recalculando a árvore automaticamente de baixo para cima.
  - **Cloud Deploy:** Aplicação "dockerizada" (Dockerfile) e hospedada na nuvem utilizando o **Render**.

## 🛠️ Tecnologias Utilizadas

- **Back-end:** Java 17, Spring Boot, Spring Web
- **Banco de Dados:** H2 Database (In-Memory) + Hibernate/JPA
- **Front-end:** HTML5, CSS3, Vanilla JS
- **Infraestrutura:** Docker, Maven, Render Web Services

## ⚙️ Como Executar Localmente

1. Certifique-se de ter o **Java 17+** e o **Maven** instalados.
2. Clone este repositório:
   ```bash
   git clone [https://github.com/Commit-Furioso/ProjetoArvoreA2](https://github.com/Commit-Furioso/ProjetoArvoreA2)
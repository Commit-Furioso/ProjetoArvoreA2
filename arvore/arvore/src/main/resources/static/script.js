let custoUnitario = 0; 

// 1. Busca os dados no Java (Spring Boot)
async function carregarFormula() {
    try {
        const respostaArvore = await fetch('/api/formulas/caneta');
        const arvore = await respostaArvore.json();

        const respostaTotal = await fetch('/api/formulas/caneta/total');
        custoUnitario = await respostaTotal.json();

        const container = document.getElementById('arvore-visual');
        container.innerHTML = desenharArvore(arvore, 0);

        calcularProducao();
        
    } catch (erro) {
        console.error("Erro de comunicação com o Java:", erro);
        alert("Erro! Verifique se o servidor Java está a correr.");
    }
}

// 2. Calcula o custo real de um nó (Implosão visual)
function calcularCustoNo(no) {
    // Se não tem filhos (é matéria-prima base), o custo é o preço unitário
    if (!no.filhos || no.filhos.length === 0) {
        return no.item.precoUnitario;
    }
    
    // Se tem filhos (é um subcomponente), o custo é a soma dos filhos
    let somaFilhos = 0;
    for (let filho of no.filhos) {
        // Multiplica o custo do filho pela quantidade que o pai precisa dele
        somaFilhos += calcularCustoNo(filho) * filho.item.quantidade;
    }
    return somaFilhos;
}

// 3. Função recursiva para criar a "Explosão" com indentação visual
function desenharArvore(no, nivel) {
    if (!no) return "";

    let espacos = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".repeat(nivel);
    let icone = nivel === 0 ? "📦" : "↳ 🧩"; 
    
    // Calcula o custo real implodido para mostrar na tela
    let custoReal = calcularCustoNo(no);
    let precoFormatado = custoReal.toFixed(2).replace('.', ',');

    let html = `<div>${espacos} ${icone} <strong>${no.item.nome}</strong> (Qtd: ${no.item.quantidade} | Custo: R$ ${precoFormatado})</div>`;

    if (no.filhos && no.filhos.length > 0) {
        for (let filho of no.filhos) {
            html += desenharArvore(filho, nivel + 1);
        }
    }
    return html;
}

// 4. Simula a produção em lote
function calcularProducao() {
    if (custoUnitario === 0) return; 

    const qtd = document.getElementById('qtd-produzir').value;
    const custoFinal = custoUnitario * qtd;
    
    document.getElementById('resultado-total').innerText = 
        `Valor Total: R$ ${custoFinal.toFixed(2).replace('.', ',')}`;
}
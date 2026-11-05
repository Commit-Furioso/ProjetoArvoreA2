let custoUnitario = 0;
let arvoreGlobal = null;

// 1. Busca os dados no Java (Spring Boot)
async function carregarFormula() {
    try {
        const respostaArvore = await fetch('/api/formulas/caneta');
        arvoreGlobal = await respostaArvore.json();

        const respostaTotal = await fetch('/api/formulas/caneta/total');
        custoUnitario = await respostaTotal.json();

        calcularProducao(); 
        await carregarItensParaEdicao();
        
    } catch (erro) {
        console.error("Erro de comunicação com o Java:", erro);
        mostrarNotificacao("❌ Erro! Verifique se o servidor Java está a correr.", 'erro');
    }
}

// 2. Calcula o custo real unitário de um nó (Implosão)
function calcularCustoNo(no) {
    if (!no.filhos || no.filhos.length === 0) {
        return no.item.precoUnitario;
    }
    let somaFilhos = 0;
    for (let filho of no.filhos) {
        somaFilhos += calcularCustoNo(filho) * filho.item.quantidade;
    }
    return somaFilhos;
}

// 3. Função recursiva com Explosão de Lote
function desenharArvore(no, nivel, multiplicadorAcumulado) {
    if (!no) return "";

    let espacos = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".repeat(nivel);
    let icone = nivel === 0 ? "📦" : "↳ 🧩"; 
    
    let qtdTotalNode = no.item.quantidade * multiplicadorAcumulado;
    let custoUnitarioDoNo = calcularCustoNo(no);
    let custoTotalNode = custoUnitarioDoNo * qtdTotalNode;

    let precoFormatado = custoTotalNode.toFixed(2).replace('.', ',');

    let html = `<div>${espacos} ${icone} <strong>${no.item.nome}</strong> (Qtd: ${qtdTotalNode} | Custo: R$ ${precoFormatado})</div>`;

    if (no.filhos && no.filhos.length > 0) {
        for (let filho of no.filhos) {
            html += desenharArvore(filho, nivel + 1, qtdTotalNode);
        }
    }
    return html;
}

// 4. Simula a produção em lote e redesenha a árvore
function calcularProducao() {
    if (custoUnitario === 0 || !arvoreGlobal) return; 

    const inputQtd = document.getElementById('qtd-produzir').value;
    const qtdLote = parseInt(inputQtd) || 1; 
    
    const custoFinal = custoUnitario * qtdLote;
    
    document.getElementById('resultado-total').innerText = 
        `Valor Total: R$ ${custoFinal.toFixed(2).replace('.', ',')}`;

    const container = document.getElementById('arvore-visual');
    container.innerHTML = desenharArvore(arvoreGlobal, 0, qtdLote);
}

// 5. Restaura a quantidade para 1 E os preços originais no Banco de Dados
async function resetarProducao() {
    try {
        // Pede ao Java para voltar os preços do banco ao padrão original
        await fetch('/api/formulas/resetar', {
            method: 'POST'
        });

        // Volta o campo de quantidade para 1
        document.getElementById('qtd-produzir').value = 1;
        
        // Recarrega a árvore e os preços originais (R$ 2,50)
        await carregarFormula();
        
        mostrarNotificacao("🔄 Quantidade e preços restaurados para o padrão (R$ 2,50)!");
    } catch (erro) {
        console.error("Erro ao resetar:", erro);
        mostrarNotificacao("❌ Erro ao restaurar os valores padrão.", 'erro');
    }
}

// 6. Carrega os itens na caixa de seleção estilizada
async function carregarItensParaEdicao() {
    const resposta = await fetch('/api/formulas/itens');
    const itens = await resposta.json();
    
    const select = document.getElementById('select-item');
    select.innerHTML = '<option value="">Selecione um componente...</option>';
    
    const nomesDosPais = new Set(itens.map(item => item.nomePai));
    
    itens.forEach(item => {
        if (item.nomePai !== null && !nomesDosPais.has(item.nome)) {
            let precoFormatado = item.precoUnitario.toFixed(2).replace('.', ',');
            select.innerHTML += `<option value="${item.id}">${item.nome} (Atual: R$ ${precoFormatado})</option>`;
        }
    });
}

// 7. Atualiza o preço no Banco de Dados via API e recalcula a árvore (Implosão)
async function atualizarPrecoComponente() {
    const id = document.getElementById('select-item').value;
    const novoPreco = document.getElementById('novo-preco').value;

    if (!id || !novoPreco) {
        mostrarNotificacao("❌ Por favor, selecione um componente e informe o novo preço!", 'erro');
        return;
    }

    try {
        await fetch(`/api/formulas/atualizar/${id}?preco=${novoPreco}`, {
            method: 'POST'
        });

        mostrarNotificacao("✅ Preço atualizado! Recalculando implosão...");
        
        document.getElementById('novo-preco').value = '';
        await carregarFormula(); 
    } catch (erro) {
        mostrarNotificacao("❌ Erro ao atualizar o preço no servidor.", 'erro');
        console.error(erro);
    }
}

// 8. Função para exibir a notificação elegante
function mostrarNotificacao(mensagem, tipo = 'sucesso') {
    const toast = document.getElementById('toast-notificacao');
    toast.innerText = mensagem;
    
    if (tipo === 'erro') {
        toast.classList.add('erro');
    } else {
        toast.classList.remove('erro');
    }

    toast.classList.add('mostrar');

    setTimeout(() => {
        toast.classList.remove('mostrar');
    }, 3000);
}
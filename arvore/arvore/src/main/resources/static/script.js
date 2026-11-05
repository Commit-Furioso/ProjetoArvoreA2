let custoUnitario = 0;
let arvoreGlobal = null;
let produtoAtual = 'Caneta'; // Produto padrão inicial

// Troca o produto ativo quando o usuário clica num dos 3 botões
async function selecionarProduto(nomeProduto) {
    produtoAtual = nomeProduto;
    document.getElementById('qtd-produzir').value = 1;
    await carregarFormula();
    mostrarNotificacao(`📦 Fórmula carregada: ${nomeProduto}`);
}

// 1. Busca os dados do produto atual no Java
async function carregarFormula() {
    try {
        const nomeCodificado = encodeURIComponent(produtoAtual);

        const respostaArvore = await fetch(`/api/formulas/produto/${nomeCodificado}`);
        arvoreGlobal = await respostaArvore.json();

        const respostaTotal = await fetch(`/api/formulas/produto/${nomeCodificado}/total`);
        custoUnitario = await respostaTotal.json();

        calcularProducao(); 
        carregarItensParaEdicao();
        
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

    let precoFormatado = custoTotalNode.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

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
    const valorFormatado = custoFinal.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    
    document.getElementById('resultado-total').innerText = `Valor Total: R$ ${valorFormatado}`;

    const container = document.getElementById('arvore-visual');
    container.innerHTML = desenharArvore(arvoreGlobal, 0, qtdLote);
}

// 5. Restaura a quantidade para 1 E os preços originais de todos os produtos no Banco
async function resetarProducao() {
    try {
        await fetch('/api/formulas/resetar', {
            method: 'POST'
        });

        document.getElementById('qtd-produzir').value = 1;
        await carregarFormula();
        
        mostrarNotificacao(`🔄 ${produtoAtual} restaurado para os valores padrão!`);
    } catch (erro) {
        console.error("Erro ao resetar:", erro);
        mostrarNotificacao("❌ Erro ao restaurar os valores padrão.", 'erro');
    }
}

// 6. Extrai apenas as matérias-primas (folhas) da árvore atual para o menu CRUD
function extrairFolhas(no, listaFolhas = []) {
    if (!no) return listaFolhas;
    if (!no.filhos || no.filhos.length === 0) {
        listaFolhas.push(no.item);
    } else {
        for (let filho of no.filhos) {
            extrairFolhas(filho, listaFolhas);
        }
    }
    return listaFolhas;
}

function carregarItensParaEdicao() {
    const select = document.getElementById('select-item');
    select.innerHTML = '<option value="">Selecione um componente...</option>';
    
    if (!arvoreGlobal) return;

    const folhas = extrairFolhas(arvoreGlobal);
    
    folhas.forEach(item => {
        let precoFormatado = item.precoUnitario.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        select.innerHTML += `<option value="${item.id}">${item.nome} - ${item.nomePai} (Atual: R$ ${precoFormatado})</option>`;
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

// 8. Exibe a notificação Toast
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
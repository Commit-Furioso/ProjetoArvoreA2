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
// Carrega os itens na caixa de seleção para podermos editar os preços
async function carregarItensParaEdicao() {
    const resposta = await fetch('/api/formulas/itens');
    const itens = await resposta.json();
    
    const select = document.getElementById('select-item');
    select.innerHTML = '<option value="">Selecione um componente...</option>';
    
    // Cria uma lista apenas com os nomes de quem é "Pai" de alguém
    const nomesDosPais = new Set(itens.map(item => item.nomePai));
    
    itens.forEach(item => {
        // Um item SÓ pode ter o preço editado se:
        // 1. Não for o produto final (raiz) -> item.nomePai !== null
        // 2. Não for pai de nenhum outro item (submontagem) -> !nomesDosPais.has(item.nome)
        if (item.nomePai !== null && !nomesDosPais.has(item.nome)) {
            // Formata o preço para mostrar sempre com 2 casas decimais no select
            let precoFormatado = item.precoUnitario.toFixed(2).replace('.', ',');
            select.innerHTML += `<option value="${item.id}">${item.nome} (Atual: R$ ${precoFormatado})</option>`;
        }
    });
}

// Atualiza o preço no Banco de Dados via API e recalcula a árvore (Implosão)
async function atualizarPrecoComponente() {
    const id = document.getElementById('select-item').value;
    const novoPreco = document.getElementById('novo-preco').value;

    if (!id || !novoPreco) {
        mostrarNotificacao("❌ Por favor, selecione um componente e informe o novo preço!", 'erro');
        return;
    }

    try {
        // Chama o Java enviando o novo preço
        await fetch(`/api/formulas/atualizar/${id}?preco=${novoPreco}`, {
            method: 'POST'
        });

        // Mostra a notificação de sucesso!
        mostrarNotificacao("✅ Preço atualizado! Recalculando implosão...");
        
        // Limpa o campo e recarrega tudo para mostrar a implosão visual
        document.getElementById('novo-preco').value = '';
        await carregarFormula(); 

    } catch (erro) {
        mostrarNotificacao("❌ Erro ao atualizar o preço no servidor.", 'erro');
        console.error(erro);
    }
}

// Quando carregar a árvore, atualiza também a caixa de seleção
const funcaoOriginal = carregarFormula;
carregarFormula = async function() {
    await funcaoOriginal();
    await carregarItensParaEdicao();
};

// Função para exibir a notificação elegante
function mostrarNotificacao(mensagem, tipo = 'sucesso') {
    const toast = document.getElementById('toast-notificacao');
    toast.innerText = mensagem;
    
    // Muda a cor consoante o tipo de mensagem
    if (tipo === 'erro') {
        toast.classList.add('erro');
    } else {
        toast.classList.remove('erro');
    }

    // Mostra a notificação
    toast.classList.add('mostrar');

    // Esconde automaticamente após 3 segundos
    setTimeout(() => {
        toast.classList.remove('mostrar');
    }, 3000);
}
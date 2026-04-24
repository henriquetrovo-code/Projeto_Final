EletroTech - Sistema de Gestão de Vendas e Estoque Este projeto é uma solução robusta desenvolvida em JavaFX para a empresa EletroTech Distribuidora, visando automatizar o controle de frente de caixa (PDV) e a gestão rigorosa de armazém.

Funcionalidades Implementadas  Gestão de Estoque & Produtos Baixa Automática (RN01): Integração total entre PDV e Estoque. Vendeu, baixou.

Bloqueio de Venda sem Estoque (RN02): O sistema impede vendas negativas com alertas em tempo real.

Processamento de Entradas: Tela dedicada para reposição de mercadorias.

Estorno Automático (RN03): Ao cancelar uma venda, os produtos retornam ao estoque com registro de motivo.

Frente de Caixa (PDV) Pagamentos Mistos: Suporte para pagamento simultâneo (Dinheiro + Cartão + Pix).

Sistema de Descontos: Limite de 5% para vendedores. Descontos maiores exigem autenticação de Gerente via popup.

Cupom Não Fiscal: Emissão de recibo detalhado e estilizado (estilo impressora térmica).

Segurança e Acessos Níveis de Permissão: Interface adaptativa para Gerentes, Vendedores e Estoquistas.

Controle de Sessão: Trava de segurança que impede navegação indevida durante o carregamento inicial.

Pré-requisitos e Instalação

Banco de Dados (MySQL) O sistema utiliza MySQL. Siga os passos abaixo:
Crie um banco de dados chamado eletrotech_db (ou o nome definido no seu projeto).

Execute o script SQL localizado em: src/main/resources/sql/script_banco.sql.

Importante: Verifique as credenciais (usuário e senha) na sua classe de conexão:

Java // Localize este arquivo no seu projeto: application.dao.Conexao private static final String USER = "seu_usuario"; private static final String PASS = "sua_senha"; 2. Configuração do JavaFX SDK: Certifique-se de ter o JavaFX SDK configurado no seu Eclipse/IntelliJ.

VM Arguments: Lembre-se de adicionar os módulos do JavaFX se estiver rodando via terminal ou se o projeto não for Maven: --module-path "caminho/para/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml

Bibliotecas (JARs) Inclua no seu Build Path:
mysql-connector-j-x.x.x.jar

Bibliotecas do JavaFX (controls, fxml, graphics, base).

Como Executar Importe o projeto na sua IDE (Eclipse/IntelliJ).

Certifique-se de que o servidor MySQL está ativo.

Execute a classe application.Main.

Instruções para Avaliação (Professor Carlos Guilherme) O usuário e senha inicial para teste podem ser encontrados na tabela usuarios do banco de dados (Sugestão: Crie um usuário 'admin' no seu script SQL).

O fluxo de venda pode ser testado selecionando múltiplos produtos e combinando formas de pagamento.

O bloqueio de estoque pode ser validado tentando vender uma quantidade superior à disponível após a entrada de mercadoria.


CÓDIGO BANCO DE DADOS:

CREATE DATABASE eletrotech;
USE eletrotech;

-- 2. Tabela de Funcionários (Usuários)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(50) NOT NULL,
    perfil ENUM('Gerente', 'Vendedor', 'Caixa') NOT NULL
) ENGINE=InnoDB;

-- 3. Tabela de Clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo_pessoa ENUM('Fisica', 'Juridica') NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20),
    endereco_completo TEXT,
    cep VARCHAR(10),
    status_conta ENUM('Ativo', 'Inativo') DEFAULT 'Ativo',
    saldo_credito DECIMAL(10,2) DEFAULT 0.00
) ENGINE=InnoDB;

-- 4. Tabela de Produtos
CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(150) NOT NULL,
    referencia VARCHAR(50),
    codigo_barras VARCHAR(50) UNIQUE,
    categoria VARCHAR(50),
    preco_custo DECIMAL(10,2) NOT NULL,
    margem_lucro DECIMAL(5,2),
    preco_venda DECIMAL(10,2) NOT NULL,
    quantidade_estoque INT DEFAULT 0,
    estoque_minimo INT DEFAULT 5
) ENGINE=InnoDB;

-- 5. Tabela de Vendas (Cabeçalho)
-- Status inicial: 'Pendente' para o faturamento posterior no Caixa
CREATE TABLE vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT,
    vendedor_codigo INT,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_venda DECIMAL(10,2) NOT NULL,
    desconto_aplicado DECIMAL(10,2) DEFAULT 0.00,
    status_venda ENUM('Pendente', 'Finalizada', 'Cancelada') DEFAULT 'Pendente',
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_venda_vendedor FOREIGN KEY (vendedor_codigo) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- 6. Tabela de Itens da Venda
CREATE TABLE itens_venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_venda FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
) ENGINE=InnoDB;

-- 7. Tabela de Pagamentos
CREATE TABLE pagamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_pagamento_venda FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Ajustando a tabela de pagamentos para suportar parcelas de boleto
ALTER TABLE pagamentos ADD COLUMN data_vencimento DATE AFTER valor;
ALTER TABLE pagamentos ADD COLUMN status_pagamento ENUM('Pendente', 'Pago') DEFAULT 'Pendente' AFTER data_vencimento;
ALTER TABLE pagamentos ADD COLUMN numero_parcela INT AFTER status_pagamento;

-- 8. Logs de Auditoria
CREATE TABLE logs_sistema (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT,
    produto_id INT,
    vendedor_codigo INT,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_acao VARCHAR(50), 
    quantidade INT,
    motivo_cancelamento TEXT,
    CONSTRAINT fk_log_vendedor FOREIGN KEY (vendedor_codigo) REFERENCES usuarios(id)
) ENGINE=InnoDB;

INSERT INTO usuarios (nome, login, senha, perfil) 
VALUES ('Henrique Trovo', 'Henrique', 'admin123', 'Gerente');

-- Adicionando as colunas que faltam para o sistema HyperTech
ALTER TABLE vendas ADD COLUMN bandeira_cartao VARCHAR(50) AFTER status_venda;
ALTER TABLE vendas ADD COLUMN nsu_comprovante VARCHAR(50) AFTER bandeira_cartao;

CREATE TABLE log_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT,
    vendedor_id INT,
    tipo_movimentacao ENUM('ENTRADA', 'SAIDA', 'CANCELAMENTO', 'AJUSTE') NOT NULL,
    quantidade INT NOT NULL,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(255),
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
);

CREATE TABLE controle_caixa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('ABERTURA', 'RETIRADA') NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    vendedor_id INT,
    observacao TEXT,
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
);


CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS produto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(19, 2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS produto_categoria (
    produto_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (produto_id, categoria_id),
    CONSTRAINT fk_produto_categoria_produto FOREIGN KEY (produto_id) REFERENCES produto (id) ON DELETE CASCADE,
    CONSTRAINT fk_produto_categoria_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id) ON DELETE CASCADE
);

# **Documentação da Aplicação de Votação**

## **Índice**
1. [Objetivo](#objetivo)
2. [Arquitetura e Estrutura do Projeto](#arquitetura-e-estrutura-do-projeto)
3. [Stacks Utilizadas](#stacks-utilizadas)
4. [Configuração e Execução com Docker](#configuração-e-execução-com-docker)
5. [Validação de Endpoints com Swagger](#validação-de-endpoints-com-swagger)
6. [Testes Automatizados e Cobertura](#testes-automatizados-e-cobertura)
7. [Considerações Finais](#considerações-finais)

---

## **Objetivo**

Esta aplicação tem como objetivo **gerenciar sessões de votação** para uma cooperativa, onde:
- Cada **pauta** pode abrir uma sessão de votação com um tempo definido ou um tempo padrão (1 minuto).
- **Associados** podem votar com as opções **"Sim" ou "Não"** e só podem votar uma vez por pauta.
- Após o fim da sessão, o **resultado da votação é publicado no Kafka** e também exibido no log.
- **Redis** é usado para armazenar e gerenciar as sessões temporariamente, com expiração automática.

---

## **Arquitetura e Estrutura do Projeto**

### **Arquitetura**

- **Microserviço em Spring Boot**:
  - Aplicação desenvolvida em **Java 17** usando **Spring Boot**.
- **Redis** para **armazenamento temporário das sessões** com TTL e expiração.
- **Apache Kafka** para **mensageria** e publicação do resultado das votações.
- **PostgreSQL** como banco de dados para persistência das pautas e votos.
- **Swagger** para documentação e teste dos endpoints.

### **Estrutura do Projeto**

```bash
src/
│
├── main/
│   ├── java/br/com/desafio/voto/
│   │   ├── config/         # Configurações de Redis, Kafka e agendamentos
│   │   ├── controller/     # Controladores REST
│   │   ├── model/          # Entidades (Pauta, SessaoVotacao, Voto, etc.)
│   │   ├── repository/     # Repositórios JPA e Redis
│   │   ├── service/        # Lógica de negócio (Sessões, Votos, etc.)
│   │   └── exception/      # Exceções personalizadas
│   └── resources/
│       ├── application.properties  # Configurações da aplicação
│       └── application.yml         # Configurações de produção (opcional)
└── test/                           # Testes unitários e de integração
```

## **Stacks Utilizadas**

- **Java 17**: Linguagem principal do projeto.  
- **Spring Boot**: Framework para simplificar o desenvolvimento do backend.  
- **PostgreSQL**: Banco de dados relacional.  
- **Redis**: Armazenamento em cache e gerenciamento de sessões.  
- **Apache Kafka**: Mensageria para envio de resultados das sessões.  
- **Swagger**: Documentação e teste interativo da API.  
- **Docker/Docker-Compose**: Contêineres para a aplicação e serviços.  

---

## **Configuração e Execução com Docker**

### **Pré-requisitos**

- **Docker** e **Docker Compose** instalados:
  - [Instalar Docker](https://docs.docker.com/get-docker/)
  - [Instalar Docker Compose](https://docs.docker.com/compose/install/)

### **Instruções para Execução**

1. **Gerar o JAR da aplicação**:

   ```bash
   mvn clean package -DskipTests
  ```

2. **Construir a imagem Docker da aplicação**:

   ```bash
   mvn clean package -DskipTests

3. **Executar os serviços com Docker Compose**:

   ```bash
   docker-compose up --build

4. **Verificar se os containers estão rodando**:

   ```bash
   docker ps
   
---

## **Validação dos Endpoints com Swagger**

Após subir a aplicação com Docker, acesse o **Swagger UI** para testar os endpoints:

### **Acessar o Swagger**

- **URL**: [http://localhost:8081/swagger-ui/index.html#/](http://localhost:8081/swagger-ui/index.html#/)

### **Endpoints Disponíveis**

- **Associado**:
  - `POST /api/Associado`: Criar/Buscar/Deletar associado.
  
- **Pauta**:
  - `POST /api/pauta`: Criar/Buscar/Deletar pauta.

- **Sessão**:
  - `POST /api/votacao/sessao`: Abrir uma sessão de votação.

- **Voto**:
  - `POST /api/votacao/voto`: Registrar um voto.

- **Resultados**:
  - `GET /api/votacao/resultado/{pautaId}`: Consultar o resultado de uma votação.
   
---

## **Testes Automatizados e Cobertura**

### **Executando os Testes**

Execute os testes com o Maven:

   ```bash
   mvn test

### **Cobertura de Testes**
- A cobertura de testes foi implementada para garantir que:
	- Todos os serviços possuem cobertura.
	- A lógica de sessão, votação e publicação no Kafka foi validada.
	
- A cobertura pode ser visualizada usando o Jacoco (integrado ao Maven).

### **Gerar Relatório de Cobertura**

   ```bash
   mvn jacoco:report
   
   ```bash
   target/site/jacoco/index.html
   
---

Esta aplicação foi projetada para ser **modular e escalável**, usando boas práticas como:

- **SOLID** para organização dos serviços.
- **Redis com TTL** para gerenciamento eficiente de sessões.
- **Kafka** para garantir uma comunicação assíncrona entre os serviços.   

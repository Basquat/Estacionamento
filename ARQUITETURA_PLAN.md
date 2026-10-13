# Plano de Arquitetura Hexagonal / Clean Architecture

## Problema Atual
- Entidades JPA expostas diretamente nos controllers (AutoModel, Pagamento)
- Lógica de negócio misturada com persistência no Service
- Sem DTOs - acoplamento forte entre API e banco
- Package único `User` com tudo misturado

## Estrutura Proposta

```
src/main/java/basquat/estacionamento/
├── application/                    # Camada de aplicação (casos de uso)
│   ├── dto/                        # DTOs de entrada/saída
│   │   ├── request/
│   │   │   ├── AutoCreateRequest.java
│   │   │   ├── AutoUpdateRequest.java
│   │   │   ├── PagamentoRequest.java
│   │   │   └── BlacklistCreateRequest.java
│   │   └── response/
│   │       ├── AutoResponse.java
│   │       ├── AutoListResponse.java
│   │       ├── PagamentoResponse.java
│   │       └── BlacklistResponse.java
│   ├── port/                       # Portas (interfaces)
│   │   ├── in/                     # Input ports (use cases)
│   │   │   ├── AutoUseCase.java
│   │   │   ├── PagamentoUseCase.java
│   │   │   └── BlacklistUseCase.java
│   │   └── out/                    # Output ports (repositories)
│   │       ├── AutoRepositoryPort.java
│   │       ├── PagamentoRepositoryPort.java
│   │       └── BlacklistRepositoryPort.java
│   ├── service/                    # Implementação dos use cases
│   │   ├── AutoServiceImpl.java
│   │   ├── PagamentoServiceImpl.java
│   │   └── BlacklistServiceImpl.java
│   └── mapper/                     # Mappers Domain <-> DTO
│       ├── Mapper.java
│       ├── PagamentoMapper.java
│       └── BlacklistMapper.java
├── domain/                         # Camada de domínio (regras de negócio puras)
│   ├── model/
│   │   ├── Auto.java               # Entidade de domínio (sem JPA)
│   │   ├── Pagamento.java
│   │   └── BlacklistEntry.java
│   ├── event/
│   │   ├── DomainEvent.java
│   │   ├── AutoCreatedEvent.java
│   │   ├── AutoUpdatedEvent.java
│   │   └── AutoDeletedEvent.java
│   ├── exception/
│   │   ├── RecursoNaoEncontradoException.java
│   │   └── RegraNegocioException.java
│   └── service/                    # Domain services (regras complexas)
│       └── PagamentoCalculator.java
├── infrastructure/                 # Camada de infraestrutura (adapters)
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── AutoEntity.java     # Entidade JPA
│   │   │   ├── PagamentoEntity.java
│   │   │   └── BlacklistEntity.java
│   │   ├── repository/
│   │   │   ├── AutoJPARepository.java
│   │   │   ├── PagamentoJPARepository.java
│   │   │   └── BlacklistJPARepository.java
│   │   └── adapter/
│   │       ├── AutoRepositoryAdapter.java
│   │       ├── PagamentoRepositoryAdapter.java
│   │       └── BlacklistRepositoryAdapter.java
│   ├── web/
│   │   ├── controller/
│   │   │   ├── AutoController.java
│   │   │   ├── PagamentoController.java
│   │   │   └── BlacklistController.java
│   │   ├── dto/                    # DTOs específicos da web (se diferentes)
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   └── event/
│       └── SseEventPublisher.java
└── config/                         # Configurações
    ├── MapperConfig.java
    └── WebConfig.java
```

## Princípios

1. **Domain isolado** - Zero dependências externas (Spring, JPA, etc.)
2. **Application** - Orquestra use cases, conhece ports (interfaces)
3. **Infrastructure** - Implementa ports, usa frameworks (JPA, Spring Web)
4. **DTOs** - Separam API do domínio; validação no controller/DTO

## Próximos Passos

1. Criar estrutura de packages
2. Definir DTOs (request/response)
3. Criar domain models (sem JPA)
4. Criar ports (interfaces)
5. Implementar adapters JPA
6. Implementar use cases
7. Refatorar controllers para usar DTOs
8. Adicionar mappers (MapStruct ou manual)
9. Testes

## DTOs Iniciais Sugeridos

### AutoCreateRequest
- placa (obrigatório, único)
- tipo (obrigatório)
- valor (obrigatório)
- pagamentos: List<PagamentoRequest>

### AutoUpdateRequest
- placa (opcional)
- tipo (opcional)
- valor (opcional)
- pago (opcional)
- pagamentos: List<PagamentoRequest>

### AutoResponse
- id
- placa
- tipo
- valor
- pago
- entrada
- pagamentos: List<PagamentoResponse>

### PagamentoRequest
- metodo (dinheiro/pix/cartao)
- valor
- troco (opcional)

### PagamentoResponse
- id
- metodo
- valor
- troco
- data
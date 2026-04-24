# Regras de Segurança — tsrapprun

## Visão Geral

As regras de segurança do tsrapprun seguem o princípio **DENY ALL por padrão**.
Cada acesso deve ser explicitamente permitido por uma regra. Isso garante que
novos dados ou coleções criados no futuro são automaticamente protegidos.

**Responsável**: Angelo (DPO)
**Última revisão**: 2026-03-18

---

## Firestore — Estrutura e Regras

### Coleções

```
/users/{userId}                    → Perfil do usuário
/events/{eventId}                  → Dados do evento
/events/{eventId}/photos/{photoId} → Metadados das fotos
```

### Matriz de Permissões

| Coleção | Operação | Quem pode | Validação |
|---------|----------|-----------|-----------|
| `/users/{userId}` | read | Apenas o dono | `auth.uid == userId` |
| `/users/{userId}` | create | Apenas o dono | + campos obrigatórios |
| `/users/{userId}` | update | Apenas o dono | + campos obrigatórios |
| `/users/{userId}` | delete | **NINGUÉM** | Requer admin |
| `/events/{eventId}` | read | Criador + participantes | `auth.uid` na lista |
| `/events/{eventId}` | create | Qualquer autenticado | + validação de campos |
| `/events/{eventId}` | update | Apenas criador | `creatorId == auth.uid` |
| `/events/{eventId}` | delete | Apenas criador | `creatorId == auth.uid` |
| `/events/.../photos/{id}` | read | Participantes do evento | Via `get()` no evento |
| `/events/.../photos/{id}` | create | Participantes do evento | + validação |
| `/events/.../photos/{id}` | delete | Apenas quem fez upload | `uploadedBy == auth.uid` |

### Validações de Campos

**User Profile:**
- `displayName`: string, 1-100 caracteres (obrigatório)
- `email`: string, formato email válido (obrigatório)
- `createdAt`: timestamp (obrigatório)

**Event:**
- `name`: string, 1-200 caracteres (obrigatório)
- `creatorId`: deve ser `auth.uid` do criador (obrigatório)
- `participants`: lista, máximo 50 itens (controle de custo)
- `createdAt`: timestamp (obrigatório)

**Photo (metadados):**
- `uploadedBy`: deve ser `auth.uid` (obrigatório)
- `uploadedAt`: timestamp (obrigatório)
- `fileName`: string, máximo 255 caracteres (obrigatório)

---

## Storage — Estrutura e Regras

### Caminhos

```
/users/{userId}/photos/{file}        → Fotos privadas do usuário
/events/{eventId}/photos/{file}      → Fotos compartilhadas do evento
/events/{eventId}/thumbnails/{file}  → Miniaturas (geradas pelo backend)
```

### Matriz de Permissões

| Caminho | Operação | Quem pode | Restrições |
|---------|----------|-----------|------------|
| `/users/{uid}/photos/*` | read | Apenas o dono | `auth.uid == userId` |
| `/users/{uid}/photos/*` | write | Apenas o dono | 10MB max + image only |
| `/events/{id}/photos/*` | read | Qualquer autenticado | — |
| `/events/{id}/photos/*` | create | Qualquer autenticado | 10MB max + image only |
| `/events/{id}/photos/*` | delete | Quem fez upload | Via metadata |
| `/events/{id}/thumbnails/*` | read | Qualquer autenticado | — |
| `/events/{id}/thumbnails/*` | write | **NINGUÉM** | Só Cloud Functions |

### Controles de Custo no Storage

| Controle | Valor | Motivo |
|----------|-------|--------|
| Tamanho máximo por arquivo | 10 MB | Evita uploads abusivos |
| Content-type | `image/*` only | Bloqueia vídeos/PDFs/executáveis |
| Thumbnails | Read-only | Geradas server-side, menores |
| Spark Plan limite | 5 GB total | Limite automático do plano gratuito |

---

## Controle de Custos — Firebase Spark Plan

### Limites Automáticos (Spark Plan = Grátis)

| Serviço | Limite Grátis | Custo Extra |
|---------|---------------|-------------|
| Auth (Google Sign-In) | Ilimitado | $0 |
| Firestore reads | 50.000/dia | N/A (Spark) |
| Firestore writes | 20.000/dia | N/A (Spark) |
| Firestore storage | 1 GiB | N/A (Spark) |
| Storage space | 5 GB | N/A (Spark) |
| Storage download | 1 GB/dia | N/A (Spark) |
| Storage upload | 1 GB/dia | N/A (Spark) |

### Controles Adicionais Implementados

1. **Máx 50 participantes por evento** — limita reads de fotos compartilhadas
2. **10MB por foto** — controla uso de storage
3. **Apenas imagens** — bloqueia uploads de arquivos grandes
4. **Thumbnails read-only** — menores que originais = menos egress
5. **Local-first** — fotos ficam no celular por padrão, cloud é opcional

---

## Arquivos Locais

As regras estão versionadas localmente em:

- `firebase-rules/firestore.rules` — Regras do Firestore
- `firebase-rules/storage.rules` — Regras do Storage

**Workflow de atualização:**
1. Edite o arquivo local
2. Revise as mudanças (code review)
3. Copie para o Firebase Console → Rules → Publish
4. Commit no git com descrição da mudança

> **Futuro**: Implementar deploy automático via Firebase CLI (`firebase deploy --only firestore:rules,storage`)

---

## Checklist de Segurança

- [x] DENY ALL por padrão (Firestore)
- [x] DENY ALL por padrão (Storage)
- [x] Usuários só acessam próprios dados
- [x] Validação de todos os campos obrigatórios
- [x] Limite de tamanho de arquivo (10MB)
- [x] Apenas imagens aceitas no Storage
- [x] Deleção de perfil bloqueada (requer admin)
- [x] Thumbnails write-only pelo backend
- [x] Spark Plan (sem custos imprevistos)
- [x] Regras versionadas no git
- [ ] App Check (fase futura — proteção contra bots)
- [ ] Firebase CLI deploy automático
- [ ] Auditoria periódica das regras

# JWT + Cookies (HttpOnly) + Refresh no banco — Fluxo completo

Este projeto usa autenticação **stateless** com:
- **Access Token (JWT)** curto em cookie **HttpOnly**
- **Refresh Token opaco** longo em cookie **HttpOnly**
- Refresh é salvo no banco como **hash (SHA-256)** com **rotação** e **revogação**

---

## Objetivo

- O endpoint de login retorna **apenas uma mensagem**
- Os tokens são **salvos em cookies**
- O frontend **não precisa** enviar `Authorization: Bearer ...` manualmente
- O backend autentica lendo o token do cookie

---

## Tokens

### 1) Access Token (JWT)
- Curto (ex.: 15 min)
- Cookie: `ACCESS_TOKEN` (HttpOnly)
- Contém claims (dados assinados):
    - `iss` (issuer): quem emitiu
    - `sub` (subject): `userId`
    - `exp`: expiração
    - `roles`: permissões
    - `email`: opcional (não sensível)

> JWT é **assinado**, não criptografado: não coloque dados sensíveis no payload.

### 2) Refresh Token (opaco)
- Longo (ex.: 14 dias)
- Cookie: `REFRESH_TOKEN` (HttpOnly)
- É uma string aleatória (não-JWT)
- No banco é salvo apenas o **hash SHA-256** do refresh

---

## Endpoints

### `POST /api/auth/login`
**Entrada:** JSON com email e senha
```json
{ "email": "user@email.com", "password": "123" }

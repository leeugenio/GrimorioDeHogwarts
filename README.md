# Grimório de Hogwarts 🧙

App Android desenvolvido para a disciplina **DS151 – Desenvolvimento para Dispositivos Móveis**.

Consome a [Hogwarts API](https://hogwarts-api.com) (gratuita, sem chave) para:

- buscar **personagens** pelo nome e exibir nome completo, espécie e casa de todos os encontrados;
- buscar uma **criatura** pelo nome e exibir nome e imagem.

## Telas

| Activity | Função |
|---|---|
| `MainActivity` | Menu inicial |
| `PersonagemActivity` | Busca de personagem |
| `CriaturaActivity` | Busca de criatura |

## Tecnologias

- Kotlin, minSdk 29
- Retrofit 2.9.0 + Gson para consumir a API
- Corrotinas (`lifecycleScope` + `Dispatchers.IO`) para não fazer rede na thread principal
- Picasso 2.8 para carregar as imagens em segundo plano

## Regras implementadas

- Texto limpo com `trim()`, campo vazio e menos de 3 caracteres bloqueiam a consulta
- ProgressBar visível durante a consulta e durante o carregamento da imagem
- "Nenhum personagem encontrado" / "Nenhuma criatura encontrada" quando não há resultado
- Espécie ou casa ausentes aparecem como "Não informado"
- Criatura sem imagem (ou com falha ao carregar) mostra uma imagem do próprio app e "Imagem indisponível"
- Sem internet ou erro de servidor: mensagem amigável, o app não fecha e é possível tentar de novo
- Botão "Voltar ao menu" nas telas de busca
- Telas fixadas em retrato

## Como executar

1. Clone o repositório:
   ```
   git clone https://github.com/leeugenio/GrimorioDeHogwarts.git
   ```
2. Abra a pasta no Android Studio e aguarde o Gradle Sync.
3. Execute em um emulador ou dispositivo com Android 10 (API 29) ou superior.

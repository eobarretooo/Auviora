# Auviora

**Private AI. Real Capabilities.**

Aplicativo Android independente de IA privada, com inferência no próprio aparelho. Versão Alpha **0.1.0-alpha.1 (1)**, publicada como pré-release com APK ARM64 assinado em 22/10/2026. A base do aplicativo e a geração de texto offline foram validadas em hardware. Os pesos do modelo são obtidos separadamente e não estão incluídos no APK nem no repositório.

## O que está disponível

Chat com streaming e botão de parada, estados reais do motor, download/importação de arquivos verificados, carregamento e descarregamento do modelo, histórico local básico com pesquisa por título, navegação lateral, temas claro/escuro/sistema e escolha de idioma inglês/português brasileiro. Conversas salvas abrem somente para leitura; o contexto nativo ainda não é restaurado.

A interface atual aceita **texto**. O motor inclui um projetor visual, mas anexos de imagem e validação visual neste novo aplicativo ainda estão pendentes. Agentes executáveis, automação de outros aplicativos e memória persistente avançada são recursos planejados.

## Compilar e usar

Consulte os requisitos e comandos completos no [README principal](../../../README.md#build-and-install) e em [Testing](../../TESTING.md). A configuração atual usa JDK 21, SDK 36.1, NDK 27.0.12077973, CMake 4.1.2 e o Gradle wrapper 9.3.1. O APK contém somente ARM64, tem API mínima 24 e foi validado em um aparelho Android 16/API 36; isso não garante funcionamento em todos os aparelhos da faixa.

Após instalar o APK, abra **Modelos**, baixe o par verificado MiniCPM-V 4.7 1B de aproximadamente 1,78 GB ou importe os arquivos exatos, mantenha o aplicativo aberto e escolha **Carregar modelo**. Aguarde **Pronto** e volte à conversa. O download usa internet; depois do provisionamento, a inferência de texto funciona offline. Use **Descarregar modelo** ao terminar.

O modelo compacto pode misturar idiomas, inventar sua identidade e errar fatos. A demonstração preserva uma resposta que forneceu oito itens quando o pedido era dez; não representa aprovação da qualidade das respostas.

## Demonstração e documentação

- [GIF real de inferência offline](../../media/gifs/offline-inference.gif)
- [Galeria, origem e reprodução das capturas](../../media/README.md)
- [Arquitetura](../../ARCHITECTURE.md), [roadmap](../../ROADMAP.md) e [estado atual](../../AI_HANDOFF.md)
- [Relatório da fase 2.1](../../PHASES/PHASE_02_1.md), [contribuições](../../CONTRIBUTING.md) e [changelog](../../CHANGELOG.md)

A documentação técnica em inglês é a fonte canônica. Licenças e créditos do código reutilizado foram preservados; os pesos têm termos próprios e a redistribuição da conversão ainda exige revisão. A fase 2.2 publicou a primeira Alpha após as verificações de assinatura, integridade e funcionamento. A fase 3 não foi iniciada.

## Distribuição Alpha

[**Baixar o APK ARM64 assinado**](https://github.com/eobarretooo/Auviora/releases/download/v0.1.0-alpha.1/Auviora-v0.1.0-alpha.1-arm64-v8a.apk) · [Pré-release v0.1.0-alpha.1](https://github.com/eobarretooo/Auviora/releases/tag/v0.1.0-alpha.1) · [SHA256SUMS.txt](https://github.com/eobarretooo/Auviora/releases/download/v0.1.0-alpha.1/SHA256SUMS.txt)

O APK tem 42.736.118 bytes (40,76 MiB). Baixe o APK ARM64 e o SHA256SUMS.txt da mesma release e compare com `Get-FileHash ARQUIVO.apk -Algorithm SHA256`. Os quatro anexos públicos foram baixados sem autenticação e conferidos. O modelo é obtido separadamente, com aproximadamente 1,78 GB. API mínima 24 é um requisito de compilação; o aparelho físico validado usa Android 16. O APK assinado também executou inferência em um emulador Android 9 com tradução ARM64; isso não equivale a testar hardware ARM64 com Android 9.

A assinatura definitiva é diferente da assinatura de depuração. Uma eventual desinstalação apaga conversas e GGUFs privados; o aplicativo ainda não oferece exportação do histórico. Nenhuma desinstalação é automática. Consulte o [processo de release](../../RELEASE_PROCESS.md), as [notas Alpha](../../releases/v0.1.0-alpha.1.md) e o [changelog](../../CHANGELOG.md).

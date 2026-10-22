# Auviora

**Private AI. Real Capabilities.**

Aplicativo Android independente de IA privada, com inferência no próprio aparelho. Versão de desenvolvimento **0.1.0-alpha.1 (1)**. A fase 2.0 concluiu a base do aplicativo e validou geração de texto offline em hardware; a fase 2.1 organiza a apresentação, as capturas reais e a demonstração. Não há APK publicado nem pesos incluídos no repositório.

## O que está disponível

Chat com streaming e botão de parada, estados reais do motor, download/importação de arquivos verificados, carregamento e descarregamento do modelo, histórico local básico com pesquisa por título, navegação lateral, temas claro/escuro/sistema e escolha de idioma inglês/português brasileiro. Conversas salvas abrem somente para leitura; o contexto nativo ainda não é restaurado.

A interface atual aceita **texto**. O motor inclui um projetor visual, mas anexos de imagem e validação visual neste novo aplicativo ainda estão pendentes. Agentes executáveis, automação de outros aplicativos e memória persistente avançada são recursos planejados.

## Compilar e usar

Consulte os requisitos e comandos completos no [README principal](../../../README.md#build-and-install) e em [Testing](../../TESTING.md). A configuração atual usa JDK 21, SDK 36.1, NDK 27.0.12077973, CMake 4.1.2 e o Gradle wrapper 9.3.1. O APK contém somente ARM64, tem API mínima 24 e foi validado em um aparelho Android 16/API 36; isso não garante funcionamento em todos os aparelhos da faixa.

Após instalar o APK de depuração, abra **Modelos**, baixe o par verificado MiniCPM-V 4.7 1B de aproximadamente 1,78 GB ou importe os arquivos exatos, mantenha o aplicativo aberto e escolha **Carregar modelo**. Aguarde **Pronto** e volte à conversa. O download usa internet; depois do provisionamento, a inferência de texto funciona offline. Use **Descarregar modelo** ao terminar.

O modelo compacto pode misturar idiomas, inventar sua identidade e errar fatos. A demonstração preserva uma resposta que forneceu oito itens quando o pedido era dez; não representa aprovação da qualidade das respostas.

## Demonstração e documentação

- [GIF real de inferência offline](../../media/gifs/offline-inference.gif)
- [Galeria, origem e reprodução das capturas](../../media/README.md)
- [Arquitetura](../../ARCHITECTURE.md), [roadmap](../../ROADMAP.md) e [estado atual](../../AI_HANDOFF.md)
- [Relatório da fase 2.1](../../PHASES/PHASE_02_1.md), [contribuições](../../CONTRIBUTING.md) e [changelog](../../CHANGELOG.md)

A documentação técnica em inglês é a fonte canônica. Licenças e créditos do código reutilizado foram preservados; os pesos têm termos próprios e a redistribuição da conversão ainda exige revisão. Nenhuma publicação, envio ao GitHub ou fase 3 foi iniciada automaticamente.

## Distribuição Alpha

Acompanhe a página real de [GitHub Releases](https://github.com/eobarretooo/Auviora/releases). A preparação da versão v0.1.0-alpha.1 está em andamento; ainda não há APK assinado publicado. Não existe link direto de download anunciado antes da publicação verificada.

Quando disponível, baixe o APK ARM64 e o SHA256SUMS.txt da mesma release e compare com `Get-FileHash ARQUIVO.apk -Algorithm SHA256`. O modelo é obtido separadamente, com aproximadamente 1,78 GB. API mínima 24 é um requisito de compilação; o aparelho validado usa Android 16.

A assinatura definitiva será diferente da assinatura de depuração. Uma eventual desinstalação apaga conversas e GGUFs privados; o aplicativo ainda não oferece exportação do histórico. Nenhuma desinstalação é automática. Consulte o [processo de release](../../RELEASE_PROCESS.md), as [notas Alpha](../../releases/v0.1.0-alpha.1.md) e o [changelog](../../CHANGELOG.md).

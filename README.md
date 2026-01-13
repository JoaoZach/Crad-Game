# Crad-Game

# Introdução
Este projeto e um jogo de multiplayer UNO card game desenvolvido para Android usando Jetpack Composo.

# Estrutura do Projeto
A estrutura do projeto segue o padrão MVVM (Model-View-ViewModel), garantindo sepração de responsabilidades e melhor manutenção do código.

# Lista de Funcionalidades da Aplicação
- Criação de jogos multijogador
- Entrada em jogos existentes através de ID
- Sincronização en tenpo real entre jogos
- Distribuição automática de cartas
- Controlo de turnos
- Implementação das regras principais de UNO:
  - Cartras númericas
  - Skip
  - Reverse
  - Draw Two
  - Wild
  - Wild Draw Four
- Escolha de cor ao jogar cartas Wild
- Indicador visual da cor escolhida
- Compra de cartas
- Gestão da direção do jogo

# Desenhos, Esquemas e Protótipos da Aplicação
A interface foi desenvolvida com base numa abordagem simples e funcional:

## Ecrâ Principal
- Botões para cirar ou entrar no jogo
- Campo de texto

## Ecrã de Jogo
- Lista de jogadores
- Indicação do jogador da vez
- Cartas na messa
- Mão do jogador da vez
- Botão para comprar carta

## Cartas
- Utilização de sprite sheet para a representar as cartas de UNO
- Renderização com Canvas

# Modelo de Dados
## Card
data class Card(
    val color: CardColor = CardColor.RED,
    var value: CardValue = CardValue.ZERO
)

## Game
data class Game(
    val gameId : String = "",
    val players : List<String> = emptyList(),
    val currentTurn : Int = 0,
    val direction : Int = 1,
    val cardsOnTable : List<Card> = emptyList(),
    val playersHands : Map<String, List<Card>> = emptyMap(),
    val deck: List<Card> = emptyList(),
    val started : Boolean = false
)

## CardValue
enum class CardValue {
    ZERO, ONE, TWO, THREE, FOUR, FIVE , SIX, SEVEN, EIGHT, NINE,
    SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
}

## CardColor
enum class CardColor {
    RED, BLUE, GREEN, YELLOW, WILD
}

# Implementação do Projeto
A lógica do jogo está concentrada na classe GameViewModel.

## Gestão de Turnos
- Controlada através do índice currentTurn
- A direção do jogo é definida por direction (1 ou -1)
- O próximo jogador é calculado com operações modulares

## Cartas Wild
- Podem ser jogadas independentemente da cor atual
- o Jogador escolhe a nova carta através de um dialgo
- A cor escolhida é aplicada à carta e exibida na interface
- A proxima jogada deve respeitar essa cor

## Sincronização
- Todas as alterações de estado são enviadas para o Firestore
- Os clientes escutam atualizações em tempo real

# Tecnologias Usadas
- Kotolin
- Jetpack Compose
- FireBase FireStore
- FireBase Authentication
- StateFlow
- Android Studio

# Dificuldades encontradas
- Gestão do deck de cartas de forma consistente
- Sincronização de estado em tempo real sem conflitos
- Garantir que ações como comprar cartas e jogar cartas fossem atómaticas
- Criação de salas em que cria ids para os utilizadores poderem entrar

# Conclusões
O desenvolvimento deste projeto permitiu aplicar conhecimentos práticos de desenvolvimento Android morderno, bem com conceitos de arquitetura, gestão de estado e jogos multijogador.

Apesar das dificuldades encontradas, foi possivel criar um jogo funcional e extensivel, respetiando as regras principas de UNO. O projeto pode ser facilmente melhorado com novas funcionalidades, como deteção de vitoria, animações ou modos de jogo adicionais.

Este trabalho contribui significativamente para a consolidação de competências em Kotlin, Jetpack Compose e FireBase.

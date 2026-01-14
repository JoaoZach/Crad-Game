# Crad-Game

# Introdução
Este projeto consite no desnvolvimento de um jogo UNO multijogador, para Android, utilizando Jetpack Compose, FireBase e a arquitetura MVVM.
A aplicação permite o registro e autenticação de utilizadores, criação e entrada em jogos, e a execução completa do jogo UNO em tempo real.

# Estrutura do Projeto
A estrutura do codigo foi organizada de forma modular para facilitar a manutenção e a escalabilidade:
com.example.unogame
│
├── models
│   ├── Card
│   ├── CardColor
│   ├── CardValue
│   ├── Game
│   └── User
│
├── navigation
│   ├── NavGraph
│   └── Screen
│
├── repositories
│   ├── AuthRepository
│   └── GameRepository
│
├── ui.theme
│   └── game
│       ├── CardItem
│       ├── GameScreen
│       ├── GameViewModel
│       └── UnoDeck
│
├── login
│   ├── LoginScreen
│   └── LoginViewModel
│
├── register
│   ├── RegisterScreen
│   └── RegisterViewModel
│
└── MainActivity

# Lista de Funcionalidades da Aplicação
- Registo de utilizadores
- Login com email e palavra-passe
- Navegação entre ecrãs
- Criação de novos jogos
- Entrada em jogos existentes através de Game ID
- Jogo UNO multijogador em tempo real
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
- Sincronização em tempo real entre jogadores

# Desenhos, Esquemas e Protótipos da Aplicação
Aaplicação segue um fluxo simples e intuitivo:

## Fluxo da Aplicação
- Ecrâ de Login / registo
- Ecrâ de criação ou entrada em jogo
- Ecrâ principal do jogo UNO

## Interface do jogo
- Cartas representadas graficamente através de sprite sheet
- Mão do jogador apresentada em lista horizontal
- Cartas na mesa visicéis a todos os jogadores
- Diálgo para esoclha de cor em cartas Wild

# Modelo de Dados
## Card
data class Card(
    val color: CardColor = CardColor.RED,
    var value: CardValue = CardValue.ZERO
)

## Game
```
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
```
## CardValue
```
enum class CardValue {
    ZERO, ONE, TWO, THREE, FOUR, FIVE , SIX, SEVEN, EIGHT, NINE,
    SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
}
```
## CardColor
```
enum class CardColor {
    RED, BLUE, GREEN, YELLOW, WILD
}
```
## User
```
data class User (
    var uId : String? = null,
    var name : String? = null,
    var email : String? = null,
)
````
# Implementação do Projeto
A aplicação segue o padrão MVVM (ModelView-ViewModel):
- Model: Representa os dados (Game, Card, User)
- View: Interfaces criadas com Jectpack Compose
- ViewModel: Contém a lógica do jogo e gere o estado

## Autenticação
- Firebase Autehntication
- Gerida através do AuthRepository

## Lógica do Jogo
- Implementada no GameViewmodel
- Regras de UNO aplicadas de forma centralizada
- Estado do jogo sincronizado via FireBase

## Navgação
- Jetpack Navigation Compose
- Rotas centralizadas no NavGraph

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
- Gestão correta de turnos entre jogadores

# Conclusões
O desenvolvimento deste projeto permitiu aplicar conhecimentos práticos de desenvolvimento Android morderno, bem com conceitos de arquitetura, gestão de estado e jogos multijogador.

Apesar das dificuldades encontradas, foi possivel criar um jogo funcional e extensivel, respetiando as regras principas de UNO. O projeto pode ser facilmente melhorado com novas funcionalidades, como deteção de vitoria, animações ou modos de jogo adicionais.

Este trabalho contribui significativamente para a consolidação de competências em Kotlin, Jetpack Compose e FireBase.

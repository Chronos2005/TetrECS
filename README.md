# TetrECS

A fast-paced block-placement puzzle game built in Java with JavaFX. It's a cross between Tetris and a grid puzzle: place pieces on a 5×5 board and clear full rows or columns before the timer runs out.



## Features

- **Single-player challenge mode**: place pieces on a 5×5 grid and clear complete rows and columns to score
- **Scoring, levels and multipliers**:
  - Each clear scores `lines × blocks × multiplier × 10`
  - The multiplier grows with each consecutive clear
  - You go up a level every 1,000 points
- **Increasing pressure**: the time per piece starts at 12 seconds and drops by 0.5 seconds per level, down to 2.5 seconds. If a piece times out, you lose one of your 3 lives
- **Piece preview and swap**: see the current and next piece, rotate the current piece, or swap the two
- **Animated timer bar and block fade-out effects** when lines clear
- **High scores**: local scores are saved to `scores.txt`, and online high scores come from the game server
- **Multiplayer lobby**: list, create, join and leave channels and chat with other players over WebSockets *(see [Known limitations](#known-limitations))*
- **Music and sound effects** for each scene and game event
- **Mouse and full keyboard support**

## Controls

| Action | Keys |
| --- | --- |
| Move the aim | Arrow keys / `W` `A` `S` `D` |
| Place piece | `Enter` or left-click a cell |
| Rotate piece | `E` |
| Swap current and next piece | `Space` |
| Back / quit to menu | `Esc` |

## Getting started

### Requirements

- JDK 17 or later
- Maven 3.8 or later

### Run

```bash
git clone https://github.com/Chronos2005/TetrECS.git
cd TetrECS
mvn clean javafx:run
```

### Build a runnable JAR

```bash
mvn clean package
java -jar target/tetrecs-1.0-SNAPSHOT.jar
```

## Project structure

```
src/main/java/uk/ac/soton/comp1206/
├── game/        # Core logic: Game, Grid, GamePiece, MultiplayerGame
├── component/   # Custom JavaFX controls: GameBoard, GameBlock, PieceBoard, ScoresList
├── scene/       # Screens: Menu, Instructions, Challenge, Lobby, Multiplayer, Scores
├── event/       # Listener interfaces for decoupled game/UI communication
├── network/     # WebSocket Communicator
└── ui/          # GameWindow and GamePane (scene management and scaling)
```

The game logic and UI are kept separate with listener interfaces such as `NextPieceListener`, `LineClearedListener` and `GameLoopListener`. JavaFX properties bind the score, level, lives and multiplier directly to the UI.

## Tech stack

- Java 17 (modular, `module-info.java`)
- JavaFX 21 (controls, media)
- nv-websocket-client for networking
- Log4j 2 for logging
- Maven

## Known limitations

- Online features (lobby, multiplayer and online high scores) connect to a University of Southampton server (`ofb-labs.soton.ac.uk`). That server isn't publicly reachable, so only single-player works outside the university network.
- The multiplayer game mode is incomplete.

## Acknowledgements

This was coursework for **COMP1206 Programming II** at the University of Southampton. The module provided the starter framework: the project skeleton, window and scene base classes, the WebSocket communicator, the piece definitions, and the media assets. I implemented the gameplay, scoring and timer loop, all the game scenes, the high-score system, the multiplayer lobby, and the UI components and animations built on top of it.

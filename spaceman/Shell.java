package spaceman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import spaceman.model.GuessChar;
import spaceman.model.Phase;
import spaceman.model.Spaceman;

/** provides point of interaction between player and the Spaceman game through the command line */
public class Shell {

  // used for command prompt styling
  private static final String STARTING_COLUMN = ": ";
  private static final String HIDDEN_CHARACTER = "_ ";
  private static final String ENDING_COLUMN = ":";
  private static final String PROMPT = "SP> ";

  // Command names
  private static final String NEW_GAME = "NEWGAME";
  private static final String GUESS = "GUESS";
  private static final String DISPLAY = "DISPLAY";
  private static final String FORFEIT = "FORFEIT";
  private static final String QUIT = "QUIT";

  // Error messages
  private static final String NO_GAME_RUNNING = "No game running.";
  private static final String INVALID_INPUT = "Invalid input!";

  // array constant that holds the 7 steps of the flying saucer
  private static final String LINE_BREAK = System.lineSeparator();
  private static final String[] FLYING_SAUCER = {
    "        _.---._" + LINE_BREAK + "      .'       '." + LINE_BREAK,
    "  _.-~===========~-._" + LINE_BREAK,
    " (___________________)" + LINE_BREAK,
    "       \\_______/" + LINE_BREAK,
    "        |     |" + LINE_BREAK,
    "        |_0/  |" + LINE_BREAK,
    "        |  \\  |" + LINE_BREAK + "        |  /\\ |" + LINE_BREAK
  };

  private Spaceman game;

  /**
   * Read and process input until the quit command has been entered.
   *
   * @param args Command line arguments.
   * @throws IOException Error reading from stdin.
   */
  public static void main(String[] args) throws IOException {
    final Shell shell = new Shell();
    shell.run();
  }

  /**
   * Run the spaceman shell. Shows prompt 'SP> ', takes commands from the user and executes them.
   */
  public void run() {
    boolean quit = false;

    while (!quit) {
      String userInput = read();
      String command = userInput;
      String parameter = null;

      // user input processing
      if (command.indexOf(' ') != -1) {
        command = userInput.substring(0, userInput.indexOf(' ')).toUpperCase(Locale.ROOT);
        parameter = userInput.substring(userInput.indexOf(' ') + 1);
      }
      command = command.toUpperCase(Locale.ROOT);

      if (userInput.toUpperCase(Locale.ROOT).matches("^NEWGAME\\s.+")) {
        newGameSetWord(parameter);
        continue;
      }

      switch (command) {
        case NEW_GAME:
          newGame();
          break;
        case DISPLAY:
          if (!parameterChecker(parameter, 0)) {
            break;
          }
          display();
          break;
        case FORFEIT:
          if (!parameterChecker(parameter, 0)) {
            break;
          }
          forfeit();
          break;
        case GUESS:
          if (!parameterChecker(parameter, 1)) {
            break;
          }
          guess(parameter);
          break;
        case QUIT:
          if (!parameterChecker(parameter, 0)) {
            break;
          }
          quit = true;
          break;
        default:
          printError("Unknown command.");
          break;
      }

      // TODO: add prompt, read user input and handle the commands by parsing and calling the
      // corresponding method
      // (if a separate method makes sense - see below)

    }
  }

  private String read() {
    BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    print(PROMPT);
    try {
      return in.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    printError(INVALID_INPUT);
    return INVALID_INPUT;
  }

  private boolean parameterChecker(String parameter, int expectedAmount) {
    if (parameter == null && expectedAmount == 1) {
      printError("Wrong number of arguments, expected one");
      return false;
    }
    if (parameter != null && expectedAmount == 0) {
      printError("Wrong number of arguments, expected zero");
      return false;
    }
    return true;
  }

  private void newGame() {
    terminateRunningGame();
    game = Spaceman.create();
    setupHiddenWord(true);
  }

  private void newGameSetWord(String wordToGuess) {
    terminateRunningGame();
    game = Spaceman.create(wordToGuess);
    setupHiddenWord(true);
  }

  private void guess(String parameter) {
    if (!gameIsRunning()) {
      printError(NO_GAME_RUNNING);
      return;
    }
    try {
      // parameter validation + processing
      if (parameter.length() > 1) {
        printError("You can guess only one character at a time.");
        return;
      }
      char character = parameter.charAt(0);
      // guessing
      game.guess(character);
      // showing results
      boolean isFinished = game.getState().getCurrentPhase() == Phase.FINISHED;
      boolean hasLost = game.getState().getCountdownValue() == 0;
      draw();
      setupHiddenWord(!hasLost);

      if (isFinished) {
        if(!hasLost){
          println("Congratulations, you win!");
        }
      }

    } catch (IllegalStateException e) {
      e.printStackTrace();
      printError(NO_GAME_RUNNING);
    }
  }

  private void draw() {
    int counter = game.getState().getCountdownValue();
    if (counter < 7) {
      print(FLYING_SAUCER[0]);
    }
    if (counter < 6) {
      print(FLYING_SAUCER[1]);
    }
    if (counter < 5) {
      print(FLYING_SAUCER[2]);
    }
    if (counter < 4) {
      print(FLYING_SAUCER[3]);
    }
    if (counter < 3) {
      print(FLYING_SAUCER[4]);
    }
    if (counter < 2) {
      print(FLYING_SAUCER[5]);
    }
    if (counter < 1) {
      print(FLYING_SAUCER[6]);
    }
  }

  private void setupHiddenWord(boolean canStillWin) {
    if (!gameIsRunning() && !canStillWin) {
      printError(NO_GAME_RUNNING);
      return;
    }
    List<GuessChar> word = game.getState().getWord().getCharacters();
    print(STARTING_COLUMN);
    for (int i = 0; i < word.size(); i++) {
      Optional<Character> character = word.get(i).maybeGetCharacter();
      if (character.isEmpty()) {
        print(HIDDEN_CHARACTER);
      }
      if (character.isPresent()) {
        print(character.get() + " ");
      }
    }
    println(ENDING_COLUMN);
  }

  private void display(){
    if (!gameIsRunning()) {
      printError(NO_GAME_RUNNING);
      return;
    }
    draw();
    setupHiddenWord(true);
  }
  private void forfeit() {
    try {
      if (gameIsRunning()) {
        game.forfeit();
        draw();
        setupHiddenWord(true);

        return;
      }
      printError(NO_GAME_RUNNING);
    } catch (IllegalStateException e) {
      e.printStackTrace();
      printError(NO_GAME_RUNNING);
    }
  }

  private void terminateRunningGame() {
    try {
      if (gameIsRunning()) {
        game.forfeit();
        return;
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      printError(NO_GAME_RUNNING);
    }
  }

  private boolean gameIsRunning() {
    if (game == null) {
      return false;
    }
    if (game.getState().getCurrentPhase() != Phase.RUNNING) {
      return false;
    }
    return true;
  }

  private void print(final String message) {
    System.out.print(message);
  }

  private void println(final String message) {
    System.out.println(message);
  }

  private void printError(final String message) {
    println("Error! " + message);
  }
}

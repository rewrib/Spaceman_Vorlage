package spaceman.model;

import java.util.Locale;

/**
 * Represents the Spaceman game as a whole
 */
public class Spaceman {

  /** Value the game countdown is started with. */
  private static final int COUNTDOWN_START = 7;

  private GameState state;

  private Spaceman(final String wordToGuess) {
    // set game state with word to guess
    state = new GameState(wordToGuess, COUNTDOWN_START);
  }

  /**
   * Create a new Spaceman game with a word chosen randomly from the {@link spaceman.model.WordDatabase}.
   *
   * @return Spaceman instance with the random word
   */
  public static Spaceman create() {
    // ask word database for a word
    WordDatabase wordDatabase = new WordDatabase();
    String randomWord = wordDatabase.getWord();
    Spaceman spaceman = new Spaceman(randomWord);
    return spaceman;
  }

  /**
   * Create a Spaceman object with the given word.
   *
   * @param wordToGuess word to use for the game
   * @return Spaceman instance for the given word
   */
  public static Spaceman create(String wordToGuess) {
    Spaceman spaceman = new Spaceman(wordToGuess);
    return spaceman;
  }

  public GameState getState() {
    return state;
  }

  /**
   * Guess the given character. If the character is in the current word to guess, all occurrences of
   * the character are revealed in the word. If the character is not in the current word to guess,
   * the countdown decreases by one.
   *
   * <p>This method can only be called on an active game. Otherwise, an <code>IllegalStateException
   * </code> is thrown.
   *
   * <p>If the guess is correct and the whole word is revealed as a consequence, the game is
   * stopped.
   *
   * @param guessedCharacter character to guess
   * @return <code>true</code>if the guess was successful. <code>false</code> otherwise.
   * @throws IllegalStateException if the current Spaceman game is not running
   */
  public boolean guess(char guessedCharacter){
    // Check whether game is still running and throw an IllegalStateException
    // otherwise
    if (state.getCurrentPhase() != Phase.RUNNING)
    {
      throw new IllegalStateException();
    }
    // Check whether the guessed character is in the current word to guess and
    // reveal it/decrease the countdown.
    char upperCaseCharacter = Character.toUpperCase(guessedCharacter);
    char lowerCaseCharacter = Character.toLowerCase(guessedCharacter);
    String wordHasChar0 = state
        .getWord()
        .getCompleteWord()
        .toUpperCase(Locale.ROOT);
    boolean wordHasChar = state
        .getWord()
        .getCompleteWord()
        .toLowerCase(Locale.ROOT)
        .indexOf(lowerCaseCharacter) != -1;
    if(wordHasChar){
      state.getWord().guess(upperCaseCharacter);
      state.getWord().guess(lowerCaseCharacter);
      if(state.getWord().isRevealed()){
        state.finishGame();
      }
      return true;
    }
    state.decreaseCountdown();

    // If the countdown reached 0 or the full word is revealed, game changes state
    // accordingly.
    if(state.getCountdownValue() == 0){
      state.getWord().revealAll();
      state.finishGame();
    }
    return false;
  }

  /**
   * Forfeit the current game. Fully reveal the word-to-guess and end the current game.
   *
   * <p>This method can only be called on an active game. Otherwise, an <code>IllegalStateException
   * </code> is thrown.
   *
   * @throws IllegalStateException if the current Spaceman game is not running
   */
  public void forfeit() {
    if (state.getCurrentPhase() != Phase.RUNNING)
    {
      throw new IllegalStateException();
    }
    state.getWord().revealAll();
    state.setCountdownToZero();
    state.finishGame();
  }
}

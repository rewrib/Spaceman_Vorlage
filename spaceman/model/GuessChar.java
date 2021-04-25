package spaceman.model;

import java.util.Optional;

/** A single character guess. This can be either an unrevealed character or a revealed character. */
public class GuessChar {

  // Optional.empty() if the character was not guessed yet.
  // Optional.of(char), otherwise.
  private final Optional<Character> character;

  /** Creates an empty GuessChar. */
  GuessChar() {
    character = Optional.empty();
  }

  /** Creates a GuessChar with the corresponding revealed character. */
  GuessChar(char revealedChar) {
    character = Optional.of(revealedChar);
  }

  /**
   * Returns stored character, if previously guessed, otherwise a Optional.empty() is being returned
   *
   * @return character
   */
  public Optional<Character> maybeGetCharacter() {
    return character;
  }
}

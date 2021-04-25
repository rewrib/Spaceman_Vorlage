package spaceman.model;

import java.util.Arrays;
import java.util.List;

/**
 * Stores the word, that the player is trying to guess and the progress the player made towards
 * guessing it.
 */
public class WordToGuess {

  private String completeWord;
  private List<GuessChar> revealedCharacters;

  WordToGuess(final String word) {
    // initialize `revealedCharacters`
    // All characters except for space (" ") should be initialized as hidden
    // GuessChar.
    // Spaces (" ") should be initialized as revealed GuessChar.
    completeWord = word;
    GuessChar[] chrArray = new GuessChar[word.length()];
    Arrays.fill(chrArray, new GuessChar());
    revealedCharacters = Arrays.asList(chrArray);
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i) == ' ') {
        revealedCharacters.set(i, new GuessChar(' '));
        continue;
      }
    }
  }

  /** Return the complete word. */
  String getCompleteWord() {
    return completeWord;
  }

  /** Return the length of the word. */
  public int getWordLength() {
    return completeWord.length();
  }

  /**
   * Get the list of characters in the word. Characters can be revealed or hidden. This information
   * is defined by the used {@link GuessChar}.
   *
   * @return the list of characters in the word (as <code>GuessChar</code>s)
   */
  public List<GuessChar> getCharacters() {
    return revealedCharacters;
  }

  /**
   * Guess the given character. If the character is in the word, all occurrences of the character
   * are revealed in the word.
   *
   * @param guessedCharacter character to guess
   * @return <code>true</code>if the character is in the word. <code>false</code> otherwise
   */
  boolean guess(final char guessedCharacter) {
    // "revealing" a character means replacing the empty GuessChar
    // object at the corresponding
    // position in `revealedCharacters` with the correct character.
    for (int i = 0; i < completeWord.length(); i++) {
      if (completeWord.charAt(i) == guessedCharacter) {
        revealedCharacters.set(i, new GuessChar(guessedCharacter));
        continue;
      }
    }
    return true;
  }

  boolean isRevealed() {
    int unknownCharCount = 0;
    for (GuessChar guessChar : revealedCharacters) {
      if (guessChar.maybeGetCharacter().isEmpty()) {
        unknownCharCount += 1;
      }
    }
    return unknownCharCount == 0;
  }

  /** Reveal all characters. */
  void revealAll() {
    for (int i = 0; i < completeWord.length(); i++) {
      revealedCharacters.set(i, new GuessChar(completeWord.charAt(i)));
      continue;
    }
  }
}

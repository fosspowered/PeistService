package testutils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Interface to create mock dummy objects for unit testing.
 *
 * @param <T> Object type to create dummy objects for.
 */
public interface DummyObject<T> {

  /**
   * Build dummy object.
   *
   * @return Return the mock object.
   */
  T build();

  /**
   * Build list of dummy objects.
   *
   * @param size Number of dummoy objects to create.
   * @return List of mock objects.
   */
  default List<T> buildList(int size) {
    return IntStream.range(0, size).mapToObj(i -> build()).collect(Collectors.toList());
  }
}

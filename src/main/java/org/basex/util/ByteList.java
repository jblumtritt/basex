package org.basex.util;

import static org.basex.util.Token.*;
import java.util.Arrays;

/**
 * This is a simple container for byte values.
 *
 * @author BaseX Team 2005-11, BSD License
 * @author Christian Gruen
 */
public final class ByteList extends ElementList {
  /** Element container. */
  protected byte[] list;

  /**
   * Default constructor.
   */
  public ByteList() {
    this(CAP);
  }

  /**
   * Constructor, specifying an initial array capacity.
   * @param c array capacity
   */
  public ByteList(final int c) {
    list = new byte[c];
  }

  /**
   * Adds an entry to the array.
   * @param e entry to be added
   * @return self reference
   */
  public ByteList add(final int e) {
    if(size == list.length) list = Arrays.copyOf(list, newSize());
    list[size++] = (byte) e;
    return this;
  }

  /**
   * Adds a byte array to the token.
   * @param b the character array to be added
   * @return self reference
   */
  public ByteList add(final byte[] b) {
    final int l = b.length;
    final int ll = list.length;
    if(size + l > ll) list = Arrays.copyOf(list, newSize(size + l));
    System.arraycopy(b, 0, list, size, l);
    size += l;
    return this;
  }

  /**
   * Returns an array with all elements.
   * @return array
   */
  public byte[] toArray() {
    return Arrays.copyOf(list, size);
  }

  @Override
  public String toString() {
    return string(list, 0, size);
  }
}

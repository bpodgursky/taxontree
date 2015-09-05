package com.bpodgursky.taxtree;

import java.util.Iterator;

import com.google.common.base.Preconditions;

//  TODO oss lr one
public class Accessors {
  public static <T> T only(Iterable<T> c ){
    Preconditions.checkNotNull(c, "Null iterable");

    Iterator<T> iterator = c.iterator();
    Preconditions.checkArgument(iterator.hasNext(), "Empty iterable");

    T val = iterator.next();

    if (iterator.hasNext()) {
      throw new RuntimeException(
          String.format(
              "Iterable has more than one element. First element: %s, Second element: %s, Has third element: %s",
              val,
              iterator.next(),
              iterator.hasNext()
          )
      );
    }

    return val;
  }

}

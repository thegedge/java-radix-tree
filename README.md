# Radix Tree

An implementation of the [Radix Tree](en.wikipedia.org/wiki/Radix_tree) data
structure in Java. A radix tree maps strings to values, allowing efficient
string lookup and prefix queries. This implementation mostly implements the
`java.util.Map` interface, minus the following:

* `keySet`, `values`, and `entrySet` methods currently return new sets rather
  than views.

## License

This project is licensed under the MIT license.

## Background

I implemented this structure while working on the [Phon project](http://phon.ling.mun.ca/)
in early 2012. The work is inspired by [Tahseen's implementation](http://code.google.com/p/radixtree/).


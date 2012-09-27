/*
 * Copyright (C) 2012 Jason Gedge <www.gedge.ca>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 *     The above copyright notice and this permission notice shall be included
 *     in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package ca.gedge.radixtree;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ca.gedge.radixtree.RadixTree;
import ca.gedge.radixtree.RadixTreeUtil;

/**
 * JUnit test cases for ca.gedge.radixtree.RadixTree
 */
public class TestRadixTree {
	/**
	 * A variation of <code>assertEquals</code> that sorts its lists before comparing them.
	 * 
	 * @param a  one of the lists
	 * @param b  one of the lists
	 */
	public static <T extends Comparable<? super T>> void assertEqualsWithSort(List<T> a, List<T> b) {
		Collections.sort(a);
		Collections.sort(b);
		assertEquals(a, b);
	}
	
	/**
	 * A variation of <code>assertEquals</code> that sorts its arrays before comparing them.
	 * 
	 * @param a  one of the arrays
	 * @param b  one of the arrays
	 */
	public static <T extends Comparable<? super T>> void assertEqualsWithSort(T[] a, T[] b) {
		Arrays.sort(a);
		Arrays.sort(b);
		assertArrayEquals(a, b);
	}
	
	@Test
	public void testLargestPrefix() {
		assertEquals(5, RadixTreeUtil.largestPrefixLength("abcdefg", "abcdexyz"));
		assertEquals(3, RadixTreeUtil.largestPrefixLength("abcdefg", "abcxyz"));
		assertEquals(3, RadixTreeUtil.largestPrefixLength("abcdefg", "abctuvxyz"));
		assertEquals(0, RadixTreeUtil.largestPrefixLength("abcdefg", ""));
		assertEquals(0, RadixTreeUtil.largestPrefixLength("", "abcxyz"));
		assertEquals(0, RadixTreeUtil.largestPrefixLength("xyz", "abcxyz"));
	}
	
	@Test
	public void testEmptyTree() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		assertEquals(0, tree.size());
	}

	@Test
	public void testSingleInsertion() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("test", 1);
		assertEquals(1, tree.size());
		assertTrue(tree.containsKey("test"));
	}
	
	@Test
	public void testMultipleInsertions() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("test", 1);
		tree.put("tent", 2);
		tree.put("tank", 3);
		tree.put("rest", 4);
		assertEquals(4, tree.size());
		assertEquals(1, tree.get("test").intValue());
		assertEquals(2, tree.get("tent").intValue());
		assertEquals(3, tree.get("tank").intValue());
		assertEquals(4, tree.get("rest").intValue());
	}
	
	@Test
	public void testMultipleInsertionOfTheSameKey() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("test", 1);
		tree.put("tent", 2);
		tree.put("tank", 3);
		tree.put("rest", 4);
		assertEquals(4, tree.size());
		assertEquals(1, tree.get("test").intValue());
		assertEquals(2, tree.get("tent").intValue());
		assertEquals(3, tree.get("tank").intValue());
		assertEquals(4, tree.get("rest").intValue());
		
		tree.put("test", 9);
		assertEquals(4, tree.size());
		assertEquals(9, tree.get("test").intValue());
		assertEquals(2, tree.get("tent").intValue());
		assertEquals(3, tree.get("tank").intValue());
		assertEquals(4, tree.get("rest").intValue());
	}
	
	@Test
	public void testPrefixFetch() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("test", 1);
		tree.put("tent", 2);
		tree.put("rest", 3);
		tree.put("tank", 4);
		
		assertEquals(4, tree.size());
		assertEqualsWithSort(tree.getValuesWithPrefix(""), new ArrayList<Integer>(tree.values()));
		assertEqualsWithSort(tree.getValuesWithPrefix("t").toArray(new Integer[0]), new Integer[]{1, 2, 4});
		assertEqualsWithSort(tree.getValuesWithPrefix("te").toArray(new Integer[0]), new Integer[]{1, 2});
		assertEqualsWithSort(tree.getValuesWithPrefix("asd").toArray(new Integer[0]), new Integer[0]);
	}
	
	@Test
	public void testSpook() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("pook", 1);
		tree.put("spook", 2);
		
		assertEquals(2, tree.size());
		assertEqualsWithSort(tree.keySet().toArray(new String[0]), new String[] {
			"pook",
			"spook"
		});
	}
	
	@Test
	public void testRemoval() {
		RadixTree<Integer> tree = new RadixTree<Integer>();
		tree.put("test", 1);
		tree.put("tent", 2);
		tree.put("tank", 3);
		assertEquals(3, tree.size());
		assertTrue(tree.containsKey("tent"));
		
		tree.remove("key");
		assertEquals(3, tree.size());
		assertTrue(tree.containsKey("tent"));
		
		tree.remove("tent");
		assertEquals(2, tree.size());
		assertEquals(1, tree.get("test").intValue());
		assertFalse(tree.containsKey("tent"));
		assertEquals(3, tree.get("tank").intValue());
	}
	
	private SecureRandom rng = new SecureRandom();
	
	@Test
	public void testManyInsertions() {
		RadixTree<BigInteger> tree = new RadixTree<BigInteger>();
		
		int n = rng.nextInt(401) + 100; // n in [100, 500]
		
		List<BigInteger> strings = generateRandomStrings(n);
		for(BigInteger x : strings)
			tree.put(x.toString(32), x);
		
		assertEquals(strings.size(), tree.size());
		for(BigInteger x : strings)
			assertTrue(tree.containsKey(x.toString(32)));
		
		assertEqualsWithSort(strings, new ArrayList<BigInteger>(tree.values()));
	}
	
	private List<BigInteger> generateRandomStrings(int n) {
		List<BigInteger> strings = new ArrayList<BigInteger>();
		while(n-- > 0) {
			final BigInteger bigint = new BigInteger(20, rng);
			if(!strings.contains(bigint))
				strings.add(bigint);
		}
		return strings;
	}
	
	@Test
	public void testRealData() throws IOException {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("data/dict.dat"), "UTF-8"));
			final RadixTree<String> tree = new RadixTree<String>();
			
			// Read data into a list first, because we want to see how long it
			// takes to construct the radix tree but don't want to include
			// I/O reads in that time
			int numLines = 0;
			String line = null;
			ArrayList<String[]> lines = new ArrayList<String[]>();
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length() > 0) {
					String pieces[] = line.split("\\s+", 2);
					if(pieces.length == 2)
						lines.add(pieces);
				}
			}
			
			// Construct tree and time the construction
			for(String [] pieces : lines) {
				// Just in case the DAT files has multiple of the same key
				if(tree.put(pieces[0], pieces[1]) == null)
					++numLines;
			}
			
			// Perform tests
			assertEquals(numLines, tree.size());
			
			assertEqualsWithSort(tree.getValuesWithPrefix("ACCI").toArray(new String[0]), new String[] {
				"a269c0caf5ed6edaaf0dfe6af2756256", // ACCIDENT
				"cd465f695982b126e418243260f60f9b", // ACCIDENTAL
				"e7fdb552e28b0fa234e7f3363d2577ed", // ACCIDENTALLY
				"9300fe6261f9198d86879c0925ca652c", // ACCIDENTLY
				"6524df69cc016e5d9a3cd03cb8a58815", // ACCIDENTS
				"edad4ecb40b0d95df72d3e587692f601", // ACCION
				"7868257ae2c6e06f8ccc6841cdc64ff1", // ACCIVAL
			});
			
			assertEqualsWithSort(tree.getValuesWithPrefix("OSTEN").toArray(new String[0]), new String[] {
				"1eef84bdf3b7307e72b99c0ed0327165", // OSTEN 
				"5011b15cd7e790336d23b70a48f7bed7", // OSTENDORF 
				"4ed1b43b743a0dee269d4184880bbffd", // OSTENSIBLE 
				"d318060019c4f987986e383cf700e7f9", // OSTENSIBLY 
				"d32a6e75341b4c1797485622bb388c8a", // OSTENSON 
				"4df4a85a0a9a345a03493c484090002d", // OSTENTATION 
				"4849c1ab49637b63ba1570b5abf33d1c", // OSTENTATIOUS 
				"d65f9261a233810f51c73cd38ab1398e", // OSTENTATIOUSLY 
			});
			
			assertEqualsWithSort(tree.getKeysWithPrefix("OSTEN").toArray(new String[0]), new String[] {
				"OSTEN",
				"OSTENDORF",
				"OSTENSIBLE",
				"OSTENSIBLY",
				"OSTENSON",
				"OSTENTATION",
				"OSTENTATIOUS",
				"OSTENTATIOUSLY"
			});
		} finally {
			if(br != null)
				br.close();
		}
	}
}

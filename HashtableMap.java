// == CS400 Fall 2024 File Header Information ==
// Name: Atif Chowdhury
// Email: achowdhury22@wisc.edu
// Group: P2.3925
// Lecturer: Florian Heimerl
// Notes to Grader: N/A

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

  /**
   * Inner class to store pairs of key-value pairs
   */
  protected class Pair {

    public KeyType key;
    public ValueType value;

    /**
     * Constructs a new pair with the specified key and value.
     * 
     * @param key   the key of the pair
     * @param value the value associated with the key
     */
    public Pair(KeyType key, ValueType value) {
      this.key = key;
      this.value = value;
    }

  }

  // instance fields
  protected LinkedList<Pair>[] table = null; // hashtable with separate chainin
  private int size; // stores number of elements in the hashtable
  private static final int DEFAULT_CAPACITY = 64;
  private static final double LOAD_FACTOR_THRESHOLD = 0.8;

  /**
   * Constructs a new hashtable with the specified initial capacity.
   * 
   * @param capacity the initial capacity of the hashtable
   * @throws IllegalArgumentException if the capacity is less than or equal to 0
   */
  @SuppressWarnings("unchecked")
  public HashtableMap(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0.");
    }
    this.table = (LinkedList<Pair>[]) new LinkedList[capacity];
    for (int i = 0; i < capacity; i++) {
      table[i] = new LinkedList<>();
    }
    this.size = 0;
  }

  /**
   * Constructs a new hashtable with the default capacity.
   */
  public HashtableMap() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Adds a new key,value pair/mapping to this collection.
   * 
   * @param key   the key of the key,value pair
   * @param value the value that key maps to
   * @throws IllegalArgumentException if key already maps to a value
   * @throws NullPointerException     if key is null
   */
  @Override
  public void put(KeyType key, ValueType value) throws IllegalArgumentException {
    // check for NULL key
    if (key == null) {
      throw new NullPointerException("Adding NULL key is not allowed.");
    }

    // check for existing key
    if (containsKey(key)) {
      throw new IllegalArgumentException("Adding duplicate KEY is not allowed.");
    }

    // adding new KEY-VALUE pair
    table[getHashIndex(key)].add(new Pair(key, value));
    size++;

    // check load factor
    if (getLoadFactor() >= LOAD_FACTOR_THRESHOLD) {
      rehash();
    }
  }

  /**
   * Checks whether a key maps to a value in this collection.
   * 
   * @param key the key to check
   * @return true if the key maps to a value, and false is the key doesn't map to a value
   */
  @Override
  public boolean containsKey(KeyType key) {
    // check for NULL key
    if (key == null) {
      return false;
    }
    // check for key
    for (Pair pair : table[getHashIndex(key)]) {
      if (pair.key.equals(key)) {
        return true;
      }
    }

    return false; // not found
  }

  /**
   * Retrieves the specific value that a key maps to.
   * 
   * @param key the key to look up
   * @return the value that key maps to
   * @throws NoSuchElementException when key is not stored in this collection
   */
  @Override
  public ValueType get(KeyType key) throws NoSuchElementException {
    // check for NULL key
    if (key == null) {
      throw new NullPointerException("NULL key is not allowed.");
    }

    // check for non-existing key
    if (!containsKey(key)) {
      throw new NoSuchElementException("No such KEY found.");
    }
    // find the key and return
    for (Pair pair : table[getHashIndex(key)]) {
      if (pair.key.equals(key)) {
        return pair.value;
      }
    }

    return null;
  }

  /**
   * Remove the mapping for a key from this collection.
   * 
   * @param key the key whose mapping to remove
   * @return the value that the removed key mapped to
   * @throws NoSuchElementException when key is not stored in this collection
   */
  @Override
  public ValueType remove(KeyType key) throws NoSuchElementException {
    // check for NULL key
    if (key == null) {
      throw new NullPointerException("NULL key is not allowed.");
    }

    // check for non-existing key
    if (!containsKey(key)) {
      throw new NoSuchElementException("No such KEY was found to remove.");
    }

    // find the key and remove it
    for (Pair pair : table[getHashIndex(key)]) {
      if (pair.key.equals(key)) {
        table[getHashIndex(key)].remove(pair);
        size--;
        return pair.value;
      }
    }
    return null;
  }

  /**
   * Removes all key,value pairs from this collection.
   */
  @Override
  public void clear() {
    for (int i = 0; i < getCapacity(); i++) {
      table[i].clear();
    }
    this.size = 0;
  }

  /**
   * Retrieves the number of keys stored in this collection.
   * 
   * @return the number of keys stored in this collection
   */
  @Override
  public int getSize() {
    return this.size;
  }

  /**
   * Retrieves this collection's capacity.
   * 
   * @return the size of te underlying array for this collection
   */
  @Override
  public int getCapacity() {
    return table.length;
  }

  /**
   * Retrieves this collection's keys.
   * 
   * @return a list of keys in the underlying array for this collection
   */
  @Override
  public List<KeyType> getKeys() {
    List<KeyType> keys = new ArrayList<>();

    for (int i = 0; i < getCapacity(); i++) {
      for (Pair pair : table[i]) {
        keys.add(pair.key);
      }
    }
    return keys;
  }

  /**
   * Helper method to calculate the load factor of the hashtable.
   * 
   * @return the current load factor
   */
  private double getLoadFactor() {
    return (double) getSize() / getCapacity();
  }

  /**
   * Helper method to compute the hash index for a given key.
   * 
   * @param key the key whose hash index is to be calculated
   * @return the computed hash index
   */
  private int getHashIndex(KeyType key) {
    return Math.abs(key.hashCode()) % getCapacity();
  }

  /**
   * Helper method that rehashes the hashtable by doubling its capacity and reinserting all existing
   * key-value pairs.
   */
  @SuppressWarnings("unchecked")
  private void rehash() {
    int newCapacity = getCapacity() * 2;
    LinkedList<Pair>[] oldTable = table;

    // initialize table with increased capacity
    this.table = (LinkedList<Pair>[]) new LinkedList[newCapacity];
    for (int i = 0; i < newCapacity; i++) {
      table[i] = new LinkedList<>();
    }
    size = 0; // initial size

    // adding all the previous key-value pairs
    for (int i = 0; i < oldTable.length; i++) {
      for (Pair pair : oldTable[i]) {
        put(pair.key, pair.value);
      }
    }
  }

  ////////////////////////// JUNIT TESTS ///////////////////////////////////

  /**
   * HashtableMapTest01: Validates the functionality of the put() and containsKey() methods for both
   * valid and invalid inputs.
   *
   * Expected: For valid inputs, put() should successfully add key-value pairs, and containsKey()
   * should return true if the key exists. For invalid inputs, put() should throw the appropriate
   * exception without adding the key-value pair, and containsKey() should return false.
   */
  @Test
  void HashtableMapTest01() {
    boolean exceptionThrown = false; // exception checker

    // create Hashtable for tests
    HashtableMap<String, Integer> testMap = new HashtableMap<>();

    // Text 01: calling put() and containsKey() with VALID input
    try {
      // adding keys
      testMap.put("key1", 10);
      testMap.put("key2", 20);
      testMap.put("key3", 30);
      testMap.put("key4", 40);
      testMap.put("key5", 50);

      // check for expected keys
      Assertions.assertTrue(
          testMap.containsKey("key1") && testMap.containsKey("key2") && testMap.containsKey("key3")
              && testMap.containsKey("key4") && testMap.containsKey("key5")
              && (testMap.getSize() == 5),
          "HashtableMapTest01() [1.1]: FAILED containsKey() did not return the expected output for VALID input.");

    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }

    // check for unexpected exception
    Assertions.assertFalse(exceptionThrown,
        "HashtableMapTest01() [1.2]: FAILED put() throws an UNEXPECTED exception for VALID input.");

    // Test 02: calling put() and containsKey() with INVALID input

    // check for NULL key [ put() ]
    Assertions.assertThrows(NullPointerException.class, () -> testMap.put(null, 10),
        "HashtableMapTest01() [2.1]: FAILED put() did not throw EXPECTED exception for NULL input.");

    // check for DUPLICATE key [ put() ]
    Assertions.assertThrows(IllegalArgumentException.class, () -> testMap.put("key1", 10),
        "HashtableMapTest01() [2.2]: FAILED put() did not throw EXPECTED exception for DUPLICATE input.");

    // check for NULL key [ containsKey() ]
    Assertions.assertFalse(testMap.containsKey(null),
        "HashtableMapTest01() [2.3]: FAILED containsKey() did not return the expected output for NULL input.");

    // check for keys that do not exist in the hashtable [ containsKey() ]
    Assertions.assertFalse(testMap.containsKey("key7"),
        "HashtableMapTest01() [2.4]: FAILED containsKey() did not return the expected output for keys that do not exist in the collection.");
  }

  /**
   * HashtableMapTest02: Tests the functionality of the get() method with valid and invalid inputs.
   *
   * Expected: For valid inputs, get() should return the correct value associated with the key. For
   * invalid inputs, such as null or nonexistent keys, get() should throw the appropriate exception.
   */
  @Test
  void HashtableMapTest02() {
    boolean exceptionThrown = false; // exception checker

    // create Hashtable for tests
    HashtableMap<String, Integer> testMap = new HashtableMap<>();

    // adding keys
    testMap.put("key1", 10);
    testMap.put("key2", 20);
    testMap.put("key3", 30);
    testMap.put("key4", 40);
    testMap.put("key5", 50);

    // Test 01: calling get() with VALID input
    try {
      // check for expected keys
      Assertions.assertTrue(
          (testMap.get("key1").equals(10)) && (testMap.get("key2").equals(20))
              && (testMap.get("key3").equals(30)) && (testMap.get("key4").equals(40))
              && (testMap.get("key5").equals(50)),
          "HashtableMapTest02() [1.1]: FAILED get() did not return the expected key.");

    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }

    // check for unexpected exception
    Assertions.assertFalse(exceptionThrown,
        "HashtableMapTest02() [1.2]: FAILED get() throws an UNEXPECTED exception for VALID input.");

    // Test 02: calling get() with INVALID inputs

    // check for NULL key
    Assertions.assertThrows(NullPointerException.class, () -> testMap.get(null),
        "HashtableMapTest02() [2.1]: FAILED get() did not throw EXPECTED exception for NULL input.");

    // check for non-existing keys
    Assertions.assertThrows(NoSuchElementException.class, () -> testMap.get("key8"),
        "HashtableMapTest02() [2.1]: FAILED get() did not throw EXPECTED exception for non-existing keys.");

  }

  /**
   * HashtableMapTest03: Validates the remove() method for both valid and invalid inputs.
   *
   * Expected: For valid inputs, remove() should delete the key-value pair from the hashtable and
   * return the value of the removed key. -For invalid inputs, such as null or nonexistent keys,
   * remove() should throw the appropriate exception without modifying the hashtable.
   */
  @Test
  void HashtableMapTest03() {
    boolean exceptionThrown = false; // exception checker

    // create Hashtable for tests
    HashtableMap<String, Integer> testMap = new HashtableMap<>();

    // adding keys
    testMap.put("key1", 10);
    testMap.put("key2", 20);
    testMap.put("key3", 30);
    testMap.put("key4", 40);
    testMap.put("key5", 50);

    // Test 01: calling remove() with VALID inputs
    try {
      int numElementBefore = testMap.getSize(); // store size before removing

      Assertions.assertTrue(
          (testMap.remove("key1").equals(10))
              && (testMap.remove("key2").equals(20)) && (testMap.remove("key3").equals(30))
              && (testMap.remove("key4").equals(40)) && (testMap.remove("key5").equals(50)),
          "HashtableMapTest03() [1.1]: FAILED remove() did not return the expected value after removing the key.");

      int numElementAfter = testMap.getSize(); // store size after removing

      // check size and capacity (should remain unchanged)
      Assertions.assertTrue(
          (numElementBefore == 5) && (numElementAfter == 0) && (testMap.getCapacity() == 64),
          "HashtableMapTest03() [1.2]: FAILED remove() did not remove expected number of keys.");

    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }

    // check for unexpected exception
    Assertions.assertFalse(exceptionThrown,
        "HashtableMapTest03() [1.3]: FAILED remove() throws an UNEXPECTED exception for VALID input.");

    // Test 02: calling remove() with INVALID input

    // check for non-existing keys
    Assertions.assertThrows(NoSuchElementException.class, () -> testMap.remove("key8"),
        "HashtableMapTest03() [2.1]: FAILED remove() did not throw EXPECTED exception for non-existing keys.");

    // check for NULL keys
    Assertions.assertThrows(NullPointerException.class, () -> testMap.remove(null),
        "HashtableMapTest03() [2.2]: FAILED remove() did not throw EXPECTED exception for NULL keys.");
  }

  /**
   * HashtableMapTest04: Verifies the functionality of the clear() method for both empty and
   * non-empty hashtables.
   *
   * Expected: Calling clear() on an empty or non-empty hashtable should remove all elements without
   * throwing any exceptions. The size should reset to zero, and the hashtable's capacity should
   * remain unchanged.
   */
  @Test
  void HashtableMapTest04() {
    boolean exceptionThrown = false; // exception checker
    // size checker
    int numElementBefore = 0;
    int numElementAfter = 0;

    // create Hashtable for tests
    HashtableMap<String, Integer> testMap = new HashtableMap<>();

    // Test 01: calling clear() on an EMPTY hashtable
    try {
      numElementBefore = testMap.getSize(); // store size before clearing

      // method call
      testMap.clear();

      numElementAfter = testMap.getSize();

      // check size and capacity (should remain unchanged)
      Assertions.assertTrue(
          (numElementAfter == 0) && (numElementBefore == 0) && (testMap.getCapacity() == 64),
          "HashtableMapTest04() [1.1]: FAILED clear() did not return the expected ouput for EMPTY hashtable.");

    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }

    // check for unexpected exception
    Assertions.assertFalse(exceptionThrown,
        "HashtableMapTest04() [1.2]: FAILED clear() throws an UNEXPECTED exception for EMPTY hashtable.");

    // reinitializing the exception checker
    exceptionThrown = false;

    // adding keys
    testMap.put("key1", 10);
    testMap.put("key2", 20);
    testMap.put("key3", 30);
    testMap.put("key4", 40);
    testMap.put("key5", 50);

    // Test 02: calling clear() on an NON-EMPTY hashtable
    try {
      numElementBefore = testMap.getSize(); // store size before clearing

      // method call
      testMap.clear();

      numElementAfter = testMap.getSize();

      // check size and capacity (should remain unchanged)
      Assertions.assertTrue(
          (numElementAfter == 0) && (numElementBefore == 5) && (testMap.getCapacity() == 64),
          "HashtableMapTest04() [2.1]: FAILED clear() did not return the expected ouput for NON-EMPTY hashtable.");

      // check for remaining elements
      Assertions.assertFalse(
          testMap.containsKey("key1") || testMap.containsKey("key2") || testMap.containsKey("key3")
              || testMap.containsKey("key4") || testMap.containsKey("key5"),
          "HashtableMapTest04() [2.2]: FAILED clear() did not remove all the elements for NON-EMPTY hashtable.");

    } catch (Exception e) {
      exceptionThrown = true; // unexpected exception
    }

    // check for unexpected exception
    Assertions.assertFalse(exceptionThrown,
        "HashtableMapTest04() [2.3]: FAILED clear() throws an UNEXPECTED exception for NON-EMPTY hashtable.");
  }

  /**
   * HashtableMapTest05: Test for rehashing functionality
   * 
   * Expected: The hashtable should correctly rehash when the load factor threshold is exceeded. All
   * key-value pairs should remain accessible and correctly mapped after rehashing. Multiple
   * rehashes should not affect data integrity.
   */
  @Test
  void HashtableMapTest05() {
    // capacity checker
    int capacityBefore = 0;
    int capacityAfter = 0;

    // create Hashtable for tests
    HashtableMap<Integer, String> testMap1 = new HashtableMap<>(2);

    // Test 01: test rehashing with inserting 5 key-value pairs
    capacityBefore = testMap1.getCapacity();

    // adding keys
    testMap1.put(1, "key1");
    testMap1.put(2, "key2"); // results in rehashing

    capacityAfter = testMap1.getCapacity();

    // check capacity and size
    Assertions.assertTrue(
        (capacityBefore == 2) && (capacityAfter == 4) && (testMap1.getSize() == 2),
        "HashtableMapTest05() [1.1]: FAILED did not REHASH as expected.");

    capacityBefore = testMap1.getCapacity();

    // adding keys
    testMap1.put(3, "key3");
    testMap1.put(4, "key4"); // results in rehasing
    testMap1.put(5, "key5");

    capacityAfter = testMap1.getCapacity();

    // check capacity and size
    Assertions.assertTrue(
        (capacityBefore == 4) && (capacityAfter == 8) && (testMap1.getSize() == 5),
        "HashtableMapTest05() [1.2]: FAILED did not REHASH as expected.");

    // Test 02: test rehashing with inserting multiple key-value pairs

    // create Hashtable for tests
    HashtableMap<Integer, String> testMap2 = new HashtableMap<>(2);

    capacityBefore = testMap2.getCapacity();

    // adding 100 key-value pairs
    for (int i = 1; i < 101; i++) {
      testMap2.put(i, "key" + i);
    }

    capacityAfter = testMap2.getCapacity();

    // check capacity and size
    Assertions.assertTrue(
        (capacityBefore == 2) && (capacityAfter == 128) && (testMap2.getSize() == 100),
        "HashtableMapTest05() [2.1]: FAILED did not REHASH as expected.");
  }
}

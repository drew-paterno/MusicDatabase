
import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 * This class implements the MapADT class. It represents the functionality of a generic hash table.
 * 
 * @author Drew Paterno
 *
 * @param <KeyType>   Generic reference to the variable which acts as the key in the hash function.
 * @param <ValueType> Generic reference to the data value which is stores at its respective key
 *                    pair.
 */
public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

  private Object[] hashTable; // Array containing the hash table.
  private int size; // Integer variable containing the number of elements in the hashTable array.
  
  /**
   * This class is used to consolidate key-data pairs into a single node of data, to be added to a
   * hash table.
   *
   * @param <KeyType>   Generic reference to the variable which acts as the key in the hash function.
   * @param <ValueType> Generic reference to the data value which is stores at its respective key
   *                    pair.
   */
  protected static class HashNode<KeyType, ValueType> {

    private KeyType key;
    private ValueType value;

    /**
     * Initializes instance fields based on user data input.
     * 
     * @param key   User inputed hash key.
     * @param value User inputed value associated with the provided key.
     */
    public HashNode(KeyType key, ValueType value) {
      this.key = key;
      this.value = value;
    }

    /**
     * Retrieves value associated with this HashNode.
     * 
     * @return Value of HashNode.
     */
    public ValueType getValue() {
      return value;
    }

    /**
     * Retrieves key associated with this HashNode.
     * 
     * @return Key of HashNode.
     */
    public KeyType getKey() {
      return key;
    }

  }

  /**
   * Default constructor. Initializes instance fields.
   */
  public HashTableMap() {
    this(10);
  }

  /**
   * Initializes all instance fields, initial capacity of hashTable is specified by the user.
   * 
   * @param capacity Initial capacity of hashTable, as specified as the user.
   */
  public HashTableMap(int capacity) {
    hashTable = new Object[capacity];
    size = 0;
  }


  /**
   * Adds a key-value pair to the hash table. If a collision occurs, chaining is used to make sure
   * multiple elements can be stored at a single index.
   * 
   * @param key   The key which is an input for the hashCode method.
   * @param value The data to be stored at a specific hashIndex (which is the function of the key in
   *              the hashCode method).
   * @return True if the key-value pair is added to the hashTable, false if the key already exists
   *         within the hash table.
   */
  @Override
  public boolean put(KeyType key, ValueType value) {
    // Use's Java's built in hashCode method to find the hashIndex for the given key.
    int hashIndex = Math.abs(key.hashCode()) % hashTable.length;
    if (hashTable[hashIndex] == null) { // Checks if spot in the array is unused.
      hashTable[hashIndex] = new LinkedList<HashNode<KeyType, ValueType>>(); // Creates an ArrayList
                                                                             // at the calculated
                                                                             // hashIndex.
      ((LinkedList<HashNode<KeyType, ValueType>>) hashTable[hashIndex])
          .add(new HashNode<KeyType, ValueType>(key, value)); // Adds a new HashNode to the
                                                              // ArrayList at the hashIndex.
    } else {
      LinkedList<HashNode<KeyType, ValueType>> x =
          ((LinkedList<HashNode<KeyType, ValueType>>) hashTable[hashIndex]);
      for (int i = 0; i < x.size(); i++) { // Ensures the key does not exist at the current
                                           // hashIndex.
        if (x.get(i).getKey().equals(key))
          return false;
      }
      x.add(new HashNode<KeyType, ValueType>(key, value)); // Adds a HashNode to an already existent
                                                           // LinkedList at the calculated
                                                           // hashIndex.
    }
    size++;
    if ((size * 1.0) / (hashTable.length) >= .8)
      reHash(); // If the load factor is greater than or equal to 0.8, the hashTable will be doubled
                // in size and existing data will be rehashed.
    return true;
  }

  /**
   * Creates a new hashTable array twice the capacity of its predecessor. Then propagates through
   * all elements in the original hashTable and rehashes them into the new hashTable.
   */
  private void reHash() {
    Object[] temp = hashTable;
    hashTable = new Object[hashTable.length * 2]; // Creates new hashTable with twice the capacity
                                                  // of its predecessor.
    size = 0;
    for (Object element : temp) { // Propagates through all elements in existing hashTable.
      if (element == null) // Skips unused hash indices.
        continue;
      else if (((LinkedList<HashNode<KeyType, ValueType>>) element).size() == 0) {
        continue; // Skips used indices with empty LinkedLists.
      }
      else {
        LinkedList<HashNode<KeyType, ValueType>> x =
            ((LinkedList<HashNode<KeyType, ValueType>>) element);
        for (int i = 0; i < x.size(); i++) {
          put(x.get(i).getKey(), x.get(i).getValue()); // Rehashes all elements in LinkedLists where
                                                       // the size is not 0.
        }
      }
    }
  }

  /**
   * Retrieves the data associated with a certain key.
   * 
   * @param key Key used to find hashIndex.
   * @return Data at the key's hashIndex.
   * @throws NoSuchElementException When the provided key does not exist within the hashTable.
   */
  @Override
  public ValueType get(KeyType key) throws NoSuchElementException {
    // Use's Java's built in hashCode method to find the hashIndex for the given key.
    int hashIndex = Math.abs(key.hashCode()) % hashTable.length;
    if (hashTable[hashIndex] == null)
      throw new NoSuchElementException("The given key does not exist within the Hash Table.");
    // Throws exception if there is nothing at calculated hashIndex.
    else {
      LinkedList<HashNode<KeyType, ValueType>> x =
          ((LinkedList<HashNode<KeyType, ValueType>>) hashTable[hashIndex]);
      for (int i = 0; i < x.size(); i++) // Propagates through LinkedList at hashIndex.
        if (x.get(i).getKey().equals(key))
          return x.get(i).getValue(); // Returns data associated with the provided key.
      throw new NoSuchElementException("The given key does not exist within the Hash Table.");
      // Otherwise, throws exception.
    }
  }

  /**
   * Returns the current size of the hashTable.
   * 
   * @return Size of hashTable.
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * Returns a boolean value based on if the hashTable contains a provided key.
   * 
   * @param key User defined key to check.
   * @return True if the hashTable contains the key, false otherwise.
   */
  @Override
  public boolean containsKey(KeyType key) {
    // Use's Java's built in hashCode method to find the hashIndex for the given key.
    int hashIndex = Math.abs(key.hashCode()) % hashTable.length;
    if (hashTable[hashIndex] == null) // Checks if hashIndex is unused.
      return false;
    else {
      LinkedList<HashNode<KeyType, ValueType>> x =
          ((LinkedList<HashNode<KeyType, ValueType>>) hashTable[hashIndex]);
      for (int i = 0; i < x.size(); i++) // Propagates through LinkedList at hashIndex.
        if (x.get(i).getKey().equals(key))
          return true;
      return false; // Reached end of propagation with no success.
    }
  }

  /**
   * Removes a key-value pair which contains a given key.
   * 
   * @param key User defined key to check.
   * @return Data associated with key-value pair, null if key does not exist within the hashTable.
   */
  @Override
  public ValueType remove(KeyType key) {
    // Use's Java's built in hashCode method to find the hashIndex for the given key.
    int hashIndex = Math.abs(key.hashCode()) % hashTable.length;
    ValueType toReturn = null; // Sets default return value to null.
    if (hashTable[hashIndex] == null) // If calculated hashIndex is unused returns null.
      return null;
    else {
      LinkedList<HashNode<KeyType, ValueType>> x =
          ((LinkedList<HashNode<KeyType, ValueType>>) hashTable[hashIndex]);
      for (int i = 0; i < x.size(); i++) // Propagates through LinkedList, searching for key.
        if (x.get(i).getKey().equals(key)) {
          toReturn = x.get(i).getValue(); // Saves value associated with key-value pair.
          x.remove(i); // Removes elements from linked list.
          size--;
          break;
        }
    }
    return toReturn; // Returns null if the given key is not found.
  }

  /**
   * Removes all elements from hashTable.
   */
  @Override
  public void clear() {
    hashTable = new Object[hashTable.length]; // Resets hashTable array, preserving its capacity.
    size = 0; // Resets size to 0.
  }
  
//  @Override
//  public String toString() {
//    String toReturn = "{";
//    for (Object element : hashTable) { // Propagates through all elements in existing hashTable.
//      if (element == null) {}
//        //toReturn += " x | ";
//      else if (((LinkedList<HashNode<KeyType, ValueType>>) element).size() == 0) {}
//        //toReturn += " x | ";
//      else {
//        toReturn += " ( ";
//        LinkedList<HashNode<KeyType, ValueType>> x =
//            ((LinkedList<HashNode<KeyType, ValueType>>) element);
//        for (int i = 0; i < x.size(); i++) {
//          toReturn += (" | " + x.get(i).getKey().toString() + ", " + x.get(i).getValue().toString() + " | ");
//        }
//        toReturn += " ) | ";
//      }
//    }
//    toReturn += "}";
//    return toReturn;
//  }
  
  protected int getCapacity() {
    return hashTable.length;
  }
}

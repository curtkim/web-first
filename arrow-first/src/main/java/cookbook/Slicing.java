package cookbook;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.util.TransferPair;

public class Slicing {
  public static void main(String[] args) {
    try (BufferAllocator allocator = new RootAllocator();
         IntVector vector = new IntVector("intVector", allocator)) {
      for (int i = 0; i < 10; i++) {
        vector.setSafe(i, i);
      }
      vector.setValueCount(10);
      System.out.println(vector);

      TransferPair tp = vector.getTransferPair(allocator);
      tp.splitAndTransfer(0, 5);
      try (IntVector sliced = (IntVector) tp.getTo()) {
        System.out.println(sliced);
      }

      tp = vector.getTransferPair(allocator);
      // copy 6 elements from index 2
      tp.splitAndTransfer(2, 6);
      try (IntVector sliced = (IntVector) tp.getTo()) {
        System.out.print(sliced);
      }
    }
  }
}

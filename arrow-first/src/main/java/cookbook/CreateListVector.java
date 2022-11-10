package cookbook;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.complex.ListVector;
import org.apache.arrow.vector.complex.impl.UnionListWriter;

public class CreateListVector {
  public static void main(String[] args) {
    final int[] data = new int[]{1, 2, 3, 10, 20, 30, 100, 200, 300, 1000, 2000, 3000};

    try (
        BufferAllocator allocator = new RootAllocator();
        ListVector listVector = ListVector.empty("listVector", allocator);
        UnionListWriter listWriter = listVector.getWriter()
    ) {
      int tmp_index = 0;
      for (int i = 0; i < 4; i++) {
        listWriter.setPosition(i);
        listWriter.startList();
        for (int j = 0; j < 3; j++) {
          listWriter.writeInt(data[tmp_index]);
          tmp_index = tmp_index + 1;
        }
        listWriter.setValueCount(3);
        listWriter.endList();
      }
      listVector.setValueCount(4);

      System.out.print(listVector);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

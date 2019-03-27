import java.util.function.*;

import static java.lang.System.out;

public class Currying {

  public static void main(String[] args) {
    doString();

    doInt();
  }

  static void doString() {
    BinaryOperator<String> simpleAdd = (a, b) -> a + " " + b;
    out.println(simpleAdd.apply("4", "5"));

    Function<String, UnaryOperator<String>> curriedAdd = a -> b -> a + " " + b;
    UnaryOperator<String> adder4 = curriedAdd.apply("4");
    out.println(adder4.apply("5"));
  }

  static void doInt() {
    IntBinaryOperator simpleAdd = (a, b) -> a + b;
    out.println(simpleAdd.applyAsInt(4, 5));

    //IntFunction<IntUnaryOperator> curriedAdd = a -> b -> a + b;
    IntFunction<IntUnaryOperator> curriedAdd = (int a) -> (int b) -> a + b;

    IntUnaryOperator adder5 = curriedAdd.apply(5);
    out.println(adder5.applyAsInt(4));
  }
}

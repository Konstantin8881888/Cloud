package com.geekbrains.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main
{
    static int foo(int x, int y)
    {
        return x + y;
    }
    static int sum(int x, int y, Func func)
    {
        return func.apply(x, y);
    }
    public static void main(String[] args)
    {

        Func sum = (a, b) -> {
            return a + b;
        };
        System.out.println(sum.apply(15, 25));

        Func sumRef = Main::foo;
        System.out.println(sumRef.apply(15, 25));

        Calc calc = Main::sum;
        System.out.println(calc.calc(15, 25, sum));

        //action
        Consumer<String> printer = System.out::println;
        printer.accept("Text");
        //Analog action
        Consumer<Integer> consumer = (value) -> System.out.println(value);
        consumer.accept(125);

        //filtering(boolean)
        Predicate<Integer> isOdd = value -> value % 2 == 1;
        System.out.println(isOdd.test(35));

        //transform
        Function<String, Integer> toInt = Integer::parseInt;
        System.out.println(toInt.apply("3454"));

        //getter
        Supplier<List<String>> emptyList = ArrayList::new;
        emptyList.get(); //Создаёт экземпляр пустого списка.
        System.out.println(emptyList.getClass());
        Supplier<Integer> value = () -> 8;//Функция возвращает любое значение.
        System.out.println(value.get());

    }
}

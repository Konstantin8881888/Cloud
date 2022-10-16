package com.geekbrains.stream;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamExamples
{
    public static void main(String[] args)
    {
        IntStream.range(0, 50)//Создаём стрим с диапазоном от 0 до 50
                .boxed()//Переводим из примитива int в ссылочный тип Integer
                .filter(x -> x % 2 == 1)// Оставляем только нечётные
                .forEach(x -> System.out.print(x + " "));//Выводим каждое полученное нечётное
        //То же, но собираем в коллекцию и сохраняем в Лист
        List<Integer> odds = IntStream.range(0, 50).boxed().filter(x -> x % 2 == 1)
                .collect(Collectors.toList());
        System.out.println(odds);
        //То же, но теперь проверяем что все элементы > 7 и сохраняем булем
        boolean allMatch = IntStream.range(0, 50).boxed().filter(x -> x % 2 == 1)
                .allMatch(x -> x > 7);
        System.out.println(allMatch);
        //То же, но находим максимум из чисел и сохраняем в Лист
        IntStream.range(0, 50).boxed().filter(x -> x % 2 == 1)
                .max(Comparator.comparingInt(x -> x)) //Необходим компаратор для сравнения по числу
                .ifPresent(System.out::println);//Проверяем, найден ли. Если найден - выполняет вывод
        //Находим все числа без фильтрации и считаем сумму
        Integer suma = IntStream.rangeClosed(0, 50).boxed().reduce(0, Integer::sum);
        System.out.println(suma);
    }
}

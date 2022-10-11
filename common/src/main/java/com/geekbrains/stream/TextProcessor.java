package com.geekbrains.stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextProcessor
{
    public static void main(String[] args) throws IOException
    {
//        printWordsInLowerCase();
//        System.out.println(getWordsStatistic());
//        for (Pair<String, Integer> stringIntegerPair: getWordsStatisticOrdered())
//        {
//            System.out.println(stringIntegerPair.getKey() + " - " + stringIntegerPair.getValue());
//        }
        System.out.println(splitWordsByLanguage());
        System.out.println(splitByClasses());
    }
    public static void printWordsInLowerCase() throws IOException
    {
        Files.lines(Path.of("server_files", "1.txt"))//Получаем стрим текста из файла
                .flatMap(line -> Stream.of(line.split(" +")))//Разделяем строчки по пробелу и оборачиваем в стрим.(Плюс - это один или более пробелов)
                .map(String::toLowerCase)//Переводим в нижний регистр.
                .map(word -> word.replaceAll("[^A-Za-z+]", ""))//Убираем всё, что не слова(цифры, знаки препин и т.д.)^ - "не" + - любая последовательность.(регулярное выражение)
                .filter(StringUtils::isNotBlank)//Фильтруем по непустым строкам. Без подключения зависимостей использовали .filter(str -> !str.isEmpty())
                .forEach(System.out::println);
    }
    public static Map<String, Integer> getWordsStatistic() throws IOException
    {
        //То же, что и выше, но без Форич и возвращаем результат
        return Files.lines(Path.of("server_files", "1.txt"))
                .flatMap(line -> Stream.of(line.split(" +")))
                .map(String::toLowerCase)
                .map(word -> word.replaceAll("[^A-Za-z+]", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toMap(Function.identity(), value -> 1, Integer::sum))//Function.identity() функция возвращает сама себя то есть(х->x), Integer::sum складывает значение вэлью 1 на каждое встречаемое такое же слово.
                ;

    }

    public static List<Pair<String, Integer>> getWordsStatisticOrdered() throws IOException
    {
        //То же, но получаем статистику сортированную
        return Files.lines(Path.of("server_files", "1.txt"))
                .flatMap(line -> Stream.of(line.split(" +")))
                .map(String::toLowerCase)
                .map(word -> word.replaceAll("[^A-Za-z]+", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toMap(Function.identity(), value -> 1, Integer::sum))
                .entrySet()//получаем множество из энтрей
                .stream()//Берём стрим
                .sorted(Comparator.comparingInt(e -> -e.getValue()))// сортируем по значению value(внимание - минус обозначает, что сортируем в обратную сторону.
                .map(e -> Pair.of(e.getKey(), e.getValue()))//Собираем в пары строка-значение
                .collect(Collectors.toList())//Собираем в список
        ;
    }

    public static Pair<List<String>, List<String>> splitWordsByLanguage() throws IOException
    {
        //Разделяем отдельно русские и английские слова
//        Map<Boolean, List<String>> byLangMap = Files.lines(Path.of("server_files", "2.txt"))
//                .flatMap(line -> Stream.of(line.split(" +")))
//                .collect(Collectors.partitioningBy(word -> word.matches("[A-Za-z]+"), Collectors.toList()));
//        return Pair.of(byLangMap.get(true), byLangMap.get(false));

        Map<String, List<String>> byClassMap = Files.lines(Path.of("server_files", "2.txt"))
                .flatMap(line -> Stream.of(line.split(" +")))
                .map(word -> word.replaceAll("[^A-Za-zА-Яа-я]+", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.groupingBy(word -> {
                    if (word.matches("[A-Za-z]+"))
                    {
                        return "en";
                    }
                    else if (word.matches("[0-9]+"))
                    {
                        return "digit";
                    }
                    else
                    {
                        return "ru";
                    }
                }));
        return Pair.of(byClassMap.get("en"), byClassMap.get("ru"));
    }
    public static Map<String, List<String>> splitByClasses() throws IOException
    {
        return Files.lines(Path.of("server_files", "2.txt"))
                .flatMap(line -> Stream.of(line.split(" +")))
                .map(word -> word.replaceAll("[^A-Za-zА-Яа-я0-9]+", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.groupingBy(word -> {
                    if (word.matches("[A-Za-z]+"))
                    {
                        return "en";
                    }
                    else if (word.matches("[0-9]+"))
                    {
                        return "digit";
                    }
                    else if (word.matches("[А-Яа-я]+"))
                    {
                        return "ru";
                    }
                    else
                    {
                        return "?";
                    }
                }));
    }
}

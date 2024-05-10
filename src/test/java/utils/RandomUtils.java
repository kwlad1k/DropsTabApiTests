package utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static tests.TestDataAPI.currencyIdMap;

public class RandomUtils {

    // Метод для выбора случайного значения из массива, исключая несколько значений
    public static Integer getRandomValueExcluding(List<Integer> values, List<Integer> excludedValues) {
        // Список, в который мы добавляем все значения, кроме исключенных
        List<Integer> filteredValues = new ArrayList<>();

        for (Integer value : values) {
            // Используем метод contains для проверки, содержится ли текущее значение в списке исключений
            if (!excludedValues.contains(value)) {
                filteredValues.add(value);
            }
        }

        // Если отфильтрованный список пуст, нет значения для выбора
        if (filteredValues.isEmpty()) {
            return null; // Возвращаем null или другое значение, чтобы указать, что выбор невозможен
        }

        // Выбираем случайный индекс из отфильтрованного списка
        Random random = new Random();
        int randomIndex = random.nextInt(filteredValues.size());

        // Возвращаем случайное значение из отфильтрованного списка
        return filteredValues.get(randomIndex);
    }

    // Метод для выбора случайного id из списка
    public static Integer getRandomId(List<Integer> ids) {
        // Проверяем, пуст ли список
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Список идентификаторов пуст");
        }

        // Создаем объект Random для генерации случайных чисел
        Random random = new Random();

        // Выбираем случайный индекс из списка
        int index = random.nextInt(ids.size());

        // Возвращаем элемент по случайному индексу
        return ids.get(index);
    }

    public static long getRandomTimestamp(String from) {
        try {
            // Парсинг начальной даты
            Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(from);
            long fromMillis = fromDate.getTime();

            // Текущее время
            long nowMillis = System.currentTimeMillis();

            // Генерация случайного значения времени
            return ThreadLocalRandom.current().nextLong(fromMillis, nowMillis + 1);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Некорректный формат даты. Используйте 'yyyy-MM-dd'.");
        }
    }

    public static int getCurrencyId(String currencySymbol) {
        return currencyIdMap.getOrDefault(currencySymbol, -1); // Возвращает -1, если символ не найден
    }
}

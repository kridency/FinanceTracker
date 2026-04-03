package org.example.util;

import org.example.exception.ApplicationException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Specification<T>(T data) implements Function<Collection<T>, Collection<T>> {
    private static final BiFunction<String, Object, Object> getPath = new BiFunction<>() {
        @Override
        public Object apply(String path, Object obj) {
            var ind = path.indexOf('.');
            try {
                var field = obj.getClass().getDeclaredField(ind == -1 ? path : path.substring(0, ind));
                field.setAccessible(true);
                var fieldObj = field.get(obj);
                return ind == -1 ? fieldObj : getPath.apply(path.substring(ind + 1), fieldObj);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ApplicationException(e.getMessage());
            }
        }
    };

    private Map<String, Optional<?>> getCriteria() {
        return Arrays.stream(data.getClass().getDeclaredFields()).filter(field -> !field.getName().equals("id"))
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        field.setAccessible(true);
                        return Optional.ofNullable(field.get(data));
                    } catch (IllegalAccessException e) {
                        throw new ApplicationException(e.getMessage());
                    }
                }));
    }

    @Override
    public Collection<T> apply(Collection<T> target) {
        var criteria = getCriteria();
        return target.stream().filter(value ->
                criteria.size() == criteria.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty()
                        || Objects.equals(getPath.apply(entry.getKey(), value), entry.getValue().get()))
                .count()
        ).toList();
    }
}

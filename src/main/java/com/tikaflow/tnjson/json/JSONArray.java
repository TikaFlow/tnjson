package com.tikaflow.tnjson.json;

import java.util.*;
import java.util.stream.Collectors;

import com.tikaflow.tnjson.JSON;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.tikaflow.tnjson.bean.Element;
import lombok.val;
import lombok.var;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JSONArray extends JSON implements List<Object> {
    private final ArrayList<Element<Object>> items = new ArrayList<>();

    public JSONArray addOne(Object obj) {
        add(obj);
        return this;
    }

    public Object at(int index) {
        return items.get(index);
    }

    public Class<?> typeAt(int index) {
        return items.get(index).getType();
    }

    public int length() {
        return size();
    }

    public String toString() {
        return items.toString();
    }

    public String format(String indent) {
        var sb = new StringBuilder("[");

        if (!items.isEmpty()) {
            sb.append("\n");
        }

        val itemIndent = indent + "    ";
        for (var item : items) {
            sb.append(itemIndent)
                    .append(item.format(itemIndent))
                    .append(",\n");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("\n")
                .append(indent)
                .append("]");

        return sb.toString();
    }

    public String format() {
        return format("");
    }

    private List<Element<Object>> wrapList(Collection<?> c) {
        return c.stream().map(Element<Object>::new).collect(Collectors.toList());
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.contains(new Element<>(o));
    }

    @Override
    public Iterator<Object> iterator() {
        return items.stream()
                .map(Element::getValue)
                .iterator();
    }

    @Override
    public Object[] toArray() {
        return items.stream()
                .map(Element::getValue)
                .toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return items.stream()
                .map(Element::getValue)
                .collect(Collectors.toList())
                .toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return items.add(new Element<>(o));
    }

    @Override
    public boolean remove(Object o) {
        return items.remove(new Element<>(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return items.containsAll(wrapList(c));
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return items.addAll(wrapList(c));
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return items.addAll(index, wrapList(c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return items.removeAll(wrapList(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return items.retainAll(wrapList(c));
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public Object get(int index) {
        return items.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return items.set(index, new Element<>(element));
    }

    @Override
    public void add(int index, Object element) {
        items.add(index, new Element<>(element));
    }

    @Override
    public Object remove(int index) {
        return items.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return items.indexOf(new Element<>(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return items.lastIndexOf(new Element<>(o));
    }

    @Override
    public ListIterator<Object> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return items.stream()
                .map(Element::getValue)
                .collect(Collectors.toList())
                .listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return items.stream()
                .map(Element::getValue)
                .collect(Collectors.toList())
                .subList(fromIndex, toIndex);
    }
}

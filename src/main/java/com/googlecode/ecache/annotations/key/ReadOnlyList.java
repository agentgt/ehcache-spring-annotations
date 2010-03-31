/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Composite interface that denotes a serializable list that is read only. None of the modification
 * methods will work on this list.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ReadOnlyList<E extends Serializable> implements List<E>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final List<E> readOnlyDelegate;

    public ReadOnlyList(List<E> readOnlyDelegate) {
        this.readOnlyDelegate = Collections.unmodifiableList(readOnlyDelegate);
    }

    public boolean add(E e) {
        return readOnlyDelegate.add(e);
    }

    public void add(int index, E element) {
        readOnlyDelegate.add(index, element);
    }

    public boolean addAll(Collection<? extends E> c) {
        return readOnlyDelegate.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return readOnlyDelegate.addAll(index, c);
    }

    public void clear() {
        readOnlyDelegate.clear();
    }

    public boolean contains(Object o) {
        return readOnlyDelegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return readOnlyDelegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return readOnlyDelegate.equals(o);
    }

    public E get(int index) {
        return readOnlyDelegate.get(index);
    }

    @Override
    public int hashCode() {
        return readOnlyDelegate.hashCode();
    }

    public int indexOf(Object o) {
        return readOnlyDelegate.indexOf(o);
    }

    public boolean isEmpty() {
        return readOnlyDelegate.isEmpty();
    }

    public Iterator<E> iterator() {
        return readOnlyDelegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return readOnlyDelegate.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return readOnlyDelegate.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return readOnlyDelegate.listIterator(index);
    }

    public E remove(int index) {
        return readOnlyDelegate.remove(index);
    }

    public boolean remove(Object o) {
        return readOnlyDelegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return readOnlyDelegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return readOnlyDelegate.retainAll(c);
    }

    public E set(int index, E element) {
        return readOnlyDelegate.set(index, element);
    }

    public int size() {
        return readOnlyDelegate.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return readOnlyDelegate.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return readOnlyDelegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return readOnlyDelegate.toArray(a);
    }

    @Override
    public String toString() {
        return readOnlyDelegate.toString();
    }
}

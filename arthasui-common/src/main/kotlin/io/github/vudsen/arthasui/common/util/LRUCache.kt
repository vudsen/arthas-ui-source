package io.github.vudsen.arthasui.common.util

import java.util.Deque

class LRUCache<E> : Deque<E> {

    private var head: ListNode<E>? = null

    private var tail: ListNode<E>? = null

    private val index: MutableMap<E, ListNode<E>> = mutableMapOf()

    companion object {
        class ListNode<E>(
            var element: E,
            var prev: ListNode<E>? = null,
            var next: ListNode<E>? = null
        )

    }

    private inner class MyMutableIterator(private var current: ListNode<E>?) : MutableIterator<E> {

        override fun hasNext(): Boolean {
            current ?.let {
                return it.next != null
            } ?: return false
        }

        override fun next(): E {
            val r = current!!
            current = r.next
            return r.element
        }

        override fun remove() {
            removeNode(current!!.prev!!)
        }

    }

    private inner class MyDescendingIterator(private var current: ListNode<E>?) : MutableIterator<E> {
        override fun hasNext(): Boolean {
            current ?.let {
                return it.prev != null
            } ?: return false
        }

        override fun next(): E {
            val r = current!!
            current = r.prev
            return r.element
        }

        override fun remove() {
            removeNode(current!!.next!!)
        }

    }

    /**
     * 移除节点，但是不修改 [index]
     */
    private fun removeNode(node: ListNode<E>) {
        if (node == head) {
            pollFirst()
            return
        } else if (node == tail) {
            pollLast()
            return
        }
        node.prev!!.next = node.next!!.prev
    }

    /**
     * 标记当前节点最近被使用过了
     */
    fun refresh(e: E): Boolean {
        val node = index[e] ?: return false
        if (index.size == 1) {
            return true
        }
        removeNode(node)
        node.next = null
        tail ?.let {
            it.next = node
            node.prev = it
            tail = node
            return true
        }
        node.prev = null
        head = node
        tail = node
        return true
    }

    override fun add(element: E): Boolean {
        return offerLast(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        for (element in elements) {
            add(element)
        }
        return true
    }

    override fun clear() {
        head = null
        tail = null
        index.clear()
    }


    override fun iterator(): MutableIterator<E> {
        return MyMutableIterator(head)
    }

    override fun remove(): E {
        return pollFirst()
    }

    override fun isEmpty(): Boolean {
        return head == null
    }

    override fun poll(): E {
        return pollFirst()
    }

    override fun element(): E {
        return peekFirst()
    }

    override fun peek(): E {
        return peekFirst()
    }

    override fun removeFirst(): E {
        return pollLast()
    }

    override fun removeLast(): E {
        return pollLast()
    }

    override fun pollFirst(): E {
        val h = head ?: throw NoSuchElementException()
        val next = h.next
        if (next == null) {
            clear()
            return h.element
        }
        head = next
        next.prev = null
        index.remove(h.element)
        return h.element
    }

    override fun pollLast(): E {
        val t = tail ?: throw NoSuchElementException()
        val prev = t.prev
        if (prev == null) {
            clear()
            return t.element
        }
        tail = prev
        prev.next = null
        index.remove(t.element)
        return t.element
    }

    override fun getFirst(): E {
        return peekFirst()
    }

    override fun getLast(): E {
        return peekLast()
    }

    override fun peekFirst(): E {
        head ?.let {
            return it.element
        } ?: throw NoSuchElementException()
    }

    override fun peekLast(): E {
        tail ?.let {
            return it.element
        } ?: throw NoSuchElementException()
    }

    override fun removeFirstOccurrence(o: Any?): Boolean {
        val iter = iterator()
        while (iter.hasNext()) {
            if (iter.next() == o) {
                iter.remove()
                return true
            }
        }
        return false
    }

    override fun removeLastOccurrence(o: Any?): Boolean {
        val iter = descendingIterator()
        while (iter.hasNext()) {
            if (iter.next() == o) {
                iter.remove()
                return true
            }
        }
        return false
    }

    override fun pop(): E {
        return pollFirst()
    }

    override fun descendingIterator(): MutableIterator<E> {
        return MyDescendingIterator(tail)
    }

    override val size: Int
        get() = index.size

    override fun push(e: E) {
        add(e)
    }

    override fun offerLast(element: E): Boolean {
        if (index.contains(element)) {
            return false
        }
        tail ?.let {
            val node = ListNode(element, it, null)
            it.next = node
            tail = node
            index[element] = node
            return true
        }
        val node = ListNode(element)
        head = node
        tail = node
        index[element] = node
        return true
    }


    override fun offerFirst(element: E): Boolean {
        if (index.contains(element)) {
            return false
        }
        head ?.let {
            val node = ListNode(element, null, it)
            it.prev = node
            head = node
            index[element] = node
            return true
        }
        val node = ListNode(element)
        head = node
        tail = node
        index[element] = node
        return true
    }


    override fun addLast(e: E) {
        offerLast(e)
    }

    override fun addFirst(e: E) {
        offerFirst(e)
    }

    override fun offer(e: E): Boolean {
        return offerLast(e)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        if (elements.size != index.size) {
            return false
        }
        for (element in elements) {
            if (!index.contains(element)) {
                return false
            }
        }
        return true
    }

    override fun contains(element: E): Boolean {
        return index.contains(element)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val toBeKeep = ArrayList<E>()
        for (element in elements) {
            index[element] ?.let {
                toBeKeep.add(it.element)
            }
        }
        clear()
        addAll(toBeKeep)
        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var flag = false
        for (element in elements) {
            index[element] ?.let {
                removeNode(it)
                flag = true
            }
        }
        return flag
    }

    override fun remove(element: E): Boolean {
        val node = index[element] ?: return false
        removeNode(node)
        index.remove(element)
        return true
    }


}
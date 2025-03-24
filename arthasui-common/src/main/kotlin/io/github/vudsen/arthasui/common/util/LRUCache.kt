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
    fun refresh(e: E) {
        val node = index[e] ?: throw NoSuchElementException()
        removeNode(node)
        node.next = null
        tail ?.let {
            it.next = node
            node.prev = it
            tail = node
            return
        }
        node.prev = null
        head = node
        tail = node
    }

    override fun add(element: E): Boolean {
        return offerLast(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        for (element in elements) {
            add(element)
        }
    }

    override fun clear() {
        head = null
        tail = null
        index.clear()
    }

    override fun iterator(): MutableIterator<E> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun removeLastOccurrence(o: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun pop(): E {
        return pollFirst()
    }

    override fun descendingIterator(): MutableIterator<E> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun contains(element: E): Boolean {
        return index.contains(element)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(element: E): Boolean {
        val node = index[element] ?: return false
        removeNode(node)
        index.remove(element)
        return true
    }


}
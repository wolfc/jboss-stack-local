/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.stack.local;

import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ThreadLocalStack {
    private static final ThreadLocal<Deque<IdentityHashMap<StackLocal, Object>>> dequeLocal = new ThreadLocal<Deque<IdentityHashMap<StackLocal, Object>>>() {
        @Override
        protected Deque<IdentityHashMap<StackLocal, Object>> initialValue() {
            return new LinkedList<IdentityHashMap<StackLocal, Object>>();
        }
    };

    static Map<StackLocal, Object> createMap() {
        // replace the null with something
        dequeLocal.get().pop();
        final IdentityHashMap<StackLocal, Object> map = new IdentityHashMap<StackLocal, Object>();
        dequeLocal.get().push(map);
        return map;
    }

    /**
     * @return the current stack local map
     * @throws NoSuchElementException if no stack element has been pushed
     */
    static Map<StackLocal, Object> currentMap() throws NoSuchElementException {
        return dequeLocal.get().getFirst();
    }

    public static void push() {
        dequeLocal.get().push(null);
    }

    public static void pop() {
        dequeLocal.get().pop();
    }
}

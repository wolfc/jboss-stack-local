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

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SimpleTestCase {
    @Test
    public void testNothingThere() {
        StackLocal<String> s = new StackLocal<String>();
        try {
            s.get();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // good
        }
    }

    @Test
    public void testOtherThread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        final StackLocal<String> s = new StackLocal<String>();
        ThreadLocalStack.push();
        try {
            s.set("Hello world!");
            Future<String> result = executor.submit(new Callable<String>() {
                public String call() throws Exception {
                    ThreadLocalStack.push();
                    try {
                        return s.get();
                    } finally {
                        ThreadLocalStack.pop();
                    }
                }
            });
            assertNull(result.get(10, TimeUnit.SECONDS));
        } finally {
            ThreadLocalStack.pop();
        }
    }

    @Test
    public void testSimple() {
        StackLocal<String> s = new StackLocal<String>();
        ThreadLocalStack.push();
        try {
            s.set("Hello world!");
            String result = s.get();
            assertEquals("Hello world!", result);
        } finally {
            ThreadLocalStack.pop();
        }
    }
}

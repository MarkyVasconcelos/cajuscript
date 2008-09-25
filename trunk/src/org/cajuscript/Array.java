/*
 * Array.java
 * 
 * This file is part of CajuScript.
 * 
 * CajuScript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3, or (at your option) 
 * any later version.
 * 
 * CajuScript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CajuScript.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cajuscript;

/**
 * To create a new Array, get the size, get a value or set a value.
 * <p>CajuScript:</p>
 * <p><blockquote><pre>
 *     strArray = array.create("java.lang.String", 2)
 *     array.set(strArray, 0, "String 0")
 *     array.set(strArray, 1, "String 1")
 *     x = 0
 *     x &lt; array.size("s", 2) &#64;
 *         System.out.println(array.get(strArray, x))
 *         x = x + 1
 *     &#64;
 * </pre></blockquote></p>
 * <p>Java:</p>
 * <p><blockquote><pre>
 *     org.cajuscript.Arary cajuArray = new org.cajuscript.Array();
 *     Object strArray = cajuArray.create("java.lang.String", 2);
 *     cajuArray.set(strArray, 0, "String 0");
 *     cajuArray.set(strArray, 1, "String 1");
 *     for (int x = 0; x &lt; cajuArray.size(strArray); x++) {
 *         System.out.println(cajuArray.get(strArray, x));
 *     }
 * </pre></blockquote></p>
 * @author eduveks
 */
public class Array {
    /**
     * Initializes a newly created <code>Array</code>
     */
    public Array() {}
    
    /**
     * Create a new array.
     * <p>CajuScript:</p>
     * <p><blockquote><pre>
     *     strArray = array.create("java.lang.String", 2)
     * </pre></blockquote></p>
     * <p>Java:</p>
     * <p><blockquote><pre>
     *     Object array = cajuArray.create("java.lang.String", 2);
     * </pre></blockquote></p>
     * @param type Types: "int" or "i", "long" or "l", "double" or "d",
     * "float" or "f", "char" or "c", "boolean" or "b", "byte" or "bt",
     * "string" or "s", "java.ANY_CLASS".
     * @param size Size of the new array.
     * @return Newly array created is returned.
     * @throws java.lang.Exception If the reference of class not been found.
     */
    public static Object create(String type, int size) throws Exception {
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("i")) {
            return new int[size];
        }
        if (type.equalsIgnoreCase("long") || type.equalsIgnoreCase("l")) {
            return new long[size];
        }
        if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("d")) {
            return new double[size];
        }
        if (type.equalsIgnoreCase("float") || type.equalsIgnoreCase("f")) {
            return new float[size];
        }
        if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("c")) {
            return new char[size];
        }
        if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("b") || type.equalsIgnoreCase("bool")) {
            return new boolean[size];
        }
        if (type.equalsIgnoreCase("byte") || type.equalsIgnoreCase("bt")) {
            return new byte[size];
        }
        if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("s")) {
            return new String[size];
        }
        return java.lang.reflect.Array.newInstance(Class.forName(type), size);
    }
    
    /**
     * Get the size of array.
     * @param array The array.
     * @return The size.
     */
    public static int size(Object array) {
        return java.lang.reflect.Array.getLength(array);
    }
    
    /**
     * Get the value in determined position.
     * @param array The array.
     * @param i The position on the array.
     * @return Object of this position.
     */
    public static Object get(Object array, int i) {
        return java.lang.reflect.Array.get(array, i);
    }
    
    /**
     * Defining the value in specific position.
     * @param array The array for be affected.
     * @param i The position on the array for be affected.
     * @param v The new object for the specific position.
     */
    public static void set(Object array, int i, Object v) {
        java.lang.reflect.Array.set(array, i, v);
    }
}
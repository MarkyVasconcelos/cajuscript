package org.cajuscript.math;

public interface Operable<T> {
	public T plus(T other);
	public T subtract(T other);
	public T multiply(T other);
	public T divide(T other);
        public T module(T other);
}

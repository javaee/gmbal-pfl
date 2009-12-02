/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glassfish.pfl.func;

import java.lang.reflect.Field;

/** Factory for constructing very simple functions.
 * Note that this does not deal with tail-call elimination, or any
 * optimization: it simply generates functions.  Much more sophisticated
 * implementations are possible, starting with the construction of an AST
 * (or some other variation on double dispatching).
 *
 * AST representation
 * Each node:
 * - Return type
 * - Combinator name (or object?)
 * - list of argument functions
 *
 * @author ken_admin
 */
public class Factory {
    private static final MOPManager mm = null ;

    // constant functions
    public static final NullaryPredicate c_false = 
	new NullaryPredicateBase( "c_false", mm ) {
	    @Override
	    public boolean eval() { return false ; 
	} 
    } ;

    public static final NullaryPredicate c_true = 
	new NullaryPredicateBase( "c_true", mm ) {
	    @Override
	    public boolean eval() { return true ; 
	} 
    } ;

    public static <T> NullaryFunction<T> object( final T value ) {
	return new NullaryFunctionBase<T>( "object", mm ) {
	    @Override
	    public T eval() { return value ; }
	} ;
    }

    // Drop the result
    public static <T> NullaryVoidFunction drop( final NullaryFunction<T> func ) {
        return new NullaryVoidFunctionBase( "drop", mm ) {
            @Override
            public void eval() {
                func.evaluate() ;
            }
        } ;
    }
    
    public static NullaryVoidFunction drop( final NullaryPredicate func ) {
        return new NullaryVoidFunctionBase( "drop", mm ) {
            @Override
            public void eval() {
                func.evaluate() ;
            }
        } ;
    }

    public static <S,T,R> BinaryVoidFunction<S,T> drop( final BinaryFunction<S,T,R> func ) {
        return new BinaryVoidFunctionBase<S,T>( "drop", mm ) {
            @Override
            public void eval( S arg1, T arg2 ) {
                func.evaluate( arg1, arg2 ) ;
            }
        } ;
    }

    public static <S,T> BinaryVoidFunction<S,T> drop( final BinaryPredicate<S,T> func ) {
        return new BinaryVoidFunctionBase<S,T>( "drop", mm ) {
            @Override
            public void eval( S arg1, T arg2 ) {
                func.evaluate( arg1, arg2 ) ;
            }
        } ;
    }

    // bind argument (higher arity to lower arity)
    public static <S,T,R> UnaryFunction<T,R> bind1( final S value,
        final BinaryFunction<S,T,R> func ) {

	return new UnaryFunctionBase<T,R>( "bind1", mm ) {
	    @Override
	    public R eval( T arg ) {
		return func.evaluate( value, arg ) ;
	    }
	} ;
    }

    public static <S,T,R> UnaryVoidFunction<T> bind1( final S value,
        final BinaryVoidFunction<S,T> func ) {

	return new UnaryVoidFunctionBase<T>( "bind1", mm ) {
	    @Override
	    public void eval( T arg ) {
		func.evaluate( value, arg ) ;
	    }
	} ;
    }

    public static <S,T> UnaryPredicate<T> bind1( final S value,
        final BinaryPredicate<S,T> func ) {

	return new UnaryPredicateBase<T>( "bind1", mm ) {
	    @Override
	    public boolean eval( T arg ) {
		return func.evaluate( value, arg ) ;
	    }
	} ;
    }

    public static <S,T,R> UnaryFunction<S,R> bind2( final T value,
        final BinaryFunction<S,T,R> func ) {

	return new UnaryFunctionBase<S,R>( "bind2", mm ) {
	    @Override
	    public R eval( S arg ) {
		return func.evaluate( arg, value ) ;
	    }
	} ;
    }

    public static <S,T,R> UnaryVoidFunction<S> bind2( final T value,
        final BinaryVoidFunction<S,T> func ) {

	return new UnaryVoidFunctionBase<S>( "bind2", mm ) {
	    @Override
	    public void eval( S arg ) {
		func.evaluate( arg, value ) ;
	    }
	} ;
    }

    public static <S,T> UnaryPredicate<S> bind2( final T value,
        final BinaryPredicate<S,T> func ) {

	return new UnaryPredicateBase<S>( "bind2", mm ) {
	    @Override
	    public boolean eval( S arg ) {
		return func.evaluate( arg, value ) ;
	    }
	} ;
    }

    public static <S,R> NullaryFunction<R> bind( final S value,
        final UnaryFunction<S,R> func ) {

	return new NullaryFunctionBase<R>( "bind", mm ) {
	    @Override
	    public R eval() {
		return func.evaluate( value ) ;
	    }
	} ;
    }

    public static <S> NullaryVoidFunction bind( final S value,
        final UnaryVoidFunction<S> func ) {

	return new NullaryVoidFunctionBase( "bind", mm ) {
	    @Override
	    public void eval() {
		func.evaluate( value ) ;
	    }
	} ;
    }

    public static <S> NullaryPredicate bind( final S value,
        final UnaryPredicate<S> func ) {

	return new NullaryPredicateBase( "bind", mm ) {
	    @Override
	    public boolean eval() {
		return func.evaluate( value ) ;
	    }
	} ;
    }

    // inject (lower arity into higher arity)
    public static <S,T,R> BinaryFunction<S,T,R> inject1(
        final UnaryFunction<T,R> func, Class<S> cls ) {

	return new BinaryFunctionBase<S,T,R>( "inject1", mm ) {
	    @Override
	    public R eval( S arg1, T arg2 ) {
		return func.evaluate( arg2 ) ;
	    }
	} ;
    }

    public static <S,T,R> BinaryFunction<S,T,R> inject2(
        final UnaryFunction<S,R> func, Class<T> cls ) {

	return new BinaryFunctionBase<S,T,R>( "inject2", mm ) {
	    @Override
	    public R eval( S arg1, T arg2 ) {
		return func.evaluate( arg1 ) ;
	    }
	} ;
    }

    public static <S,R> UnaryFunction<S,R> inject( 
        final NullaryFunction<R> func, Class<S> cls ) {

        return new UnaryFunctionBase<S,R>( "inject", mm ) {
            @Override
            public R eval( S arg ) {
                return func.evaluate() ;
            }
        } ;
    }

    // compose
    public static <S,T,R> UnaryFunction<S,R> comp(
	final UnaryFunction<S,T> first, final UnaryFunction<T,R> second ) {
	return new UnaryFunctionBase<S,R>( "comp", mm ) {
            @Override
	    public R eval( S arg ) {
		return second.evaluate( first.evaluate( arg ) ) ;
	    }
	} ;
    }

    public static <S,T,U,V,R> BinaryFunction<S,T,R> comp(
        final UnaryFunction<S,U> f1,
        final UnaryFunction<T,V> f2,
        final BinaryFunction<U,V,R> bin ) {

        return new BinaryFunctionBase<S, T, R>( "comp", mm ) {
            @Override
            public R eval(S arg1, T arg2) {
                return bin.evaluate( f1.evaluate(arg1), f2.evaluate(arg2)) ;
            }
        } ;
    }

    public static <S,R> UnaryFunction<S,R> combine(
        final BinaryFunction<S,S,R> func ) {

        return new UnaryFunctionBase<S,R>( "combine", mm ) {
            @Override
            public R eval( S arg ) {
                return func.evaluate( arg, arg ) ;
            }
        } ;
    }

    // logical functions for predicates
    public static final class Equal<S,T> extends BinaryPredicateBase<S,T> {
	public Equal() {
	    super( "equal", mm ) ;
	}

        @Override
        public boolean eval(S arg1, T arg2) {
            return arg1.equals( arg2 ) ;
        }
    }

    public static final <S,T> BinaryPredicate<S,T> equal() {
        return new Equal<S,T>() ;
    }

    // arithmetic
    public static final BinaryFunction<Long,Long,Long> plus = 
	new BinaryFunctionBase<Long,Long,Long>( "plus", mm ) {
        @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1+arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> minus = 
	new BinaryFunctionBase<Long,Long,Long>( "minus", mm ) {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1-arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> times = 
	new BinaryFunctionBase<Long,Long,Long>( "times", mm ) {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1*arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> div = 
	new BinaryFunctionBase<Long,Long,Long>( "div", mm ) {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1/arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> mod = 
	new BinaryFunctionBase<Long,Long,Long>( "mod", mm ) {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1 % arg2 ;
	    }
	} ;


    // conditional
    public static <S,T,R> BinaryFunction<S,T,R> cond(
        final BinaryPredicate<S,T> c, final BinaryFunction<S,T,R> t,
	final BinaryFunction<S,T,R> f ) {

	return new BinaryFunctionBase<S,T,R>( "cond", mm ) {
	    @Override
	    public R eval( S arg1, T arg2 ) {
		if (c.evaluate( arg1, arg2 )) {
		    return t.evaluate( arg1, arg2 ) ;
		} else {
		    return f.evaluate( arg1, arg2 ) ;
		}
	    }
	} ;
    }

    public static <S,R> UnaryFunction<S,R> cond(
        final UnaryPredicate<S> c, final UnaryFunction<S,R> t,
	final UnaryFunction<S,R> f ) {

	return new UnaryFunctionBase<S,R>( "cond", mm ) {
	    @Override
	    public R eval( S arg1 ) {
		if (c.evaluate( arg1 )) {
		    return t.evaluate( arg1 ) ;
		} else {
		    return f.evaluate( arg1 ) ;
		}
	    }
	} ;
    }

    // curry
    public static <S,T,R> UnaryFunction<S,UnaryFunction<T,R>> curry(
        final BinaryFunction<S,T,R> func ) {

	return new UnaryFunctionBase<S,UnaryFunction<T,R>>( "curry(1)", mm ) {
	    @Override
	    public UnaryFunction<T,R> eval( final S sarg ) {
		return new UnaryFunctionBase<T,R>( "curry(2)", mm ) {
		    @Override
		    public R eval( final T targ ) {
			return func.evaluate( sarg, targ ) ;
		    }
		} ;
	    }
	} ;
    }

    // Functions related to java objects
    // getField
    // setField
    // call (static method)
    // call (non-static method)
    // newObject (call constructor)

    public static <T,R> UnaryFunction<T,R> getField( Class<? extends T> cls, String fname ) {
	final Field fld ;
	try {
	    fld = cls.getDeclaredField(fname);
	} catch (NoSuchFieldException ex) {
	    throw new IllegalArgumentException( ex ) ;
	} catch (SecurityException ex) {
	    throw new IllegalArgumentException( ex ) ;
	}

	return new UnaryFunctionBase<T,R>("getField", mm ) {
	    @Override
	    public R eval( T obj ) {
	        try {
		    return (R) fld.get( obj );
		} catch (IllegalArgumentException ex) {
		    throw new IllegalArgumentException( ex ) ;
		} catch (IllegalAccessException ex) {
		    throw new IllegalArgumentException( ex ) ;
		}
	    }
	} ;
    }

    // Function aliases, which allow construction of recursive functions
    public static class BinaryFunctionAlias<S,T,R> extends BinaryFunctionBase<S,T,R> {
	private BinaryFunction<S,T,R> delegate = null ;

	public BinaryFunctionAlias() {
	    super( "alias", mm ) ;
	}

        @Override
	public R eval( S arg1, T arg2 ) {
	    if (delegate == null)
		throw new IllegalStateException( "delegate has not been set" ) ;
	    else
		return delegate.evaluate( arg1, arg2 ) ;
	}

	public void set( BinaryFunction<S,T,R> f ) {
	    if (delegate == null)
		delegate = f ;
	    else
		throw new IllegalStateException( "delegate has already been set" ) ;
	}
    }

    public static class UnaryFunctionAlias<S,R> extends UnaryFunctionBase<S,R> {
	private UnaryFunction<S,R> delegate = null ;

	public UnaryFunctionAlias() {
	    super( "alias", mm ) ;
	}

        @Override
	public R eval( S arg1 ) {
	    if (delegate == null)
		throw new IllegalStateException( "delegate has not been set" ) ;
	    else
		return delegate.evaluate( arg1 ) ;
	}

	public void set( UnaryFunction<S,R> f ) {
	    if (delegate == null)
		delegate = f ;
	    else
		throw new IllegalStateException( "delegate has already been set" ) ;
	}
    }

    // Simple tests
    private static UnaryFunction<Long,Long> jfib = 
	new UnaryFunctionBase<Long,Long>( "jfib", mm ) {
        @Override
	    public Long eval( Long arg ) {
		if (arg == 0)
		    return 1L ;
		else if (arg == 1)
		    return 1L ;
		else
		    return evaluate(arg-1) + evaluate(arg-2) ;
	    }
	} ;

    private static final UnaryFunctionAlias<Long,Long> fib =
        new UnaryFunctionAlias<Long,Long>() ;
    private static final BinaryPredicate<Long,Long> lequ = new Equal<Long,Long>() ;
    static {
        fib.set(
            cond( bind2( 0L, lequ ),
                inject( object(1L), Long.class ),
                cond( bind2( 1L, lequ ),
                    inject( object(1L), Long.class ),
                    combine( comp(
                        comp( fib, bind2( 1L, minus )),
                        comp( fib, bind2( 2L, minus )),
                        plus ) ) ) ) ) ;
    }

    private static final int WARMUP = 10000 ;
    private static final int TEST = 20000 ;

    private static Long time( NullaryVoidFunction test ) {
        for (int ctr=0; ctr<WARMUP; ctr++) {
            test.evaluate() ;
        }

        final long start = System.nanoTime() ;
        for (int ctr=0; ctr<TEST; ctr++) {
            test.evaluate() ;
        }
        final long duration = System.nanoTime() - start ;
        return duration / TEST ;
    }

    public static void main( String[] args ) {
	final Long jfibTime = time( drop( bind( 20L, jfib ))) ;
	final Long fibTime = time( drop( bind( 20L, fib ))) ;
	System.out.println( "Time for normal Java fib implementation = " + jfibTime ) ;
	System.out.println( "Time for pfl fib implementation         = " + fibTime ) ;
    }
}

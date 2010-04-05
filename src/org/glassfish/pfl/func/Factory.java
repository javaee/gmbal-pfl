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
    private Factory() {}

    private static MOPManager mm = null ;

    public static void setMOPManager( MOPManager lmm ) {
	mm = lmm ;
    }

    // constant functions
    public static NullaryPredicate FALSE() {
	return new NullaryPredicateConstantImpl( false )  ;
    }

    public static NullaryPredicate TRUE() {
	return new NullaryPredicateConstantImpl( true )  ;
    } ;

    public static NullaryFunction<Long> NUM( int value ) {
        return new NullaryFunctionConstantImpl<Long>( (long)value ) ;
    }

    public static <T> NullaryFunction<T> OBJ( final T value ) {
        return new NullaryFunctionConstantImpl<T>( value ) ;
    }

    // Drop the result
    public static <T> NullaryVoidFunction drop( final NullaryFunction<T> func ) {
        return new NullaryFunctionDropImpl( func) ;
    }
    
    public static NullaryVoidFunction drop( final NullaryPredicate func ) {
        return new NullaryPredicateDropImpl( func ) ;
    }

    public static <S,R> UnaryVoidFunction<S> drop( final UnaryFunction<S,R> func ) {
        return new UnaryFunctionDropImpl( func) ;
    }

    public static <S> UnaryVoidFunction<S> drop( final UnaryPredicate<S> func ) {
        return new UnaryPredicateDropImpl<S>( func ) ;
    }

    public static <S,T,R> BinaryVoidFunction<S,T> drop( final BinaryFunction<S,T,R> func ) {
        return new BinaryFunctionDropImpl( func) ;
    }

    public static <S,T> BinaryVoidFunction<S,T> drop( final BinaryPredicate<S,T> func ) {
        return new BinaryPredicateDropImpl<S, T>( func ) ;
    }

    // bind argument (higher arity to lower arity)
    public static <S,T,R> UnaryFunction<T,R> bind1( final S value,
        final BinaryFunction<S,T,R> func ) {
	return new BinaryFunctionBind1Impl<S,T,R>( func, value) ;
    }

    public static <S,T> UnaryVoidFunction<T> bind1( final S value,
        final BinaryVoidFunction<S,T> func ) {
        return new BinaryVoidFunctionBind1Impl<S,T>( func, value ) ;
    }

    public static <S,T> UnaryPredicate<T> bind1( final S value,
        final BinaryPredicate<S,T> func ) {
        return new BinaryPredicateBind1Impl<S,T>( func, value ) ;
    }

    public static <S,T,R> UnaryFunction<S,R> bind2( final T value,
        final BinaryFunction<S,T,R> func ) {
        return new BinaryFunctionBind2Impl<S,T,R>( func, value ) ;
    }

    public static <S,T> UnaryVoidFunction<S> bind2( final T value,
        final BinaryVoidFunction<S,T> func ) {
        return new BinaryVoidFunctionBind2Impl<S,T>( func, value ) ;
    }

    public static <S,T> UnaryPredicate<S> bind2( final T value,
        final BinaryPredicate<S,T> func ) {
        return new BinaryPredicateBind2Impl<S,T>( func, value ) ;
    }

    public static <S,R> NullaryFunction<R> bind( final S value,
        final UnaryFunction<S,R> func ) {

	return new UnaryFunctionBindImpl( func, value) ;
    }

    public static <S> NullaryVoidFunction bind( final S value,
        final UnaryVoidFunction<S> func ) {

        return new UnaryVoidFunctionBindImpl( func, value ) ;
    }

    public static <S> NullaryPredicate bind( final S value,
        final UnaryPredicate<S> func ) {

	return new UnaryPredicateBindImpl( func, value) ;
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

    public static <S,R> UnaryFunction<S,R> inject(
        final R val, Class<S> cls ) {

        return new UnaryFunctionBase<S,R>( "inject", mm ) {
            @Override
            public R eval( S arg ) {
                return val ;
            }
        } ;
    }

    // compose
    public static <S,T,R> UnaryFunction<S,R> comp(
	final UnaryFunction<S,T> first, final UnaryFunction<T,R> second ) {
	return new UnaryFunctionCompImpl( second, first) ;
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

    private static final UnaryFunctionAlias<Long,Long> fib ;
    private static final BinaryPredicate<Long,Long> lequ ;
    private static final MOPManager lmm ;

    static {
	lmm = new MOPManager() ;
	setMOPManager( lmm ) ;

        fib = new UnaryFunctionAlias<Long,Long>() ;
        lequ = new Equal<Long,Long>() ;
        UnaryFunction<Long,Long> c1 = inject( 1L, Long.class ) ;
        fib.set(
            cond( bind2( 0L, lequ ), c1,
                cond( bind2( 1L, lequ ), c1,
                    combine( comp(
                        comp( bind2( 1L, minus ), fib ),
                        comp( bind2( 2L, minus ), fib ),
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
        // lmm.setMOP(MOP.tracer);
        System.out.println( "jfib(20) = " + jfib.evaluate(20L) ) ;
        System.out.println( "fib(20)  = " + fib.evaluate(20L) ) ;
	final Long jfibTime = time( drop( bind( 20L, jfib ))) ;
	final Long fibTime = time( drop( bind( 20L, fib ))) ;
	System.out.println( "Time for normal Java fib implementation = "
            + jfibTime ) ;
	System.out.println( "Time for pfl fib implementation         = "
            + fibTime ) ;
    }

//========================= Implementation classes ========================//

    // Constants
    public static class NullaryPredicateConstantImpl
        extends NullaryPredicateBase {
        private final boolean value ;

        public NullaryPredicateConstantImpl(boolean value) {
            super("NullaryPredicateConstant", mm);
            this.value = value ;
        }

        @Override
        public boolean eval() {
            return value;
        }

        public boolean value() { return value ; }
    }

    public static class NullaryFunctionConstantImpl<T>
        extends NullaryFunctionBase<T> {
        private final T value ;

        public NullaryFunctionConstantImpl(T value) {
            super("NullaryFunctionConstant", mm);
            this.value = value ;
        }

        @Override
        public T eval() {
            return value;
        }

        public T value() { return value ; }
    }

    // Drop result
    public static class NullaryFunctionDropImpl<T> extends NullaryVoidFunctionBase {
        private final NullaryFunction<T> func;

        public NullaryFunctionDropImpl( final NullaryFunction<T> func) {
            super("NullaryFunctionDrop", mm);
            this.func = func;
        }

        @Override
        public void eval() {
            func.evaluate();
        }

        public NullaryFunction<T> func() { return func ; }
    }

    public static class NullaryPredicateDropImpl extends NullaryVoidFunctionBase {
        private final NullaryPredicate func;

        public NullaryPredicateDropImpl( final NullaryPredicate func) {
            super("NullaryPredicateDrop", mm);
            this.func = func;
        }

        @Override
        public void eval() {
            func.evaluate();
        }

        public NullaryPredicate func() { return func ; }
    }

    private static class UnaryFunctionDropImpl<S,R>
        extends UnaryVoidFunctionBase<S> {

        private final UnaryFunction<S,R> func;

        public UnaryFunctionDropImpl( UnaryFunction<S,R> func) {
            super("UnaryFunctionDrop", mm);
            this.func = func;
        }

        @Override
        public void eval(S arg1 ) {
            func.evaluate(arg1);
        }

        public UnaryFunction<S,R> func() { return func ; }
    }

    private static class UnaryPredicateDropImpl<S>
        extends UnaryVoidFunctionBase<S> {

        private final UnaryPredicate<S> func;

        public UnaryPredicateDropImpl( UnaryPredicate<S> func) {
            super("UnaryPredicateDrop", mm);
            this.func = func;
        }

        @Override
        public void eval(S arg1) {
            func.evaluate(arg1);
        }

        public UnaryPredicate<S> func() { return func ; }
    }

    private static class BinaryFunctionDropImpl<S,T,R>
        extends BinaryVoidFunctionBase<S, T> {

        private final BinaryFunction<S, T, R> func;

        public BinaryFunctionDropImpl( BinaryFunction<S, T, R> func) {
            super("BinaryFunctionDrop", mm);
            this.func = func;
        }

        @Override
        public void eval(S arg1, T arg2) {
            func.evaluate(arg1, arg2);
        }

        public BinaryFunction<S,T,R> func() { return func ; }
    }

    private static class BinaryPredicateDropImpl<S,T>
        extends BinaryVoidFunctionBase<S, T> {

        private final BinaryPredicate<S, T> func;

        public BinaryPredicateDropImpl( BinaryPredicate<S, T> func) {
            super("BinaryPredicateDrop", mm);
            this.func = func;
        }

        @Override
        public void eval(S arg1, T arg2) {
            func.evaluate(arg1, arg2);
        }

        public BinaryPredicate<S,T> func() { return func ; }
    }

    // Bind argument
    public static class UnaryVoidFunctionBindImpl<S>
        extends NullaryVoidFunctionBase {

        private final UnaryVoidFunction<S> func;
        private final S value;

        public UnaryVoidFunctionBindImpl( UnaryVoidFunction<S> func, S value) {
            super("UnaryPredicateBind", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public void eval() {
            func.evaluate(value);
        }
    }

    public static class UnaryPredicateBindImpl<S>
        extends NullaryPredicateBase {

        private final UnaryPredicate<S> func;
        private final S value;

        public UnaryPredicateBindImpl( UnaryPredicate<S> func, S value) {
            super("UnaryPredicateBind", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval() {
            return func.evaluate(value);
        }
    }

    public static class UnaryFunctionBindImpl<S,R>
        extends NullaryFunctionBase {

        private final UnaryFunction<S,R> func;
        private final S value;

        public UnaryFunctionBindImpl( UnaryFunction<S,R> func, S value) {
            super("UnaryFunctionBind", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval() {
            return func.evaluate(value);
        }
    }

    private static class BinaryFunctionBind1Impl<S,T,R>
        extends UnaryFunctionBase<T, R> {

        private final BinaryFunction<S, T, R> func;
        private final S value;

        public BinaryFunctionBind1Impl( BinaryFunction<S, T, R> func, S value) {
            super("BinaryFunctionBind1", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval(T arg) {
            return func.evaluate(value, arg);
        }
    }

    private static class BinaryVoidFunctionBind1Impl<S,T>
        extends UnaryVoidFunctionBase<T> {

        private final BinaryVoidFunction<S, T> func;
        private final S value;

        public BinaryVoidFunctionBind1Impl( final BinaryVoidFunction<S, T> func,
            S value) {
            super("BinaryVoidFunctionBind1", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public void eval(T arg) {
            func.evaluate(value, arg);
        }
    }


    private static class BinaryPredicateBind1Impl<S,T>
        extends UnaryPredicateBase<T> {

        private final BinaryPredicate<S, T> func;
        private final S value;

        public BinaryPredicateBind1Impl( final BinaryPredicate<S, T> func,
            S value) {
            super("BinaryVoidFunctionBind1", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval(T arg) {
            return func.evaluate(value, arg);
        }
    }


    private static class BinaryFunctionBind2Impl<S,T,R>
        extends UnaryFunctionBase<S, R> {

        private final BinaryFunction<S, T, R> func;
        private final T value;

        public BinaryFunctionBind2Impl( BinaryFunction<S, T, R> func, T value) {
            super("BinaryFunctionBind2", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval(S arg) {
            return func.evaluate(arg, value);
        }
    }

    private static class BinaryVoidFunctionBind2Impl<S,T>
        extends UnaryVoidFunctionBase<S> {

        private final BinaryVoidFunction<S, T> func;
        private final T value;

        public BinaryVoidFunctionBind2Impl( final BinaryVoidFunction<S, T> func,
            T value) {
            super("BinaryVoidFunctionBind2", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public void eval(S arg) {
            func.evaluate(arg, value);
        }
    }


    private static class BinaryPredicateBind2Impl<S,T>
        extends UnaryPredicateBase<S> {

        private final BinaryPredicate<S, T> func;
        private final T value;

        public BinaryPredicateBind2Impl( final BinaryPredicate<S, T> func,
            T value) {
            super("BinaryVoidFunctionBind2", mm);
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval(S arg) {
            return func.evaluate(arg, value);
        }
    }

    private static class UnaryFunctionCompImpl<S,T,R> extends UnaryFunctionBase<S, R> {

        private final UnaryFunction<T, R> second;
        private final UnaryFunction<S, T> first;

        public UnaryFunctionCompImpl( UnaryFunction<T, R> second,
            UnaryFunction<S, T> first) {
            super("UnaryFunctionComp", mm);
            this.second = second;
            this.first = first;
        }

        @Override
        public R eval(S arg) {
            return second.evaluate(first.evaluate(arg));
        }
    }

}

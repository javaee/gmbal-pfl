/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glassfish.pfl.ff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.glassfish.pfl.basic.func.BinaryFunction;
import org.glassfish.pfl.basic.func.BinaryFunctionBase;
import org.glassfish.pfl.basic.func.BinaryPredicate;
import org.glassfish.pfl.basic.func.BinaryPredicateBase;
import org.glassfish.pfl.basic.func.BinaryVoidFunction;
import org.glassfish.pfl.basic.func.BinaryVoidFunctionBase;
import org.glassfish.pfl.basic.func.NullaryFunction;
import org.glassfish.pfl.basic.func.NullaryFunctionBase;
import org.glassfish.pfl.basic.func.NullaryPredicate;
import org.glassfish.pfl.basic.func.NullaryPredicateBase;
import org.glassfish.pfl.basic.func.NullaryVoidFunction;
import org.glassfish.pfl.basic.func.NullaryVoidFunctionBase;
import org.glassfish.pfl.basic.func.UnaryFunction;
import org.glassfish.pfl.basic.func.UnaryFunctionBase;
import org.glassfish.pfl.basic.func.UnaryPredicate;
import org.glassfish.pfl.basic.func.UnaryPredicateBase;
import org.glassfish.pfl.basic.func.UnaryVoidFunction;
import org.glassfish.pfl.basic.func.UnaryVoidFunctionBase;

/** Factory for constructing very simple functions.
 * Note that this does not deal with tail-call elimination, or any
 * optimization: it simply generates functions.  Much more sophisticated
 * implementations are possible, starting with the construction of an AST
 * (or some other variation on double dispatching).
 *
 * This is a higher-level function facility which will use facilities
 * such as codegen, which itself uses the lower-level pfl code.
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

    private static Method checkPredicate( Class<?> cls, String mname,
        Class<?>... args) {
        Method m;
        try {
            m = cls.getDeclaredMethod(mname, args );
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException( ex ) ;
        } catch (SecurityException ex) {
            throw new RuntimeException( ex ) ;
        }

        if (!Modifier.isStatic( m.getModifiers())) {
            throw new RuntimeException( "Method " + mname
                + " is not static" ) ;
        }

        Class<?> rtype = m.getReturnType() ;
        if (!rtype.equals( boolean.class )
            || !(rtype.equals( Boolean.class ))) {

            throw new RuntimeException( "Method " + mname
                + " does not return boolean or Boolean" ) ;
        }

        return m ;
    }

    private static Object makeProxy(final Object delegate, final Class<?> cls ) {
        InvocationHandler ih = new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] os)
                throws Throwable {
                return method.invoke( delegate, os) ;
            }
        } ;

        Class<?>[] types = { NullaryPredicate.class } ;
        return Proxy.newProxyInstance(
            cls.getClass().getClassLoader(), types, ih) ;
    }

    public static class NullaryPredicateStaticFromImpl extends
        NullaryPredicateBase {

        private final Class<?> cls ;
        private final String mname ;

        final Method minvoke ;

        public NullaryPredicateStaticFromImpl( Class<?> cls, String mname ) {
            super( "froms[ " + cls.getName() + "." + mname + "]" ) ;
            this.cls = cls ;
            this.mname = mname ;
            minvoke = checkPredicate( cls, mname ) ;
        }

        @Override
        public boolean eval() {
            try {
                return (Boolean) minvoke.invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException( ex ) ;
            }
        }
    }

    /** Create a nullary predicate from a static method on class cls that
     * returns a boolean.
     *
     * @param cls The class containing method boolean [mname]().
     * @param mname The name of the method in cls.
     * @return A NullaryPredicate that call cls.mname() when evaluated.
     */
    public static NullaryPredicate froms( final Class<?> cls,
        final String mname ) {
        final Object delegate = new NullaryPredicateStaticFromImpl( cls,
            mname ) ;
        return (NullaryPredicate)makeProxy( delegate, cls ) ;
    }

    /** Create a unary predicate from a static method on class cls that
     * returns a boolean and takes one argument.
     *
     * @param cls The class containing method boolean [mname]( arg1 ).
     * @param arg1 The type of the argument to the method
     * @param mname The name of the method in cls.
     * @return A UnaryPredicate that callx cls.mname() when evaluated.
     */
    @SuppressWarnings({"unchecked"})
    public static <S> UnaryPredicate<S> froms( final Class<?> cls,
        final String mname, final Class<S> arg1 ) {

        final Method minvoke = checkPredicate( cls, mname, arg1) ;

        final Object delegate = new UnaryPredicateBase<S>(
            "froms[" + cls.getName() + "]" ) {

            @Override
            public boolean eval( S arg ) {
                try {
                    return (Boolean) minvoke.invoke(null, arg );
                } catch (Exception ex) {
                    throw new RuntimeException( ex ) ;
                }
            }
        } ;

        return (UnaryPredicate<S>)makeProxy( delegate, cls ) ;
    }

    /** Create a binary predicate from a static method on class cls that
     * returns a boolean and calls two arguments.
     *
     * @param cls The class containing method boolean [mname]( arg1 ).
     * @param arg1 The type of the argument to the method
     * @param mname The name of the method in cls.
     * @return A UnaryPredicate that callx cls.mname() when evaluated.
     */
    @SuppressWarnings({"unchecked"})
    public static <S,T> BinaryPredicate<S,T> froms( final Class<?> cls,
        final String mname, final Class<S> arg1, final Class<T> arg2 ) {

        final Method minvoke = checkPredicate( cls, mname, arg1, arg2 ) ;

        final Object delegate = new BinaryPredicateBase<S,T>(
            "froms[" + cls.getName() + "]" ) {

            @Override
            public boolean eval( S arg1, T arg2 ) {
                try {
                    return (Boolean) minvoke.invoke(null, arg1, arg2 );
                } catch (Exception ex) {
                    throw new RuntimeException( ex ) ;
                }
            }
        } ;

        return (BinaryPredicate<S,T>)makeProxy( delegate, cls ) ;
    }

    /** Create a unary predicate from a non-static method on class cls that
     * returns a boolean and takes no arguments.
     *
     * @param cls The class containing method boolean [mname]( arg1 ).
     * @param mname The name of the method in cls.
     * @return A UnaryPredicate that callx cls.mname() when evaluated.
     */
    @SuppressWarnings({"unchecked"})
    public static <S> UnaryPredicate<S> from( final Class<S> cls,
        final String mname ) {

        final Method minvoke = checkPredicate( cls, mname) ;

        final Object delegate = new UnaryPredicateBase<S>(
            "from[" + cls.getName() + "]" ) {

            @Override
            public boolean eval( S arg ) {
                try {
                    return (Boolean) minvoke.invoke( arg );
                } catch (Exception ex) {
                    throw new RuntimeException( ex ) ;
                }
            }
        } ;

        return (UnaryPredicate<S>)makeProxy( delegate, cls ) ;
    }

    /** Create a binary predicate from a static method on class cls that
     * returns a boolean and calls two arguments.
     *
     * @param cls The class containing method boolean [mname]( arg1 ).
     * @param arg1 The type of the argument to the method
     * @param mname The name of the method in cls.
     * @return A UnaryPredicate that callx cls.mname() when evaluated.
     */
    @SuppressWarnings({"unchecked"})
    public static <S,T> BinaryPredicate<S,T> from( final Class<S> cls,
        final String mname, final Class<T> arg1 ) {

        final Method minvoke = checkPredicate( cls, mname, arg1 ) ;

        final Object delegate = new BinaryPredicateBase<S,T>(
            "froms[" + cls.getName() + "]" ) {

            @Override
            public boolean eval( S arg1, T arg2 ) {
                try {
                    return (Boolean) minvoke.invoke(arg1, arg2 );
                } catch (Exception ex) {
                    throw new RuntimeException( ex ) ;
                }
            }
        } ;

        return (BinaryPredicate<S,T>)makeProxy( delegate, cls ) ;
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
        return new NullaryFunctionDropImpl<T>( func) ;
    }
    
    public static NullaryVoidFunction drop( final NullaryPredicate func ) {
        return new NullaryPredicateDropImpl( func ) ;
    }

    public static <S,R> UnaryVoidFunction<S> drop( final UnaryFunction<S,R> func ) {
        return new UnaryFunctionDropImpl<S,R>( func) ;
    }

    public static <S> UnaryVoidFunction<S> drop( final UnaryPredicate<S> func ) {
        return new UnaryPredicateDropImpl<S>( func ) ;
    }

    public static <S,T,R> BinaryVoidFunction<S,T> drop( final BinaryFunction<S,T,R> func ) {
        return new BinaryFunctionDropImpl<S,T,R>( func) ;
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

	return new UnaryFunctionBindImpl<S,R>( func, value) ;
    }

    public static <S> NullaryVoidFunction bind( final S value,
        final UnaryVoidFunction<S> func ) {

        return new UnaryVoidFunctionBindImpl<S>( func, value ) ;
    }

    public static <S> NullaryPredicate bind( final S value,
        final UnaryPredicate<S> func ) {

	return new UnaryPredicateBindImpl<S>( func, value) ;
    }

    // inject (lower arity into higher arity)
    public static <S,T,R> BinaryFunction<S,T,R> inject1(
        final UnaryFunction<T,R> func, Class<S> cls ) {

	return new BinaryFunctionBase<S,T,R>( "inject1") {
	    @Override
	    public R eval( S arg1, T arg2 ) {
		return func.evaluate( arg2 ) ;
	    }
	} ;
    }

    public static <S,T,R> BinaryFunction<S,T,R> inject2(
        final UnaryFunction<S,R> func, Class<T> cls ) {

	return new BinaryFunctionBase<S,T,R>( "inject2") {
	    @Override
	    public R eval( S arg1, T arg2 ) {
		return func.evaluate( arg1 ) ;
	    }
	} ;
    }

    public static <S,R> UnaryFunction<S,R> inject( 
        final NullaryFunction<R> func, Class<S> cls ) {

        return new UnaryFunctionBase<S,R>( "inject") {
            @Override
            public R eval( S arg ) {
                return func.evaluate() ;
            }
        } ;
    }

    public static <S,R> UnaryFunction<S,R> inject(
        final R val, Class<S> cls ) {

        return new UnaryFunctionBase<S,R>( "inject") {
            @Override
            public R eval( S arg ) {
                return val ;
            }
        } ;
    }

    // compose
    public static <S,T,R> UnaryFunction<S,R> comp(
	final UnaryFunction<S,T> first, final UnaryFunction<T,R> second ) {
	return new UnaryFunctionCompImpl<S,T,R>( second, first) ;
    }

    public static <S,T,U,V,R> BinaryFunction<S,T,R> comp(
        final UnaryFunction<S,U> f1,
        final UnaryFunction<T,V> f2,
        final BinaryFunction<U,V,R> bin ) {

        return new BinaryFunctionBase<S, T, R>( "comp") {
            @Override
            public R eval(S arg1, T arg2) {
                return bin.evaluate( f1.evaluate(arg1), f2.evaluate(arg2)) ;
            }
        } ;
    }

    public static <S,R> UnaryFunction<S,R> combine(
        final BinaryFunction<S,S,R> func ) {

        return new UnaryFunctionBase<S,R>( "combine") {
            @Override
            public R eval( S arg ) {
                return func.evaluate( arg, arg ) ;
            }
        } ;
    }

    // logical functions for predicates
    public static final class Equal<S,T> extends BinaryPredicateBase<S,T> {
	public Equal() {
	    super( "equal") ;
	}

        @Override
        public boolean eval(S arg1, T arg2) {
            return arg1.equals( arg2 ) ;
        }
    }

    public static <S,T> BinaryPredicate<S,T> equal() {
        return new Equal<S,T>() ;
    }

    // arithmetic
    public static final BinaryFunction<Long,Long,Long> plus = 
	new BinaryFunctionBase<Long,Long,Long>( "plus") {
        @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1+arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> minus = 
	new BinaryFunctionBase<Long,Long,Long>( "minus") {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1-arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> times = 
	new BinaryFunctionBase<Long,Long,Long>( "times") {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1*arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> div = 
	new BinaryFunctionBase<Long,Long,Long>( "div") {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1/arg2 ;
	    }
	} ;

    public static final BinaryFunction<Long,Long,Long> mod = 
	new BinaryFunctionBase<Long,Long,Long>( "mod") {
	    @Override
	    public Long eval( Long arg1, Long arg2 ) {
		return arg1 % arg2 ;
	    }
	} ;


    // conditional
    public static <S,T,R> BinaryFunction<S,T,R> cond(
        final BinaryPredicate<S,T> c, final BinaryFunction<S,T,R> t,
	final BinaryFunction<S,T,R> f ) {

	return new BinaryFunctionBase<S,T,R>( "cond") {
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

	return new UnaryFunctionBase<S,R>( "cond") {
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

	return new UnaryFunctionBase<S,UnaryFunction<T,R>>( "curry(1)") {
	    @Override
	    public UnaryFunction<T,R> eval( final S sarg ) {
		return new UnaryFunctionBase<T,R>( "curry(2)") {
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

	return new UnaryFunctionBase<T,R>("getField") {
	    @Override
            @SuppressWarnings("unchecked")
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
	    super( "alias") ;
	}

        @Override
	public R eval( S arg1, T arg2 ) {
	    if (delegate == null) {
                throw new IllegalStateException("delegate has not been set");
            } else {
                return delegate.evaluate(arg1, arg2);
            }
	}

	public void set( BinaryFunction<S,T,R> f ) {
	    if (delegate == null) {
                delegate = f;
            } else {
                throw new IllegalStateException("delegate has already been set");
            }
	}
    }

    public static class UnaryFunctionAlias<S,R> extends UnaryFunctionBase<S,R> {
	private UnaryFunction<S,R> delegate = null ;

	public UnaryFunctionAlias() {
	    super( "alias") ;
	}

        @Override
	public R eval( S arg1 ) {
	    if (delegate == null) {
                throw new IllegalStateException("delegate has not been set");
            } else {
                return delegate.evaluate(arg1);
            }
	}

	public void set( UnaryFunction<S,R> f ) {
	    if (delegate == null) {
                delegate = f;
            } else {
                throw new IllegalStateException("delegate has already been set");
            }
	}
    }

    // Simple tests
    private static UnaryFunction<Long,Long> jfib = 
	new UnaryFunctionBase<Long,Long>( "jfib") {
        @Override
	    public Long eval( Long arg ) {
		if (arg == 0) {
                    return 1L;
                } else if (arg == 1) {
                    return 1L;
                } else {
                    return evaluate(arg - 1) + evaluate(arg - 2);
                }
	    }
	} ;

    private static final UnaryFunctionAlias<Long,Long> fib ;
    private static final BinaryPredicate<Long,Long> lequ ;

    static {
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
            super("NullaryPredicateConstant");
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
            super("NullaryFunctionConstant");
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
            super("NullaryFunctionDrop");
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
            super("NullaryPredicateDrop");
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
            super("UnaryFunctionDrop");
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
            super("UnaryPredicateDrop");
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
            super("BinaryFunctionDrop");
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
            super("BinaryPredicateDrop");
            this.func = func;
        }

        @Override
        public void eval(S arg1, T arg2) {
            func.evaluate(arg1, arg2);
        }

        public BinaryPredicate<S,T> func() { return func ; }
    }

    /* Possible enhancement:
     * public static interface UnaryVoidFunctionBind<S>
     *     extends UnaryVoidFunction<S> {
     *     UnaryFunction<S> func() ;
     *     S value() ;
     * }
     *
     * private abstract static class UnaryVoidFunctionBindImpl<S>
     *     extends NullaryVoidFunctionBase
     *     implements UnaryVoidFunctionBind<S> {
     *
     *    @Override
     *    public void eval() {
     *        func().evaluate(value());
     *    }
     * }
     *
     * public static UnaryVoidFunctionBind<S> makeBind(
     *     UnaryVoidFunction<S> func, S value ) {
     *     return FOO.maker( UnaryVoidFunctionBindImpl.class,
     *         "func", func, "value", value ) ;
     * }
     *
     */
    // Bind argument
    public static class UnaryVoidFunctionBindImpl<S>
        extends NullaryVoidFunctionBase {

        private final UnaryVoidFunction<S> func;
        private final S value;

        public UnaryVoidFunctionBindImpl( UnaryVoidFunction<S> func, S value) {
            super("UnaryPredicateBind");
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
            super("UnaryPredicateBind");
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval() {
            return func.evaluate(value);
        }
    }

    public static class UnaryFunctionBindImpl<S,R>
        extends NullaryFunctionBase<R> {

        private final UnaryFunction<S,R> func;
        private final S value;

        public UnaryFunctionBindImpl( UnaryFunction<S,R> func, S value) {
            super("UnaryFunctionBind");
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval() {
            return func.evaluate(value);
        }
    }

    public static class BinaryFunctionBind1Impl<S,T,R>
        extends UnaryFunctionBase<T, R> {

        private final BinaryFunction<S, T, R> func;
        private final S value;

        public BinaryFunctionBind1Impl( BinaryFunction<S, T, R> func, S value) {
            super("BinaryFunctionBind1");
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval(T arg) {
            return func.evaluate(value, arg);
        }
    }

    public static class BinaryVoidFunctionBind1Impl<S,T>
        extends UnaryVoidFunctionBase<T> {

        private final BinaryVoidFunction<S, T> func;
        private final S value;

        public BinaryVoidFunctionBind1Impl( final BinaryVoidFunction<S, T> func,
            S value) {
            super("BinaryVoidFunctionBind1");
            this.func = func;
            this.value = value;
        }

        @Override
        public void eval(T arg) {
            func.evaluate(value, arg);
        }
    }


    public static class BinaryPredicateBind1Impl<S,T>
        extends UnaryPredicateBase<T> {

        private final BinaryPredicate<S, T> func;
        private final S value;

        public BinaryPredicateBind1Impl( final BinaryPredicate<S, T> func,
            S value) {
            super("BinaryVoidFunctionBind1");
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval(T arg) {
            return func.evaluate(value, arg);
        }
    }


    public static class BinaryFunctionBind2Impl<S,T,R>
        extends UnaryFunctionBase<S, R> {

        private final BinaryFunction<S, T, R> func;
        private final T value;

        public BinaryFunctionBind2Impl( BinaryFunction<S, T, R> func, T value) {
            super("BinaryFunctionBind2");
            this.func = func;
            this.value = value;
        }

        @Override
        public R eval(S arg) {
            return func.evaluate(arg, value);
        }
    }

    public static class BinaryVoidFunctionBind2Impl<S,T>
        extends UnaryVoidFunctionBase<S> {

        private final BinaryVoidFunction<S, T> func;
        private final T value;

        public BinaryVoidFunctionBind2Impl( final BinaryVoidFunction<S, T> func,
            T value) {
            super("BinaryVoidFunctionBind2");
            this.func = func;
            this.value = value;
        }

        @Override
        public void eval(S arg) {
            func.evaluate(arg, value);
        }
    }

    public static class BinaryPredicateBind2Impl<S,T>
        extends UnaryPredicateBase<S> {

        private final BinaryPredicate<S, T> func;
        private final T value;

        public BinaryPredicateBind2Impl( final BinaryPredicate<S, T> func,
            T value) {
            super("BinaryVoidFunctionBind2");
            this.func = func;
            this.value = value;
        }

        @Override
        public boolean eval(S arg) {
            return func.evaluate(arg, value);
        }
    }

    public static class UnaryFunctionCompImpl<S,T,R>
        extends UnaryFunctionBase<S, R> {

        private final UnaryFunction<T, R> second;
        private final UnaryFunction<S, T> first;

        public UnaryFunctionCompImpl( UnaryFunction<T, R> second,
            UnaryFunction<S, T> first) {
            super("UnaryFunctionComp");
            this.second = second;
            this.first = first;
        }

        @Override
        public R eval(S arg) {
            return second.evaluate(first.evaluate(arg));
        }
    }

}

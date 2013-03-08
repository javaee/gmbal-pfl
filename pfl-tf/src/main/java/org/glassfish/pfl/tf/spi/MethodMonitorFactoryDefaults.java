/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.pfl.tf.spi;

import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEventController;
import org.glassfish.pfl.tf.timer.spi.TimerManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.pfl.basic.algorithm.Algorithms;
import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.logex.OperationTracer ;

/**
 *
 * @author ken
 */
public class MethodMonitorFactoryDefaults {
    private MethodMonitorFactoryDefaults() {}

    private static Map<String,String> prefixTable =
        new HashMap<String,String>() ;

    /** Add a new Package prefix symbol to the table.  This reduces the
     * size of the generated messages in the dprintImpl output.
     * 
     * @param pkg A Java package name. Should end in '.' (which will be added
     * if not present). 
     * @param symbol The symbol to substitute for the package.  Typically this
     * is 3-8 upper case characters.
     */
    public static void addPrefix( String pkg, String symbol ) {
        String str ;
        if (pkg.charAt( pkg.length() - 1) == '.') {
            str = pkg ;
        } else {
            str = pkg + '.' ;
        }

        prefixTable.put( str, symbol ) ;
    }

    private static String compressClassName( String name ) {
        for (Map.Entry<String,String> entry : prefixTable.entrySet()) {
            if (name.startsWith( entry.getKey() )) {
                return "(" + entry.getValue() + ")." +
                    name.substring( entry.getKey().length() ) ;
            }
        }

        return name ;
    }

    private static MethodMonitorFactory operationTracerImpl =
        new MethodMonitorFactoryBase( "OperationTracerImpl" ) {
            public MethodMonitor create( final Class<?> cls) {
                return new MethodMonitorBase( "OperationTracer", cls, this ) {
                    public void enter(int ident, Object... args) {
                        String mname = MethodMonitorRegistry.getMethodName(
                            cls, ident) ;

                        OperationTracer.enter( mname, args ) ;
                    }

                    public void info(Object[] args, int callerIdent, 
                        int selfIdent ) {
                        // Should we do something here?
                    }

                    public void exit(int ident) {
                        OperationTracer.exit() ;
                    }

                    public void exit(int ident, Object result) {
                        OperationTracer.exit() ;
                    }

                    public void exception(int ident, Throwable thr) { }

                    public void clear() {
                        OperationTracer.clear() ;
                    }
                } ;
            }
        };

    private static MethodMonitorFactory dprintImpl = 
        new MethodMonitorFactoryBase( "DprintImpl" ) {
            private static final boolean USE_LOGGER = false ;

            public MethodMonitor create( final Class<?> cls ) {
                return new MethodMonitorBase( "Dprint", cls, this ) {
                    final String loggerName =
                        USE_LOGGER ? cls.getPackage().getName()
                                   : null ;

                    final String sourceClassName = compressClassName(
                        cls.getName() ) ;

                    public synchronized void dprint(String mname, String msg) {
                        String prefix = "(" + Thread.currentThread().getName()
                            + "): " ;

                        if (USE_LOGGER) {
                            Logger.getLogger( loggerName ).
                                logp( Level.INFO, prefix + msg, sourceClassName,
                                    mname ) ;
                        } else {
                            System.out.println( prefix + sourceClassName
                                + "." + mname + msg ) ;
                        }
                    }

                    private String makeString( Object... args ) {
                        if (args.length == 0) {
                            return "";
                        }

                        StringBuilder sb = new StringBuilder() ;
                        sb.append( '(' ) ;
                        boolean first = true ;
                        for (Object obj : args) {
                            if (first) {
                                first = false ;
                            } else {
                                sb.append( ' ' ) ;
                            }

                            sb.append( Algorithms.convertToString(obj)) ;
                        }
                        sb.append( ')' ) ;

                        return sb.toString() ;
                    }

                    public void enter( int ident, Object... args ) {
                        String mname = MethodMonitorRegistry.getMethodName( cls,
                            ident ) ;
                        String str = makeString( args ) ;
                        dprint( mname, "->" + str ) ;
                    }

                    public void exception( int ident, Throwable thr ) {
                        String mname = MethodMonitorRegistry.getMethodName( cls,
                            ident ) ;
                        dprint( mname, ":throw:" + thr ) ;
                    }

                    public void info( Object[] args, int callerId,
                        int selfId ) {

                        String mname = MethodMonitorRegistry.getMethodName( cls,
                            callerId ) ;
                        String infoName = MethodMonitorRegistry.getMethodName( cls,
                            selfId) ;
                        String str = makeString( args ) ;
                        dprint( mname, "::(" + infoName + ")" + str ) ;
                    }

                    public void exit( int ident ) {
                        String mname = MethodMonitorRegistry.getMethodName( cls,
                            ident ) ;
                        dprint( mname, "<-" ) ;
                    }

                    public void exit( int ident, Object retVal ) {
                        String mname = MethodMonitorRegistry.getMethodName( cls,
                            ident ) ;
                        dprint( mname, "<-(" + retVal + ")" ) ;
                    }

                    public void clear() {
                        // NO-OP
                    }
                } ;
            }
        } ;

    private static MethodMonitorFactory noOpImpl =
        new MethodMonitorFactoryBase( "NoOp" ) {
            public MethodMonitor create(final Class<?> cls) {
                return new MethodMonitorBase( "NoOp", cls, this ) {
                    public void enter(int ident, Object... args) { }

                    public void info(Object[] args, int callerId,
                        int selfId ) { }

                    public void exit(int ident) { }

                    public void exit(int ident, Object result) { }

                    public void exception(int ident, Throwable thr) { }

                    public void clear() { }
                } ;
            }
        } ;

    public static <T> MethodMonitorFactory makeTimingImpl(
        final TimerManager<T> tm ) {

        final String name = "Timing[" + tm.toString() + "]" ;

        return new MethodMonitorFactoryBase( name ) {
            public MethodMonitor create(final Class<?> cls) {
                return new MethodMonitorBase( name, cls, this ) {
                    private TimerEventController tec = tm.controller() ;

                    private final List<Timer> timers =
                        tm.getTimers( cls ) ;

                    private final List<TimingPointType> timerTypes =
                        MethodMonitorRegistry.getTimerTypes( cls ) ;

                    public void enter(int ident, Object... args) { 
                        Timer tp = timers.get( ident ) ;
                        tec.enter( tp ) ;
                    }

                    public void info(Object[] args, int callerId,
                        int selfId ) { 
                        
                        Timer tp = timers.get( selfId ) ;
                        if (tp != null) {
                            TimingPointType tpt = timerTypes.get( selfId ) ;
                            if (tpt == TimingPointType.ENTER) {
                                tec.enter( tp ) ;
                            } else if (tpt == TimingPointType.EXIT) {
                                tec.exit( tp ) ;
                            }
                        }
                    }

                    public void exit(int ident) { 
                        Timer tp = timers.get( ident ) ;
                        tec.exit( tp ) ;
                    }

                    public void exit(int ident, Object result) { 
                        Timer tp = timers.get( ident ) ;
                        tec.exit( tp ) ;
                    }

                    public void exception(int ident, Throwable thr) { 
                        Timer tp = timers.get( ident ) ;
                        tec.exit( tp ) ;
                    }

                    public void clear() { }
                } ;
            }
        } ;
    }

    public static MethodMonitorFactory operationTracer() {
        return operationTracerImpl ;
    }

    public static MethodMonitorFactory noOp() {
        return noOpImpl ;
    }

    public static MethodMonitorFactory dprint() {
        return dprintImpl ;
    }

    static MethodMonitor composeMM( final List<MethodMonitor> mms ) {
        if (mms.isEmpty()) {
            return noOpImpl.create( null ) ;
        }

        if (mms.size() == 1) {
            for (MethodMonitor mm : mms) {
                return mm ;
            }
        }

        final Set<MethodMonitorFactory> factories = new HashSet<MethodMonitorFactory>() ;
        Class<?> cls = null ;
        for (MethodMonitor mm: mms) {
            factories.add( mm.factory() )  ;
            Class<?> mmClass = mm.myClass() ;
            if (cls == null) {
                cls = mmClass ;
            } else if (!cls.equals( mmClass )) {
                throw new RuntimeException( "MethodMonitors not composable: "
                    + "not all instantiated from the same class") ;
            }
        }
        final MethodMonitorFactory mmf = compose(factories) ;

        final StringBuilder sb = new StringBuilder( "compose(" ) ;
        boolean first = true ;
        for (MethodMonitor mm : mms ) {
            if (first == true) {
                first = false ;
            } else {
                sb.append( ',' ) ;
            }

            sb.append( mm.name() ) ;
        }

        final String name = sb.toString() ;

        return new MethodMonitorBase( name, cls, mmf ) {
            public void enter(int ident, Object... args) {
                for (MethodMonitor mm : mms) {
                    mm.enter( ident, args ) ;
                }
            }

            public void info( Object[] args, int callerId,
                int selfId ) {

                for (MethodMonitor mm : mms) {
                    mm.info( args, callerId, selfId ) ;
                }
            }

            public void exit(int ident) {
                for (MethodMonitor mm : mms) {
                    mm.exit( ident ) ;
                }
            }

            public void exit(int ident, Object result) {
                for (MethodMonitor mm : mms) {
                    mm.exit( ident, result ) ;
                }
            }

            public void exception(int ident, Throwable thr) {
                for (MethodMonitor mm : mms) {
                    mm.exception( ident, thr ) ;
                }
            }

            public void clear() {
                for (MethodMonitor mm : mms) {
                    mm.clear() ;
                }
            }
        } ;
    }

    public static MethodMonitorFactory compose(
        final Collection<MethodMonitorFactory> factories ) {

        if (factories.isEmpty()) {
            return noOpImpl ;
        }

        if (factories.size() == 1) {
            for (MethodMonitorFactory mmf : factories) {
                return mmf ;
            }
        }

        final Set<MethodMonitorFactory> mmfs =
            new HashSet<MethodMonitorFactory>() ;
        for (MethodMonitorFactory mmf : factories ) {
            mmfs.addAll( mmf.contents() ) ;
        }

        final StringBuilder sb = new StringBuilder( "compose(" ) ;
        boolean first = true ;
        for (MethodMonitorFactory mmf : mmfs ) {
            if (first == true) {
                first = false ;
            } else {
                sb.append( ',' ) ;
            }

            sb.append( mmf.name() ) ;
        }

        final String name = sb.toString() ;

        return new MethodMonitorFactoryBase( name, mmfs ) {
            public MethodMonitor create(final Class<?> cls) {
                final List<MethodMonitor> mms = new ArrayList<MethodMonitor>() ;
                for (MethodMonitorFactory mmf : contents()) {
                    mms.add( mmf.create( cls ) ) ;
                }

                return composeMM( mms ) ;
            }
        } ;
    }
}

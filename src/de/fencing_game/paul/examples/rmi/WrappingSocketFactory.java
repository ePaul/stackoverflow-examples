package de.fencing_game.paul.examples.rmi;

import java.io.*;
import java.net.*;
import java.rmi.server.*;


/**
 * A base class for RMI socket factories which do their
 * work by wrapping the streams of Sockets from another
 * Socket factory.
 *
 * Subclasses have to overwrite the {@link #wrap} method.
 *
 * Instances of this class can be used as both client and
 * server socket factories, or as only one of them.
 */
public abstract class WrappingSocketFactory 
    extends RMISocketFactory
    implements Serializable
{


    /**
     * A simple holder class for a pair of streams.
     */
    public static class StreamPair {
        public InputStream input;
        public OutputStream output;
        public StreamPair(InputStream in, OutputStream out) {
            this.input = in; this.output = out;
        }
    }

    /**
     * The base client socket factory. This will be serialized.
     */
    private RMIClientSocketFactory baseCFactory;

    /**
     * The base server socket factory. This will not be serialized,
     * since the server socket factory is used only on the server side.
     */
    private transient RMIServerSocketFactory baseSFactory;

    private static final long serialVersionUID = 1;

    // --------------------------


    /**
     * Creates a WrappingSocketFactory based on a pair of
     * socket factories.
     *
     * @param cFac the base socket factory used for creating client
     *   sockets. This may be {@code null}, then we will use the
     *  {@linkplain RMISocketFactory#getDefault() default socket factory}
     *  of client system where this object is finally used for
     *   creating sockets.
     *   If not null, it should be serializable.
     * @param sFac the base socket factory used for creating server
     *   sockets. This may be {@code null}, then we will use the
     *  {@linkplain RMISocketFactory#getDefault() default RMI Socket factory}.
     *  This will not be serialized to the client.
     */
    public WrappingSocketFactory(RMIClientSocketFactory cFac,
                                 RMIServerSocketFactory sFac) {
        this.baseCFactory = cFac;
        this.baseSFactory = sFac;
    }


    /**
     * Creates a WrappingSocketFactory based on a socket factory.
     *
     * This constructor is equivalent to
     * {@code WrappingSocketFactory(fac, fac)}.
     *
     * @param fac the factory to be used as a base for both client and
     *   server socket. This should be either serializable or {@code null}
     *   (then we will use the
     * {@linkplain RMISocketFactory#getDefault() default socket factory}
     *   as a base).
     */
    public WrappingSocketFactory(RMISocketFactory fac) {
        this(fac, fac);
    }

    /**
     * Creates a WrappingSocketFactory based on the
     * {@link RMISocketFactory#getSocketFactory global socket factory}.
     *
     * This uses the global socket factory at the time of the constructor
     * call. If this is {@code null}, we will use the
     * {@linkplain RMISocketFactory#getDefault() default socket factory}
     * instead.
     */
    public WrappingSocketFactory() {
        this( RMISocketFactory.getSocketFactory());
    }


    /**
     * Wraps a pair of streams.
     * Subclasses must implement this method do do the actual
     * work to be done.
     * @param input the input stream from the base socket.
     * @param output the output stream to the base socket.
     * @param server if true, we are constructing a socket in
     *    {@link ServerSocket#accept}. If false, this is a pure
     *   client socket.
     */
    protected abstract StreamPair wrap(InputStream input,
                                       OutputStream output,
                                       boolean server);


    /**
     * returns the current base client socket factory.
     * This is either the factory given to the constructor
     * (if not {@code null}) or the default RMI socket factory.
     */
    private RMIClientSocketFactory getCSFac() {
        if(baseCFactory == null) {
            return RMISocketFactory.getDefaultSocketFactory();
        }
        return baseCFactory;
    }

    /**
     * returns the current base server socket factory.
     * This is either the factory given to the constructor
     * (if not {@code null}) or the default RMI socket factory.
     */
    private RMIServerSocketFactory getSSFac() {
        if(baseSFactory == null) {
            return RMISocketFactory.getDefaultSocketFactory();
        }
        return baseSFactory;
    }

    /**
     * Creates a client socket and connects it to the given host/port pair.
     *
     * This retrieves a socket to the host/port from the base client
     * socket factory and then wraps a new socket (with a custom SocketImpl)
     * around it.
     * @param host the host we want to be connected with.
     * @param port the port we want to be connected with.
     * @return a new Socket connected to the host/port pair.
     * @throws IOException if something goes wrong.
     */
    public Socket createSocket(String host, int port)
        throws IOException
    {
        System.err.println("createSocket(" + host + ", " + port + ")");
        Socket baseSocket = getCSFac().createSocket(host, port);
        StreamPair streams = this.wrap(baseSocket.getInputStream(),
                                       baseSocket.getOutputStream(),
                                       false);
        SocketImpl wrappingImpl = new WrappingSocketImpl(streams, baseSocket);
        return new Socket(wrappingImpl) {
            public boolean isConnected() { return true; }
        };
    }

    /**
     * Creates a server socket listening on the given port.
     *
     * This retrieves a ServerSocket listening on the given port
     * from the base server socket factory, and then creates a 
     * custom server socket, which on {@link ServerSocket#accept accept}
     * wraps new Sockets (with a custom SocketImpl) around the sockets
     * from the base server socket.
     * @param host the host we want to be connected with.
     * @param port the port we want to be connected with.
     * @return a new Socket connected to the host/port pair.
     * @throws IOException if something goes wrong.
     */
    public ServerSocket createServerSocket(int port)
        throws IOException
    {
        System.err.println("createServerSocket(" + port + ")");
        final ServerSocket baseSocket = getSSFac().createServerSocket(port);
        ServerSocket ss = new WrappingServerSocket(baseSocket);
        System.err.println(" => " + ss);
        return ss;
    }

    /**
     * A server socket subclass which wraps our custom sockets around the
     * sockets retrieves by a base server socket.
     *
     * We only override enough methods to work. Basically, this is
     * a unbound server socket, which handles {@link #accept} specially.
     */
    private class WrappingServerSocket extends ServerSocket {
        private ServerSocket base;

        public WrappingServerSocket(ServerSocket b)
            throws IOException
        {
            this.base = b;
        }

        /**
         * returns the local port this ServerSocket is bound to.
         */
        public int getLocalPort() {
            return base.getLocalPort();
        }

        /**
         * accepts a connection from some remote host.
         * This will accept a socket from the base socket, and then
         * wrap a new custom socket around it.
         */
        public Socket accept() throws IOException {
            System.err.println(this+".accept()");
            final Socket baseSocket = base.accept();
            System.err.println("baseSocket: " + baseSocket);
            StreamPair streams =
                WrappingSocketFactory.this.wrap(baseSocket.getInputStream(),
                                                baseSocket.getOutputStream(),
                                                true);
            SocketImpl wrappingImpl =
                new WrappingSocketImpl(streams, baseSocket);
            System.err.println("wrappingImpl: " + wrappingImpl);

            // For some reason, this seems to work only as a
            // anonymous direct subclass of Socket, not as a
            // external subclass.      Strange.
            Socket result = new Socket(wrappingImpl) {
                    public boolean isConnected() { return true; }
                    public boolean isBound() { return true; }
                    public int getLocalPort() {
                        return baseSocket.getLocalPort();
                    }
                    public InetAddress getLocalAddress() {
                        return baseSocket.getLocalAddress();
                    }
                };
            System.err.println("result: " + result);
            return result;
        }
    }

    /**
     * A SocketImpl implementation which works on a pair
     * of streams.
     *
     * A instance of this class represents an already
     * connected socket, thus all the methods relating to
     * connecting, accepting and such are not implemented.
     *
     * The implemented methods are {@link #getInputStream},
     * {@link #getOutputStream}, {@link #available} and the
     * shutdown methods {@link #close}, {@link #shutdownInput},
     * {@link #shutdownOutput}.
     */
    private static class WrappingSocketImpl extends SocketImpl {
        private InputStream inStream;
        private OutputStream outStream;

        private Socket base;
        
        WrappingSocketImpl(StreamPair pair, Socket base) {
            System.err.println("new WrappingSocketImpl()");
            this.inStream = pair.input;
            this.outStream = pair.output;
            this.base = base;
        }


        protected InputStream getInputStream() {
            return inStream;
        }

        protected OutputStream getOutputStream() {
            return outStream;
        }

        protected void close() throws IOException {
            base.close();
        }

        protected int available() throws IOException {
            System.err.println("available()");
            return inStream.available();
        }


        protected void shutdownInput() throws IOException {
            base.shutdownInput();
            // TODO: inStream.close() ?
        }

        protected void shutdownOutput() throws IOException {
            base.shutdownOutput();
            // TODO: outStream.close()?
        }

         
        // only logging

        protected void create(boolean stream) {
            System.err.println("create(" + stream + ")");
        }


        public Object getOption(int optID) {
            System.err.println("getOption(" + optID + ")");
            return null;
        }

        public void setOption(int optID, Object value) {
            System.err.println("setOption(" + optID +"," + value + ")");
            // noop.
        }

        // unsupported operations


        protected void connect(String host, int port) {
            System.err.println("connect(" + host + ", " + port + ")");
            throw new UnsupportedOperationException();
        }


        protected void connect(InetAddress address, int port) {
            System.err.println("connect(" + address + ", " + port + ")");
            throw new UnsupportedOperationException();
        }

        protected void connect(SocketAddress addr, int timeout) {
            System.err.println("connect(" + addr + ", " + timeout + ")");
            throw new UnsupportedOperationException();
        }

        protected void bind(InetAddress host, int port) {
            System.err.println("bind(" + host + ", " + port + ")");
            throw new UnsupportedOperationException();
        }

        protected void listen(int backlog) {
            System.err.println("listen(" + backlog + ")");
            throw new UnsupportedOperationException();
        }

        protected void accept(SocketImpl otherSide) {
            System.err.println("accept(" + otherSide + ")");
            throw new UnsupportedOperationException();
        }

        protected void sendUrgentData(int data) {
            System.err.println("sendUrgentData()");
            throw new UnsupportedOperationException();
        }
    }
}

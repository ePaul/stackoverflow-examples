package de.fencing_game.paul.examples.rmi;

import java.io.*;
import java.net.*;
import java.rmi.server.*;


public abstract class WrappingSocketFactory 
    implements RMIClientSocketFactory, RMIServerSocketFactory, Serializable
{

    public WrappingSocketFactory(RMIClientSocketFactory cFac,
                                 RMIServerSocketFactory sFac) {
        this.baseCFactory = cFac;
        this.baseSFactory = sFac;
    }

    public WrappingSocketFactory(RMISocketFactory fac) {
        this(fac, fac);
    }

    public WrappingSocketFactory() {
        this( RMISocketFactory.getSocketFactory());
    }

    public static class StreamPair {
        public InputStream input;
        public OutputStream output;
        public StreamPair(InputStream in, OutputStream out) {
            this.input = in; this.output = out;
        }
    }

    RMIClientSocketFactory baseCFactory;
    RMIServerSocketFactory baseSFactory;

    protected abstract StreamPair wrap(InputStream input,
                                       OutputStream output,
                                       boolean server);
    
    private RMIClientSocketFactory getCSFac() {
        if(baseCFactory == null) {
            return RMISocketFactory.getDefaultSocketFactory();
        }
        return baseCFactory;
    }

    private RMIServerSocketFactory getSSFac() {
        if(baseSFactory == null) {
            return RMISocketFactory.getDefaultSocketFactory();
        }
        return baseSFactory;
    }


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

    public ServerSocket createServerSocket(int port)
        throws IOException
    {
        System.err.println("createServerSocket(" + port + ")");
        final ServerSocket baseSocket = getSSFac().createServerSocket(port);
        port = baseSocket.getLocalPort();
        ServerSocket ss = new WrappingServerSocket(baseSocket, port);
        System.err.println(" => " + ss);
        return ss;
    }

    private class WrappingServerSocket extends ServerSocket {
        private ServerSocket base;
        private int port;

        public WrappingServerSocket(ServerSocket b, int port)
            throws IOException
        {
            this.base = b;
            this.port = port;
        }

        public int getLocalPort() {
            return port;
        }

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
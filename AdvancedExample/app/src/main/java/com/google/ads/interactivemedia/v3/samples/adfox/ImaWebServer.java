package com.google.ads.interactivemedia.v3.samples.adfox;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;

import fi.iki.elonen.InternalRewrite;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebServerPlugin;
import fi.iki.elonen.WebServerPluginInfo;
import fi.iki.elonen.util.ServerRunner;


public class ImaWebServer extends NanoHTTPD {
    public static final List<String> INDEX_FILE_NAMES = new ArrayList() {
        {
            this.add("index.html");
            this.add("index.htm");
        }
    };
    private static final String LICENCE;
    private static Map<String, WebServerPlugin> mimeTypeHandlers;
    private final boolean quiet;
    private final String cors;
    protected List<File> rootDirs;
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final int MAX_AGE = 151200;
    static final String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
    public static final String ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME = "AccessControlAllowHeader";

    public static void main(String[] args) {
        int port = 8080;
        String host = null;
        ArrayList rootDirs = new ArrayList();
        boolean quiet = false;
        String cors = null;
        HashMap options = new HashMap();

        for(int sb = 0; sb < args.length; ++sb) {
            if(!"-h".equalsIgnoreCase(args[sb]) && !"--host".equalsIgnoreCase(args[sb])) {
                if(!"-p".equalsIgnoreCase(args[sb]) && !"--port".equalsIgnoreCase(args[sb])) {
                    if(!"-q".equalsIgnoreCase(args[sb]) && !"--quiet".equalsIgnoreCase(args[sb])) {
                        if(!"-d".equalsIgnoreCase(args[sb]) && !"--dir".equalsIgnoreCase(args[sb])) {
                            int serviceLoader;
                            if(args[sb].startsWith("--cors")) {
                                cors = "*";
                                serviceLoader = args[sb].indexOf(61);
                                if(serviceLoader > 0) {
                                    cors = args[sb].substring(serviceLoader + 1);
                                }
                            } else if("--licence".equalsIgnoreCase(args[sb])) {
                                System.out.println(LICENCE + "\n");
                            } else if(args[sb].startsWith("-X:")) {
                                serviceLoader = args[sb].indexOf(61);
                                if(serviceLoader > 0) {
                                    String dir = args[sb].substring(0, serviceLoader);
                                    String info = args[sb].substring(serviceLoader + 1, args[sb].length());
                                    options.put(dir, info);
                                }
                            }
                        } else {
                            rootDirs.add((new File(args[sb + 1])).getAbsoluteFile());
                        }
                    } else {
                        quiet = true;
                    }
                } else {
                    port = Integer.parseInt(args[sb + 1]);
                }
            } else {
                host = args[sb + 1];
            }
        }

        if(rootDirs.isEmpty()) {
            rootDirs.add((new File(".")).getAbsoluteFile());
        }

        options.put("host", host);
        options.put("port", "" + port);
        options.put("quiet", String.valueOf(quiet));
        StringBuilder var22 = new StringBuilder();
        Iterator var23 = rootDirs.iterator();

        while(var23.hasNext()) {
            File var25 = (File)var23.next();
            if(var22.length() > 0) {
                var22.append(":");
            }

            try {
                var22.append(var25.getCanonicalPath());
            } catch (IOException var21) {
                ;
            }
        }

        options.put("home", var22.toString());
        ServiceLoader var24 = ServiceLoader.load(WebServerPluginInfo.class);
        Iterator var26 = var24.iterator();

        while(var26.hasNext()) {
            WebServerPluginInfo var27 = (WebServerPluginInfo)var26.next();
            String[] mimeTypes = var27.getMimeTypes();
            String[] var12 = mimeTypes;
            int var13 = mimeTypes.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                String mime = var12[var14];
                String[] indexFiles = var27.getIndexFilesForMimeType(mime);
                if(!quiet) {
                    System.out.print("# Found plugin for Mime type: \"" + mime + "\"");
                    if(indexFiles != null) {
                        System.out.print(" (serving index files: ");
                        String[] var17 = indexFiles;
                        int var18 = indexFiles.length;

                        for(int var19 = 0; var19 < var18; ++var19) {
                            String indexFile = var17[var19];
                            System.out.print(indexFile + " ");
                        }
                    }

                    System.out.println(").");
                }

                registerPluginForMimeType(indexFiles, mime, var27.getWebServerPlugin(mime), options);
            }
        }

        ServerRunner.executeInstance(new fi.iki.elonen.SimpleWebServer(host, port, rootDirs, quiet, cors));
    }

    protected static void registerPluginForMimeType(String[] indexFiles, String mimeType, WebServerPlugin plugin, Map<String, String> commandLineOptions) {
        if(mimeType != null && plugin != null) {
            if(indexFiles != null) {
                String[] var4 = indexFiles;
                int var5 = indexFiles.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String filename = var4[var6];
                    int dot = filename.lastIndexOf(46);
                    if(dot >= 0) {
                        String extension = filename.substring(dot + 1).toLowerCase();
                        mimeTypes().put(extension, mimeType);
                    }
                }

                INDEX_FILE_NAMES.addAll(Arrays.asList(indexFiles));
            }

            mimeTypeHandlers.put(mimeType, plugin);
            plugin.initialize(commandLineOptions);
        }
    }

    public ImaWebServer(String host, int port, File wwwroot, boolean quiet, String cors) {
        this(host, port, Collections.singletonList(wwwroot), quiet, cors);
    }

    public ImaWebServer(String host, int port, File wwwroot, boolean quiet) {
        this(host, port, (List)Collections.singletonList(wwwroot), quiet, (String)null);
    }

    public ImaWebServer(String host, int port, List<File> wwwroots, boolean quiet) {
        this(host, port, (List)wwwroots, quiet, (String)null);
    }

    public ImaWebServer(String host, int port, List<File> wwwroots, boolean quiet, String cors) {
        super(host, port);
        this.quiet = quiet;
        this.cors = cors;
        this.rootDirs = new ArrayList(wwwroots);
        this.init();
    }

    private boolean canServeUri(String uri, File homeDir) {
        File f = new File(homeDir, uri);
        boolean canServeUri = f.exists();
        if(!canServeUri) {
            WebServerPlugin plugin = (WebServerPlugin)mimeTypeHandlers.get(getMimeTypeForFile(uri));
            if(plugin != null) {
                canServeUri = plugin.canServeUri(uri, homeDir);
            }
        }

        return canServeUri;
    }

    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);

        while(st.hasMoreTokens()) {
            String tok = st.nextToken();
            if("/".equals(tok)) {
                newUri = newUri + "/";
            } else if(" ".equals(tok)) {
                newUri = newUri + "%20";
            } else {
                try {
                    newUri = newUri + URLEncoder.encode(tok, "UTF-8");
                } catch (UnsupportedEncodingException var6) {
                    ;
                }
            }
        }

        return newUri;
    }

    private String findIndexFileInDirectory(File directory) {
        Iterator var2 = INDEX_FILE_NAMES.iterator();

        String fileName;
        File indexFile;
        do {
            if(!var2.hasNext()) {
                return null;
            }

            fileName = (String)var2.next();
            indexFile = new File(directory, fileName);
        } while(!indexFile.isFile());

        return fileName;
    }

    protected Response getForbiddenResponse(String s) {
        return newFixedLengthResponse(Response.Status.FORBIDDEN, "text/plain", "FORBIDDEN: " + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "INTERNAL ERROR: " + s);
    }

    protected Response getNotFoundResponse() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Error 404, file not found.");
    }

    public void init() {
    }

    protected String listDirectory(String uri, File f) {
        String heading = "Directory " + uri;
        StringBuilder msg = new StringBuilder("<html><head><title>" + heading + "</title><style><!--\n" + "span.dirname { font-weight: bold; }\n" + "span.filesize { font-size: 75%; }\n" + "// -->\n" + "</style>" + "</head><body><h1>" + heading + "</h1>");
        String up = null;
        if(uri.length() > 1) {
            String files = uri.substring(0, uri.length() - 1);
            int directories = files.lastIndexOf(47);
            if(directories >= 0 && directories < files.length()) {
                up = uri.substring(0, directories + 1);
            }
        }

        List files1 = Arrays.asList(f.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (new File(dir, name)).isFile();
            }
        }));
        Collections.sort(files1);
        List directories1 = Arrays.asList(f.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (new File(dir, name)).isDirectory();
            }
        }));
        Collections.sort(directories1);
        if(up != null || directories1.size() + files1.size() > 0) {
            msg.append("<ul>");
            Iterator var8;
            String file;
            if(up != null || directories1.size() > 0) {
                msg.append("<section class=\"directories\">");
                if(up != null) {
                    msg.append("<li><a rel=\"directory\" href=\"").append(up).append("\"><span class=\"dirname\">..</span></a></b></li>");
                }

                var8 = directories1.iterator();

                while(var8.hasNext()) {
                    file = (String)var8.next();
                    String curFile = file + "/";
                    msg.append("<li><a rel=\"directory\" href=\"").append(this.encodeUri(uri + curFile)).append("\"><span class=\"dirname\">").append(curFile).append("</span></a></b></li>");
                }

                msg.append("</section>");
            }

            if(files1.size() > 0) {
                msg.append("<section class=\"files\">");

                for(var8 = files1.iterator(); var8.hasNext(); msg.append(")</span></li>")) {
                    file = (String)var8.next();
                    msg.append("<li><a href=\"").append(this.encodeUri(uri + file)).append("\"><span class=\"filename\">").append(file).append("</span></a>");
                    File curFile1 = new File(f, file);
                    long len = curFile1.length();
                    msg.append("&nbsp;<span class=\"filesize\">(");
                    if(len < 1024L) {
                        msg.append(len).append(" bytes");
                    } else if(len < 1048576L) {
                        msg.append(len / 1024L).append(".").append(len % 1024L / 10L % 100L).append(" KB");
                    } else {
                        msg.append(len / 1048576L).append(".").append(len % 1048576L / 10000L % 100L).append(" MB");
                    }
                }

                msg.append("</section>");
            }

            msg.append("</ul>");
        }

        msg.append("</body></html>");
        return msg.toString();
    }

    public static Response newFixedLengthResponse(Response.IStatus status, String mimeType, String message) {
        Response response = NanoHTTPD.newFixedLengthResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response respond(Map<String, String> headers, IHTTPSession session, String uri) {
        Response r;
        if(this.cors != null && Method.OPTIONS.equals(session.getMethod())) {
            r = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", (InputStream)null, 0L);
        } else {
            r = this.defaultRespond(headers, session, uri);
        }

        if(this.cors != null) {
            r = this.addCORSHeaders(headers, r, this.cors);
        }

        return r;
    }

    private Response defaultRespond(Map<String, String> headers, IHTTPSession session, String uri) {
        uri = uri.trim().replace(File.separatorChar, '/');
        if(uri.indexOf(63) >= 0) {
            uri = uri.substring(0, uri.indexOf(63));
        }

        if(uri.contains("../")) {
            return this.getForbiddenResponse("Won\'t serve ../ for security reasons.");
        } else {
            boolean canServeUri = false;
            File homeDir = null;

            for(int f = 0; !canServeUri && f < this.rootDirs.size(); ++f) {
                homeDir = (File)this.rootDirs.get(f);
                canServeUri = this.canServeUri(uri, homeDir);
            }

            if(!canServeUri) {
                return this.getNotFoundResponse();
            } else {
                File var11 = new File(homeDir, uri);
                if(var11.isDirectory() && !uri.endsWith("/")) {
                    uri = uri + "/";
                    Response var12 = newFixedLengthResponse(Response.Status.REDIRECT, "text/html", "<html><body>Redirected: <a href=\"" + uri + "\">" + uri + "</a></body></html>");
                    var12.addHeader("Location", uri);
                    return var12;
                } else {
                    String mimeTypeForFile;
                    if(var11.isDirectory()) {
                        mimeTypeForFile = this.findIndexFileInDirectory(var11);
                        return mimeTypeForFile == null?(var11.canRead()?newFixedLengthResponse(Response.Status.OK, "text/html", this.listDirectory(uri, var11)):this.getForbiddenResponse("No directory listing.")):this.respond(headers, session, uri + mimeTypeForFile);
                    } else {
                        mimeTypeForFile = getMimeTypeForFile(uri);
                        WebServerPlugin plugin = (WebServerPlugin)mimeTypeHandlers.get(mimeTypeForFile);
                        Response response = null;
                        if(plugin != null && plugin.canServeUri(uri, homeDir)) {
                            response = plugin.serveFile(uri, headers, session, var11, mimeTypeForFile);
                            if(response != null && response instanceof InternalRewrite) {
                                InternalRewrite rewrite = (InternalRewrite)response;
                                return this.respond(rewrite.getHeaders(), session, rewrite.getUri());
                            }
                        } else {
                            response = this.serveFile(uri, headers, var11, mimeTypeForFile);
                        }

                        return response != null?response:this.getNotFoundResponse();
                    }
                }
            }
        }
    }

    public Response serve(IHTTPSession session) {
        Map header = session.getHeaders();
        Map parms = session.getParms();
        String uri = session.getUri();
        Iterator e;
        if(!this.quiet) {
            System.out.println(session.getMethod() + " \'" + uri + "\' ");
            e = header.keySet().iterator();

            String homeDir;
            while(e.hasNext()) {
                homeDir = (String)e.next();
                System.out.println("  HDR: \'" + homeDir + "\' = \'" + (String)header.get(homeDir) + "\'");
            }

            e = parms.keySet().iterator();

            while(e.hasNext()) {
                homeDir = (String)e.next();
                System.out.println("  PRM: \'" + homeDir + "\' = \'" + (String)parms.get(homeDir) + "\'");
            }
        }

        e = this.rootDirs.iterator();

        File homeDir1;
        do {
            if(!e.hasNext()) {
                return this.respond(Collections.unmodifiableMap(header), session, uri);
            }

            homeDir1 = (File)e.next();
        } while(homeDir1.isDirectory());

        return this.getInternalErrorResponse("given path is not a directory (" + homeDir1 + ").");
    }

    Response serveFile(String uri, Map<String, String> header, File file, String mime) {
        Response res;
        try {
            String ioe = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());
            long startFrom = 0L;
            long endAt = -1L;
            String range = (String)header.get("range");
            if(range != null && range.startsWith("bytes=")) {
                range = range.substring("bytes=".length());
                int ifRange = range.indexOf(45);

                try {
                    if(ifRange > 0) {
                        startFrom = Long.parseLong(range.substring(0, ifRange));
                        endAt = Long.parseLong(range.substring(ifRange + 1));
                    }
                } catch (NumberFormatException var21) {
                    ;
                }
            }

            String ifRange1 = (String)header.get("if-range");
            boolean headerIfRangeMissingOrMatching = ifRange1 == null || ioe.equals(ifRange1);
            String ifNoneMatch = (String)header.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*".equals(ifNoneMatch) || ifNoneMatch.equals(ioe));
            long fileLen = file.length();
            if(headerIfRangeMissingOrMatching && range != null && startFrom >= 0L && startFrom < fileLen) {
                if(headerIfNoneMatchPresentAndMatching) {
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", ioe);
                } else {
                    if(endAt < 0L) {
                        endAt = fileLen - 1L;
                    }

                    long newLen = endAt - startFrom + 1L;
                    if(newLen < 0L) {
                        newLen = 0L;
                    }

                    FileInputStream fis = new FileInputStream(file);
                    fis.skip(startFrom);
                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, fis, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", ioe);
                }
            } else if(headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, "text/plain", "");
                res.addHeader("Content-Range", "bytes */" + fileLen);
                res.addHeader("ETag", ioe);
            } else if(range == null && headerIfNoneMatchPresentAndMatching) {
                res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                res.addHeader("ETag", ioe);
            } else if(!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                res.addHeader("ETag", ioe);
            } else {
                res = this.newFixedFileResponse(file, mime);
                res.addHeader("Content-Length", "" + fileLen);
                res.addHeader("ETag", ioe);
            }
        } catch (IOException var22) {
            res = this.getForbiddenResponse("Reading file failed.");
        }

        return res;
    }

    private Response newFixedFileResponse(File file, String mime) throws FileNotFoundException {
        Response res = newFixedLengthResponse(Response.Status.OK, mime, new FileInputStream(file), (long)((int)file.length()));
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    protected Response addCORSHeaders(Map<String, String> queryHeaders, Response resp, String cors) {
        resp.addHeader("Access-Control-Allow-Origin", cors);
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Headers", this.calculateAllowHeaders(queryHeaders));
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        resp.addHeader("Access-Control-Max-Age", "151200");
        return resp;
    }

    private String calculateAllowHeaders(Map<String, String> queryHeaders) {
        return System.getProperty("AccessControlAllowHeader", "origin,accept,content-type");
    }

    static {
        mimeTypes();
        InputStream stream = fi.iki.elonen.SimpleWebServer.class.getResourceAsStream("/LICENSE.txt");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        String text;
        try {
            int count;
            while((count = stream.read(buffer)) >= 0) {
                bytes.write(buffer, 0, count);
            }

            text = bytes.toString("UTF-8");
        } catch (Exception var6) {
            text = "unknown";
        }

        LICENCE = text;
        mimeTypeHandlers = new HashMap();
    }
}

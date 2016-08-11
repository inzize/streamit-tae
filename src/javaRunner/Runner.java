package javaRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.*;

public class Runner {

    private final static Logger logger = Logger.getLogger("compiler");
    ByteArrayOutputStream bytecode = new ByteArrayOutputStream();

    public Class Load(String className, String javaCode) throws ClassNotFoundException, Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            logger.severe("Could not find a suitable java compiler");
            System.exit(-1);
        }
        JavaFileManager fileManager = new ForwardingJavaFileManager<JavaFileManager>(compiler.getStandardFileManager(null, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, final String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                return new SimpleJavaFileObject(URI.create(className), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        return bytecode;
                    }
                };
            }

            @Override
            public JavaFileObject getJavaFileForInput(JavaFileManager.Location lctn, String string, JavaFileObject.Kind kind) throws IOException {
                return null;
            }
        };

        Listener listener = null;

        if (compiler.getTask(null, fileManager, listener, null, null, Arrays.asList(new JavaSourceFromString(className, javaCode))).call()) {
            ClassLoader loader = new CustomClassLoader(Runner.class.getClassLoader(), className, bytecode.toByteArray());
            return loader.loadClass(className);
        } else {
            System.err.println(javaCode);
            throw new Exception("Init Code generation problem");
        }
    }
}

class Listener implements javax.tools.DiagnosticListener<javax.tools.JavaFileObject> {

    private final static Logger logger = Logger.getLogger("compiler");

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        //if ("ERROR".equals(diagnostic.getKind().toString()))
        logger.log(Level.WARNING, "{0}", diagnostic.getMessage(null));
    }
}

class CustomClassLoader extends ClassLoader {

    byte[] bytecode;
    ClassLoader standard;
    String className;

    public CustomClassLoader(ClassLoader standard, String className, byte[] bytecode) {
        super(CustomClassLoader.class.getClassLoader());
        this.bytecode = bytecode;
        this.standard = standard;
        this.className = className;
    }

    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        if (className == null ? this.className == null : className.equals(this.className)) {
            return defineClass(className, bytecode, 0, bytecode.length, null);
        } else {
            return standard.loadClass(className);
        }
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {

    private final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

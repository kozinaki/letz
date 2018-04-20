package net.kozinaki.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

/**
* Build automation tool class.
* @author kozinaki
*/
public class Letz {

    /**
    * Available actions.
    */
    enum Action {

        COMPILE("compile"), /* compile project */
        RUN("run"),         /* run project */
        CLEAN("clean"),     /* clean project */
        UNKNOWN("unknown");
        
        private String value;
        
        Action(String action) {
            this.value = action;
        }

        static Action getAction(String value) {
            for(Action action : Action.values()) {
                if(value.equals(action.value))
                    return action;
            }
            return UNKNOWN;
        }

    }

    private static String BUILD_FILE = "xyz.ini";
    private static String SOURCES_PROPERTY = "sources";
    private static String RESOURCES_PROPERTY = "resources";
    private static String CLASS_PROPERTY = "class";
    private static String BUILD_PROPERTY = "build";

    private static Properties properties;

    private static String _source;
    private static String _resources;
    private static String _clazz;
    private static String _build;

    /**
    * Entry point.
    * @param args action that tool must do
    *           {compile, run, clean}
    */
    public static void main(String args[]) {
        try {
            properties = new Properties();
            FileInputStream in = new FileInputStream(BUILD_FILE);
            properties.load(in);
            in.close();
            _source = properties.getProperty(SOURCES_PROPERTY);
            _resources = properties.getProperty(RESOURCES_PROPERTY);
            _clazz = properties.getProperty(CLASS_PROPERTY);
            _build = properties.getProperty(BUILD_PROPERTY);
        } catch (IOException e) {
            System.out.println("\nYou must have xyz.ini file to continue.");
            return;
        }
        Action action;
        if(args.length != 0) {
            action = Action.getAction(args[0]);
        } else {
            printArgumentsException();
            return;
        }
        switch (action) {
            case COMPILE:
                compile();
                break;
            case RUN:
                run();
                break;
            case CLEAN:
                clean();
                break;
            default:
                printArgumentsException();
                break;
        }
    }

    private static void printArgumentsException() {
        System.out.println("\nYou must type some of arguments: \n\tcompile \n\trun \n\tclean");
    }

    /**
    * Compile project; That method response for action COMPILE.
    */
    private static void compile() {
        try {
            File buildDirectory = new File(_build);
            buildDirectory.mkdirs();

            List<String> sources = new ArrayList<>();
            String packagePath = _clazz.replace(".", "/");
            File sourceDirectory = new File(_source
                                .concat("/")
                                .concat(packagePath.substring(0, packagePath.lastIndexOf("/"))));
            findSources(sources, sourceDirectory);

            String[] procAndArgs = new String[] {"javac", "-d", _build};
            String[] finalLine = new String[procAndArgs.length + sources.size()];
            System.arraycopy(procAndArgs, 0, finalLine, 0, procAndArgs.length);
            System.arraycopy(sources.toArray(new String[sources.size()]), 0, finalLine, procAndArgs.length, sources.size());

            Process proc = Runtime.getRuntime().exec(finalLine);

            InputStream in = proc.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder result = new StringBuilder();
            while((line = reader.readLine()) != null)
                System.out.println(line);
            proc.waitFor();
            List<String> resources = new ArrayList<>();
            findResources(resources, new File(_resources), _build);
        } catch (IOException | InterruptedException e) {
            
        }
    }

    /**
    * Find recursively sources java project that must be compile.
    * @param list the list java sources for compile
    * @param path the path where java sources are located
    */ 
    private static void findSources(List<String> list, File path) throws IOException {
        String[] fileSources = path.list();
        for(int i = 0; i < fileSources.length; i++) {
            if(fileSources[i].contains(".java")) {
                list.add(path.getAbsolutePath().concat("/").concat(fileSources[i]));
            }
            else {
                findSources(list, new File(path.getAbsolutePath().concat("/").concat(fileSources[i])));
            }
        }
    }

    /**
    * Find recursively resources java project that must be copied to build directory of project.
    * @param list the list resources that must be copied
    * @param path the path where resources are located
    * @param inDir resource directory that must be copied
    */
    private static void findResources(List<String> list, File path, String inDir) throws IOException {
        File[] fileResources = path.listFiles();        
        for(int i = 0; i < fileResources.length; i++) {
            if(fileResources[i].isDirectory())
                findResources(list, fileResources[i], inDir.concat("/").concat(fileResources[i].getName()));
            else
                copy(fileResources[i], inDir);
        }
    }

    /**
    * Copy resource file to build directory of project.
    * @param file the file that must be copied
    * @param resourceDirectory resource directory that must be copied
    */
    private static void copy(File file, String resourceDirectory) throws IOException {
        InputStream in = new FileInputStream(file);
        File directory = new File(resourceDirectory);
        if(!directory.exists())
            directory.mkdirs();
        File fileResource = new File(resourceDirectory.concat("/").concat(file.getName()));
        fileResource.createNewFile();
        FileOutputStream out = new FileOutputStream(fileResource);
        byte[] data = new byte[1024];
        int length;
        while((length = in.read(data)) != -1)
            out.write(data, 0, length);
        out.close();
        in.close();
    }

    /**
    * Run project; That method response for action RUN.
    */
    private static void run() {
        try {
            String mainClass = _clazz.replace(".", "/");
            String[] procAndArgs = new String[] {"java", "-cp", _build, mainClass};
            Process proc = Runtime.getRuntime().exec(procAndArgs);
            InputStream in = proc.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null)
                System.out.println(line);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {

        }
    }

    /**
    * Clean project; That method response for action CLEAN.
    */
    private static void clean() {
        String[] build = _build.split("/");
        File buildDirectory = new File(build[0]);
        if(buildDirectory.exists())
            deleteFiles(new File(build[0]));
    }

    /**
    * Recursively delete files from build directory.
    * @param file the file that must be delete
    */
    private static void deleteFiles(File file) {
        File[] listFiles = file.listFiles();
        for(int i = 0; i < listFiles.length; i++) {
            if(listFiles[i].isDirectory())
                deleteFiles(listFiles[i]);
            else
                listFiles[i].delete();
        }
        file.delete();                
    }

}

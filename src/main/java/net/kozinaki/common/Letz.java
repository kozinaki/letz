package net.kozinaki.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class Letz {

    private static Properties properties;

    private static String _source;
    private static String _resources;
    private static String _clazz;
    private static String _build;

    public static void main(String args[]) {
        try {
            properties = new Properties();
            FileInputStream in = new FileInputStream("xyz.ini");
            properties.load(in);
            in.close();
            _source = properties.getProperty("source");
            _resources = properties.getProperty("resources");
            _clazz = properties.getProperty("class");
            _build = properties.getProperty("build");
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("\nYou must have xyz.ini file to continue.");
            return;
        }
        String arg;
        if(args.length != 0)
            arg = args[0];
        else {
            printArgumentsException();
            return;
        }
        switch (arg) {
            case "compile":
                compile();
                break;
            case "run":
                run();
                break;
            case "clear":
                clear();
                break;
            default:
                printArgumentsException();
                break;
        }
    }

    private static void printArgumentsException() {
        System.out.println("\nYou must type some of arguments: \n\tcompile \n\trun \n\tclear");
    }

    public static void compile() {
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

    public static void findSources(List<String> list, File path) throws IOException {
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

    private static void findResources(List<String> list, File path, String inDir) throws IOException {
        File[] fileResources = path.listFiles();        
        for(int i = 0; i < fileResources.length; i++) {
            if(fileResources[i].isDirectory())
                findResources(list, fileResources[i], inDir.concat("/").concat(fileResources[i].getName()));
            else
                copy(fileResources[i], inDir);
        }
    }

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

    public static void run() {
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

    public static void clear() {
        String[] build = _build.split("/");
        File buildDirectory = new File(build[0]);
        if(buildDirectory.exists())
            deleteFiles(new File(build[0]));
    }

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
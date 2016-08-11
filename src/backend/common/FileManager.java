package backend.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static Path CreateSrcDir(String srcFilename) throws IOException {
        Path src_dir;
        // if no --output-dir=<path> is given use subdirectory next to *.str file
        if (!streamit2.Compiler.outputDir.isEmpty()) {
            src_dir = Paths.get(new File(streamit2.Compiler.outputDir).getAbsolutePath());
        } else {
            src_dir = Paths.get(directory(srcFilename), "generated_src");
        }

        try {
            Files.createDirectory(src_dir);
        } catch (FileAlreadyExistsException e) {
        }
        return src_dir;
    }

    public static Path CreateProjectDir(Path src_dir, String srcFilename) throws IOException {
        Path project_dir = Paths.get(src_dir.toString(), basename(srcFilename));
        try {
            Files.createDirectory(project_dir);
        } catch (FileAlreadyExistsException e) {
        }
        return project_dir;
    }

    public static Path CreateCDir(String target, Path project_dir) throws IOException {
        Path c_dir = Paths.get(project_dir.toString(), target);
        if (!Files.exists(c_dir)) {
            Files.createDirectory(c_dir);
        } else {
            DeleteContents(c_dir.toFile());
        }
        return c_dir;
    }

    public static Path CreateProcessorDir(Path project_dir, String procesorName) throws IOException {
        Path processor_dir = Paths.get(project_dir.toString(), procesorName);
        if (!Files.exists(processor_dir)) {
            Files.createDirectory(processor_dir);
        }
        DeleteContents(processor_dir.toFile());
        return processor_dir;
    }

    private static void DeleteContents(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                DeleteContents(f);
            }
            f.delete();
        }
    }

    private static String directory(String srcFilename) {
        File file = new File(srcFilename);
        return file.getParent();
    }

    private static String basename(String srcFilename) {
        File file = new File(srcFilename);
        String fileName = file.getName();

        if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}

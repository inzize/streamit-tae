package backend.sequential;

import ast.Program;
import backend.BackendCodeGenerator;
import backend.common.CodeGenerator;
import backend.common.FileManager;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SequentialCodeGenerator implements BackendCodeGenerator {

    public void Generate(String srcFilename, Program program, List<FilterInstance> filters) throws FileNotFoundException, IOException {
        Path src_dir = FileManager.CreateSrcDir(srcFilename);
        Path project_dir = FileManager.CreateProjectDir(src_dir, srcFilename);
        Path c_dir = FileManager.CreateCDir("c", project_dir);

        Program.GenerateC(true);

        String filename = c_dir + File.separator + "main.c";
        CodeGenerator generator = new CodeGenerator(null, filename, program, filters);
        generator.Generate();
    }

    private static void DeleteContents(File file) {
        for (File f : file.listFiles()) {
            f.delete();
        }
    }

    private static String directory(String srcFilename) {
        int pos = srcFilename.lastIndexOf("\\");
        if (pos < 0) {
            return ".";
        } else {
            return srcFilename.substring(0, pos);
        }
    }

    private static String basename(String srcFilename) {
        int start = srcFilename.lastIndexOf("\\") + 1;
        int end = srcFilename.lastIndexOf(".");
        return srcFilename.substring(start, end);
    }

    @Override
    public void GenerateHeader() {
    }
}

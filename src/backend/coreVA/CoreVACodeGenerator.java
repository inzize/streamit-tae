package backend.coreVA;

import backend.common.Chip;
import backend.common.Processor;
import static ast.GeneratesCode.writer;
import ast.Program;
import backend.common.channels.AbstractChannel;
import backend.common.CodeGenerator;
import backend.common.FileManager;
import backend.coreVA.xmlModel.MpsocChip;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedMap;

public class CoreVACodeGenerator extends CodeGenerator 
{   
    private final Map<FilterInstance, Processor> partitioning;
    public static Path project_dir;
    private CoreVAProcessor proc;
    
    public CoreVACodeGenerator(Processor proc, String filename, Program program, List<FilterInstance> local_filters, SortedMap<FilterInstance, Processor> partitioning) throws FileNotFoundException {
        super(proc, filename, program, local_filters);
        this.partitioning   = partitioning;
        this.proc = (CoreVAProcessor) proc;
    }
    
    class FilterComparator implements Comparator<Map.Entry<FilterInstance,AbstractChannel>> {    
        private final int descent;
        
        public FilterComparator(boolean descent) {
            this.descent = (descent?-1:1);
        }
        public int compareTargetCPU(Map.Entry<FilterInstance, AbstractChannel> t, FilterInstance t1) {
            return descent*Integer.compare(partitioning.get(t.getKey()).getGID(),partitioning.get(t1).getGID()); 
        }
        @Override
        public int compare(Map.Entry<FilterInstance, AbstractChannel> t, Map.Entry<FilterInstance, AbstractChannel> t1) {
            int compareTargetCPU = descent*Integer.compare(partitioning.get(t.getKey()).getGID(),partitioning.get(t1.getKey()).getGID());
            int compareFilterID  = t.getValue().getIdent().compareTo(t1.getValue().getIdent());
            if (compareTargetCPU!=0) return compareTargetCPU;
            return compareFilterID; 
        }
    }  
 
    @Override
    protected void generateInitChannelsBody() {
                
        // initialize shared tcdm memory if available
        // on any cpu with filters and in any case on cpu 0
        MpsocChip cc = (MpsocChip) streamit2.Compiler.chip;
        if (cc.sharedMemAvail(proc) && (!local_filters.isEmpty() || (proc.getId() == 0))) {
            writer.writeLine("#ifdef COREVA");
            writer.writeLine("stcdm_alloc_wait_init();");
            writer.writeLine("#endif");
        }
        
        if (local_filters.isEmpty()) return; //no filters?-> exit
                   
        final int defaultCapacity = 100;
        final FilterInstance us = local_filters.get(0);
        FilterComparator filterComp       = new FilterComparator(false);
        FilterComparator filterCompDescent = new FilterComparator(true);
        PriorityQueue<Map.Entry<FilterInstance,AbstractChannel>> inputs  = new PriorityQueue<>(defaultCapacity,filterComp);
        PriorityQueue<Map.Entry<FilterInstance,AbstractChannel>> outputs = new PriorityQueue<>(defaultCapacity,filterCompDescent);
        
        //Sort filters (sort order-> primary: target cpu, secondary: channel ident) 
        for (FilterInstance filter : local_filters) {
            for (Map.Entry<FilterInstance,AbstractChannel> input : filter.inputs.entrySet()) 
                inputs.add(input);
            for (Map.Entry<FilterInstance,AbstractChannel> output: filter.outputs.entrySet())
                outputs.add(output);            
        }
        
        //input and output order according to the communication api
        while(!inputs.isEmpty() && filterComp.compareTargetCPU(inputs.peek(),us)<=0)
            inputs.poll().getValue().InitRead();
        
        while(!outputs.isEmpty() && filterComp.compareTargetCPU(outputs.peek(),us)>=0)
            outputs.poll().getValue().InitWrite();
        
        while (!outputs.isEmpty()) 
            outputs.poll().getValue().InitWrite();
    
        while (!inputs.isEmpty())
            inputs.poll().getValue().InitRead();
        
        for (FilterInstance filter : local_filters) {
            if (filter.FileWriterChannel != null) {
                filter.FileWriterChannel.InitWrite();
            }
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.InitWrite();
            }
        }

    }
    
    public static Path Generate(String src_file, CoreVAChip chip, Program program, SortedMap<FilterInstance, Processor> partitioning) throws FileNotFoundException, IOException
    {
        Path generated_dir = FileManager.CreateSrcDir(src_file);
        project_dir = FileManager.CreateProjectDir(generated_dir.toAbsolutePath(), src_file);
        Path coreva_dir = FileManager.CreateCDir("coreva", project_dir);        

        Program.GenerateC(true);
        
        CreateMainFile(coreva_dir, chip);
        createMakefile(project_dir);
        createCMakeLists(project_dir);

        Path processor_dir = FileManager.CreateProcessorDir(coreva_dir, "cores");

        for (Processor processor : chip.getProcessors())
        {
            String filename = processor_dir + File.separator + processor.toString() + ".c";

            List<FilterInstance> local_filters = new ArrayList();
            for (FilterInstance filter : partitioning.keySet())
                if (partitioning.get(filter) == processor)
                    local_filters.add(filter);

            CodeGenerator generator = new CoreVACodeGenerator(processor, filename, program, local_filters, partitioning);
            generator.Generate();
        }
        return project_dir;
    }
    
    public static void CreateMainFile(Path coreva_dir, CoreVAChip chip) throws IOException {
        try (FileWriter file_writer = new FileWriter(coreva_dir.toString() + File.separator + "main_pthread.c")) {
            PrintWriter writer = new PrintWriter(file_writer);
            writer.println("// generated by compiler");
            writer.println("#include <pthread.h>");
            writer.println("#include <mpsoc_com-lib.h>");
            writer.println("#include <fio_com-lib.h>");
            writer.println();

            for (Processor processor : chip.getProcessors()) {
                final CoreVAProcessor coreva = (CoreVAProcessor) processor;
                writer.println("extern void cpu_" + coreva.getCol() + "_" + coreva.getRow() + "_" + coreva.getId()+ "();");
            }

            writer.println();

            writer.println("int main()");
            writer.println("{");
            if ((chip.getRows() + chip.getCols()) > 2)
                writer.println("\tpthread_t threadNoc[" + chip.getCols() + "][" + chip.getRows() + "];");
            writer.println("\tpthread_t threadCore[" + chip.getCols() + "][" + chip.getRows() + "][" + chip.getMaxDepth() + "];");
            writer.println("\tinitMpsocController(" + chip.getCols() + ", " + chip.getRows() + ", " + chip.getMaxDepth() + ");");

            writer.println();

			writer.println("\t//Intitilization for Pthread FileIO");
			writer.println("\tf = malloc(sizeof(fileio_t));");
			writer.println("\tf->channel = calloc (FIO_CHANNEL_MAX_NUM, sizeof (*f->channel));");
			writer.println("\tassert (f->channel != NULL);");
			writer.println("\t/* XXX: this is a bad design decision since programs depend on the (at");
			writer.println("\t * compile time unknown) channel count and are _not_portable_! */");
			writer.println("\tf->scratchpadSize = SCRATCHPAD_SIZE - FIO_CHANNEL_MAX_NUM * FIO_CHANNEL_MEM_FOOTPRINT;");
			writer.println("\tf->scratchpad = calloc (f->scratchpadSize, sizeof (*f->scratchpad));");
			writer.println("\tassert (f->scratchpad != NULL);");
			
			writer.println();
			
            writer.println("\tpthread_key_create(&procIdX, globalDestructor);");
            writer.println("\tpthread_key_create(&procIdY, globalDestructor);");
            writer.println("\tpthread_key_create(&procIdID, globalDestructor);");
            writer.println("\tpthread_key_create(&procType, globalDestructor);");
            writer.println("\tpthread_key_create(&procDebugFile, globalDestructor);");
            writer.println();

            if ((chip.getRows() + chip.getCols()) > 2) {
                for (int row = 0; row < chip.getRows(); row++) {
                    for (int col = 0; col < chip.getCols(); col++) {
                        writer.println("\tpthread_create(&(threadNoc[" + col + "][" + row + "]), NULL, Start, createPos(&mpsocControl, " + col + ", " + row + ", 0, 'N'));");
                    }
                }
            }

            for (Processor processor : chip.getProcessors()) {
                final CoreVAProcessor coreva = (CoreVAProcessor) processor;
                writer.println("\tpthread_create(&(threadCore[" + coreva.getCol() + "][" + coreva.getRow() + "][" + coreva.getId()+ "]), NULL, Start, createPos(&cpu_" + coreva.getCol() + "_" + coreva.getRow() + "_" + coreva.getId()+ ", " + coreva.getCol() + ", " + coreva.getRow() + ", " + coreva.getId()+ ", 'P'));");
            }
            writer.println();

            for (Processor processor : chip.getProcessors()) {
                final CoreVAProcessor coreva = (CoreVAProcessor) processor;
                writer.println("\tpthread_join(threadCore[" + coreva.getCol() + "][" + coreva.getRow() + "][" + coreva.getId()+ "], NULL);");
            }
//            for (int row = 0; row < chip.getRows(); row++)
//                for (int col = 0; col < chip.getCols(); col++)
//                        writer.println("\tpthread_join(threadNoc["+row+"]["+col+"], NULL);");    
            writer.println("\treturn 0;");
            writer.println("}");
        }
    }
    
    public static void createMakefile(Path project_dir) throws IOException {
        try (FileWriter file_writer = new FileWriter(project_dir.toString() + File.separator + "Makefile")) {
            PrintWriter writer = new PrintWriter(file_writer);
            writer.println("# generated by compiler");
            writer.println("ROOT_PATH=$(COREVA_STREAMIT)");
            writer.println("ifeq ($(COREVA_MPSOC_SIMFLAGS),)");
            writer.println("\tRUNTIME_DIR:=sim");
            writer.println("else");
            writer.println("\tRUNTIME_DIR:=sim_$(shell echo '$(COREVA_MPSOC_SIMFLAGS)' | sed 's/[^a-zA-Z0-9]//g\')");
            writer.println("endif");
            writer.println("%: ");
            writer.println("\t@mkdir -p $(RUNTIME_DIR) #remove this when there are dir guards in makebase_streamit");
            writer.println("\t@RUNTIME_DIR=\"${RUNTIME_DIR}\" ROOT_PATH=\"${ROOT_PATH}\" $(MAKE) --no-print-directory -C ${RUNTIME_DIR} -f $(COREVA_SOFTWARE)/makebase_streamit.mk $@");
        }
    }

    public static void createCMakeLists(Path project_dir) throws IOException {
        try (FileWriter file_writer = new FileWriter(project_dir.toString() + File.separator + "CMakeLists.txt")) {
            PrintWriter writer = new PrintWriter(file_writer);
            writer.println("# generated by compiler");
            writer.println("cmake_minimum_required(VERSION 3.0.2)");
            writer.println("include($ENV{COREVA_SOFTWARE}/streamit.cmake)");
            writer.println("include($ENV{COREVA_SOFTWARE}/streamit.cmake)");
            writer.println("option(COREVA_TOOLCHAIN \"Use the CoreVA toolchain\" ON)");
            writer.println("if(COREVA_TOOLCHAIN)");
            writer.println("  set(CMAKE_TOOLCHAIN_FILE $ENV{COREVA_SOFTWARE}/toolchain.cmake)");
            writer.println("  add_definitions(\"-DCOREVA\")");
            writer.println("endif()");
            writer.println("project(Streamit-CoreVA-Software C ASM)");
            writer.println("init_streamit_cmake()");
        }
    }
    
    @Override
    public void generateHeader()
    {
        super.generateHeader();      
        writer.println("#include \"mpsoc_com-lib.h\"");
        writer.println("#include \"fio_com-lib.h\"");
        writer.println("#ifndef COREVA_HWACC_MISC_TRACE\n#define COREVA_HWACC_MISC_TRACE(id) \n#endif\n");
    }
}

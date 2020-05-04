package name.martingeisse.simv.cpu.testutil;

import name.martingeisse.simv.cpu.BasicInstructionTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class BuildProgramsMain {

    private static final Class<?>[] testClasses = {
            BasicInstructionTest.class,
    };

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(ProgramStorageKey.BASE_FOLDER);
        ProgramStorageKey.BASE_FOLDER.mkdir();
        for (Class<?> testClass : testClasses) {
            for (Method testMethod : testClass.getMethods()) {
                Program programAnnotation = testMethod.getAnnotation(Program.class);
                if (programAnnotation == null) {
                    continue;
                }
                ProgramStorageKey key = new ProgramStorageKey(testClass, testMethod.getName());
                String sourceCode = ".option norvc\n.text\nentryPoint:\n" +
                    StringUtils.join(programAnnotation.value(), '\n');
                FileUtils.write(key.getSourceCodeFile(), sourceCode);
                compile(key);
            }
        }
    }

    private static void compile(ProgramStorageKey key) throws Exception {

        String sourceFile = key.getSourceCodeFile().getAbsolutePath();
        String objectFile = key.getObjectFile().getAbsolutePath();
        String elfFile = key.getElfFile().getAbsolutePath();
        String mapFile = key.getMapFile().getAbsolutePath();
        String imageFile = key.getImageFile().getAbsolutePath();

        exec(tool("gcc"), "-msmall-data-limit=100000", "-march=rv32im", "-mabi=ilp32", "-fno-exceptions",
                "-Wall", "-fno-tree-loop-distribute-patterns", "-c",
                "-o", objectFile, sourceFile);
        exec(tool("ld"), "-Map=" + mapFile, "-A", "rv32im", "-N", "-Ttext=0x80200000", "-e", "entryPoint",
                "-o", elfFile, objectFile);
        exec(tool("objcopy"), "-j", ".text", "-j", ".rodata", "-j", ".sdata", "-I", "elf32-littleriscv", "-O", "binary",
                elfFile, imageFile);

        if (!key.getImageFile().exists()) {
            throw new RuntimeException("output file does not exist: " + key.getImageFile());
        }
    }

    private static String tool(String suffix) {
        return System.getProperty("user.home") + "/riscv-toolchain/bin/riscv32-unknown-linux-gnu-" + suffix;
    }

    private static void exec(String... command) throws Exception {
        Process process = Runtime.getRuntime().exec(command, null, ProgramStorageKey.BASE_FOLDER);
        int status = process.waitFor();
        if (status != 0) {
            System.err.println();
            System.err.println("******************************************");
            System.err.println("*** ERROR WHILE COMPILING TEST PROGRAM ***");
            System.err.println("******************************************");
            System.err.println();
            System.err.println("command: " + StringUtils.join(command, ' '));
            System.err.println();
            System.err.println("command segments: " + StringUtils.join(command, '|'));
            System.err.println();
            IOUtils.copy(process.getInputStream(), System.err);
            IOUtils.copy(process.getErrorStream(), System.err);
            System.err.println("status code " + status);
            System.err.flush();
            System.exit(1);
        }
    }

}

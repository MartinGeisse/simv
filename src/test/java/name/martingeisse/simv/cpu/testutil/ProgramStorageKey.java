package name.martingeisse.simv.cpu.testutil;

import java.io.File;

public final class ProgramStorageKey {

    public static final File BASE_FOLDER = new File("resource/testprog");

    private final Class<?> testClass;
    private final String testMethodName;

    public ProgramStorageKey(Class<?> testClass, String testMethodName) {
        this.testClass = testClass;
        this.testMethodName = testMethodName;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public String getFullName() {
        return testClass.getName().replace('/', '_').replace('.', '_') + '_' + testMethodName;
    }

    public File getSourceCodeFile() {
        return new File(BASE_FOLDER, getFullName() + ".S");
    }

    public File getObjectFile() {
        return new File(BASE_FOLDER, getFullName() + ".o");
    }

    public File getElfFile() {
        return new File(BASE_FOLDER, getFullName() + ".elf");
    }

    public File getImageFile() {
        return new File(BASE_FOLDER, getFullName() + ".bin");
    }

    @Override
    public String toString() {
        return "ProgramStorageKey{" +
                "testClass=" + testClass +
                ", testMethodName='" + testMethodName + '\'' +
                '}';
    }

}

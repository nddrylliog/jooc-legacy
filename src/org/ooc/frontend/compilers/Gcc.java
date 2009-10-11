package org.ooc.frontend.compilers;

/**
 * Gnu Compilers Collection 
 * 
 * @author Amos Wenger
 */
public class Gcc extends BaseCompiler {

	public Gcc() {
		super("gcc");
	}
	
	public Gcc(String executableName) {
		super(executableName);
	}

	public void addDynamicLibrary(String library) {
		command.add("-l"+library);
	}

	public void addIncludePath(String path) {
		command.add("-I"+path);
	}

	public void addLibraryPath(String path) {
		command.add("-L"+path);
	}

	public void addObjectFile(String file) {
		command.add(file);
	}

	public void addOption(String option) {
		command.add(option);
	}

	public void setOutputPath(String path) {
		command.add("-o");
		command.add(path);
	}

	public void setCompileOnly() {
		command.add("-c");
	}

	public void setDebugEnabled() {
		command.add("-g");
	}
	
	@Override
	public void reset() {
		super.reset();
		command.add("-std=c99");
		command.add("-Wall");
	}

	public boolean supportsDeclInFor() {
		return true;
	}

	public boolean supportsVLAs() {
		return true;
	}
	
	@Override
	public Gcc clone() {
		return new Gcc();
	}

}

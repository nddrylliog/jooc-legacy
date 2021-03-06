package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.parser.ModuleParser;

public class Import extends Node {

	protected String path;
	protected Module module;

	public Import(String path, Token defaultToken) {
		super(defaultToken);
		this.path = path;
	}
	
	public String getFullName() {
		return path.replace("/", ".");
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public Module getModule() {
		if(module == null) {
			return ModuleParser.cache.get(path + ".ooc");
		}
		return module;
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	@Override
	public String toString() {
		return super.toString()+" : "+path;
	}
	
}

package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class VariableDecl extends Declaration implements MustBeUnwrapped, PotentiallyStatic, MustBeResolved {

	public static class VariableDeclAtom extends Node {
		String name;
		Expression expression;
		Assignment assign;
		
		public VariableDeclAtom(String name, Expression expression, Token startToken) {
			super(startToken);
			this.name = name;
			this.expression = expression;
		}

		@Override
		public boolean replace(Node oldie, Node kiddo) {
			if(oldie == expression) {
				expression = (Expression) kiddo;
				return true;
			}
			return false;
		}

		@Override
		public void accept(Visitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(Visitor visitor) throws IOException {
			if(expression != null) expression.accept(visitor);
		}

		@Override
		public boolean hasChildren() {
			return expression != null;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Expression getExpression() {
			return expression;
		}
		
		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		@Override
		public String toString() {
			if(expression != null) return getClass().getSimpleName()+": "+name+"="+expression;
			return getClass().getSimpleName()+": "+name;
		}

	}
	
	private boolean isStatic;
	
	protected Type type;
	protected TypeDecl typeDecl;
	
	protected NodeList<VariableDeclAtom> atoms;
	
	public VariableDecl(Type type, boolean isStatic, Token startToken) {
		super(null, startToken);
		this.type = type;
		this.isStatic = isStatic;
		this.atoms = new NodeList<VariableDeclAtom>(startToken);
	}
	
	@Override
	public String getName() {
		if(atoms.size() == 1) return atoms.get(0).name;
		throw new UnsupportedOperationException("Can't getName on a VariableDeclaration with multiple variables "+atoms);
	}
	
	public boolean hasAtom(String name) {
		for(VariableDeclAtom atom: atoms) {
			if(atom.name.equals(name)) return true;
		}
		return false;
	}
	
	public VariableDeclAtom getAtom(String name) {
		for(VariableDeclAtom atom: atoms) {
			if(atom.name.equals(name)) return atom;
		}
		return null;
	}
	
	public NodeList<VariableDeclAtom> getAtoms() {
		return atoms;
	}
	
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException(
				"Can't setName on a VariableDeclaration, because it has" +
				" several atoms, so we don't know which one to adjust," +
				" e.g. it doesn't make sense.");
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return typeDecl;
	}
	
	public void setTypeDecl(TypeDecl typeDecl) {
		this.typeDecl = typeDecl;
		if(type instanceof FuncType) {
			FuncType funcType = (FuncType) type;
			funcType.getDecl().setTypeDecl(typeDecl);
		}
	}
	
	public boolean isMember() {
		return typeDecl != null;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		if(getType() != null) getType().accept(visitor);
		atoms.accept(visitor);
	}

	@Override
	public boolean unwrap(NodeList<Node> stack) throws OocCompilationError, IOException {
	
		if(stack.get(stack.size() - 2) instanceof ClassDecl) {
			unwrapToClassInitializers(stack, (ClassDecl) stack.get(stack.size() - 2));
			return false;
		}
		
		return unwrapToVarAcc(stack);
		
	}

	@SuppressWarnings("unchecked")
	public boolean unwrapToVarAcc(NodeList<Node> stack) {

		Node parent = stack.peek();
		Node grandpa = stack.get(stack.size() - 2);
		
		if(parent instanceof Line
		|| grandpa instanceof Module
		|| grandpa instanceof FunctionDecl
		|| grandpa instanceof TypeDecl
		) {
			return false;
		}
		
		if(atoms.size() != 1) {
			throw new OocCompilationError(this, stack, "Multi-var decls used an expression.. wtf?");
		}
		VariableDeclAtom atom = atoms.get(0);
		VariableAccess varAcc = new VariableAccess(atom.name, atom.startToken);
		varAcc.setRef(this);
		if(!parent.replace(this, varAcc)) {
			throw new Error("Couldn't replace "+this+" with "+varAcc+" in "+parent);
		}
		
		if(parent instanceof NodeList<?>) {
			NodeList<Node> list = (NodeList<Node>) parent;
			for(Node node: list) {
				if(node instanceof VariableAccess) {
					VariableAccess brother = (VariableAccess) node;
					if(brother.getName().equals(atom.name)) {
						brother.setRef(this);
					}
				}
			}
		}
		
		int lineIndex = stack.find(Line.class);
		if(lineIndex == -1) {
			throw new Error("Not in a line! How are we supposed to add one? Stack = "+stack);
		}
		Line line = (Line) stack.get(lineIndex);
		
		int bodyIndex = lineIndex - 1;
		if(bodyIndex == -1) {
			throw new Error("Didn't find a nodelist containing the line! How are we suppoed to add one? Stack = "+stack);
		}
		NodeList<Line> body = (NodeList<Line>) stack.get(bodyIndex);
		
		int declIndex = stack.find(VariableDecl.class);
		if(declIndex == -1) {
			Block block = new Block(startToken);
			block.getBody().add(new Line(this));
			block.getBody().add(line);
			body.replace(line, new Line(block));
		} else {
			body.addBefore(line, new Line(this));
		}
		return true;
		
	}

	public void unwrapToClassInitializers(NodeList<Node> hierarchy, ClassDecl classDecl) {		
		
		for(VariableDeclAtom atom: atoms) {

			if(atom.getExpression() == null) continue;
			VariableAccess access = isStatic ?
					new VariableAccess(typeDecl.getType().getName(), atom.startToken)
					: new VariableAccess("this", atom.startToken);
					
			Assignment assign = new Assignment(
				new MemberAccess(access, atom.getName(), atom.startToken), atom.getExpression(), atom.startToken
			);
			atom.assign = assign;
			Line line = new Line(assign);
			if(isStatic) {
				access.setRef(classDecl);
				classDecl.getFunction(ClassDecl.LOAD_FUNC_NAME, "", null).getBody().add(line);
			} else {
				classDecl.getFunction(ClassDecl.DEFAULTS_FUNC_NAME, "", null).getBody().add(line);
			}
			atom.expression = null;
		
		}
		
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String repr = "";
		//if(isConst) repr = "const " + repr;
		if(isStatic) repr = "static " + repr;
		Iterator<VariableDeclAtom> iter = atoms.iterator();
		while(iter.hasNext()) {
			repr += iter.next().getName();
			if(iter.hasNext()) repr += ", ";
		}
		repr += ":"+type;
		return getClass().getSimpleName()+"|"+repr;
	}

	public boolean shouldBeLowerCase() {
		return (externName == null || !externName.isEmpty()) && type != null && !(type.getName().equals("Class"));
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Type type = getType();
		if(type != null && !type.isArray() && type.isGenericRecursive()
				&& type.isFlat() && !isMember() && !(this instanceof Argument)) {
			Type newType = new Type("Octet", type.startToken);
			newType.setPointerLevel(1);
			newType.setArray(true);
			VariableAccess tAccess = new VariableAccess(type.getRef().getName(), startToken);
			MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
			newType.setArraySize(sizeAccess);
			newType.setRef(type.getRef());
			setType(newType);
			return Response.RESTART;
		}
		
		return Response.OK;
	}
	
}

package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class FunctionCall extends Access implements MustBeResolved {

	protected String name;
	protected String suffix;
	protected final NodeList<Expression> typeParams;
	protected final NodeList<Expression> arguments;
	protected FunctionDecl impl;
	protected AddressOf returnArg;
	protected Type realType;
	
	public FunctionCall(String name, String suffix, Token startToken) {
		super(startToken);
		this.name = name;
		this.suffix = suffix;
		this.typeParams = new NodeList<Expression>();
		this.arguments = new NodeList<Expression>(startToken);
		this.impl = null;
		this.returnArg = null;
		this.realType = null;
	}
	
	public FunctionCall(FunctionDecl func, Token startToken) {
		this(func.getName(), func.getSuffix(), startToken);
		setImpl(func);
	}

	public void setImpl(FunctionDecl impl) {
		this.impl = impl;
	}
	
	public FunctionDecl getImpl() {
		return impl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public NodeList<Expression> getTypeParams() {
		return typeParams;
	}
	
	public NodeList<Expression> getArguments() {
		return arguments;
	}
	
	public AddressOf getReturnArg() {
		return returnArg;
	}

	@Override
	public Type getType() {
		return realType;
	}
	
	private Type realTypize(Type type, NodeList<Node> stack) {
		int i = -1;
		for(VariableAccess exprParam: type.getTypeParams()) {
			i++;
			VariableAccess expr = resolveTypeParam(exprParam.getName(), stack, true);
			if(expr != null){
				type.getTypeParams().set(i, expr);
			}
		}
		return type;
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
		typeParams.accept(visitor);
		arguments.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == impl) {
			impl = (FunctionDecl) kiddo;
			return true;
		}
		return false;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(final NodeList<Node> stack, final Resolver res, final boolean fatal) throws IOException {
		
		if(impl == null) {
			if (name.equals("this")) {
				resolveConstructorCall(stack, false);
			} else if (name.equals("super")) {
				resolveConstructorCall(stack, true);
			}  else {
				resolveRegular(stack, res, fatal);
			}
		}
	
		if(impl != null) {
			autocast();
			Response response = handleGenerics(stack, res, fatal);
			if(response != Response.OK) return response;
		}
		
 		if(impl == null) {
 			if(fatal) {
 				String message = "Couldn't resolve call to function "+name+getArgsRepr()+".";
 				String guess = guessCorrectName(stack, res);
 				if(guess != null) {
 					message += " Did you mean "+guess+" ?";
 				}
 				throw new OocCompilationError(this, stack, message);
 			}
 			return Response.LOOP;
 		}
		
 		return Response.OK;
		
	}

	protected Response handleGenerics(final NodeList<Node> stack, final Resolver res, boolean fatal)
			throws IOException {

		if(impl == null) {
			if(fatal) throw new OocCompilationError(this, stack, "Didn't find implementation for "
					+this+", can't handle generics.");
			return Response.LOOP;
		}
		
		// If one of the arguments which type is generic is not a VariableAccess
		// turn it into a VDFE and unwrap it
		LinkedHashMap<String, TypeParam> generics = impl.getTypeParams();
		if(!generics.isEmpty()) for(TypeParam genType: generics.values()) {
			Response response = checkGenType(stack, genType, fatal);
			if(response != Response.OK) return response;
		}
		if(impl.getTypeDecl() != null) for(TypeParam genType: impl.getTypeDecl().getTypeParams().values()) {
			Response response = checkGenType(stack, genType, fatal);
			if(response != Response.OK) return response;
		}
		
		// Find all variable accesses to fill this function's type params
		if(typeParams.size() < impl.getTypeParams().size()) {
			Iterator<TypeParam> iter = impl.getTypeParams().values().iterator();
			for(int i = 0; i < typeParams.size(); i++) iter.next();
			while(iter.hasNext()) {
				TypeParam typeParam = iter.next();
				Expression result = resolveTypeParam(typeParam.getName(), stack, fatal);
				if(result == null) {
					if(fatal) throwUnresolvedType(stack, typeParam.getName());
					return Response.LOOP;
				}
				typeParams.add(result);
			}
			return Response.RESTART;
		}
		
		// Determine the real type of this function call.
		if(realType == null) {
			Type retType = impl.getReturnType();
			if(retType.isGenericRecursive()) {
				Type candidate = realTypize(retType, stack);
				if(candidate == null) {
					if(fatal) throw new OocCompilationError(this, stack, "RealType still null, can't resolve generic type "+retType);
					return Response.LOOP;
				}
				realType = candidate;
			} else {
				realType = retType;
			}
		}
		
		// Turn any outer assigned access into a returnArg, unwrap if in varDecl.
		Type returnType = impl.getReturnType();
		TypeParam genType = impl.getGenericType(returnType.getName());
		if(genType != null) {
			Node parent = stack.peek();
			if(parent instanceof Assignment) {
				Assignment ass = (Assignment) parent;
				if(ass.getLeft() instanceof Access) {
					returnArg = new AddressOf(ass.getLeft(), startToken);
					stack.get(stack.size() - 2).replace(ass, this);
					return Response.RESTART;
				}
			} else if(parent instanceof VariableDeclAtom) {
				VariableDeclAtom atom = (VariableDeclAtom) parent;
				return unwrapFromVarDecl(stack, genType,  atom, fatal);
			} else if(parent instanceof Line) {
				// alright =)
			} else {
				VariableDeclFromExpr vdfe = new VariableDeclFromExpr(generateTempName("gcall"),
						this, startToken);
				Type implRetType = impl.getReturnType();
				if(implRetType.getRef() == null) {
					if(impl.getTypeDecl() != null) stack.push(impl.getTypeDecl());
					stack.push(impl);
					implRetType.resolve(stack, res, false);
					stack.pop(impl);
					if(impl.getTypeDecl() != null) stack.pop(impl.getTypeDecl());
				}
				
				parent.replace(this, vdfe);
				vdfe.unwrapToVarAcc(stack);
				return Response.RESTART;
			}
		}
		
		return Response.OK;
		
	}

	protected VariableAccess resolveTypeParam(String typeParam, NodeList<Node> stack, boolean fatal) {

		if(impl == null) return null;
		
		VariableAccess result = null;
		
		int i = -1;
		for(Argument arg: impl.getArguments()) {
			i++;
			if(arg.getType().getName().equals(typeParam)) {
				Expression callArg = arguments.get(i);
				result = new MemberAccess(callArg, "class", callArg.startToken);
				break;
			}
		}
		
		return result;
		
	}

	private Response checkGenType(final NodeList<Node> stack, TypeParam genType, boolean fatal) {
		Iterator<Argument> iter = impl.getThisLessArgsIter();
		int i = -1;
		while(iter.hasNext()) {
			i++;
			Argument arg = iter.next();
			if(arg.getType() == null) {
				continue;
			}
			if(!arg.getType().getName().equals(genType.getName())) continue;
			Expression expr = arguments.get(i);
			if(!(expr instanceof VariableAccess)) {
				System.out.println("in "+this+" vdf'ing "+arg);
				VariableDeclFromExpr vdfe = new VariableDeclFromExpr(
						generateTempName(genType.getName()+"param"), expr, startToken);
				arguments.replace(expr, vdfe);
				stack.push(this);
				stack.push(arguments);
				vdfe.unwrapToVarAcc(stack);
				stack.pop(arguments);
				stack.pop(this);
				return Response.RESTART;
			}
		}
		return Response.OK;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Response unwrapFromVarDecl(final NodeList<Node> stack,
			TypeParam genType, VariableDeclAtom atom, boolean fatal) throws OocCompilationError, EOFException {
		
		int varDeclIndex = stack.find(VariableDecl.class);
		VariableDecl decl = (VariableDecl) stack.get(varDeclIndex);
		
		if(decl.getType() == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Couldn't resolve type of "+decl);
			}
			return Response.LOOP;
		}
		
		Declaration typeRef = decl.getType().getRef();
		if(typeRef == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Couldn't figure out ref of type "+decl);
			}
			return Response.LOOP;
		}
		Expression result = resolveTypeParam(typeRef.getName(), stack, true);
		if(result != null) {
			decl.setType(result.getType());
		}
		atom.replace(this, null);
		
		int lineIndex = stack.find(Line.class, varDeclIndex);
		Line line = (Line) stack.get(lineIndex);		
		
		NodeList<Line> list = (NodeList<Line>) stack.get(lineIndex - 1);
		VariableAccess varAcc = new VariableAccess(atom.getName(), startToken);
		varAcc.setRef(decl);
		returnArg = new AddressOf(varAcc, startToken);
		list.addAfter(line, new Line(this));
		
		return Response.RESTART;
		
	}

	protected void autocast() {
		if(impl == null) return;

		Iterator<Expression> callArgs = arguments.iterator();
		Iterator<Argument> implArgs = impl.getThisLessArgsIter();
		while(implArgs.hasNext() && callArgs.hasNext()) {
			Expression callArg = callArgs.next();
			Argument implArg = implArgs.next();
			if(implArg.getType() == null || callArg.getType() == null) {
				continue;
			}
			if(implArg.getType().isSuperOf(callArg.getType())) {
				arguments.replace(callArg, new Cast(callArg, implArg.getType(), callArg.startToken));
			}
		}
	}

	protected String guessCorrectName(final NodeList<Node> mainStack, final Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		NodeList<FunctionDecl> funcs = new NodeList<FunctionDecl>();
		
		for(int i = mainStack.size() - 1; i >= 0; i--) {
			Node node = mainStack.get(i);
			if(!(node instanceof Scope)) continue;
			((Scope) node).getFunctions(funcs);
		}
		
		for(FunctionDecl decl: funcs) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getProtoRepr();
			}
		}
		
		Module module = (Module) mainStack.get(0);
		for(Import imp: module.getImports()) {
			for(Node node: imp.getModule().body) {
				if(node instanceof FunctionDecl) {
					FunctionDecl decl = (FunctionDecl) node;
					int distance = Levenshtein.distance(name, decl.getName());
					if(distance < bestDistance) {
						bestDistance = distance;
						bestMatch = decl.getProtoRepr();
					}
				}
			}
		}
		
		if(bestDistance > 3) return null;
		return bestMatch;
		
	}

	protected void resolveConstructorCall(final NodeList<Node> mainStack, final boolean isSuper) throws OocCompilationError {
		
		int typeIndex = mainStack.find(TypeDecl.class);
		if(typeIndex == -1) {
			throw new OocCompilationError(this, mainStack, (isSuper ? "super" : "this")
					+getArgsRepr()+" call outside a class declaration, doesn't make sense.");
		}
		TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
		if(isSuper) {
			if(!(typeDecl instanceof ClassDecl)) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in type def "
						+typeDecl.getName()+" which is not a class! wtf?");
			}
			ClassDecl classDecl = ((ClassDecl) typeDecl);
			if(classDecl.getSuperRef() == null) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in class "
						+typeDecl.getName()+" which has no super-class!");
			}
			typeDecl = classDecl.getSuperRef();
		}
		
		for(FunctionDecl decl: typeDecl.getFunctions()) {
			if(decl.getName().equals("init") && (suffix.isEmpty() || decl.getSuffix().equals(suffix))) {
				if(matchesArgs(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
	}
	
	protected boolean resolveRegular(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		impl = getFunction(name, suffix, this, stack);

		if(impl == null) {
			Module module = (Module) stack.get(0);
			for(Import imp: module.getImports()) {
				searchIn(imp.getModule());
				if(impl != null) break;
			}
		}
		
		if(impl == null) {
			int typeIndex = stack.find(TypeDecl.class);
			if(typeIndex != -1) {
				TypeDecl typeDeclaration = (TypeDecl) stack.get(typeIndex);
				for(VariableDecl varDecl: typeDeclaration.getVariables()) {
					if(varDecl.getType() instanceof FuncType && varDecl.getName().equals(name)) {
						FuncType funcType = (FuncType) varDecl.getType();
						if(matchesArgs(funcType.getDecl())) {
							impl = funcType.getDecl();
							break;
						}
					}
				}
			}
		}
		
		if(impl == null) {
			VariableDecl varDecl = getVariable(name, stack);
			if(varDecl != null) {
				if(varDecl.getName().equals(name)) {
					if(varDecl.getType() instanceof FuncType) {
						FuncType funcType = (FuncType) varDecl.getType();
						impl = funcType.getDecl();
					} else {
						if(varDecl.getType() == null) return false;
						throw new OocCompilationError(this, stack, "Trying to call "
								+name+", which isn't a function pointer (Func), but a "+varDecl.getType());
					}
				}
			}
		}

		if(impl != null) {
			if(impl.isMember()) transformToMemberCall(stack, res);
			return true;
		}
		
		return false;
		
	}

	private void transformToMemberCall(final NodeList<Node> stack,
			final Resolver res) throws IOException {
		MemberCall memberCall = null;
		if(impl.isStatic()) {
			memberCall = new MemberCall(new VariableAccess(impl.getTypeDecl().getType().getName(), startToken), this, startToken);
		} else {
			VariableAccess thisAccess = new VariableAccess("this", startToken);
			thisAccess.resolve(stack, res, true);
			memberCall = new MemberCall(thisAccess, this, startToken);
		}
		memberCall.setImpl(impl);
		stack.peek().replace(this, memberCall);
	}
	
	protected void searchIn(Module module) {
		for(Node node: module.getBody()) {
			if(node instanceof FunctionDecl) {
				FunctionDecl decl = (FunctionDecl) node;
				if(matches(decl)) {
					impl = decl;
					return;
				}
			}
		}
	}

	public boolean matches(FunctionDecl decl) {
		return matchesName(decl) && matchesArgs(decl);
	}

	public boolean matchesArgs(FunctionDecl decl) {
		int numArgs = decl.getArguments().size();
		if(decl.hasThis()) numArgs--;
		
		if(numArgs == arguments.size()
			|| ((numArgs > 0 && decl.getArguments().getLast() instanceof VarArg)
			&& (numArgs - 1 <= arguments.size()))) {
			return true;
		}
		return false;
	}

	public boolean matchesName(FunctionDecl decl) {
		return decl.isNamed(name, suffix);
	}
	
	public String getArgsRepr() {
		StringBuilder sB = new StringBuilder();
		sB.append('(');
		Iterator<Expression> iter = arguments.iterator();
		while(iter.hasNext()) {
			Expression arg = iter.next();
			sB.append(arg.getType()+":"+arg);
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
	}

	public boolean isConstructorCall() {
		return name.equals("this") || name.equals("super");
	}
	
	public String getProtoRepr() {
		if(suffix.isEmpty()) {
			return name+getArgsRepr();
		}
		return name+"~"+suffix+getArgsRepr();
	}
	
	@Override
	public String toString() {
		return getProtoRepr();
	}

	public int getScore(FunctionDecl decl) {
		int score = 0;
		
		NodeList<Argument> declArgs = decl.getArguments();
		if(matchesArgs(decl)) {
			score += 10;
		} else {
			return 0;
		}
		
		if(declArgs.size() == 0) return score;
		
		Iterator<Argument> declIter = declArgs.iterator();
		if(decl.hasThis() && declIter.hasNext()) declIter.next();
		Iterator<Expression> callIter = arguments.iterator();
		while(callIter.hasNext()) {
			Argument declArg = declIter.next();
			Expression callArg = callIter.next();
			if(declArg.getType() == null) {
				return -1;
			}
			if(declArg.getType().equals(callArg.getType())) {
				score += 10;
			}
		}
		
		return score;
	}

	public void throwUnresolvedType(NodeList<Node> stack, String typeName) {
		
		if(impl != null) {
			throw new OocCompilationError(this, stack, "Couldn't figure out generic type <"+typeName+"> for "+impl);
		}
		throw new OocCompilationError(this, stack, "Couldn't figure out generic type <"+typeName+"> for "+getProtoRepr());
		
	}
	
}

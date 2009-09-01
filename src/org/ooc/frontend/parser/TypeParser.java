package org.ooc.frontend.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class TypeParser {

	public static Type parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {
		
		String name = "";
		int pointerLevel = 0;
		int referenceLevel = 0;
		boolean isArray = false;
		List<Type> typeParams = null;
		
		Token startToken = reader.peek();
		
		//TODO add more checks
		while(reader.hasNext()) {
			Token t = reader.peek();
			if(t.type == TokenType.UNSIGNED) {
				reader.skip();
				name += "unsigned ";
			} else if(t.type == TokenType.SIGNED) {
				reader.skip();
				name += "signed ";
			} else if(t.type == TokenType.LONG) {
				reader.skip();
				name += "long ";
			} else if(t.type == TokenType.STRUCT) {
				reader.skip();
				name += "struct ";
			} else if(t.type == TokenType.UNION) {
				reader.skip();
				name += "union ";
			} else break;
		}
			
		if(reader.peek().type == TokenType.NAME) {
			name += reader.read().get(sReader);
		}
		
		if(name.equals("Func")) {
			FuncType funcType = new FuncType(startToken);
			ArgumentListFiller.fill(module, sReader, reader, true, funcType.getDecl().getArguments());
			if(reader.peek().type == TokenType.ARROW) {
				reader.read();
				funcType.getDecl().setReturnType(TypeParser.parse(module, sReader, reader));
				if(funcType.getDecl().getReturnType() == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected function pointer return type after the arrow '->'");
				}
			}
			return funcType;
		}
		
		if(reader.peek().type == TokenType.LESSTHAN) {
			reader.skip();
			while(reader.peek().type != TokenType.GREATERTHAN) {
				Type innerType = TypeParser.parse(module, sReader, reader);
				if(innerType == null) {
					typeParams = null;
					break;
				}
				if(typeParams == null) typeParams = new ArrayList<Type>(); 
				typeParams.add(innerType);
				if(reader.peek().type != TokenType.COMMA) break;
			}
			if(reader.read().type != TokenType.GREATERTHAN) {
				typeParams = null;
			}
		}

		while(reader.peek().type == TokenType.OPEN_SQUAR) {
			reader.skip();
			if(reader.read().type != TokenType.CLOS_SQUAR) {
				return null;
			}
			pointerLevel++;
			isArray = true;
		}
		
		while(reader.peek().type == TokenType.STAR) {
			pointerLevel++;
			reader.skip();
		}
		
		while(reader.peek().type == TokenType.AT) {
			referenceLevel++;
			reader.skip();
		}
		
		if(!name.isEmpty()) {
			Type type = new Type(name.trim(), pointerLevel, referenceLevel, startToken);
			if(name.equals("This")) {
				System.out.println("Just parsed type 'This', stack = "+module.parseStack);
				if(!module.parseStack.isEmpty()) {
					Declaration decl = (Declaration) module.parseStack.peek();
					type.setRef(decl);
					type.setName(decl.getName());
				}
			}
			type.setArray(isArray);
			if(typeParams != null) type.getTypeParams().addAll(typeParams);
			return type;
		}
		return null;
		
	}
	
}

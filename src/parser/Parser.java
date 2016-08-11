package parser;

import ast.misc.Splitter;
import ast.misc.DuplicateSplitter;
import ast.misc.RoundRobinJoiner;
import ast.misc.RoundRobinSplitter;
import ast.programElements.Struct;
import ast.programElements.NamedPipeline;
import ast.programElements.NamedSplitJoin;
import ast.programElements.NamedFeedbackLoop;
import ast.programElements.Native;
import ast.programElements.NamedFilter;
import ast.programElements.ProgramElement;
import ast.programElements.NativeFunction;
import ast.programElements.Helper;
import ast.programElements.Static;
import ast.statements.streamIT.Loop;
import ast.statements.If;
import ast.statements.Assignment;
import ast.statements.Empty;
import ast.statements.streamIT.Enqueue;
import ast.statements.Break;
import ast.statements.streamIT.Push;
import ast.statements.Do;
import ast.statements.For;
import ast.statements.streamIT.Split;
import ast.statements.Continue;
import ast.statements.Return;
import ast.statements.streamIT.Join;
import ast.statements.Statement;
import ast.statements.streamIT.Add;
import ast.statements.ExpressionStmt;
import ast.statements.Block;
import ast.statements.While;
import ast.statements.streamIT.Body;
import ast.streamNodes.StreamNode;
import ast.streamNodes.AnonSplitJoin;
import ast.streamNodes.AnonPipeline;
import ast.streamNodes.AnonFeedbackLoop;
import ast.streamNodes.AnonFilter;
import ast.streamNodes.NamedStream;
import ast.types.streamIT.StreamType;
import ast.types.Type;
import ast.types.NamedType;
import ast.types.Array;
import ast.members.Function;
import ast.members.Init;
import ast.members.Member;
import ast.members.Work;
import ast.members.Handler;
import ast.expressions.streamIT.Peek;
import ast.expressions.streamIT.Float2;
import ast.expressions.streamIT.Float4;
import ast.expressions.Identifier;
import ast.expressions.streamIT.Range;
import ast.expressions.streamIT.Float3;
import ast.expressions.ArrayIndex;
import ast.expressions.ComplexConstant;
import ast.expressions.Binary;
import ast.expressions.Cast;
import ast.expressions.Call;
import ast.expressions.streamIT.Pop;
import ast.expressions.ArrayInit;
import ast.expressions.Prefix;
import ast.expressions.Expression;
import ast.expressions.ParenExpr;
import ast.expressions.PostFix;
import ast.expressions.Constant;
import ast.expressions.streamIT.Iter;
import ast.expressions.Expression0;
import ast.expressions.Ternary;
import ast.expressions.Field;
import ast.statements.VarDecl;
import ast.*;
import ast.statements.Statement0;
import java.io.IOException;
import java.util.*;

public class Parser {

    private final Scanner scanner;
    Token next;

    public Parser(String filename) throws IOException {
	scanner = new Scanner(filename);
	next = scanner.Next();
    }

    Object Error(String expected) {
	System.err.println("Expected " + expected + ", found " + next + " (" + scanner.value + ")");
	throw new UnsupportedOperationException("found " + next + " expected " + expected + scanner.location());
    }

    String Expect(Token token) throws IOException {
	String value = scanner.value;
	if (token != next) {
	    Error(token.toString());
	}
	next = scanner.Next();
	return value;
    }

    public Program Parse() throws IOException {
	// (struct_decl|stream_decl|native_decl|helper_decl|global_decl)* EOF;
	Program program = new Program();

	while (!scanner.eof()) {
	    if (next == Token.Struct) {
		ProgramElement e = struct_decl();
		program.elements.add(e);
	    } else if (next == Token.Native) {
		ProgramElement e = native_decl();
		program.elements.add(e);
	    } else if (next == Token.Helper) {
		ProgramElement e = helper_decl();
		program.elements.add(e);
	    } else if (next == Token.Static) {
		Static e = global_decl();
		program.statics.add(e);
	    } else {
		ProgramElement e = stream_decl();
		program.elements.add(e);
	    }
	}
	return program;
    }

    Struct struct_decl() throws IOException {
	// TK_struct ID LCURLY (param_decl SEMI)* RCURLY;
	Expect(Token.Struct);
	String id = Expect(Token.Identifier);
	Expect(Token.OpenCurly);
	List<VarDecl> params = new ArrayList<>();
	while (next != Token.CloseCurly) {
	    VarDecl param = param_decl();
	    params.add(param);
	    Expect(Token.SemiColon);
	}
	Expect(Token.CloseCurly);
	return new Struct(id, params);
    }

    Native native_decl() throws IOException {
	// TK_native ID LCURLY (native_function_decl)* RCURLY;
	Expect(Token.Native);
	String id = Expect(Token.Identifier);
	Expect(Token.OpenCurly);
	List<NativeFunction> decls = new ArrayList<>();
	while (next != Token.CloseCurly) {
	    NativeFunction decl = native_function_decl();
	    decls.add(decl);
	}
	Expect(Token.CloseCurly);
	return new Native(id, decls);
    }

    NativeFunction native_function_decl() throws IOException {
	// data_type ID param_decl_list SEMI;
	Type type = data_type();
	String id = Expect(Token.Identifier);
	List<VarDecl> params = param_decl_list();
	Expect(Token.SemiColon);
	return new NativeFunction(type, id, params);
    }

    Helper helper_decl() throws IOException {
	// TK_helper ID LCURLY (function_decl)* RCURLY;
	Expect(Token.Helper);
	String id = Expect(Token.Identifier);
	Expect(Token.OpenCurly);
	List<Function> functions = new ArrayList<>();
	while (next != Token.CloseCurly) {
	    Function decl = function_decl();
	    functions.add(decl);
	}
	Expect(Token.CloseCurly);
	return new Helper(id, functions);
    }

    Static global_decl() throws IOException {
	// TK_static global_body;
	Expect(Token.Static);
	return global_body();
    }

    Static global_body() throws IOException {
	// LCURLY (field_decl SEMI)* (init_decl)? RCURLY;
	Expect(Token.OpenCurly);
	Static global = new Static();
	while (next != Token.Init && next != Token.CloseCurly) {
	    List<VarDecl> field = field_decl();
	    global.decls.addAll(field);
	    Expect(Token.SemiColon);
	}
	if (next == Token.Init) {
	    global.init = init_decl();
	}
	Expect(Token.CloseCurly);
	return global;
    }

    ProgramElement stream_decl() throws IOException {
	// stream_type_decl (filter_decl|struct_stream_decl);
	StreamType type = stream_type_decl();
	if (next == Token.Pipeline || next == Token.SplitJoin || next == Token.FeedbackLoop) {
	    return struct_stream_decl(type);
	} else {
	    return filter_decl(type);
	}
    }

    ProgramElement struct_stream_decl(StreamType type) throws IOException {
	// (TK_pipeline|TK_splitjoin|TK_feedbackloop) ID (param_decl_list)? block;
	List<VarDecl> params = new ArrayList<>();

	if (next == Token.Pipeline) {
	    Expect(Token.Pipeline);
	    String id = Expect(Token.Identifier);
	    if (next == Token.OpenParenthesis) {
		params = param_decl_list();
	    }
	    Block body = block();
	    return new NamedPipeline(type, id, params, body);
	} else if (next == Token.SplitJoin) {
	    Expect(Token.SplitJoin);
	    String id = Expect(Token.Identifier);
	    if (next == Token.OpenParenthesis) {
		params = param_decl_list();
	    }
	    Block body = block();
	    return new NamedSplitJoin(type, id, params, body);
	} else {
	    Expect(Token.FeedbackLoop);
	    String id = Expect(Token.Identifier);
	    if (next == Token.OpenParenthesis) {
		params = param_decl_list();
	    }
	    Block body = block();
	    return new NamedFeedbackLoop(type, id, params, body);
	}
    }

    StreamType stream_type_decl() throws IOException {
	// data_type ARROW data_type;
	Type input = data_type();
	Expect(Token.Arrow);
	Type output = data_type();
	return new StreamType(input, output);
    }

    boolean isType() throws IOException {
	return next == Token.Void || next == Token.Bit || next == Token.Float || next == Token.Double || next == Token.Complex || next == Token.Boolean || next == Token.Float2 || next == Token.Float3 || next == Token.Float4 ||
                next == Token.Int8 || next == Token.UInt8 || next == Token.Int16 || next == Token.UInt16 || next == Token.Int32 || next == Token.UInt32 || next == Token.Int64 || next == Token.UInt64;    }

    Type data_type() throws IOException {
	//(primitive_type|ID) (LSQUARE (right_expr|) RSQUARE)* |TK_void;        
	Type type;
	if (next == Token.Void) {
	    Expect(Token.Void);
	    type = new NamedType("void");
	} else if (next == Token.Identifier) {
	    String id = Expect(Token.Identifier);
	    type = new NamedType(id);
	} else {
	    type = primitive_type();
	}

	return array_type(type);
    }

    Type array_type(Type type) throws IOException {
	if (next == Token.OpenBracket) {
	    Expect(Token.OpenBracket);
	    Expression0 size = null;
	    if (next != Token.CloseBracket) {
		size = expression();
	    }
	    Expect(Token.CloseBracket);
	    return new Array(array_type(type), size);
	} else {
	    return type;
	}
    }

    Expression0 float2_initializer() throws IOException {
	// TK_float2 LPAREN right_expr COMMA right_expr RPAREN;
	Expect(Token.Float2);
	Expect(Token.OpenParenthesis);
	Expression0 e1 = expression();
	Expect(Token.Comma);
	Expression0 e2 = expression();
	Expect(Token.CloseParenthesis);
	return new Float2(e1, e2);
    }

    Expression0 float3_initializer() throws IOException {
	// TK_float3 LPAREN right_expr COMMA right_expr COMMA right_expr RPAREN; 
	Expect(Token.Float3);
	Expect(Token.OpenParenthesis);
	Expression0 e1 = expression();
	Expect(Token.Comma);
	Expression0 e2 = expression();
	Expect(Token.Comma);
	Expression0 e3 = expression();
	Expect(Token.CloseParenthesis);
	return new Float3(e1, e2, e3);
    }

    Expression0 float4_initializer() throws IOException {
	// TK_float4 LPAREN right_expr COMMA right_expr COMMA right_expr COMMA right_expr RPAREN;
	Expect(Token.Float4);
	Expect(Token.OpenParenthesis);
	Expression0 e1 = expression();
	Expect(Token.Comma);
	Expression0 e2 = expression();
	Expect(Token.Comma);
	Expression0 e3 = expression();
	Expect(Token.Comma);
	Expression0 e4 = expression();
	Expect(Token.CloseParenthesis);
	return new Float4(e1, e2, e3, e4);
    }

    Expression0 streamit_value_expr() throws IOException {
	// TK_pop LPAREN RPAREN|TK_peek LPAREN right_expr RPAREN|TK_iter LPAREN RPAREN;
	if (next == Token.Pop) {
	    Expect(Token.Pop);
	    Expect(Token.OpenParenthesis);
	    Expect(Token.CloseParenthesis);
	    return new Pop();
	} else if (next == Token.Peek) {
	    Expect(Token.Peek);
	    Expect(Token.OpenParenthesis);
	    Expression0 e = expression();
	    Expect(Token.CloseParenthesis);
	    return new Peek(e);
	} else {
	    Expect(Token.Iter);
	    Expect(Token.OpenParenthesis);
	    Expect(Token.CloseParenthesis);
	    return new Iter();
	}
    }

    NamedFilter filter_decl(StreamType type) throws IOException {
	// (stateful_decl)? TK_filter ID (param_decl_list)? filter_body;
	boolean stateful = false;
	if (next == Token.Stateful) {
	    Expect(Token.Stateful);
	    stateful = true;
	}

	Expect(Token.Filter);
	String id = Expect(Token.Identifier);
	List<VarDecl> params = new ArrayList<>();
	if (next == Token.OpenParenthesis) {
	    params = param_decl_list();
	}

	NamedFilter filter = new NamedFilter(type, stateful, id, params);

	filter_body(filter);
	return filter;
    }

    List<VarDecl> param_decl_list() throws IOException {
	// LPAREN (param_decl (COMMA param_decl)*)? RPAREN;
	List<VarDecl> params = new ArrayList<>();
	Expect(Token.OpenParenthesis);
	if (next != Token.CloseParenthesis) {
	    VarDecl p = param_decl();
	    params.add(p);
	    while (next == Token.Comma) {
		Expect(Token.Comma);
		VarDecl p2 = param_decl();
		params.add(p2);
	    }
	}
	Expect(Token.CloseParenthesis);
	return params;
    }

    VarDecl param_decl() throws IOException {
	// data_type ID;
	Type type = data_type();
	String id = Expect(Token.Identifier);
	return new VarDecl(type, id, null);
    }

    void filter_body(Filter filter) throws IOException {
	// LCURLY (init_decl|work_decl|function_decl|handler_decl|field_decl SEMI)* RCURLY;
	Expect(Token.OpenCurly);
	while (next != Token.CloseCurly) {
	    if (next == Token.Init) {
		filter.init = init_decl();
	    } else if (next == Token.PreWork) {
		filter.prework = work_decl();
	    } else if (next == Token.Work) {
		filter.work = work_decl();
	    } else if (next == Token.Handler) {
		Member handler = handler_decl();
		filter.body.add(handler);
	    } else {

		Type type = data_type();
		String id = Expect(Token.Identifier);
		if (next == Token.OpenParenthesis) {
		    List<VarDecl> params = param_decl_list();
		    Function function = new Function(type, id, params);
		    while (next == Token.Push || next == Token.Pop || next == Token.Peek) {
			String which = Expect(next);
			Expression0 rate = rate_expr();
			switch (which) {
			    case "push":
				function.push = new Expression(rate);
				break;
			    case "pop":
				function.pop = new Expression(rate);
				break;
			    default:
				function.peek = new Expression(rate);
				break;
			}
		    }
		    function.body = new Statement(block());
		    filter.functions.add(function);
		} else {
		    List<VarDecl> vars = new ArrayList<>();
		    if (next == Token.Assign) {
			Expect(Token.Assign);
			Expression0 value = var_initializer();
			vars.add(new VarDecl(type, id, value));
		    } else {
			vars.add(new VarDecl(type, id, null));
		    }
		    while (next == Token.Comma) {
			Expect(Token.Comma);
			id = Expect(Token.Identifier);
			if (next == Token.Assign) {
			    Expect(Token.Assign);
			    Expression0 value = var_initializer();
			    vars.add(new VarDecl(type, id, value));
			} else {
			    vars.add(new VarDecl(type, id, null));
			}
		    }
		    Expect(Token.SemiColon);
		    filter.decls.addAll(vars);
		}
	    }
	}
	Expect(Token.CloseCurly);
    }

    Function function_decl() throws IOException {
	// data_type ID param_decl_list (TK_push rate_expr|TK_pop rate_expr|TK_peek rate_expr)* block;
	Type returnType = data_type();
	String id = Expect(Token.Identifier);
	List<VarDecl> params = param_decl_list();
	Function function = new Function(returnType, id, params);
	while (next == Token.Push || next == Token.Pop || next == Token.Peek) {
	    String which = Expect(next);
	    Expression0 rate = rate_expr();
	    if (null != which) switch (which) {
                case "push":
                    function.push = new Expression(rate);
                    break;
                case "pop":
                    function.pop = new Expression(rate);
                    break;
                case "peek":
                    function.peek = new Expression(rate);
                    break;
            }
	}
	function.body = new Statement(block());
	return function;
    }

    List<VarDecl> field_decl() throws IOException {
	// data_type ID (ASSIGN var_initializer)? (COMMA ID (ASSIGN var_initializer)?)*;
	Type type = data_type();
	List<VarDecl> vars = new ArrayList<>();
	String id = Expect(Token.Identifier);
	if (next == Token.Assign) {
	    Expect(Token.Assign);
	    Expression0 value = var_initializer();
	    vars.add(new VarDecl(type, id, value));
	} else {
	    vars.add(new VarDecl(type, id, null));
	}
	while (next == Token.Comma) {
	    Expect(Token.Comma);
	    id = Expect(Token.Identifier);
	    if (next == Token.Assign) {
		Expect(Token.Assign);
		Expression0 value = var_initializer();
		vars.add(new VarDecl(type, id, value));
	    } else {
		vars.add(new VarDecl(type, id, null));
	    }
	}
	return vars;
    }

    Handler handler_decl() throws IOException {
	// TK_handler ID param_decl_list block;
	Expect(Token.Handler);
	String id = Expect(Token.Identifier);
	List<VarDecl> params = param_decl_list();
	Block body = block();
	return new Handler(id, params, body);
    }

    Init init_decl() throws IOException {
	// TK_Init block
	Expect(Token.Init);
	Block body = block();
	return new Init(body);
    }

    void statement(List<Statement0> stmts) throws IOException {
	// add_statement|body_statement|loop_statement|split_statement SEMI|join_statement SEMI|enqueue_statement SEMI|push_statement SEMI|
	// block|variable_decl SEMI!|expr_statement SEMI!|TK_break SEMI |TK_continue SEMI |return_statement SEMI|
	// if_else_statement|while_statement|do_while_statement SEMI|for_statement|msg_statement SEMI|SEMI;
	if (next == Token.Add) {
	    stmts.add(add_statement());
	} else if (next == Token.Body) {
	    stmts.add(body_statement());
	} else if (next == Token.Loop) {
	    stmts.add(loop_statement());
	} else if (next == Token.Split) {
	    Statement0 split = split_statement();
	    Expect(Token.SemiColon);
	    stmts.add(split);
	} else if (next == Token.Join) {
	    Statement0 join = join_statement();
	    Expect(Token.SemiColon);
	    stmts.add(join);
	} else if (next == Token.Enqueue) {
	    Statement0 enqueue = enqueue_statement();
	    Expect(Token.SemiColon);
	    stmts.add(enqueue);
	} else if (next == Token.Push) {
	    Statement0 push = push_statement();
	    Expect(Token.SemiColon);
	    stmts.add(push);
	} else {
	    c_statement(stmts);
	}
    }

    Statement0 enqueue_statement() throws IOException {
	// TK_enqueue right_expr;
	Expect(Token.Enqueue);
	Expression0 e = expression();
	return new Enqueue(e);
    }

    Statement0 join_statement() throws IOException {
	// TK_join splitter_or_joiner;
	Expect(Token.Join);
	RoundRobinJoiner s = joiner();
	return new Join(s);
    }

    Statement0 push_statement() throws IOException {
	// TK_push LPAREN right_expr RPAREN;
	Expect(Token.Push);
	Expect(Token.OpenParenthesis);
	Expression0 e = expression();
	Expect(Token.CloseParenthesis);
	return new Push(e);
    }

    Statement0 add_statement() throws IOException {
	// TK_add stream_creator;
	Expect(Token.Add);
	StreamNode s = stream_creator();
	return new Add(s);
    }

    Statement0 body_statement() throws IOException {
	// TK_body stream_creator; 
	Expect(Token.Body);
	StreamNode body = stream_creator();
	return new Body(body);
    }

    Statement0 loop_statement() throws IOException {
	// TK_loop stream_creator; 
	Expect(Token.Loop);
	StreamNode loop = stream_creator();
	return new Loop(loop);
    }

    Statement0 split_statement() throws IOException {
	// TK_split splitter_or_joiner;
	Expect(Token.Split);
	Splitter split = splitter();
	return new Split(split);
    }

    StreamNode stream_creator() throws IOException {
	// anonymous_stream|named_stream SEMI;
	if (next == Token.Identifier) {
	    StreamNode named = named_stream();
	    Expect(Token.SemiColon);
	    return named;
	} else {
	    return anonymous_stream();
	}
    }

    StreamNode anonymous_stream() throws IOException {
	// (stream_type_decl)? ((stateful_decl)? TK_filter filter_body (SEMI)?
	// | (TK_pipeline|TK_splitjoin|TK_feedbackloop) block (SEMI)?);

	StreamType type = null;
	if (isType()) {
	    type = stream_type_decl();
	}

	if (next == Token.Pipeline || next == Token.SplitJoin || next == Token.FeedbackLoop) {
	    String stream = Expect(next);
	    Statement0 block = block();
	    if (next == Token.SemiColon) {
		Expect(Token.SemiColon);
	    }
	    switch (stream) {
                case "pipeline": return new AnonPipeline(type, block);
                case "splitjoin": return new AnonSplitJoin(type, block);
                default: return new AnonFeedbackLoop(type, block);
	    }
	} else {
	    boolean stateful = false;
	    if (next == Token.Stateful) {
		Expect(Token.Stateful);
		stateful = true;
	    }
	    Expect(Token.Filter);
	    AnonFilter filter = new AnonFilter(type, stateful);
	    filter_body(filter);
	    if (next == Token.SemiColon) {
		Expect(Token.SemiColon);
	    }

	    return filter;
	}
    }

    StreamNode named_stream() throws IOException {
	// ID (LESS_THAN data_type MORE_THAN)? (func_call_params)?;
	String id = Expect(Token.Identifier);
	Type type = null;
	if (next == Token.LessThan) {
	    Expect(Token.LessThan);
	    type = data_type();
	    Expect(Token.GreaterThan);
	}
	List<Expression> params;
	if (next == Token.OpenParenthesis) {
	    params = func_call_params();
	} else {
	    params = new ArrayList<>();
	}

	return new NamedStream(id, type, params);
    }

    Splitter splitter() throws IOException {
	// TK_roundrobin (LPAREN RPAREN|LPAREN right_expr RPAREN|func_call_params|)|TK_duplicate (LPAREN RPAREN)?;
	if (next == Token.RoundRobin) {
	    Expect(Token.RoundRobin);
	    List<Expression> list = new ArrayList<>();
	    if (next == Token.OpenParenthesis) {
		Expect(Token.OpenParenthesis);
		if (next != Token.CloseParenthesis) {
		    Expression0 e = expression();
		    list.add(new Expression(e));
		    while (next == Token.Comma) {
			Expect(Token.Comma);
			Expression0 e2 = expression();
			list.add(new Expression(e2));
		    }
		}
		Expect(Token.CloseParenthesis);
	    }
	    return new RoundRobinSplitter(list);
	} else {
	    Expect(Token.Duplicate);
	    if (next == Token.OpenParenthesis) {
		Expect(Token.OpenParenthesis);
		Expect(Token.CloseParenthesis);
	    }
	    return new DuplicateSplitter();
	}
    }

    RoundRobinJoiner joiner() throws IOException {
	// TK_roundrobin (LPAREN RPAREN|LPAREN right_expr RPAREN|func_call_params|);
	Expect(Token.RoundRobin);
	List<Expression> list = new ArrayList<>();
	if (next == Token.OpenParenthesis) {
	    Expect(Token.OpenParenthesis);
	    if (next != Token.CloseParenthesis) {
		Expression0 e = expression();
		list.add(new Expression(e));
		while (next == Token.Comma) {
		    Expect(Token.Comma);
		    Expression0 e2 = expression();
		    list.add(new Expression(e2));
		}
	    }
	    Expect(Token.CloseParenthesis);
	}
	return new RoundRobinJoiner(list);
    }

    Work work_decl() throws IOException {
	// (TK_work|TK_prework) (TK_push rate_expr|TK_pop rate_expr|TK_peek rate_expr)* block;
	if (next == Token.Work) {
	    Expect(Token.Work);
	    Work decl = new Work();
	    while (next == Token.Push || next == Token.Pop || next == Token.Peek) {
		String which = Expect(next);
		Expression0 rate = rate_expr();
		switch (which) {
		    case "push":
			decl.push = new Expression(rate);
			break;
		    case "pop":
			decl.pop = new Expression(rate);
			break;
		    default:
			decl.peek = new Expression(rate);
			break;
		}
	    }
	    decl.body = block();
	    return decl;
	} else {
	    Expect(Token.PreWork);
	    Work decl = new Work();
	    while (next == Token.Push || next == Token.Pop || next == Token.Peek) {
		String which = Expect(next);
		Expression0 rate = rate_expr();
		switch (which) {
		    case "push":
			decl.push = new Expression(rate);
			break;
		    case "pop":
			decl.pop = new Expression(rate);
			break;
		    default:
			decl.peek = new Expression(rate);
			break;
		}
	    }
	    decl.body = block();
	    return decl;
	}
    }

    Expression0 rate_expr() throws IOException {
	// (STAR)|range_expr|right_expr;
	if (next == Token.Mult) {
	    Expect(Token.Mult);
	    throw new UnsupportedOperationException("Dynamic Rates - Not supported yet.");
	} else if (next == Token.OpenBracket) {
	    return range_expr();
	} else {
	    return expression();
	}
    }

    Expression0 range_expr() throws IOException {
	// (LSQUARE dynamic_expr COMMA dynamic_expr (COMMA dynamic_expr)? RSQUARE);
	Expect(Token.OpenBracket);
	Expression0 e1 = dynamic_expr();
	Expect(Token.Comma);
	Expression0 e2 = dynamic_expr();
	Expression0 e3 = null;
	if (next == Token.Comma) {
	    Expect(Token.Comma);
	    e3 = dynamic_expr();
	}
	Expect(Token.CloseBracket);
	return new Range(e1, e2, e3);
    }

    Expression0 dynamic_expr() throws IOException {
	// (STAR)|right_expr;
	if (next == Token.Mult) {
	    Expect(Token.Mult);
	    return null;
	} else {
	    return expression();
	}
    }

    // C Statements ..............................................
    Block block() throws IOException {
	// LCURLY (statement)* RCURLY;
	Expect(Token.OpenCurly);
	List<Statement> stmts = new ArrayList<>();
	while (next != Token.CloseCurly) {
	    List<Statement0> local = new ArrayList<>();
	    statement(local);
	    for (Statement0 s : local) {
		stmts.add(new Statement(s));
	    }
	}
	Expect(Token.CloseCurly);
	return new Block(stmts);
    }

    Statement0 single_statement() throws IOException {
	List<Statement0> stmts = new ArrayList<>();
	statement(stmts);
	assert (stmts.size() == 1);
	return stmts.get(0);
    }

    void c_statement(List<Statement0> stmts) throws IOException {
	if (next == Token.OpenCurly) {
	    stmts.add(block());
	} else if (next == Token.Break) {
	    Expect(Token.Break);
	    Expect(Token.SemiColon);
	    stmts.add(new Break());
	} else if (next == Token.Continue) {
	    Expect(Token.Continue);
	    Expect(Token.SemiColon);
	    stmts.add(new Continue());
	} else if (next == Token.Return) {
	    Statement0 value = return_statement();
	    Expect(Token.SemiColon);
	    stmts.add(value);
	} else if (next == Token.If) {
	    stmts.add(if_else_statement());
	} else if (next == Token.While) {
	    stmts.add(while_statement());
	} else if (next == Token.Do) {
	    Statement0 value = do_while_statement();
	    Expect(Token.SemiColon);
	    stmts.add(value);
	} else if (next == Token.For) {
	    stmts.add(for_statement());
	} else if (next == Token.SemiColon) {
	    Expect(Token.SemiColon);
	    stmts.add(new Empty());
	} else if (isType()) {
	    variable_decl(stmts);
	    Expect(Token.SemiColon);
	} else {
	    Statement0 value = expr_statement();
	    Expect(Token.SemiColon);
	    stmts.add(value);
	}
    }

    Statement0 return_statement() throws IOException {
	// TK_return (right_expr)?;
	Expect(Token.Return);
	if (next != Token.SemiColon) {
	    return new Return(expression());
	} else {
	    return new Return(null);
	}
    }

    Statement0 do_while_statement() throws IOException {
	//TK_do statement TK_while LPAREN right_expr RPAREN;
	Expect(Token.Do);
	Statement0 stmt = single_statement();
	Expect(Token.While);
	Expect(Token.OpenParenthesis);
	Expression0 cond = expression();
	Expect(Token.CloseParenthesis);
	return new Do(stmt, cond);
    }

    List<Expression> func_call_params() throws IOException {
	// LPAREN (right_expr (COMMA right_expr)*)? RPAREN ;
	List<Expression> params = new ArrayList<>();
	Expect(Token.OpenParenthesis);
	if (next != Token.CloseParenthesis) {
	    Expression0 e1 = expression();
	    params.add(new Expression(e1));
	    while (next == Token.Comma) {
		Expect(Token.Comma);
		Expression0 e2 = expression();
		params.add(new Expression(e2));
	    }
	}
	Expect(Token.CloseParenthesis);
	return params;
    }

    Statement0 if_else_statement() throws IOException {
	// TK_if LPAREN right_expr RPAREN statement (TK_else statement)? ;
	Expect(Token.If);
	Expect(Token.OpenParenthesis);
	Expression0 cond = expression();
	Expect(Token.CloseParenthesis);
	Statement0 Then = single_statement();
	Statement0 Else = null;
	if (next == Token.Else) {
	    Expect(Token.Else);
	    Else = single_statement();
	}
	return new If(cond, Then, Else);
    }

    Statement0 while_statement() throws IOException {
	// TK_while LPAREN right_expr RPAREN statement;
	Expect(Token.While);
	Expect(Token.OpenParenthesis);
	Expression0 cond = expression();
	Expect(Token.CloseParenthesis);
	Statement0 stmt = single_statement();
	return new While(cond, stmt);
    }

    Statement0 for_statement() throws IOException {
	// TK_for LPAREN for_init_statement SEMI (right_expr|) SEMI for_incr_statement RPAREN statement;
	Expect(Token.For);
	Expect(Token.OpenParenthesis);
	List<Statement0> init = for_init_statement();
	Expect(Token.SemiColon);
	Expression0 cond = null;
	if (next != Token.SemiColon) {
	    cond = expression();
	}
	Expect(Token.SemiColon);
	Statement0 incr = for_incr_statement();
	Expect(Token.CloseParenthesis);
	Statement0 stmt = single_statement();
	return new For(init, cond, incr, stmt);
    }

    List<Statement0> for_init_statement() throws IOException {
	// variable_decl|expr_statement|(SEMI);
	List<Statement0> inits = new ArrayList<>();
	if (next == Token.SemiColon) {
	} else if (isType()) {
	    variable_decl(inits);
	} else {
	    inits.add(expr_statement());
	}
	return inits;
    }

    void variable_decl(List<Statement0> stmts) throws IOException {
	// data_type ID (ASSIGN var_initializer)? (COMMA ID (ASSIGN var_initializer)?)* ;
	Type type = data_type();
	String id = Expect(Token.Identifier);
	Expression0 value = null;
	if (next == Token.Assign) {
	    Expect(Token.Assign);
	    value = var_initializer();

	}
	stmts.add(new VarDecl(type, id, value));
	while (next == Token.Comma) {
	    Expect(Token.Comma);

	    id = Expect(Token.Identifier);
	    if (next == Token.Assign) {
		Expect(Token.Assign);
		value = var_initializer();
	    }
	    stmts.add(new VarDecl(type, id, value));
	}
    }

    Expression0 var_initializer() throws IOException {
	// right_expr|arr_initializer;
	if (next == Token.OpenCurly) {
	    return arr_initializer();
	} else {
	    return expression();
	}
    }

    Expression0 arr_initializer() throws IOException {
	// LCURLY (var_initializer (COMMA var_initializer)*)? RCURLY;
	List<Expression> elements = new ArrayList<>();
	Expect(Token.OpenCurly);
	if (next != Token.CloseCurly) {
	    Expression0 e1 = var_initializer();
	    elements.add(new Expression(e1));
	    while (next == Token.Comma) {
		Expect(Token.Comma);
		Expression0 e2 = var_initializer();
		elements.add(new Expression(e2));
	    }
	}
	Expect(Token.CloseCurly);
	return new ArrayInit(elements);
    }

    Statement0 for_incr_statement() throws IOException {
	// expr_statement|;
	if (next != Token.CloseParenthesis) {
	    return expr_statement();
	} else {
	    return null;
	}
    }

    Statement0 expr_statement() throws IOException {
	Expression0 e1 = expression();
	switch (next) {
	    case Assign:
	    case PlusAssign:
	    case MinusAssign:
	    case MultAssign:
	    case DivAssign:
	    case LeftShiftAssign:
	    case RightShiftAssign:
		String op = Expect(next);
		Expression0 e2 = expression();
		return new Assignment(e1, op, e2);
	    default:
		return new ExpressionStmt(e1);
	}
    }

    // Expressions .......................................
    Expression0 expression() throws IOException {
	// ternaryExpr|float2_initializer|float3_initializer|float4_initializer;
	if (next == Token.Float2) {
	    return float2_initializer();
	} else if (next == Token.Float3) {
	    return float3_initializer();
	} else if (next == Token.Float4) {
	    return float4_initializer();
	} else {
	    return ternaryExpr();
	}
    }

    Expression0 ternaryExpr() throws IOException {
	// logicOrExpr (QUESTION ternaryExpr COLON ternaryExpr)?;
	Expression0 e1 = logicOrExpr();
	if (next == Token.Question) {
	    Expect(Token.Question);
	    Expression0 e2 = ternaryExpr();
	    Expect(Token.Colon);
	    Expression0 e3 = ternaryExpr();
	    e1 = new Ternary(e1, e2, e3);
	}
	return e1;
    }

    Expression0 logicOrExpr() throws IOException {
	//  logicAndExpr (LOGIC_OR logicAndExpr)*;
	Expression0 e1 = logicAndExpr();
	while (next == Token.OrOr) {
	    String op = Expect(Token.OrOr);
	    Expression0 e2 = logicAndExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 logicAndExpr() throws IOException {
	// bitwiseExpr (LOGIC_AND bitwiseExpr)*;
	Expression0 e1 = bitwiseExpr();
	while (next == Token.AndAnd) {
	    String op = Expect(Token.AndAnd);
	    Expression0 e2 = bitwiseExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 bitwiseExpr() throws IOException {
	// equalExpr ((BITWISE_OR|BITWISE_AND|BITWISE_XOR) equalExpr)*;
	Expression0 e1 = equalExpr();
	while (next == Token.Or || next == Token.And || next == Token.XOr) {
	    String op = Expect(next);
	    Expression0 e2 = equalExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 equalExpr() throws IOException {
	// compareExpr((EQUAL|NOT_EQUAL) compareExpr)*;
	Expression0 e1 = compareExpr();
	while (next == Token.Equals || next == Token.NotEquals) {
	    String op = Expect(next);
	    Expression0 e2 = compareExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 compareExpr() throws IOException {
	// addExpr ((LESS_THAN|LESS_EQUAL|MORE_THAN|MORE_EQUAL) addExpr)*;
	Expression0 e1 = addExpr();
	while (next == Token.LessThan || next == Token.GreaterThan || next == Token.LessEquals || next == Token.GreaterEquals) {
	    String op = Expect(next);
	    Expression0 e2 = addExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 addExpr() throws IOException {
	// multExpr ((PLUS|MINUS) multExpr)*;
	Expression0 e1 = multExpr();
	while (next == Token.Plus || next == Token.Minus) {
	    String op = Expect(next);
	    Expression0 e2 = multExpr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 multExpr() throws IOException {
	// castExpr ((STAR|DIV|MOD|LSHIFT|RSHIFT) castExpr)*;
	Expression0 e1 = prefix_expr();
	while (next == Token.Mult || next == Token.Div || next == Token.Mod || next == Token.LeftShift || next == Token.RightShift) {
	    String op = Expect(next);
	    Expression0 e2 = prefix_expr();
	    e1 = new Binary(e1, op, e2);
	}
	return e1;
    }

    Expression0 prefix_expr() throws IOException {
	if (next == Token.Not || next == Token.Tilde || next == Token.Minus || next == Token.Plus) {
	    String op = Expect(next);
	    Expression0 e = prefix_expr();
	    return new Prefix(op, e);
	} else {
	    return value_expr();
	}
    }

    Expression0 value_expr() throws IOException {
	if (next == Token.OpenParenthesis) {
	    Expect(Token.OpenParenthesis);
	    if (isType()) {
		NamedType type = primitive_type();
		Expect(Token.CloseParenthesis);
		Expression0 value = prefix_expr();
		return new Cast(type, value);
	    } else {
		Expression0 e = expression();
		Expect(Token.CloseParenthesis);
		return new ParenExpr(e);
	    }
	} else if (/*next == Token.Pi || */next == Token.True || next == Token.False || next == Token.Character || next == Token.String || next == Token.HexNumber || next == Token.IntNumber || next == Token.FloatNumber || next == Token.ComplexNumber) {
	    return constantExpr();
	} else if (next == Token.Pop || next == Token.Peek || next == Token.Iter) {
	    return streamit_value_expr();
	} else {
	    return value();
	}
    }

    Expression0 value() throws IOException {
	String op = null;
	if (next == Token.PlusPlus || next == Token.MinusMinus) {
	    op = Expect(next);
	}

	String id = Expect(Token.Identifier);

	Expression0 e = new Identifier(id);
	if (op != null) {
	    e = new Prefix(op, e);
	}

	while (next == Token.Dot || next == Token.OpenBracket || next == Token.OpenParenthesis) {
	    if (next == Token.Dot) {
		Expect(Token.Dot);
		String field = Expect(Token.Identifier);
		e = new Field(e, field);
	    } else if (next == Token.OpenBracket) {
		Expect(Token.OpenBracket);
		Expression0 index = null;
		if (next != Token.CloseBracket) {
		    index = expression();
		}
		Expect(Token.CloseBracket);
		e = new ArrayIndex(e, index);
	    } else {
		List<Expression> params = func_call_params();
		e = new Call(e, params);
	    }
	}

	if (next == Token.PlusPlus || next == Token.MinusMinus) {
	    String op2 = Expect(next);
	    e = new PostFix(e, op2);
	}

	return e;
    }

    Constant constantExpr() throws IOException {
	// HEXNUMBER|NUMBER|CHAR_LITERAL|STRING_LITERAL|TK_pi|TK_true|TK_false;
	switch (next) {
	    case True:
	    case False:
		return new Constant(Boolean.parseBoolean(Expect(next)));
	    case HexNumber:
		return new Constant(Expect(next));
	    //return new Constant(Long.parseLong(Expect(next).substring(2), 16));
	    case IntNumber:
		return new Constant(Integer.parseInt(Expect(next), 10));
	    case FloatNumber:
		return new Constant(Float.parseFloat(Expect(next)));
	    case ComplexNumber:
		return new ComplexConstant(Expect(next));
	    case Character:
	    case String:
		//case Pi:
		return new Constant(Expect(next));
	    default:
		return (Constant) Error("constant expression");
	}
    }

    NamedType primitive_type() throws IOException {
	// TK_boolean|TK_bit|TK_int|TK_float|TK_double|TK_complex|TK_float2|TK_float3|TK_float4 ;
	switch (next) {
	    case Boolean:
	    case Bit:
	    case Double:
	    case Float2:
	    case Float3:
	    case Float4:
		String type = Expect(next);
		return new NamedType(type);
	    case Complex:
		Expect(next);
		return NamedType.Complex;
	    case Float:
                Expect(next);
                return NamedType.Float;
            case Int8:
                Expect(next);
                return NamedType.Int8_t;
            case UInt8:
                Expect(next);
                return NamedType.UInt8_t;
            case Int16:
                Expect(next);
                return NamedType.Int16_t;
            case UInt16:
                Expect(next);
                return NamedType.UInt16_t;
            case Int32:
                Expect(next);
                return NamedType.Int32_t;
            case UInt32:
                Expect(next);
                return NamedType.UInt32_t;
            case Int64:
                Expect(next);
                return NamedType.Int64_t;
            case UInt64:
                Expect(next);
                return NamedType.UInt64_t;
            default:
		return (NamedType) Error("primitive type");
	}
    }
}
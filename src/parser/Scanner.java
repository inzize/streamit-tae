package parser;

import java.io.*;

public class Scanner {

    private FileReader reader = null;
    private char next = '\0';
    public int line = 1, col = 0;
    public String value = null;
    boolean eof = false;
    StringBuilder builder = new StringBuilder();
    String filename;

    public Scanner(String filename) {
        try {
            this.filename = filename;
            reader = new FileReader(filename);
            advance();
        } catch (IOException ex) {
            System.err.println("Error: can't open " + filename);
            System.exit(0);
        }
    }

    public boolean eof() {
        return eof;
    }

    public void advance() throws IOException {
        if (next != '\0') {
            builder.append(next);
        }

        try {
            int ch = reader.read();
            if (ch == -1) {
                eof = true;
            } else {
                next = (char) ch;
            }
        } catch (IOException e) {
            eof = true;
            throw e;
        }
        col++;
    }

    public void skip() throws IOException {
        try {
            int ch = reader.read();
            if (ch == -1) {
                eof = true;
            } else {
                next = (char) ch;
            }
        } catch (IOException e) {
            eof = true;
            throw e;
        }
        col++;
    }

    private void NewLine() {
        line++;
        col = 0;
    }

    public Token Next() throws IOException {
        Token t = Next0();
        value = builder.toString();
        builder = new StringBuilder();
        return t;
    }

    public Token Next0() throws IOException {
        Token token;
        boolean zero = false;

        if (eof) {
            return Token.EOF;
        }

        switch (next) {
            case '\n':
                NewLine();
            case ' ':
            case '\r':
            case '\t':
                skip();
                return Next0();
            case '{':
                token = Token.OpenCurly;
                break;
            case '}':
                token = Token.CloseCurly;
                break;
            case '=':
                advance();
                if (next == '=') {
                    token = Token.Equals;
                } else {
                    return Token.Assign;
                }
                break;
            case '%':
                token = Token.Mod;
                break;
            case '*':
                advance();
                if (next == '=') {
                    token = Token.MultAssign;
                } else {
                    return Token.Mult;
                }
                break;
            case '"':
                advance();
                while (next != '"') {
                    advance();
                }
                token = Token.String;
                break;
            case ':':
                token = Token.Colon;
                break;
            case '?':
                token = Token.Question;
                break;
            case '/':
                advance();
                if (next == '*') {
                    builder.deleteCharAt(builder.length() - 1);
                    skip();
                    do {
                        while (next != '*') {
                            if (next == '\n') {
                                NewLine();
                            }
                            skip();
                        }
                        skip();
                    } while (next != '/');
                    skip();
                    return Next0();
                } else if (next == '/') {
                    builder.deleteCharAt(builder.length() - 1);
                    while (next != '\n') {
                        skip();
                    }
                    return Next0();
                } else if (next == '=') {
                    token = Token.DivAssign;
                } else {
                    return Token.Div;
                }
                break;
            case ',':
                token = Token.Comma;
                break;
            case '<':
                advance();
                if (next == '=') {
                    token = Token.LessEquals;
                } else if (next == '<') {
                    advance();
                    if (next == '=') {
                        token = Token.LeftShiftAssign;
                    } else {
                        return Token.LeftShift;
                    }
                } else {
                    return Token.LessThan;
                }
                break;
            case '>':
                advance();
                if (next == '=') {
                    token = Token.GreaterEquals;
                } else if (next == '>') {
                    advance();
                    if (next == '=') {
                        token = Token.RightShiftAssign;
                    } else {
                        return Token.RightShift;
                    }
                } else {
                    return Token.GreaterThan;
                }
                break;
            case '+':
                advance();
                if (next == '+') {
                    token = Token.PlusPlus;
                } else if (next == '=') {
                    token = Token.PlusAssign;
                } else {
                    return Token.Plus;
                }
                break;
            case '!':
                advance();
                if (next == '=') {
                    token = Token.NotEquals;
                } else {
                    return Token.Not;
                }
                break;
            case '&':
                advance();
                if (next == '&') {
                    token = Token.AndAnd;
                } else {
                    return Token.And;
                }
                break;
            case '|':
                advance();
                if (next == '|') {
                    token = Token.OrOr;
                } else {
                    return Token.Or;
                }
                break;
            case '^':
                token = Token.XOr;
                break;
            case '~':
                token = Token.Tilde;
                break;
            case '(':
                token = Token.OpenParenthesis;
                break;
            case ')':
                token = Token.CloseParenthesis;
                break;
            case '[':
                token = Token.OpenBracket;
                break;
            case ']':
                token = Token.CloseBracket;
                break;
            case ';':
                token = Token.SemiColon;
                break;
            case '.':
                token = Token.Dot;
                break;
            case '-':
                advance();
                if (next == '>') {
                    token = Token.Arrow;
                } else if (next == '=') {
                    token = Token.MinusAssign;
                } else if (next == '-') {
                    token = Token.MinusMinus;
                } else {
                    return Token.Minus;
                }
                break;
            case '0':
                advance();
                // "0x" ( (DIGIT) | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f')+ 
                if (next == 'x') {
                    advance();
                    while (Character.isDigit(next) || ('A' <= next && next <= 'F') || ('a' <= next && next <= 'f')) {
                        advance();
                    }
                    return Token.HexNumber;
                } else {
                    zero = true;
                }
            default:
                if (Character.isDigit(next) || zero) {
                    //(DIGIT)+ (DOT (DIGIT)+ )? (('e' | 'E') ('+'|'-')? (DIGIT)+ )? ('i'|'f')?

                    boolean real = false;

                    while (Character.isDigit(next)) {
                        advance();
                    }

                    if (next == '.') {
                        advance();
                        while (Character.isDigit(next)) {
                            advance();
                        }
                        real = true;
                    }

                    if (next == 'e' || next == 'E') {
                        advance();
                        if (next == '+' || next == '-') {
                            advance();
                        }
                        while (Character.isDigit(next)) {
                            advance();
                        }

                        real = true;
                    }
                    if (next == 'i') {
                        advance();
                        return Token.ComplexNumber;
                    } else if (next == 'f') {
                        advance();
                    }

                    if (real) {
                        return Token.FloatNumber;
                    } else {
                        return Token.IntNumber;
                    }
                } else if (Character.isJavaIdentifierStart(next)) {
                    StringBuilder ident = new StringBuilder();
                    while (Character.isJavaIdentifierPart(next)) {
                        ident.append(next);
                        advance();
                        value = ident.toString();
                    }
                    switch (ident.toString()) {
                        case "add":
                            return Token.Add;
                        case "pipeline":
                            return Token.Pipeline;
                        case "filter":
                            return Token.Filter;
                        case "feedbackloop":
                            return Token.FeedbackLoop;
                        case "body":
                            return Token.Body;
                        case "loop":
                            return Token.Loop;
                        case "stateful":
                            return Token.Stateful;
                        case "work":
                            return Token.Work;
                        case "push":
                            return Token.Push;
                        case "pop":
                            return Token.Pop;
                        case "iter":
                            return Token.Iter;
                        case "peek":
                            return Token.Peek;
                        case "splitjoin":
                            return Token.SplitJoin;
                        case "split":
                            return Token.Split;
                        case "duplicate":
                            return Token.Duplicate;
                        case "roundrobin":
                            return Token.RoundRobin;
                        case "join":
                            return Token.Join;
                        case "prework":
                            return Token.PreWork;
                        case "init":
                            return Token.Init;
                        case "void":
                            return Token.Void;
                        case "int":
                            return Token.Int32;
                        case "short":
                            return Token.Int16;
                        case "long":
                            return Token.Int32; // long has 4 byte at coreva
                        case "int8_t":
                            return Token.Int8;
                        case "uint8_t":
                            return Token.UInt8;
                        case "int16_t":
                            return Token.Int16;
                        case "uint16_t":
                            return Token.UInt16;
                        case "int32_t":
                            return Token.Int32;
                        case "uint32_t":
                            return Token.UInt32;
                        case "int64_t":
                            return Token.Int64;
                        case "uint64_t":
                            return Token.UInt64;                       
                        case "bit":
                            return Token.Bit;
                        case "boolean":
                            return Token.Boolean;
                        case "complex":
                            return Token.Complex;
                        //case "pi": return Token.Pi;
                        case "false":
                            return Token.False;
                        case "true":
                            return Token.True;
                        case "double":
                            return Token.Double;
                        case "float2":
                            return Token.Float2;
                        case "float3":
                            return Token.Float3;
                        case "float4":
                            return Token.Float4;
                        case "static":
                            return Token.Static;
                        case "float":
                            return Token.Float;
                        case "native":
                            return Token.Native;
                        case "helper":
                            return Token.Helper;
                        case "enqueue":
                            return Token.Enqueue;
                        case "handler":
                            return Token.Handler;
                        case "to":
                            return Token.To;
                        case "for":
                            return Token.For;
                        case "if":
                            return Token.If;
                        case "else":
                            return Token.Else;
                        case "while":
                            return Token.While;
                        case "break":
                            return Token.Break;
                        case "continue":
                            return Token.Continue;
                        case "return":
                            return Token.Return;
                        case "do":
                            return Token.Do;
                        case "struct":
                            return Token.Struct;
                        default:
                            return Token.Identifier;
                    }
                } else {
                    throw new IOException("Unexpected character '" + next + "'" + location());
                }
        }

        advance();
        return token;
    }

    public String location() {
        return " at line " + line + ", col " + col + " " + filename;
    }
}

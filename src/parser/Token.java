package parser;

public enum Token { 
    Arrow, Pipeline, Filter, FeedbackLoop, Body, Loop, Stateful, 
    Identifier, Add, SplitJoin, Split, Duplicate, RoundRobin, Join, Init, Work, PreWork, Push, Pop, Peek, Iter, Static, 
    HexNumber, FloatNumber, IntNumber, ComplexNumber, String, Character, Boolean, Double, Float2, Float3, Float4,
    //Pi,
    True, False,
    Void, Float, Bit, Complex, To, Handler, Native, Helper, Enqueue,
    // Integer types:
    Int8, UInt8, Int16, UInt16, Int32, UInt32, Int64, UInt64,
    OpenParenthesis, CloseParenthesis, OpenBracket, CloseBracket, OpenCurly, CloseCurly, 
    Comma, SemiColon, 
    LessThan, GreaterThan, LessEquals, GreaterEquals, Equals, NotEquals,
    Plus, Minus, And, XOr, Or, Mod, Mult, Div, PlusPlus, MinusMinus, Not, AndAnd, OrOr, Tilde, LeftShift, RightShift,
    Assign, MultAssign, MinusAssign, PlusAssign, DivAssign, LeftShiftAssign, RightShiftAssign,
    Question, Colon, Dot,
    For, If, Else, Struct, While, Do, Break, Continue, Return,
    EOF
};

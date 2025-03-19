package io.github.vudsen.arthasui.language.arthas;

import com.intellij.psi.tree.IElementType;

import java.util.Stack;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes.*;

%%

%{

    private Stack<Integer> states = new Stack<>();


    private void beginState(int state) {
        states.push(state);
        yybegin(state);
    }

    private void popState() {
        states.pop();
        if (states.isEmpty()) {
            yybegin(YYINITIAL);
        } else {
            yybegin(states.peek());
        }
    }

    public _ArthasLexer() {
      this((java.io.Reader)null);
    }

    private static String zzToPrintable(CharSequence str) {
        return zzToPrintable(str.toString());
    }
%}
%debug

%public
%class _ArthasLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ \t\n\x0B\f\r]+
EOL=\n
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
LINE_COMMENT="//".*
ARGUMENT_HEAD=-[a-zA-Z]+
IDENTIFIER=[a-zA-Z\d]+
NON_WHITESPACE_SEQUENCE=[^\n ]+
CLASS_PATTERN=[a-zA-Z\d]+(\.[a-zA-Z\d]+)*
%states CLAZZ_METHOD_PAIR_1, CLAZZ_METHOD_PAIR_2
%states WAITING_IDENTIFIER, WAITING_ANY_SQE_OR_ARG, WAITING_CLASS_PATTERN, WAITING_FILE_PATH, WAITING_KEY_AND_VALUE
%%


<WAITING_IDENTIFIER> {
    {IDENTIFIER}                  { popState(); return IDENTIFIER; }
}
<WAITING_CLASS_PATTERN> {
    {CLASS_PATTERN}               { popState(); return CLASS_PATTERN; }
}

<WAITING_ANY_SQE_OR_ARG> {
    {ARGUMENT_HEAD}               { beginState(WAITING_IDENTIFIER); return ARGUMENT_HEAD; }
    {NON_WHITESPACE_SEQUENCE}     { popState(); return NON_WHITESPACE_SEQUENCE; }
}

<CLAZZ_METHOD_PAIR_1> {
    {CLASS_PATTERN}               { beginState(CLAZZ_METHOD_PAIR_2); return CLASS_PATTERN; }
    {ARGUMENT_HEAD}               { beginState(WAITING_IDENTIFIER); return ARGUMENT_HEAD; }
    <CLAZZ_METHOD_PAIR_2> {
        {IDENTIFIER}              { popState(); popState(); return IDENTIFIER; }
    }
}

<WAITING_FILE_PATH> {
    {NON_WHITESPACE_SEQUENCE}     { return FILE_PATH; }
    {EOL}                         { popState(); return EOL; }
}

<WAITING_KEY_AND_VALUE> {
    {NON_WHITESPACE_SEQUENCE} { return NON_WHITESPACE_SEQUENCE; }
}

<YYINITIAL> {
  {WHITE_SPACE}                   { return WHITE_SPACE; }

  ";"                             { return SEMICOLON; }
  "."                             { return DOT; }
  "auth"                          { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_AUTH; }
  "base64"                        { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_BASE64; }
  "cat"                           { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_CAT; }
  "classloader"                   { return COMMAND_CLASSLOADER; }
  "cls"                           { return COMMAND_CLS; }
  "dashboard"                     { return COMMAND_DASHBOARD; }
  "dump"                          { beginState(WAITING_CLASS_PATTERN); return COMMAND_DUMP; }
  "echo"                          { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_ECHO; }
  "getstatic"                     { beginState(CLAZZ_METHOD_PAIR_1); return COMMAND_GETSTATIC; }
  "grep"                          { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_GREP; }
  "heapdump"                      { beginState(WAITING_ANY_SQE_OR_ARG); return COMMAND_HEAPDUMP; }
  "help"                          { return COMMAND_HELP; }
  "history"                       { return COMMAND_HISTORY; }
  "jad"                           { beginState(WAITING_CLASS_PATTERN);return COMMAND_JAD; }
  "jfr"                           { return COMMAND_JFR; }
  "jvm"                           { return COMMAND_JVM; }
  "keymap"                        { return COMMAND_KEYMAP; }
  "logger"                        { return COMMAND_LOGGER; }
  "mbean"                         { return COMMAND_MBEAN; }
  "mc"                            { return COMMAND_MC; }
  "memory"                        { return COMMAND_MEMORY; }
  "options"                       { return COMMAND_OPTIONS; }
  "watch"                         { beginState(CLAZZ_METHOD_PAIR_1);return COMMAND_WATCH; }
  "ognl"                          { return COMMAND_OGNL; }
  "perfcounter"                   { return COMMAND_PERFCOUNTER; }
  "profiler"                      { beginState(WAITING_IDENTIFIER); return COMMAND_PROFILER; }
  "pwd"                           { return COMMAND_PWD; }
  "quit"                          { return COMMAND_QUIT; }
  "redefine"                      { beginState(WAITING_FILE_PATH); return COMMAND_REDEFINE; }
  "reset"                         { beginState(WAITING_CLASS_PATTERN); return COMMAND_RESET; }
  "retransform"                   { beginState(WAITING_FILE_PATH); return COMMAND_RETRANSFORM; }
  "sc"                            { beginState(WAITING_CLASS_PATTERN); return COMMAND_SC; }
  "session"                       { beginState(CLAZZ_METHOD_PAIR_1); return COMMAND_SESSION; }
  "sm"                            { return COMMAND_SM; }
  "stack"                         { beginState(CLAZZ_METHOD_PAIR_1); return COMMAND_STACK; }
  "stop"                          { return COMMAND_STOP; }
  "sysenv"                        { beginState(WAITING_KEY_AND_VALUE); return COMMAND_SYSENV; }
  "sysprop"                       { beginState(WAITING_KEY_AND_VALUE); return COMMAND_SYSPROP; }
  "tee"                           { beginState(WAITING_FILE_PATH); return COMMAND_TEE; }
  "thread"                        { return COMMAND_THREAD; }
  "trace"                         { beginState(CLAZZ_METHOD_PAIR_1); return COMMAND_TRACE; }
  "tt"                            { beginState(CLAZZ_METHOD_PAIR_1); return COMMAND_TT; }
  "version"                       { return COMMAND_VERSION; }
  "vmoptions"                     { beginState(WAITING_KEY_AND_VALUE); return COMMAND_VMOPTIONS; }
  "vmtool"                        { return COMMAND_VMTOOL; }

  {SPACE}                         { return SPACE; }
  {STRING}                        { return STRING; }
  {LINE_COMMENT}                  { return LINE_COMMENT; }
  {IDENTIFIER}                    { return IDENTIFIER; }
  {NON_WHITESPACE_SEQUENCE}       { return NON_WHITESPACE_SEQUENCE; }



}
{ARGUMENT_HEAD}                  { beginState(WAITING_IDENTIFIER); return ARGUMENT_HEAD; }
{EOL}                           { return EOL; }
" " { return WHITE_SPACE; }
[^] { return BAD_CHARACTER; }

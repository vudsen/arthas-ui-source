// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.github.vudsen.arthasui.language.arthas.psi.impl.*;

public interface ArthasTypes {

  IElementType ARGUMENT = new ArthasElementType("ARGUMENT");
  IElementType AUTH_COMMAND = new ArthasElementType("AUTH_COMMAND");
  IElementType CAT_EXPRESSION = new ArthasElementType("CAT_EXPRESSION");
  IElementType CLASSLOADER_EXPRESSION = new ArthasElementType("CLASSLOADER_EXPRESSION");
  IElementType CLS_STATEMENT = new ArthasElementType("CLS_STATEMENT");
  IElementType COMMAND = new ArthasElementType("COMMAND");
  IElementType DASHBOARD_STATEMENT = new ArthasElementType("DASHBOARD_STATEMENT");
  IElementType ECHO_EXPRESSION = new ArthasElementType("ECHO_EXPRESSION");
  IElementType HEAPDUMP_STATEMENT = new ArthasElementType("HEAPDUMP_STATEMENT");
  IElementType HELP_EXPRESSION = new ArthasElementType("HELP_EXPRESSION");
  IElementType HISTORY_EXPRESSION = new ArthasElementType("HISTORY_EXPRESSION");
  IElementType JAD_EXPRESSION = new ArthasElementType("JAD_EXPRESSION");
  IElementType JVM_EXPRESSION = new ArthasElementType("JVM_EXPRESSION");
  IElementType KEYMAP_EXPRESSION = new ArthasElementType("KEYMAP_EXPRESSION");
  IElementType LOGGER_EXPRESSION = new ArthasElementType("LOGGER_EXPRESSION");
  IElementType MC_STATEMENT = new ArthasElementType("MC_STATEMENT");
  IElementType MEMORY_EXPRESSION = new ArthasElementType("MEMORY_EXPRESSION");
  IElementType OGNL_STATEMENT = new ArthasElementType("OGNL_STATEMENT");
  IElementType PWD_EXPRESSION = new ArthasElementType("PWD_EXPRESSION");
  IElementType QUIT_STATEMENT = new ArthasElementType("QUIT_STATEMENT");
  IElementType RETRANSFORM_STATEMENT = new ArthasElementType("RETRANSFORM_STATEMENT");
  IElementType SC_EXPRESSION = new ArthasElementType("SC_EXPRESSION");
  IElementType SESSION_EXPRESSION = new ArthasElementType("SESSION_EXPRESSION");
  IElementType SM_EXPRESSION = new ArthasElementType("SM_EXPRESSION");
  IElementType STACK_STATEMENT = new ArthasElementType("STACK_STATEMENT");
  IElementType STOP_STATEMENT = new ArthasElementType("STOP_STATEMENT");
  IElementType SYSENV_EXPRESSION = new ArthasElementType("SYSENV_EXPRESSION");
  IElementType SYSPROP_EXPRESSION = new ArthasElementType("SYSPROP_EXPRESSION");
  IElementType THREAD_EXPRESSION = new ArthasElementType("THREAD_EXPRESSION");
  IElementType TRACE_STATEMENT = new ArthasElementType("TRACE_STATEMENT");
  IElementType TT_EXPRESSION = new ArthasElementType("TT_EXPRESSION");
  IElementType TT_T_STATEMENT = new ArthasElementType("TT_T_STATEMENT");
  IElementType UNHANDLED_BASE_64_EXPRESSION = new ArthasElementType("UNHANDLED_BASE_64_EXPRESSION");
  IElementType UNHANDLED_DUMP_STATEMENT = new ArthasElementType("UNHANDLED_DUMP_STATEMENT");
  IElementType UNHANDLED_GETSTATIC_EXPRESSION = new ArthasElementType("UNHANDLED_GETSTATIC_EXPRESSION");
  IElementType UNHANDLED_GREP_EXPRESSION = new ArthasElementType("UNHANDLED_GREP_EXPRESSION");
  IElementType UNHANDLED_JFR_STATEMENT = new ArthasElementType("UNHANDLED_JFR_STATEMENT");
  IElementType UNHANDLED_MBEAN_STATEMENT = new ArthasElementType("UNHANDLED_MBEAN_STATEMENT");
  IElementType UNHANDLED_MONITOR_STATEMENT = new ArthasElementType("UNHANDLED_MONITOR_STATEMENT");
  IElementType UNHANDLED_OPTIONS_STATEMENT = new ArthasElementType("UNHANDLED_OPTIONS_STATEMENT");
  IElementType UNHANDLED_PERFCOUNTER_EXPRESSION = new ArthasElementType("UNHANDLED_PERFCOUNTER_EXPRESSION");
  IElementType UNHANDLED_PROFILER_STATEMENT = new ArthasElementType("UNHANDLED_PROFILER_STATEMENT");
  IElementType UNHANDLED_REDEFINE_STATEMENT = new ArthasElementType("UNHANDLED_REDEFINE_STATEMENT");
  IElementType UNHANDLED_RESET_STATEMENT = new ArthasElementType("UNHANDLED_RESET_STATEMENT");
  IElementType UNHANDLED_TEE_EXPRESSION = new ArthasElementType("UNHANDLED_TEE_EXPRESSION");
  IElementType UNHANDLED_VMTOOL_STATEMENT = new ArthasElementType("UNHANDLED_VMTOOL_STATEMENT");
  IElementType VERSION_EXPRESSION = new ArthasElementType("VERSION_EXPRESSION");
  IElementType VMOPTION_EXPRESSION = new ArthasElementType("VMOPTION_EXPRESSION");
  IElementType WATCH_COMMAND = new ArthasElementType("WATCH_COMMAND");

  IElementType ANY_SEQUENCE = new ArthasTokenType("any_sequence");
  IElementType ARGUMENT_HEAD = new ArthasTokenType("ARGUMENT_HEAD");
  IElementType CLASS_PATTERN = new ArthasTokenType("CLASS_PATTERN");
  IElementType CLAZZ = new ArthasTokenType("clazz");
  IElementType COMMAND_AUTH = new ArthasTokenType("auth");
  IElementType COMMAND_BASE64 = new ArthasTokenType("base64");
  IElementType COMMAND_CAT = new ArthasTokenType("cat");
  IElementType COMMAND_CLASSLOADER = new ArthasTokenType("classloader");
  IElementType COMMAND_CLS = new ArthasTokenType("cls");
  IElementType COMMAND_DASHBOARD = new ArthasTokenType("dashboard");
  IElementType COMMAND_DUMP = new ArthasTokenType("dump");
  IElementType COMMAND_ECHO = new ArthasTokenType("echo");
  IElementType COMMAND_GETSTATIC = new ArthasTokenType("getstatic");
  IElementType COMMAND_GREP = new ArthasTokenType("grep");
  IElementType COMMAND_HEAPDUMP = new ArthasTokenType("heapdump");
  IElementType COMMAND_HELP = new ArthasTokenType("help");
  IElementType COMMAND_HISTORY = new ArthasTokenType("history");
  IElementType COMMAND_JAD = new ArthasTokenType("jad");
  IElementType COMMAND_JFR = new ArthasTokenType("jfr");
  IElementType COMMAND_JVM = new ArthasTokenType("jvm");
  IElementType COMMAND_KEYMAP = new ArthasTokenType("keymap");
  IElementType COMMAND_LOGGER = new ArthasTokenType("logger");
  IElementType COMMAND_MBEAN = new ArthasTokenType("mbean");
  IElementType COMMAND_MC = new ArthasTokenType("mc");
  IElementType COMMAND_MEMORY = new ArthasTokenType("memory");
  IElementType COMMAND_OGNL = new ArthasTokenType("ognl");
  IElementType COMMAND_OPTIONS = new ArthasTokenType("options");
  IElementType COMMAND_PERFCOUNTER = new ArthasTokenType("perfcounter");
  IElementType COMMAND_PROFILER = new ArthasTokenType("profiler");
  IElementType COMMAND_PWD = new ArthasTokenType("pwd");
  IElementType COMMAND_QUIT = new ArthasTokenType("quit");
  IElementType COMMAND_REDEFINE = new ArthasTokenType("redefine");
  IElementType COMMAND_RESET = new ArthasTokenType("reset");
  IElementType COMMAND_RETRANSFORM = new ArthasTokenType("retransform");
  IElementType COMMAND_SC = new ArthasTokenType("sc");
  IElementType COMMAND_SESSION = new ArthasTokenType("session");
  IElementType COMMAND_SM = new ArthasTokenType("sm");
  IElementType COMMAND_STACK = new ArthasTokenType("stack");
  IElementType COMMAND_STOP = new ArthasTokenType("stop");
  IElementType COMMAND_SYSENV = new ArthasTokenType("sysenv");
  IElementType COMMAND_SYSPROP = new ArthasTokenType("sysprop");
  IElementType COMMAND_TEE = new ArthasTokenType("tee");
  IElementType COMMAND_THREAD = new ArthasTokenType("thread");
  IElementType COMMAND_TRACE = new ArthasTokenType("trace");
  IElementType COMMAND_TT = new ArthasTokenType("tt");
  IElementType COMMAND_VERSION = new ArthasTokenType("version");
  IElementType COMMAND_VMOPTIONS = new ArthasTokenType("vmoptions");
  IElementType COMMAND_VMTOOL = new ArthasTokenType("vmtool");
  IElementType COMMAND_WATCH = new ArthasTokenType("watch");
  IElementType DOT = new ArthasTokenType(".");
  IElementType IDENTIFIER = new ArthasTokenType("IDENTIFIER");
  IElementType LINE_COMMENT = new ArthasTokenType("line_comment");
  IElementType METHOD = new ArthasTokenType("method");
  IElementType NON_WHITESPACE_SEQUENCE = new ArthasTokenType("NON_WHITESPACE_SEQUENCE");
  IElementType SEMICOLON = new ArthasTokenType(";");
  IElementType STRING = new ArthasTokenType("string");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARGUMENT) {
        return new ArthasArgumentImpl(node);
      }
      else if (type == AUTH_COMMAND) {
        return new ArthasAuthCommandImpl(node);
      }
      else if (type == CAT_EXPRESSION) {
        return new ArthasCatExpressionImpl(node);
      }
      else if (type == CLASSLOADER_EXPRESSION) {
        return new ArthasClassloaderExpressionImpl(node);
      }
      else if (type == CLS_STATEMENT) {
        return new ArthasClsStatementImpl(node);
      }
      else if (type == COMMAND) {
        return new ArthasCommandImpl(node);
      }
      else if (type == DASHBOARD_STATEMENT) {
        return new ArthasDashboardStatementImpl(node);
      }
      else if (type == ECHO_EXPRESSION) {
        return new ArthasEchoExpressionImpl(node);
      }
      else if (type == HEAPDUMP_STATEMENT) {
        return new ArthasHeapdumpStatementImpl(node);
      }
      else if (type == HELP_EXPRESSION) {
        return new ArthasHelpExpressionImpl(node);
      }
      else if (type == HISTORY_EXPRESSION) {
        return new ArthasHistoryExpressionImpl(node);
      }
      else if (type == JAD_EXPRESSION) {
        return new ArthasJadExpressionImpl(node);
      }
      else if (type == JVM_EXPRESSION) {
        return new ArthasJvmExpressionImpl(node);
      }
      else if (type == KEYMAP_EXPRESSION) {
        return new ArthasKeymapExpressionImpl(node);
      }
      else if (type == LOGGER_EXPRESSION) {
        return new ArthasLoggerExpressionImpl(node);
      }
      else if (type == MC_STATEMENT) {
        return new ArthasMcStatementImpl(node);
      }
      else if (type == MEMORY_EXPRESSION) {
        return new ArthasMemoryExpressionImpl(node);
      }
      else if (type == OGNL_STATEMENT) {
        return new ArthasOgnlStatementImpl(node);
      }
      else if (type == PWD_EXPRESSION) {
        return new ArthasPwdExpressionImpl(node);
      }
      else if (type == QUIT_STATEMENT) {
        return new ArthasQuitStatementImpl(node);
      }
      else if (type == RETRANSFORM_STATEMENT) {
        return new ArthasRetransformStatementImpl(node);
      }
      else if (type == SC_EXPRESSION) {
        return new ArthasScExpressionImpl(node);
      }
      else if (type == SESSION_EXPRESSION) {
        return new ArthasSessionExpressionImpl(node);
      }
      else if (type == SM_EXPRESSION) {
        return new ArthasSmExpressionImpl(node);
      }
      else if (type == STACK_STATEMENT) {
        return new ArthasStackStatementImpl(node);
      }
      else if (type == STOP_STATEMENT) {
        return new ArthasStopStatementImpl(node);
      }
      else if (type == SYSENV_EXPRESSION) {
        return new ArthasSysenvExpressionImpl(node);
      }
      else if (type == SYSPROP_EXPRESSION) {
        return new ArthasSyspropExpressionImpl(node);
      }
      else if (type == THREAD_EXPRESSION) {
        return new ArthasThreadExpressionImpl(node);
      }
      else if (type == TRACE_STATEMENT) {
        return new ArthasTraceStatementImpl(node);
      }
      else if (type == TT_EXPRESSION) {
        return new ArthasTtExpressionImpl(node);
      }
      else if (type == TT_T_STATEMENT) {
        return new ArthasTtTStatementImpl(node);
      }
      else if (type == UNHANDLED_BASE_64_EXPRESSION) {
        return new ArthasUnhandledBase64ExpressionImpl(node);
      }
      else if (type == UNHANDLED_DUMP_STATEMENT) {
        return new ArthasUnhandledDumpStatementImpl(node);
      }
      else if (type == UNHANDLED_GETSTATIC_EXPRESSION) {
        return new ArthasUnhandledGetstaticExpressionImpl(node);
      }
      else if (type == UNHANDLED_GREP_EXPRESSION) {
        return new ArthasUnhandledGrepExpressionImpl(node);
      }
      else if (type == UNHANDLED_JFR_STATEMENT) {
        return new ArthasUnhandledJfrStatementImpl(node);
      }
      else if (type == UNHANDLED_MBEAN_STATEMENT) {
        return new ArthasUnhandledMbeanStatementImpl(node);
      }
      else if (type == UNHANDLED_MONITOR_STATEMENT) {
        return new ArthasUnhandledMonitorStatementImpl(node);
      }
      else if (type == UNHANDLED_OPTIONS_STATEMENT) {
        return new ArthasUnhandledOptionsStatementImpl(node);
      }
      else if (type == UNHANDLED_PERFCOUNTER_EXPRESSION) {
        return new ArthasUnhandledPerfcounterExpressionImpl(node);
      }
      else if (type == UNHANDLED_PROFILER_STATEMENT) {
        return new ArthasUnhandledProfilerStatementImpl(node);
      }
      else if (type == UNHANDLED_REDEFINE_STATEMENT) {
        return new ArthasUnhandledRedefineStatementImpl(node);
      }
      else if (type == UNHANDLED_RESET_STATEMENT) {
        return new ArthasUnhandledResetStatementImpl(node);
      }
      else if (type == UNHANDLED_TEE_EXPRESSION) {
        return new ArthasUnhandledTeeExpressionImpl(node);
      }
      else if (type == UNHANDLED_VMTOOL_STATEMENT) {
        return new ArthasUnhandledVmtoolStatementImpl(node);
      }
      else if (type == VERSION_EXPRESSION) {
        return new ArthasVersionExpressionImpl(node);
      }
      else if (type == VMOPTION_EXPRESSION) {
        return new ArthasVmoptionExpressionImpl(node);
      }
      else if (type == WATCH_COMMAND) {
        return new ArthasWatchCommandImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

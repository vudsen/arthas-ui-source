// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.github.vudsen.arthasui.language.arthas.psi.impl.*;

public interface ArthasTypes {

  IElementType ARGUMENT = new ArthasElementType("ARGUMENT");
  IElementType AUTH_COMMAND = new ArthasElementType("AUTH_COMMAND");
  IElementType BASE_64_COMMAND = new ArthasElementType("BASE_64_COMMAND");
  IElementType CAT_COMMAND = new ArthasElementType("CAT_COMMAND");
  IElementType CLASSLOADER_COMMAND = new ArthasElementType("CLASSLOADER_COMMAND");
  IElementType CLAZZ = new ArthasElementType("CLAZZ");
  IElementType CLS_COMMAND = new ArthasElementType("CLS_COMMAND");
  IElementType COMMAND = new ArthasElementType("COMMAND");
  IElementType DASHBOARD_COMMAND = new ArthasElementType("DASHBOARD_COMMAND");
  IElementType DUMP_COMMAND = new ArthasElementType("DUMP_COMMAND");
  IElementType ECHO_COMMAND = new ArthasElementType("ECHO_COMMAND");
  IElementType GETSTATIC_COMMAND = new ArthasElementType("GETSTATIC_COMMAND");
  IElementType GREP_COMMAND = new ArthasElementType("GREP_COMMAND");
  IElementType HEAPDUMP_COMMAND = new ArthasElementType("HEAPDUMP_COMMAND");
  IElementType HELP_COMMAND = new ArthasElementType("HELP_COMMAND");
  IElementType HISTORY_COMMAND = new ArthasElementType("HISTORY_COMMAND");
  IElementType JAD_COMMAND = new ArthasElementType("JAD_COMMAND");
  IElementType JFR_COMMAND = new ArthasElementType("JFR_COMMAND");
  IElementType JVM_COMMAND = new ArthasElementType("JVM_COMMAND");
  IElementType KEYMAP_COMMAND = new ArthasElementType("KEYMAP_COMMAND");
  IElementType LOGGER_COMMAND = new ArthasElementType("LOGGER_COMMAND");
  IElementType MBEAN_COMMAND = new ArthasElementType("MBEAN_COMMAND");
  IElementType MC_COMMAND = new ArthasElementType("MC_COMMAND");
  IElementType MEMORY_COMMAND = new ArthasElementType("MEMORY_COMMAND");
  IElementType MONITOR_COMMAND = new ArthasElementType("MONITOR_COMMAND");
  IElementType OGNL_COMMAND = new ArthasElementType("OGNL_COMMAND");
  IElementType OPTIONS_COMMAND = new ArthasElementType("OPTIONS_COMMAND");
  IElementType PERFCOUNTER_COMMAND = new ArthasElementType("PERFCOUNTER_COMMAND");
  IElementType PROFILER_COMMAND = new ArthasElementType("PROFILER_COMMAND");
  IElementType PWD_COMMAND = new ArthasElementType("PWD_COMMAND");
  IElementType QUIT_COMMAND = new ArthasElementType("QUIT_COMMAND");
  IElementType REDEFINE_COMMAND = new ArthasElementType("REDEFINE_COMMAND");
  IElementType RESET_COMMAND = new ArthasElementType("RESET_COMMAND");
  IElementType RETRANSFORM_COMMAND = new ArthasElementType("RETRANSFORM_COMMAND");
  IElementType SC_COMMAND = new ArthasElementType("SC_COMMAND");
  IElementType SESSION_COMMAND = new ArthasElementType("SESSION_COMMAND");
  IElementType SM_COMMAND = new ArthasElementType("SM_COMMAND");
  IElementType STACK_COMMAND = new ArthasElementType("STACK_COMMAND");
  IElementType STOP_COMMAND = new ArthasElementType("STOP_COMMAND");
  IElementType SYSENV_COMMAND = new ArthasElementType("SYSENV_COMMAND");
  IElementType SYSPROP_COMMAND = new ArthasElementType("SYSPROP_COMMAND");
  IElementType TEE_COMMAND = new ArthasElementType("TEE_COMMAND");
  IElementType THREAD_COMMAND = new ArthasElementType("THREAD_COMMAND");
  IElementType TRACE_COMMAND = new ArthasElementType("TRACE_COMMAND");
  IElementType TT_COMMAND = new ArthasElementType("TT_COMMAND");
  IElementType VERSION_COMMAND = new ArthasElementType("VERSION_COMMAND");
  IElementType VMOPTION_COMMAND = new ArthasElementType("VMOPTION_COMMAND");
  IElementType VMTOOL_COMMAND = new ArthasElementType("VMTOOL_COMMAND");
  IElementType WATCH_COMMAND = new ArthasElementType("WATCH_COMMAND");

  IElementType ARGUMENT_HEAD = new ArthasTokenType("ARGUMENT_HEAD");
  IElementType CLASS_PATTERN = new ArthasTokenType("CLASS_PATTERN");
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
  IElementType COMMAND_MONITOR = new ArthasTokenType("monitor");
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
  IElementType COMMAND_VMOPTION = new ArthasTokenType("vmoption");
  IElementType COMMAND_VMTOOL = new ArthasTokenType("vmtool");
  IElementType COMMAND_WATCH = new ArthasTokenType("watch");
  IElementType DOT = new ArthasTokenType(".");
  IElementType FILE_PATH = new ArthasTokenType("");
  IElementType IDENTIFIER = new ArthasTokenType("IDENTIFIER");
  IElementType LINE_COMMENT = new ArthasTokenType("line_comment");
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
      else if (type == BASE_64_COMMAND) {
        return new ArthasBase64CommandImpl(node);
      }
      else if (type == CAT_COMMAND) {
        return new ArthasCatCommandImpl(node);
      }
      else if (type == CLASSLOADER_COMMAND) {
        return new ArthasClassloaderCommandImpl(node);
      }
      else if (type == CLAZZ) {
        return new ArthasClazzImpl(node);
      }
      else if (type == CLS_COMMAND) {
        return new ArthasClsCommandImpl(node);
      }
      else if (type == COMMAND) {
        return new ArthasCommandImpl(node);
      }
      else if (type == DASHBOARD_COMMAND) {
        return new ArthasDashboardCommandImpl(node);
      }
      else if (type == DUMP_COMMAND) {
        return new ArthasDumpCommandImpl(node);
      }
      else if (type == ECHO_COMMAND) {
        return new ArthasEchoCommandImpl(node);
      }
      else if (type == GETSTATIC_COMMAND) {
        return new ArthasGetstaticCommandImpl(node);
      }
      else if (type == GREP_COMMAND) {
        return new ArthasGrepCommandImpl(node);
      }
      else if (type == HEAPDUMP_COMMAND) {
        return new ArthasHeapdumpCommandImpl(node);
      }
      else if (type == HELP_COMMAND) {
        return new ArthasHelpCommandImpl(node);
      }
      else if (type == HISTORY_COMMAND) {
        return new ArthasHistoryCommandImpl(node);
      }
      else if (type == JAD_COMMAND) {
        return new ArthasJadCommandImpl(node);
      }
      else if (type == JFR_COMMAND) {
        return new ArthasJfrCommandImpl(node);
      }
      else if (type == JVM_COMMAND) {
        return new ArthasJvmCommandImpl(node);
      }
      else if (type == KEYMAP_COMMAND) {
        return new ArthasKeymapCommandImpl(node);
      }
      else if (type == LOGGER_COMMAND) {
        return new ArthasLoggerCommandImpl(node);
      }
      else if (type == MBEAN_COMMAND) {
        return new ArthasMbeanCommandImpl(node);
      }
      else if (type == MC_COMMAND) {
        return new ArthasMcCommandImpl(node);
      }
      else if (type == MEMORY_COMMAND) {
        return new ArthasMemoryCommandImpl(node);
      }
      else if (type == MONITOR_COMMAND) {
        return new ArthasMonitorCommandImpl(node);
      }
      else if (type == OGNL_COMMAND) {
        return new ArthasOgnlCommandImpl(node);
      }
      else if (type == OPTIONS_COMMAND) {
        return new ArthasOptionsCommandImpl(node);
      }
      else if (type == PERFCOUNTER_COMMAND) {
        return new ArthasPerfcounterCommandImpl(node);
      }
      else if (type == PROFILER_COMMAND) {
        return new ArthasProfilerCommandImpl(node);
      }
      else if (type == PWD_COMMAND) {
        return new ArthasPwdCommandImpl(node);
      }
      else if (type == QUIT_COMMAND) {
        return new ArthasQuitCommandImpl(node);
      }
      else if (type == REDEFINE_COMMAND) {
        return new ArthasRedefineCommandImpl(node);
      }
      else if (type == RESET_COMMAND) {
        return new ArthasResetCommandImpl(node);
      }
      else if (type == RETRANSFORM_COMMAND) {
        return new ArthasRetransformCommandImpl(node);
      }
      else if (type == SC_COMMAND) {
        return new ArthasScCommandImpl(node);
      }
      else if (type == SESSION_COMMAND) {
        return new ArthasSessionCommandImpl(node);
      }
      else if (type == SM_COMMAND) {
        return new ArthasSmCommandImpl(node);
      }
      else if (type == STACK_COMMAND) {
        return new ArthasStackCommandImpl(node);
      }
      else if (type == STOP_COMMAND) {
        return new ArthasStopCommandImpl(node);
      }
      else if (type == SYSENV_COMMAND) {
        return new ArthasSysenvCommandImpl(node);
      }
      else if (type == SYSPROP_COMMAND) {
        return new ArthasSyspropCommandImpl(node);
      }
      else if (type == TEE_COMMAND) {
        return new ArthasTeeCommandImpl(node);
      }
      else if (type == THREAD_COMMAND) {
        return new ArthasThreadCommandImpl(node);
      }
      else if (type == TRACE_COMMAND) {
        return new ArthasTraceCommandImpl(node);
      }
      else if (type == TT_COMMAND) {
        return new ArthasTtCommandImpl(node);
      }
      else if (type == VERSION_COMMAND) {
        return new ArthasVersionCommandImpl(node);
      }
      else if (type == VMOPTION_COMMAND) {
        return new ArthasVmoptionCommandImpl(node);
      }
      else if (type == VMTOOL_COMMAND) {
        return new ArthasVmtoolCommandImpl(node);
      }
      else if (type == WATCH_COMMAND) {
        return new ArthasWatchCommandImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

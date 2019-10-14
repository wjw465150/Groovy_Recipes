## Groovy from the Command Line {#命令行中的Groovy}

用于shell脚本的Java ?是的,对的。

另一方面，Groovy在这方面给了我惊喜。现在，不要误解我的意思——没有哪个自重的Unix系统管理员会抛弃自我混淆的Perl和shell脚本，转而支持Groovy。但是对我来说，使用我非常熟悉的语言来完成服务器上的日常工作是一个完美的选择。我不是一个全职的系统管理员，但是我一直面临着一些杂事，比如费力地通过一个充满Tomcat日志文件的目录，或者批量地将一个充满图像的目录从一种格式转换成另一种格式。用Groovy做这种事情是如此自然，以至于我无法想象用其他任何语言来做这件事。

在本章中，我们将讨论如何从命令提示符运行未编译的Groovy脚本，并从用户处获取命令行参数。您可以像调用本机操作系统命令一样轻松地调用其他Groovy脚本。Groovy作为胶水语言的才能在这里得到了充分的展示。Groovy泰然自若地模糊了本机操作系统任务和Java库之间的区别，执行管理任务——我敢说吗?非常愉快。

### 5.1 Running Uncompiled Groovy Scripts {#运行未编译的Groovy脚本}
```bash
groovy hello.groovy
groovy hello
```
groovy命令允许您运行未编译的Groovy脚本。 例如，在您选择的文本编辑器中创建一个名为`hello.groovy`的文件。 向其添加以下行：
```groovy
println "Hello Groovy World"
```

要运行脚本，请键入`groovy hello.groovy`。如果您使用".groovy"文件扩展名，您可以在命令提示符中键入扩展名时关闭该扩展名: `groovy hello`。

对于我们这些精通企业Java开发和“编译 -> JAR -> WAR -> EAR -> 部署”生命周期的人来说，认为我们实际上只需要保存一个文件并运行它，似乎有些不切实际。一旦你体验过“想一下 -> 编码 -> 运行”，你就会对它上瘾。

### 5.2 Shebanging Groovy {#打捆Groovy}
```bash
#!/usr/bin/env groovy
println "Hello Groovy World"
```

类unix操作系统的爱好者熟悉他们的脚本的“shebanging”—“hash”和“bang”的缩写，即脚本第一行的前两个字符。敲打脚本可让您在命令行输入时省去命令解释器。无需键入`groovy hello`来运行此脚本，您只需键入`hello.groovy`。 因为脚本是自我感知的(也就是说，它已经知道它是一个Groovy脚本)，所以在命名文件时甚至可以省略文件扩展名。在命令提示符中键入hello使它看起来像一个本机命令。

Shebanging Groovy脚本假设有四件事：
+ 您使用的是类似Unix的操作系统：Linux，Mac OS X，Solaris等（很抱歉，Windows用户，除非您也是Cygwin [^ 502]用户）。
+ 您已将文件设置为可执行文件（chmod a+x hello）。
+ 当前目录（.）在您的PATH中。 如果不是，`./ hello`还算不错。
+ 环境变量`GROOVY_HOME`存在，`GROOVY_HOME/bin`在您的路径中的某个地方。您总是可以在脚本的顶部硬编码到groovy命令解释器的确切路径，但这将阻止您使用第2.1节(在Unix、Linux和Mac OS X上安装groovy，见第25页)中讨论的符号链接技巧在不同版本的groovy之间切换。

我有许多实用程序脚本保存在`~/bin`中。她们已经在我的`PATH`上了。这意味着，无论我在文件系统的哪个位置，我都可以键入一些聪明的操作，在某种程度上模糊地记得我是用Groovy编写脚本的，但老实说，我并不关心。

### 5.3 Accepting Command-Line Arguments {#接受命令行参数}
```groovy
if(args){
  println "Hello ${args[0]}"
} else{
  println "Hello Stranger"
}
```

还记得编写您的第一个HelloWorld Java类吗?大概是这样的:
```java
public class HelloWorld{
  public static void main(String[] args){
    if(args != null && args.length > 0){
      System.out.println("Hello " + args[0]);
    } else{
      System.out.println("Hello Stranger");
    }
  }
}
```

在编译完`javac HelloWorld.java`之后，您可以通过输入`java HelloWorld Bub`来运行它。

Using Groovy, you can boil the same exercise down to its bare essentials. Type the code that started this tip into a file named Hola.groovy. Next type groovy Hola Bub. Since all Groovy scripts are compiled into valid Java bytecode by the groovy command interpreter in memory, you effectively end up with the Java example without having to type all of that additional boilerplate code.

The reason this terse if statement works is thanks to Groovy truth. For more information, see Section 3.10, Groovy Truth, on page 54.

Every Groovy script has an implicit argsString array that represents the command-line arguments passed into the script. (You guessed it—this is the args of public static void main(String[ ] args) fame.) To see the magic args array in action, create a file named cli.groovy, and type the following:
```groovy
args.each{println it}
```

Typing groovy cli this is a test yields the following:
```bash
$ groovy cli this is a test
===>
this
is
a
test
```

### 5.4 Running a Shell Command
```bash
// in Windows:
println "cmd /c dir".execute().text

//in Unix / Linux / Mac OS X:
println "ls -al".execute().text
```
Running a shell command is as simple as calling .execute() on a String. This returns a java.lang.Process. You can use this trick to run full programs or simple command-line tasks. As the code examples demonstrate, the commands inside the String will most likely differ between operating systems. The ls command will work only on Mac OS X, Unix, and Linux systems. The dir command will work only on Windows derivatives.

If you simply call .execute() on a String, the resulting output text is not captured. This might be acceptable for commands such as "rm some-file.txt".execute(). If  you want to see the output returned from the shell command, you append .text to the end of .execute().

On Unix-like systems, most shell commands are actually executable programs. Type which ls to see the path to the command. This means that nearly everything you would normally type at the command line can simply be wrapped up in quotes and executed. (One unfortunate exception to this rule is when you are dealing with wildcards. See Section 5.5, Using Shell Wildcards in Groovy Scripts, on the next page for more details.) For example, you can run println "ifconfig".execute().text to see the current network settings.

On Windows systems, println "ipconfig /all".execute().text returns similar results. This trick works because ipconfig.exe lives on your path in c:\windows\system32. Unfortunately, many of the most common commands you type at a command prompt in Windows are not executable programs at all. Search as you might, you’ll never find a dir.exe or copy.com tucked away in a system directory somewhere. These commands are embedded in cmd.exe.

To execute them, you must type cmd /c. For a list of the embedded commands, type cmd /? at a command prompt. You’ll see the following list on Windows XP:
```bash
DIR
COPY
REN
DEL or ERASE
COLOR
CD or CHDIR
MD or MKDIR
PROMPT
PUSHD
POPD
SET
SETLOCAL
ENDLOCAL
IF
FOR
CALL
SHIFT
GOTO
START
ASSOC
FTYPE
```

Knowing this, many Windows users just prepend cmd /c to all commands they execute in Groovy. Although it’s a bit more verbose, it certainly doesn’t hurt  anything to type "cmd /c ipconfig /all".execute().text.

One last bit of advice for Windows users—don’t forget to escape your backslashes in directories: println "cmd /c dir c:\\tmp".execute().text.

### 5.5 Using Shell Wildcards in Groovy Scripts
```groovy
//in Windows:
println "cmd /c dir *.groovy".execute().text
def c = ["cmd", "/c", "dir *.groovy"].execute().text
println c

//in Unix / Linux / Mac OS X:
def output = ["sh", "-c", "ls -al *.groovy"].execute().text
println output

//sadly, these don't work
println "ls -al *.groovy".execute().text
println "sh -c ls -al *.groovy".execute().text
```

In Section 5.4, Running a Shell Command, on the preceding page, you learned that some common commands that you type on a Windows machine (dir, copy, and so on) are embedded in the cmd shell. That shell manages wildcard expansion as well. So, asking for all files that end in .groovy is something that the shell expands into a list and then passes on to the dir command.

On Unix-like systems, the shell is responsible for expanding wildcard characters as well. Knowing that, explicitly including the shell in your command makes sense. You can type sh -c "ls -al *.groovy" to get an idea of what we are trying to accomplish.

Unfortunately, the embedded quotes required for this command cause me a bit of heartburn if I try to call execute on a single string. Luckily, we can call execute on a String array as well. The first element in the array is the command, and all the following elements are passed in as arguments. Although this form is a bit more verbose (and admittedly not exactly intuitive at first glance), it does work. We get -1 for style points, but +1 for getting the job done....

### 5.6 Running Multiple Shell Commands at Once
```bash
//in Windows:
println "cmd /c dir c:\\opt & dir c:\\tmp".execute().text
//in Unix / Linux / Mac OS X:
println "ls /opt & ls /tmp".execute().text
```
You can string together multiple shell commands using the & character. Of course, this has nothing to do with Groovy—this is a feature of the underlying OS. To prove it, try typing the commands surrounded by quotes directly at a command prompt.

### 5.7 Waiting for a Shell Command to Finish Before Continuing
```bash
def p = "convert -crop 256x256 full.jpg tile.jpg".execute()
p.waitFor()
println "ls".execute().text
```

If you have a long-running command and want to wait for it to complete before proceeding, you can assign the command to a variable and use the ".waitFor()" method. This example shows the ImageMagick command convert -crop, which takes a large image and breaks it up into 256-by-256 pixel tiles. You’ll want to wait for the command to complete before displaying the directory listing of the current directory to ensure that all the resulting tiles appear.

### 5.8 Getting System Properties
```bash
println System.getProperty("java.version")
===> 1.5.0_07

System.properties.each{println it}
===>
java.version=1.5.0_07
java.vm.vendor="Apple Computer, Inc."
os.arch=i386
os.name=Mac OS X
os.version=10.4.10
user.home=/Users/sdavis
...
```

The JVM provides you with a comfortable sandbox, shielding your code from operating system differences. Sun coined the phrase “write once, run anywhere” (WORA) to describe this phenomena, although the oldtimers and cynics bend this a bit to “write once, debug everywhere.”

Almost everything you are doing in this chapter expressly pokes WORA in the eye. You are messing around at the OS level, running commands that will almost certainly break if you try to run them anywhere but the operating system for which they were expressly written. Given that, it’s nice to be able to determine programmatically what type of hardware you are running on, what version of the JVM you are using, and so on. The System.properties hashmap allows you to do this type of introspection.

If you already know the name of the variable you are looking for, you can ask for it explicitly; System.getProperty("file.separator"), for example, lets you know whether you should be in a forward-slashy or backwardslashy kind of mood.

On the other hand, you might feel like doing some window shopping instead. Typing System.properties.each{println it} allows you to dump the full list of properties out, one by one. This is a great tool for exposing all the interesting bits of a running system. I usually have this oneliner Groovlet running on each of my production servers so that I can keep an eye on them remotely. (For more on Groovlets, see Section 2.6, Running Groovy on a Web Server (Groovlets), on page 33. For more on
keeping your private bits from becoming public bits, see the venerable Tomcat documentation on Security Realms[^508].)

Here are various useful system properties as they appear on my MacBook Pro:
```bash
java.version=1.5.0_07
java.vendor=Apple Computer, Inc.
java.vendor.url=http://apple.com/
java.home=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
groovy.home=/opt/groovy
java.class.path=/path/to/some.jar:/path/to/another.jar

file.separator=/
path.separator=:
line.separator=[NOTE: this is the OS-specific newline string.]
os.name=Mac OS X
os.version=10.4.10
os.arch=i386

user.dir=/current/dir/where/you/ran/this/script
java.io.tmpdir=/tmp
user.home=/Users/sdavis
user.name=sdavis
user.country=US
user.language=en
```
**file.separator, path.separator, and line.separator**
>  These, as you already know, are the most common things that vary between Windows and Unix-like operating systems.

**user.dir**
>  This is the current directory (the directory from which the class is being run). Knowing the user.dir is nice if you want to look for directories and files relative to where you are right now.

**java.io.tmp**
>  This is a good place to write short-lived, temporary files. This variable exists on every system, although the exact file path varies. Having a generic dumping ground that is guaranteed to exist on every system is a nice little hidden gem. Just don’t expect those files to live beyond the current block of execution.

**user.home**
>  This little fella, like java.io.tmp, is guaranteed to exist on every system, although the exact file path varies. This is a great place to write more permanent data.

**Reading in Custom Values from -D or JAVA_OPTS**
The System.properties hashmap is good for more than just dealing with the boring old default values that appear on every system. Custom values can be passed into System.properties in a couple of ways. If you have ever used the -D parameter with Ant targets (for example, ant -Dserver.port=9090 deploy), you know they show up in System.properties as well (System.getProperty("server.port")). Values stored in the JAVA_OPTS environment variable also show up in System.properties.

### 5.9 Getting Environment Variables
```groovy
println System.getenv("JAVA_HOME")
===> /Library/Java/Home

System.env.each{println it}
===>
PWD=/Users/sdavis/groovybook/Book/code/cli
USER=sdavis
LOGNAME=sdavis
HOME=/Users/sdavis
GROOVY_HOME=/opt/groovy
GRAILS_HOME=/opt/grails
JAVA_HOME=/Library/Java/Home
JRE_HOME=/Library/Java/Home
JAVA_OPTS= -Dscript.name=/opt/groovy/bin/groovy
SHELL=/bin/bash
PATH=/opt/local/bin:/usr/local/bin:...
```

Like system properties (as discussed in Section 5.8, Getting System Properties, on page 92), environment variables are another rich vein to mine for system-specific information.

If you already know the name of the environment variable you are looking for, you can ask for it explicitly; System.getenv("GROOVY_HOME"), for example, lets you know the directory where Groovy is installed. To iterate through all the environment variables on the system, System.env.each{println it} does the trick.

You may notice some overlap between environment and system variables. For example, System.getProperty("groovy.home") and System. getenv("GROOVY_HOME") both yield the same thing: /opt/groovy. Other times, the specific bit of information you are looking for can be found only in one place or the other. For example, the list of environment variables will likely contain variables such as TOMCAT_HOME, JBOSS_HOME, and ANT_HOME that don’t appear in the list of system  properties.

Like anything else, having both available to you will be important at different times. Your customization tweaks might come in via environment variables or -D parameters. Those variables might point you toward the user’s home directory or an application directory where config files can be found such as server.xml, struts-config.xml, or .bash_profile. The important thing is that you’ll be able to manage the whole system, regardless of which specific mechanism is used.

### 5.10 Evaluating a String
```groovy
def s = "Scott"
def cmdName = "size"
evaluate("println s.${cmdName}()")
===> 5
cmdName = "toUpperCase"
evaluate "println s.${cmdName}()"
===> SCOTT
```

In Section 5.4, Running a Shell Command, on page 89, we discussed how to call execute on an arbitrary string. evaluate works slightly differently.

Instead of running a shell command, evaluate allows you to dynamically execute a random string as Groovy code. The previous examples were dynamically calling two methods on a String—size() and toUpperCase(). (Did you notice the optional parentheses in the second example?) This leads to some interesting capabilities, such as being able to iterate over all methods on an object and call them:
```groovy
//NOTE: This is pseudocode -- it won't actually run
def s = "Scott"
s.class.methods.each{cmdName ->
evaluate("s.${cmdName}()")
}
```

Although this example won’t work as written—it does not take into account the arguments that some of the String methods require such as s.substring(2,4)—it shows the potential value of evaluating Groovy code on the fly. It also quite nicely illustrates the risks. If you blindly accept commands from an end user and execute them on the fly, you should be prepared for the script kiddie who sends you rm -Rf /. For a working example of evaluating methods on the fly, see Section 10.4, Discovering the Methods of a Class, on page 188.

### 5.11 Calling Another Groovy Script
```groovy
// hello.groovy
println "Howdy"
// goodbye.groovy
hello.main()
println "Goodbye"
```
You probably call one Java class from inside another Java class all the time. If the two classes are in the same package, you can call one from the other directly: AnotherClass.doSomething();. If they live in separate packages, you need to import the other package or fully qualify the class:  com.elsewhere.AnotherClass.doSomething();. Calling one Groovy script from another works in fundamentally the same way. As long as you remember that Groovy code gets compiled to bytecode on the fly, you’ll never go wrong.

In the previous example, hello.groovy gets compiled into the following equivalent Java code:
```groovy
public class hello{
  public static void main(String[] args){
    System.out.println("Howdy");
  }
}
```
The lowercase class name might look strange, but Groovy simply uses the filename as the class name. (Sound familiar?) Script content that 
isn’t explicitly wrapped in a function/closure/whatever is that script’s public static void main(String[ ] args). Two scripts living in the same directory are effectively in the same package. So, calling any script in the same directory as you’re in is as simple as calling the static main method on the class.

**Calling Another Script with Parameters**
```groovy
//hello2.groovy
if(args){
  println "Hello ${args[0]}"
  if(args.size() > 1){
    println "...and your little dog, too: ${args[1]}"
  }
}

//goodbye2.groovy
hello2.main()
hello2.main("Glenda")
hello2.main("Dorothy", "Toto")
println "Goodbye"
```

Since the script body is effectively the public static void main(String[ ] args) method, it only follows that you are able to pass in parameters via the provided string array.

**Calling Methods in Another Script**
```groovy
//hello3.groovy
if(args){
  println "Hello ${args[0]}"
  if(args.size() > 1){
    println "...and your little dog, too: ${args[1]}"
  }
}

def sayHola(){
  println "Hola"
}

//goodbye3.groovy
hello3.main()
hello3.main("Glenda")
hello3.main("Dorothy", "Toto")
println "Goodbye"
h = new hello3()
h.sayHola()
```

If the other script has static methods (such as main), you can call them statically. If the other script defines instance methods, you must instantiate the script before you can call them.

Calling Another Script in a Different Directory
```groovy
evaluate(new File("/some/other/dir/hello.groovy"))
```
Our friend evaluate comes back for another visit. (See Section 5.10, Evaluating a String, on page 95 for an alternate use of evaluate.) This time you are evaluating a file instead of an arbitrary string. This effectively calls the main method of the other file.

If you are trying to do anything more complicated with script-to-script calls than what we’ve already discussed, my recommendation is to compile your scripts to bytecode, place them in the package of your choice, JAR them up, and call them as you would any other Java class.

### 5.12 Groovy on the Fly (groovy -e)
```bash
$ groovy -e "println System.properties['java.class.path']"
===>
/opt/groovy/lib/groovy-1.1-beta-2.jar:/System/Library/Frameworks
/JavaVM.framework/Versions/1.5.0/Classes/.compatibility/14compatibility.jar
```

Groovy makes it easy to run code quickly. You can save a file and run it immediately. You can open up a quick Groovy shell or Groovy console to work with the language interactively. But sometimes running a single line of Groovy at the command line is all you need. The -e flag tells Groovy to evaluate the string you just  passed in.

For example, let’s say you are picking up a strange JAR on your classpath. You can type echo $CLASSPATH on a Unix-like system to see if the environment variable is the culprit. (set on a Windows system will give you similar results.) If the classpath comes up empty, there are many other places those pesky JARs can sneak in. Look in $JAVA_HOME/lib, $JAVA_HOME/lib/ext, and $GROOVY_HOME/lib to see if any strangers are lurking around. The previous example will show you exactly what the JRE sees—it is up to you to hunt down the intruders from there.

### 5.13 Including JARs at the Command Line
```bash
$ groovy -classpath ~/lib/derbyclient.jar:~/lib/jdom.jar:. db2xml.groovy
```

If you have a script that depends on other libraries, you can pass groovy a -classpath switch with a list of JARs. This is, of course, no different from running java from the command line. To run our fictional db2xml.groovy script, it’s not surprising that the script needs access to both a database driver and an XML library.

**Automatically Including JARs in the .groovy/lib Directory**
```bash
//on Windows:
mkdir C:\Documents and Settings\UserName\.groovy\lib

//on Unix, Linux, and Mac OS X:
mkdir ~/.groovy/lib

// uncomment the following line in
// $GROOVY_HOME/conf/groovy-starter.conf
# load user specific libraries
load !{user.home}/.groovy/lib/*.jar
```

You’ll soon grow tired of having to type commonly used JARs (such as JDBC drivers) on the command line each time. If you create a ".groovy/lib" directory in your home directory (don’t forget the leading dot), any JARs found in this directory will be automatically included in the CLASSPATH when you run Groovy from the command prompt. The ".groovy/lib" directory is disabled by default; be sure to enable it in "$GROOVY_HOME/conf/ groovy-starter.conf".

## File Tricks

Groovy offers many shortcuts for dealing with files and directories. Listing files, copying files, renaming files, deleting files—Groovy brings welcome help for all  these mundane tasks. The fact that Groovy adds new methods directly to the standard JDK classes such as java.io.File make these new features feel like a natural part of the language.

The stalwart Java build tool Ant makes a cameo appearance in this chapter as well. Ant goes far beyond the standard Java I/O library capabilities, adding support for related functionality such as batch operations and ZIP files. Even though Ant is written in Java, the interface most developers are familiar with is the ubiquitous build.xml file. Groovy’s native support for XML is covered extensively in Chapter 7, Parsing XML, on page 116 and Chapter 8, Writing XML, on page 136. In this chapter, you’ll see a great example of this in action with AntBuilder—all the power of Ant, none of the XML. It’s pure code all the way; you’ll never look at build files the same way again.

### 6.1 Listing All Files in a Directory
```groovy
new File(".").eachFile{file ->
  println file
}

//prints both files and directories
===>
./error.jsp
./GroovyLogo.zip
./index.jsp
./META-INF
./result.jsp
./WEB-INF
```

The eachFile method that Groovy adds to the standard java.io.File makes short work of displaying a directory listing. In this case, you’re looking at the current directory ("."). You can, of course, pass in a fully qualified directory name as well: new File("/opt/tomcat/webapps/myapp").

To give you an idea of the keystrokes Groovy saves you, here is the corresponding code in Java:
```groovy
import java.io.File;

public class DirList {
  public static void main(String[] args) {
    File dir = new File(".");
    File[] files = dir.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      System.out.println(file);
    }
  }
}
```

Again, you should note that Groovy augments the java.io.File object that comes with Java. This means that all the standard JDK methods are available for use as well as the new Groovy ones. The eachFile method is added to the class, as discussed in Section 10.11, Adding Methods to a Class Dynamically (ExpandoMetaClass), on page 198. To see all the methods added to java.io.File, refer to the GDK documentation[^601].

**Command-Line Input**
```groovy
$ groovy list /some/other/dir

//list.groovy:
new File(args[0]).eachFile{file ->
  println file
}
```

For a more flexible version of this script, you can borrow the trick discussed in Section 5.3, Accepting Command-Line Arguments, on page 88. Assuming that this script is saved in a file named list.groovy, this example gives you the flexibility to pass in any directory name.

**Listing Only Directories**
```groovy
new File(".").eachDir{dir ->
  println dir
}

===>
./META-INF
./WEB-INF
```

To limit your output to directories, you use File.eachDir. You can also use File.eachDirRecurse to traverse the entire directory tree:
```groovy
new File(".").eachDirRecurse{dir ->
  println dir
}

===>
./META-INF
./WEB-INF
./WEB-INF/classes
./WEB-INF/classes/org
./WEB-INF/classes/org/davisworld
./WEB-INF/lib
```

**Listing Only Files**
```groovy
new File(".").eachFile{file ->
  if(file.isFile()){
    println file
  }
}

===>
./error.jsp
./GroovyLogo.zip
./index.jsp
./result.jsp
```

At the beginning of this section, we saw that File.eachFile returns both files and directories. (Don’t blame Groovy—this mirrors the standard JDK behavior of File.listFiles.) Luckily, you can use another standard JDK method to filter your output: File.isFile.

Groovy also offers a File.eachFileRecurse method that allows you to see all files in the directory tree:
```groovy
new File(".").eachFileRecurse{file ->
if(file.isFile()){
println file
}
}

===>
./error.jsp
./GroovyLogo.zip
./index.jsp
./result.jsp
./META-INF/MANIFEST.MF
./WEB-INF/web.xml
./WEB-INF/classes/org/davisworld/MyServlet.class
./WEB-INF/lib/groovy.jar
```

**Listing Specific Files in a Directory**
```groovy
new File(".").eachFile{file ->
if(file.name.endsWith(".jsp")){
println file
}
}
===>
./error.jsp
./index.jsp
./result.jsp
```

The if statement is a perfect example of using Groovy and Java together. file.name is the Groovy equivalent of file.getName(), as discussed in Section 4.2, Getter and Setter Shortcut Syntax, on page 72. file.name returns a String, which allows you to use the standard JDK endsWith() method.

If you’re a fan of regular expressions, Groovy offers a File.eachFileMatch method:
```groovy
new File(".").eachFileMatch(~/.*\.jsp/){file ->
  println file
}
```

File.eachFileMatch technically accepts any class with a method boolean isCase(String s). This means you could expand the example to include a JspFilter class:
```groovy
class JspFilter {
  boolean isCase(String filename) {
    return filename.endsWith(".jsp")
  }
}

new File(".").eachFileMatch(new JspFilter()){file ->
  println file
}
```

Unfortunately, "File.eachFileMatch" passes "File.getName()" to the filter class, not "File.getAbsolutePath()". In other words, the filter sees MyServlet.class, not "./WEB-INF/classes/org/davisworld/MyServlet.class". This means that in order to do any sophisticated filtering on the list (for example, listing only those files bigger than a certain size), you should use "File.eachFile" or "File.eachFileRecurse" with your own if statement rather than relying on "File.eachFileMatch".

```groovy
//list files greater than 500kb
new File(".").eachFile{file ->
  if(file.size() > (500 * 1024)){
    println file
  }
}

===>
./GroovyLogo.zip
```

### 6.2 Reading the Contents of a File
```groovy
new File("x.txt").eachLine{line->
  println line
}
```

Just as you can walk through each file in a directory, you can also easily walk through each line of a file using File.eachLine. For binary files, there is also  "File.eachByte".

Section 8.14, Converting CSV to XML, on page 148 demonstrates a slightly more sophisticated version of File.eachLine. In the example, a comma-separated value  (CSV) file is walked through line by line using File.splitEachLine.

**Reading the Contents of a File into a String Variable**
```groovy
String body = new File("x.txt").text
```

It’s pretty convenient to be able to read in the entire contents of a file using a single method: File.getText(). This trick will prove to be convenient in later sections  such as Section 6.4, Copying Files, on page 108 and Section 6.3, Appending Data to an Existing File, on page 107.

For binary files, Groovy offers an alternate method, File.readBytes, which returns the entire contents as a byte[ ].

**Reading the Contents of a File into an ArrayList**
```groovy
List lines = new File("x.txt").readLines()
```

File.readLines returns the contents of the file as an ArrayList: one element per line in the file. This provides the convenience of having the entire file in memory (like File.getText()), while still allowing you to iterate through it line by line (like File.eachLine).

**Quick-and-Dirty File Content Analysis**
```groovy
// juliet.txt
O Romeo, Romeo! wherefore art thou Romeo?
Deny thy father and refuse thy name;
Or, if thou wilt not, be but sworn my love,
And I'll no longer be a Capulet.

// FileStats.groovy
File file = new File("juliet.txt")
List lines = file.readLines()
println "Number of lines: ${lines.size()}"
int wordCount = 0
file.splitEachLine(" "){words ->
println words.size()
wordCount += words.size()
}
println "Number of words: ${wordCount}"

===>
Number of lines: 4
7
7
10
7
Number of words: 31
```

Using the few convenience methods on File that we’ve discussed in this section, you can easily return some metadata such as line and word count. In this case, I chose a quick snippet from Romeo and Juliet[^602]. As programmers, it’s not too much of a reach to imagine a Groovy script that could recurse through a directory, looking only at .java files, and return a basic line count/file count for your project, is it?

### 6.3 Writing Text to a File
```groovy
File file = new File("hello.txt")
file.write("Hello World\n")
println file.text
===>
Hello World

println file.readLines().size()
===>
1
```

The convenience of a single File.write method in Groovy is pretty breathtaking. Contrast the four lines of Groovy code with the forty-plus lines of corresponding  Java code:
```groovy
import java.io.*;
public class NewFile {
  public static void main(String[] args) {
    File file = new File("hello.txt");
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      pw.println("Hello World");
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      pw.flush();
      pw.close();
    }

    BufferedReader br = null;
    int lineCount = 0;
    try {
      br = new BufferedReader(new FileReader(file));
      String line = null;
      while((line = br.readLine()) != null){
        System.out.println(line);
        lineCount++;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println(lineCount);
  }
}
```
The File.write method is destructive: the contents of the file are overwritten with the new data. The ability to write an entire file in a single line of code is used to great effect in Section 6.4, Copying Files, on page 108.

**Appending Data to an Existing File**
```groovy
File file = new File("hello.txt")
println "${file.size()} lines"
===> 1 lines
file.append("How's it going?\n")
file << "I'm fine, thanks.\n"
println "${file.size()} lines"
===> 3 lines
```
While File.write is a destructive call, File.append leaves the existing content in place, adding the new text to the end of the file.

Did you notice the operator overloading in action? The << operator is equivalent to the append() method call. (See Section 3.7, Operator Overloading, on page 50  for more information.)

**Merging Several Text Files**
```bash
? ls -al
drwxr-xr-x 8 sdavis sdavis 272 Dec 2 13:02 .
drwxr-xr-x 4 sdavis sdavis 136 Dec 2 12:53 ..
-rw-r--r--@ 1 sdavis sdavis 759 Nov 29 01:04 access.2007-11-28.log
-rw-r--r--@ 1 sdavis sdavis 823 Nov 30 01:01 access.2007-11-29.log
-rw-r--r--@ 1 sdavis sdavis 654 Dec 1 01:02 access.2007-11-30.log
-rw-r--r--@ 1 sdavis sdavis 233 Dec 2 13:04 merge.groovy
drwxr-xr-x 2 sdavis sdavis 68 Dec 2 12:59 summary

// merge.groovy
File logDir = new File(".")
File mergedFile = new File("summary/merged.log")
mergedFile.write("") //empty out the existing file
logDir.eachFile{file ->
  if(file.isFile() && file.name.endsWith(".log")){
    mergedFile << file.text
  }
}
```

At the end of each month, I like rolling up my web server’s daily traffic files into a monthly summary. With a mere eight lines of code, I can do this with ease. I create a file in the summary directory named "merged.log". If the file already exists, a quick mergedFile.write("") ensures that it is emptied out of any data from the previous run. I then walk through each item in the current directory, limiting my focus to files that end with .log. (The file.isFile check makes sure I don’t accidentally include a directory name that ends with .log.) mergedFile.append(file.text) takes the file contents of the current file and appends it to mergedFile.


### 6.4 Copying Files
```groovy
def src = new File("src.txt")
new File("dest.txt").write(src.text)
```

Combining the tricks from Section 6.2, Reading the Contents of a File, on page 104 and Section 6.3, Writing Text to a File, on page 105, you can see how easy it is to quickly write the text of one file to another.

You might think it’s odd that Groovy doesn’t provide a simple copy method to do this on your behalf. I wish I had a better response than “Uh, I agree.” At any rate, there are several other ways to copy files using Groovy that are worth looking into. And thanks to the dynamic nature of Groovy, at the end of this section I’ll show you how to fix this interesting API omission. (You might also take a look at Section 6.5, Using AntBuilder to Copy a File, on the following page for yet another 
way to copy files.)

**Copying Binary Files**
```groovy
File src = new File("src.jpg")
new File("dest.jpg").withOutputStream{ out ->
  out.write src.readBytes()
}
```

The majority of the convenience methods Groovy adds to java.io.File are geared toward text files. Luckily, binary files aren’t completely ignored. Calling withOutputStream allows you to write binary data within the closure, knowing that all that silly flush() and close() nonsense is already taken care of.

Of course, this method works for text files as well. What you sacrifice in brevity you gain back in a generic algorithm that can be used for any file, regardless of type.

**Copying Files Using the Underlying Operating System**
```groovy
File src = new File("src.jpg")
File dest = new File("dest.jpg")
"cp ${src.name} ${dest.name}".execute()
```
Using what we discussed in Section 5.4, Running a Shell Command, on page 89, letting your operating system do the heavy lifting makes quick work of copying files. You lose platform independence using this method, but you gain the full capabilities of the underlying operating system. Sometimes abstractions like java.io.File are helpful; other times they get in the way.

**Adding Your Own Copy Method to File**
```groovy
File.metaClass.copy = {String destName ->
  if(delegate.isFile()){
    new File(destName).withOutputStream{ out ->
      out.write delegate.readBytes()
    }
  }
}
new File("src.jpg").copy("dest.jpg")
```

Now that we’ve explored several ways to copy files, you can add the method of your choice directly to the java.io.File object. (For more information, see Section 10.11, Adding Methods to a Class Dynamically (ExpandoMetaClass), on page 198)

### 6.5 Using AntBuilder to Copy a File
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", tofile:"dest.txt")
```

Anything that can be expressed in the traditional Ant XML format (usually found in a file named build.xml) can also be expressed in Groovy code using an groovy.util.AntBuilder. (See Chapter 8, Writing XML, on page 136 for more on easily working with XML using Groovy builders.) Since the underlying Ant JARs are included with Groovy, you don’t even need to have Ant installed on your system to take advantage of AntBuilder.

In this example, we’re taking the <copy> task from Ant and using it in Groovy. (A great place to see all the core Ant tasks and their parameters is in the online documentation[^605]. ) Here is what this task looks like in its native Ant dialect:
```xml
// build.xml
<project name="test" basedir=".">
  <target name="copy">
    <copy file="src.txt" tofile="dest.txt" />
  </target>
</project>
```

```bash
$ ant copy
Buildfile: build.xml
copy:
    [copy] Copying 1 file to /
BUILD SUCCESSFUL
Total time: 0 seconds
```

Creating an AntBuilder object in Groovy implicitly takes care of the boilerplate <project> and <target> code, much like a Groovy script takes care of the boilerplate public class and public static void main(String[ ] args), as discussed in Section 5.3, Accepting Command-Line Arguments, on page 88. After that,  ant.copy(file:"src.txt", tofile:"dest.txt") mirrors the Ant XML, albeit in MarkupBuilder dialect.

It initially might seem strange to use Ant for things other than building Java projects. But if you think about it for just a moment, <javac> is only one of the many tasks that Ant supports natively. If Ant provides convenient tasks for copying, moving, renaming, and deleting files—all implemented in Java, therefore ensuring cross-platform compliance, I might add—why not take advantage of it? If you already are familiar with the common Ant tasks, this is a way you can reuse your existing knowledge rather than learning Yet Another API.

**Copying a File to a Directory**
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", todir:"../backup")
```

Another nicety that Ant offers is the ability to copy a file to a directory. If you want the filename to remain the same, this cuts down on a bit of repetition.

**Overwriting the Destination File**
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", tofile:"dest.txt", overwrite:true)
```

By default, Ant will not overwrite the destination file if it is newer than the source file. To force the copy to happen, use the overwrite attribute.

### 6.6 Using AntBuilder to Copy a Directory
```groovy
def ant = new AntBuilder()
ant.copy(todir:"backup"){
fileset(dir:"images")
}
```

```xml
// build.xml
<project name="test" basedir=".">
  <target name="backupImages">
    <copy todir="backup">
      <fileset dir="images" />
    </copy>
  </target>
</project>
```

To copy an entire directory of files (including subdirectories), you need to use a nested fileset. Notice that the nested XML shows up as a nested closure in Groovy.

**Selectively Including/Excluding Files**
```groovy
//NOTE: this WILL NOT copy files in subdirectories
// due to the pattern in include and exclude
def ant = new AntBuilder()
ant.copy(todir:"backup", overwrite:true){
  fileset(dir:"images"){
    include(name:"*.jpg")
    exclude(name:"*.txt")
  }
}
```
Expanding the fileset allows you to selectively include and exclude files based on pattern matching.

In accordance with Ant rules, the pattern "*.jpg" copies only those files in the parent directory. Files in subdirectories will not be copied unless you change the pattern to "**/*.jpg":
```groovy
//NOTE: this WILL copy files in subdirectories
// due to the pattern in include and exclude
def ant = new AntBuilder()
ant.copy(todir:"backup", overwrite:true){
  fileset(dir:"images"){
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}
```

**Flattening the Directory Structure on Copy**
```groovy
def ant = new AntBuilder()
ant.copy(todir:"backup", overwrite:true, flatten:true){
  fileset(dir:"images"){
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}

// images (before):
images/logo.jpg
images/big_image.jpg
images/icons/button.jpg
images/icons/arrow.jpg
images/thumbnails/big_image_thumb.jpg

// backup (after):
backup/logo.jpg
backup/big_image.jpg
backup/button.jpg
backup/arrow.jpg
backup/big_image_thumb.jpg
```
Ant offers a quirky little attribute called flatten on the <copy> task. Let’s assume you have files in images, images/icons, and images/thumbnails. If you want to consolidate them all to the backup directory without preserving the nested directory structure, you set the flatten attribute to true. Of course, bear in mind that you run the risk of filename collisions when you copy from many different directories into a single one. Remember to set the overwrite attribute appropriately.

### 6.7 Moving/Renaming Files
```groovy
// using the File method
File src = new File("src.txt")
src.renameTo(new File("dest.txt"))

// using the operating system
"mv src.txt dest.txt".execute()

// using AntBuilder
def ant = new AntBuilder()
ant.move(file:"src.txt", tofile:"dest.txt")
```
After Section 6.4, Copying Files, on page 108 and Section 6.5, Using AntBuilder to Copy a File, on page 109, this section might be a bit anticlimactic. You can move files using the standard JDK File.renameTo method. You can also shell out to your operating system. You can also use the AntBuilder.move method. They all do the same thing—it’s a matter of personal preference which technique you use.

### 6.8 Deleting Files
```groovy
// using the File method
new File("src.txt").delete()

// using the operating system
"rm src.txt".execute()

// using AntBuilder
def ant = new AntBuilder()
ant.delete(file:"src.txt")
```

The techniques covered in Section 6.4, Copying Files, on page 108 and Section 6.5, Using AntBuilder to Copy a File, on page 109 apply equally well here. You can use the standard JDK File.delete method. You can also shell out to your operating system. You can also use the AntBuilder. delete method.

**Deleting a Directory**
```groovy
def ant = new AntBuilder()
ant.delete(dir:"tmp")
```

Just like with AntBuilder.copy, you can delete either an individual file or a directory. Remember that AntBuilder.copy won’t overwrite a newer destination file? Well, AntBuilder.delete won’t delete empty directories unless you explicitly ask it to do so:
```groovy
def ant = new AntBuilder()
ant.delete(dir:"tmp", includeemptydirs:"true")
```

**Deleting Selected Files from a Directory**
```groovy
def ant = new AntBuilder()
ant.delete{
  fileset(dir:"tmp", includes:"**/*.bak")
}
```

The same nested filesets we used in Section 6.6, Using AntBuilder to Copy a Directory, on page 110 work here as well. Remember that *.bak will delete only the files in the current directory; **/*.bak recursively deletes files all the way down the directory tree.

### 6.9 Creating a ZIP File/Tarball
```groovy
def ant = new AntBuilder()

// zip files
ant.zip(basedir:"images", destfile:"../backup.zip")

// tar files
ant.tar(basedir:"images", destfile:"../backup.tar")
ant.gzip(zipfile:"../backup.tar.gz", src:"../backup.tar")
ant.bzip2(zipfile:"../backup.tar.bz2", src:"../backup.tar")
```

AntBuilder comes to the rescue once again when it comes to creating ZIP files. The techniques described here are similar to what we saw in Section 6.5, Using AntBuilder to Copy a File, on page 109.

Notice that AntBuilder.zip compresses the files by default. To compress a .tar file, you should call AntBuilder.gzip or AntBuilder.bzip2. Gzip is the more common compression format of the two, but bzip2 tends to yield a smaller (more compressed) file.

**Zipping Up Selected Files**
```groovy
def ant = new AntBuilder()
ant.zip(destfile:"../backup.zip"){
  fileset(dir:"images"){
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}
```

The same nested filesets we discussed in Section 6.6, Using AntBuilder to Copy a Directory, on page 110 work here as well. Remember that *.jpg will zip up only those files in the current directory; **/*.jpg recursively zips up files all the way down the directory tree.

AntBuilder.tar supports the same nested fileset that you see here with AntBuilder.zip.

### 6.10 Unzipping/Untarring Files
```groovy
def ant = new AntBuilder()

// unzip files
ant.unzip(src:"../backup.zip", dest:"/dest")

// untar files
ant.gunzip(src:"../backup.tar.gz")
ant.bunzip2(src:"../backup.tar.bz2")
ant.untar(src:"../backup.tar", dest:"/dest")
```

Not surprisingly, unzipping files looks much like what we discussed in Section 6.9, Creating a ZIP File/Tarball, on the preceding page. If your tarball is compressed,  you should gunzip or bunzip2 it as appropriate.

**Unzipping Selected Files**
```groovy
def ant = new AntBuilder()
ant.unzip(src:"../backup.zip", dest:"/dest"){
  patternset{
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}
```

This example is using a patternset in this example, although the same nested filesets that we discussed in Section 6.6, Using AntBuilder to Copy a Directory, on page 110 work here as well. Remember that *.jpg will unzip files only in the root of the zip file; **/*.jpg recursively unzips files all the way down the directory tree.

AntBuilder.untar supports the same nested patternset you can see here with AntBuilder.unzip.










[^502]: http://www.cygwin.com/
[^508]: http://tomcat.apache.org/tomcat-6.0-doc/realm-howto.html
[^601]: http://groovy.codehaus.org/groovy-jdk.html
[^602]: http://www.gutenberg.org/dirs/etext98/2ws1610.txt
[^605]: http://ant.apache.org/manual/index.html
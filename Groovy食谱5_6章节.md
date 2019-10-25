## 第5章 Groovy from the Command Line {#命令行中的Groovy}

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
+ 您使用的是类似Unix的操作系统：Linux，Mac OS X，Solaris等（很抱歉，Windows用户，除非您也是Cygwin [^502]用户）。
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

使用Groovy，您可以将相同的练习简化为基本要领。 在一个名为`Hola.groovy`的文件中键入启动此技巧的代码。 接下来键入`Hola Bub`。 由于groovy命令解释器将所有Groovy脚本都编译到内存中的有效Java字节代码中，因此您可以有效地得到Java示例，而不必键入所有其他示例代码。

这个简洁的if语句之所以有效，是因为Groovy的真理。有关更多信息，请参见第54页第3.10节，Groovy真相。

每个Groovy脚本都有一个隐式的`args`字符串数组，它表示传入脚本的命令行参数。(你猜对了——这就是《public static void main》的args。)要查看magic args数组的运行情况，请创建一个名为`cli.groovy`的文件。，然后键入以下内容:
```groovy
args.each{println it}
```

输入`groovy cli this is a test`，结果如下：
```bash
$ groovy cli this is a test

===>
this
is
a
test
```

### 5.4 Running a Shell Command {#运行Shell命令}
```bash
// in Windows:
println "cmd /c dir".execute().text

//in Unix / Linux / Mac OS X:
println "ls -al".execute().text
```
运行shell命令就像在字符串上调用`execute()`一样简单。这将返回一个`java.lang.Process`。您可以使用此技巧来运行完整的程序或简单的命令行任务。正如代码示例所演示的，字符串中的命令很可能在不同的操作系统之间有所不同。`ls`命令只适用于Mac OS X、Unix和Linux系统。`dir`命令只适用于Windows衍生工具。

如果仅在字符串上调用" .execute()"，则不会捕获结果输出文本。 对于`" rm some-file.txt".execute()`之类的命令，这可能是可以接受的。 如果要查看shell命令返回的输出，请在" .execute()"的末尾附加" .text"。

在类似Unix的系统上，大多数shell命令实际上是可执行程序。 键入哪个" ls"以查看命令的路径。 这意味着您通常在命令行中键入的几乎所有内容都可以用引号引起来并执行。 （此规则的一个不幸例外是在处理通配符时。有关更多详细信息，请参见下一页的第5.5节，在Groovy脚本中使用Shell通配符。）例如，您可以运行`println  "ifconfig".execute().text`查看当前网络设置。

在Windows系统上，`println "ipconfig /all".execute().text`返回相似的结果。 因为"ipconfig.exe"位于" c:\windows\system32"中的路径上，所以可以使用此技巧。 不幸的是，您在Windows中的命令提示符下键入的许多最常见命令根本不是可执行程序。 进行搜索时，您永远找不到隐藏在系统目录中的`dir.exe` 或 `copy.com`。 这些命令嵌入在 " cmd.exe" 中。

要执行它们，必须键入 " cmd /c"。 有关嵌入式命令的列表，请键入" cmd/?"。 在命令提示符下。 您将在Windows XP上看到以下列表：
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

知道了这一点，许多Windows用户只是将"cmd /c"放在他们在Groovy中执行的所有命令之前。 尽管有点冗长，但键入`"cmd/c ipconfig /all".execute().text`肯定不会对您造成任何伤害。

Windows用户的最后一点建议-不要忘记在目录中转义反斜杠：`println "cmd /c dir c:\\tmp".execute().text`。

### 5.5 Using Shell Wildcards in Groovy Scripts {#在Groovy脚本中使用Shell通配符}
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

在上一页的5.4节，运行Shell命令中，您了解到，在Windows机器上键入的一些常见命令（目录，副本等）已嵌入在"cmd Shell"中。 该外壳还可以管理通配符扩展。 因此，要求所有以".groovy"结尾的文件都是shell扩展为列表然后传递给dir命令的事情。

在类似Unix的系统上，外壳程序还负责扩展通配符。 知道这一点后，在命令中明确包含外壳程序是有意义的。 您可以输入`sh -c " ls -al * .groovy" `了解我们要完成的工作。

不幸的是，如果我尝试在单个字符串上调用`execute`，则此命令所需的嵌入式引号会引起我极大的伤痛。 幸运的是，我们也可以在String数组上调用execute。 数组中的第一个元素是命令，所有随后的元素都作为参数传递。 尽管这种形式较为冗长（乍一看也不太直观），但它确实有效。 我们为样式点获得-1，但为完成工作而获得+1。...

### 5.6 Running Multiple Shell Commands at Once {#一次运行多个Shell命令}
```bash
//in Windows:
println "cmd /c dir c:\\opt & dir c:\\tmp".execute().text
//in Unix / Linux / Mac OS X:
println "ls /opt & ls /tmp".execute().text
```
您可以使用“＆”字符将多个shell命令串在一起。 当然，这与Groovy无关-这是基础OS的功能。 为了证明这一点，请尝试直接在命令提示符下键入用引号引起来的命令。

### 5.7 Waiting for a Shell Command to Finish Before Continuing {#等待Shell命令完成后再继续}
```bash
def p = "convert -crop 256x256 full.jpg tile.jpg".execute()
p.waitFor()
println "ls".execute().text
```

如果您有一个运行时间较长的命令，并希望在继续操作之前等待它完成，则可以将该命令分配给一个变量，然后使用" .waitFor()"方法。 此示例显示了ImageMagick命令"convert -crop"，该命令拍摄大图像并将其分解为"256 x 256"像素的图块。 在显示当前目录的目录列表之前，您需要等待命令完成，以确保显示所有生成的图块。

### 5.8 Getting System Properties {#获取系统属性}
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

JVM为您提供了一个舒适的沙箱，保护您的代码不受操作系统差异的影响。Sun创造了“一次编写，随处运行”(WORA)这个短语来描述这种现象，尽管老前辈和愤世嫉俗者将其稍作修改为“一次编写，到处调试”。

您在本章中所做的几乎所有事情都将WORA视作眼中钉。 您正在操作系统级别上乱七八糟，如果您尝试在明确为其编写操作系统的任何地方运行它们，几乎可以肯定会破坏这些命令。 鉴于此，很高兴能够以编程方式确定您所运行的硬件类型，所使用的JVM版本，等等。 "System.properties"哈希表使您可以进行这种自省。

如果您已经知道要查找的变量的名称，则可以明确要求它； 例如，` System.getProperty(" file.separator")`可让您知道您是处于正斜线状态还是反斜线状态。

另一方面，你可能会想去逛街。输入`System.properties.each{println it}`允许您一个一个地输出完整的属性列表。这是一个展示运行系统中所有有趣部分的好工具。我通常在每个生产服务器上运行这个Groovlet，这样我就可以远程监视它们。(有关groovlet的更多信息，请参见第2.6节，在Web服务器上运行Groovy (groovlet)，见第33页。有关防止私有位成为公共位的更多信息，请参阅关于安全域的Tomcat文档[^508])。

以下是出现在我的MacBook Pro上的各种有用的系统属性:
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
>  您已经知道，这些是在Windows和类unix操作系统之间最常见的差异。

**user.dir**
>  这是当前目录（运行类的目录）。 如果您要查找相对于当前位置的目录和文件，则知道`user.dir`是很好的。

**java.io.tmp**
>  这是写入短期临时文件的好地方。 尽管确切的文件路径有所不同，但每个系统上都存在此变量。 有一个通用的垃圾场，可以保证在每个系统上都存在，是一个很好的隐藏的宝石。 只是不要期望这些文件会超出当前的执行范围。

**user.home**
>  尽管确切的文件路径各不相同，但可以保证每个小系统（如“ java.io.tmp”）都存在于每个系统上。 这是写入更多永久数据的好地方。

**Reading in Custom Values from -D or JAVA_OPTS**
`System.properties`哈希表不仅可以处理每个系统上出现的无聊的旧默认值，还可以提供更多好处。 自定义值可以通过两种方法传递到`System.properties`中。 如果您曾经在Ant目标中使用过“-D”参数（例如，`ant -Dserver.port = 9090 deploy`），则您知道它们也会显示在` System.properties`中（System.getProperty("server.port")）。 存储在`JAVA_OPTS`环境变量中的值也显示在`System.properties`中。

### 5.9 Getting Environment Variables {#获取环境变量}
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

与系统属性类似（如第5.8节，获取系统属性，在第92页中所述），环境变量是挖掘系统特定信息的另一个丰富途径。

如果您已经知道要查找的环境变量的名称，则可以明确要求它； 例如，`System.getenv("GROOVY_HOME")`可让您知道Groovy的安装目录。 要遍历系统上的所有环境变量，请执行`System.env.each {println it}`。

您可能会注意到环境变量和系统变量之间有一些重叠。 例如，`System.getProperty("groovy.home")`和`System. getenv("GROOVY_HOME")`都得到同样的事情：`/opt/groovy`。 有时，您要查找的特定信息只能在一个地方或另一个地方找到。 例如，环境变量列表可能包含未出现在系统属性列表中的变量，例如TOMCAT_HOME，JBOSS_HOME和ANT_HOME。

像其他任何东西一样，在不同的时间提供给您都很重要。 您的自定义调整可能通过环境变量或`-D`参数进入。 这些变量可能会将您指向用户的主目录或可以找到配置文件的应用程序目录，例如`server.xml`，`struts-config.xml`或`.bash_profile`。 重要的是，无论使用哪种特定机制，您都将能够管理整个系统。

### 5.10 Evaluating a String {#计算一个字符串}
```groovy
def s = "Scott"
def cmdName = "size"
evaluate("println s.${cmdName}()")
===> 5
cmdName = "toUpperCase"
evaluate "println s.${cmdName}()"
===> SCOTT
```

在第89页的5.4节中，我们讨论了如何对任意字符串调用execute。evaluate的工作方式略有不同。

evaluate不运行shell命令，而是允许您以Groovy代码的形式动态执行随机字符串。前面的示例动态地调用了String的`size()`和`toUpperCase()`上的两个方法。(你注意到第二个例子中的可选括号了吗?)这带来了一些有趣的功能，比如能够迭代一个对象上的所有方法并调用它们:
```groovy
//NOTE: This is pseudocode -- it won't actually run
def s = "Scott"
s.class.methods.each{cmdName ->
  evaluate("s.${cmdName}()")
}
```

尽管此示例无法按书面形式运行-它没有考虑到一些字符串方法需要的参数，比如“s.substring(2,4)”它显示了动态评估Groovy代码的潜在价值。它也很好地说明了其中的风险。如果您盲目地接受终端用户的命令并动态地执行它们，那么您应该为发送给您`rm -Rf /`的脚本准备好。有关动态评估方法的工作示例，请参见第10.4节，类方法的发现，见第188页。

### 5.11 Calling Another Groovy Script {#调用另一个Groovy脚本}
```groovy
// hello.groovy
println "Howdy"

// goodbye.groovy
hello.main()
println "Goodbye"
```
您可能一直从另一个Java类中调用一个Java类。如果这两个类在同一个包中，您可以直接从另一个调用: "AnotherClass.doSomething();"。如果它们位于单独的包中，则需要导入另一个包或完全限定类: "com.else.anotherclass.dosomething();"。从另一个Groovy脚本调用另一个Groovy脚本的工作原理基本相同。只要您记住Groovy代码被动态地编译为字节码，就永远不会出错。

在前面的例子中，"hello.groovy"被编译成如下等价的Java代码:
```groovy
public class hello{
  public static void main(String[] args){
    System.out.println("Howdy");
  }
}
```
小写的类名可能看起来很奇怪，但Groovy只是使用文件名作为类名。(听起来很熟悉吗?)没有显式封装在函数/闭包/脚本的"public static void main(String[ ] args)"中的脚本内容。位于相同目录中的两个脚本实际上位于相同的包中。因此，调用与所在目录相同的脚本与调用类上的静态main方法一样简单。

**使用参数调用另一个脚本**
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

由于脚本体实际上是"public static void main(String[ ] args)"方法，因此只能通过提供的字符串数组传递参数。

**在另一个脚本中调用方法**
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

如果其他脚本有静态方法(如main)，则可以静态地调用它们。如果其他脚本定义了实例方法，则必须在调用它们之前实例化脚本。

**调用不同目录中的另一个脚本**
```groovy
evaluate(new File("/some/other/dir/hello.groovy"))
```
我们的朋友`evaluate`又来了。(参见第95页第5.10节，求值字符串，用于求值的另一种用法。)这一次，您要计算的是一个文件，而不是一个任意字符串。这实际上调用了另一个文件的主方法。

如果您试图使用脚本到脚本的调用来做比我们已经讨论过的更复杂的事情，我的建议是将您的脚本编译成字节码，将它们放在您选择的包中，将它们打包，然后像调用其他任何Java类一样调用它们。

### 5.12 Groovy on the Fly (groovy -e) {#动态的Groovy (Groovy -e)}
```bash
$ groovy -e "println System.properties['java.class.path']"
===>
/opt/groovy/lib/groovy-1.1-beta-2.jar:/System/Library/Frameworks
/JavaVM.framework/Versions/1.5.0/Classes/.compatibility/14compatibility.jar
```

Groovy使快速运行代码变得很容易。您可以保存一个文件并立即运行它。您可以打开一个快速的Groovy shell或Groovy控制台来交互地使用该语言。但是有时候，在命令行上运行Groovy的一行就足够了。`-e`标志告诉Groovy对刚刚传入的字符串求值。

例如，假设您正在类路径上拾取一个奇怪的JAR。 您可以在类似Unix的系统上键入"echo $CLASSPATH"来查看环境变量是否是罪魁祸首。 （在Windows系统上设置将获得相似的结果。）如果类路径显示为空，那么讨厌的JAR可以在许多其他地方潜入。 查看 "$JAVA_HOME/lib, $JAVA_HOME/lib/ext, 和 $GROOVY_HOME/lib"，以查看是否有任何陌生人潜伏。 前面的示例将向您精确显示JRE所看到的内容-您可以从那里查找入侵者。

### 5.13 Including JARs at the Command Line {#在命令行中包含JAR}
```bash
$ groovy -classpath ~/lib/derbyclient.jar:~/lib/jdom.jar:. db2xml.groovy
```

如果您有依赖于其他库的脚本，则可以通过JAR列表向groovy传递`-classpath`开关。 当然，这与从命令行运行Java没有什么不同。 要运行我们的"db2xml.groovy"脚本，该脚本需要访问数据库驱动程序和XML库就不足为奇了。

**在`.groovy/lib`目录中自动包括JAR**
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

您很快就会厌倦了每次都必须在命令行上键入常用的JAR（例如JDBC驱动程序）。 如果在主目录中创建`.groovy/lib`目录（请不要忘记前导点），则在命令提示符下运行Groovy时，在此目录中找到的所有JAR都会自动包含在CLASSPATH中。 默认情况下，`.groovy/lib`目录是禁用的； 确保在`$GROOVY_HOME/conf/groovy-starter.conf`中启用它。

## 第6章 File Tricks {#文件的技巧}

Groovy提供了许多处理文件和目录的捷径。 列出文件，复制文件，重命名文件，删除文件-Groovy为所有这些平凡的任务带来了可喜的帮助。 Groovy直接向标准JDK类（例如java.io.File）添加新方法的事实使这些新功能看起来像是该语言的自然组成部分。

健壮的Java构建工具Ant在本章中也有客串出现。 Ant远远超出了标准Java I/O库功能，增加了对相关功能（如批处理操作和ZIP文件）的支持。 即使Ant是用Java编写的，但大多数开发人员熟悉的接口是普遍存在的build.xml文件。 Groovy对XML的本机支持在第7章“解析XML”（第116页）和第8章“编写XML”（第136页）中进行了广泛讨论。在本章中，您将看到一个很好的例子，说明如何在AntBuilder中使用它-所有功能 Ant，没有XML。 一路都是纯代码； 您再也不会以相同的方式查看构建文件了。

### 6.1 Listing All Files in a Directory {#列出目录中的所有文件}
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

Groovy添加到标准java.io.File中的eachFile方法使显示目录列表的工作很简单。 在这种情况下，您要查看当前目录（"."）。 当然，您也可以传入完全限定的目录名称：`new File("/opt/tomcat/webapps/myapp")`。

为了让您了解Groovy为您保存的击键信息，下面是Java中相应的代码:
```java
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

同样，您应该注意到Groovy增强了Java附带的对象`Java.io.file`。这意味着所有标准的JDK方法和新的Groovy方法都是可用的。`eachFile`方法被添加到类中，如第10.11节所讨论的，在第198页动态地将方法添加到类中(ExpandoMetaClass)。查看添加到java.io中的所有方法。文件，请参阅GDK文档[^601]。

**命令行输入**
```groovy
$ groovy list /some/other/dir

//list.groovy:
new File(args[0]).eachFile{file ->
  println file
}
```

对于这个脚本的更灵活的版本，您可以借鉴第5.3节中讨论的技巧，即接受命令行参数，见第88页。假设这个脚本保存在一个名为`list.groovy`的文件中，这个示例为您提供了传递任何目录名的灵活性。

**仅列出目录**
```groovy
new File(".").eachDir{dir ->
  println dir
}

===>
./META-INF
./WEB-INF
```

要将输出限制为目录，请使用`File.eachDir`。 您还可以使用`File.eachDirRecurse`遍历整个目录树：
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

**仅列出文件**
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

在本节的开头，我们看到`File.eachFile`返回文件和目录。 （不要怪Groovy，这反映了`File.listFiles`的标准JDK行为。）幸运的是，您可以使用另一种标准JDK方法来过滤输出：`File.isFile`。

Groovy还提供了一个`File.eachFileRecurse`方法，允许您查看目录树中的所有文件:
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

**列出目录中的特定文件**
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

if语句是一个将Groovy和Java结合使用的完美示例。`file.name`是Groovy中的`file.getName()`，如第72页4.2节“Getter和Setter快捷语法”中所述。name返回一个字符串，该字符串允许您使用标准JDK `endsWith()`方法。

如果您是正则表达式的爱好者，Groovy提供了一个`File.eachFileMatch`方法:
```groovy
new File(".").eachFileMatch(~/.*\.jsp/){file ->
  println file
}
```

`File.eachFileMatch`在技术上接受任何具有布尔方法`isCase(String s)`的类。这意味着你可以扩展这个例子来包含一个JspFilter类:
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

不幸的是，`File.eachFileMatch`将`File.getName()`传递给过滤器类，而不是`File.getAbsolutePath()`。换句话说，过滤器看到的是`MyServlet.class`，而不是`./WEB-INF/classes/org/davisworld/myservlet.class`。这意味着，为了对列表进行任何复杂的过滤(例如，只列出那些大于特定大小的文件)，您应该使用`File.eachFile`或`File.eachFileRecurse`自己的if语句，而不是依赖`File.eachFileMatch`。

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

### 6.2 Reading the Contents of a File {#读取文件的内容}
```groovy
new File("x.txt").eachLine{line->
  println line
}
```

正如您可以遍历目录中的每个文件一样，您也可以使用`file . eachline`轻松遍历文件的每一行。对于二进制文件，还有`File.eachByte`。

第8.14节，将CSV转换为XML，在第148页演示了一个稍微复杂的版本`File.eachLine`。在这个例子中，使用`file.spliteachline`逐行遍历逗号分隔值(CSV)文件。

**将文件内容读入字符串变量**
```groovy
String body = new File("x.txt").text
```

使用单一方法`file.gettext()`读取文件的全部内容非常方便。在后面的章节，如第108页的第6.4节“复制文件”和第107页的第6.3节“将数据附加到现有文件”中，这种技巧将被证明是很方便的。

对于二进制文件，Groovy提供了另一种方法`File.readBytes`，它以`byte[]`的形式返回整个内容。

**将文件的内容读入ArrayList**
```groovy
List lines = new File("x.txt").readLines()
```

`File.readlines`以ArrayList的形式返回文件的内容:文件中的每行一个元素。这提供了在内存中保存整个文件的便利(比如`File.gettext()`)，同时仍然允许逐行遍历(比如`File.eachline`)。

**快速文件内容分析**
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

使用我们在本节中讨论的几个方便的文件方法，您可以轻松地返回一些元数据，如行数和字数。在本例中，我选择了罗密欧与朱丽叶的一个简短片段[^602]。作为程序员，不难想象Groovy脚本可以递归遍历一个目录，只查看`.java`文件，并返回项目的基本"行数/文件数"，不是吗?

### 6.3 Writing Text to a File {#将文本写入文件}
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

一个文件的便利性。Groovy中的写方法是相当惊人的。将Groovy的四行代码与相应的Java代码的四十多行代码进行对比:
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
`File.write`方法是破坏性的：文件的内容被新数据覆盖。 在第108页的第6.4节“复制文件”中，可以使用用单行代码写入整个文件的功能就可以发挥很大的作用。

**将数据附加到现有文件**
```groovy
File file = new File("hello.txt")
println "${file.size()} lines"
===> 1 lines

file.append("How's it going?\n")
file << "I'm fine, thanks.\n"

println "${file.size()} lines"
===> 3 lines
```
虽然`File.write`是一个破坏性的调用，但`File.append`保留了现有内容，将新文本添加到文件的末尾。

你注意到操作符重载了吗? `<<`操作符等价于`append()`方法调用。(更多信息见第50页第3.7节，操作符重载。)

**合并多个文本文件**
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

在每个月末，我喜欢将网络服务器的每日流量文件汇总为每月摘要。 仅用八行代码，我就可以轻松实现这一点。 我在摘要目录中创建了一个名为`merged.log`的文件。 如果该文件已经存在，则可以通过快速`mergedFile.write("")`清空上一次运行中的所有数据。 然后，我遍历当前目录中的每个项目，将重点放在以`.log`结尾的文件上。 （`file.isFile`检查确保我不会意外地包含以`.log`结尾的目录名称。）`mergedFile.append(file.text)`获取当前文件的文件内容并将其附加到 mergedFile。


### 6.4 Copying Files {#复制文件}
```groovy
def src = new File("src.txt")
new File("dest.txt").write(src.text)
```

结合第6.2节(读取文件内容，见第104页)和第6.3节(将文本写入文件，见第105页)中的技巧，您可以看到将一个文件的文本快速写入另一个文件是多么容易。

您可能会觉得奇怪，Groovy没有提供一个简单的复制方法来代替您完成这项工作。我希望我有一个比“嗯，我同意”更好的回答。无论如何，还有其他几种使用Groovy复制文件的方法值得研究。由于Groovy的动态特性，在本节的最后，我将向您展示如何修复这个有趣的API遗漏。(你也可以看看6.5节，使用AntBuilder来复制文件，在接下来的页面中，你可以找到另一种复制文件的方法。)

**二进制文件复制**
```groovy
File src = new File("src.jpg")
new File("dest.jpg").withOutputStream{ out ->
  out.write src.readBytes()
}
```

Groovy添加到的大多数便利方法`java.io.File`都是针对文本文件的。幸运的是，二进制文件并没有完全被忽略。调用`withOutputStream`允许您在闭包中编写二进制数据，因为您知道那些愚蠢的`flush()`和`close()`废话都已经得到了解决。

当然，这种方法也适用于文本文件。你在简洁中所做的牺牲，在通用算法中得到了回报，通用算法可以用于任何文件，无论其类型如何。

**使用底层操作系统复制文件**
```groovy
File src = new File("src.jpg")
File dest = new File("dest.jpg")
"cp ${src.name} ${dest.name}".execute()
```
使用我们在第5.4节中讨论的内容，在第89页运行Shell命令，让您的操作系统完成繁重的工作，从而快速复制文件。使用这种方法会失去平台独立性，但是可以获得底层操作系统的全部功能。有时候像`java.io.File`这样的抽象是有帮助的;其他时候，他们会成为障碍。

**将自己的复制方法添加到文件中**
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

现在，我们已经探索了复制文件的几种方法，您可以将选择的方法直接添加到`java.io.File`对象中。(有关更多信息，请参见第10.11节，动态地向类添加方法`ExpandoMetaClass`，见第198页)

### 6.5 使用AntBuilder复制文件
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", tofile:"dest.txt")
```

任何可以用传统Ant XML格式表示的内容(通常可以在名为build.xml的文件中找到)也可以使用Groovy代码`Groovy.util.antbuilder`表示。(参见第8章，编写XML，第136页，了解更多关于使用Groovy生成器轻松使用XML的信息。)由于底层的Ant jar包含在Groovy中，您甚至不需要在系统上安装Ant来利用AntBuilder。

在此示例中，我们从Ant中获取`<copy>`任务，并在Groovy中使用它。 （在线文档[^605]中是查看所有核心Ant任务及其参数的好地方。）这是此任务在其本机Ant方言中的样子：
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

在Groovy中创建AntBuilder对象隐式地处理了样板`<project>`和`<target>`代码，就像Groovy脚本处理了样板`public class` 和 `public static void main(String [] args)`一样， 在第88页的第5.3节“接受命令行参数”中进行了讨论。此后，`ant.copy(file: "src.txt", tofile: " dest.txt")`镜像了Ant XML，尽管使用了MarkupBuilder方言。

最初，将Ant用于构建Java项目以外的事情似乎很奇怪。 但是，如果您想一想，<javac>仅仅是Ant本身支持的众多任务之一。 如果Ant提供了用于复制，移动，重命名和删除文件的便捷任务（全部用Java实现，因此确保了跨平台的合规性，我可能会添加），为什么不利用它呢？ 如果您已经熟悉了常见的Ant任务，则可以通过这种方法重用现有的知识，而不必学习“另一个API”。

**将文件复制到目录**
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", todir:"../backup")
```

Ant提供的另一个好处是可以将文件复制到目录中。 如果您希望文件名保持不变，则可以减少一些重复。

**覆盖目标文件**
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", tofile:"dest.txt", overwrite:true)
```

默认情况下，如果目标文件比源文件新，则Ant不会覆盖目标文件。 要强制执行复制，请使用`overwrite`属性。

### 6.6 Using AntBuilder to Copy a Directory {#使用AntBuilder复制目录}
```groovy
def ant = new AntBuilder()
ant.copy(todir: "backup"){
  fileset(dir: "images")
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

要复制文件的整个目录（包括子目录），您需要使用嵌套的文件集。 注意，嵌套的XML在Groovy中显示为嵌套的闭包。

**选择性地包含/排除文件**
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
扩展文件集使您可以根据模式匹配有选择地包括和排除文件。

根据Ant规则，模式 `*.jpg`仅复制父目录中的那些文件。 除非将模式更改为 `**/*.jpg`，否则不会复制子目录中的文件：
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

**在复制时将目录结构展平**
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
Ant在<copy>任务上提供了一个奇怪的小属性flatten。假设您有文件位于"images, images/icons, 和 images/thumbnails"目录中。如果希望在不保留嵌套目录结构的情况下将它们全部合并到备份目录，可以将flatten属性设置为true。当然，请记住，当您将许多不同的目录复制到一个目录中时，您可能会面临文件名冲突的风险。记住要适当地设置overwrite属性。

### 6.7 Moving/Renaming Files {#移动/重命名文件}
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
在第108页的第6.4节，复制文件和第109页的第6.5节，使用AntBuilder复制文件之后，本节可能有点过时了。 您可以使用标准JDK`File.renameTo`方法移动文件。 您也可以使用您的操作系统命令。 您也可以使用`AntBuilder.move`方法。 他们都做相同的事情-使用哪种技术完全取决于个人喜好。

### 6.8 Deleting Files {#删除文件}
```groovy
// using the File method
new File("src.txt").delete()

// using the operating system
"rm src.txt".execute()

// using AntBuilder
def ant = new AntBuilder()
ant.delete(file:"src.txt")
```

第6.4节，第108页的复制文件和第6.5节，使用AntBuilder复制文件的第109页中介绍的技术在这里同样适用。 您可以使用标准的JDK`File.delete`方法。 您也可以使用您的操作系统命令。 您也可以使用`AntBuilder.delete`方法。

**删除一个目录**
```groovy
def ant = new AntBuilder()
ant.delete(dir:"tmp")
```

与`AntBuilder.copy`一样，您可以删除单个文件或目录。还记得`AntBuilder.copy`不会覆盖新的目标文件吗? `AntBuilder.delete`不会删除空目录，除非你明确要求它这样做:
```groovy
def ant = new AntBuilder()
ant.delete(dir:"tmp", includeemptydirs:"true")
```

**从目录中删除选定的文件**
```groovy
def ant = new AntBuilder()
ant.delete{
  fileset(dir:"tmp", includes:"**/*.bak")
}
```

我们在第6.6节中使用的嵌套文件集(使用AntBuilder复制目录)在110页中也可以使用。请记住`*.bak`只删除当前目录中的文件;`**/*.bak`递归地删除目录树中的所有文件。

### 6.9 Creating a ZIP File/Tarball {#创建压缩文件}
```groovy
def ant = new AntBuilder()

// zip files
ant.zip(basedir:"images", destfile:"../backup.zip")

// tar files
ant.tar(basedir:"images", destfile:"../backup.tar")
ant.gzip(zipfile:"../backup.tar.gz", src:"../backup.tar")
ant.bzip2(zipfile:"../backup.tar.bz2", src:"../backup.tar")
```

在创建ZIP文件时，AntBuilder再次为您提供帮助。 这里描述的技术与我们在第109页的第6.5节“使用AntBuilder复制文件”中看到的技术类似。

请注意，默认情况下`AntBuilder.zip`会压缩文件。 要压缩`.tar`文件，应调用`AntBuilder.gzip`或`AntBuilder.bzip2`。 Gzip是两者中最常用的压缩格式，但是bzip2倾向于产生较小的文件（压缩程度更高）。

**压缩所选文件**
```groovy
def ant = new AntBuilder()
ant.zip(destfile:"../backup.zip"){
  fileset(dir:"images"){
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}
```

我们在第110页的第6.6节“使用AntBuilder复制目录”中讨论的嵌套文件集也可以在此处使用。 请记住`*.jpg`仅压缩当前目录中的那些文件； `**/*.jpg`递归地将文件压缩到目录树下。

`AntBuilder.tar`支持您在这里看到的与`AntBuilder.zip`相同的嵌套文件集。

### 6.10 Unzipping/Untarring Files {解压缩文件}
```groovy
def ant = new AntBuilder()

// unzip files
ant.unzip(src:"../backup.zip", dest:"/dest")

// untar files
ant.gunzip(src:"../backup.tar.gz")
ant.bunzip2(src:"../backup.tar.bz2")
ant.untar(src:"../backup.tar", dest:"/dest")
```

毫不奇怪，解压缩文件的外观很像我们在上一页的6.9节，创建`ZIP文件/Tarball`中讨论的那样。 如果压缩的tarball，则应适当对其进行gunzip或bunzip2压缩。

**解压缩所选文件**
```groovy
def ant = new AntBuilder()
ant.unzip(src:"../backup.zip", dest:"/dest"){
  patternset{
    include(name:"**/*.jpg")
    exclude(name:"**/*.txt")
  }
}
```

尽管在第110页的第6.6节“使用AntBuilder复制目录”中讨论的嵌套文件集也可以在此示例中使用，但该示例在此示例中使用了模式集。 请记住，`*.jpg`仅在zip文件的根目录中解压缩文件； `**/*.jpg`递归地将文件解压缩到目录树的下方。

`AntBuilder.untar`支持您在这里看到的与`AntBuilder.unzip`相同的嵌套模式集。


[^502]: http://www.cygwin.com/
[^508]: http://tomcat.apache.org/tomcat-6.0-doc/realm-howto.html
[^601]: http://groovy.codehaus.org/groovy-jdk.html
[^602]: http://www.gutenberg.org/dirs/etext98/2ws1610.txt
[^605]: http://ant.apache.org/manual/index.html
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

## File Tricks {#文件的技巧}

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

It’s pretty convenient to be able to read in the entire contents of a file using a single method: File.getText(). This trick will prove to be convenient in later sections  such as Section 6.4, Copying Files, on page 108 and Section 6.3, Appending Data to an Existing File, on page 107.

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

The convenience of a single File.write method in Groovy is pretty breathtaking. Contrast the four lines of Groovy code with the forty-plus lines of corresponding  Java code:
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
The File.write method is destructive: the contents of the file are overwritten with the new data. The ability to write an entire file in a single line of code is used to great effect in Section 6.4, Copying Files, on page 108.

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
While File.write is a destructive call, File.append leaves the existing content in place, adding the new text to the end of the file.

Did you notice the operator overloading in action? The << operator is equivalent to the append() method call. (See Section 3.7, Operator Overloading, on page 50  for more information.)

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

The majority of the convenience methods Groovy adds to java.io.File are geared toward text files. Luckily, binary files aren’t completely ignored. Calling withOutputStream allows you to write binary data within the closure, knowing that all that silly flush() and close() nonsense is already taken care of.

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

Now that we’ve explored several ways to copy files, you can add the method of your choice directly to the java.io.File object. (For more information, see Section 10.11, Adding Methods to a Class Dynamically (ExpandoMetaClass), on page 198)

### 6.5 Using AntBuilder to Copy a File
```groovy
def ant = new AntBuilder()
ant.copy(file:"src.txt", tofile:"dest.txt")
```

Anything that can be expressed in the traditional Ant XML format (usually found in a file named build.xml) can also be expressed in Groovy code using an groovy.util.AntBuilder. (See Chapter 8, Writing XML, on page 136 for more on easily working with XML using Groovy builders.) Since the underlying Ant JARs are included with Groovy, you don’t even need to have Ant installed on your system to take advantage of AntBuilder.

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

Creating an AntBuilder object in Groovy implicitly takes care of the boilerplate <project> and <target> code, much like a Groovy script takes care of the boilerplate public class and public static void main(String[ ] args), as discussed in Section 5.3, Accepting Command-Line Arguments, on page 88. After that,  ant.copy(file:"src.txt", tofile:"dest.txt") mirrors the Ant XML, albeit in MarkupBuilder dialect.

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
Ant offers a quirky little attribute called flatten on the <copy> task. Let’s assume you have files in images, images/icons, and images/thumbnails. If you want to consolidate them all to the backup directory without preserving the nested directory structure, you set the flatten attribute to true. Of course, bear in mind that you run the risk of filename collisions when you copy from many different directories into a single one. Remember to set the overwrite attribute appropriately.

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
After Section 6.4, Copying Files, on page 108 and Section 6.5, Using AntBuilder to Copy a File, on page 109, this section might be a bit anticlimactic. You can move files using the standard JDK File.renameTo method. You can also shell out to your operating system. You can also use the AntBuilder.move method. They all do the same thing—it’s a matter of personal preference which technique you use.

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

Just like with AntBuilder.copy, you can delete either an individual file or a directory. Remember that AntBuilder.copy won’t overwrite a newer destination file? Well, AntBuilder.delete won’t delete empty directories unless you explicitly ask it to do so:
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
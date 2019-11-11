## 第3章 Groovy新手 {#3_New_to_Groovy}

Groovy是对Java的补充、扩充，在某些情况下，它还提供了非常必要的改进。(毕竟，Java早在1995年就发布了。那是软件时代的前寒武纪，不是吗?) 例如，Java中需要的一些东西在Groovy中是可选的:分号、数据类型，甚至异常处理。默认情况下，Groovy自动包含的包比Java多得多。 Groovy向现有类(如String、List和Map)添加了新的方便方法。所有这些操作都是为了消除历史上减慢Java开发过程的一些减速带。

Groovy最有趣的地方是，您一直在编写它，甚至没有意识到它。Java在99%的情况下都是有效的Groovy—只需将`.java`文件重命名为`.groovy`，就可以运行了。(参见第69页的第4章，Java和Groovy集成，了解少数几种使Java不能成为100%有效的Groovy的边缘情况。)Groovy是Java的一个超集。它绝不意味着要取代Java。事实上，如果没有Java, Groovy就不会存在。Groovy旨在成**为比Java更好的Java**，同时始终支持您的遗留代码库。

但是Groovy不仅仅改进了现有的语言。Groovy引入了新的类，如`Closure`、`Range`和`GString`。Groovy引入了安全解引用的概念，以避免冗长的空检查块。Groovy提供了一个新的特殊的多行字符串变量。总的来说，Groovy以一种积极的方式“拥抱和扩展”Java。继续读下去，看看如果Java是在21世纪编写的，它会是什么样子。

### 3.1 自动导入 {#3_1_Automatic_Imports}
```groovy
import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.math.BigDecimal;
import groovy.lang.*;
import groovy.util.*;
```
Java自动为您导入`java.lang`包。 这意味着您可以使用诸如String和Integer之类，并调用`System.out.println()`，而不必在每个Java文件的顶部键入` import java.lang.*`。

在Groovy中，您可以获得许多附加包。换句话说，您可以使用这些包中的类，而不必在文件的顶部显式地导入它们。这些自动导入的净效果是，在默认情况下，您可以使用更多的JDK和GDK。Java类及其Groovy增强功能，如`List`(第3.14节，第58页上的List快捷方式)、`Map`(第3.15节，第62页上的Map快捷方式)、`File`(第6章，第100页上的File Tricks)和`URL`(第9章，第152页上的Web服务)，在您需要它们的时候就会出现。此外,常见的Groovy类,如`XmlParse`和`XmlSlurper`(7.2节,理解XmlSlurper XmlParse和之间的区别,117页),`Expando`(10.9节,创建一个Expando, 194页),和`ExpandoMetaClass`(添加一个类的方法动态(ExpandoMetaClass), 190页)准备好了,等待你由于自动导入,Groovy并代表你。

### 3.2 可选的分号 {#3_2_Optional_Semicolons}
```groovy
msg = "Hello"
msg += " World"; msg += "!";
println msg;
===> "Hello World!"
```

在Groovy中，分号是完全可选的。如果同一行有许多语句，则必须使用它们。否则，在一个语句的行尾使用它们现在是一种风格上的决定，而不是编译器的要求。

当然，这意味着我们应该为下一场大规模的技术圣战做好准备。“呵,分号,分号!你为什么是分号?”
::: alert-info
**偷偷走向DSL**
```groovy
def list = []
list.add("Groovy")
list.add "Groovy"
list << "Groovy"
```

这三个语句都是等效的。 每个都将Groovy一词添加到列表中。 第一种使用传统的Java的`add()`方法。 第二个调用相同的方法，只是不带括号。 第三种方法使用运算符重载(如第50页第3.7节“运算符重载”中讨论的那样)。 `<<`操作符在幕后调用`add()`方法。您是否喜欢一种语法而不喜欢其他语法，这是个人喜好的问题。在每种情况下，Groovy都试图使您的代码尽可能具有表现力和易于阅读，同时仍然保留一些实际执行的代码。

使用动态语言(如Groovy)的一个好处是，它使创建特定于领域的语言`(DSLs).∗`变得很容易。特性，比如可选圆括号(第3.3节，后面一页是可选圆括号)和可选分号(第3.2节，前面一页是可选分号)，为开发人员提供了工具，使编程变得不那么像编程。DSL可以看作是“可执行伪代码”。你也可以把它看作是一种允许非程序员做简单编程任务的方式。

```groovy
def shoppingList = []
def add = shoppingList.&add
def remove = shoppingList.&remove
add "Milk"
add "Bread"
add "Beer"
remove "Beer"
add "Apple Juice"
print shoppingList
```

除了省略圆括号和分号之外，这个简单的例子还使用了方法指针(第10.7节，在第193页创建一个方法指针)来进一步简化语法。很快，您就有了一些与编写源代码完全不同的东西。添加“牛奶”、删除“啤酒”和打印购物清单都感觉非常自然，即使对于非程序员也是如此。下一页继续。

- - - - - -
 (DSLs).∗ :  http://en.wikipedia.org/wiki/Domain-specific_programming_language
:::

::: alert-info
**偷偷走向DSL(续)**

将此与Java替代方法进行比较：“不要忘记在每一行的末尾都包括分号。分号。 就像“ 3:00”中小时和分钟之间的内容一样，只在逗号顶部加一个点，而不是两个点。 您找到了它，它位于键盘上的L键旁边。 好，现在让我们继续
公共静态void main（String [] args）....”

DSL的最好之处在于，它们不仅为初学者和非程序员带来好处-简化源代码对于所有相关人员而言都是轻松的胜利。
:::

我讨厌争论花括号应该放在哪里—如果对Kernighan和Ritchie足够好[^31]，那么对我来说也足够好。 就我而言，文本编辑器之战[^32]的胜利者已经决定了。您可以使用emac—我有一个可行的替代方案。(虽然有人在我背后说我是旧恶习的受害者，但我不会用回应来美化那些恶毒的谣言。)

那么，当涉及到可选分号时，我们该怎么办呢?我个人不使用它们，坦白地说，我也不想念它们。我认为，如果它们不是真正必需的，那么它们只不过是视觉上的杂乱-一个残留的尾巴，它反映了Groovy的过去，而不是决定了它的未来。一旦您被DSL的bug所困扰(请参阅前一页的侧栏)，就有机会去掉无法发音的符号，转而使用更像英语的编程风格，这是一个可喜的改变。(当然，我总是愿意你请我喝杯啤酒，试着让我明白我的错误。事先警告一下-可能要喝上几品脱才能说服我……)

### 3.3 可选的括号 {#3_3_Optional_Parentheses}
```groovy
println("Hello World!")
println "Hello World!"
===> "Hello World!"
```
在Groovy中，方法参数周围的括号是可选的。这通常用于简单的方法，如println。但是，如果方法没有参数，则必须仍然使用圆括号。例如:

```groovy
def s = "Hello"
println s.toUpperCase()
===> HELLO
```

无参数方法需要圆括号，因为否则编译器将无法区分方法调用和第4.2节(第72页的getter和setter快捷语法)中讨论的简短`getter/setter`调用之间的区别。在使用Groovy一段时间之后，当您在代码中看到`person.name`时，您将知道它是调用`person.getName()`的Groovy快捷方式。

**如何使无参数方法括号可选**
当然，如果整个“无参数括号”要求确实让您彻夜难眠，那么有几种巧妙的方法可以解决此问题。 （不，我不建议“切换到Ruby”。）

第一个解决方案是创建一个看起来像getter的方法，即使它根本不是真正的getter。我不是一个骄傲的人——我已经知道在我的Pizza类上编写getDeliver()这样的方法，以便稍后调用Pizza .deliver。当然，这违反了神圣的“getter/setter”契约，这是所有新手Java开发人员都必须签署的契约，但是如果不偶尔违反这些规则，为什么还要制定规则呢?

另一个绕过这些讨厌的空括号的选项是创建一个方法指针，如第10.7节中讨论的，在第193页创建一个方法指针:
```groovy
def pizza = new Pizza()
def deliver = pizza.&deliver()
deliver
```

**何时使用括号，何时省略括号**
既然您已经决定是否要使用分号，那么您将面临何时使用括号的难题。

我给你的建议和最高法院法官波特·斯图尔特的建议是一样的:当你看到它的时候你就会知道了。`println "Hello"`看起来不是比`System.out.println("Hello")`更好吗?我不能告诉你为什么——它就是这样。

但这并不意味着我总是避免括号。我可能用的比不用的多。如果我正在编写DSL(如第43页边栏中讨论的那样)，我倾向于使用更少的括号。如果我正在编写更传统的Java/Groovy代码，我将更经常地使用它们。但是在一天结束的时候，我没有一个艰难而快速的决策过程，除了“在这个时候，去掉括号似乎是正确的做法。”

### 3.4 可选的返回语句 {#3_4_Optional_Return_Statements}
```groovy
String getFullName(){
  return "${firstName} ${lastName}"
}

//equivalent code
String getFullName(){
  "${firstName} ${lastName}"
}
```

Groovy中方法的最后一行是一个隐式返回语句。我们可以显式地使用return语句，也可以安全地关闭它。

那么，为什么return语句是可选的呢?因为艾尔·戈尔说所有那些多余的不必要的打字是全球警告的第623个主要原因。“拯救按键，拯救地球”不仅仅是我当场想出的一个朗朗上口的口号。(事实上是这样，但你不同意它看起来像你会在《难以忽视的真相》中看到的东西吗?)

就像本章中所有其他可选内容一样，允许您省略return语句是为了减少编程语言的视觉噪音。在我看来，创建`add(x,y){x + y}`这样的方法是在简洁和可读性之间取得了恰当的平衡。如果你觉得它太简洁，那就不要用它。真的。没关系。

如果我需要过早地退出一个方法，我就会使用return语句。例如，我非常相信快速失败，所以在我的withdraw()方法中，会尽快返回“资金不足——稍后再试”。如果我在方法的早期使用return，我可能也会在最后一行使用它来实现视觉对称。另一方面，return并没有为快速的单行方法(如前一段中的add方法)增加多少清晰度。Groovy允许我有目的地编程，而不是让我屈服于编译器的同行压力。当我准备好时，我将使用return，而不是因为编译器让我这么做。

### 3.5 可选数据类型声明(鸭子类型) {#3_5_Optional_Datatype_Declaration__Duck_Typing_}
```groovy
//In scripts:
w = "Hello"
String x = "Hello"
println w.class
===> java.lang.String
println w.class == x.class
===> true
//In compiled classes:
def y = "Hello"
String z = "Hello"
println y.class
===> java.lang.String
println y.class == z.class
===> true
```

Groovy不会强制您显式地定义变量的类型。`def name = "Jane"`等价于`String name = "Jane"`——两者都是字符串。关键字`def`的意思是，“我不太关心这个变量是什么类型的，您也不应该太关心。请注意，在脚本和Groovy Shell中(与编译类相反)，您可以更加随意，完全不使用`def`。实际上，在`Groovy Shell`中，应该去掉数据类型声明。(更多信息请参见第30页的侧栏。)

另一方面，Java是一种静态类型语言。这意味着当你声明每个变量时，你必须给它一个数据类型:
```groovy
Duck mallard = new Mallard();
```

在这段代码中，您不能分辨Duck是类还是接口。(考虑 `List List = new ArrayList()` 和 `ArrayList List = new ArrayList()`。)鸭子可能是野鸭的父类。也许它是一个定义鸭子行为的接口。如果编译器允许您将绿头鸭填充到鸭子形状的变量中，那么绿头鸭必须提供与鸭子相同的所有方法。无论绿头鸭是如何实现的，您都可以安全地说(至少可以说)绿头鸭是Duck类型的。

这个概念称为多态性-希腊语为“许多形状”。多态性是运行流行的依赖项注入（DI）框架（例如Spring，HiveMind和Google Guice）的动力。 这些DI引擎允许开发人员保持其类之间的松散耦合。 例如，如果您在整个代码中对对MySQL JDBC驱动程序的引用进行硬编码，则如果您以后决定切换到PostgreSQL，则必须执行广泛的搜索和替换任务。 另一方面，`java.sql.Driver`是一个接口。 您可以简单地对Driver接口进行编码，并允许Spring在运行时注入适当的JDBC驱动程序实现。

Groovy用Java编写，因此通过扩展，所有变量都具有特定的数据类型。 Groovy的不同之处在于，您不必在使用变量之前就明确声明它的数据类型。 在快捷脚本中，这意味着您只需编写`w =“ Hello”`。 您可以确定w确实是java.lang.String类型，不是吗？ 使用groovyc编译Groovy时，如果要声明变量而不显式声明类型，则必须使用`def`关键字。

为什么这很重要？ 这不仅是为您节省了一些宝贵的按键。 这很重要，因为它将Groovy从一种静态类型的语言转移到了一种动态类型的语言。 动态类型语言的对象在编译时不必满足接口的“合同”要求； 他们只需要在运行时正确响应方法调用即可。 （有关此示例，请参见第185页的第10.3节“检查字段的存在”和第190页的第10.5节“检查方法的存在”。）
```groovy
def d = new Duck()
```
几本畅销Python书籍的作者Alex Martelli创造了duck typing[^35] 这个短语来描述动态类型语言。只要变量像鸭子一样“走路”和像鸭子一样“嘎嘎叫”，它就不必被正式声明为Duck类型——换句话说，它必须在运行时响应那些方法调用。

### 3.6 可选的异常处理 {#3_6_Optional_Exception_Handling}
```groovy
//in Groovy:
def reader = new FileReader("/foo.txt")

//in Java:
try{
  Reader reader = new FileReader("/foo.txt")
} catch(FileNotFoundException e){
  e.printStackTrace()
}
```

在Java中，有两种类型的异常:检查的和未检查的。已检查异常扩展`java.lang.Exception`。我们必须封装可能在`try/catch`块中抛出异常的方法。例如，如果传入不存在的文件名，FileReader构造函数将抛出FileNotFoundException。未检查异常扩展`java.lang.Error`或`java.lang.RuntimeException`。 方法可能会引发NullPointerException，ClassCastException和IndexOutOfBoundsException之类的异常，但是编译器不需要您将它们包装在`try/catch`块中。 针对`java.lang.Error`的Javadoc说，我们不需要捕获此类异常，因为这些错误是不应该发生的异常情况。

尽管Java允许在已检查和未检查的异常之间进行微妙的区分是很不错的做法，但不幸的是，我们开发人员无法自行确定严重性级别。 如果FileReader构造函数引发了一个已检查的异常，并且您认为该异常不够重要，则编译器将尊重您的意见并拒绝编译您的代码。
```bash
$ javac TestFile.java
TestFile.java:6: unreported exception java.io.FileNotFoundException;
must be caught or declared to be thrown
Reader reader = new FileReader("/foo.txt");
1 error
```

但是，如果您只是在上一行中显式创建了文件，该怎么办？ 上一次文件创建失败是什么时候？ 是否有95％的可能性发生？5％？0.0005％？ 它类似于SunSetException（每天发生的事情）还是SunJustExplodedException？ 换句话说，是您期望发生的事情还是可能发生的事情（“永远不应该发生的异常情况”）？

如果您一直在写该文件，而现在只想读回内容怎么办？ FileNotFoundException在这里是否有意义？ 如果您试图获取操作系统上始终存在的目录的句柄，例如`/etc/hosts`或`c:\windows`，该怎么办？ 即使编译器具有最佳意图，一个简单的单行命令现在也需要六行代码。

更阴险的是，您认为catch块现在包含什么?如果您回答“什么都没有”、“我的IDE生成了什么”或“关闭那个愚蠢的编译器的最低限度”，那么您是正确的。

格伦·范德堡（Glenn Vanderburg）说：“错误的开发人员会把天堂和地球转移到错误的地方。”但是，良性疏忽呢？只接受您的IDE自动生成的代码（这很可能是带有todo标签的空块）？

如果我踢到你最喜欢的圣牛的小腿，我道歉。我很欣赏检查异常的意图， 但是一想到现在有多少空的catch块在生产环境中运行，有多少开发人员在常规实践中捕获异常，我就不寒而栗， 以及有多少异常被吃掉了，并且永远不会被重新抛出，因为它们的目的是为了让应用程序保持正常运行。

现在考虑有多少代码专门用于可怕的(但未选中)NullPointerException。我经常得到null值，但是编译器将其归类为“不应该发生的异常情况”。“显然，在已检查和未检查异常的意图和现实之间存在差距。

Groovy通过将所有已检查的异常转换为未检查的异常来解决这个问题。这一小步将返回异常严重程度的判断给开发人员。如果您运行的web服务经常从最终用户获得格式不正确的请求，您可能会选择显式地捕获NullPointerException，即使Java编译器不需要它。 如果您指的是一个不可能丢失的文件(例如: `WEB-INF/web.xml`)。您可以选择不捕获FileNotFoundException。 多亏了Groovy，“不应该发生的异常条件”的定义现在完全回到了您的控制之中。就像使用可选的逗号和括号一样，您的编程也是有目的的。捕获异常是因为您希望这样做，而不是编译器希望您这样做。

### 3.7 操作符重载 {#3_7_Operator_Overloading}
```groovy
def d = new Date()
===> Sat Sep 01 13:14:20 MDT 2007

d.next()
===> Sun Sep 02 13:14:20 MDT 2007

(1..3).each{ println d++ }
===>
Sat Sep 01 13:14:20 MDT 2007
Sun Sep 02 13:14:20 MDT 2007
Mon Sep 03 13:14:20 MDT 2007
```
在离开Java语言很长一段时间之后，操作符重载在Groovy中仍然很活跃。正如您在本例中所看到的，`++`操作符在幕后调用`next()`方法。下面的列表显示了操作符和相应的方法调用:
| 操作符    | 方法    |
| :-- | :-- |
|a == b or a != b     | a.equals(b)    |
| a + b  | a.plus(b) |
| a - b  | a.minus(b) |
| a * b  | a.multiply(b) |
| a / b  | a.div(b) |
| a % b  | a.mod(b) |
| a++ or ++a  | a.next() |
| a- - or - -a  | a.previous() |
| a & b  | a.and(b) |
| a | b  | a.or(b) |
| a[b]  | a.getAt(b) |
| a[b] = c  | a.putAt(b,c) |
| a << b  | a.leftShift(b) |
| a >> b  | a.rightShift(b) |
| a < b or a > b or a <= b or a >= b  | a.compareTo(b) |

这种语法糖出现在GDK[^37] (JDK的Groovy增强)中。例如，第58页的第3.14节“列表快捷方式”演示了添加到`java.util.List`中的一些方便操作符。您可以使用传统的Java方法(`List.add("foo")`)或新的Groovy方法(`List << "foo"`)向列表添加项。

当然，您也可以将这些方法添加到您自己的类中。 在Groovy中 `order.leftShift(item)` 变成  `order << item` .

是否使用运算符重载取决于您，但是我必须承认，`date + 7`的感觉比`date.roll(Calendar.DATE,7)`更加自然。

### 3.8 安全解除引用(?) {#3_8_Safe_Dereferencing____}
```groovy
def s = "Jane"
s.size()
===> 5
s = null
s.size()
Caught: java.lang.NullPointerException: Cannot invoke method size()
        on null object at CommandLine.run(CommandLine.groovy:2)
//notice that we can call size()
//without throwing a NullPointerException
//thanks to the safe dereferencing ? operator
s?.size()
===> null
```

Null引用可能会意外出现。 由于它们既常见又昂贵（在Java中引发异常会中止操作），因此许多Java程序员习惯于围绕潜在的空情况进行防御性编程，例如：
```groovy
if(s != null){
  s.doSomething();
}
```

如果接收到Null引用并不像编译器希望的那样灾难性，那么这将很繁琐（且冗长）。 如果您想忽略NullPointerException并以静默方式进行操作，Groovy提供了一种快捷方式。 在任何可能为空的对象引用的末尾添加一个问号，Groovy会在后台为您将调用包装在`try/catch`块中。

```groovy
s?.doSomething()
```

此安全解除引用可以链接到任何深度。 假设您有一个Person类，它有Address类和PhoneNumber类。 您可以安全地一直追溯到电话号码，而不必担心为每个单独的潜在NullPointerException捕获信息。

```groovy
//in Java:
if(person != null && person.getAddress() != null
          && person.getAddress().getPhoneNumber() != null ){
  System.out.println(person.getAddress().getPhoneNumber());
}
else{
  System.out.println("");
}

//in Groovy:
println person?.address?.phoneNumber
```

### 3.9 自动装箱 {#3_9_Autoboxing}
```groovy
def i = 2
println i.class
===> java.lang.Integer

def d = 2.2
println d.class
===> java.math.BigDecimal
```

自动装箱有助于克服Java语言的特殊性：Java是面向对象的。 Java提供原始数据类型（int，float，double）以及对象（Integer，Float，Double）。 在1995年，这是一个合理的让步。 使用基本类型提高速度； 使用对象是为了方便开发人员。 在发布Java 5时，Sun添加了自动装箱(透明地将原语提升为大写字母对象中)，以帮助消除这种历史上的奇怪现象。Sun并没有消除原始/对象的划分;它只是让它不那么容易显现。

Groovy使Java 5自动装箱又迈出了一步-它可以快速自动装箱所有东西。 这意味着您可以执行有趣的任务，例如在Java开发人员看来像原始的对象上调用方法：
```groovy
2.class
===> class java.lang.Integer

2.toFloat()
===> 2.0

3.times{println "Hi"}
Hi
Hi
Hi
```
即使显式地将变量转换为原始类型，仍然会得到一个对象。在Groovy中，一切都是对象。一切。就Groovy而言，原始类型已经不存在了。
```groovy
float f = (float) 2.2F
f.class
===> class java.lang.Float
```

如果调用一个Java方法，而该方法需要的是原始类型而不是对象，情况又会如何呢?无需担心—groovy根据需要解箱(unboxes)这些值。如果你想要更精确的控制，你可以使用`as`关键字:
```groovy
javaClass.javaMethod(totalCost as double)
```

如果显式地将数字转换为浮点数或双精度浮点数，它将自动装箱为浮点数或双精度浮点数。如果你只输入一个小数点后的数字，它会自动装箱为BigDecimal。这是为什么呢?它主要是为了避免Java中可怕的“浮点算术”问题:
```groovy
//In Java:
public class PiggyBank{
public static void main(String[] args){
  double sum = 0.0d;
  for(int i = 0; i < 10; i++){
    sum += 0.1d;
  }
  System.out.println(sum);
  }
}

$ java PiggyBank
===> 0.9999999999999999
```

假设你连续十天把一枚10美分的硬币放在你的储蓄罐里。根据Java的说法，你最终得到的是一美元，还是一种渐进地接近一美元的东西，却从来没有真正得到它?

约书亚•布洛赫(Joshua Bloch)在他的开创性著作《有效的Java》(Effective Java)中有整整一节专门讨论了这一点。在149页，项目31的标题说明了一切:“如果需要确切的答案，避免浮点数和双精度数。Groovy如何处理相同的问题?
```groovy
//In Groovy:
def sum = 0
10.times{ sum += 0.1}
println sum
===> 1.0
```

用于`java.math`的Javadoc。BigDecimal指出，它最适合用于“不可变的、任意精度带符号的小数”。BigDecimal类让用户完全控制舍入行为。“最小意外原则表明`1.1 + 1.1应该返回2.2`,`10 * 0.1应该等于1.0`。BigDecimal(和Groovy)提供了您期望的结果。

### 3.10 Groovy的True {#3_10_Groovy_Truth}
```groovy
//true
if(1) // any non-zero value is true
if(-1)
if(!null) // any non-null value is true
if("John") // any non-empty string is true

Map family = [dad:"John", mom:"Jane"]
if(family) // true since the map is populated

String[] sa = new String[1]
if(sa) // true since the array length is greater than 0

StringBuffer sb = new StringBuffer()
sb.append("Hi")
if(sb) // true since the StringBuffer is populated

//false
if(0) // zero is false
if(null) // null is false
if("") // empty strings are false

Map family = [:]
if(family) // false since the map is empty

String[] sa = new String[0]
if(sa) // false since the array is zero length

StringBuffer sb = new StringBuffer()
if(sb) // false since the StringBuffer is empty
```

"Groovy truth"是Groovy语言中评估为true的简写。 在Java中，唯一评估为true的东西就是true。 这会导致很多无关的输入。 例如，如果您尝试引入Java中的命令行参数，则必须执行以下操作：
```groovy
//in Java:
if(args != null && args.length > 0){
  File dir = new File(args[0]);
} else{
  System.out.println("Usage: ListDir /some/dir/name" );
}
```

当然，您只需编写`File dir = new File(args [0])`并希望取得最佳效果。 但是，如果您的用户提供的参数数量不正确怎么办？ 如果他们键入`java ListDir`而不是`java ListDir /tmp`，该怎么办？ 您希望他们看到哪个错误？
```groovy
//default message:
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 0
at ListDir.main(ListDir.java:6)
//your custom error message:
Usage: ListDir /some/dir/name
```

多亏了`Groovy truth`，同样的错误捕获代码块可以被缩短为:
```groovy
//in Groovy:
if(args){
  dir = new File(args[0])
}
else{
  println "Usage: ListDir /some/dir/name"
}
```
`0、NULL和""(空字符串)`的值都为false。这意味着在处理来自用户的输入时，简单的if(args)捕获了所有最可能要避免的事情。

### 3.11 嵌入引号 {#3_11_Embedded_Quotes}
```groovy
def s1 = 'My name is "Jane"'
def s2 = "My name is 'Jane'"
def s3 = "My name is \"Jane\""
```
Groovy向Java字符串添加了一些不错的新技巧。在Java中，一个单引号用于表示一个char基元。在Groovy中，我们可以使用单引号来包围字符串。这意味着我们可以使用单引号来保存包含双引号的字符串，而不必转义它们。当然，包含嵌入单引号的双引号字符串也是如此。使用退格转义字符在两种语言中是相同的。

### 3.12 heredoc(三重引号) {#3_12_Heredocs__Triple_Quotes_}
```groovy
String s = """This is a
multi-line String.
"You don't need to escape internal quotes" , he said.
"""

def ss = '''This
That, The Other'''

def xml = """
<book id="987">
  <title>Groovy Recipes</title>
  <author>Scott Davis</author>
</book>"""

def html = """<body onload="init()">...</body>"""
```

Heredocs [^312]支持多种动态语言，从Python到Perl到Ruby。 Heredoc允许您将多行字符串存储在单个变量中。 Groovy使用三引号（三个单引号或三个双引号）来定义Heredocs。

即使您的字符串是单行的，heredocs仍然非常有价值。 将XML，HTML或JSON片段放入变量中是进行单元测试的好方法。 不必转义内部引号可以轻松地将输出复制到变量中并立即开始针对该变量编写断言。

有关heredocs实际应用的示例，请参阅第239页第12.4节，设置Atom提要。


### 3.13 Groovy的String {#3_13_GStrings}
```groovy
def name = "John"
println "Hello ${name}. Today is ${new Date()}"
===> Hello John. Today is Fri Dec 28 15:16:32 MDT 2007
```

对于任何使用Ant构建文件或Java服务器页面(jsp)的人来说，字符串中嵌入的美元符号和大括号都是一个熟悉的场景。它使字符串连接比传统Java容易得多: `"Hello " + name + "."`。 Groovy以GString(当然是“Groovy字符串”的缩写)的形式将这种语法引入到语言中。任何包含表达式的字符串都是GString:
```groovy
println "Hello John".class
===> class java.lang.String

println "Hello ${name}".class
===> class org.codehaus.groovy.runtime.GStringImpl
```

混合GString和heredocs(上一页的第3.12节，heredocs(三重引号))构成了一个特别强大的组合:
```groovy
def name = "John"
def date = new Date()
def amount = 987.65
def template = """
Dear ${name},
  This is a friendly notice that ${amount} was
  deposited in your checking account on ${date}.
"""
```

### 3.14 列表快捷方式 {#3_14_List_Shortcuts}
```groovy
def languages = ["Java", "Groovy", "JRuby"]
println languages.class
===> java.util.ArrayList
```

Groovy为创建ArrayLists提供了一种简洁的语法。

将以逗号分隔的值列表放在等号右边的方括号中，就得到了一个列表。(Maps也提供了类似的简单构造—参见第62页3.15节，Map快捷方式)。

虽然在默认情况下，方括号将提供一个ArrayList，但您可以在行尾加上as子句，以转换成其他各种数据类型。例如:
```groovy
def languages = ["Java", "Groovy", "JRuby"] as String[]
def languages = ["Java", "Groovy", "JRuby"] as Set
```

**创建一个空列表**
```groovy
def empty = []
println empty.size()
===> 0
```

要创建空列表，只需使用空集符号。

**添加一个元素**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages << "Jython"
===> [Java, Groovy, JRuby, Jython]
```

向列表中添加项很容易。Groovy将`<<`操作符重载到`leftShift()`方法，以完成此任务。(有关操作符重载的更多信息，请参见第50页第3.7节，操作符重载。)

**得到一个元素**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
println languages[1]
println languages.getAt(1)
==> Groovy
```

即使从技术上讲是列表，您也可以对其进行类似数组的调用。 Groovy模糊了列表和数组之间的语法区别，使您可以使用最喜欢的样式。

**Iterating(迭代)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]

//使用默认的“it”变量:
languages.each{println it}
===>
Java
Groovy
JRuby

//使用您选择的指定变量:
languages.each{ lang ->
  println lang
}
===>
Java
Groovy
JRuby
```

遍历列表是一种常见的活动，因此Groovy为您提供了一种方便的方法。在第一个示例中，使用迭代器变量的默认名称`it`。在第二个示例中，您显式地将变量命名为`lang`。

当然，您仍然可以使用所有传统的Java方法来遍历列表。如果你喜欢Java 5 的 `for..in`语法或 `java.util.Iterator`迭代器，您可以继续使用它。请记住Groovy增强了Java;它不会取代它。

**使用索引进行迭代**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.eachWithIndex{lang, i ->
  println "${i}: ${lang}"
}
===>
0: Java
1: Groovy
2: JRuby
```

"eachWithIndex()"为您提供当前元素和计数器变量。

**Sort(排序)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.sort()
===> [Groovy, JRuby, Java]
println languages
===> [Groovy, JRuby, Java]
```
您可以轻松地对列表进行排序。请注意，这是一个永久性的更改。`sort()`修改原始列表的内部排序顺序。

**Reverse(反转)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.reverse()
===> [JRuby, Groovy, Java]
println languages
===> [Java, Groovy, JRuby]
```
您可以轻松地反转列表。注意`reverse()`不会修改列表的原始排序顺序。它返回一个新列表。

**Pop(弹出)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.pop()
===> "JRuby"
println languages
===> [Java, Groovy]
```
您可以从列表中弹出内容。 pop方法使用LIFO，表示后进先出。 请注意，这是永久更改。 `pop()`从列表中删除最后一项。

**Concatenating(级联)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
def others = ["Jython", "JavaScript"]
languages += others
===> [Java, Groovy, JRuby, Jython, JavaScript]
languages -= others
===> [Java, Groovy, JRuby]
```
您可以轻松地将两个列表添加在一起。你可以很容易地再减去它们。

**Join(连接)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
groovy> languages.join()
===> JavaGroovyJRuby
groovy> languages.join(",")
===> Java,Groovy,JRuby
```
便捷方法`join()`返回一个字符串，其中包含List中的每个元素。 如果将字符串参数传递给`join()`，则每个元素将由字符串分隔。

**Find All(找到所有)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.findAll{ it.startsWith("G") }
===> [Groovy]
```

`findAll()`允许查询列表。它返回一个新列表，其中包含与您的条件匹配的所有元素。

**Max, Min, Sum(最大,最小,总和)**
```groovy
def scores = [80, 90, 70]
println scores.max()
===> 90
println scores.min()
===> 70
println scores.sum()
===> 240
```

`max()`返回列表中的最大值。 `min()`返回最小值。 `sum()`汇总列表中的所有元素。

**Collect(收集)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
languages.collect{ it += " is cool"}
===> [Java is cool, Groovy is cool, JRuby is cool]
```
如果要修改列表中的每个元素，则可以使用`collect()`方法。 请注意，`collect()`不会修改原始列表。 它返回一个新的列表。


**Flatten(展平)**
```groovy
def languages = ["Java", "Groovy", "JRuby"]
def others = ["Jython", "JavaScript"]
languages << others
===> [Java, Groovy, JRuby, [Jython, JavaScript]]
languages = languages.flatten()
===> [Java, Groovy, JRuby, Jython, JavaScript]
```
如果您具有多维列表，则`flatten()`返回一维数组。 请注意，`flatten()`不会修改原始列表。 它返回一个新的列表。

**`Spread Operator (*)` (点差算子)**
```groovy
def params = []
params << "jdbc:mysql://localhost:3306/bookstore_dev?autoreconnect=true"
params << "com.mysql.jdbc.Driver"
params << "username"
params << "password"
def sql = groovy.sql.Sql.newInstance(*params)
```

顾名思义，spread运算符将List的元素展开。 在此示例中，newInstance方法需要四个字符串参数。 `*params`接受List并将元素散布到方法参数的每个插槽中。

`spread-dot(扩展点)`运算符的工作方向相反。 它允许您简洁地遍历列表，在每个元素上调用相同的方法：
```groovy
def languages = ["Java", "Groovy", "JRuby"]
println languages*.toUpperCase()
===> [JAVA, GROOVY, JRUBY]
```

### 3.15 映射快捷方式 {#3_15_Map_Shortcuts}
```groovy
def family = [dad:"John", mom:"Jane"]
println family.getClass()
===> java.util.LinkedHashMap
```

Groovy为创建映射提供了简洁的语法。您只需在等号右边的方括号中放入`逗号限制的名称/值对列表`，就得到了一个映射。列表提供了一个类似的简单构造—参见第3.14节，列表快捷方式，在第58页。

**创建一个空映射**
```groovy
def empty = [:]
println empty.size()
===> 0
```
要创建空映射，只需使用带冒号的空集表示法。

**得到一个元素**
```groovy
def family = [dad:"John", mom:"Jane"]
family.get("dad")
family.dad
===> John
```

您可以使用传统的Java `get()`方法从Map中返回一个元素。 但是，Groovy缩短了此语法，使其看起来就像您在直接调用该键一样。

如果您想使用类似数组的语法，那么family ['dad']是从Map中获取元素的另一种方法。

::: alert-info
**陷阱：`.class`为什么在除Map之外的所有类上都能工作？**
```groovy
def family = [dad:"John", mom:"Jane"]
println family.class
===> null
println family.getClass()
===> java.util.LinkedHashMap
```

由于使用点表示法将元素从Map中取出，因此调用`map.class`返回null而不是类类型。 为什么？ 因为您的Map不包含名为class的元素。 对于Map，必须使用方法调用的长Java形式:`map.getClass()`。 当然，`getClass()`可在所有类中使用，因此，如果您希望100％的时间使用它，则这可能是最安全的调用形式。

需要更多信息，请参阅第73页上的侧栏。
:::

**添加元素**
```groovy
def family = [dad:"John", mom:"Jane"]
family.put("kid", "Timmy")
family.kid2 = "Susie"
===> {dad=John, mom=Jane, kid=Timmy, kid2=Susie}
```
您可以使用传统的Java `put()`方法将元素添加到Map中。 Groovy将其缩短为用于获取元素的相同的点号。

如果您希望使用类似数组的语法，`family[’kid2’] = "Susie"`也是有效的。

**Iterating(迭代)**
```groovy
def family = [dad:"John", mom:"Jane"]

//using the default 'it' variable:
family.each{println it}
===>
dad=John
mom=Jane

//getting the key and value from 'it'
family.each{println "${it.value} is the ${it.key}" }
===>
John is the dad
Jane is the mom

//using named variables for the key and value
family.each{k,v ->
  println "${v} is the ${k}"
}
===>
John is the dad
Jane is the mom
```

遍历映射是一种常见的活动，因此Groovy为您提供了一种方便的方法。第一个示例使用迭代器变量的默认名称it。下一个示例将使用`it.key` 和 `it.value`获取`名称/值对`的单独部分。最后一个示例显式地分别命名键和值变量`k和v`。

**Concatenating(连接)**
```groovy
def family = [dad:"John", mom:"Jane"]
def kids = [kid:"Timmy", kid2:"Susie"]
family += kids
===> {dad=John, kid=Timmy, kid2=Susie, mom=Jane}

kids.each{k,v->
  family.remove("${k}")
}
===> {dad=John, mom=Jane}
```

您可以轻松地将两个Map添加在一起。 Groovy没有提供从另一个Map中减去一个Map的捷径，但是语法太短了，至多只是一个小小的疏忽。

**Finding Keys(查找键)**
```groovy
def family = [dad:"John", mom:"Jane"]
family.keySet()
===> [dad, mom]
family.containsKey("dad")
===> true
```

您可以使用与Java的`keySet()`中相同的策略来查找映射的键，该策略返回所有键的列表，`containsKey()`让您知道键是否存在。

**Finding Values(查找值)**
```groovy
def family = [dad:"John", mom:"Jane"]
family.values()
===> [John, Jane]
family.containsValue("John")
===> true
```

您可以使用与Java的`values()`中相同的策略来查找Groovy中的Map值，它返回所有值的列表，`containsValue()`让您知道某个值是否存在。

### 3.16 范围 {#3_16_Ranges}
```groovy
def r = 1..3
println r.class
===> groovy.lang.IntRange
r.each{println it}
===>
1 2 3
r.each{ println "Hi" }
===>
Hi
Hi
Hi
(1..3).each{println "Bye"}
===>
Bye
Bye
Bye
```

Groovy为范围提供了一种本机数据类型。您可以在变量中存储一个范围，也可以动态地创建和使用它们。

为了简单起见，这里的所有示例都使用整数。但范围要灵活得多。它们可以包含实现`Comparable`接口并具有`next()`和`previous()`方法的任何类。考虑一下这个日期范围的快速示例:
```groovy
def today = new Date()
===> Sat Dec 29 23:59:28 MST 2007
def nextWeek = today + 7
===> Sat Jan 05 23:59:28 MST 2008
(today..nextWeek).each{println it}
===>
Sat Dec 29 23:59:28 MST 2007
Sun Dec 30 23:59:28 MST 2007
Mon Dec 31 23:59:28 MST 2007
Tue Jan 01 23:59:28 MST 2008
Wed Jan 02 23:59:28 MST 2008
Thu Jan 03 23:59:28 MST 2008
Fri Jan 04 23:59:28 MST 2008
Sat Jan 05 23:59:28 MST 2008
```


**Size, From, To(大小，从，到)**
```groovy
def r = 1..3
r.size()
===> 3
r.from
===> 1
r.to
===> 3
```

我们可以询问范围的大小，起点和终点

**For(for循环)**
```groovy
for(i in 1..3){ println "Attempt ${i}" }
===>
Attempt 1
Attempt 2
Attempt 3
(1..3).each{ println "Attempt ${it}" }
===>
Attempt 1
Attempt 2
Attempt 3
```

范围通常用于for循环，尽管直接在范围上调用`each`更为简洁。


**Contains(包含)**
```groovy
def r = 1..3
r.contains(1) && r.contains(3)
===> true
r.contains(2)
===> true
r.contains(12)
===> false
```
范围可以告诉您任意值是否落在该范围内。 起点和终点都包括在范围内。

**Reverse(反转)**
```groovy
r.reverse()
===> [3, 2, 1]
```
如果您需要向后遍历Range，则有一个方便的`reverse()`方法。

### 3.17 闭包和块 {#3_17_Closures_and_Blocks}
```groovy
def hi = { println "Hi"}
hi()
===> Hi
```

`groovy.lang.Closure`最简单的形式是一个独立的，命名的代码块。 它是没有周围类的行为。

实际上，闭包并不是一个完全陌生的概念。我们在Java中有代码块(if、for、while、try、catch等)，只是没有命名的代码块。Groovy增加了这种微小的语义差异，并在很大程度上利用了它。(有关闭包的实际应用示例，请参见219页11.8节“理解控制器和视图”。)

如果您不认为这是严格意义上的学术意义[^317]，那么我很谦虚地表示歉意。 我还将有意识地避免使用诸如“ lambda样式的函数式编程”之类的短语[^318]。我并不是很讨厌-事情的简单事实是实现类名为Closure。


**接受参数**
```groovy
def hello = { println "Hi ${it}" }
hello("John")
hello "John"
===> Hi John
```

熟悉的匿名it参数在第3.14节中讨论过，列表快捷方式在第58页，第3.15节中讨论过映射快捷方式在第62页。请注意，在调用闭包时可以省略括号，就像在调用方法时一样。(更多信息见第44页3.3节，可选括号。)

下面是一个更高级的闭包实例。注意如何在each和convertToCelsius闭包中使用it参数。

```groovy
def convertToCelsius = {
  return (5.0/9.0) * (it.toFloat() - 32.0)
}
[0, 32, 70, 100].each{
  println "${it} degrees fahrenheit in celsius: ${convertToCelsius(it)}"
}

===>
0 degrees fahrenheit in celsius: -17.7777777792
32 degrees fahrenheit in celsius: 0.0
70 degrees fahrenheit in celsius: 21.1111111128
100 degrees fahrenheit in celsius: 37.7777777808
```

**命名参数**
```groovy
def calculateTax = { taxRate, amount ->
  return amount + (taxRate * amount)
}
println "Total cost: ${calculateTax(0.055, 100)}"
===> Total cost: 105.500
```

尽管匿名it参数在编写快速的临时脚本时非常方便，但是从长远来看，命名参数将有助于提高代码的可读性和可维护性。如果您的闭包需要多个参数，那么除了为它们命名之外别无选择。

**Currying Parameters(固化参数)**
```groovy
def calculateTax = { taxRate, amount ->
  return amount + (taxRate * amount)
}

def tax = calculateTax.curry(0.1)
[10,20,30].each{
  println "Total cost: ${tax(it)}"
}
===>
Total cost: 11.0
Total cost: 22.0
Total cost: 33.0
```

在实例化闭包时，可以使用curry方法将值预加载到参数中。在本例中，为taxRate硬编码一个默认值将显著降低闭包的可重用性。另一方面，每次调用闭包时都必须传递相同的税率，这是不必要的重复和冗长。提高税率正好达到了恰当的平衡。

您可以根据需要使用任意数量的参数。 第一个curry调用将填充最左侧的参数。 每个后续调用将填充右侧的下一个参数。


## 第4章 Java和Groovy集成 {#4_Java_and_Groovy_Integration}

Groovy最大的卖点之一是它与Java的无缝集成。在本章中，我们将以各种方式探讨这种集成。我们将使用普通的旧Groovy对象(POGOs)作为普通旧Java对象(pojo)的完全替代。我们将从Java调用Groovy代码，从Groovy调用Java代码。最后，我们将探索如何使用Ant来编译整个项目，包括Groovy和Java类的健康组合。

### 4.1 Groovy的对象 {#4_1_GroovyBeans__aka_POGOs_}
```groovy
package org.davisworld.bookstore
class Book{
  String title
  String author
  Integer pages
}
```

正如我们在第16页的1.1节Groovy中所看到的，这就是POGO的全部内容。 Groovy将JavaBeans归结为纯粹的本质。

**Packaging(打包)**
在此示例中，您会注意到的第一件事是包装。 您可能永远不需要打包Groovy脚本，但是Groovy类的打包方式与Java类的打包方式相同。 （有关编写Groovy脚本的更多信息，请参见第86页的命令行中的第5章，Groovy。）在Java开发人员看来，唯一看起来奇怪的是缺少分号。 （正如我们在第42页的3.2节，可选分号中所讨论的，您可以根据需要将其重新添加。）

**Public Classes, Private Attributes, Public Methods**
```groovy
// in Groovy:
class Book{
  String title
  String toString(){
    return title
  }
}
// in Java:
public class Book{
  private String title;
  public String toString(){
    return title;
  }
}
```

如果您未提供访问修饰符（公共，私有或受保护的），则Groovy中的类隐式是`公共的`。 在Java中，如果不另行说明，则类是`包私有的`。 如果您在两种语言之间来回移动时不注意，这可能是一个严重的“陷阱”。 （有关更多信息，请参见下页的侧栏。）

如果您不提供访问修饰符，则Groovy中的属性将隐式`私有`。 您可以通过一些内省来证明这一点：

```groovy
println Book.getDeclaredField("title")
===> private java.lang.String Book.title
```

Groovy中的方法默认情况下是`公共的`。 这是证明：

```groovy
println Book.getDeclaredMethod("toString")
===> public java.lang.String Book.toString()
```

那么，Groovy开发人员对包私有访问有什么反对意见呢?没什么,真的。他们的目标是默认允许类做正确的事情，而包私有访问是一个不幸的附带损害。

花一分钟想想你最近从事的主要Java项目。您有多少带有私有属性的公共POJOs? 在这里，您可能可以在此处安全地使用“ 80/20规则”，但是如果我按了您的要求，则最终可能会达到90％或更高。带有私有属性的公共类是编写的绝大多数Java代码，Groovy的智能默认设置反映了这一业务现实。

::: alert-info
**Gotcha: No Package-Private Visibility(陷阱：没有包私有可见性)**

在Java中，如果将访问修饰符保留在类，属性或方法之外，则意味着同一包中的其他类或另一个包中的直接子类可以直接访问它们。 这称为包私有访问。在Groovy中，没有访问修饰符的类被视为公共类。 没有访问修饰符的属性被视为私有。 没有访问修饰符的方法是公共的。 尽管可以说这种快捷方式对于主流用法更为有用，但是它代表了Java语义不同于Groovy语义的少数情况之一。

在Groovy中，无法为类，属性或方法包提供`包私有`的可见性。 在Groovy中声明公共，私有和受保护元素的方式与在Java中声明方式相同。

------

∗. http://java.sun.com/docs/books/tutorial/java/javaOO/accesscontrol.html
:::

### 4.2 自动生成的Getter和Setter {#4_2_Autogenerated_Getters_and_Setters}
```groovy
class Book{
  String title
}

Book book = new Book()
book.setTitle("Groovy Recipes")
println book.getTitle()
===> Groovy Recipes
```

尽管没有如此明显的public和private修饰符会稍微减少类的大小，但是真正自动产生getter和setter的才是真正的区别。 默认情况下，POGO中的每个属性都会获得一个匹配集。

再回想一下您的上一个Java项目。你是精心手工制作了每个getter和setter，还是让IDE生成了代码?

如果这段代码是死记硬背的，并且没有什么意思，那么就让Groovy编译器代替您的IDE来为您生成它，可以极大地减少项目中的视觉混乱。而且，如果您碰巧覆盖了默认值

getter或setter的行为，看看你的眼睛是如何被规则的例外吸引的:
```groovy
class Book{
  String title
  String author
  Integer pages
  String getTitle(){
    return title.toUpperCase()
  }
}
```

**Getter and Setter Shortcut Syntax(Getter和Setter快捷语法)**
```groovy
class Book{
  String title
}
Book book = new Book()
book.title = "Groovy Recipes"
//book.setTitle("Groovy Recipes")
println book.title
//println book.getTitle()
===> Groovy Recipes
```

Groovy减少视觉混乱的另一个方法是在处理类属性时允许的语法捷径。`book.title`在幕后调用`book.getTitle()`。这是一种使它感觉更自然的尝试——似乎是直接处理图书的标题，而不是调用返回字符串值的Book类的`getTitle()`方法。(有关更多信息，请参见下一页的侧栏。)

遗留的Java的`getter`和`setter`语法在Groovy中仍然是完全有效的。

**Suppressing Getter/Setter Generation(抑制生成Getter/Setter)**
```groovy
class Book2{
  private String title
}

println Book2.getDeclaredField("title")
===> private java.lang.String Book2.title
println Book2.methods.each{println it}; "DONE"
// neither getTitle() nor setTitle() should appear in the list
```

在Groovy中显式地将字段标记为private会抑制相应的getter和setter方法的创建。如果您希望字段对Java类真正隐藏起来，那么这个小工具非常有用。

::: alert-info
**Groovy语法快捷键**

如第4.2节“Getter和Setter快捷语法”所示，在前一页，`book.getTitle()`可以缩写`为book.title`。虽然这个`getter/setter`快捷方式是Groovy中的默认行为，但是在语言中有很多地方它被选择性地覆盖，以表示完全不同的意思。

在第3.15节，Map快捷方式，在62页，一个像书一样的调用。hashmap上的`book.title`是`book.get("title")`的快捷方式。在第7章解析XML中，在第116页，同样的调用是解析XML片段的一种快速方法，如`<book><title>Groovy Recipes</title></book>`。在第10.8节中，调用不存在的方法(invokeMethod)，在第193页，您将学习如何接受该调用并使用它做几乎任何您想做的事情。

我不认为这是一个陷阱;事实上，我认为它是一个强大的语言特性。但如果你没有预料到，它会让你猝不及防。
:::

但是其他Groovy类的可见性又如何呢?好吧，这段代码应该非常清楚，字段仍然是可访问的，尽管缺乏getter和setter方法:
```groovy
def b2 = new Book2()
b2.title = "Groovy Recipes"
println b2.title
===> Groovy Recipes
```

Groovy在隐私方面存在一些问题——简而言之，它忽略了私有修饰符。(是的，这是一个相当大的问题。有关更多信息，请参阅第80页的侧栏。)

如果希望保护私有字段不受Groovy中意外修改的影响，可以添加一对不做任何事的`getter`和`setter`。将这些方法标记为私有将防止它们扰乱公共API。

```groovy
class Book3{
  private String title
  private String getTitle(){}
  private void setTitle(title){}
}

def b3 = new Book3()
b3.title = "Groovy Recipes"
println b3.title
===> null
```

### 4.3 getProperty和setProperty {#4_3_getProperty_and_setProperty}
```groovy
class Book{
  String title
}

Book book = new Book()
book.setProperty("title", "Groovy Recipes")
//book.title = "Groovy Recipes"
//book.setTitle("Groovy Recipes")

println book.getProperty("title")
//println book.title
//println book.getTitle()
===> Groovy Recipes
```

这个例子展示了在POGO上设置和获取属性的第三种方法--`book.getProperty()`和`book.setProperty()`。在传统Java中，调用`book.getTitle()`是第二天性。正如我们在4.2节中讨论的，Getter和Setter快捷语法，在第72页，Groovy允许您将`book.getTitle()`缩短为`book.title`。但是，如果希望使用更通用的方法来处理类的字段，该怎么办呢?

Groovy借鉴了` java.lang.System`的技巧，它提供了一种访问类属性的通用方法。 如第5.8节“获取系统属性”（第92页）中所述，您无法进行诸如` System.getJavaVersion()`之类的方法调用。 您必须以更通用的方式要求系统属性-` System.getPropery(" java.version")`。 要获取所有适当关系的列表，请输入` System.getProperties()`。 现在，通过` groovy.lang.GroovyObject`接口可以在每个类上使用这些通用方法。

是的，您总是可以使用`java.lang.reflect`包来完成这种事情，但是Groovy使该语法易于使用。 一旦您开始更常规地处理元编程，与类进行交互的这种方式将与` book.getTitle()`或`book.title`一样自然。 有关此内容的更多信息，请参见第10.2节，发现类的字段，第183页。

**Property Access with GStrings(使用GStrings进行属性访问)**
```groovy
class Book{
  String title
}
def b = new Book()
def prop = "title"
def value = "Groovy Recipes"
b."${prop}" = value
println b."${prop}"
===> Groovy Recipes
```

与`getProperty`和`setProperty`方法一样，还有一种更“出色”的方法来处理字段。您可以将字段的名称传递给GString以获得最大的灵活性。(有关GStrings的更多信息，请参见第3.13节，GStrings，第57页。)

### 4.4 使属性只读 {#4_4_Making_Attributes_Read-Only}
```groovy
class Book{
  final String title
  
  Book(title){
    this.title = title
  }
}

Book book = new Book()
book.title = "Groovy Recipes"
//===>
//ERROR groovy.lang.ReadOnlyPropertyException:
//Cannot set readonly property: title for class: Book

Book book2 = new Book("GIS for Web Developers")
println book2.title
//===>
//GIS for Web Developers
```

最终修饰符在Groovy和Java中的工作方式相同。 具体来说，这意味着只能在实例化类时才能设置属性。 如果您尝试在事实之后修改属性，则会引发` groovy.lang.ReadOnlyPropertyException`。

### 4.5 构造函数快捷语法 {#4_5_Constructor_Shortcut_Syntax}
```groovy
class Book{
  String title
  String author
  Integer pages
}

Book book1 = new Book(title:"Groovy Recipes", author:"Scott Davis", pages:250)
Book book2 = new Book(pages:230, author:"Scott Davis",
                      title:"GIS for Web Developers")
Book book3 = new Book(title:"Google Maps API")
Book book4 = new Book()
```
Groovy为构造函数提供了便利，这是您在Java中从未见过的。 通过支持命名参数和变长参数列表，您可以按照自己认为合适的任何方式实例化类。 book1和book2演示了由于已命名变量，因此您可以按任何顺序提供它们。 book3演示了等式的vararg部分：在这种情况下，您只需输入标题即可。 book4演示了Groovy便捷方法中的任何一个都不会干扰默认的Java构造函数。

这个构造函数快捷方式的特别之处在于，它也可以在纯Java类中使用。构造函数行为是在运行时添加的，因此它既适用于Groovy也适用于Java类。有关这方面的实际演示，请参见第4.9节，从Groovy调用Java，见第81页。

### 4.6 可选参数/默认值 {#4_6_Optional_Parameters_Default_Values}
```groovy
class Payment{
BigDecimal amount
  String type
  public Payment(BigDecimal amount, String type="cash"){
    this.amount = amount
    this.type = type
  }

  String toString(){
    return "${amount} ${type}"
  }
}

def pmt1 = new Payment(10.50, "cash")
println pmt1
//===> 10.50 cash

def pmt2 = new Payment(12.75)
println pmt2
//===> 12.75 cash

def pmt3 = new Payment(15.99, "credit")
println pmt3
//===> 15.99 credit
```
在本例中，除非显式提供另一个值，否则输入默认值“cash”。这简化了开发过程，不需要您维护两个单独的重载构造函数—一个只接受数量，另一个接受数量和类型。可选参数的真正好处在于，它们可以用于任何类型的方法。请考虑以下简化电影票购买的方法:

```groovy
class Ticket{
  static String buy(Integer quantity=1, String ticketType="adult"){
    return "${quantity} x ${ticketType}"
  }
}

println Ticket.buy()
println Ticket.buy()
println Ticket.buy(2)
println Ticket.buy(4, "child")
===>
1 x adult
1 x adult
2 x adult
4 x child
```
在本例中，单一方法提供了极大的灵活性。如果您在没有参数的情况下调用它，它将对所有内容使用智能默认值。下一个最有可能的场景(理论上)是两个人约会——代码允许您覆盖数量，同时仍然默认票证类型为“adult”。

在支付示例中，金额没有默认值。您需要提供它每次您创建一个新的付款。另一方面，如果没有提供，则默认为“cash”。可选参数必须总是在所有必需参数之后。可选参数也应该按重要程度排序——最可能更改的参数应该在列表的最前面，其次是最可能更改的参数，以此类推，最后是最不可能被覆盖的参数。
```groovy
static String buy(Integer quantity=1, String ticketType="adult",BigDecimal discount=0.0)
//won't compile
Ticket.buy(0.15)
//will compile
Ticket.buy(1, "adult", 0.15)
```
考虑到新buy()方法中参数的顺序，您不可能在不指定所有三个值的情况下请求一张成人票的15%折扣。optionals列表中的级联重要性顺序表明，可以安全地忽略右边的参数，但是必须指定左边的参数。

### 4.7 私有方法 {#4_7_Private_Methods}
```groovy
class Book{
  String title
  private String getTitle(){
    return title
  }

  private void setTitle(String title){
    this.title = title
  }

  private void poke(){
    println "Ouch!"
  }
}

Book book = new Book()

// 注意，Groovy完全忽略了私有访问修饰符
book.title = "Groovy Recipes"
println book.title
===> Groovy Recipes
book.poke()
===> Ouch!
```
简单地说，Groovy不关心方法的私有访问修饰符。您可以像调用公共方法一样轻松地调用私有方法。(有关更多信息，请参见下一页的侧栏。)

私有方法不会显示在公共界面中。 也就是说，当您调用`Book.methods.each {println it}`时，` poke()`不会出现。 您知道` poke()`可用的唯一方法是，如果您前面有源代码。

Java尊重private修饰符。 在Java中实例化时，不能通过常规方式调用poke()。

### 4.8 从Java调用Groovy {#4_8_Calling_Groovy_from_Java}
```java
public class BookstoreJava implements Bookstore {
  private Book b; // written in Groovy
  private Publisher p; // written in Java

  public Book makeBook() {
    b = new Book();
    b.setAuthor("Scott Davis");
    b.setTitle("Groovy Recipes");
    b.setPages(250);
    return b;
  }

  public Publisher makePublisher() {
    p = new Publisher();
    p.setName("Pragmatic Bookshelf");
    return p;
  }
}
```
此时，您可能正在寻找Book是用Groovy实现的，而Publisher是用Java实现的证据。这就是重点!一旦用Groovy编写的类被编译，它看起来与用Java编写的类没有什么不同。自动生成的getter和setter(第4.2节，自动生成的getter和setter，在第71页)与Java实现的getter和setter没有什么区别。这使得Groovy成为JavaBeans的完美替代品。

::: alert-info
**陷阱:Groovy忽略私有修饰符**

如第78页的第4.7节私有方法所示，Groovy允许您像调用公共方法一样轻松地调用类的私有方法。正如第4.2节(抑制Getter/Setter生成)中所演示的，在第72页，Groovy允许您像访问公共字段一样访问私有字段。

底线是Java尊重私有访问修饰符;Groovy不。Java是敲你前门的邻居，即使它知道你把钥匙藏在哪里。Groovy是一个让自己进来借一杯糖，然后在厨房桌子上给你留一张纸条的邻居。当我第一次使用Groovy时，这是我发现的最令人不安的(咳咳)特性。在最好的情况下，忽略私人修饰语似乎是不礼貌的。在最坏的情况下，这可能是完全危险的。

也许是Groovy对隐私的漫不经心的态度让我最初感到不舒服。很容易调用一个私有方法，你会想，“这一定是个bug。”当然，您也可以通过使用`java.lang.reflect`绕过Java中的私有修饰符。但是由于某些原因，在Java中调用私有方法似乎更加谨慎。在Java中，您必须有意识地去调用私有方法。你必须知道你在做什么。我们远离了Java的老路——毫无疑问，我们正在做一些不同寻常的事情。

虽然Groovy中缺乏隐私的问题仍然偶尔困扰着我，但在实践中这并不是什么大问题。私有方法不会出现在公共接口中，所以通常我知道私有方法存在的唯一方法是打开源代码。如果我有那么多机会去上课，我就有责任不把事情搞砸。按照同样的思路，在为单元测试准备类时，访问私有方法和字段实际上非常有用，特别是如果编写的类不是很容易测试的话。

Bjarne Stroustrup曾有句著名的话：“ C可以很容易地将自己拍到脚上； C ++使它变得更难，但是当您这样做时，它会使您全力以赴。”有些人可能会说，在`private methods`的案例中，Groovy让炸掉整条腿变得更容易了。我个人对这个问题的看法更务实一些:我宁愿要一把更锋利的手术刀和一个训练有素的外科医生，也不愿要一把更钝的刀片。开发人员有责任明智地使用这个特性。
:::

使用纯Java实现中必须使用的一小部分代码就可以获得相同的行为。这段代码唯一需要做的事情就是编译Groovy类(我们将在下一页的Groovy联合编译器4.11节中讨论)，并且在`$GROOVY_HOME/embeddable`中找到的单个Groovy JAR位于类路径的某个地方。

### 4.9 从Groovy调用Java {#4_9_Calling_Java_from_Groovy}
```groovy
class BookstoreGroovy implements Bookstore{
  Book b // written in Groovy
  Publisher p // written in Java

  Book makeBook(){
    b = new Book(author:"Scott Davis", pages:250, title:"Groovy Recipes")
  }

  Publisher makePublisher(){
    p = new Publisher(name:"Pragmatic Bookshelf")
  }
}
```
在第79页的第4.8节“从Java调用Groovy”中，我们看到Groovy类在从Java运行时看起来就像Java类。在这个例子中，您可以看到Java类在从Groovy运行时看起来就像Groovy类。即使Publisher是用Java编写的，您仍然可以使用Groovy中提供的很酷的构造函数快捷方式(第4.5节，构造函数快捷语法，见第76页)。

### 4.10 Groovy和Java中的接口 {#4_10_Interfaces_in_Groovy_and_Java}
```groovy
// Bookstore.java
public interface Bookstore {
  public Book makeBook();
  public Publisher makePublisher();
}

// BookstoreGroovy.groovy
class BookstoreGroovy implements Bookstore{...}

// BookstoreJava.java
public class BookstoreJava implements Bookstore {...}
```
您在这里看到的是Groovy与Java无缝集成的另一个例子。Bookstore接口是用Java编写的。如前所述，Book是用Groovy编写的，Publisher是用Java编写的。这个接口可以很好地处理这两个类。

现在看看BookstoreGroovy。它是用Groovy编写的，但是它能够像BookstoreJava一样轻松地实现Bookstore(用Java编写)。

这段代码唯一需要做的事情就是编译您的Groovy类(我们将在第4.11节，Groovy联合编译器中对此进行讨论)，并且在`$GROOVY_HOME/embeddable`中找到的单个Groovy JAR位于类路径的某个地方。

### 4.11 Groovy联合编译器 {#4_11_The_Groovy_Joint_Compiler}
```bash
// compile Groovy code
$ groovyc *.groovy

// compile Java code
$ javac *.java

// compile both Groovy and Java code
// using groovyc for the Groovy code and javac for the Java code
$ groovyc * -j -Jclasspath=$GROOVY_HOME/embeddable/groovy-all-1.5.0.jar:.
```
毫不奇怪，groovyc将Groovy源代码编译成字节码，就像javac编译Java源代码一样。然而，Groovy编译器增加了一个更微妙但非常有用的特性:使用单个命令联合编译Java和Groovy代码的能力。

**满足依赖关系**
为了理解groovyc的功能，让我们更深入地研究一下javac的生命周期。在javac编译您的代码之前，它必须满足所有的依赖项。例如，让我们尝试编译Bookstore接口:
```bash
$ javac Bookstore.java
```

```java
// Bookstore.java
public interface Bookstore {
  public Book makeBook();
  public Publisher makePublisher();
}
```

javac要做的第一件事就是查找Book 和Publisher。没有它们，Bookstore就不可能被编辑。因此，javac在类路径中搜索"Book.class"和"Publisher.class"。它们可能存储在JAR中，也可能只是随意放置，但是如果javac能够在已经编译的状态中找到它们，那么它就可以继续编译Bookstore。

如果javac找不到"Book.class"或"Publisher.class"，然后查找"Book.java"和"Publisher.java"。如果能够找到源代码，它将代表您编译它们，然后继续编译Bookstore。明白了吗?

好的，那么Groovy代码是如何在这个过程中产生干扰的呢?不幸的是，javac只知道如何编译Java代码。有几种可插入的编译器可用来管理许多不同类型的源代码——GNU GCC编译器[^411]就是一个很好的例子。遗憾的是，javac不是其中之一。如果它找不到Book.class or Book.java，它放弃了。在我们的例子中，如果Book是用Groovy编写的，javac会这样说:
```bash
$ javac Bookstore.java
Bookstore.java:2: cannot find symbol
symbol : class Book
location: interface Bookstore
public Book makeBook();
^
1 error
```

在这个简单的示例中，变通办法是“嘿，医生，这样做时会很痛”。 由于javac不会为您编译Groovy代码，因此请尝试先编译"Book.groovy"，然后再编译"Bookstore.java"：
```bash
$ groovyc Book.groovy
$ javac Bookstore.java
$ ls -al

-rw-r--r-- 1 sdavis sdavis 5052 Dec 10 17:03 Book.class
-rw-r--r--@ 1 sdavis sdavis 60 Dec 10 16:57 Book.groovy
-rw-r--r-- 1 sdavis sdavis 169 Dec 10 17:03 Bookstore.class
-rw-r--r--@ 1 sdavis sdavis 93 Dec 10 16:56 Bookstore.java
-rw-r--r-- 1 sdavis sdavis 228 Dec 10 17:03 Publisher.class
-rw-r--r--@ 1 sdavis sdavis 48 Dec 10 16:58 Publisher.java
```
世界一切都很好，对吧？ 您将"Book.groovy"编译为字节码，这使javac可以在没有抱怨的情况下编译"Bookstore.java"。 （请注意，Publisher.java与Bookstore.java是免费编译的。）

尽管对于简单的项目，手动管理Groovy/Java依赖关系链是可行的，但是如果您拥有依赖于"依赖Groovy类的Java类的"Groovy类，它很快就会成为噩梦。

**一个命令，两个编译器**
```bash
$ groovyc * -j -Jclasspath=$GROOVY_HOME/embeddable/groovy-all-1.5.0.jar:.
```

由于无法帮助javac编译Groovy，因此您可以使用groovyc来实现此功能。 但是请不要误解，groovyc不会编译Java代码。 通过将`-j`标志传递给编译器，它向编译器发出信号，使其将Javac用于Java代码，并将groovyc用于Groovy代码。 使用每种语言的本机编译器，您将获得两种语言之间的依赖关系解析的所有好处。

小写的-j标志打开联合编译。 您可以包括多个大写的-J标志，以将标准标志传递给javac编译器。 此示例确保javac可以通过传入classpath参数来找到Groovy JAR。 如果未设置CLASSPATH环境变量，则必须使用classpath标志。 如果您在类路径中没有Groovy JAR，则Java代码将无法针对Groovy类进行编译。

在此示例中，您告诉javac生成与Java 1.4兼容的类：
```bash
$ groovyc * -j -Jclasspath=$GROOVY_HOME/embeddable/groovy-all-1.5.0.jar:. -Jsource=1.4 -Jtarget=1.4
```

### 4.12 用Ant编译项目 {#4_12_Compiling_Your_Project_with_Ant}
```xml
<taskdef name="groovyc"
         classname="org.codehaus.groovy.ant.Groovyc"
         classpathref="my.classpath"/>
<groovyc
      srcdir="${src}"
      destdir="${dest}"
      classpathref="my.classpath"
      jointCompilationOptions="-j -Jsource=1.4 -Jtarget=1.4" />
```
很高兴知道您可以从命令行编译Groovy代码（Groovy Joint Compiler，第4.11节，第82页），但是大多数项目都使用Ant。 幸运的是，Groovy在这种情况下提供了Ant任务。

为了避免taskdef步骤，将Groovy JAR从 `$GROOVY_HOME/embeddable` 放到 `$ANT_HOME/lib` 目录中。

### 4.13 使用Maven编译项目 {#4_13_Compiling_Your_Project_with_Maven}
```js
http://mojo.codehaus.org/groovy
```
虽然Groovy不提供Maven 2.0开箱即用的支持，但是Mojo项目提供了这种支持。有一个Maven插件允许您联合编译Groovy代码(详细信息请参阅第82页的第4.11节，Groovy联合编译器)。还有一个Maven原型插件，它为您的Groovy项目生成一个框架。

[^31]: http://en.wikipedia.org/wiki/Indent_style
[^32]: http://en.wikipedia.org/wiki/Editor_war
[^33]: http://en.wikipedia.org/wiki/I_know_it_when_I_see_it
[^35]: http://en.wikipedia.org/wiki/Duck_typing
[^37]: http://groovy.codehaus.org/groovy-jdk/
[^312]: http://en.wikipedia.org/wiki/Heredoc
[^317]: http://en.wikipedia.org/wiki/Closure_%28computer_science%29
[^318]: http://en.wikipedia.org/wiki/Functional_programming
[^411]: http://gcc.gnu.org/

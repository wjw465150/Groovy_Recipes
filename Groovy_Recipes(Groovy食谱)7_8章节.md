## Parsing XML {#解析XML}
Groovy使得使用XML变得轻而易举。 当然，您仍然可以在工具包中使用经过验证的Java XML库，但是一旦您体验了Groovy的原生`parsers `和`slurpers`，您就会想知道为什么要使用其他任何东西。 Groovy最大限度地减少了XML和代码之间的鸿沟，使XML感觉像是语言的自然扩展。 有关如何使用新发现的XML解析技能的一些实际示例，请参阅第9章，Web服务，第152页。

### 7.1 The “I'm in a Hurry” Guide to Parsing XML {#在“我有急事”指南解析XML}
```groovy
def p = """<person id="99">John Smith</person>"""
def person = new XmlSlurper().parseText(p)

println person
===> John Smith
println person.@id
===> 99
```
在Groovy中处理XML的最快方法是使用`XmlSlurper`来填充它。 如此示例所示，您只需按名称查询元素即可获得元素的文本。 要获取属性值，请使用`@`和属性名称来请求它。

请注意，在这个例子中，Groovy heredocs使用XML有多好？ 您不必担心多行或转义内部引号。 一切都存储在pString变量中。 每当我处理XML，HTML，JSON或任何其他可能嵌入引号的格式时，我只需将它们包含在三引号中。 有关更多信息，请参见第56页上的第3.12节"Heredocs（三重引号）"。

```groovy
def p2 = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
</person>"""

def person = new XmlSlurper().parseText(p2)
println person.firstname
===> Jane
println person.address.city
===> Denver
println person.address.@type
===> home
```

XmlSlurper允许您通过简单地按名称请求节点来导航任意深度的XML结构。 例如，person.address.city对应于`<person><address><city>`。

`Groovy/XML`关系有许多微妙的细微差别。 我们将介绍第二个解析器--`XmlParser`.它将在下一节中补充`XmlSlurpe`r。 根据您的观点，它们可能是令人困惑的相似或令人发狂的不同。 我们将花费本章的其余部分来比较和对比它们。 但是，如果您需要做的就是解析一些简单的XML而不想过多考虑它，那么您可以使用`XmlSlurper`并继续您的生活。

### 7.2 Understanding the Difference Between XmlParser and XmlSlurper

```groovy
def p = """<person id="99">John Smith</person>"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.getClass()
===> class groovy.util.Node

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person.getClass()
===> class groovy.util.slurpersupport.NodeChild
```

::: alert-dark
**不同或相同？**

了解XmlParser和XmlSlurper之间的差异可能是一件棘手的事情。 有时差异是明显的 - 这个调用仅适用于该类。 其他时候，差异可能非常微妙。 当然，很多时候因为快乐的巧合，这两个class以同样的方式运作。

为了帮助澄清事情，当我在同一个例子中向您展示XmlParser和XmlSlurper时，我会将代码标记为（***different ***）或（***same***）。 通常我试图提出一个或另一个：“嘿，看看这两个是多么相似！”或“这是两者之间的重要区别。
:::

Groovy提供了两个原生XML解析器：`groovy.util.XmlParser`和`groovy.util.XmlSlurper`。 他们的API几乎完全相同，这是一个永无止境的混乱源。 （“有什么区别？”“我应该使用哪一个？”“为什么在地球上我会有两个同样做同事的课程？”）当然，答案是，他们并没有做同样的事情。 它们都是XML解析库，但每种都采用略有不同的方法解决问题。

`XmlParser`根据节点来考虑文档。 当您开始处理更复杂的XML文档时，XmlParser将在您导航树时返回节点列表。

另一方面，XmlSlurper将文档视为`groovy.util.slurpersupport.GPathResult`。 （由于GPathResult是一个抽象类，你可以看到`groovy.util.slurpersupport.NodeChild`显示为实现。）GPath就像XPath[^71]，只有一个“groovier”语法。 XPath使用斜杠表示法来深入嵌套嵌套的XML树 -  GPath使用点来做同样的事情。

我们将在本章中深入探讨这些想法。 但是现在，将`XmlParser`视为处理XML文档节点的一种方法。 可以将`XmlSlurper`视为一种根据查询结果处理数据本身的方法。
```xml
<person id="99">John Smith</person>
```
如果您查看此XML片段并查看其值为`John Smith`的人，那么您就像是一个`XmlSlurper`。 相反，如果您看到一个根节点，其`text()`方法应返回`String John Smith`，那么您肯定更多地处于`XmlParser`的思维模式中。 有关两个解析器的不同世界观重要性的一个非常好的示例，请参见第127页的第7.8节“深入嵌套的XML导航”。

您可能会想，“为什么不直接将XML编组到GroovyBean中？ 然后你可以在对象上调用getter和setter。“ 如果是这种情况，请直接跳到第7.10节，或者查看JAXB[^72]或Castor[^73]等项目。我同意如果您使用XML作为序列化或持久性格式， 获取数据的bean表示是你应该尽快做的事情。 但本章的主要重点是以可以通过编程方式使用XML的方式将XML转换为Groovy。 有很多XML文件，例如`server.xml`，`web.xml`和`struts-config.xml`，它们可能足以将它们作为临时XML Groovy对象处理并留在那里。

**了解XmlParser**
```groovy
def p = """<person id="100">Jane Doe</person>"""
def person = new XmlParser().parseText(p)

println person.text()
===> Jane Doe
println person.attribute("id")
===> 100
println person.attribute("foo")
===> null
```

`XmlParser.parseText()`返回一个`groovy.util.Node`。 Node是一个很好的类，用于保存XML元素之类的东西。 有一个`text()`方法返回节点的主体。 有一个`attribute()`方法接受一个名称并返回给定的属性。 如果要求不存在的属性，`attribute()`将返回`null`。 很简单，对吧？

需要注意的重要一点是，您正在对对象进行方法调用。 没有直接处理XML的错觉。 您调用`text()`方法返回文本。 您调用`attribute()`方法以返回该属性。

如果您更喜欢使用JDOM等Java库来以编程方式使用XML，那么XmlParser会让您感觉宾至如归。

您还应该注意，我将节点人命名为与XML文档中的元素名称匹配。 这只是一个有助于模糊的惯例
XML和Groovy代码之间的区别。 从技术上讲，你可以很容易地命名节点foo并调用`foo.text()`来返回"Jane Doe"。 XML不是Groovy（或Java中的本机数据类型），但巧妙地命名变量有助于最大限度地减少认知断开。

**了解XmlSlurper**
```groovy
def p = """<person id="100">Jane Doe</person>"""
def person = new XmlSlurper().parseText(p)

println person
===> Jane Doe
println person.@id
===> 100
println person.@foo
===> (returns an empty string)
```

`XmlSlurper.parseText()`返回`groovy.util.slurpersupport.GPathResult`。 从技术上讲，这是一个特殊的类，但是现在我想让你把它想象成一个`GPath`查询的String结果。 在此示例中，要求person返回查询的结果 - 该元素的文本（或正文）。 如果您熟悉`XPath`，就会知道`@`用于查询属性。 查询`person.@id`返回100。

XmlSlurper是一个空安全的XML解析器。 要求`person.@foo`（一个不存在的属性）返回一个空字符串。 要求`person.bar`（一个不存在的节点）也会返回一个空字符串。 这样可以避免使用`try / catch`块不必要地破坏你的代码，以保护你免受可怕的未经检查的**NullPointerException**。 在这两种情况下，`XmlParser`都会抛出空值。

这里需要注意的重要一点是，感觉我们正在直接处理XML。 没有明显的方法调用（尽管这只是Groovy开发人员正在玩的元编程主要技巧）。 如果你不太认真并且破坏幻觉，你会更快乐。 保持`XmlSlurper`与`XmlParser`不同的最好方法是将后者视为处理API而将前者视为直接处理XML。 相信我。

什么，你不相信我？ 你仍然想知道当firstname和lastname不是API的编译部分时，XmlParser如何处理像`person.firstname`和`person.lastname`这样的调用？ 有关更多信息，请参见第193页上的第10.8节“调用不存在的方法（invokeMethod）”。

### 7.3 Parsing XML Documents {#解析XML文档}
```groovy
def file = new File("person.xml")
def url = "http://somewhere.org/person.xml"

// XmlParser (*** same ***)
def person = new XmlParser().parse(file)
def person2 = new XmlParser().parse(url)

// XmlSlurper (*** same ***)
person = new XmlSlurper().parse(file)
person2 = new XmlSlurper().parse(url)
```

XmlParser和XmlSlurper共享相同的parse()方法。 您可以传递parse()文件或表示URL的字符串 - 在幕后为您处理所有传输机制。 有关接受InputSource，InputStream和Reader的重载parse()方法的更多示例，请参阅 `http://groovy.codehaus.org/api/`上的API文档。

**解析XML字符串**
```groovy
def p = """<person id="99">John Smith</person>"""

// XmlParser (*** same ***)
def person = new XmlParser().parseText(p)

// XmlSlurper (*** same ***)
person = new XmlSlurper().parseText(p)
```

由于接受String的重载parse()方法将其视为URL，因此如果已经将XML存储在String变量中，则可以使用单独的parseText()方法。 我们将在本节的大多数示例中使用parseText()，因为为了清晰和`复制/粘贴`友好性，XML与其余代码内联。

### 7.4 Dealing with XML Attributes {#处理XML属性}

```groovy
def p = """<person id="99" ssn="555-11-2222">John Smith</person>"""

// XmlParser (*** same ***)
def person = new XmlParser().parseText(p)
println person.attributes()
===> ["ssn":"555-11-2222", "id":"99"]

person.attributes().each{name, value->
  println "${name} ${value}"
}
===>
ssn 555-11-2222
id 99

// XmlSlurper (*** same ***)
person = new XmlSlurper().parseText(p)
println person.attributes()
===> ["ssn":"555-11-2222", "id":"99"]

person.attributes().each{name, value->
  println "${name} ${value}"
}
===>
ssn 555-11-2222
id 99
```

属性是Java等效的Java哈希映射 - 它们是XML元素上的一系列`名称/值`对。 Node和GPathResult都有一个相同的attributes()方法，它返回一个hashmap。 有关使用散列映射可以执行的所有技巧，请参见第62页上的第3.15节“映射快捷方式”。

**获得单个属性**
```groovy
def p = """<person id="99" ssn="555-11-2222">John Smith</person>"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.attribute("id")
===> 99
println person.attribute("foo")
===> null

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person.@id
===> 99
println person.@foo
===> (returns an empty string)
```
使用XmlParser时，使用attribute()方法提取单个属性。 使用XmlSlurper时，直接在属性名称上使用`@`表示法。

**使用属性的Hashmap语法**
```groovy
def p = """<person id="99" ssn="555-11-2222">John Smith</person>"""

// XmlParser (*** same ***)
def person = new XmlParser().parseText(p)
println person["@id"]
===> 99

def atts = ["id", "ssn"]
atts.each{att->
println person["@${att}"]
}
===>
99
555-11-2222

// XmlSlurper (*** same ***)
person = new XmlSlurper().parseText(p)
println person["@id"]
===> 99

atts.each{att->
println person["@${att}"]
}
===>
99
555-11-2222
```
XmlParser和XmlSlurper都支持相同的属性替代语法。 使用hashmap表示法（person["@id"]）是一种理想的方法，既可以模糊这两个库之间的区别，也可以在你试图区分它们时彻底迷惑自己。

我发现这种备用hashmap语法的最佳用途是当我需要根据泛型变量提取属性时。 知道其他类支持相同的语法 - `println person["@${att}"]` - 意思是我不必过于考虑这个问题。 我只是使用在两种情况下都有效的语法。

当然，在XmlParser的情况下，您可以轻松地使用`person.attribute("${att}")`。 在XmlSlurper的情况下，你可以使用`person."@${att}"`。

### 7.5 Getting the Body of an XML Element {#获取XML元素的主体}
```groovy
def p = """<person id="100">Jane Doe</person>"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.text()
===> Jane Doe

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person
===> Jane Doe
```

从XML元素中获取文本需要与XmlParser和XmlSlurper略有不同的语法。 回想一下第7.2节“了解XmlParser和XmlSlurper之间的差异”，第117页，每个都有一个略微不同的世界观。 XmlSlurper将所有内容视为大型`GPath查询`。 要求person等元素默认要求其文本。 另一方面，XmlParser将所有内容视为`节点`。 您必须在节点上调用text()。 如果不这样做，则调用toString()，它返回调试输出：
```groovy
def p = """<person id="100">Jane Doe</person>"""
def person = new XmlParser().parseText(p)

println person
===> person[attributes={id=100}; value=[Jane Doe]]
```

**使用元素的Hashmap语法**
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
</person>"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person['firstname'].text()
===> Jane

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person['firstname']
===> Jane
```
两个解析器都允许您将每个子XML节点视为其父节点的Map元素。 调用`person.firstname.text()`或`person['firstname'].text()`（在XmlParser的情况下）对你来说纯粹是一种风格选择，尽管如果你有一个要处理的元素名列表，Map语法有时更容易使用：

```groovy
def xml = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
</person>
"""

def person = new XmlParser().parseText(xml)
def elements = ["firstname", "lastname"]
elements.each{element->
  println person[element].text()
}
===>
Jane
Doe
```

### 7.6 Dealing with Mixed-Case Element Names {#处理混合大小写元素名称}

```groovy
// 注意firstname和LastName中的大小写差异
// Groovy代码镜像XML元素名称的大小写
def p = """
<person id="99">
  <firstname>John</firstname>
  <LastName>Smith</LastName>
</person>
"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.firstname.text()
===> John
println person.LastName.text()
===> Smith

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person.firstname
===> John
println person.LastName
===> Smith
```

无论是XML解析器关心XML元素的名称是否为小写，大写或混合大小写。 您在Groovy中引用它们的方式都要与它们在XML文件中显示的方式相同。

### 7.7 Dealing with Hyphenated Element Names {#处理连字符元素名称}
```groovy
//注意带连字符和下划线的元素名称
//Groovy必须使用特殊语法来处理连字符
def p = """
<person id="99">
  <first-name>John</first-name>
  <last_name>Smith</last_name>
</person>
"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.first-name.text()
===>
Caught: groovy.lang.MissingPropertyException:
No such property: name for class: person

println person.'first-name'.text()
println person['first-name'].text()
===>
John

println person.last_name.text()
println person.'last_name'.text()
println person['last_name'].text()
===>
Smith

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person.'first-name'
println person['first-name']
===>
John

println person.last_name
println person.'last_name'
println person['last_name']
===>
Smith
```
两种XML解析器都尽力模糊XML和Groovy之间的区别，尽可能地镜像节点名称。 不幸的是，在某些边缘情况下，当命名规则不匹配100％时，这个外观会崩溃。 （这被称为漏洞抽象[^77]。）

尽管带连字符的名称在XML中完全有效，但Groovy中的`person.first-name`意味着“获取变量名称的值并从person.first中减去它。”用带引号括起带连字符的名称会将语句转换回有效的Groovy构造。

但请注意，带有下划线的名称可以按原样使用。 下划线在Groovy和XML中都有效，因此您可以在Groovy中保留引号。 人们往前走，没有什么可看的。 这里没有泄漏的抽象。

### 7.8 Navigating Deeply Nested XML {#深入导航嵌套XML}
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
</person>"""

// XmlParser (*** different ***)
def person = new XmlParser().parseText(p)
println person.address[0].street[0].text()
===> 123 Main St

// XmlSlurper (*** different ***)
person = new XmlSlurper().parseText(p)
println person.address.street
===> 123 Main St
```

从本章开始，我一直试图告诉你这两个库有多么不同。 现在，您第一次真正看到了两种不同的世界观。

XmlParser将XML文档视为节点的ArrayList。 这意味着您必须在树中一直使用数组表示法。 XmlSlurper将XML文档视为一个等待发生的大型GPath查询。 让我们更详细地探讨每一个。

**XmlParser: text(), children(), and value()**
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
</person>"""

def person = new XmlParser().parseText(p)
println person.text()
===> (returns an empty string)

println person.children()
===>
[
  firstname[attributes={}; value=[Jane]],
  lastname[attributes={}; value=[Doe]],
  address[attributes={type=home}; value=[
    street[attributes={}; value=[123 Main St]],
    city[attributes={}; value=[Denver]],
    state[attributes={}; value=[CO]],
    zip[attributes={}; value=[80020]]
  ]]
]

println person.value()
// 返回text() 或 value()的泛型函数，
// 取决于填充的字段。
// 在这种情况下，person.value()等同于children()。
```

我们已经讨论过text()方法了。 现在是时候介绍你经常使用的其他Node方法：children()。 虽然text()返回一个String，但children()返回一个节点的ArrayList。 如果你想想看，一个XML文档中的节点只能有一个或其他。 Person 有 children; firstname 有 text. Address 有 children; city 是 text.

理解Node的双重性质 - 加上一些Groovy真理（如第3.10节，Groovy Truth，第54页所述） - 使得确定节点是叶子还是分支变得微不足道。 这允许您非常简单地递归任意深度的文档。

```groovy
if(person.text()){
  println "Leaf"
} else{
  println "Branch"
}
===> Branch

if(person.children()){
  println "Branch"
} else{
  println "Leaf"
}
===> Branch
```

你应该熟悉的Node上的最后一个方法是value()。 此方法返回text()或children()，具体取决于填充的内容。

**XmlParser: each()**
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
  <address type="work">
    <street>987 Other Ave</street>
    <city>Boulder</city>
    <state>CO</state>
    <zip>80090</zip>
  </address>
</person>"""

def person = new XmlParser().parseText(p)

println person.address[0].attribute("type")
===> home

println person.address[1].attribute("type")
===> work

person.address.each{a->
  println a.attribute("type")
}
===>
home
work
```

由于children()返回节点的ArrayList，因此您可以使用第53页上的第3.14节“列表快捷方式”中学到的所有技巧来处理它们。 您可以使用数组表示法来获取您感兴趣的特定地址，或者您可以使用each()来遍历列表。

当使用XmlParser在树中导航时，语法会在每个循环中提醒您，每个子节点可能是许多子节点中的一个。 在下面的示例中，我们遍历文档中的每个地址，并询问找到的第一个城市。 在本例中，这有点令人失望——从逻辑上讲，一个地址拥有多个城市是没有意义的，但是没有XML规则可以阻止这种情况的发生。因此，您必须明确地捕捉它:

```groovy
person.address.each{a->
  println a.city[0].text()
}
===>
Denver
Boulder
```

从积极的方面看，XmlParser使对XML进行垂直切片变得非常简单。如果你只是想让每个城市的所有地址，这段代码使它的工作很短:
```groovy
person.address.city.each{c->
  println c.text()
}
===>
Denver
Boulder
```
我希望本节能够非常清楚地说明，XmlParser认为您的XML文档只不过是节点和节点列表。

**XmlSlurper**
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
</person>"""

def person = new XmlSlurper().parseText(p)
println person.firstname
===> Jane

println person.lastname
===> Doe

println person.address.city
===> Denver
```

XmlParser将所有内容视为**节点**或**节点列表**，而XmlSlurper将所有内容视为**GPath查询的结果**。 这使得导航路径更加自然。当你问`person.address.city`时。您是在隐式地请求该元素中的文本。 换句话说，XmlParser对分支有很强的亲和力。XmlSlurper正好相反:它针对叶子进行了优化。

当然，如果你不够具体，有时候你的查询结果可能看起来毫无意义:
```groovy
println person
===> JaneDoe123 Main StDenverCO80020
println person.address
===> 123 Main StDenverCO80020
```

在每一种情况下，您都需要一个分支而不是一片叶子。确保你总是要求一个特定的叶子将有助于确保你得到你想要的结果。在下面的例子中，为了得到一个合理的回答，你必须询问一个特定地址的城市:
```groovy
def p = """
<person id="100">
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home">
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
  <address type="work">
    <street>987 Other Ave</street>
    <city>Boulder</city>
    <state>CO</state>
    <zip>80090</zip>
  </address>
</person>"""

def person = new XmlSlurper().parseText(p)
println person.address.city
===>DenverBoulder
println person.address[0].city
===>Denver
```
另一方面，如果你真的想要所有城市的垂直部分，你可以像浏览其他列表一样浏览每一个城市:

```groovy
person.address.city.each{println it}
===>
Denver
Boulder
```

### 7.9 Parsing an XML Document with Namespaces {#使用名称空间解析XML文档 }
```groovy
def p_xml = """
<p:person
  xmlns:p="http://somewhere.org/person"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://somewhere.org/person
                      http://somewhere.org/person.xsd"
  id="99">
  <p:firstname>John</p:firstname>
  <p:last-name>Smith</p:last-name>
</p:person>
"""

def person = new XmlParser().parseText(p_xml)

//如果没有名称空间，就无法找到firstname元素
println person.firstname.text()
===> []

def p = new groovy.xml.Namespace("http://somewhere.org/person")
println person[p.firstname].text()
===> John

println person[p.'last-name'].text()
===> Smith
```

当人们抱怨XML时，名称空间通常排在首位。“这使事情复杂化了，”他们低声咕哝着。当然，名称空间的好处是可以生成表示复杂域的XML文档。考虑一个在不同上下文中使用了name元素的文档:
```xml
<product:name>iPhone</product:name>
<vendor:name>Apple</vendor:name>
```

名称元素命名空间的另一种替代方法是使它们在缺省名称空间中惟一，但是如果要合并来自不同来源的XML，这可能是不可能的。
```xml
<product-name>iPhone</product-name>
<vendor-name>Apple</vendor-name>
```

幸运的是，Groovy使处理名称空间变得尽可能不引人注目。您只需声明名称空间，然后在所有元素引用前面加上名称空间变量:
```groovy
def p = new groovy.xml.Namespace("http://somewhere.org/person")
println person[p.firstname].text()
===> John
```

因为点运算符是用来遍历树的，所以请求`person.p.firstname`会有歧义。在处理带名称空间的元素时，只能使用HashMap表示法，如第124页第7.5节中讨论的，即使用元素的HashMap语法:`person[p.firstname].text()`。如果您有连字符的元素也有名称空间, 只需引用元素名称:`person[p. 'last-name'].text()`。

**名称空间 在 XmlSlurper**
```groovy
def p = """
<p:person
  xmlns:p="http://somewhere.org/person"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://somewhere.org/person
                      http://somewhere.org/person.xsd"
  id="99">
  <p:firstname>John</p:firstname>
  <p:last-name>Smith</p:last-name>
</p:person>
"""

def person = new XmlSlurper().parseText(p)
println person.firstname
println person.'last-name'
===>
John
Smith
```

在XML名称空间方面，XmlSlurper不同于XmlParser。 默认情况下，XmlSlurper忽略所有名称空间， 而XmlParser关注它们。这使得以一种松散(如果不是完全有效)的方式撕裂XML文档变得很容易。 如果您告诉XmlSlurper名称空间，它将尊重这些名称空间。GPathResult类有一个`declareNamespace()`方法，它接受名称空间的映射。

```groovy
def itemXml = """
<item
  xmlns:product="urn:somecompany:products"
  xmlns:vendor="urn:somecompany:vendors" >
  <product:name>iPhone</product:name>
  <vendor:name>Apple</vendor:name>
  <quantity>1</quantity>
</item>
"""

def item = new XmlSlurper().parseText(itemXml)
println item.name
===> iPhoneApple

def ns = [:]
ns.product = "urn:somecompany:products"
ns.vendor = "urn:somecompany:vendors"
item.declareNamespace(ns)

println item.'product:name'
===> iPhone
```

如果没有声明名称空间，调用name元素将返回两个名称。一旦GPathResult知道名称空间，它将允许您调用适当的限定元素。

您是否注意到XmlParser让您在名称空间和元素名称之间使用一个点?XmlSlurper再次接近于匹配原始XML语法。`item.'product:name'`对应于`<item><product:name>`使用相同的符号`:(冒号)`。 不幸的是，冒号不是变量名中的合法字符。在XmlSlurper中，需要用引号括住名称空间元素名。

### 7.10 Populating a GroovyBean from XML {#从XML填充GroovyBean}
```groovy
def p = """
<person>
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
</person>
"""

class Person{
  String firstname
  String lastname
}

def pxml = new XmlParser().parseText(p)
def person = new Person()

pxml.children().each{child ->
  person.setProperty(child.name(), child.text())
}
```

尽管这个解决方案没有提供真正的XML到java封送解决方案(比如Castor[^710])的丰富性，但是您应该知道，您可以很容易地从XML构造一个有效的GroovyBean。 `pxml.children()`返回节点列表。每个节点都有一个name()方法和一个text()方法。在GroovyBean上使用原生的setProperty方法可以快速地从XML构造一个有效的类。

如果您知道嵌套的XML结构更深入，则应该递归调用`children()`。 如果您有属性，则可以在每个节点上调用`attributes()`以返回Map。 （有关XML文档结构的动态内省的更多提示，请参见第78页上的第7.8节，XmlParser:text(), children()和value()，这里的重点是不要为每个文档提供完整的解决方案。 可能的情况 - 重点是展示使用日常Groovy类处理XML的可能性。

## Writing XML {#编写XML}
在第7章，XML解析，第116页，我们探讨了摄取XML的不同方法。(既然您已经完全了解了XmlSlurper，那么“slurp”听起来是不是比“ingest”酷多了?)在本章中，我们将研究编写XML的不同方法。

与Groovy解析器一样，您有两个用于构建XML文档的类似(但略有不同)类——markupbuilder和StreamingMarkupBuilder。在本章结束之前，您应该更清楚地了解每种方法的优缺点。

### 8.1 The “I'm in a Hurry” Guide to Creating an XML Document {#{#在“我有急事”指南编写XML}}

```groovy
def xml = new groovy.xml.MarkupBuilder()
xml.person(id:99){
  firstname("John")
  lastname("Smith")
}
===>
<person id='99'>
<firstname>John</firstname>
<lastname>Smith</lastname>
</person>
```

就像魔术一样，XML文档似乎很容易从Groovy中消失。这是因为groovy.xml.MarkupBuilder具有动态特性。像person、firstname和lastname这样的方法看起来像是MarkupBuilder原生的， 尽管半秒钟的思考就能让我们相信MarkupBuilder根本不可能将整个单词字典作为方法来实现这一点。相反，我们必须把功劳归于我们支持动态的朋友`invokeMethod()`，如第10.8节中讨论的，调用不存在的方法(invokeMethod)，在第193页。

当您在MarkupBuilder上调用不存在的方法时，`invokeMethod()`会捕获这些调用并将它们解释为XML文档的节点。作为不存在方法的参数传入的`name:value`对被解释为属性。(Groovy支持命名参数和变长参数列表，如第76页第4.5节“构造函数快捷语法”中所述。)在没有名称前缀的情况下传入的值被解释为元素的主体。嵌套闭包对应于XML文档中的嵌套。

**捕获输出**
```groovy
def sw = new StringWriter()
def xml = new groovy.xml.MarkupBuilder(sw)

def fw = new FileWriter("/path/to/some/file.xml" )
def xml2 = new groovy.xml.MarkupBuilder(fw)
```

默认情况下，MarkupBuilder将输出回显到`System.out`。如果希望捕获输出，备用构造函数接受`Writer`。您可以传入`StringWriter`来捕获内存中的输出，也可以使用`FileWriter`将结果直接写入文件。

### 8.2 Creating Mixed-Case Element Names {#创建混合大小写元素名称}

```groovy
def xml = new groovy.xml.MarkupBuilder()
xml.PERSON(id:100){
  firstName("Jane")
  LastName("Doe")
}

===>
<PERSON id='100'>
<firstName>Jane</firstName>
<LastName>Doe</LastName>
</PERSON>
```

如第125页第7.6节中讨论的，处理混合大小写元素名称，Groovy代码的目的是尽可能匹配XML输出。尽管本例中的奇怪情况不遵循`Java/Groovy`编码约定(类以大写字母开头，变量以小写字母开头，常量全部大写)，但Groovy保留了这种情况，以便您的输出完全符合您的期望。

### 8.3 Creating Hyphenated Element Names {#创建带连字符的元素名称}
```groovy
def xml = new groovy.xml.MarkupBuilder()
xml.person(id:99){
  "first-name"("John")
  last_name("Smith")
}

===>
<person id='99'>
<first-name>John</first-name>
<last_name>Smith</last_name>
</person>
```

正如第7.7节中讨论的，在第126页处理带连字符的元素名称时，带连字符的元素名称在XML中完全有效，但在Groovy中无效。

要使用MarkupBuilder创建带连字符的XML元素名称，只需将元素名称括在引号中。因为下划线在Groovy中是有效的，所以MarkupBuilder会不加更改地传递它们。

如果你忘记在引号中加上连字符，你会得到一个例外:
```groovy
def xml = new groovy.xml.MarkupBuilder()
xml.person(id:99){
  first-name("John")
  last_name("Smith")
}

===>
Caught: groovy.lang.MissingPropertyException:
No such property: first for class: builder
```

### 8.4 Creating Namespaced XML Using MarkupBuilder {#使用MarkupBuilder创建名称空间XML}
```groovy
def xml = new groovy.xml.MarkupBuilder()
def params = [:]
params."xmlns:product" = "urn:somecompany:products"
params."xmlns:vendor" = "urn:somecompany:vendors"
params.id = 99
xml.person(params){
  "product:name"("iPhone")
  "vendor:name"("Apple")
  quantity(1)
}

===>
<person
xmlns:product='urn:somecompany:products'
xmlns:vendor='urn:somecompany:vendors'
id='99'>
<product:name>iPhone</product:name>
<vendor:name>Apple</vendor:name>
<quantity>1</quantity>
</person>
```

您可以使用MarkupBuilder轻松地创建具有名称空间的XML文档。根元素中的名称空间声明与任何其他属性没有什么不同。您的名称空间元素名称与用连字符连接的元素名称没有什么不同——您只需将它们括在引号中。

从技术上讲，MarkupBuilder并不理解名称空间，但这并不妨碍它轻松地吐出您要求它吐出的任何内容。在第8.7节中，使用StreamingMarkupBuilder创建带名称空间的XML，在第142页，您可以看到一个支持名称空间的构建器。

### 8.5 Understanding the Difference Between MarkupBuilder and StreamingMarkupBuilder {#理解MarkupBuilder和StreamingMarkupBuilder之间的区别}

```groovy
// MarkupBuilder
def xml = new groovy.xml.MarkupBuilder()
xml.person(id:100){
  firstname("Jane")
  lastname("Doe")
}

===>
<person id='100'>
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
</person>

// StreamingMarkupBuilder
def xml = new groovy.xml.StreamingMarkupBuilder().bind{
  person(id:100){
    firstname("Jane")
    lastname("Doe")
  }
}

println xml
===>
<person id='100'><firstname>Jane</firstname><lastname>Doe</lastname></person>
```

就像我们在第7.2节中讨论的兄弟XmlParser和XmlSlurper一样，Groovy在第117页中提供了两种方式来生成XML。

MarkupBuilder是这两种方法中比较简单的一种，但也比较有限。StreamingMarkupBuilder是一个类，当您的需求超出MarkupBuilder所能提供的范围时，就可以使用它。

**MarkupBuilder和StreamingMarkupBuilder之间有三个关键区别:**
::: alert-dark
*　MarkupBuilder默认将其输出发送到`System.out`; StreamingMarkupBuilder是静默的，直到您明确地将其移交给`Writer`。

*　MarkupBuilder是同步的;StreamingMarkupBuilder是异步的。换句话说，MarkupBuilder立即写出XML文档。StreamingMarkupBuilder允许您单独定义`closure(闭包)`。在将StreamingMarkupBuilder传递给`Writer`之前，不会生成文档。

*　最后，MarkupBuilder漂亮地打印它的输出，而StreamingMarkupBuilder不会。(为了可读性，本章中StreamingMarkupBuilder的所有后续XML输出都将进行漂亮的打印。)如果需要漂亮地打印结果，可以使用命令行工具Tidy[^851](大多数Unix/Linux/Mac系统上的标准工具，适用于Windows)或Java库JTidy[^852]。
:::

本章的其余部分将重点介绍StreamingMarkupBuilder及其为团队带来的高级功能。

### 8.6 Creating Parts of the XML Document Separately {#分别创建XML文档的各个部分}

```groovy
def builder = new groovy.xml.StreamingMarkupBuilder()
def person = {
  person(id:99){
  firstname("John")
  lastname("Smith")
  }
}
println builder.bind(person)
===>
<person id='99'><firstname>John</firstname><lastname>Smith</lastname></person>
```

StreamingMarkupBuilder允许您定义闭包并将其传递给`bind()`方法。这意味着您可以解耦这两者——独立创建闭包并在您想要创建XML文档的确切时刻将其绑定到StreamingMarkupBuilder。

如果您可以独立创建一个闭包，那么您可以独立创建多个闭包并根据需要将它们组合在一起，这是理所当然的:

```groovy
def builder = new groovy.xml.StreamingMarkupBuilder()
def person1 = {
  person(id:99){
    firstname("John")
    lastname("Smith")
  }
}

def person2 = {
  person(id:100){
    firstname("Jane")
    lastname("Doe")
  }
}

def personList = {
  "person-list"{
    out << person1
    out << person2
  }
}

println builder.bind(personList)
===>
<person-list>
  <person id='99'>
    <firstname>John</firstname><lastname>Smith</lastname>
  </person>
  <person id='100'>
    <firstname>Jane</firstname><lastname>Doe</lastname>
  </person>
</person-list>
```

在本例中，personList闭包包含对另外两个闭包的引用:person1和person2。StreamingMarkupBuilder提供了一个`out`目标，您应该将嵌入式闭包指向该目标。如果没有`out`, StreamingMarkupBuilder无法区分希望发出的元素(firstname)和需要取消引用的闭包之间的区别。

### 8.7 Creating Namespaced XML Using StreamingMarkupBuilder {#使用StreamingMarkupBuilder创建名称空间XML}

```groovy
def builder = new groovy.xml.StreamingMarkupBuilder().bind{
  mkp.declareNamespace('':'http://myDefaultNamespace' )
  mkp.declareNamespace('location':'http://someOtherNamespace' )
  person(id:100){
    firstname("Jane")
    lastname("Doe")
    location.address("123 Main St")
  }
}

println builder
===>
<person id='100'
        xmlns='http://myDefaultNamespace'
        xmlns:location='http://someOtherNamespace' >
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <location:address>123 Main St</location:address>
</person>
```

在第8.4节中，使用MarkupBuilder创建名称空间XML，在第138页，我们欺骗MarkupBuilder发出名称空间XML元素，尽管从技术上讲它并不支持名称空间。 另一方面，StreamingMarkupBuilder是名称空间感知的。将名称空间声明传递到保留的名称空间`mkp`。 任何以`mkp为`前缀的内容都被解释为构造器的内部指令，而不是应该发出的输出。 请注意，`location.address`是作为`location:address`发出的，而`mkp.declareNamespace`在输出中找不到。 您可以通过传入一个空字符串作为键来指定XML文档的默认命名空间。

### 8.8 Printing Out the XML Declaration {#打印XML声明}

```groovy
def builder = new groovy.xml.StreamingMarkupBuilder()
def person = {
  mkp.xmlDeclaration()
}
println builder.bind(person)
===>
<?xml version="1.0" encoding="MacRoman"?>

//setting the encoding
def builder2 = new groovy.xml.StreamingMarkupBuilder()
builder2.encoding = "UTF-8"
println builder2.bind{
  mkp.xmlDeclaration()
}
===>
<?xml version="1.0" encoding="UTF-8"?>
```
在保留的`mkp`名称空间上调用xmlDeclaration()时，将打印XML声明。 您可以直接在StreamingMarkupBuilder实例上设置编码，以覆盖默认的系统编码。

### 8.9 Printing Out Processing Instructions {#打印输出处理指令}

```groovy
def builder = new groovy.xml.StreamingMarkupBuilder()
def person = {
  mkp.pi("xml-stylesheet": "type='text/xsl' href='myfile.xslt'" )
}
println builder.bind(person)
===>
<?xml-stylesheet type='text/xsl' href='myfile.xslt'?>
```
当您在保留的`mkp`名称空间上调用`pi()`时，将打印处理指令，如用于XSLT的指令。

### 8.10 Printing Arbitrary Strings (Comments, CDATA) {#打印任意字符串（注释，CDATA）}

```groovy
def comment = "<!-- address is optional -->"
def builder = new groovy.xml.StreamingMarkupBuilder().bind{
  person(id:99){
    firstname("John")
    lastname("Smith")
    mkp.yieldUnescaped(comment)
    unescaped << comment
  }
}
println builder
===>
<person id='99'>
  <firstname>John</firstname>
  <lastname>Smith</lastname>
  <!-- address is optional -->
  <!-- address is optional -->
</person>
```
保留的名称空间`mkp`在过去几节中发挥了重要作用。调用`mks.declareNamespace()`允许您创建自己的名称空间。调用`mks.xmlDeclaration()`将输出一个XML声明。调用`mkp.pi()`输出处理指令。现在您看到了另一个方法调用`mks.yieldUnescaped()`。顾名思义，此方法将不更改地打印传入的字符串。`unescape <<`是一个执行相同操作的方便目标。这纯粹是一种风格的决定，你使用哪种形式。

如果希望StreamingMarkupBuilder为您转义字符串，可以调用`mks .yield()`或`out <<`。(还记得第8.6节吗，在第140页单独创建XML文档的各个部分?)

```groovy
def comment = "<!-- address is optional -->"
def builder = new groovy.xml.StreamingMarkupBuilder().bind{
  mkp.yieldUnescaped(comment)
  unescaped << comment
  mkp.yield(comment)
  out << comment
}
println builder
===>
<!-- address is optional -->
<!-- address is optional -->
&lt;!-- address is optional --&gt;
&lt;!-- address is optional --&gt;
```
`mkp.yield`和`out <<`的一个有趣特性是，默认情况下，它转义字符串，但不改变传递其他闭包。在开发过程中，如果我在字符串和闭包之间切换，就会不止一次地遇到这种情况。好消息是，`mkp.yieldUnescaped`和`unescape << `也没有改变传递闭包。换句话说，对于闭包，可以交替使用`out`和`unescape`。但是，如果希望在字符串和闭包之间进行多态切换，`unescape`可能比`out`更好。

**CDATA**
```groovy
def cdata = " >< & Look 'at' me & >< "
def builder = new groovy.xml.StreamingMarkupBuilder().bind{
  unescaped << "<![CDATA[" + cdata + "]]>"
}
println builder
===>
<![CDATA[ >< & Look 'at' me & >< ]]>
```
在XML中，CDATA[^810]部分提示解析器不要将文本视为标记。相反，它应该被解释为普通的旧字符数据。实际上，这意味着您可以传入通常需要转义的字符，例如`<, >, &`和`引号`(单引号和双引号)。


### 8.11 Writing StreamingMarkupBuilder Output to a File {#将StreamingMarkupBuilder输出写入文件}
```groovy
def writer = new FileWriter("person.xml")
writer << builder.bind(person)
```
您可以将StreamingMarkupBuilder的输出传递给实现`Writer`接口的任何Java类。

### 8.12 StreamingMarkupBuilder at a Glance {#StreamingMarkupBuilder概览}
```groovy
def comment = "<!-- address is new to this release -->"
def builder = new groovy.xml.StreamingMarkupBuilder()
builder.encoding = "UTF-8"
def person = {
  mkp.xmlDeclaration()
  mkp.pi("xml-stylesheet": "type='text/xsl' href='myfile.xslt'" )
  mkp.declareNamespace('': 'http://myDefaultNamespace' )
  mkp.declareNamespace('location': 'http://someOtherNamespace' )
  person(id:100){
    firstname("Jane")
    lastname("Doe")
    mkp.yieldUnescaped(comment)
    location.address("123 Main")
  }
}
def writer = new FileWriter("person.xml")
writer << builder.bind(person)

System.out << builder.bind(person)
===>
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type='text/xsl' href='myfile.xslt'?>
<person id='100'
        xmlns='http://myDefaultNamespace'
        xmlns:location='http://someOtherNamespace' >
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <!-- address is new to this release -->
  <location:address>123 Main</location:address>
</person>
```
将您在过去几节中学到的所有内容放在一起，可以为您构建各种复杂的XML文档提供所需的工具。 MarkupBuilder仍然可以用于简单的工作，但是当您需要执行复杂的工作时，可以使用StreamingMarkupBuilder。

### 8.13 Creating HTML on the Fly {#快速创建HTML}
```groovy
def x = new groovy.xml.MarkupBuilder()
x.html{
  head{
    title("Search Results")
    link(rel:"stylesheet", type:"text/css", href:"http://main.css")
    script(type:"text/javascript", src:"http://main.js")
  }
  body{
    h1("Search Results")
    div(id:"results", class:"simple"){
      table(border:1){
        tr{
          th("Name")
          th("Address")
        }
        tr{
          td{
            a(href:"http://abc.org?id=100" ,"Jane Doe")
          }
          td("123 Main St")
        }
      }
    }
  }
}

===>
<html>
  <head>
    <title>Search Results</title>
    <link rel='stylesheet' type='text/css' href='http://main.css' />
    <script type='text/javascript' src='http://main.js' />
  </head>
  <body>
    <h1>Search Results</h1>
    <div id='results' class='simple'>
      <table border='1'>
        <tr>
          <th>Name</th>
          <th>Address</th>
        </tr>
      <tr>
        <td>
          <a href='http://abc.org?id=100' >Jane Doe</a>
        </td>
        <td>123 Main St</td>
      </tr>
      </table>
    </div>
  </body>
</html>
```
MarkupBuilder同样擅长生成XML或HTML。在前面的示例中，我创建了一个快速HTML页面。

请记住，您并不是在Grails这样的完整web框架中。Grails在更高的抽象级别上运行，这使得它更容易生成HTML表之类的东西。 Groovy Server Pages (GSPs)是一个比用手写出所有HTML更好的模板解决方案，就像我在这里所做的一样，就像jsp通常比拥有一系列`System.out`更好一样。Servlet的`doGet()`方法中的println语句。 这个示例的目的是演示Groovy的DSL功能。(有关dsl的更多信息，请参见第43页的侧栏。)Groovy代码几乎完全匹配生成的HTML。对于在Groovy中动态地编写特别的HTML页面，我没有找到比MarkupBuilder更好的东西。

**HTML and StreamingMarkupBuilder**
```groovy
def h = {
  head{
    title("Search Results")
    link(rel:"stylesheet", type:"text/css", href:"http://main.css")
    script(type:"text/javascript", src:"http://main.js")
  }
}

def b = {
  body{
    h1("Search Results")
    div(id:"results", class:"simple"){
      table(border:1){
        tr{
          th("Name")
          th("Address")
        }
        tr{
          td{
          a(href:"http://abc.org?id=100" ,"Jane Doe")
          }
          td("123 Main St")
        }
      }
    }
  }
}

def html = new groovy.xml.StreamingMarkupBuilder().bind{
  unescaped << '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"' +
               '"http://www.w3.org/TR/html4/strict.dtd">'
  html{
    out << h
    out << b
  }
}

def htmlWriter = new FileWriter("test.html")
htmlWriter << html
```
使用StreamingMarkupBuilder，您可以异步定义页面的块，并根据需要将它们组合在一起。这允许您组合一个更健壮的模板系统。

### 8.14 Converting CSV to XML {#CSV转换为XML}
```groovy
// input file (addresses.csv):
99,John Smith,456 Fleet St,Denver,CO,80021
100,Jane Doe,123 Main St,Denver,CO,80020
101,Frank Jones,345 Center Blvd,Omaha,NE,68124

// groovy file:
def fileIn = new File("addresses.csv")
def fileOut = new FileWriter("addresses.xml")
def xml = new groovy.xml.MarkupBuilder(fileOut)
xml.addressBook{
  fileIn.splitEachLine(","){ tokens ->
    entry(id:tokens[0]){
      name(tokens[1])
      addresss(tokens[2])
      city(tokens[3])
      state(tokens[4])
      zipcode(tokens[5])
    }
  }
}

// output file (addresses.xml):
<addressBook>
  <entry id='99'>
    <name>John Smith</name>
    <addresss>456 Fleet St</addresss>
    <city>Denver</city>
    <state>CO</state>
    <zipcode>80021</zipcode>
  </entry>
  <entry id='100'>
    <name>Jane Doe</name>
    <addresss>123 Main St</addresss>
    <city>Denver</city>
    <state>CO</state>
    <zipcode>80020</zipcode>
  </entry>
  <entry id='101'>
    <name>Frank Jones</name>
    <addresss>345 Center Blvd</addresss>
    <city>Omaha</city>
    <state>NE</state>
    <zipcode>68124</zipcode>
  </entry>
</addressBook>
```
CSV文件非常常见。在XML之前，CSV是另一种以与供应商、语言和平台无关的方式存储数据的方法。结合第6.2节中讨论的splitEachLine()方法(读取104页上的文件内容)和第8.1节中讨论的MarkupBuilder(创建XML文档的“我很着急”指南)，您可以在第136页轻松地将CSV转换为XML。

**Parsing Complex CSV**
```groovy
// input file
99,John Smith,"456 Fleet St, Suite 123",Denver,CO,80021
100,Jane Doe,123 Main St,Denver,CO,80020
101,"Frank Jones, Jr." ,345 Center Blvd,Omaha,NE,68124

// output file
<addressBook>
  <entry id='99'>
    <name>John Smith</name>
    <addresss>"456 Fleet St</addresss>
    <city> Suite 123"</city>
    <state>Denver</state>
    <zipcode>CO</zipcode>
  </entry>
  <entry id='100'>
    <name>Jane Doe</name>
    <addresss>123 Main St</addresss>
    <city>Denver</city>
    <state>CO</state>
    <zipcode>80020</zipcode>
  </entry>
  <entry id='101'>
    <name>"Frank Jones</name>
    <addresss> Jr."</addresss>
    <city>345 Center Blvd</city>
    <state>Omaha</state>
    <zipcode>NE</zipcode>
  </entry>
</addressBook>
```

不幸的是，CSV很少像第一个示例中那样清晰地显示自己。有时会在字段中嵌入逗号，要求整个字段被引号包围。(您是否捕捉到了John Smith和Frank Jones, Jr.的错误XML地址?)

要解析出这些CSV记录，您需要做的不仅仅是简单地分割逗号上的行。下面是一个稍微健壮一些的CSV解析类SmartCsvParser，它在处理单个CSV字段中的嵌入逗号方面做得更好:
```groovy
def fileIn = new File("addresses2.csv")
def fileOut = new FileWriter("addresses2.xml")
def xml = new groovy.xml.MarkupBuilder(fileOut)
xml.addressBook {
  use(SmartCsvParser) {
    fileIn.eachLine { line - >
      def fields = line.smartSplit()
      entry(id: fields[0]) {
        name(fields[1])
        addresss(fields[2])
        city(fields[3])
        state(fields[4])
        zipcode(fields[5])
      }
    }
  }
}

class SmartCsvParser {
  static String[] smartSplit(String self) {
    def list = []
    def st = new StringTokenizer(self, ",")
    while (st.hasMoreTokens()) {
      def thisToken = st.nextToken()
      while (thisToken.startsWith("\"") && !thisToken.endsWith("\"")) {
        thisToken += "," + st.nextToken()
      }
      list << thisToken.noQuote()
    }
    return list
  }
  static String noQuote(String self) {
    if (self.startsWith("\"") || self.startsWith("\'")) {
      return self[1.. - 2]
    } else {
      return self
    }
  }
}
```
让我们在前面的示例中研究SmartCsvParser。smartSplit查看每个令牌。如果令牌以双引号开始，而不是以双引号结束，则必须有一个部分字段。smartSplit将继续添加令牌，直到找到结束报价。一旦所有字段正确地连接在一起，noQuote方法就会在必要时从字段值中删除周围的引号。

您使用了一个(category 类别)（如在第10.10节，将方法动态添加到类（Categories），在第196页中所述）将smartSplit方法添加到从fileIn.eachLine返回的字符串中。 这使您可以将smartSplit保留在本地。 如果您认为此方法在全局范围内受到关注，则很可能会改用ExpandoMetaClass类（如第198页上的第10.11节“将方法动态添加到类中（ExpandoMetaClass）中所述）”。

### 8.15 Converting JDBC ResultSets to XML {#将JDBC结果集转换为XML}
```groovy
//table addressbook:
|name |address |city |st |zipcode
+------------+----------------+-------+---+-------
|John Smith |456 Fleet St |Denver |CO |80021
|Jane Doe |123 Main St |Denver |CO |80020
|Frank Jones |345 Center Blvd |Omaha |NE |68124

//groovy:
def sql = groovy.sql.Sql.newInstance(
  "jdbc:derby://localhost:1527/MyDbTest;create=true" ,
  "testUser",
  "testPassword",
  "org.apache.derby.jdbc.ClientDriver" )

def xml = new groovy.xml.MarkupBuilder()
xml.addressBook{
  sql.eachRow("select * from addressbook"){ row ->
    entry{
      name(row.name)
      addresss(row.address)
      city(row.city)
      state(row.st)
      zipcode(row.zipcode)
    }
  }
}
```
类似于`File.eachFil`e允许您遍历目录中的每个文件（第6.1节，列出目录中的所有文件，第100页）和`List.each`允许您遍历列表中的每个项目（第3.14节，列表快捷方式） ，在第58页）上，可以使用`groovy.sql.Sql`对象使用`eachRow`闭包对JDBC ResultSet进行迭代。 在MarkupBuilder中混合可以为您提供一个透明的JDBC到XML转换器。

[^71]: http://en.wikipedia.org/wiki/Xpath
[^72]: http://en.wikipedia.org/wiki/JAXB
[^73]: http://castor.org
[^77]: http://en.wikipedia.org/wiki/Leaky_abstraction
[^710]: http://castor.org
[^851]: http://tidy.sourceforge.net/
[^852]: http://jtidy.sourceforge.net/
[^810]: http://en.wikipedia.org/wiki/CDATA

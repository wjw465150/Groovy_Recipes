# Groovy拾遗

## 美妙的操作符
许多以前使用 C++ 的开发人员会怀念操作符重载，例如 `+` 和 `-`。虽然它们很方便，但是被覆盖的操作符的多态实质会造成混淆，所以操作符重载在 Java 语言中被取消了。这个限制的好处是清晰：Java 开发人员不必猜想两个对象上的 `+` 是把它们加在一起还是把一个对象附加到另一个对象上。不好的地方则是丧失了一个有价值的简写形式。

现在，期望*放任自由的* Groovy 把这个简写形式带回来！以下将介绍 Groovy 对*操作符即时多态*（也称为操作符重载）的支持。正如 C++ 开发人员会告诉您的，这个东西既方便又有趣，虽然必须小心谨慎才能接近。  

### 算术类操作符

Groovy 支持以下算术类操作符的重载：

#### 表 1. Groovy 的算术类操作符  

| 操作符     | 方法             |
| ---------- | ---------------- |
| a + b      | `a.plus(b)`      |
| a - b      | `a.minus(b)`     |
| a * b      | `a.multiply(b)`  |
| a / b      | `a.divide(b)`    |
| a++ or ++a | `a.next()`       |
| a-- or --a | `a.previous()`   |
| a << b     | `a.leftShift(b)` |

您可能已经注意到 Groovy 中的 `+` 操作符已经在几个不同的领域重载了，特别是在用于集合的时候。

在表 1 中有一个可以重载的算术类操作符 `<<`，它恰好也为 Groovy 的集合重载。在集合的情况下，`<<` 覆盖后的作用像普通的 Java `add()` 方法一样，把值添加到集合的尾部（这与 Ruby 也很相似）。

### 数组类操作符

Groovy 支持重载标准的 Java 数组存取语法 `[]`，如表 4 所示：

#### 表 2. 数组操作符

| 操作符   | 方法            |
| -------- | --------------- |
| a[b]     | `a.getAt(b)`    |
| a[b] = c | `a.putAt(b, c)` |

可以看到，操作符的即时多态，或操作符重载，对于我们来说，如果小心使用和记录，会非常强大。但是，要当心不要滥用这个特性。如果决定覆盖一个操作符去做一些非常规的事情，请一定要清楚地记录下您的工作。对 Groovy 类进行改进，支持重载非常简单。小心应对并记录所做的工作，对于由此而来的方便的简写形式来说，代价非常公道。

## Groovy在Spring中的简单使用

### 1. 首先 编写java的业务接口类
```groovy
package com.springandgroovy;


public interface HelloWorldService {

	String sayHello();

}
```

### 2. 编写groovy类实现这个接口(注意：该文件名是HelloWorldServiceImpl.groovy)
```groovy
package com.springandgroovy;

public class HelloWorldServiceImpl  implements HelloWorldService{

	String name;

	String sayHello(){

		return "Hello $name!!!. Welcome to Scripting in Groovy.";

	}

}
```

### 3.比较关键的是spring配置文件,在文件的头部需要`lang`的名字空间,以便识别 `<lang:groovy ...`  
```xml
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:lang="http://www.springframework.org/schema/lang" xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
            http://www.springframework.org/schema/lang
            http://www.springframework.org/schema/lang/spring-lang-2.5.xsd">
  
  <!-- 设置默认的延时刷新时间 -->
  <lang:defaults refresh-check-delay="60000" />
  
  <lang:groovy id="helloWorldService" script-source="classpath:com/springandgroovy/HelloWorldServiceImpl.groovy">
    <lang:property name="name" value="meera"/>
  </lang:groovy>
</beans>
```
### 4. 还可以将HelloWorldServiceImpl写在spring的配置文件中,如下所示：（不提倡使用此方法）   
```xml
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:lang="http://www.springframework.org/schema/lang" xsi:schemaLocation="http://www.springframework.org/schema/beans
             http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
             http://www.springframework.org/schema/lang
             http://www.springframework.org/schema/lang/spring-lang-2.5.xsd">
  <!--   
      <lang:defaults refresh-check-delay="60000" />

      <lang:groovy id="helloWorldService"
        script-source="classpath:com/springandgroovy/HelloWorldServiceImpl.groovy">
        <lang:property name="name" value="meera">
      </lang:groovy>
  -->
  
  <lang:groovy id="helloWorldService">
    <lang:inline-script>
             <![CDATA[
                package com.springandgroovy;

                public class HelloWorldServiceImpl  implements HelloWorldService{
                    String name;

                    String sayHello(){
                      return "Hello $name. Welcome to Scripting in Groovy.";
                    }
               }
           ]]>
       </lang:inline-script>
    <lang:property name="name" value="meera"></lang:property>
  </lang:groovy>
</beans>

```

## 给大文件计算`SHA1`哈希值  
```groovy
import java.security.MessageDigest

int KB = 1024
int MB = 1024*KB

File f = new File(args[0])
if (!f.exists() || !f.isFile()) {
  println "Invalid file $f provided"
  println "Usage: groovy sha1.groovy <file_to_hash>"
}

def messageDigest = MessageDigest.getInstance("SHA1")

long start = System.currentTimeMillis()

f.eachByte(MB) { byte[] buf, int bytesRead ->
  messageDigest.update(buf, 0, bytesRead);
}

def sha1Hex = new BigInteger(1, messageDigest.digest()).toString(16).padLeft( 40, '0' )
long delta = System.currentTimeMillis()-start

println "$sha1Hex took $delta ms to calculate"
```

## ConfigSlurper
ConfigSlurper是Groovy中的一个实用程序类，用于编写属性文件，例如用于执行配置的脚本。 与常规Java属性文件不同，ConfigSlurper脚本支持本机Java类型，并且结构类似于树。 下面是如何使用ConfigSlurper脚本配置Log4j的示例：  
```properties
log4j.appender.stdout = "org.apache.log4j.ConsoleAppender"
log4j.appender."stdout.layout"="org.apache.log4j.PatternLayout"
log4j.rootLogger="error,stdout"
log4j.logger.org.springframework="info,stdout"
log4j.additivity.org.springframework=false
```
要将其加载到可读配置中，您可以执行以下操作：
```groovy
def config = new ConfigSlurper().parse(new File('myconfig.groovy').toURL())

assert "info,stdout" == config.log4j.logger.org.springframework
assert false == config.log4j.additivity.org.springframework
```
从上面的示例中可以看出，您可以使用点表示法导航配置，返回值是Java类型，如字符串和布尔 值。  

### 转换为Java属性文件和从Java属性文件转换
您可以将ConfigSlurper配置转换为Java属性文件或从Java属性文件转换。 例如：  
```groovy
java.util.Properties props = // load from somewhere

def config = new ConfigSlurper().parse(props)

props = config.toProperties()
```

### 合并配置  
您可以合并配置对象，这样如果您有多个配置文件并想要创建一个中央配置对象，您可以执行以下操作：  
```groovy
def config1 = new ConfigSlurper().parse(..)
def config2 = new ConfigSlurper().parse(..)

config1 = config1.merge(config2)
```

### 将配置序列化到磁盘
您可以将配置对象序列化到磁盘。 每个配置对象都实现了`groovy.lang.Writable`接口，允许您将配置写入任何`java.io.Writer`：  
```groovy
def config = new ConfigSlurper().parse(..)

new File("..").withWriter { writer ->
     config.writeTo(writer)
}
```

### 特殊的“环境(`environments`)”配置
ConfigSlurper类具有除默认构造函数之外的特殊构造函数，该构造函数采用“environment”参数。 此特殊构造函数与称为`环境`的属性设置协同工作。 这允许属性文件中存在默认设置，该设置可以通过适当的环境闭包中的设置取代。 这允许多个相关配置存储在同一文件中。

`Sample.groovy`这个groovy属性文件：
```groovy
sample {
  foo = "default_foo"
  bar = "default_bar"
}

environments {
  development {
    sample {
      foo = "dev_foo"
    }
  }
  test {
    sample {
      bar = "test_bar"
    }
  }
}
```
以下是演示此配置的演示代码：  
```groovy
def config = new ConfigSlurper("development").parse(new File('Sample.groovy').toURL())

assert config.sample.foo == "dev_foo"
assert config.sample.bar == "default_bar"

config = new ConfigSlurper("test").parse(new File('Sample.groovy').toURL())

assert config.sample.foo == "default_foo"
assert config.sample.bar == "test_bar"
```
>  注意：环境闭包不能直接解析。 不使用特殊环境构造函数，将忽略闭包。
>  

## 转换`SQL Result`到`XML`  
```groovy
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
def schema = "PROD"
def sql = Sql.newInstance("jdbc:oracle:thin:@hostname:1526:${schema}", "scott", "tiger", "oracle.jdbc.driver.OracleDriver")

/* Request */
def req = """
SELECT id,  name, givenname, unit FROM ${schema}.people
WHERE
in_unit=1
AND visible=0
"""

def out = new File('out.xml')
def writer = new FileWriter( out )
def xml = new MarkupBuilder( writer )

xml.agents {
    sql.eachRow( req as String  ) { row ->
        /* For each row output detail */
            xml.agent(id:row.id) {
                name( row.name )
                givenname( row.givenname )
                unit( row.unit )
            }
    }
}
```
输出:
```xml
<agents>                               <!-- xml.agents {                  -->
  <agent id='870872'>                  <!--    agent(id:row.id) {         -->
    <name>ABTI</name>                  <!--       name( row.nom )         -->
    <givenname>Jean</givenname>        <!--       givenname( row.prenom ) -->
    <unit>Sales</unit>                 <!--       unit( row.unite )       -->
  </agent>                             <!--    }                          -->
...
</agents>
```

## HTTP

### Making an HTTP GET Request  
```groovy
def page = new URL('http://www.aboutgroovy.com' ).text
  
new URL('http://www.aboutgroovy.com').eachLine { line ->
   println line
}
  
'http://www.aboutgroovy.com'.toURL().text
 
//Processing a Request Based on the HTTP Response Code
def url = new URL('http://www.aboutgroovy.com')
def connection = url.openConnection()
if(connection.responseCode == 200) {
   println connection.content.text
} else {
   println 'An error occurred:'
   println connection.responseCode
   println connection.responseMessage
}

// Get content of URL with parameters.
def url = "http://www.mrhaki.com/url.html".toURL()

// Simple Integer enhancement to make
// 10.seconds be 10 * 1000 ms.
Integer.metaClass.getSeconds = { ->
    delegate * 1000
}

def content = url.getText(connectTimeout: 10.seconds, readTimeout: 10.seconds,
                          useCaches: true, allowUserInteraction: false,
                          requestProperties: ['User-Agent': 'Groovy Sample Script'])
                          
url.newReader(connectTimeout: 10.seconds, useCaches: true).withReader { reader ->
    assert reader.readLine() == 'Simple test document'
}
```

### Making an HTTP POST Request
```groovy
def url = new URL('http://search.yahoo.com/search')
def connection = url.openConnection()

//switch the method to POST (GET is the default)
connection.setRequestMethod('POST')

//write the data
def queryString = 'n=20&vf=pdf&p=groovy+grails'
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(queryString)
writer.flush()
writer.close()
connection.connect()

//print the results
println connection.content.text
//PUT 和 DELETE 操作形式上同上。

//RESTful POST Requests Using XML
def xml = """
<person id="100" >
  <firstname>Jane</firstname>
  <lastname>Doe</lastname>
  <address type="home" >
    <street>123 Main St</street>
    <city>Denver</city>
    <state>CO</state>
    <zip>80020</zip>
  </address>
</person>
 """
 ...
 connection.setRequestProperty('Content-Type', 'application/xml')
 ...
 writer.write(xml)
 ...
```

## Padding Strings
Groovy使用几种填充方法扩展了String类。 这些方法允许我们定义String值必须占用的固定宽度。 如果String本身小于固定宽度，则用空格或我们定义的任何其他字符或字符串填充空格。 我们可以填充字符串的左侧或右侧或左右两侧，并将字符串放在中心。

当我们创建在控制台上运行的Groovy脚本并且我们想要格式化某些输出时，这些方法特别有用。  
```groovy
assert '   Groovy   ' == 'Groovy'.center(12)
assert 'Groovy      ' == "Groovy".padRight(12)
assert '      Groovy' == /Groovy/.padLeft(12)

assert '---Groovy---' == "Groovy".center(12, '-')
assert 'Groovy * * *' == "Groovy".padRight(12, ' *')
assert 'Groovy Groovy Groovy' == 'Groovy'.padLeft(20, 'Groovy ')

def createOutput = {
	def table = [
	// Page,    Response time, Size
	['page1.html',        200, 1201],
	['page2.html',         42, 8853],
	['page3.html',         98, 3432],
	['page4.html',        432, 9081]
	]

	def total = { data, index ->
		data.inject(0) { result, row -> result += row[index] }
	}
	def totalTime = total.curry(table, 1)
	def totalSize = total.curry(table, 2)

	def out = new StringBuffer()
	out << ' Summary '.center(15, "*") << '\n\n'
	out << 'Total pages:'.padRight(25)
	out << table.size().toString().padLeft(6) << '\n'
	out << 'Total response time (ms):'.padRight(25)
	out << totalTime().toString().padLeft(6) << '\n'
	out << 'Total size (KB):'.padRight(25)
	out << totalSize().toString().padLeft(6) << '\n\n'

	out << ' Details '.center(15, "*") << '\n\n'
	table.each {
		out << it[0].padRight(14)
		out << it[1].toString().padLeft(5)
		out << it[2].toString().padLeft(8)
		out << '\n'
	}
	out.toString()
}

assert '''\
*** Summary ***

Total pages:                  4
Total response time (ms):   772
Total size (KB):          22567

*** Details ***

page1.html      200    1201
page2.html       42    8853
page3.html       98    3432
page4.html      432    9081
''' == createOutput()
```

## `With`
Groovy有一个`with`方法，我们可以用它来对方法调用和属性访问对象进行分组。 `with`方法接受闭包，闭包中的每个方法调用或属性访问都适用于对象（如果适用）。 该方法是Groovy对`java.lang.Object`类的扩展的一部分。 让我们看一个例子：  
```groovy
class Sample {
    String username
    String email
    List<String> labels = []
    def speakUp() { "I am $username" }
    def addLabel(value) { labels << value }
}

def sample = new Sample()
sample.with {
    username = 'mrhaki'
    email = 'email@host.com'
    println speakUp()  // Output: I am mrhaki
    addLabel 'Groovy' 
    addLabel 'Java'    
}
assert 2 == sample.labels.size()
assert 'Groovy' == sample.labels[0]
assert 'Java' == sample.labels[1]
assert 'mrhaki' == sample.username
assert 'email@host.com' == sample.email

def sb = new StringBuilder()
sb.with {
    append 'Just another way to add '
    append 'strings to the StringBuilder '
    append 'object.'    
}

assert 'Just another way to add strings to the StringBuilder object.' == sb.toString()

// Another example as seen at 
// http://javajeff.blogspot.com/2008/11/getting-groovy-with-with.html
def cal = Calendar.instance
cal.with {
    clear()
    set(YEAR, 2009)
    set MONTH, SEPTEMBER
    set DATE, 4    
    add DATE, 2
}
assert'September 6, 2009' == cal.time.format('MMMM d, yyyy')

```

## 数组`Array`
Groovy支持数组，就像在Java中一样。 由于添加到阵列的GDK扩展，我们只获得了更多的方法。 我们唯一需要考虑的是我们初始化数组的方式。 在Java中，我们可以使用以下代码定义和填充数组：`String [] s = new String [] {"a"，"b"} ;`,但在Groovy中我们不能使用此语法。 在Groovy中，前面的语句将变为`String [] s = ["a"，"b"] as String[]`。 
```groovy
def strArray = new String[3]
assert strArray instanceof String[]
strArray[0] = 'mrhaki'
strArray.putAt(1, 'Groovy')  // New syntax.
strArray[2] = 'Java'

assert 'mrhaki' == strArray.getAt(0)  // Just another way to get a value.
assert 'Groovy' == strArray[1]
assert 'Java' == strArray[-1]  // Negative indeces allowed.
assert ['mrhaki', 'Groovy'] == strArray[0..1]  // We can use ranges.
assert ['mrhaki', 'Java'] == strArray[0, 2]

assert 3 == strArray.length  // Normal length property for arrays.
assert 3 == strArray.size()  // Groovy adds size() method as well.

// We can use min() and max() methods.
assert 42 == [102,301,42,83].min()
assert 301 == [102,301,42,83].max()
assert 'Java' == strArray.min { it.size() }
assert 'mrhaki' == strArray.max { it[0] as char }

// We can even use the Collection GDK methods on an array.
strArray.eachWithIndex { value, idx -> assert value == strArray[idx] }
assert ['ikahrm', 'yvoorG', 'avaJ'] == strArray.collect { it.reverse() }
assert 'Groovy' == strArray.find { it =~ /Groovy/ }

// We can remove values with the '-' operator.
assert ['Groovy', 'Java'] == strArray - 'mrhaki'

// Other useful methods for arrays.
assert ['Java', 'Groovy', 'mrhaki'] == strArray.reverse()
assert ['Groovy', 'Java', 'mrhaki'] == strArray.sort()
assert 1 == strArray.count('mrhaki')

// Convert to ArrayList.
def strList = strArray.toList()
assert 'java.util.ArrayList' == strList.class.name

// Convert ArrayList to array object.
def otherArray = strList as String[]
assert otherArray instanceof String[]

```

## GroupBy
我们可以在Groovy中使用`groupBy()`方法将`List`或`Map`中的元素分组。
```
import static java.util.Calendar.*

class User {
    String name
    String city
    Date birthDate
    public String toString() { name }
}

def users = [
    new User(name:'mrhaki', city: 'Tilburg',   birthDate: Date.parse('yyyy-MM-dd', '1973-9-7')),
    new User(name:'bob',    city: 'New York',  birthDate: Date.parse('yyyy-MM-dd', '1963-3-30')),
    new User(name:'britt',  city: 'Amsterdam', birthDate: Date.parse('yyyy-MM-dd', '1980-5-12')),
    new User(name:'kim',    city: 'Amsterdam', birthDate: Date.parse('yyyy-MM-dd', '1983-3-30')),
    new User(name:'liam',   city: 'Tilburg',   birthDate: Date.parse('yyyy-MM-dd', '2009-3-6'))
]

def result = users.groupBy({it.city}, {it.birthDate.format('MMM')})

assert result.toMapString() == 
    '[Tilburg:[Sep:[mrhaki], Mar:[liam]], New York:[Mar:[bob]], Amsterdam:[May:[britt], Mar:[kim]]]'

assert result.Amsterdam.size() == 2
assert result.Tilburg.Mar.name == ['liam']

result = users.groupBy({it.name[0]}, {it.city})
assert result.toMapString() ==
    '[m:[Tilburg:[mrhaki]], b:[New York:[bob], Amsterdam:[britt]], k:[Amsterdam:[kim]], l:[Tilburg:[liam]]]'
assert result.k.Amsterdam.name == ['kim']  

// groupBy with multiple closues also works on Map
def usersByCityMap = users.groupBy({it.city})
def resultMap = usersByCityMap.groupBy({it.value.size()}, { k,v -> k.contains('i') })
assert resultMap.toMapString() ==
    '[2:[true:[Tilburg:[mrhaki, liam]], false:[Amsterdam:[britt, kim]]], 1:[false:[New York:[bob]]]]'
assert resultMap[1].size() == 1
assert resultMap[2].size() == 2
assert resultMap[2][true].Tilburg.name.join(',') == 'mrhaki,liam'

```

## 用`CliBuilder`来解析命令行
```groovy
import java.text.*

def showdate(args) {
    def cli = new CliBuilder(usage: 'showdate.groovy -[chflms] [date] [prefix]')
    // Create the list of options.
    cli.with {
        h longOpt: 'help', 'Show usage information'
        c longOpt: 'format-custom', args: 1, argName: 'format', 'Format date with custom format defined by "format"'
        f longOpt: 'format-full',   'Use DateFormat#FULL format'
        l longOpt: 'format-long',   'Use DateFormat#LONG format'
        m longOpt: 'format-medium', 'Use DateFormat#MEDIUM format (default)'
        s longOpt: 'format-short',  'Use DateFormat#SHORT format'
    }
    
    def options = cli.parse(args)
    if (!options) {
        return
    }
    // Show usage text when -h or --help option is used.
    if (options.h) {
        cli.usage()
        // Will output:
        // usage: showdate.groovy -[chflms] [date] [prefix]
        //  -c,--format-custom <format>   Format date with custom format defined by "format"
        //  -f,--format-full              Use DateFormat#FULL format   
        //  -h,--help                     Show usage information   
        //  -l,--format-long              Use DateFormat#LONG format   
        //  -m,--format-medium            Use DateFormat#MEDIUM format   
        //  -s,--format-short             Use DateFormat#SHORT format   
        return
    }
    
    // Determine formatter.
    def df = DateFormat.getDateInstance(DateFormat.MEDIUM)  // Defeault.
    if (options.f) {  // Using short option.
        df = DateFormat.getDateInstance(DateFormat.FULL) 
    } else if (options.'format-long') {  // Using long option.
        df = DateFormat.getDateInstance(DateFormat.LONG) 
    } else if (options.'format-medium') {
        df = DateFormat.getDateInstance(DateFormat.MEDIUM) 
    } else if (options.s) {
        df = DateFormat.getDateInstance(DateFormat.SHORT) 
    } else if (options.'format-custom') {
        df = new SimpleDateFormat(options.c)
    }

    // Handle all non-option arguments.
    def prefix = ''  // Default is empty prefix.
    def date = new Date()  // Default is current date.
    def extraArguments = options.arguments()
    if (extraArguments) {
        date = new Date().parse(extraArguments[0])
        // The rest of the arguments belong to the prefix.
        if (extraArguments.size() > 1) {
            prefix = extraArguments[1..-1].join(' ')
        }
    }
    
    "$prefix${df.format(date)}"
}

// Set locale for assertions.
Locale.setDefault(Locale.US)
assert '12/1/09' == showdate(['--format-short', '2009/12/1'])
assert '12/1/09' == showdate(['-s', '2009/12/1'])
assert 'Dec 1, 2009' == showdate(['2009/12/1'])
assert 'Dec 1, 2009' == showdate(['--format-medium', '2009/12/1'])
assert 'Dec 1, 2009' == showdate(['-m', '2009/12/1'])
assert 'December 1, 2009' == showdate(['--format-long', '2009/12/1'])
assert 'December 1, 2009' == showdate(['-l', '2009/12/1'])
assert 'Tuesday, December 1, 2009' == showdate(['--format-full', '2009/12/1'])
assert 'Tuesday, December 1, 2009' == showdate(['-f', '2009/12/1'])
assert 'Default date format: Dec 1, 2009' == showdate(['2009/12/1', 'Default', 'date', 'format: '])
assert 'Important date: Dec 1, 2009' == showdate(['-m', '2009/12/1', 'Important date: '])
assert 'week 49 of the year 2009 AD' == showdate(['-c', "'week' w 'of the year' yyyy G", '2009/12/1'])
assert '2009/12/01' == showdate(['--format-custom', 'yyyy/MM/dd', '2009/12/01'])
assert '2009' == showdate(['-cyyyy', '2009/12/1'])
assert new Date().format('yyyy/MM/dd') == showdate(['--format-custom', 'yyyy/MM/dd'])

println showdate(args) 
```

## 正则表达式
在Groovy中，我们使用`=~`运算符（查找运算符）来创建新的匹配器(Matcher)对象。  
我们可以使用第二个运算符`==~`（匹配运算符）来进行精确匹配。 使用此运算符，将在Matcher对象上调用`matches()`方法。 结果是一个布尔值。
```groovy
def finder = ('groovy' =~ /gr.*/)
assert finder instanceof java.util.regex.Matcher

def matcher = ('groovy' ==~ /gr.*/)
assert matcher instanceof Boolean

assert 'Groovy rocks!' =~ /Groovy/  // =~ in conditional context returns boolean.
assert !('Groovy rocks!' ==~ /Groovy/)  // ==~ looks for an exact match.
assert 'Groovy rocks!' ==~ /Groovy.*/

def cool = /gr\w{4}/  // Start with gr followed by 4 characters.
def findCool = ('groovy, java and grails rock!' =~ /$cool/)
assert 2 == findCool.count
assert 2 == findCool.size()  // Groovy adds size() method.
assert 'groovy' == findCool[0]  // Array-like access to match results.
assert 'grails' == findCool.getAt(1)

// With grouping we get a multidimensional array.
def group = ('groovy and grails, ruby and rails' =~ /(\w+) and (\w+)/)
assert group.hasGroup()
assert 2 == group.size()
assert ['groovy and grails', 'groovy', 'grails'] == group[0]
assert 'rails' == group[1][2]

// Use matcher methods.
assert 'Hi world' == ('Hello world' =~ /Hello/).replaceFirst('Hi')

// Groovy matcher syntax can be used in other methods.
assert ['abc'] == ['def', 'abc', '123'].findAll { it =~ /abc/ }
assert [false, false, true] == ['def', 'abc', '123'].collect { it ==~ /\d{3}/ }
```

## 能否跳出`each`闭包
```groovy
List.metaClass.eachUntilGreaterThanFive = { closure ->
    for ( value in delegate ) {
        if ( value  > 5 ) break
        closure(value)
    }
}

def a = [1, 2, 3, 4, 5, 6, 7]

a.eachUntilGreaterThanFive {
    println it
}
```

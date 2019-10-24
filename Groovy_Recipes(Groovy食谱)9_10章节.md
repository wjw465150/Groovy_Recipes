# 第9章 Web Services {#Web服务}

Web服务如今无处不在。作为一个行业，一旦我们发现XML可以通过HTTP和HTML传递，我们就进入了面向服务的体系结构(SOA)的新时代。这种从远程数据源获取数据的新方法意味着开发人员必须理解底层TCP/IP和HTTP的机制，以及各种更高层的XML方言:SOAP、REST和XML-RPC。幸运的是，Groovy在所有方面都帮助了我们。

在本章中，我们将从如何确定本地`TCP/IP`地址和域名以及远程系统的域名的底层基础知识开始。我们将向上移动到HTTP—学习如何以编程方式获取、发布、放置和删除。我们将以如何发送和接收SOAP消息、XML-RPC消息和RESTful请求的示例结束本章。我们甚至还将解析一些逗号分隔值(CSV)数据，这只是为了满足以前的需要。

## 9.1 Finding Your Local IP Address and Name {#查找您的本地IP地址和名称}
```groovy
InetAddress.localHost.hostAddress
===> 63.246.7.76

InetAddress.localHost.hostName
===> myServer

InetAddress.localHost.canonicalHostName
===> www.aboutgroovy.com
```
在你能和别人交流之前，了解自己总是有帮助的。在这个示例中，您将发现您的IP地址、您的本地主机名和供其他用户使用的DNS名称。

InetAddress类来自`java.net`包。 您不能直接实例化InetAddress类`def addr=new InetAddress()`，因为构造函数是私有的。 但是，您可以使用几种不同的静态方法来返回格式正确的InetAddress。 这里讨论了获取本地信息的`getLocalHost()`方法； 下一页中的第9.2节“查找远程IP地址和域名”中讨论了用于获取远程信息的`getByName()`和`getAllByName()`。

`getLocalHost()`方法返回一个InetAddress，它表示本地主机或运行其的硬件。 如第72页的第4.2节“ Getter和Setter快捷方式语法”中所述，`getLocalHost()`可在Groovy中缩短为`localHost`。 一旦有了`localHost`的句柄，就可以调用`getHostAddress()`来获取IP地址，或者可以调用`getHostName()`来获取本地计算机名称。 该名称是系统的专用名称，与在DNS中注册的名称相对，世界其他地方都可以看到。 调用`getCanonicalHostName()`执行DNS查找。

当然，正如在第89页的5.4节，运行Shell命令中所讨论的，操作系统附带的常用命令行工具仅需`execute()`就可使用。 它们可能不像InetAddress方法那样容易解析，但是如您所见，它们公开了更多细节。
```groovy
// available on all operating systems
"hostname".execute().text
===> myServer

// on Unix/Linux/Mac OS X
println "ifconfig".execute().text
===>
en2: flags=8963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1500
    inet6 fe80::21c:42ff:fe00:0%en2 prefixlen 64 scopeid 0x8
    inet 10.37.129.3 netmask 0xffffff00 broadcast 10.37.129.255
    ether 00:1c:42:00:00:00
    media: autoselect status: active
    supported media: autoselect

// on Windows
println "ipconfig /all".execute().text
===>
Windows IP Configuration
    Host Name . . . . . . . . . . . . : scottdavis1079
    Primary Dns Suffix . . . . . . . :
    Node Type . . . . . . . . . . . . : Unknown
    IP Routing Enabled. . . . . . . . : No
    WINS Proxy Enabled. . . . . . . . : No

Ethernet adapter Local Area Connection:
    Connection-specific DNS Suffix . :
    Description . . . . . . . . . . . : Parallels Network Adapter
    Physical Address. . . . . . . . . : 00-61-20-5C-3B-B9
    Dhcp Enabled. . . . . . . . . . . : Yes
    Autoconfiguration Enabled . . . . : Yes
    IP Address. . . . . . . . . . . . : 10.211.55.3
    Subnet Mask . . . . . . . . . . . : 255.255.255.0
    Default Gateway . . . . . . . . . : 10.211.55.1
    DHCP Server . . . . . . . . . . . : 10.211.55.1
    DNS Servers . . . . . . . . . . . : 10.211.55.1
    Lease Obtained. . . . . . . . . . : Tuesday, October 09, 2007 2:53:02 PM
    Lease Expires . . . . . . . . . . : Tuesday, October 16, 2007 2:53:02 PM
```

## 9.2 Finding a Remote IP Address and Domain Name {#查找远程IP地址和域名}
```groovy
InetAddress.getByName("www.aboutgroovy.com")
===> www.aboutgroovy.com/63.246.7.76

InetAddress.getAllByName("www.google.com").each{println it}
===>
www.google.com/64.233.167.99
www.google.com/64.233.167.104
www.google.com/64.233.167.147

InetAddress.getByName("www.google.com").hostAddress
===> 64.233.167.99

InetAddress.getByName("64.233.167.99").canonicalHostName
===> py-in-f99.google.com
```

除了返回有关本地计算机的信息外，您还可以使用InetAddress查找有关远程`systems.getByName()`的信息，该格式返回格式良好的InetAddress对象，该对象代表远程`systems.getByName()`接受任一域名 （例如`www.aboutgroovy.com`）或IP位址（例如`64.233.167.99`）。 一旦拥有了系统的句柄，就可以要求其`hostAddress`和`canonicalHostName`。

有时，DNS名称可以解析为许多不同的IP地址。 对于繁忙的网站尤其如此，这些网站在许多物理服务器之间实现了负载平衡。 如果DNS名称解析为多个IP地址，则`getByName()`将返回列表中的第一个IP地址，而`getAllByName()`将返回所有这些IP地址。

当然，您也可以使用通常的命令行工具来查询远程系统：
```groovy
// on Unix/Linux/Mac OS X
println "dig www.aboutgroovy.com".execute().text
===>
; <<>> DiG 9.3.4 <<>> www.aboutgroovy.com
;; global options: printcmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 55649
;; flags: qr rd ra; QUERY: 1, ANSWER: 1, AUTHORITY: 2, ADDITIONAL: 2

;; QUESTION SECTION:
;www.aboutgroovy.com. IN A

;; ANSWER SECTION:
www.aboutgroovy.com. 300 IN A 63.246.7.76

;; AUTHORITY SECTION:
aboutgroovy.com. 82368 IN NS ns1.contegix.com.
aboutgroovy.com. 82368 IN NS ns2.contegix.com.
;; ADDITIONAL SECTION:
ns1.contegix.com. 11655 IN A 63.246.7.200
ns2.contegix.com. 11655 IN A 63.246.22.100

;; Query time: 204 msec
;; SERVER: 66.174.92.14#53(66.174.92.14)
;; WHEN: Tue Oct 9 15:16:16 2007
;; MSG SIZE rcvd: 130

// on Windows
println "nslookup www.aboutgroovy.com".execute().text
===>
Server: UnKnown
Address: 10.211.55.1

Name: www.aboutgroovy.com
Address: 63.246.7.76
```

## 9.3 Making an HTTP GET Request {#发出HTTP GET请求}
```groovy
def page = new URL("http://www.aboutgroovy.com").text
===>
<html><head><title>...

new URL("http://www.aboutgroovy.com").eachLine{line ->
  println line
}

===>
<html>
<head>
<title>
...
```

获取HTML页面内容的最简单方法是在URL上调用`getText()`。 这使您可以将整个响应存储在String变量中。 如果页面太大而不能舒适地执行此操作，则还可以使用`eachLine()`逐行遍历响应。

Groovy在`java.lang.String`中添加了一个`toURL()`方法，使您可以使用稍微简化一些的语法来发出相同的请求：
```javascript
"http://www.aboutgroovy.com".toURL().text
"http://www.aboutgroovy.com".toURL().eachLine{...}
```

我们将讨论如何简化此过程，以使您可以简单地在第198页上的第10.11节“将方法动态添加到类（ExpandoMetaClass）”中调用`"http://www.aboutgroovy.com".get()`。

**根据HTTP响应代码处理请求**
```groovy
def url = new URL("http://www.aboutgroovy.com")
def connection = url.openConnection()
if(connection.responseCode == 200){
  println connection.content.text
} else{
  println "An error occurred:"
  println connection.responseCode
  println connection.responseMessage
}
```

直接在URL对象上调用`getText()`意味着您希望一切顺利—没有连接超时，没有404，等等。 尽管您的乐观态度值得称赞，但是如果您想编写稍微更多的容错代码，则应该在URL上调用`openConnection()`。

这将返回一个`java.net.URLConnection`对象，该对象使您可以对URL对象进行更详细的工作。 `connection.content.text`返回与`url.text`相同的信息，同时使您可以对200或404的响应进行更多的自省-`connection.responseCode`； `connection.responseMessage`用于OK或找不到文件。

**获取HTTP响应元数据**
```groovy
def url = new URL("http://www.aboutgroovy.com")
def connection = url.openConnection()
connection.responseCode
===> 200
connection.responseMessage
===> OK
connection.contentLength
===> 4216
connection.contentType
===> text/html
connection.date
===> 1191250061000
connection.expiration
===> 0
connection.lastModified
===> 0

connection.headerFields.each{println it}
===>
Content-Length=[4216]
Set-Cookie=[JSESSIONID=3B2DE7CBDAE3D58EC46D5A8DF5AF89D1; Path=/]
Date=[Mon, 01 Oct 2007 14:47:41 GMT]
null=[HTTP/1.1 200 OK]
Server=[Apache-Coyote/1.1]
Content-Type=[text/html]
```
一旦拥有`URLConnection`的句柄，就可以完全访问随附的响应元数据。 除了`responseCode`和`responseMessage`之外，您还可以请求诸如`contentLength`和`contentType`之类的东西，甚至可以逐个迭代每个响应头。

**创建便捷的GET类**
```groovy
class Get{
  String url
  String queryString
  URLConnection connection
  String text
  
  String getText(){
    def thisUrl = new URL(this.toString())
    connection = thisUrl.openConnection()
    if(connection.responseCode == 200){
      return connection.content.text
    } else{
      return "Something bad happened\n" +
      "URL: " + this.toString() + "\n" +
      connection.responseCode + ": " +
      connection.responseMessage
    }
  }

  String toString(){
    return url + "?" + queryString
  }
}

def get = new Get(url:"http://search.yahoo.com/search")
get.queryString = "p=groovy"
println get
===> http://search.yahoo.com/search?p=groovy

println get.text
===> <html><head>...

get.url = "http://www.yahoo.com/no.such.page"
println get.text
===>
Something bad happened
URL: http://www.yahoo.com/no.such.page?p=groovy
404: Not Found
```

到目前为止，您已经编写了一些非常程序化的代码[^901]。它确实完成了工作，但是在缺乏可重用性方面，它只受到一点点影响。(你敢说“复制粘贴”是一种有效的重用吗?你是一个优秀的面向对象程序员——你怎么能想到这样的事情呢?)这个自定义Get类将您到目前为止学到的所有东西包装成可重用的东西。它有一个很好的简单接口，并隐藏了足够多的HttpConnection复杂性，因此值得您花时间去使用它。

现在，没有什么比`"http://www.aboutgroovy.com".toURL().text`更简单的了。与之相对的是`Jakarta Commons HttpClient`[^902]，这是一个非常完整的库，比我自己一个人的库要完整得多。当然，缺点是向项目添加了另一个依赖项。自定义Get类很好地分割了这种差异。它比`"". tourl().text`稍微健壮一些，但它是在纯Groovy中实现的，因此不必担心类路径中的JAR膨胀。

还有一件事：Get类增加了对查询字符串的支持。 这是名称/值对的集合，可以将其附加到URL的末尾以进一步对其进行自定义。 有关更多信息，请参见第9.4节，使用查询字符串。

**RESTful GET请求**
```groovy
"http://search.yahooapis.com/WebSearchService/V1/webSearch?
appid=YahooDemo&query=groovy&results=10".toURL().text

//alternately, using our Get class
def get = new Get()
get.url = "http://search.yahooapis.com/WebSearchService/V1/webSearch"
get.queryString = "appid=YahooDemo&query=groovy&results=10"
def results = get.text
```

RESTful Web服务是一种Web服务。 REST代表代表性状态转移[^903]。 尽管对于真正意义上的RESTful的含义有多种不同的解释，但通常公认的是，返回XML结果（与HTML或其他数据格式相对）的HTTP GET请求构成了RESTful Web服务的最简单形式。

Yahoo提供了一个RESTful API [^904]，该API以XML返回查询结果。 此查询返回搜索词groovy的前十个匹配。 有关此查询的结果及其解析方法，请参阅第9.1页的第9.12节“将Yahoo Search结果解析为XML”。

## 9.4 Working with Query Strings {#使用查询字符串}
```javascript
"http://search.yahoo.com/search?p=groovy".toURL().text
```

查询字符串允许您通过在地址末尾添加名称/值对来发出更复杂的HTTP GET请求。 现在，您不仅可以在`http://search.yahoo.com`上请求一个静态页面，还可以对包含groovy一词的所有页面进行动态查询。

Web从简单的分布式文件系统转换为完全可编程的Web [^905]。 以编程方式发出HTTP GET请求的机制没有改变，这没有比我们在第155页的9.3节“发出HTTP GET请求”中讨论的复杂。

但是，使用查询字符串的语义为编程可能性开辟了一个全新的世界。

例如，可以在单个URL中捕获复杂的网页，例如显示丹佛国际机场的Google地图。 这意味着我们只需单击页面右上角的“链接到此页面”，就可以对其进行超链接，添加书签或将其通过电子邮件发送给朋友。 查询字符串中的每个元素代表地图的不同方面：ll代表地图的纬度/经度中心点（39.87075，-104.694214），z代表缩放级别（11），t代表类型（h或混合） ），依此类推。
```javascript
"http://maps.google.com/maps?f=q&hl=en&geocode=&time=&date=&ttype=
&q=dia&sll=37.0625,-95.677068&sspn=34.038806,73.125&ie=UTF8
&ll=39.87075,-104.694214&spn=0.2577,0.571289&z=11&iwloc=addr&om=1&t=h"
.toURL().text
```

**从列表构建查询字符串**
```groovy
def queryString = []
queryString << "n=" + URLEncoder.encode("20")
queryString << "vd=" + URLEncoder.encode("m3")
queryString << "vl=" + URLEncoder.encode("lang_en")
queryString << "vf=" + URLEncoder.encode("pdf")
queryString << "p=" + URLEncoder.encode("groovy grails")

def address = "http://search.yahoo.com/search"
def url = new URL(address + "?" + queryString.join("&"))
println url
===>
http://search.yahoo.com/search?n=20&vd=m3&vl=lang_en&vf=pdf&p=groovy+grails

println url.text
```

通常，您需要负责从任意数据值集合中组装格式正确的查询字符串。 秘诀是确保值是经过URL编码的[^906]（"foo bar baz" ==> "foo+bar+baz"），而名称部分（nonsense =）仍为纯文本。 如果尝试将名称和值URL编码为单个字符串（“unsense=foo bar baz”），则等号（=）将转换为`%3D`，并且您的Web服务器很可能会拒绝该请求。

本示例创建一个`名称/值对`列表，确保仅使用`java.net.URLEncoder`对值进行URL编码。 稍后，当您需要格式正确的查询字符串时，请调用`queryString.join("&")`。 正如我们在第3.14节“连接”（第60页）中讨论的那样，它以单个字符串的形式返回列表，每个元素都与您作为参数传入的字符串连接在一起。

通过执行高级Yahoo搜索并从结果URL中挑选有趣的名称/值对来构建此特定的查询字符串。 n返回二十个结果，而不是默认的十个结果。 vd将结果限制为过去三个月中发布的结果。 vl仅返回英文页面。 vf仅过滤PDF文档的结果。 最后，p寻找提到异常或粗糙的结果。

**从Map构建查询字符串**
```groovy
def map = [n:20, vf:"pdf", p:"groovy grails"]
def list = []
map.each{name,value->
  list << "$name=" + URLEncoder.encode(value.toString())
}
println list.join("&")
===> n=20&vf=pdf&p=groovy+grails
```

Groovy Maps是表示查询字符串的一种很好的方法，因为两者自然都有名称/值对。 此示例仍然使用临时List存储URL编码的值，并使用`join("&")`在最后一分钟将它们放在一起。

有一种极端的情况使它不能成为100％的解决方案。 查询字符串允许具有重复的命名元素，而Map强制使用唯一的名称。
```javascript
http://localhost/order?book=Groovy+Recipes&book=Groovy+In+Action
```
如果您可以忍受此限制，那么Maps是完美的解决方案。 如果需要支持重复的命名元素，请参见部分9.4，创建便捷QueryString类以获取更多信息。

**创建一个便捷的QueryString类**
```groovy
class QueryString{
  Map params = [:]
  
  //this constructor allows you to pass in a Map
  QueryString(Map params){
    if(params){
      this.params.putAll(params)
    }
  }
  
  //this method allows you to add name/value pairs
  void add(String name, Object value){
    params.put(name, value)
  }
  
  //this method returns a well-formed QueryString
  String toString(){
    def list = []
    params.each{name,value->
      list << "$name=" + URLEncoder.encode(value.toString())
    }
    return list.join("&")
  }
}

def qs = new QueryString(n:20, vf:"pdf", p:"groovy grails")
println qs
===> n=20&vf=pdf&p=groovy+grails

def qs2 = new QueryString()
qs2.params.put("firstname", "Scott")
qs2.add("id", 99)
qs2.add "updated", new Date()
println qs2
===> firstname=Scott&id=99&updated=Wed+Oct+10+20%3A17%3A34+MDT+2007
```

创建便捷类可以使您将构建格式正确的查询字符串的机制封装到可重用的组件中。

qs对象在构造函数中接受名称/值对，这些名称/值对被强制转换成映射。(您也可以将现有的映射传递给构造函数。)qs2对象演示了三种不同的方法来传递名称/值对——通过使用方便的带括号的`add()`方法直接访问params映射，最后通过调用相同的`add()`方法，同时利用Groovy的可选括号。

注意，`add()`方法接受值的对象。这允许您存储整数和类等值，而不是简单的字符串。调用`URLEncoder.encode(value.toString())`确保正确地将值插入查询字符串。

将查询字符串与第9.3节中创建的Get类组合在一起，创建一个方便的Get类，在第157页开始展示用很少的代码就可以组装的能力——Get和QueryString之间的代码不到50行。
```groovy
class Get{
  String url
  QueryString queryString = new QueryString()
  URLConnection connection
  String text
  
  String getText(){
    def thisUrl = new URL(this.toString())
    connection = thisUrl.openConnection()
    if(connection.responseCode == 200){
      return connection.content.text
    } else{
      return "Something bad happened\n" +
             "URL: " + this.toString() + "\n" +
             connection.responseCode + ": " +
             connection.responseMessage
    }
  }
  
  String toString(){
    return url + "?" + queryString.toString()
  }
}
def get = new Get(url:"http://search.yahoo.com/search")
get.queryString.add("n", 20)
get.queryString.add("vf", "pdf")
get.queryString.add("p", "groovy grails")

println get
===> http://search.yahoo.com/search?n=20&vf=pdf&p=groovy+grails

println get.text
===> <html><head>...
```

请注意，将您的queryString字段从String升级到完整的QueryString对象仅需要在两个位置接触Get类。 现在，该字段声明将创建一个新的`QueryString()`，并使用`toString()`方法调用`queryString.toString()`。 现在，此升级使您可以让Get类创建格式正确的QueryString，而不是强迫您自己创建一个。 诸如`get.queryString.add("p","groovy grails")`之类的调用在幕后做了正确的事情，确保值正确地进行了URL编码。

还记得我们在第161页的第9.4节“从映射中构建查询字符串”中讨论的查询字符串/哈希映射不匹配吗？ 因为QueryString类当前已实现，所以对`qs.add()`的每次调用都会替换名称/值对。 为了支持重复的命名元素，如果名称存在，则需要重构QueryString类以将值追加到List。 有关如何添加此功能的想法，请参阅第193页，第10.8节“调用不存在的方法（invokeMethod）”。

## 9.5 Making an HTTP POST Request {#发出HTTP POST请求}
```groovy
def url = new URL("http://search.yahoo.com/search")
def connection = url.openConnection()

//switch the method to POST (GET is the default)
connection.setRequestMethod("POST")

//write the data
def queryString = "n=20&vf=pdf&p=groovy+grails"
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(queryString)
writer.flush()
writer.close()
connection.connect()

//print the results
println connection.content.text
===> <html><head>...
```

发出HTTP POST请求时，不能在URL类上使用与进行GET请求时相同的`getText()`快捷方式。 您必须获取URLConnection，以便可以将请求方法设置为POST（默认为GET）。 对于GET请求，查询字符串将附加到URL对象的末尾。 相反，POST的查询字符串嵌入在请求的主体中。 为此，您必须做三件事：将URLConnection的doOutput值设置为true，获取outputStream，并在调用`connect()`之前向其中写入查询字符串。

**从列表构建查询字符串**
```groovy
def queryString = []
queryString << "n=" + URLEncoder.encode("20")
queryString << "vf=" + URLEncoder.encode("pdf")
queryString << "p=" + URLEncoder.encode("groovy grails")

def url = new URL("http://search.yahoo.com/search")
def connection = url.openConnection()
connection.setRequestMethod("POST")
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(queryString.join("&"))
writer.flush()
writer.close()
connection.connect()

def results = conn.content.text
```

正如在第160页的第9.4节“从列表中构建查询字符串”中所讨论的那样，从列表中构建查询字符串的秘诀是确保值经过URL编码，然后将元素与`&`连在一起。

**创建一个便利的Post类**
```groovy
class Post{
  String url
  QueryString queryString = new QueryString()
  URLConnection connection
  String text
  
  String getText(){
    def thisUrl = new URL(url)
    connection = thisUrl.openConnection()
    connection.setRequestMethod("POST")
    connection.doOutput = true
    Writer writer = new OutputStreamWriter(connection.outputStream)
    writer.write(queryString.toString())
    writer.flush()
    writer.close()
    connection.connect()
    return connection.content.text
  }
  
  String toString(){
    return "POST:\n" +
           url + "\n" +
    queryString.toString()
  }
}

def post = new Post(url:"http://search.yahoo.com/search")
post.queryString.add("n", 20)
post.queryString.add("vf", "pdf")
post.queryString.add("p", "groovy grails")

println post
===>
POST:
http://search.yahoo.com/search
n=20&vf=pdf&p=groovy+grails

println post.text
===> <html><head>...
```
将所有复杂的连接逻辑放到Post类中（与您在9.4节，创建便捷QueryString类，在第161页中创建的QueryString类相结合），可以提供非常引人注目的开发经验。

**模拟HTML表单进行测试**
```xml
<form method="post" action="http://localhost:8888/jaw/controller">
  <input type="hidden" name="action" value="saveCar" />
  Make: <input type="text" name="make" value="" /></td>
  Model: <input type="text" name="model" value="" /></td>
  Year: <input type="text" name="modelYear" value="" /></td>
  <input type="submit" name="save" value="Save" />
</form>
```

现在您有了Post类，您可以轻松地使用代码来模拟HTML表单提交。 使用此HTML表单，您可以使用以下代码模拟用户填写表单并单击Submit按钮：
```groovy
def post = new Post(url:"http://localhost:8888/jaw/controller")
post.queryString.add("action", "saveCar")
post.queryString.add("make", "Toyota")
post.queryString.add("model", "Prius")
post.queryString.add("modelYear", 2012)
println post.text
```
此时剩下要做的就是在`post.text`上写断言，以验证表单提交是否正确执行。

**使用XML的RESTful POST请求**
```groovy
def xml = """<car>
  <make>Toyota</make>
  <model-year>2012</model-year>
  <model>Prius</model>
</car>"""

def url = new URL("http://localhost:8888/jaw/car")
def connection = url.openConnection()

//set the metadata
connection.setRequestMethod("POST")
connection.setRequestProperty("Content-Type","application/xml")

//write the data
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(xml)
writer.flush()
writer.close()
connection.connect()
def results = connection.content.text
```

在RESTful Web服务中，用于请求的HTTP动词具有深层的语义。 常见的数据库隐喻-创建，检索，更新，删除（CRUD）-同样适用于RESTful应用程序[^907]， 尽管SQL语句中使用的动词并不相同。 您对数据库执行的SELECT类似于HTTP GET。 您将记录插入表中，类似于将表单数据POST到网站中。 HTTP PUT等效于数据库UPDATE。 DELETE是最令人惊讶的，它在SQL和HTTP中具有相同的含义。

RESTful Web服务通常期望POST主体中包含XML，而不是到目前为止展示的查询字符串。 要传递XML，您需要对代码进行两个小的更改。 首先，您很可能需要将`Content-Type`从`application/www-form-urlencoded`（POST的默认设置）更改为`application/xml`。 （确切的Content-Type取决于您正在调用的RESTful服务。）您需要做的另一件事不是对数据进行URL编码, XML有效负载应以其本机格式传输。 有关POST XML的另一个示例，请参见第9.10节，发出SOAP请求，第172页。

## 9.6 Making an HTTP PUT Request {#发出HTTP PUT请求}
```groovy
def xml = """<car id="142">
  <make>Toyota</make>
  <model-year>2012</model-year>
  <model>Prius, Luxury Edition</model>
</car>"""

def url = new URL("http://localhost:8888/jaw/car/142")
def connection = url.openConnection()
connection.setRequestMethod("PUT")
connection.setRequestProperty("Content-Type","application/xml")
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(xml)
writer.flush()
writer.close()
connection.connect()

def result = connection.content.text
```

执行HTTP PUT在语法上与执行POST相同，但有一个例外-`connection.setRequestMethod("PUT")`。 如上一节中的第9.5节“使用XML进行RESTful POST请求”中所述，PUT在语义上是UPDATE，而POST等效于SQL INSERT。 本示例将模型描述更新为包括“豪华版”。

**创建一个方便的Put类**
```groovy
class Put{
  String url
  String body
  String contentType = "application/xml"
  URLConnection connection
  String text

  String getText(){
    def thisUrl = new URL(url)
    connection = thisUrl.openConnection()
    connection.setRequestMethod("PUT")
    connection.setRequestProperty("Content-Type", contentType)
    connection.doOutput = true
    Writer writer = new OutputStreamWriter(connection.outputStream)
    writer.write(body)
    writer.flush()
    writer.close()
    connection.connect()
    return connection.content.text
  }
  
  String toString(){
    return "PUT:\n" +
            contentType + "\n" +
            url + "\n" +
            body
  }
}

def xml = """<car id="142">
  <make>Toyota</make>
  <model-year>2012</model-year>
  <model>Prius, Luxury Edition</model>
</car>"""

def put = new Put(url:"http://localhost:8888/jaw/car/142")
put.body = xml
println put
===>
PUT:
application/xml
http://localhost:8888/jaw/car/142
<car id="142">
  <make>Toyota</make>
  <model-year>2012</model-year>
  <model>Prius, Luxury Edition</model>
</car>
def result = put.text
```
Put类与Post类几乎相同，但具有三个区别。 将queryString字段交换为普通的String字段。 另外，您公开一个contentType字段，以便您可以根据需要进行更改。 最后，将requestMethod设置为PUT。

## 9.7 Making an HTTP DELETE Request {#发出HTTP DELETE请求}
```
def url = new URL("http://localhost:8888/jaw/car/142")
def connection = url.openConnection()
connection.setRequestMethod("DELETE")
connection.connect()
def result = connection.content.text
```
执行HTTP DELETE在语法上与执行GET相同，但有一个例外-`connection.setRequestMethod("DELETE")`。 POST和PUT请求的正文中有数据，而GET和DELETE（以及HEAD，OPTION和其他HTTP动词）只有一个URL。 如第166页的第9.5节“使用XML进行RESTful POST请求”中所讨论的，DELETE完全按照您的期望执行操作-有效地从id=142的汽车中删除。

**创建一个方便的Delete类**
```groovy
class Delete{
  String url
  QueryString queryString = new QueryString()
  URLConnection connection
  String text
  
  String getText(){
    def thisUrl = new URL(this.toString())
    connection = thisUrl.openConnection()
    connection.setRequestMethod("DELETE")
    if(connection.responseCode == 200){
      return connection.content.text
    } else{
      return "Something bad happened\n" +
             "URL: " + this.toString() + "\n" +
             connection.responseCode + ": " +
             connection.responseMessage
    }
  }
  
  String toString(){
    return "DELETE:\n" +
    url + "?" + queryString.toString()
  }
}

def delete = new Delete(url:"http://localhost:8888/jaw/car/142")
println delete
===>
DELETE:
http://localhost:8888/jaw/car/142
def results = delete.text
```
Delete类与Get类几乎相同，但有一个区别:requestMethod设置为DELETE。

## 9.8 Making a RESTful Request {#发出RESTful请求}
```groovy
def partialRestRequest = "http://geocoder.us/service/rest/geocode?address="
def address = "1600 Pennsylvania Ave, Washington DC"
def restUrl = new URL(partialRestRequest + URLEncoder.encode(address))
def restResponse = restUrl.text
```
此请求以XML格式返回白宫的纬度/经度。 随意替换您自己的地址。 您可以通过访问`http://geocoder.us`或在任何主要地图网站的搜索框中键入坐标对来查看地图上的返回点。

**解析RESTful响应**
```groovy
//Response:
<rdf:RDF
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
  <geo:Point rdf:nodeID="aid76408515">
    <dc:description>
      1600 Pennsylvania Ave NW, Washington DC 20502
    </dc:description>
    <geo:long>-77.037684</geo:long>
    <geo:lat>38.898748</geo:lat>
  </geo:Point>
</rdf:RDF>

def restResponse = restUrl.text
def RDF = new XmlSlurper().parseText(restResponse)
println RDF.Point.description
println RDF.Point.long
println RDF.Point.lat
```

XmlSlurper允许您避免处理名称空间并提取相关字段。 有关更多信息，请参见第7.9节“使用命名空间解析XML文档”。

::: alert-info
**Web服务案例研究：Geocoder.us**
如果您曾经使用过Google Maps*， Yahoo! 地图†，MapQuest‡，Microsoft LiveSearch§或任何其他地图网站，您一直在使用Web服务，甚至没有意识到它。 您键入站点的地址（例如，123 Main St.）本身并不是可映射的。 要在地图上绘制地址，必须将街道地址转换为纬度/经度点。 进行这种转换的Web服务类型称为地址解析器。

所有主要的地图网站都提供地理编码API，但是有一个独立的地理编码网站在本章的一些示例中起着重要作用。Geocoder.us¶可以很好地用作上一页第9.8节“发出RESTful请求”的草料； 第9.9节“发出CSV请求”； 下一页上的第9.10节，发出SOAP请求； 以及第9.1页的发出XML-RPC请求，第174页。这是因为它使您可以使用各种不同的Web服务方言进行相同的基本查询。 Geocoder.us是一项基于美国人口普查局提供的免费数据的非商业性免费服务。

在撰写本文时，Geocoder.us还不支持其他几种格式-RSS和Atom。 您可以访问AboutGroovy.com，以获取有关它们的真实示例，但是如果Geocoder.us有时将这些格式添加到组合中，请不要感到惊讶。 GeoRSS和GeoAtom都存在并且越来越流行。 例如，Flickr，k在每个搜索结果页面的底部都提供了GeoRSS提要。

∗. http://maps.google.com
†. http://maps.yahoo.com
‡. http://www.mapquest.com
§. http://maps.live.com/
¶. http://geocoder.us
k. http://flickr.com
:::

## 9.9 Making a CSV Request {#发出CSV请求}
```groovy
def partialCsvRequest = "http://geocoder.us/service/csv/geocode?address="
def address = "1600 Pennsylvania Ave, Washington DC"
def csvUrl = new URL(partialCsvRequest + URLEncoder.encode(address))
def csvResponse = csvUrl.text
```
此请求以CSV格式返回白宫的纬度/经度。 随意替换您自己的地址。 您可以通过访问`http://geocoder.us`或在任何主要地图网站的搜索框中键入坐标对来查看地图上的返回点。

**解析CSV响应**
```groovy
//Response:
38.898748,-77.037684,1600 Pennsylvania Ave NW,Washington,DC,20502
39.
def csvResponse = csvUrl.text
def tokens = csvResponse.split(",")
println "Latitude: [${tokens[0]}]"
println "Longitude: [${tokens[1]}]"
println "Address: [${tokens[2]}]"
println "City: [${tokens[3]}]"
println "State: [${tokens[4]}]"
println "Zip: [${tokens[5]}]"
```
在结果字符串上调用`split(",")`可让您轻松访问各个字段。 有关解析CSV的更多信息，请参阅第148页，第8.14节“将CSV转换为XML”。

## 9.10 Making a SOAP Request {#发出SOAP请求}
```groovy
def address = "1600 Pennsylvania Av, Washington, DC"
def soapRequest = """<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance"
  xmlns:xsd="http://www.w3.org/1999/XMLSchema"
  xmlns:tns="http://rpc.geocoder.us/Geo/Coder/US/">
  <SOAP-ENV:Body>
    <tns:geocode
      SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
        <location xsi:type="xsd:string">${address}</location>
    </tns:geocode>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"""

def soapUrl = new URL("http://geocoder.us/service/soap")
def connection = soapUrl.openConnection()

connection.setRequestMethod("POST")
connection.setRequestProperty("Content-Type","application/xml")
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(soapRequest)
writer.flush()
writer.close()
connection.connect()
def soapResponse = connection.content.text
```
这将白宫的纬度/经度作为SOAP返回。 随意替换您自己的地址。 您可以通过访问`http://geocoder.us`或在任何主要地图网站的搜索框中键入坐标对来查看地图上的返回点。

您在这里看到的是通过直接发布SOAP信封来发出原始SOAP请求的方法。 您可以在`http://geocoder.us/dist/eg/clients/GeoCoder.wsdl`中找到此服务的WSDL文档。 拥有WSDL之后，您始终可以使用大多数SOAP框架附带的任何标准`wsdl2java/java2wsdl`实用程序。

**解析SOAP响应**
```groovy
//Response:
<?xml version="1.0" encoding="utf-8"?>
<SOAP-ENV:Envelope xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/1999/XMLSchema" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <SOAP-ENV:Body>
    <namesp9:geocodeResponse xmlns:namesp9="http://rpc.geocoder.us/Geo/Coder/US/">
      <geo:s-gensym111 xsi:type="SOAP-ENC:Array" xmlns:geo="http://rpc.geocoder.us/Geo/Coder/US/" SOAP-ENC:arrayType="geo:GeocoderAddressResult[1]">
        <item xsi:type="geo:GeocoderAddressResult">
          <number xsi:type="xsd:int">1600</number>
          <lat xsi:type="xsd:float">38.898748</lat>
          <street xsi:type="xsd:string">Pennsylvania</street>
          <state xsi:type="xsd:string">DC</state>
          <city xsi:type="xsd:string">Washington</city>
          <zip xsi:type="xsd:int">20502</zip>
          <suffix xsi:type="xsd:string">NW</suffix>
          <long xsi:type="xsd:float">-77.037684</long>
          <type xsi:type="xsd:string">Ave</type>
          <prefix xsi:type="xsd:string"/>
        </item>
      </geo:s-gensym111>
    </namesp9:geocodeResponse>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

def soapResponse = connection.content.text
def Envelope = new XmlSlurper().parseText(soapResponse)
println Envelope.Body.geocodeResponse.'s-gensym111'.item.long
println Envelope.Body.geocodeResponse.'s-gensym111'.item.lat

//since the array's name ('s-gensym111') changes with each request
// we can deal with it generically as such:
def itor = Envelope.Body.geocodeResponse.breadthFirst()
while(itor.hasNext()){
  def fragment = itor.next()
  if(fragment.name() == "item"){
    println fragment.lat
    println fragment.long
  }
}
```

XmlSlurper允许您避免处理名称空间并提取相关字段。 有关更多信息，请参见第7.9节“使用命名空间解析XML文档”。

Geocoder.us的SOAP接口有点不典型。 geocodeResponse的名称空间和其中的array元素的元素名称因响应而异。 这样就不可能将GPath硬编码到lat和long的深层元素上。 在我处理过的所有其他基于SOAP的Web服务中，元素名称和名称空间都很稳定，很少更改。

尽管有这些bug，我还是决定继续使用这个站点作为SOAP示例。能够用四种不同的方言对相同的服务提出相同的请求，再加上能够展示如何灵活地处理响应异常，这是一个意想不到的“好处”，这让人难以拒绝。因为我试图向您展示的是客户端代码，而不是规范的服务器端SOAP示例，所以我认为您可以忽略一两个障碍。

## 9.11 Making an XML-RPC Request {#发出XML-RPC请求}
```groovy
def address = "1600 Pennsylvania Av, Washington, DC"
def xmlrpcRequest = """<methodCall>
<methodName>geocode</methodName>
<params>
<param>
<value><string>${address}</string></value>
</param>
</params>
</methodCall>"""

def xmlrpcUrl = new URL("http://geocoder.us/service/xmlrpc")
def connection = xmlrpcUrl.openConnection()
connection.setRequestMethod("POST")
connection.setRequestProperty("Content-Type","application/xml")
connection.doOutput = true
Writer writer = new OutputStreamWriter(connection.outputStream)
writer.write(xmlrpcRequest)
writer.flush()
writer.close()
connection.connect()

def xmlrpcResponse = connection.content.text
```
该请求以XML-RPC的形式返回白宫的纬度/经度。 随意替换您自己的地址。 您可以通过访问`http://geocoder.us`或在任何主要地图网站的搜索框中键入坐标对来查看地图上的返回点。

**Parsing an XML-RPC Response**
```groovy
//Response:
<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value>
              <struct>
                <member>
                  <name>number</name>
                  <value>
                    <int>1600</int>
                  </value>
                </member>
                <member>
                  <name>lat</name>
                  <value>
                    <double>38.898748</double>
                  </value>
                </member>
                <member>
                  <name>street</name>
                  <value>
                    <string>Pennsylvania</string>
                  </value>
                </member>
                <member>
                  <name>state</name>
                  <value>
                    <string>DC</string>
                  </value>
                </member>
                <member>
                  <name>city</name>
                  <value>
                    <string>Washington</string>
                  </value>
                </member>
                <member>
                  <name>zip</name>
                  <value>
                    <int>20502</int>
                  </value>
                </member>
                <member>
                  <name>suffix</name>
                  <value>
                    <string>NW</string>
                  </value>
                </member>
                <member>
                  <name>long</name>
                  <value>
                    <double>-77.037684</double>
                  </value>
                </member>
                <member>
                  <name>type</name>
                  <value>
                    <string>Ave</string>
                  </value>
                </member>
                <member>
                  <name>prefix</name>
                  <value>
                    <string/>
                  </value>
                </member>
              </struct>
            </value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>


def xmlrpcResponse = connection.content.text
def methodResponse = new XmlSlurper().parseText(xmlrpcResponse)
methodResponse.params.param.value.array.data.value.struct.member.each{member ->
  if(member.name == "lat" || member.name == "long"){
    println "${member.name}: ${member.value.double}"
  }
}
```
XmlSlurper允许您避免处理名称空间并提取相关字段。 有关更多信息，请参见第7.9节“使用命名空间解析XML文档”。 尽管嵌套响应的深度几乎是可笑的（您的目标经纬度和长元素深度为11个级别），但您仍可以轻松地找到它们并打印结果。

## 9.12 Parsing Yahoo Search Results as XML {#将Yahoo搜索结果解析为XML}
```groovy
def yahooAddress = "http://search.yahooapis.com/WebSearchService/V1/webSearch?"
def queryString = "appid=YahooDemo&query=groovy&results=10"
def xmlResponse = "${yahooAddress}${queryString}".toURL().text
```
如第159页的第9.3节“ RESTful GET请求”中所述，Yahoo提供了一个RESTful API，该API以XML而不是通常的HTML格式返回搜索结果。 您可以通过简单地调整查询字符串上的名称/值对，以多种方式调整查询。

**解析XML Yahoo搜索结果**
```groovy
//Response:
<ResultSet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:yahoo:srch" xsi:schemaLocation="urn:yahoo:srch
http://api.search.yahoo.com/WebSearchService/V1/WebSearchResponse.xsd" type="web" totalResultsAvailable="20700000" totalResultsReturned="10" firstResultPosition="1" moreSearch="/WebSearchService/V1/webSearch?query=groovy&amp;appid=YahooDemo">
  <Result>
    <Title>Groovy - Home</Title>
    <Summary>Groovy ... </Summary>
    <Url>http://groovy.codehaus.org/</Url>
    <ClickUrl>http://uk.wrs.yahoo.com/</ClickUrl>
    <DisplayUrl>groovy.codehaus.org/</DisplayUrl>
    <ModificationDate>1191394800</ModificationDate>
    <MimeType>text/html</MimeType>
    <Cache>
      <Url>http://uk.wrs.yahoo.com/</Url>
      <Size>39661</Size>
    </Cache>
  </Result>
</ResultSet>

def ResultSet = new XmlSlurper().parseText(xmlResponse)
ResultSet.Result.each{
  println it.Title
  println it.Url
  println "-----"
}
===>
Groovy - Home
http://groovy.codehaus.org/
-----
Groovy - Wikipedia, the free encyclopedia
http://en.wikipedia.org/wiki/Groovy
-----
...
```
XmlSlurper允许您避免处理名称空间并提取相关字段。 有关更多信息，请参见第7.9页的第7.9节“使用命名空间解析XML文档”。

##9.13 Parsing an Atom Feed {#解析Atom Feed}
```
def atom = "http://aboutgroovy.com/item/atom".toURL().text
```
为`AboutGroovy.com`获取Atom[^908]联合feed是很简单的。由于它是一个简单的HTTP GET，甚至不需要查询字符串，与我们必须经历的发布SOAP请求的英勇步骤相比，它似乎有点虎头蛇尾。
```groovy
//Response:
<feed xmlns="http://www.w3.org/2005/Atom">
  <title type="text">aboutGroovy.com</title>
  <link rel="alternate" type="text/html" href="http://aboutGroovy.com"/>
  <link rel="self" type="application/atom+xml" href="http://aboutGroovy.com/item/atom"/>
  <updated>2007-10-10T13:15:23-07:00</updated>
  <author>
    <name>Scott Davis</name>
  </author>
  <id>tag:aboutgroovy.com,2006-12-18:thisIsUnique</id>
  <generator uri="http://aboutGroovy.com" version="0.0.2">
Hand-rolled Grails code</generator>
  <entry xmlns='http://www.w3.org/2005/Atom'>
    <author>
      <name>Scott Davis</name>
    </author>
    <published>2007-10-10T10:44:48-07:00</published>
    <updated>2007-10-10T10:44:48-07:00</updated>
    <link href='http://aboutGroovy.com/item/show/258' rel='alternate' title='G2One, Inc. -- Professional Support for Groovy and Grails' type='text/html'/>
    <id>tag:aboutgroovy.com,2006:/item/show/258</id>
    <title type='text'>
G2One, Inc. -- Professional Support for Groovy and Grails
</title>
    <content type='xhtml'>
      <div xmlns='http://www.w3.org/1999/xhtml'>
        <p>Category: news</p>
        <p>
          <a href='http://www.g2one.com/'>Original Source</a>
        </p>
        <p>Groovy and Grails now have a corporate home -- G2One. The project
leads for both Groovy and Grails (Guillaume Laforge and Graeme
Rocher) have joined forces with Alex Tkachman (until recently
with JetBrains) to form a new company.</p>
      </div>
    </content>
  </entry>
</feed>


def feed = new XmlSlurper().parseText(atom)
feed.entry.each{
  println it.title
  println it.published
  println "-----"
}

===>
SAP Adds Groovy/Grails Support
2007-10-10T10:52:21-07:00
-----
G2One, Inc. -- Professional Support for Groovy and Grails
2007-10-10T10:44:48-07:00
-----
...
```
XmlSlurper允许您避免处理名称空间并提取相关字段。有关更多信息，请参见第132页第7.9节“使用名称空间解析XML文档”。

Atom是REST的一种实现，它已经超越了简单的Blogsphere联合组织而受到欢迎。 Google于2006年12月正式弃用了其SOAP API。根据GData [^909]的倡议，它将所有Web服务迁移到Atom。 有关完全RESTful API（包含使用HTTP GET，POST，PUT和DELETE进行身份验证和完整CRUD的示例）的充分记录的示例，请参阅Google Calendar API。

有关如何创建Atom提要的信息，请参见第12.4节，设置Atom提要，见第239页。

## 9.14 Parsing an RSS Feed {#解析RSS Feed}
```groovy
def rssFeed = "http://aboutgroovy.com/podcast/rss".toURL().text
```
获取RSS feed就像发出普通的旧的HTTP GET请求一样简单。
```groovy
//Response:
<rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" version="2.0">
  <channel>
    <title>About Groovy Podcasts</title>
    <link>http://aboutGroovy.com</link>
    <language>en-us</language>
    <copyright>2007 AboutGroovy.com</copyright>
    <itunes:subtitle>
Your source for the very latest Groovy and Grails news
</itunes:subtitle>
    <itunes:author>Scott Davis</itunes:author>
    <itunes:summary>About Groovy interviews</itunes:summary>
    <description>About Groovy interviews</description>
    <itunes:owner>
      <itunes:name>Scott Davis</itunes:name>
      <itunes:email>scott@aboutGroovy.com</itunes:email>
    </itunes:owner>
    <itunes:image href="http://aboutgroovy.com/images/aboutGroovy3.png"/>
    <itunes:category text="Technology"/>
    <itunes:category text="Java"/>
    <itunes:category text="Groovy"/>
    <itunes:category text="Grails"/>
    <item>
      <title>AboutGroovy Interviews Neal Ford</title>
      <itunes:author>Scott Davis</itunes:author>
      <itunes:subtitle></itunes:subtitle>
      <itunes:summary>Neal Ford of ThoughtWorks is truly a polyglot programmer.
In this exclusive interview, Neal opines on Groovy, Ruby, Java, DSLs, and the future of programming languages. Opinionated and entertaining, Neal
doesn't pull any punches. Enjoy.
</itunes:summary>
      <enclosure url="http://aboutgroovy.com/podcasts/NealFord.mp3" length="33720522" type="audio/mpeg"/>
      <guid>http://aboutgroovy.com/podcasts/NealFord.mp3</guid>
      <pubDate>2007-04-17T01:15:00-07:00</pubDate>
      <itunes:duration>44:19</itunes:duration>
      <itunes:keywords>java,groovy,grails</itunes:keywords>
    </item>
  </channel>
</rss>

def rss = new XmlSlurper().parseText(rssFeed)
rss.channel.item.each{
  println it.title
  println it.pubDate
  println it.enclosure.@url
  println it.duration
  println "-----"
}

===>
AboutGroovy Interviews Neal Ford
2007-04-17T01:15:00-07:00
http://aboutgroovy.com/podcasts/NealFord.mp3
44:19
-----
AboutGroovy Interviews Jeremy Rayner
2007-03-13T01:18:00-07:00
http://aboutgroovy.com/podcasts/JeremyRayner.mp3
50:54
-----
...
```

XmlSlurper允许您避免处理名称空间并提取相关字段。 有关更多信息，请参见第7.9节“使用命名空间解析XML文档”。

雅虎有许多RSS源，它们提供的不仅是简单的博客联合组织。 有关通过有线发送真实数据的RSS供稿的几个示例，请参见`http://developer.yahoo.com/weather/`以及`http://developer.yahoo.com/traffic/`。

# 第10章 Metaprogramming {#元编程}
元编程[^1001]是编写能够在运行时动态改变其行为的代码。(我希望这个类现在就有那个方法。)它为您的代码提供了流动性和灵活性，如果您精通C或Java之类的静态编程语言，那么您的代码可能会非常陌生。像Smalltalk和Ruby这样的动态语言就有这种功能，现在Groovy允许您在Java环境中做相同的事情。

没有反射[^1002]的补充概念，即在运行时以编程方式询问自己的能力，没有一种自重的动态语言会是完整的。(此类具有哪些字段？它将响应什么方法？)尽管在Java中可以通过使用Reflection API做到这一点，但实际上很少使用它。有人可能会争辩说，这些概念在静态类型的语言中比在动态类型的语言中没有那么重要—毕竟，一旦用Java定义了接口，为什么要以编程方式询问该接口定义了哪些方法？ 您已经知道先验问题的答案，并且在Java中，接口永远不会改变。 （多态性基于此概念。）

在第41页的第3章，对Groovy入门中，我们讨论了Java语言的有趣附加组件。 大多数开发人员已经知道`java.util.ArrayList`是什么，因此指出其他一些很棒的新方法是一种以新方式使用熟悉的类的练习。除非您已经使用Java中的Reflection API进行操作，或者除非习惯通过`Class.forName()`实例化所有类，否则本章中的想法可能会朝着一个新的方向延伸。 （为什么您应该以编程方式询问此类是否具有字段或对特定方法作出响应，这不是您的编译器为您做的事情吗？）

本章介绍了以编程方式询问您的类有哪些字段和方法的方法。 我们还将研究如何在运行时通过MetaClass类动态添加新的字段和方法。 我们将讨论使用`invokeMethod()`不存在的调用方法。 甚至有称为Expandos的对象在运行时完全创建。 尽享Groovy风格的元编程。

## 10.1 Discovering the Class {#发现类}
```groovy
def s = "Hello"
println s.class
===> java.lang.String
```
Java中的每个对象都有一个`getClass()`方法。在Groovy中，可以缩短对类的调用。(更多信息见第72页4.2节，Getter和Setter快捷语法。)

注意，在本例中，您使用duck类型来声明变量s-def，而不是String。尽管如此，当被请求时，变量正确地将自己标识为字符串。(有关更多信息，请参见第3.5节，可选数据类型声明(Duck Typing)，见第47页。)

一旦有了这样的class，你就可以问各种有趣的问题。请注意，所有这些都可以通过乏味的旧类`java.lang.Class`获得。Groovy只是为迭代添加了`each()`语法糖，以及默认的it变量。(有关更多信息，请参见第3.14节，List快捷方式，见第58页。)
```groovy
String.constructors.each{println it}
===>
public java.lang.String()
public java.lang.String(char[])
public java.lang.String(byte[])
public java.lang.String(java.lang.StringBuffer)
public java.lang.String(java.lang.StringBuilder)
public java.lang.String(java.lang.String)
...

String.interfaces.each{println it}
===>
interface java.io.Serializable
interface java.lang.Comparable
interface java.lang.CharSequence
```

## 10.2 Discovering the Fields of a Class {#发现类的字段}
```groovy
def d = new Date()
println d.properties
===> {month=8, day=6, calendarDate=2007-09-01T08:38:55.348-0600,
time=1188657535348, timeImpl=1188657535348, class=class java.util.Date,
timezoneOffset=360, date=1, hours=8, minutes=38, year=107,
julianCalendar=sun.util.calendar.JulianCalendar@d085f8, seconds=55}
```
在类上调用`getProperties()`将返回所有字段的`java.util.HashMap`。对于稍微漂亮一些的输出，您可以调用HashMap上的`each()`。(还记得吗，它是默认的迭代器变量，正如我们在第59页3.14节“迭代”中讨论的那样。)
```groovy
d.properties.each{println it}
===>
month=8
day=6
calendarDate=2007-09-01T08:38:55.348-0600
time=1188657535348
timeImpl=1188657535348
class=class java.util.Date
timezoneOffset=360
date=1
hours=8
minutes=38
year=107
julianCalendar=sun.util.calendar.JulianCalendar@d085f8
seconds=55
```
Java为您提供了一种实现几乎相同功能的方法。每个`java.lang.Class`提供一个`getDeclaredFields()`方法，该方法返回一个`java.lang.reflect.Field`对象的数组。
```groovy
d.class.declaredFields.each{println it}
===>
private static final sun.util.calendar.BaseCalendar java.util.Date.gcal
private static sun.util.calendar.BaseCalendar java.util.Date.jcal
private transient long java.util.Date.fastTime
private transient sun.util.calendar.BaseCalendar$Date java.util.Date.cdate
private static int java.util.Date.defaultCenturyStart
private static final long java.util.Date.serialVersionUID
private static final java.lang.String[] java.util.Date.wtb
private static final int[] java.util.Date.ttb
```
等一下…为什么`getProperties`调用不匹配`getDeclaredFields`调用?也许后一种方法上的Javadocs[^1003]可以解释这个问题:“`getDeclaredFields()`返回一个字段对象数组，它反映了这个类对象所声明的所有字段或这个类对象所表示的接口。这包括公共字段、受保护字段、默认(包)访问字段和私有字段，但不包括继承字段。”

虽然Java方法在技术上更加正确—字段month、day和year在技术上是内部类的一部分—Groovy方法`getProperties`只是获取类上的getter和setter。尽管它们并不是Date对象的真正字段，但API设计人员似乎希望您将对象视为具有这些字段的对象。这里提供了这两种方法，以便您可以选择最适合您需要的方法。

**Groovy的MetaClass字段**
```groovy
class Person{
  String firstname
  String lastname
}

def p = new Person(firstname:"John", lastname:"Smith")
p.properties.each{println it}
===>
firstname=John
lastname=Smith
class=class Person
metaClass=groovy.lang.MetaClassImpl@ebd7c4[class Person]
```
对Java类调用`getProperties()`将返回您所期望的字段数。在Groovy中，出现了一个更有趣的值得注意的领域: `metaClass(元类)`。

看到firstname和lastname 出现在列表中，你不应该感到惊讶。您可能不希望在列表中看到class，但是回想一下前面的部分，`getProperties()`返回对象上的所有继承字段，而不仅仅是您定义的字段。由于Person扩展了`java.lang.Object`，所以可以看到`getClass()`方法出现在这里，就好像它是Person类上的一个字段。

它是使Groovy与众不同的最后一个意外字段`MetaClass`。所有Groovy类都实现了`Groovy.lang. Groovyobject`接口。这个接口上的`getMetaClass()`方法负责将最后一个未预料到的字段带到参与方。

`MetaClass`是使Groovy成为动态语言的原因。它允许在运行时而不是编译时向类添加新字段和方法。它允许您将`execute()`和`toURL()`这样的方法添加到`java.lang.String`中，即使它是`Final class`。

从下一节开始—10.3节，检查字段的存在—并继续本章的其余部分，`MetaClass`的力量将慢慢展开。你会看到怎么把它弄到这里。我们将在本章的其余部分讨论如何使用它。

**MetaClasses for Java Classes**
```groovy
// in Groovy 1.0
GroovySystem.metaClassRegistry.getMetaClass(Date)
// in Groovy 1.5
Date.metaClass
```

Groovy 1.0中的Java对象不容易公开MetaClass，但它们都有一个MetaClass。要找到它，您必须查询JDK类的MetaClassRegistry。在Groovy 1.5中，这个过程被极大地简化了——您只需直接向类请求它的MetaClass。

## 10.3 Checking for the Existence of a Field {#检查字段是否存在}
```groovy
class Person{
  String firstname
  String lastname
}

def p = new Person()
if(p.metaClass.hasProperty(p, "firstname")){
  p.firstname = "Jane"
}
println p.firstname
===> Jane

p.last = "Doe"
ERROR: groovy.lang.MissingPropertyException: No such property:
last for class: Person
```
每个`java.lang.Class`都有一个`getField()`方法，该方法返回存在的字段。如果调用失败，则抛出一个`java.lang.NoSuchFieldException`。通过在调用之前查询类，Groovy允许您更安全一点。在MetaClass上调用`hasProperty()`方法，如果它存在，则返回字段;如果不存在，则返回null。

正如第54页第3.10节Groovy Truth中所讨论的，null响应的计算结果为false，这使您可以既谨慎又动态。这种技术正是JavaScript开发人员多年来一直在做的，以确保他们的代码能够跨不同的浏览器工作。

**Groovy 1.0 解决方案**

```groovy
if(p.properties.containsKey("firstname")){
  p.firstname = "Jane"
}
```
`hasProperty()`方法出现在Groovy 1.5中。在Groovy 1.0中，可以使用`getProperties()`方法返回的HashMap上的`containsKey()`方法有效地进行相同的检查。

**你什么时候用这个?**
```groovy
// url to test this code:
http://localhost:8080/groovlets/person.groovy?
firstname=Scott&lastname=Davis&title=Bench+Warmer

// person.groovy
class Person{
  String firstname
  String lastname
  String toString(){"${firstname} ${lastname}"}
}

def person = new Person()
request.parameterMap.each{name, value->
  if(person.metaClass.hasProperty(person, name)){
    person.setProperty(name, value[0])
  }
}

println "QueryString: ${request.queryString}"
println "<br/>"
println "Incoming parameters: ${request.parameterMap}"
println "<br/>"
println "Resulting Person: ${person}"
```

动态地确定类具有哪些字段对于动态地填充类非常有帮助。例如，下面是一个简单的Groovlet，它根据通过查询字符串传入的`名称/值`对填充类。(在图10.1中，在下面的页面中，您可以看到在浏览器中呈现的结果。)

这是每个现有web框架都必须解决的问题。但是，即使您不进行web开发，这种技术也同样方便。无论何时，当您从XML、CSV、hashmap或其他任何地方动态地填充POGO时，您都应该礼貌地询问POGO是否能够处理数据流，而不是粗暴地强行将其咽下去。

**Figure 10.1: 展示hasProperty()值的Groovlet**
![Figure 10.1: A Groovlet demonstrating the value of hasProperty()](_v_images/20191017131550135_28206.png)

首先，定义Person类并实例化它。接下来，逐个遍历QueryString值。根据示例中的URL，您应该在查询字符串中找到firstname、lastname和title条目。 如果您只是遍历键列表并在person上愉快地调用`setProperty()`，那么当您到达title时就会发生糟糕的事情，因为person没有title字段。(具体来说，Groovy会报错一个Groovy.lang.missingpropertyexception。) 在`hasProperty()`检查中包装`setProperty()`调用可以确保只有知道如何处理的字段被注入。所有不匹配的字段都会被简单地丢弃。 

如果你想让代码更漂亮一点，你可以在person类上添加一个`hasProperty()`便利方法:
```groovy
// person.groovy
class Person{
  String firstname
  String lastname
  
  String toString(){"${firstname} ${lastname}" }
  
  MetaProperty hasProperty(String property){
    return this.metaClass.hasProperty(this, property)
  }
}

def person = new Person()

request.parameterMap.each{name, value->
  if(person.hasProperty(name)){
    person.setProperty(name, value[0])
  }
}

println "QueryString: ${request.queryString}"
println "<br/>"
println "Incoming parameters: ${request.parameterMap}"
println "<br/>"
println "Resulting Person: ${person}"
```

有关groovlet的更多信息，请参见第2.6节，在Web服务器上运行Groovy (groovlet)，见第33页。有关查询字符串的更多信息，请参阅第159页关于处理查询字符串的9.4节。

## 10.4 Discovering the Methods of a Class {#发现类的方法}
```groovy
def d = new Date()
d.class.methods.each{println it}
===>
...
public void java.util.Date.setTime(long)
public long java.util.Date.getTime()
public int java.util.Date.getYear()
public int java.util.Date.getMonth()
public int java.util.Date.getDate()
public int java.util.Date.getHours()
public int java.util.Date.getMinutes()
public int java.util.Date.getSeconds()
...
```

每个类都有一个`getMethods()`方法。遍历这个列表与遍历字段没有什么不同，就像我们在10.2节中讨论的那样，在183页上查找类的字段。

如果只显示方法名称，则可以稍微简化一下列表：
```groovy
d.class.methods.name

===>
[hashCode, compareTo, compareTo, equals, toString, clone, parse,
after, before, setTime, getTime, getYear, getMonth, getDate, getHours,
getMinutes, getSeconds, UTC, setYear, setMonth, setDate, getDay, setHours,
setMinutes, setSeconds, toLocaleString, toGMTString, getTimezoneOffset,
getClass, wait, wait, wait, notify, notifyAll]
```

**使用Evaluate动态调用类的方法**
```groovy
def d = new Date()

d.class.methods.each{method ->
  if(method.name.startsWith("get")){
    print "${method.name}: "
    evaluate("dd = new Date(); println dd.${method.name}()" )
  }
}
===>
getTime: 1188665901916
getYear: 107
getMonth: 8
getDate: 1
getHours: 10
getMinutes: 58
getSeconds: 21
getDay: 6
getTimezoneOffset: 360
getClass: class java.util.Date
```

在第95页的5.10节，Evaluating字符串中，我们讨论了通过Evaluating任意字符串来运行Groovy代码。 如果要遍历Date对象上的所有方法并动态执行所有`getter`，该怎么办？ 这个例子可以解决问题。

尽管此代码可以按预期工作，但您是否在评估语句中注意到我快速拉给您的代码？这里有多个Date实例:您遍历其方法的d实例，以及在循环中每次实例化的单独dd。必须这样做，因为每个`evaluate`都创建了自己的`groovy.lang.GroovyShell`，不幸的是，它看不到d变量。如果您尝试调用`d.${method.name}()`，您将得到一个错误消息:
```groovy
Caught: groovy.lang.MissingPropertyException:
No such property: d for class: Script1
```

Script1是由`evaluate`调用创建的匿名脚本。

解决此问题的第二种方法是重用相同的Date实例。在第30页的边栏中，我们讨论了`groovy.lang.Binding`类。这实际上是一个hashmap值，您可以将其传递给GroovyShell的构造函数。只需多几行代码，就可以确保d对于`evaluate`方法调用是可见的:
```groovy
def d = new Date()
def binding = new Binding()
binding.setVariable("d", d)
def gs = new GroovyShell(binding)

d.class.methods.each{method ->
  if(method.name.startsWith("get")){
    print "${method.name}: "
    gs.evaluate("println d.${method.name}()" )
  }
}
```

**使用GString动态调用类的方法**
```groovy
def d = new Date()
d.class.methods.each{method ->
  if(method.name.startsWith("get")){
    print "${method.name}: "
    println d."${method.name}"()
  }
}
```

理解围绕evaluate、GroovyShell和Binding的细微之处很重要，但是不要忘记GString的强大功能也很重要。这是动态地调用类上的方法的最简单、最简洁的方法—将它放到GString中，然后让语句的运行时evaluation完成其余的工作。

**Groovy类的其他方法**
```groovy
class Person{
  String firstname
  String lastname
}

def p = new Person()
p.class.methods.name

===> [getMetaClass, setMetaClass, invokeMethod, getFirstname,
setFirstname, getLastname, setLastname, setProperty, getProperty,
hashCode, getClass, equals, toString, wait, wait, wait, notify, notifyAll]
```
让我们来计算在Groovy对象中找到的这个方法列表。字段的getter和setter并不奇怪:
```groovy
getFirstname, setFirstname, getLastname, setLastname
```

分别来自`java.lang.Object`和`java.lang.Class`的方法如下:
```groovy
hashCode, getClass, equals, toString, wait, wait, wait, notify, notifyAll
```

剩下的是来自`groovy.lang.GroovyObject`的添加:
```groovy
getMetaClass, setMetaClass, invokeMethod, setProperty, getProperty
```

## 10.5 Checking for the Existence of a Method {#检查方法是否存在}
```groovy
class Person{
  String firstname
  String lastname
}

def p = new Person()
if(p.metaClass.respondsTo(p, "getFirstname")){
  println p.getFirstname()
}

p.foo()
ERROR: groovy.lang.MissingMethodException: No signature of method:
  Person.foo() is applicable for argument types: () values: {}
```

正如我们在第10.3节中所做的，检查字段是否存在，在第185页，您可以使用`MetaClass `的`respondsTo`方法在调用之前动态地验证方法的存在。这个方法是在Groovy 1.5中添加的。

**Groovy 1.0**
```groovy
def list = p.class.methods as List
if(list.contains("getFirstname")){
  p.getFirstname()
}
```
对于Groovy 1.0用户，可以通过查询类上的方法列表来完成相同的工作。由于`getMethods()`从技术上返回一个数组，所以将它作为列表返回，这样就可以使用方便的`contains()`方法。

**你什么时候用这个?**
我们在第47页的3.5节(可选数据类型声明(duck typing)中讨论了duck typing。Java是一种静态类型语言，这意味着类的所有行为都是在编译时定义的。Groovy是一种动态类型语言，这意味着可以在运行时添加类编译时不存在的行为。(参见第10.8节，调用不存在的方法(invokeMethod)，在第193页有一个这样的例子。)简单来说，这意味着你没有必要成为鸭子(duck d = new duck())，只要你在运行时像鸭子一样走路和嘎嘎叫(respondsTo("walk") && respondsTo("quack"))

**检查重载方法**
```groovy
class Greeting{
  def sayHello(){
    println "Hello, Stranger"
  }
  
  def sayHello(String name){
    println "Hello, ${name}"
  }
}

def g = new Greeting()
if(g.metaClass.respondsTo(g, "sayHello", null)){
  g.sayHello()
}
===> Hello, Stranger

if(g.metaClass.respondsTo(g, "sayHello", String)){
  g.sayHello("Jane")
}
===> Hello, Jane

println "Number of sayHello() methods: " +
g.metaClass.respondsTo(g, "sayHello").size()
===> Number of sayHello() methods: 2

g.metaClass.respondsTo(g, "sayHello").each{m ->
  println "${m.name} ${m.nativeParameterTypes}"
}
===>
sayHello {class java.lang.String}
sayHello {}
```
如果您的类有几个重载的方法，您可以将额外的参数传递给respondsTo方法——每个参数有一个唯一的数据类型。如果方法不接受任何参数(如sayHello())，则传递null以进行参数检查。

如果你想看看问候语是否有一个`sayHello(String name1, String name2)`方法在你调用它之前，试试这个:
```groovy
if(g.metaClass.respondsTo(g, "sayHello", String, String)){
  g.sayHello("Jane", "Doe")
}
```
这种技术正是JavaScript开发人员多年来一直在做的，以确保他们的代码能够跨不同的浏览器工作。

## 10.6 Creating a Field Pointer {#创建字段指针}
```groovy
class Person{
  String name

  String getName(){
    "My name is ${name}"
  }
}

def p = new Person()
p.name = "Jane"
println p.name
===> My name is Jane
println p.@name
===> Jane
```
当您编写`p.name`时，其实您正在调用`p.getName()`。 如果要绕过封装并直接访问该字段（即使它是私有的！），只需在字段名称前加上`@`前缀即可。 例如：`p.@name`。 使用此方法时应格外小心-打破封装并不是一时兴起的事情。 如果getter或setter除了直接设置属性的值之外还执行其他任何操作，则可能会产生不可预测的结果。

## 10.7 Creating a Method Pointer {#创建方法指针}
```groovy
def list = []
def insert = list.&add
insert "Java"
insert "Groovy"
println list
===> ["Java", "Groovy"]
```
Groovy允许您使用`&`前缀创建指向方法的指针。 在此示例中，insert是`list.&add()`的别名。 这使您可以创建自己的域特定语言。 Groovy允许使用可选的括号（请参见第3.3节，可选的括号，第44页）和可选的分号（请参见第3.2节，可选的分号，第42页），这一事实使它看起来不太像编程语言，而更像是普通英语。

如果Groovy无法别名调用`System.out.println()`，那么我最喜欢的Groovy功能之一`println "Hello")`将不存在。 有关DSL的更多信息，请参阅第43页的边栏。

## 10.8 Calling Methods That Don’t Exist (invokeMethod) {#调用不存在的方法(invokeMethod)}
```groovy
class Person{
  String name
  Map relationships = [:]
  
  Object invokeMethod(String what, Object who){
    if(relationships.containsKey(what)){
      who.each{thisPerson ->
        relationships.get(what).add(thisPerson)
      }
    } else{
      relationships.put(what,who as List)
    }
  }
}

def scott = new Person(name:"Scott")
scott.married "Kim"
scott.knows "Neal"
scott.workedWith "Brian"
scott.knows "Venkat"
scott.workedWith "Jared"
scott.knows "Ted", "Ben", "David"

println scott.relationships
===>
["married":["Kim"],
  "knows":["Neal", "Venkat", "Ted", "Ben", "David"],
  "workedWith":["Brian", "Jared"]]
```
使用`invokeMethod()`，您可以开始了解动态语言的功能。 在此示例中，您希望在与Person的关系定义方面具有完全的灵活性。 如果您想说`scott.likesToEatSushiWith "Chris"`，则无需创建`likesToEatSushiWith())`方法并将其静态编译到类中。 您希望能够动态创建新型的关系。

尽管RelationshipsMap可以让您灵活地存储任意名称/值对，但不得不编写`scott.put(" onceWentRollerSkatingWith","Megan")`不如`scott.onceWentRollerSkatingWith "Megan"`优雅。

`invokeMethod(String name,Object args)`是Groovy元编程的核心。 对象上的每个方法调用都会被`invokeMethod`拦截。 `name`参数是方法调用（married, knows, 和 workedWith）。 `args`参数是一个Object数组，可捕获所有后续参数(Kim，Neal和Brian)。

如果没有`invokeMethod()`，第116页第7章“解析XML”中讨论的解析器或slurpers都无法像它们那样优雅地工作，从而使您可以像在父节点上调用方法一样调用子XML元素。 。

## 10.9 Creating an Expando {#创建一个Expando}
```groovy
def e = new Expando()
e.class
===> class groovy.util.Expando
e.properties
===> {}
e.class.methods.name
===> [invokeMethod, getMetaPropertyValues, hashCode, equals,
toString, setProperty, getProperty, getProperties, getMetaClass,
setMetaClass, getClass, wait, wait, wait, notify, notifyAll]
```
Expandos是好奇的小动物。 它们是空白状态，只是等待您为它们附加新字段和方法的对象。 您可以看到，在创建它们之后，它们就没有字段可说了，只有它们从`java.lang.Object`和`groovy.lang.GroovyObject`继承的基本方法。

那么，它们有什么用？
```groovy
e.latitude = 70
e.longitude = 30
println e
===> {longitude=30, latitude=70}
```
Expandos将神奇地扩展以支持您需要的任何字段。 您只需将字段附加到对象，然后您的expando就开始成形。 （诸如JavaScript之类的动态语言可以很好地发挥作用。）

那么方法呢?只需向expando添加一个新的闭包。(更多信息见第67页3.17节，闭包和块。)
```groovy
e.areWeLost = {->
  return (e.longitude != 30) || (e.latitude != 70)
}

e.areWeLost()
===> false

e.latitude = 12
e.areWeLost()
===> true
```
在本例中，areWeLost闭包不接受任何参数。下面是一个使用单个参数的闭包示例:
```groovy
e.goNorth = { howMuch ->
  e.latitude += howMuch
}

println e.latitude
===> 12

e.goNorth(20)
===> 32
```

## 10.10 Adding Methods to a Class Dynamically (Categories) {#动态地向类添加方法(Categories)}
```groovy
use(RandomHelper){
  15.times{ println 10.rand() }
}

class RandomHelper{
  static int rand(Integer self){
    def r = new Random()
    return r.nextInt(self.intValue())
  }
}
===> 5 2 7 0 7 8 2 3 5 1 7 8 9 8 1
```
Categories允许您在运行时向任何类添加新功能。这意味着您可以添加原始作者所遗忘的那些缺少的方法—即使您无法访问原始源代码。

在本例中，我们向Integer类添加了一个`rand()`方法。调用`10.rand()`返回一个从0到9的随机数。调用`100.rand()`在0到99之间执行相同的操作。您明白了。`use`块中的任何Integer都将自动获得此方法。use块之外的任何东西都不受影响。

请注意，RandomHelper类没有什么特别的，它不会扩展任何神奇的父类或实现特殊的接口。 唯一的要求是所有方法都必须接受自己（self）的实例作为第一个参数。 这种类型的类在Groovy中称为`category `。

使用纯Java，由于多种原因，您将无法直接向`java.lang.Integer`类添加新行为。 首先，将`rand()`方法添加到Integer的源代码，对其进行编译并获得广泛分布的机会非常渺茫。 （“嘿，您的应用程序需要哪个版本的Java？”  “呃，1.5。Scott...在生产中您使用多少版本的服务器？”）

所以修改源代码是不可能的。下一个逻辑步骤是扩展Integer，对吧?如果Integer没有被声明为final。因此，使用纯Java解决方案，您可以创建自己的`com.mycompany.Integer`类，它用您的自定义行为包装`Java.lang.integer`。这个解决方案的问题是，由于Java的强类型，您不能用自己的Integer替换Sun的Integer。这个6行的解决方案看起来总是更好，不是吗?

**一个稍微高级的Category示例**
```groovy
use(InternetUtils){
  println "http://localhost:8080/".get()
  println "http://search.yahoo.com/search".get("p=groovy")
  
  def params = [:]
  params.n = "10"
  params.vl = "lang_eng"
  params.p = "groovy"
  println "http://search.yahoo.com/search".get(params)
}

class InternetUtils{
  static String get(String self){
    return self.toURL().text
  }
  
  static String get(String self, String queryString){
    def url = self + "?" + queryString
    return url.get()
  }
  
  static String get(String self, Map params){
    def list = []
    params.each{k,v->
      list << "$k=" + URLEncoder.encode(v)
    }
  
    def url = self + "?" + list.join("&")
    return url.get()
  }
}
```

在这个例子中，你定义一个InternetUtils类，提供了一些新的方法：任何字符串转换为一个URL对象，并执行一个HTTP GET请求，接受字符串作为查询字符串的重载get方法不带参数的get方法 ，最后是重载的get方法，该方法从参数hashmap中构造出格式正确的查询字符串。 （有关使用Groovy简化HTTP GET请求的更多信息，请参阅第9.3节，发出HTTP GET请求，第155页。）

使用块使新功能的范围缩小。 您不必担心新方法会潜入整个应用程序的所有Strings中。 当然，如果您确实想将这些新方法全局地应用于所有String，请参见下一页的第10.11节，将方法动态添加到类（ExpandoMetaClass）。

您可以在一个使用块中使用任意数量的categories 。 只需将逗号分隔的列表传递给use块：
```groovy
use(RandomHelper, InternetUtils, SomeOtherCategory) { ... }
```
Categories are just as useful in Java as they are in Groovy. (Sorry, I couldn’t resist the pun.) InternetUtils is a pretty handy class to have around in either language. There is nothing that explicitly ties it to Groovy. The use block, of course, is pure Groovy syntactic sugar, but the Category class can be used anywhere you need it in either language.

Mixing in new functionality to any class is now at your fingertips. Once you get hooked on this new programming paradigm, you’ll wonder how you ever lived without it. (See Section 8.14, Parsing Complex CSV, on page 149 for another example of categories in action.)

## 10.11 Adding Methods to a Class Dynamically (ExpandoMetaClass) {动态地向类添加方法（ExpandoMetaClass）}
```groovy
Integer.metaClass.rand = {->
  def r = new Random()
  return r.nextInt(delegate.intValue())
}

15.times{ println 10.rand() }
===> 2 5 5 5 8 7 2 9 1 4 0 9 9 0 8
```
在第10.2节Groovy的MetaClass字段（第184页）中，我们了解到Groovy中的每个类都有一个MetaClass。 在第194页的10.9节，创建Expando中，我们了解了可延展的对象，这些对象可以动态添加新方法。 ExpandoMetaClass类结合了这两个概念-每个类的MetaClass都可以在运行时像Expando一样进行扩展。 在此示例中，我们将rand()方法直接添加到Integer的MetaClass。 这意味着正在运行的应用程序中的所有Integer现在都具有rand()方法。

当使用categories(如第10.10节所讨论的，在第196页动态地向类添加方法(categories))时，每个方法必须有一个`self`参数。在使用ExpandoMetaClass时，delegate充当此角色。这个关键字为您提供了元类委托，它为您提供了链中的一个类。在本例中，对委托的调用为10。

**一个稍微高级一点的ExpandoMetaClass示例**
```groovy
String.metaClass.get = {->
  return delegate.toURL().text
}

String.metaClass.get = {String queryString ->
  def url = delegate + "?" + queryString
  return url.get()
}

String.metaClass.get = {Map params ->
  def list = []
  params.each{k,v->
    list << "$k=" + URLEncoder.encode(v)
  }

  def url = delegate + "?" + list.join("&")
  return url.get()
}

println "http://localhost:8080/".get()
println "http://search.yahoo.com/search".get("p=groovy")

def params = [:]
params.n = "10"
params.vl = "lang_eng"
params.p = "groovy"
println "http://search.yahoo.com/search".get(params)
```
就功能而言，这里的三个方法与第10.10节中的示例相同，即在第196页动态地向类添加方法(Categories)。在实现方面，您所面对的代码是基于Groovy语法和习惯用法的。self引用都已更改为delegate。闭包与分组在category类中的静态方法不同。

那么，应该使用category还是ExpandoMetaClass?答案是“视情况而定。”(这难道不总是答案吗?)如果您希望将新方法的范围限制在定义良好的代码块内，那么category 是最理想的。如果希望将新方法应用于整个正在运行的应用程序中的所有实例，则最好使用ExpandoMetaClass。如果您希望Java和Groovy代码能够轻松地共享新功能，那么category就留给您一个带有静态方法的普通旧Java类。ExpandoMetaclass与Groovy的联系更紧密，但它们的性能也明显更好。


[^901]: http://en.wikipedia.org/wiki/Procedural_programming
[^902]: http://jakarta.apache.org/httpcomponents/httpcomponents-client
[^903]: http://en.wikipedia.org/wiki/Representational_State_Transfer
[^904]: http://developer.yahoo.com/search/web/V1/webSearch.html
[^905]: http://www.programmableweb.com/
[^906]: http://en.wikipedia.org/wiki/Urlencode
[^907]: http://en.wikipedia.org/wiki/Create%2C_read%2C_update_and_delete
[^908]: http://en.wikipedia.org/wiki/Atom_%28standard%29
[^909]: http://code.google.com/apis/gdata/
[^1001]: http://en.wikipedia.org/wiki/Metaprogramming
[^1002]: http://en.wikipedia.org/wiki/Reflection_%28computer_science%29
[^1003]: http://java.sun.com/javase/6/docs/api/java/lang/Class.html#getDeclaredFields()
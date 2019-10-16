# Web Services

Web services are everywhere these days. Once we as an industry figured out that XML travels over HTTP as well as HTML, we entered a new age of service-oriented architecture (SOA). This new way of grabbing data from remote sources means that developers must understand the
mechanics of low-level TCP/IP and HTTP as well as the various higherlevel XML dialects out in the wild: SOAP, REST, and XML-RPC. Luckily, Groovy helps us on all fronts.

In this chapter, we’ll start with the low-level basics of how to determine your local TCP/IP address and domain name and those of remote systems. We’ll move up the stack to HTTP—learning how to GET, POST,PUT, and DELETE programmatically. We’ll end the chapter with examples of how to send and receive SOAP messages, XML-RPC messages, and RESTful requests. We’ll even parse a bit of comma-separated value (CSV) data just for old-times’ sake.

## 9.1 Finding Your Local IP Address and Name
```groovy
InetAddress.localHost.hostAddress
===> 63.246.7.76

InetAddress.localHost.hostName
===> myServer

InetAddress.localHost.canonicalHostName
===> www.aboutgroovy.com
```
Before you can communicate with anyone else, it always helps knowing about yourself. In this example, you’ll discover your IP address, your local host name, and the DNS name by which the rest of the world knows you.

The InetAddress class comes to you from the java.net package. You cannot directly instantiate an InetAddress class (def addr = new InetAddress()) because the constructor is private. You can, however, use a couple of different static methods to return a well-formed InetAddress. The getLocalHost() method for getting local information is discussed here; getByName() and getAllByName() for getting remote information are discussed in Section 9.2, Finding a Remote IP Address and Domain Name, on the next page.

The getLocalHost() method returns an InetAddress that represents the localhost or the hardware on which it is running. As discussed in Section 4.2, Getter and Setter Shortcut Syntax, on page 72, getLocalHost() can be shortened to localHost in Groovy. Once you have a handle to localHost, you can call getHostAddress() to get your IP address or getHostName() to get the local machine name. This name is the private name of the system, as opposed to the name registered in DNS for the rest of the world to see. Calling getCanonicalHostName() performs a DNS lookup.

Of course, as discussed in Section 5.4, Running a Shell Command, on page 89, the usual command-line tools that ship with your operating system are just an execute() away. They might not be as easy to parse as the InetAddress methods, but as you can see they expose quite a bit more detail.
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

## 9.2 Finding a Remote IP Address and Domain Name
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

In addition to its returning information about the local machine, you can use InetAddress to find out about remote systems. getByName() returns a well-formed InetAddress object that represents the remote system. getByName() accepts either a domain name (for example, www.aboutgroovy.com) or an IP address (for example, 64.233.167.99). Once you have a handle to the system, you can ask for its hostAddress and its canonicalHostName.

Sometimes a DNS name can resolve to many different IP addresses. This is especially true for busy websites that load balance the traffic among many physical servers. If a DNS name resolves to more than one IP address, getByName() will return the first one in the list, whereas getAllByName() will return all of them.

Of course, the usual command-line tools for asking about remote systems are available to you as well:
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

## 9.3 Making an HTTP GET Request
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

The simplest way to get the contents of an HTML page is to call getText() on the URL. This allows you to store the entire response in a String variable. If the page is too big to do this comfortably, you can also iterate through the response line by line using eachLine().

Groovy adds a toURL() method to java.lang.String, allowing you to make identical requests using a slightly more streamlined syntax:
```javascript
"http://www.aboutgroovy.com".toURL().text
"http://www.aboutgroovy.com".toURL().eachLine{...}
```

We’ll discuss how to streamline this to the point where you can simply call "http://www.aboutgroovy.com".get() in Section 10.11, Adding Methods to a Class Dynamically (ExpandoMetaClass), on page 198.

**Processing a Request Based on the HTTP Response Code**
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

Calling getText() directly on the URL object means that you expect everything to go perfectly—no connection timeouts, no 404s, and so on. Although you should be commended on your optimism, if you want to write slightly more fault-tolerant code, then you should call openConnection() on the URL.

This returns a java.net.URLConnection object that will allow you to do a bit more detailed work with the URL object. connection.content.text returns the same information as url.text while allowing you to do more introspection on the response—connection.responseCode for the 200 or the 404; connection.responseMessage for OK or File Not Found.

**Getting HTTP Response Metadata**
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

Once you have a handle to the URLConnection, you have full access to the accompanying response metadata. In addition to the responseCode and responseMessage, you can ask for things such as the contentLength and the contentType and can even iterate over each response header one by one.

**Creating a Convenience GET Class**
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

Up to this point you’ve been writing some pretty procedural[^901] code. It certainly gets the job done, but it suffers just a wee bit in terms of lack of reusability. (Don’t you dare suggest that “copy and paste” is a valid type of reuse. You’re a good object-oriented programmer—how could you even think such a thing?) This custom Get class wraps everything you’ve learned up to this point into something that can be reused. It has a nice simple interface and hides enough of the HttpConnection complexity to make it worth your time.

Now, nothing can compare to the simplicity of "http://www.aboutgroovy.com".toURL().text. On the opposite end of the spectrum is Jakarta Commons HttpClient[^902]—a great library that is far more complete than anything I could put together on my own. The drawback, of course, is adding yet another dependency to the project. The custom Get class splits the difference nicely. It is slightly more robust than "".toURL().text, and yet it is implemented in pure Groovy so you don’t have to worry about JAR bloat in your classpath.

One more thing: the Get class adds support for a query string. This is a collection of name/value pairs that can be appended to the end of a URL to further customize it. See Section 9.4, Working with Query Strings for more information.

**RESTful GET Requests**
```groovy
"http://search.yahooapis.com/WebSearchService/V1/webSearch?
appid=YahooDemo&query=groovy&results=10".toURL().text

//alternately, using our Get class
def get = new Get()
get.url = "http://search.yahooapis.com/WebSearchService/V1/webSearch"
get.queryString = "appid=YahooDemo&query=groovy&results=10"
def results = get.text
```

RESTful web services are a type of web service. REST stands for Representational State Transfer[^903]. Although there are many differing interpretations of what it means to be truly RESTful, it is generally accepted that an HTTP GET request that returns XML results (as opposed to HTML or some other data format) constitutes the simplest form of a RESTful web service.

Yahoo offers a RESTful API[^904] that returns query results in XML. This query returns the top-ten hits for the search term groovy. For the result of this query and how to parse it, see Section 9.12, Parsing Yahoo Search Results as XML, on page 176.

## 9.4 Working with Query Strings
```javascript
"http://search.yahoo.com/search?p=groovy".toURL().text
```

A query string allows you to make more complex HTTP GET requests by adding name/value pairs to the end of the address. Now instead of just asking for a static page at http://search.yahoo.com, you can make a dynamic query for all pages that contain the word groovy.

The Web is transformed from a simple distributed filesystem to a fully programmable Web[^905]. The mechanics of programmatically making an HTTP GET request don’t change—it is no more complicated than what we discussed in Section 9.3, Making an HTTP GET Request, on page 155.

However, the semantics of using query strings opens up a whole new world of programmatic possibilities.

For example, complicated web pages like a Google map showing the Denver International Airport can be captured in a single URL. This means we can hyperlink it, bookmark it, or email it to a friend simply by clicking Link to This Page in the upper-right corner of the page. Each element in the query string represents a different aspect of the map: ll for the latitude/longitude center point of the map (39.87075,-104.694214), z for the zoom level (11), t for the type (h, or hybrid), and so forth.
```javascript
"http://maps.google.com/maps?f=q&hl=en&geocode=&time=&date=&ttype=
&q=dia&sll=37.0625,-95.677068&sspn=34.038806,73.125&ie=UTF8
&ll=39.87075,-104.694214&spn=0.2577,0.571289&z=11&iwloc=addr&om=1&t=h"
.toURL().text
```

**Building the Query String from a List**
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

Often you’ll be tasked with assembling a well-formed query string from an arbitrary collection of data values. The secret is to make sure the values are URL encoded[^906] (“foo bar baz” ==> foo+bar+baz), while the name portion (nonsense=) remains plain text. If you try to URL encode  the name and the value as a single string (“nonsense=foo bar baz”), the equals sign (=) will get converted to %3D, and your web server will most likely reject the request.

This example creates a List of name/value pairs, ensuring that only the value gets URL encoded using the java.net.URLEncoder. Later when you need the well-formed query string, you call queryString.join("&"). As we discussed in Section 3.14, Join, on page 60, this returns the list as a single string with each element joined by the string you passed in as the parameter.

This particular query string was built by performing an advanced Yahoo search and cherry-picking the interesting name/value pairs from the resulting URL. n returns twenty results instead of the default ten. vdlimits the results to those posted in the past three months. vl returns only English pages. vf filters the results for only PDF documents. And finally, p looks for results that mention either groovy or grails.

**Building the Query String from a Map**
```groovy
def map = [n:20, vf:"pdf", p:"groovy grails"]
def list = []
map.each{name,value->
  list << "$name=" + URLEncoder.encode(value.toString())
}
println list.join("&")
===> n=20&vf=pdf&p=groovy+grails
```

Groovy Maps are a great way to represent query strings since both naturally have name/value pairs. This example still uses a temporary List to store the URL-encoded values and a join("&") to put them together at the last minute.

There is one edge case that keeps this from being a 100% solution. Query strings are allowed to have duplicate named elements, whereas Maps enforce unique names.
```javascript
http://localhost/order?book=Groovy+Recipes&book=Groovy+In+Action
```
If you can live with this limitation, then Maps are the perfect solution. If you need to support duplicate named elements, see Section 9.4, Creating a Convenience QueryString Class for more information.

**Creating a Convenience QueryString Class**
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

Creating a convenience class allows you to encapsulate the mechanics of building a well-formed query string into a reusable component.

The qs object accepts name/value pairs in the constructor that get coerced into a Map. (You could have also passed in an existing Map to the constructor.) The qs2 object demonstrates three different ways to pass in name/values pairs—by accessing the params Map directly, by using the convenient add() method with parentheses, and finally by calling the same add() method while taking advantage of Groovy’s optional parentheses.

Notice that the add() method accepts an Object for the value. This allows you to store values such as integers and classes instead of simple strings. Calling URLEncoder.encode(value.toString()) ensures that the values get plugged into the query string correctly.

Combining the query string with the Get class created in Section 9.3, Creating a Convenience GET Class, on page 157 begins to demonstrate the power you’ve managed to assemble with very little code—there are fewer than fifty lines of code between Get and QueryString.
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

Notice that upgrading your queryString field from a String to a full-fledged QueryString object requires touching the Get class in only two places. The field declaration now creates a new QueryString(), and the toString() method calls queryString.toString(). This upgrade now allows you to let the Get class create the well-formed QueryString instead of forcing you to create one on your own. Calls such as get.queryString.add("p", "groovy grails") do the right thing behind the scenes, ensuring that the values are properly URL encoded.

Remember the query string/hashmap mismatch we discussed in Section 9.4, Building the  uery String from a Map, on page 161? Because the QueryString class is currently implemented, each call to qs.add() replaces the name/value pair. To support duplicate named elements, the QueryString class would need to be refactored to append values to a List if the name existed. For an idea of how to add this feature, see Section 10.8, Calling Methods That Don’t Exist (invokeMethod), on page 193.

## 9.5 Making an HTTP POST Request
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

When making an HTTP POST request, you cannot use the same getText() shortcut on the URL class that you could when making a GET request. You must get the URLConnection so that you can set the request method to POST (GET is the default). For a GET request, the query string is appended to the end of the URL object. In contrast, the query string of a POST is embedded in the body of the request. To accomplish this, you must do three things: set the doOutput value of the URLConnection to true, get the outputStream, and write the query string to it before you call connect().

**Building the Query String from a List**
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

As discussed in Section 9.4, Building the Query String from a List, on page 160, the secret to building up a query string from a List is making sure the values get URL encoded and then joining the elements together with an &.

**Creating a Convenience Post Class**
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
Putting all the complicated connection logic into a Post class—combined with the QueryString class you created in Section 9.4, Creating a Convenience QueryString Class, on page 161—makes for a pretty compelling development experience.

**Mocking HTML Forms for Testing**
```xml
<form method="post" action="http://localhost:8888/jaw/controller">
  <input type="hidden" name="action" value="saveCar" />
  Make: <input type="text" name="make" value="" /></td>
  Model: <input type="text" name="model" value="" /></td>
  Year: <input type="text" name="modelYear" value="" /></td>
  <input type="submit" name="save" value="Save" />
</form>
```

Now that you have Post class, you can easily mock up an HTML form submission using code. Given this HTML form, you could simulate a user filling out the form and clicking the submit button using the following code:
```groovy
def post = new Post(url:"http://localhost:8888/jaw/controller")
post.queryString.add("action", "saveCar")
post.queryString.add("make", "Toyota")
post.queryString.add("model", "Prius")
post.queryString.add("modelYear", 2012)
println post.text
```
All that is left to do at this point is to write the assertion on post.text that verifies the form submission was performed correctly.

**RESTful POST Requests Using XML**
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

In RESTful web services, the HTTP verb used for the request has deep semantic meaning. A common database metaphor—create, retrieve, update, delete (CRUD)—is equally applicable to RESTful applications[^907], although the verbs used in SQL statements aren’t identical. The SELECT you perform against a database is analogous to an HTTP GET. You INSERT records into a table, whereas you POST form data to a website. HTTP PUT is the equivalent of a database UPDATE. DELETE is the least surprising of all—it has the same meaning in both SQL and HTTP.

RESTful web services usually expect XML in the body of the POST as opposed to the query strings being demonstrated up to this point. To pass in XML, you need to make two minor changes to your code. First, you’ll most likely need to change the Content-Type from application/xwww-form-urlencoded (the default for POST) to application/xml. (The exact Content-Type depends on the RESTful service you are calling.) The other thing you need to do is not URL encode the data. The XML payload should be transported in its native format. For another example of POSTing XML, see Section 9.10, Making a SOAP Request, on page 172.













[^901]: http://en.wikipedia.org/wiki/Procedural_programming
[^902]: http://jakarta.apache.org/httpcomponents/httpcomponents-client
[^903]: http://en.wikipedia.org/wiki/Representational_State_Transfer
[^904]: http://developer.yahoo.com/search/web/V1/webSearch.html
[^905]: http://www.programmableweb.com/
[^906]: http://en.wikipedia.org/wiki/Urlencode
[^907]: http://en.wikipedia.org/wiki/Create%2C_read%2C_update_and_delete
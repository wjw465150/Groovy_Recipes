# Base64 URL和文件名安全编码

Groovy支持Base64编码[很长一段时间](http://mrhaki.blogspot.com/2009/11/groovy-goodness-base64-encoding.html)。 从Groovy 2.5.0开始，我们还可以使用Base64 URL和Filename Safe编码来使用`encodeBase64Url`方法对字节数组进行编码。 结果是一个`Writable`对象。 我们可以在`Writable`对象上调用`toString`方法来获得`String`值。 可以使用添加到`String`类的`decodeBase64Url`方法使用相同的编码对编码的`String`值进行解码。

在下面的示例Groovy代码中，我们对字节数组进行编码和解码：

```groovy
import static java.nio.charset.StandardCharsets.UTF_8
 
def message = 'Groovy rocks!'
 
// Get bytes array for String using UTF8.
def messageBytes = message.getBytes(UTF_8)
 
// Encode using Base64 URL and Filename encoding.
def messageBase64Url = messageBytes.encodeBase64Url().toString()
 
// Encode using Base64 URL and Filename encoding with padding.
def messageBase64UrlPad = messageBytes.encodeBase64Url(true).toString()
 
assert messageBase64Url == 'R3Jvb3Z5IHJvY2tzIQ'
assert messageBase64UrlPad == 'R3Jvb3Z5IHJvY2tzIQ=='
 
// Decode the String values.
assert new String(messageBase64Url.decodeBase64Url()) == 'Groovy rocks!'
assert new String(messageBase64UrlPad.decodeBase64Url()) == 'Groovy rocks!'
```

用Groovy 2.5.0编写。

# 不可修改的集合

当我们想在Groovy中创建无法修改的集合时，我们可以使用[asImmutable](http://mrhaki.blogspot.com/2009/10/groovy-goodness-immutable-collections.html)。 从Groovy 2.5.0开始，我们也可以在集合中使用`asUnmodifiable`方法。 该方法可以应用于所有`Collection`类型，包括`Map`。

在下面的例子中，我们在`List`和`Map`上使用`asUnmodifiable`：

```groovy
import static groovy.test.GroovyAssert.shouldFail
 
// Create List that is unmodifiable.
def list = ['Groovy', 'Gradle', 'Asciidoctor', 'Micronaut'].asUnmodifiable()
 
shouldFail(UnsupportedOperationException) {
    // We cannot add new items.
    list << 'Java'
}
     
shouldFail(UnsupportedOperationException) {
    // We cannot change items.
    list[0] = 'Java'
}
 
 
// Create Map that is unmodifiable.
def data = [name: 'Messages from mrhaki', subject: 'Gr8 stuff'].asUnmodifiable()
 
shouldFail(UnsupportedOperationException) {
    // We cannot add a new key.
    data.subject = 'Dev subjects'
}
     
shouldFail(UnsupportedOperationException) {
    // We cannot change the value of a key.
    data.blog = true
}
```

用Groovy 2.5.0编写。

# 计算MD5和SHA哈希值

Groovy为`String`类添加了许多有用的方法。 从Groovy 2.5.0开始，我们甚至可以使用`md5`和`digest`方法计算MD5和SHA哈希值。 `md5`方法使用MD5算法创建哈希值。 `digest`方法接受算法的名称作为值。 这些值取决于我们Java平台上的可用算法。 例如，算法MD2，MD5，SHA-1，SHA-256，SHA-384和SHA-512默认可用。

在下一个例子中，我们在`String`值上使用`md5`和`digest`方法：

```groovy
def value = 'IamASecret'
 
def md5 = value.md5()
 
// We can provide hash algorithm with digest method.
def md2 = value.digest('MD2')
def sha1 = value.digest('SHA-1')
def sha256 = value.digest('SHA-256')
 
assert md5 == 'a5f3147c32785421718513f38a20ca44'
assert md2 == '832cbe3966e186194b1203c00ef47488'
assert sha1 == '52ebfed118e0a411e9d9cbd60636fc9dea718928'
assert sha256 == '4f5e3d486d1fd6c822a81aa0b93d884a2a44daf2eb69ac779a91bc76de512cbe'
```

用Groovy 2.5.0编写。

# 前序和后序树遍历

Groovy中的`Node`类有`depthFirst`和`breadthFirst`方法，可以使用深度优先遍历或广度优先遍历返回`Node`对象的集合。由于Groovy 2.5.0，我们可以指定是使用preorder(默认值)还是postorder遍历。此外，这些方法现在接受一个“闭包”，该“闭包”将为每个访问的节点调用。`Closure`将当前“节点”作为第一个参数，第二个参数是当前节点的树级。

在下面的例子中，我们读取了一些XML，然后使用`depthFirst`以几种方式访问节点树:

```groovy
// We start with a XML node hierarchy.
def xml = '''
        <A>
          <B>
            <D/>
            <E/>
          </B>
          <C>
            <F/>
          </C>
        </A>
        '''
def root = new XmlParser().parseText(xml)
 
// Preorder traversal is default, but
// we can also specify it with the boolean
// argument of depthFirst method.
assert root.depthFirst(true)
           .collect { node -> node.name() } == ['A', 'B', 'D', 'E', 'C', 'F']
            
// Groovy 2.5.0 adds possibility to
// directly call closure for
// each node visited where the first
// Closure argument is the node and
// the second argument the level.
def result = []
root.depthFirst { node, level -> result << "$level${node.name()}" }
 
assert result == ['1A', '2B', '3D', '3E', '2C', '3F']
 
// Postorder traversal can be specified
// by setting preorder argment to false.
// When used in combination with Closure
// argument we must using named argument
// preorder.
result = []
root.depthFirst(preorder: false) { node -> result << node.name() }
 
assert result == ['D', 'E', 'B', 'F', 'C', 'A']
```

在第二个示例中，我们使用了`breadthFirst`方法。这意味着树中每层访问的节点:

```groovy
// Let's create a Node hierarchy.
def builder = NodeBuilder.newInstance()
def root = builder.A {
    B {
        D()
        E()
    }
    C {
        F()
    }
}
 
 
// Preorder traversal is default, but
// we can also specify it with the boolean
// argument of breadthFirst method.
assert root.breadthFirst(true)
           .collect { node -> node.name() } == ['A', 'B', 'C', 'D', 'E', 'F']
            
// Groovy 2.5.0 adds possibility to
// directly call closure for
// each node visited with node and level.
def result = []
root.breadthFirst { node, level -> result << "$level${node.name()}" }
 
assert result == ['1A', '2B', '2C', '3D', '3E', '3F']
 
// Postorder traversal is implemented
// as starting at the lowest level and
// working our way up.
result = []
root.breadthFirst(preorder: false) { node -> result << node.name() }
 
assert result == ['D', 'E', 'F', 'B', 'C', 'A']
```

用Groovy 2.5.0编写。

# 使用Tap方法轻松创建对象

Groovy 2.5.0将`tap`方法添加到所有对象并更改`with`方法的方法签名。 在[上一篇文章](http://mrhaki.blogspot.com/2009/09/groovy-goodness-with-method.html) 中，我们已经了解了`with`方法。在Groovy 2.5.0中，我们可以为`with`方法添加一个额外的`boolean`参数。 如果值为`false`（默认值），则`with`方法必须返回与闭包调用返回的值相同的值。如果值为`true`，则返回调用`with`方法的对象实例。 新的`tap`方法是`with(true)`的别名，所以它总是返回对象实例。

在第一个例子中，我们使用`tap`方法创建一个新的`Sample`对象并设置属性值并调用`Sample`class的方法：

```groovy
/**
 * Sample class with some properties
 * and a method.
 */
class Sample {
     
    String username, email
     
    List<String> labels = []
     
    void addLabel(value) {
        labels << value
    }
     
}
 
// Use tap method to create instance of
// Sample and set properties and invoke methods.
def sample =
        new Sample().tap {
            assert delegate.class.name == 'Sample'
             
            username = 'mrhaki'
            email = 'email@host.com'
            addLabel 'Groovy'
            addLabel 'Gradle'
             
            // We use tap, an alias for with(true),
            // so the delegate of the closure,
            // the Sample object, is returned.
        }
 
assert sample.labels == ['Groovy', 'Gradle']
assert sample.username == 'mrhaki'
assert sample.email == 'email@host.com'
```

在下面的示例中，我们使用`with`方法来演示使用不同参数值的多个调用的差异：

```groovy
/**
 * Sample class with some properties
 * and a method.
 */
class Sample {
     
    String username, email
     
    List<String> labels = []
     
    void addLabel(value) {
        labels << value
    }
     
}
 
// Use with method to create instance of
// Sample and set properties and invoke methods.
def sample1 =
        new Sample().with {
            assert delegate.class.name == 'Sample'
 
            username = 'mrhaki'
            email = 'email@host.com'
            addLabel 'Groovy'
            addLabel 'Gradle'  
        }
        
// With method returns the result
// from the closure. In the previous
// case the return result is null,
// because the last statement addLabel
// is used as return value. addLabel has
// return type void.
assert !sample1
 
 
// Use with method to create instance of
// Sample and set properties and invoke methods.
def sample2 =
        new Sample().with {
            assert delegate.class.name == 'Sample'
 
            username = 'mrhaki'
            email = 'email@host.com'
            addLabel 'Groovy'
            addLabel 'Gradle'
             
            // Explicitly return delegate of
            // closure, which is the Sample object.
            return delegate
        }
 
assert sample2.labels == ['Groovy', 'Gradle']
assert sample2.username == 'mrhaki'
assert sample2.email == 'email@host.com'
 
 
// Use with method to create instance of
// Sample and set properties and invoke methods.
def sample3 =
        new Sample().with(true) {
            assert delegate.class.name == 'Sample'
 
            username = 'mrhaki'
            email = 'email@host.com'
            addLabel 'Groovy'
            addLabel 'Gradle'
             
            // We use with(true), so the
            // delegate of the closure, the Sample
            // object, is returned.
        }
 
assert sample3.labels == ['Groovy', 'Gradle']
assert sample3.username == 'mrhaki'
assert sample3.email == 'email@host.com'
```

使用`with`方法的一个很好的用例是使用来自对象的值将对象转换为另一种类型。 在下一个例子中，我们使用来自`Sample`对象的值来创建一个新的`String`：

```groovy
/**
 * Sample class with some properties
 * and a method.
 */
class Sample {
     
    String username, email
     
    List<String> labels = []
     
    void addLabel(value) {
        labels << value
    }
     
}
 
def sample =
        new Sample().tap {
            username = 'mrhaki'
            email = 'email@host.com'
            addLabel 'Groovy'
            addLabel 'Gradle'
        }
 
// The with method can be very useful to
// transform object to another type using
// values from the object.
def user = sample.with { "$username likes ${labels.join(', ')}." }
 
assert user == 'mrhaki likes Groovy, Gradle.'
```

用Groovy 2.5.0编写。

# 我的Class在哪里

通过将`getLocation`方法添加到`Class`类，Groovy 2.5.0可以获得`Class`文件的位置。 如果`Class`是JDK的一部分，则返回的位置是`null`，否则是我们使用`Class`文件获取JAR文件或源文件（如果可用）的位置。

在下面的示例中，我们获取内部JDK`String`类和Groovy实用程序类`ConfigSlurper`的位置：

```
// Internal JDK class location is null.
assert String.location == null
 
 
// Import ConfigSlurper with alias.
import groovy.util.ConfigSlurper as ConfigReader
 
// Location of Groovy JAR file.
def groovyJarFile = 'file:/Users/mrhaki/.sdkman/candidates/groovy/2.5.0/lib/groovy-2.5.0.jar'.toURL()  
 
// ConfigSlurper is located in the Groovy JAR file.
assert ConfigSlurper.location == groovyJarFile
 
// Works also for aliased class.
assert ConfigReader.location == groovyJarFile       
```

用Groovy 2.5.0编写。

# 在SQL GString Query中使用扩展变量

使用`groovy.sql.Sql`类可以很容易地使用Groovy代码中的SQL数据库。 该类有几种方法来执行SQL查询，但是如果我们使用`Sql`中带有`GString`参数的方法，我们必须特别小心。Groovy将提取所有变量表达式，并将它们用作从SQL查询构造的`PreparedStatement`中占位符的值。 如果我们有变量表达式不应该被提取为`PreparedStatement`的参数，我们必须使用`Sql.expand`方法。 此方法将使变量表达式成为`groovy.sql.ExpandedVariable`对象。 此对象不用作`PreparedStatement`查询的参数，但该值被评估为`GString`变量表达式。

在下面的示例中，我们有一个类，它使用`GString`查询值调用`Sql`对象的几个方法。 我们可以看到何时使用`Sql.expand`以及何时不需要：

```groovy
package mrhaki
 
import groovy.sql.*
 
class SampleDAO {
    private static final String TABLE_NAME = 'sample'
    private static final String COLUMN_ID = 'id'
    private static final String COLUMN_NAME = 'name'
    private static final String COLUMN_DESCRIPTION = 'description'
 
    private final Sql sql =
        Sql.newInstance(
            'jdbc:h2:test', 'sa', 'sa', 'org.h2.Driver')
 
    Long create() {
        // We need to use Sql.expand() in our GString query.
        // If we don't use it the GString variable expressions are interpreted
        // as a placeholder in a SQL prepared statement, but we don't
        // that here.
        final query =
            """
            INSERT INTO ${Sql.expand(TABLE_NAME)} DEFAULT VALUES
            """
 
        final insertedKeys = sql.executeInsert(query)
        return insertedKeys[0][0]
    }
 
    void updateDescription(final Long id, final String description) {
        // In the following GString SQL we need
        // Sql.expand(), because we use executeUpdate
        // with only the GString argument.
        // Groovy will extract all variable expressions and
        // use them as the placeholders
        // for the SQL prepared statement.
        // So to make sure only description and id are
        // placeholders for the prepared statement we use
        // Sql.expand() for the other variables.
        final query =
            """
            UPDATE ${Sql.expand(TABLE_NAME)}
            SET ${Sql.expand(COLUMN_DESCRIPTION)} = ${description}
            WHERE ${Sql.expand(COLUMN_ID)} = ${id}
            """
        sql.executeUpdate(query)
    }
 
    void updateName(final Long id, final String name) {
        // In the following GString SQL we don't need
        // Sql.expand(), because we use the executeUpdate
        // method with GString argument AND argument
        // with values for the placeholders.
        final query =
            """
            UPDATE ${TABLE_NAME}
            SET ${COLUMN_NAME} = :nameValue
            WHERE ${COLUMN_ID} = :idValue
            """
        sql.executeUpdate(query, nameValue: name, idValue: id)
    }
}

```

用Groovy 2.5.4编写。

# 自定义JSON输出-JsonGenerator

Groovy 2.5.0增加了通过`JsonGenerator`实例自定义JSON输出。 将对象转换为JSON字符串值的最简单方法是通过`JsonOutput.toJson`。 此方法使用默认的`JsonGenerator`，其JSON输出具有合理的默认值。 但是我们可以使用自定义生成器并创建JSON输出。 要创建自定义生成器，我们使用可通过`JsonGenerator.Options`访问的构建器。 通过流式的API，我们可以例如忽略输出中带有`null`值的字段，更改日期的日期格式，并按名称或值的类型忽略字段。 我们可以通过将转换的实现添加为`Closure`或者实现`JsonGenerator.Converter`接口来为类型添加自定义转换器。 要获取JSON字符串，我们只需调用生成器的`toJson`方法。

在下面的示例Groovy代码中，我们有一个带有数据的`Map`，我们想将它转换为JSON。 首先我们使用默认生成器然后创建自己的生成器来自定义JSON输出：

```groovy
// Sample class to be used in JSON.
@groovy.transform.TupleConstructor
class Student {
    String firstName, lastName
}
 
def data =
    [student: new Student('Hubert', 'Klein Ikkink'),
     dateOfBirth: Date.parse('yyyyMMdd', '19730709'),
     website: 'https://www.mrhaki.com'.toURL(),
     password: 'IamSecret',
     awake: Optional.empty(),
     married: Optional.of(true),
     location: null,
     currency: '\u20AC' /* Unicode EURO */]
      
 
import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter
         
// Default JSON generator. This generator is used by
// Groovy to create JSON if we don't specify our own.
// For this example we define the default generator
// explicitly to see the default output.      
def jsonDefaultOutput = new JsonGenerator.Options().build()
         
// Use generator to create JSON string.
def jsonDefaultResult = jsonDefaultOutput.toJson(data) // Or use JsonOutput.toJson(data)
 
assert jsonDefaultResult == '{"student":{"firstName":"Hubert","lastName":"Klein Ikkink"},' +
    '"dateOfBirth":"1973-07-08T23:00:00+0000","website":"https://www.mrhaki.com","password":"IamSecret",' +
    '"awake":{"present":false},"married":{"present":true},"location":null,"currency":"\\u20ac"}'
 
 
// Define custom rules for JSON that will be generated.
def jsonOutput =
    new JsonGenerator.Options()
        .excludeNulls()  // Do not include fields with value null.
        .dateFormat('EEEE dd-MM-yyyy', new Locale('nl', 'NL')) // Set format for dates.
        .timezone('Europe/Amsterdam') // Set timezone to be used for formatting dates.
        .excludeFieldsByName('password')  // Exclude fields with given name(s).
        .excludeFieldsByType(URL)  // Exclude fields of given type(s).
        .disableUnicodeEscaping()  // Do not escape UNICODE.
        .addConverter(Optional) { value -> value.orElse('UNKNOWN') } // Custom converter for given type defined as Closure.
        .addConverter(new Converter() {  // Custom converter implemented via Converter interface.
         
            /**
             * Indicate which type this converter can handle.
             */
            boolean handles(Class<?> type) {
                return Student.isAssignableFrom(type)
            }
             
            /**
             * Logic to convert Student object.
             */
            Object convert(Object student, String key) {
                "$student.firstName $student.lastName"
            }
             
        })
        .build()  // Create the converter instance.
 
// Use generator to create JSON from Map data structure.
def jsonResult = jsonOutput.toJson(data)
 
assert jsonResult == '{"student":"Hubert Klein Ikkink",' +
    '"dateOfBirth":"maandag 09-07-1973",' +
    '"awake":"UNKNOWN","married":true,"currency":"€"}'

```

`JsonBuilder`和`StreamingJsonBuilder`类现在也支持使用`JsonGenerator`实例。 需要创建JSON输出时使用生成器。 使用自定义生成器不会更改构建器的内部数据结构。

在下面的示例中，我们使用前一个示例的自定义生成器，并将其应用于`JsonBuilder`和`StreamingJsonBuilder`实例：

```groovy
import groovy.json.JsonBuilder
 
// We can use a generator instance as constructor argument
// for JsonBuilder. The generator is used when we create the
// JSON string. It will not effecct the internal JSON data structure.
def jsonBuilder = new JsonBuilder(jsonOutput)
jsonBuilder {
    student new Student('Hubert', 'Klein Ikkink')
    dateOfBirth Date.parse('yyyyMMdd', '19730709')
    website 'https://www.mrhaki.com'.toURL()
    password 'IamSecret'
    awake Optional.empty()
    married Optional.of(true)
    location null
    currency  '\u20AC'
}
 
def jsonBuilderResult = jsonBuilder.toString()
 
assert jsonBuilderResult == '{"student":"Hubert Klein Ikkink",' +
    '"dateOfBirth":"maandag 09-07-1973",' +
    '"awake":"UNKNOWN","married":true,"currency":"€"}'
 
// The internal structure is unaffected by the generator.
assert jsonBuilder.content.password == 'IamSecret'
assert jsonBuilder.content.website.host == 'www.mrhaki.com'
 
 
import groovy.json.StreamingJsonBuilder
 
new StringWriter().withWriter { output ->
 
    // As with JsonBuilder we can provide a custom generator via
    // the constructor for StreamingJsonBuilder.
    def jsonStreamingBuilder = new StreamingJsonBuilder(output, jsonOutput)
    jsonStreamingBuilder {
        student new Student('Hubert', 'Klein Ikkink')
        dateOfBirth Date.parse('yyyyMMdd', '19730709')
        website 'https://www.mrhaki.com'.toURL()
        password 'IamSecret'
        awake Optional.empty()
        married Optional.of(true)
        location null
        currency  '\u20AC'
    }
 
    def jsonStreamingBuilderResult = output.toString()
     
    assert jsonStreamingBuilderResult == '{"student":"Hubert Klein Ikkink",' +
        '"dateOfBirth":"maandag 09-07-1973",' +
        '"awake":"UNKNOWN","married":true,"currency":"€"}'
}
```

用Groovy 2.5.0编写。



# 添加带注释的Map构造函数

从Groovy的早期开始，我们可以创建POGO（Plain Old Groovy Objects）类，它们将具有带有`Map`参数的构造函数。 Groovy在生成的类中自动添加构造函数。我们可以使用命名参数来创建POGO的实例，因为`Map`参数构造函数。 这只有在我们不添加自己的构造函数且属性不是最终的时才有效。从Groovy 2.5.0开始，我们可以使用`@MapConstrutor` AST转换注释来添加带有`Map`参数的构造函数。使用注释我们可以有更多选项来自定义生成的构造函数。例如，我们可以让Groovy使用`Map`参数生成构造函数，并添加我们自己的构造函数。 属性也可以是final，我们仍然可以使用带有`Map`参数的构造函数。

首先，我们在创建POGO时查看Groovy中的默认行为：

```groovy
// Simple POGO.
// Groovy adds Map argument
// constructor to the class.
class Person {
    String name
    String alias
    List<String> likes
}
 
// Create Person object using
// the Map argument constructor.
// We can use named arguments,
// with the name of key being
// the property name. Groovy
// converts this to Map.
def mrhaki =
    new Person(
        alias: 'mrhaki',
        name: 'Hubert Klein Ikkink',
        likes: ['Groovy', 'Gradle'])
         
assert mrhaki.alias == 'mrhaki'
assert mrhaki.name == 'Hubert Klein Ikkink'
assert mrhaki.likes == ['Groovy', 'Gradle']
 
 
// Sample class with already
// a constructor. Groovy cannot
// create a Map argument constructor now.
class Student {
    String name
    String alias
     
    Student(String name) {
        this.name = name
    }
}
 
 
import static groovy.test.GroovyAssert.shouldFail
 
// When we try to use named arguments (turns into a Map)
// in the constructor we get an exception.
def exception = shouldFail(GroovyRuntimeException) {
    def student =
        new Student(
            name: 'Hubert Klein Ikkink',
            alias: 'mrhaki')
}
 
assert exception.message.startsWith('failed to invoke constructor: public Student(java.lang.String) with arguments: []')
assert exception.message.endsWith('reason: java.lang.IllegalArgumentException: wrong number of arguments')

```

现在让我们在下一个例子中使用`@MapConstructor`注释：

```groovy
import groovy.transform.MapConstructor
 
@MapConstructor
class Person {
    final String name // AST transformation supports read-only properties.
    final String alias
    List<String> likes
}
 
// Create object using the Map argument constructor.
def mrhaki =
    new Person(
        name: 'Hubert Klein Ikkink',
        alias: 'mrhaki',
        likes: ['Groovy', 'Gradle'])
         
assert mrhaki.name == 'Hubert Klein Ikkink'
assert mrhaki.alias == 'mrhaki'
assert mrhaki.likes == ['Groovy', 'Gradle']
 
// Using the annotation the Map argument
// constructor is added, even though we
// have our own constructor as well.
@MapConstructor
class Student {
    String name
    String alias
     
    Student(String name) {
        this.name = name
    }
}
 
def student =
    new Student(
        name: 'Hubert Klein Ikkink',
        alias: 'mrhaki')
         
assert student.name == 'Hubert Klein Ikkink'
assert student.alias == 'mrhaki'
```

AST转换支持几个属性。 我们可以使用属性`includes`和`excludes`来包含或排除将在`Map`参数构造函数中获取值的属性。 在下面的例子中，我们看到了如何使用`includes`属性：

```groovy
import groovy.transform.MapConstructor
 
@MapConstructor(includes = 'name')
class Person {
    final String name
    final String alias
    List<String> likes
}
 
// Create object using the Map argument constructor.
def mrhaki =
    new Person(
        name: 'Hubert Klein Ikkink',
        alias: 'mrhaki',
        likes: ['Groovy', 'Gradle'])
         
assert mrhaki.name == 'Hubert Klein Ikkink'
assert !mrhaki.alias
assert !mrhaki.likes
```

我们可以使用属性`pre`和`post`通过AST转换添加在生成的代码之前或之后执行的自定义代码。 我们使用需要执行的代码为这些属性分配一个`Closure`。

在下一个示例中，我们使用代码设置`pre`属性，如果未通过构造函数设置，则该代码计算`alias`属性值：

```
// If alias is set in constructor use it, otherwise
// calculate alias value based on name value.
@MapConstructor(post = { alias = alias ?: name.split().collect { it[0] }.join() })
class Person {
    final String name // AST transformation supports read-only properties.
    final String alias
    List<String> likes
}
 
// Set alias in constructor.
def mrhaki =
    new Person(
        name: 'Hubert Klein Ikkink',
        alias: 'mrhaki',
        likes: ['Groovy', 'Gradle'])
         
assert mrhaki.name == 'Hubert Klein Ikkink'
assert mrhaki.alias == 'mrhaki'
assert mrhaki.likes == ['Groovy', 'Gradle']
 
// Don't set alias via constructor.
def hubert =
    new Person(
        name: 'Hubert A. Klein Ikkink')
         
assert hubert.name == 'Hubert A. Klein Ikkink'
assert hubert.alias == 'HAKI'
assert !hubert.likes
```

用Groovy 2.5.0编写。

# 增强Java 8 Stream

Groovy 2.5.0添加了几种方法来使Java 8 Streams更加`Groovy`。 首先，将`toList`和`toSet`方法添加到`Stream`类中。 这些方法将使用`Stream.collect`方法将流转换为`List`和`Set`，并使用`Collectors.toList`和`Collectors.toSet`作为参数。 此外，我们可以使用添加到所有数组对象的`stream`方法将任何数组对象转换为`Stream`。

在下面的例子中，我们使用支持将数组转换为`Stream`，然后从流中获取`List`和`Set`：

```groovy
def sample = ['Groovy', 'Gradle', 'Grails', 'Spock'] as String[]
 
def result = sample.stream()  // Use stream() on array objects
                   .filter { s -> s.startsWith('Gr') }
                   .map { s -> s.toUpperCase() }
                   .toList()  // toList() added to Stream by Groovy
                    
assert result == ['GROOVY', 'GRADLE', 'GRAILS']
 
 
def numbers = [1, 2, 3, 1, 4, 2, 5, 6] as int[]
 
def even = numbers.stream()  // Use stream() on array objects
                  .filter { n -> n % 2 == 0 }
                  .toSet()  // toSet() added to Stream
                   
assert even == [2, 4, 6] as Set
```

用Groovy 2.5.0编写。

# 在范围中使用字符串值

我们可以使用简单的语法在Groovy中使用范围，其中范围的起始值和结束值由`..`分隔为包含范围，而`..<`作为独占范围，如我们在[上一篇文章](http://mrhaki.blogspot.com/2009/09/groovy-goodness-keep-your-values-in.html)中所见。 范围的值主要是数字或`enum`值。 但我们也可以使用`String`值来定义范围。 Groovy将检查`String`值是否相同，以及除最后一个字符之外的值是否相同。 然后，基于字符的`int`值，`String`值的最后一个字符的自然排序用于创建范围值。

在下面的示例中，我们使用`String`值定义了几个范围。 我们甚至可以使用`String`值定义反向范围。

```groovy
// Range is defined based on int
// value of character.
def characters = 'A'..'F'
 
assert characters.from == 'A'
assert characters.to == 'F'
assert characters.toList() == ['A', 'B', 'C', 'D', 'E', 'F']
assert characters.step(2) == ['A', 'C', 'E']
 
 
// We can create a reverse range
// also based on the int value
// of the character.
def sample = '&'..'!'
 
assert sample.toList() == ['&', '%', '$', '#', '"', '!']
assert sample.reverse
assert sample.from == '!'
assert sample.to == '&'
 
 
// We can use String values and
// the last character is used
// to create a range. Therefore
// the last character must be valid
// to create a range from.
def groovyRange = 'Groovy10'..<'Groovy15'
 
assert groovyRange.from == 'Groovy10'
assert groovyRange.to == 'Groovy14'
assert groovyRange.toList() == ['Groovy10', 'Groovy11', 'Groovy12', 'Groovy13', 'Groovy14']
 
 
// Also works in reverse.
def groovyReverse = 'Groovy19'..'Groovy15'
 
assert groovyReverse.reverse
assert groovyReverse.from == 'Groovy15'
assert groovyReverse.to == 'Groovy19'
assert groovyReverse.toList() == ['Groovy19', 'Groovy18', 'Groovy17', 'Groovy16', 'Groovy15']
 
 
import static groovy.test.GroovyAssert.shouldFail
 
// Should fail because String values,
// except for the last character, should
// be the same.
shouldFail(IllegalArgumentException) {
    def invalidRange = 'Groovy15'..'Groovy20'
}
```

用Groovy 2.5.0编写。




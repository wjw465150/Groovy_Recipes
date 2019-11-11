# 使用ExpandoMetaClass动态地向类添加方法

我们可以动态地向Groovy中的类添加新的行为，比如方法。 所以这意味着一个方法不会添加到源代码中的类定义中，而是添加到应用程序已经运行的类定义中。 为此，Groovy为所有类添加了一个`metaClass`属性。 这个属性的类型是`ExpandoMetaClass`。 我们可以将方法（也是静态的），属性，构造函数分配给`metaClass`属性，并将定义的行为动态添加到类定义中。 在我们添加了行为之后，我们可以创建类的新实例并调用方法，构造函数并像以前一样访问属性。

```groovy
//我们将方法rightShift添加到List类。
//实现只是调用List的remove方法
//提供的参数。
List.metaClass.rightShift {
    delegate.remove it
}

def list = ['one', 'two', 'three', 'four']
assert 4 == list.size()

list.rightShift 'two'
assert 3 == list.size()
assert ['one', 'three', 'four'] == list

// Operator overloading in action: rightShift is >>
list >> 'one'
assert 2 == list.size()
assert ['three', 'four'] == list


//我们还可以向特定实例而不是类添加行为。
//注意我们使用实例列表而不是类List来分配
//方法groovy到metaClass属性。
list.metaClass.groovy {
    delegate.collect { it + ' groovy' }
}

assert ['three groovy', 'four groovy'] == list.groovy()

def newList = ['a', 'b']
try {
    newList.groovy()  // groovy method was added to list instance not List class.
    assert false
} catch (e) {
    assert e instanceof MissingMethodException
}
```

# 创建一个单例类

在Groovy中创建单例类很简单。 我们只需要使用`@Singleton`转换注释，并为我们生成一个完整的单例类。

```groovy
package com.mrhaki.blog

// 旧式单例类
public class StringUtil {
    private static final StringUtil instance = new StringUtil();

    private StringUtil() {
    }

    public static StringUtil getInstance() { 
        return instance;
    }
    
    int count(text) { 
        text.size() 
    }
}

assert 6 == StringUtil.instance.count('mrhaki')

//使用@Singleton创建有效的单例类。
//我们也可以使用@Singleton(lazy=true)进行延迟加载
//单例类
@Singleton 
class Util {
    int count(text) {
        text.size()
    }
}

assert 6 == Util.instance.count("mrhaki")

try {
    new Util()
} catch (e) {
    assert e instanceof RuntimeException
    assert "Can't instantiate singleton com.mrhaki.blog.Util. Use com.mrhaki.blog.Util.instance" == e.message
}
```

# 简单的`@ToString`注解

从Groovy 1.8开始，我们可以使用`@ToString`注释来轻松创建`toString()`方法。 我们只需要在类定义中添加注释，我们就可以获得类的属性的格式良好的输出。

我们甚至可以自定义我们想要在输出中看到的内容。 如果我们添加属性`includeNames=true`，我们可以在`toString()`输出中看到我们类的属性的名称。 默认情况下，只将属性添加到输出中，但我们也可以包含字段以及注释属性`includeFields=true`。 要排除属性，我们使用属性`excludes`，并在输出中用逗号分隔我们不想要的属性的名称。

最后，我们可以包含一个超类的属性，其注释属性为`includeSuper=true`。

让我们看一下`@ToString`中的几个样本：

```groovy
// Most simple implementation of toString.
import groovy.transform.ToString

@ToString
class Person {
    String name
    List likes
    private boolean active = false
}

def person = new Person(name: 'mrhaki', likes: ['Groovy', 'Java'])

assert person.toString() == 'Person(mrhaki, [Groovy, Java])'
// includeNames to output the names of the properties.
import groovy.transform.ToString

@ToString(includeNames=true)
class Person {
    String name
    List likes
    private boolean active = false
}

def person = new Person(name: 'mrhaki', likes: ['Groovy', 'Java'])

assert person.toString() == 'Person(name:mrhaki, likes:[Groovy, Java])'
// includeFields to not only output properties, but also field values.
import groovy.transform.ToString

@ToString(includeNames=true, includeFields=true)
class Person {
    String name
    List likes
    private boolean active = false
}

def person = new Person(name: 'mrhaki', likes: ['Groovy', 'Java'])

assert person.toString() == 'Person(name:mrhaki, likes:[Groovy, Java], active:false)'
// Use includeSuper to include properties from super class in output.
import groovy.transform.ToString

@ToString(includeNames=true)
class Person {
    String name
    List likes
    private boolean active = false
}

@ToString(includeSuper=true, includeNames=true)
class Student extends Person {
    List courses
}

def student = new Student(name: 'mrhaki', likes: ['Groovy', 'Java'], courses: ['IT', 'Business'])

assert student.toString() == 'Student(courses:[IT, Business], super:Person(name:mrhaki, likes:[Groovy, Java]))'
// excludes active field and likes property from output
import groovy.transform.ToString

@ToString(includeNames=true, includeFields=true, excludes='active,likes')
class Person {
    String name
    List likes
    private boolean active = false
}

def person = new Person(name: 'mrhaki', likes: ['Groovy', 'Java'])

assert person.toString() == 'Person(name:mrhaki)'
```

# 使用EqualsAndHashCode注解生成equals和hashcode方法

Groovy 1.8中有很多新的字节码生成注释。 其中一个是`@EqualsAndHashCode`注释。 使用此注释，为类生成`equals()`和`hashCode()`方法。 `hashCode()`方法是使用Groovy`org.codehaus.groovy.util.HashCodeHelper`实现的（遵循书中的算法* Effective Java *）。 `equals()`方法查看类的所有单个属性，以查看两个对象是否相同。

我们甚至可以包括类字段而不是仅包含用于生成两种方法的属性。 在分配注释时，我们只需要使用`includeFields=true`。

要包含对超类的调用，我们使用注释属性`callSuper`并赋值'true`。 最后，我们还可以从哈希码计算或相等比较中排除属性或字段。 我们使用注释属性`excludes`，我们可以分配属性和字段名称列表。

```groovy
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includeFields=true)
class User {
    String name
    boolean active
    List likes
    private int age = 37
}

def user = new User(name: 'mrhaki', active: false, likes: ['Groovy', 'Java'])
def mrhaki = new User(name: 'mrhaki', likes: ['Groovy', 'Java'])
def hubert = new User(name: 'Hubert Klein Ikkink', likes: ['Groovy', 'Java'])

assert user == mrhaki
assert mrhaki != hubert

Set users = new HashSet()
users.add user
users.add mrhaki
users.add hubert
assert users.size() == 2
```

# 使一个类不可变

创建不可变对象，创建后不能更改。 这使得不可变对象在并发和函数编程中非常有用。 要将Java类定义为不可变，我们必须将所有属性定义为readonly和private。 只有构造函数可以设置属性的值。 [Groovy文档](http://groovy.codehaus.org/Immutable+AST+Macro) 具有适用于不可变对象的规则的完整列表。 使类不可变的Java代码很冗长，特别是因为需要重写`hashCode（）`，`equals（）`和`toString（）`方法。

Groovy有`@ Immutable`转换为我们完成所有工作。 我们只需要在类定义中定义`@Imputable`，我们为这个类创建的任何对象都是一个不可变对象。 Groovy按照不可变对象的规则生成一个类文件。 所以所有属性都是readonly，构造函数是为了设置属性，生成`hashCode()`，`equals()`和`toString()`方法的实现，和 [more](http://groovy.codehaus.org/gapi/groovy/lang/Immutable.html)。

```groovy
@Immutable class User {
    String username, email
    Date created = new Date()
    Collection roles
}

def first = new User(username: 'mrhaki', email: 'email@host.com', roles: ['admin', 'user'])
assert 'mrhaki' == first.username
assert 'email@host.com' == first.email
assert ['admin', 'user'] == first.roles
assert new Date().after(first.created)
try {
    // Properties are readonly.
    first.username = 'new username'
} catch (ReadOnlyPropertyException e) {
    assert 'Cannot set readonly property: username for class: User' == e.message
}
try {
    // Collections are wrapped in immutable wrapper classes, so we cannot
    // change the contents of the collection.
    first.roles << 'new role'
} catch (UnsupportedOperationException e) {
    assert true
}


def date = new Date(109, 8, 16)
def second = new User('user', 'test@host.com', date, ['user'])
assert 'user' == second.username
assert 'test@host.com' == second.email
assert ['user'] == second.roles
assert '2009/08/16' == second.created.format('yyyy/MM/dd')
assert date == second.created
assert !date.is(second.created)  // Date, Clonables and arrays are deep copied.
// toString() implementation is created.
assert 'User(user, test@host.com, Wed Sep 16 00:00:00 UTC 2009, [user])' == second.toString() 


def third = new User(username: 'user', email: 'test@host.com', created: date, roles: ['user'])
// equals() method is also generated by the annotation and is based on the
// property values.
assert third == second
```

# 元组构造函数创建

Groovy 1.8添加了`@TupleConstructor`注释。 通过这个注释，我们可以在编译时自动创建一个元组构造函数。 因此构造函数可以在编译的类中找到。 对于类中的每个属性，将使用默认值创建构造函数中的参数。 类中定义的属性的顺序还定义了构造函数中参数的顺序。 因为参数具有默认值，所以我们可以使用Groovy语法，并在使用构造函数时将参数留在参数列表的末尾。

我们还可以包含字段作为构造函数参数。 我们使用注释属性`includeFields=true`来激活它。

如果我们在类中定义构造函数而不是`TupleConstructor`注释将不会创建额外的构造函数。 但我们可以使用属性值`force=true`覆盖此行为。 我们必须确保自己没有构造函数冲突，因为现在注释将创建额外的构造函数。

如果我们的类扩展了另一个类，并且我们想要包含超类的属性或字段，我们可以使用属性`includeSuperProperties`和`includeSuperFields`。 我们甚至可以指示注释在构造函数中创建代码，以使用属性调用超类的超级构造函数。 我们必须设置注释属性`callSuper=true`来实现这一点。

```groovy
import groovy.transform.TupleConstructor

@TupleConstructor()
class Person {
    String name
    List likes
    private boolean active = false
}

def person = new Person('mrhaki', ['Groovy', 'Java'])

assert person.name == 'mrhaki'
assert person.likes == ['Groovy', 'Java']

person = new Person('mrhaki')

assert person.name == 'mrhaki'
assert !person.likes
// includeFields in the constructor creation.
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields=true)
class Person {
    String name
    List likes
    private boolean active = false

    boolean isActivated() { active }
}

def person = new Person('mrhaki', ['Groovy', 'Java'], true)

assert person.name == 'mrhaki'
assert person.likes == ['Groovy', 'Java']
assert person.activated
// use force attribute to force creation of constructor
// even if we define our own constructors.
import groovy.transform.TupleConstructor

@TupleConstructor(force=true)
class Person {
    String name
    List likes
    private boolean active = false

    Person(boolean active) {
        this.active = active
    }

    boolean isActivated() { active }
}

def person = new Person('mrhaki', ['Groovy', 'Java'])

assert person.name == 'mrhaki'
assert person.likes == ['Groovy', 'Java']
assert !person.activated

person = new Person(true)

assert person.activated
// include properties and fields from super class.
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields=true)
class Person {
    String name
    List likes
    private boolean active = false

    boolean isActivated() { active }
}

@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
class Student extends Person {
    List courses
}

def student = new Student('mrhaki', ['Groovy', 'Java'], true, ['IT'])

assert student.name == 'mrhaki'
assert student.likes == ['Groovy', 'Java']
assert student.activated
assert student.courses == ['IT']
```

# 使用Builder AST 转换为流式API

从Groovy 2.3开始，我们可以使用`@Builder` AST转换轻松地为我们的类创建一个流畅的API。 我们可以将注释应用于我们的类，结果类文件将具有支持流畅API的所有必要方法。 我们可以自定义如何使用不同的注释参数生成流畅的API。 在Groovy代码中，我们已经可以[使用`with`方法](http://mrhaki.blogspot.com/2009/09/groovy-goodness-with-method.html) 有一个简洁的方法来设置属性值或使用 命名的构造函数参数。 但是如果我们的类需要从Java中使用，那么为Java开发人员提供一个流畅的API来为我们的Groovy类做很好。

在下面的示例中，我们将`@Builder`注释应用于具有一些属性的简单类`Message`。 我们将所有内容保留为默认设置，然后生成的`Message`类文件将有一个新的`builder`方法，该方法返回一个内部帮助器类，我们可以使用它来设置我们的属性。 对于每个属性，它们是一个带有属性名称的新方法，因此我们可以设置一个值。 最后，我们的类包含一个`build`，它将返回一个具有正确属性值的`Message`类的新实例。

```groovy
import groovy.transform.builder.Builder

@Builder
class Message {
    String from, to, subject, body
}

def message = Message
        .builder()  // New internal helper class.
        .from('mrhaki@mrhaki.com')  // Method per property.
        .to('mail@host.nl')
        .subject('Sample mail')
        .body('Groovy rocks!')
        .build()  // Create instance of Message

assert message.body == 'Groovy rocks!'
assert message.from == 'mrhaki@mrhaki.com'
assert message.subject == 'Sample mail'
assert message.to == 'mail@host.nl'
//If we want to change the names of the builder and build methods we can 
//use the annotation parameters builderMethodName andbuildMethodName:

import groovy.transform.builder.Builder

@Builder(builderMethodName = 'initiator', buildMethodName = 'create')
class Message {
    String from, to, subject, body
}

def message = Message.initiator()
        .from('mrhaki@mrhaki.com')
        .body('Groovy rocks!')
        .create()

assert message.body == 'Groovy rocks!'
assert message.from == 'mrhaki@mrhaki.com'
//We see that for each property a corresponding method is generated. We 
//can also customize the prefix for the generated method name with the 
//annotation parameter prefix. In the following sample we define the 
//prefix assign for the method names:

import groovy.transform.builder.Builder

@Builder(prefix = 'assign')
class Message {
    String from, to, subject, body
}

def message = Message.builder()
        .assignFrom('mrhaki@mrhaki.com')
        .assignBody('Groovy rocks!')
        .build()

assert message.body == 'Groovy rocks!'
assert message.from == 'mrhaki@mrhaki.com'
//Finally we can also include and exclude properties to need to be 
//included or excluded from our fluent API. We use the annotation //parametersincludes and excludes to define the names of the properties. 
//This can be a list or a comma separated list of names.

import groovy.transform.builder.Builder

@Builder(excludes = 'body' /* or includes = 'from,to,subject' */)
class Message {
    String from, to, subject, body
}

def message = Message.builder()
        .from('mrhaki@mrhaki.com')
        .to('mail@host.nl')
        .subject('Groovy 2.3 is released')
        .build()

assert message.from == 'mrhaki@mrhaki.com'
assert message.subject == 'Groovy 2.3 is released'

try {
    message = Message.builder().body('Groovy rocks!').build()
} catch (MissingMethodException e) {
    assert e.message.readLines().first() ==
            'No signature of method: static Message.body() is applicable for argument types: (java.lang.String) values: [Groovy rocks!]'
}
```

`@Builder` AST转换还检查`@Canonical` AST转换是否应用于类。 对于生成的构建器代码，还包括或排除在`@Canonical`转换中定义的任何包含或排除的属性。

我们可以使用`builderStrategy`注释参数定义`SimpleStrategy`策略。 然后生成的类将没有单独的内部帮助器构建器类和构建方法。 默认的`prefix`设置为`set`，但如果我们想要，我们可以更改：

```groovy
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = 'assign')
class Message {
    String from, to, subject, body
}

def message = new Message()
        .assignFrom('mrhaki@mrhaki.com')  // Method per property.
        .assignTo('mail@host.nl')
        .assignSubject('Sample mail')
        .assignBody('Groovy rocks!')

assert message.body == 'Groovy rocks!'
assert message.from == 'mrhaki@mrhaki.com'
assert message.subject == 'Sample mail'
assert message.to == 'mail@host.nl'
```

我们将在未来的博客文章中看到`@ Builder`注释的其他功能。

# 创建索引属性Getter和Setter方法

在Groovy中，我们可以在类中定义属性，并自动在类文件中生成这些属性的getter和setter方法。 如果我们有一个Collection类型属性，我们通常会获得此属性的`get/set`方法。 但是根据JavaBean规范，我们可以将Collection类型属性定义为索引属性。 这意味着我们需要一个带索引参数的额外`get/set`方法，因此我们可以直接在属性中设置元素的值：

```groovy
//Methods to access individual values
public PropertyElement getPropertyName(int index)
public void setPropertyName(int index, PropertyElement element)

/Methods to access the entire indexed property array
public PropertyElement[] getPropertyName()
public void setPropertyName(PropertyElement element[])
```

通常，如果我们在Groovy代码中使用我们的类，我们不需要那些额外的方法，因为我们可以通过GPath来访问和设置Collection类型属性中的元素。 但是假设我们的类需要从Java代码或IDE访问，我们需要这些额外的方法。 我们只需要将`@IndexedProperty`注释添加到我们的属性中，我们就可以得到我们想要的额外的getter和setter方法：

```groovy
import groovy.transform.IndexedProperty

class Group {
    String name
    List members = []
}

class IndexedGroup {
    String name
    @IndexedProperty List members = []
}

def group = new Group(name: 'Groovy')
group.members[0] = 'mrhaki'
group.members[1] = 'Hubert'
assert 2 == group.members.size()
assert ['mrhaki', 'Hubert'] == group.members

try {
    group.setMembers(0, 'hubert') // Not index property
} catch (MissingMethodException e) {
    assert e
}

def indexedGroup = new IndexedGroup(name: 'Grails')
indexedGroup.members[0] = 'mrhaki'
indexedGroup.setMembers 1, 'Hubert'
assert 2 == indexedGroup.members.size()
assert 'mrhaki' == indexedGroup.getMembers(0)
assert 'Hubert' == indexedGroup.members[1]
```

# 填充字符串

Groovy使用几个填充方法扩展了`String`类。 这些方法允许我们定义一个固定的宽度`String`值必须占用。 如果`String`本身小于固定宽度，则用空格或任何其他字符或我们定义的`String`填充空格。 我们可以在`String`的左侧或右侧或左右两侧填充，并将`String`放在中心。

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
```

```
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

# Switch语句

The Java switch statement looks pale compared to Groovy's switch statement. In Groovy we can use different classifiers for a switch statement instead of only an int or int-derived type. Anything that implements the `isCase()` method can be used as a classifier. Groovy already added an `isCase()`method to `Class` (uses `isInstance`), `Object` (uses (`equals`), collections (uses `contains`) and regular expressions (uses `matches`). If we implement the `isCase` method in our own Groovy classes we can use it as a classifier as well. Finally we can use a closure as a classifier. The closure will be evaluated to a boolean value.

```groovy
def testSwitch(val) {
    def result
    switch (val) {
        case ~/^Switch.*Groovy$/:
            result = 'Pattern match'
            break
        case BigInteger:
            result = 'Class isInstance'
            break
        case 60..90:
            result = 'Range contains'
            break
        case [21, 'test', 9.12]:
            result = 'List contains'
            break
        case 42.056:
            result = 'Object equals'
            break
        case { it instanceof Integer && it < 50 }:
            result = 'Closure boolean'
            break
        default:
            result = 'Default'
            break
    }    
    result
}

assert 'Pattern match' == testSwitch("Switch to Groovy")
assert 'Class isInstance' == testSwitch(42G)
assert 'Range contains' == testSwitch(70)
assert 'List contains' == testSwitch('test')
assert 'Object equals' == testSwitch(42.056)
assert 'Closure boolean' == testSwitch(20)
assert 'Default' == testSwitch('default')
```

# With方法

Groovy has a with method we can use to group method calls and property access to an object. The with method accepts a closure and every method call or property access in the closure applies to the object if applicable. The method is part of [Groovy's extensions](http://groovy.codehaus.org/groovy-jdk/java/lang/Object.html) to the `java.lang.Object` class. Let's see this with an example:

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

# 将字符串转换为枚举

After reading [Groovy, State of the Union - Groovy Grails eXchange 2010 by Guillaume Laforge](http://www.slideshare.net/glaforge/groovy-state-of-the-union-groovy-grails-exchange-2010-guillaume-laforge-6190839) I discovered that in Groovy 1.7.6 we can transform a String into a Enum value. We can use type coersion or the `as` keyword to turn a String or GString into a corresponding Enum value (if possible).

```groovy
enum Compass {
  NORTH, EAST, SOUTH, WEST
}
 
// Coersion with as keyword.
def north = 'NORTH' as Compass
assert north == Compass.NORTH
 
// Coersion by type.
Compass south = 'south'.toUpperCase()
assert south == Compass.SOUTH
 
def result = ['EA', 'WE'].collect {
  // Coersion of GString to Enum.
  "${it}ST" as Compass
}
assert result[0] == Compass.EAST
assert result[1] == Compass.WEST

```

# 使用数组

Groovy supports arrays just like in Java. We only get a lot more methods because of the [GDK extensions](http://groovy.codehaus.org/groovy-jdk/primitive-types/T[].html) added to arrays. The only we thing we need to consider is the way we initialize arrays. In Java we can define and populate an array with the following code:`String[] s = new String[] { "a", "b" };`, but in Groovy we cannot use this syntax. In Groovy the previous statement would become`String[] s = ["a", "b"] as String[]`.

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

# 使用闭包创建简单的构建器

在Groovy中，我们可以使用预定义的构建器，如`JsonBuilder`或`MarkupBuilder`来创建数据或文本结构。 只需使用闭包即可轻松创建自己的构建器。 构建器中的节点只是一个方法，我们可以使用闭包作为方法的参数，以在构建器层次结构中创建新级别。

我们可以在构建器语法中使用预定义的方法名，但也可以通过实现`methodmissing`方法使用动态的或未知的方法名。对于我们可以使用`real property`方法或通过实现`propertyMissing `方法来实现的属性也是一样。

在我们的示例中，我们创建了一个新的构建器来为旅行航班创建一个`Reservation`对象。在构建器中，我们可以定义乘客列表、目的地机场、离港机场以及该航班是否是双向航班。

```groovy
// Builder syntax to create a reservation with passengers,
// departing and destination airport and make it a 2-way flight.
def reservation = new ReservationBuilder().make {
    passengers {
        name 'mrhaki'
        name 'Hubert A. Klein Ikkink'
    }
    from 'Schiphol, Amsterdam'
    to 'Kastrup, Copenhagen'
    retourFlight
}

assert reservation.flight.from == new Airport(name: 'Schiphol', city: 'Amsterdam')
assert reservation.flight.to == new Airport(name: 'Kastrup', city: 'Copenhagen')
assert reservation.passengers.size() == 2
assert reservation.passengers == [new Person(name: 'mrhaki'), new Person(name: 'Hubert A. Klein Ikkink')]
assert reservation.retourFlight


// ----------------------------------------------
// Builder implementation and supporting classes.
// ----------------------------------------------
import groovy.transform.*

@Canonical
class Reservation {
    Flight flight = new Flight()
    List<Person> passengers = []
    Boolean retourFlight = false
}

@Canonical
class Person { String name }

@Canonical
class Airport { String name, city }

@Canonical
class Flight { Airport from, to }

// The actual builder for reservations.
class ReservationBuilder {
    // Reservation to make and fill with property values.
    Reservation reservation

    private Boolean passengersMode = false

    Reservation make(Closure definition) {
        reservation = new Reservation()

        runClosure definition

        reservation
    }

    void passengers(Closure names) {
        passengersMode = true

        runClosure names

        passengersMode = false
    }

    void name(String personName) {
        if (passengersMode) {
            reservation.passengers << new Person(name: personName)
        } else {
            throw new IllegalStateException("name() only allowed in passengers context.")
        }
    }

    def methodMissing(String name, arguments) {
        // to and from method calls will set flight properties
        // with Airport objects.
        if (name in ['to', 'from']) {
            def airport = arguments[0].split(',')
            def airPortname = airport[0].trim()
            def city = airport[1].trim()
            reservation.flight."$name" = new Airport(name: airPortname, city: city)
        }
    }

    def propertyMissing(String name) {
        // Property access of retourFlight sets reservation
        // property retourFlight to true.
        if (name == 'retourFlight') {
            reservation.retourFlight = true
        }
    }

    private runClosure(Closure runClosure) {
        // Create clone of closure for threading access.
        Closure runClone = runClosure.clone()

        // Set delegate of closure to this builder.
        runClone.delegate = this  //@wjw_note: 此句很重要,改变delegate的指向,使其只指向builder

        // And only use this builder as the closure delegate.
        runClone.resolveStrategy = Closure.DELEGATE_ONLY  //@wjw_note: 此句也很重要,只让builder成为delegate

        // Run closure code.
        runClone()
    }

}

```

# 解决与构建器的命名冲突

使用Groovy，我们可以使用例如MarkupBuilder或JSONBuilder来创建XML或JSON内容。 构建器是创建内容的一种非常优雅的方式。 Groovy中的大多数构建器使用`invokeMethod`和`getProperty`和`setProperty`方法来动态构建内容。 但这也意味着如果我们的构建器节点名称与运行构建器的代码的本地上下文中的方法或属性名称相同，那么我们就会出现命名冲突。 让我们看一下这个简单的例子：

```groovy
import groovy.xml.*

def body = []

def writer = new StringWriter()
def builder = new MarkupBuilder(writer)
builder.message {
    body(contentType: 'plain') {
        text 'Simple message'
    }
}

def contents = writer.toString()
println contents
```

当我们运行此代码时，我们收到一条错误消息：

```
groovy.lang.MissingMethodException: No signature of method: java.util.ArrayList.call() is applicable for argument types: (java.util.LinkedHashMap, xmlmessage$_run_closure1_closure2) values: [[contentType:plain], xmlmessage$_run_closure1_closure2@50502819]
Possible solutions: tail(), wait(), last(), any(), max(), clear()
 at xmlmessage$_run_closure1.doCall(xmlmessage.groovy:7)
 at xmlmessage$_run_closure1.doCall(xmlmessage.groovy)
 at xmlmessage.run(xmlmessage.groovy:6)
```

Groovy尝试使用我们的books变量的`ArrayList`类来执行`call`方法，该方法有两个类型为`LinkedHashMap`和closure的参数。 因此构建器找到了我们的局部变量，构建器尝试使用此变量，这会导致显示的错误。

下面的示例显示了如果我们有一个与构建器中的节点同名的本地方法会发生什么：

```groovy
import groovy.xml.*

def body(value) {
    println "body contents is $value"
}

def writer = new StringWriter()
def builder = new MarkupBuilder(writer)
builder.message {
    body {
        text 'Simple message'
    }
}

def contents = writer.toString()
println contents
```

如果我们运行此脚本，我们会得到以下输出：

```
body contents is 
<message>
  <text>Simple message</text>
</message>
```

我们使用名称主体的方法与构建器中的主体节点具有相同的签名和名称。 调用我们的本地方法，我们看到`println`语句的输出。

要解决此命名冲突，我们可以更改本地变量或方法的名称。 但这并不总是可行或不可取的。 想象一下，方法或变量是动态添加到我们的类，而不是我们无法更改名称。 但我们也可以稍微更改构建器语法以获得我们想要的内容。

要强制我们的构建器使用构建器的代码来创建内容，我们可以在节点名称前加上`delegate`。 Delegate是我们构建器的闭包上下文。 这样，我们的构建器将不使用任何已定义的变量或方法名称来创建内容。

```groovy
//@wjw_note: 注意此时在builder里delgate已经不等于owner和this了,此时delgate指向builder!

import groovy.xml.*

def body = []

def writer = new StringWriter()
def builder = new MarkupBuilder(writer)
builder.message {
    delegate.body(contentType: 'plain') {
        text 'Simple message'
    }
}

def contents = writer.toString()
println contents
```

当我们运行脚本时，我们得到以下输出：

```
<message>
  <body contentType='plain'>
    <text>Simple message</text>
  </body>
</message>
```

这就是我们想要的输出。

如果我们使用Grails并使用`render`方法创建XML或JSON，我们必须意识到方法在控制器中也是动态可用的。 例如，Grails标记库中的`message`方法是控制器中的方法：

```groovy
package builder.naming

class SampleController {

    def index() {
        render(contenType: 'text/xml') {
            message {
                content 'Contents'
            }
        }
    }
}
```

如果我们调用此控制器，我们将获得以下XML输出：

```
<content>Contents</content>
```

但是如果我们将代码更改为：

```
package builder.naming

class SampleController {

    def index() {
        render(contenType: 'text/xml') {
            delegate.message {
                content 'Contents'
            }
        }
    }
}
```

我们得到以下输出：

```
<message><content>Contents</content></message>
```

# 闭包(Closure)作为一个类

当我们编写Groovy代码时，我们也很有可能编写一些闭包。 例如，如果我们使用集合并使用`each`，`collect`或`find`方法，我们使用闭包作为这些方法的参数。 我们可以为变量赋值闭包，并使用变量名来引用闭包。 但是我们也可以创建`Closure`类的子类来实现闭包。 然后我们在可以使用闭包的地方使用新闭包类的实例。

要将闭包写为类，我们必须继承`Closure`并实现名为`doCall`的方法。 该方法可以接受任意参数，返回类型可以由我们定义。 所以我们没有覆盖超类`Closure`中的方法`doCall`。 但是Groovy将寻找一个名为`doCall`的方法来执行闭包逻辑，并在内部使用`Closure`超类中的方法。

在下面的示例中，我们将一个非常简单的闭包写为一个类，以检查对象是否为数字。 然后我们使用带有`findAll`方法的类的实例来处理对象集合：

```groovy
class IsNumber extends Closure<Boolean> /* return type for closure as generic type */ {

    IsNumber() {
        super(null)
    }

    /**
     * Implementation of closure.
     */
    Boolean doCall(final Object value) {
        // Check if value is a number, if so
        // return true, otherwise false.
        value in Number
    }

}

def list = ['a', 100, 'Groovy', 1, 8, 42.0, true]

def numbers = list.findAll(new IsNumber())

assert numbers == [100, 1, 8, 42.0]
```

使用Groovy 2.3.7编写的代码。



# 确定Map中的最小和最大条目

从Groovy 1.7.6开始，我们可以在`Map`上使用`min()`和`max()`方法。 我们使用闭包来定义最小值或最大值的条件。 如果我们在闭包中使用两个参数，我们必须进行经典比较。 如果第一个参数小于第二个参数，则返回负值;如果它们都相等，则返回零;如果第一个参数大于第二个参数，则返回正值。 如果我们使用单个参数，我们可以返回一个用作`Comparable`的值来确定`Map`中的最大或最小条目。

```groovy
def money = [cents: 5, dime: 2, quarter: 3]

// Determine max entry.
assert money.max { it.value }.value == 5
assert money.max { it.key }.key == 'quarter'  // Use String comparison for key.
assert money.max { a, b ->
    a.key.size() <=> b.key.size() 
}.key == 'quarter'  // Use Comparator and compare key size.

// Determine min entry.
assert money.min { it.value }.value == 2
assert money.min { it.key }.key == 'cents'  // Use String comparison for key.
assert money.min { a, b ->
    a.key.size() <=> b.key.size() 
}.key == 'dime'  // Use Comparator and compare key size.
```

# 删除或获取带有条件的元素

在Groovy中我们可以使用[`drop（）`和`take（）`方法](http://mrhaki.blogspot.com/2011/09/groovy-goodness-take-and-drop-items.html)来 从集合或`String`对象中获取元素。 从Groovy 1.8.7开始，我们也可以使用`dropWhile()`和`takeWhile()`方法，并使用闭包来定义一个条件来停止删除或获取元素。 使用`dropWhile()`方法，我们删除元素或字符，直到闭包中的条件为“true”。 `takeWhile()`方法返回一个集合中的元素或来自`String`的字符，直到闭包的条件为'true`。

在以下示例中，我们将了解如何使用这些方法：

```groovy
def s = "Groovy Rocks!"

assert s.takeWhile { it != 'R' } == 'Groovy '
assert s.dropWhile { it != 'R' } == 'Rocks!'


def list = 0..10

assert 0..4 == list.takeWhile { it < 5 }
assert 5..10 == list.dropWhile { it < 5 }


def m = [name: 'mrhaki', loves: 'Groovy', worksAt: 'JDriven']

assert [name: 'mrhaki'] == m.takeWhile { key, value -> key.length() == 4 }
assert [loves: 'Groovy', worksAt: 'JDriven'] == m.dropWhile { it.key == 'name' }
```

代码是用Groovy 2.0.4编写的

# 在集合中查找数据

Groovy为`Collection`类添加了几种方法来查找集合中的元素。 `findXXX()`方法采用闭包，如果一个元素与闭包中定义的条件匹配，我们得到一个结果。 我们也可以使用`any()`方法来验证是否至少有一个元素适用于闭包条件，或者我们使用`every()`方法来通知所有确认闭包条件的元素。 `any()`和`every()`方法都返回一个`boolean`值。

```groovy
def list = ['Daffy', 'Bugs', 'Elmer', 'Tweety', 'Silvester', 'Yosemite']
assert 'Bugs' == list.find { it == 'Bugs' }
assert ['Daffy', 'Bugs', 'Elmer'] == list.findAll { it.size() < 6 }
assert 1 == list.findIndexOf { name -> name =~ /^B.*/ }  // Start with B.
assert 3 == list.findIndexOf(3) { it[0] > 'S' }  // Use a start index.
assert [0,3,5] == list.findIndexValues { it =~ /(y|Y)/ }  // Contains y or Y.
assert [3,5] == list.findIndexValues(2) { it =~ /(y|Y)/ }
assert 2 == list.findLastIndexOf { it.size() == 5 }
assert 5 == list.findLastIndexOf(1) { it.count('e') > 1 }
assert list.any { it =~ /a/ }
assert list.every { it.size() > 3 }

def map = [name: 'Messages from mrhaki', url: 'http://mrhaki.blogspot.com', blog: true]
def found = map.find { key, value -> key == 'name' }
assert found.key == 'name' && found.value == 'Messages from mrhaki'
found = map.find { it.value =~ /mrhaki/ }
assert found.key == 'name' && found.value == 'Messages from mrhaki'
assert [name: 'Messages from mrhaki', url: 'http://mrhaki.blogspot.com'] == map.findAll { key, value -> value =~ /mrhaki/ }
assert 1 == map.findIndexOf { it.value.endsWith('com') }
assert [1,2] == map.findIndexValues { it.key =~ /l/ }  // All keys with the letter 'l'.
assert 2 == map.findLastIndexOf { it.key =~ /l/ && it.value }
assert map.any { entry -> entry.value }
assert map.every { key, value -> key.size() >= 3 }
```

# 获取集合的索引

由于Groovy 2.4，我们可以使用集合上的`indexes`属性来获得集合中元素的索引。结果我们得到一个`IntRange`对象。

```groovy
def list = [3, 20, 10, 2, 1]
assert list.indices == 0..4


// Combine letters in alphabet
// with position (zero-based).
def alphabet = 'a'..'z'
def alphabetIndices = [alphabet, alphabet.indices].transpose()
// alphabetIndices = [['a', 0], ['b', 1], ...]

// Find position of each letter
// from 'groovy' in alphabet.
def positionInAlphabet = 'groovy'.inject([]) { result, value ->
    result << alphabetIndices.find { it[0] == value }[1] + 1
    result
}

assert positionInAlphabet == [7, 18, 15, 15, 22, 25]
```

# 具有多个闭包的GroupBy

我们可以使用Groovy中的`groupBy()`方法在列表或映射中对元素进行长时间的分组[分组元素](http://mrhaki.blogspot.com/2009/09/groovy-goodness-using-groupby-method.html)。我们传递一个带有分组条件的闭包，以获得包含分组项的映射。由于Groovy 1.8.1，我们可以使用多个闭包来进行分组。我们可以将它用于List和Map对象。

```groovy
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

# 从集合中排序和删除重复项的新方法

在Groovy中，我们可以使用`sort`和`unique`方法对集合进行排序或从集合中删除重复项。 这些方法会更改调用它们的集合。 这是我们可能想要避免的副作用。 因此，`sort`和`unique`方法改变了，我们可以传递一个`boolean`参数来指示原始集合是否应该被更改，或者我们必须有一个新的集合作为方法的结果，保持原始集合不变。 从Groovy 2.4开始，我们有两个新方法，默认返回一个新的集合：`toSorted`和`toUnique`。

在下面的示例中，我们看到了新的方法：

```groovy
@groovy.transform.Sortable
@groovy.transform.ToString
class User {
String username, email
}
 
def mrhaki1 = new User(username: 'mrhaki', email: 'mrhaki@localhost')
def mrhaki2 = new User(username: 'mrhaki', email: 'user@localhost')
def hubert1 = new User(username: 'hubert', email: 'user@localhost')
def hubert2 = new User(username: 'hubert', email: 'hubert@localhost')
 
// We make the list immutable,
// so we check the toSorted and toUnique methods
// do not alter it.
def users = [mrhaki1, mrhaki2, hubert1, hubert2].asImmutable()
 
// toSorted
def sortedUsers = users.toSorted()

// @Sortable adds a compareTo method
// to User class to sort first by username
// and then email.
assert sortedUsers == [hubert2, hubert1, mrhaki1, mrhaki2]
 
// Original list is unchanged.
assert users == [mrhaki1, mrhaki2, hubert1, hubert2]
 
// Use toSorted with closure.
def sortedByEmail = users.toSorted { a, b -> a.email <=> b.email }
assert sortedByEmail == [hubert2, mrhaki1, mrhaki2, hubert1]
 
// Or use toSorted with Comparator.
// @Sortable added static comparatorByProperty
// methods.
def sortedByEmailComparator = users.toSorted(User.comparatorByEmail())
assert sortedByEmailComparator == [hubert2, mrhaki1, mrhaki2, hubert1]

// toUnique with Comparator.
def uniqueUsers = users.toUnique(User.comparatorByUsername())
assert uniqueUsers == [mrhaki1, hubert1]
assert users == [mrhaki1, mrhaki2, hubert1, hubert2]
 
// toUnique with Closure.
def uniqueByEmail = users.toUnique { a, b -> a.email <=> b.email }
assert uniqueByEmail == [mrhaki1, mrhaki2, hubert2]

```

# 可观察的Map 和 List

Groovy提供了[`ObservableMap`](http://groovy.codehaus.org/api/groovy/util/ObservableMap.html)和[`ObservableList`](http://groovy.codehaus.org/api/groovy/util/ObservableList.html)类。 当我们添加，删除或更改地图或列表的内容时，这些类会发出`PropertyChangeEvent`对象。 对于几个动作，我们得到不同的事件类型，我们可以使用`PropertyChangeListener`对象来订阅这些事件。 例如，如果我们添加一个元素，我们可以订阅属性更改事件并获得`PropertyAddedEvent`。

```groovy
import java.beans.*

def event 
// Listener will assign event to global event variable.
def listener = { 
    event = it
} as PropertyChangeListener

/* ObservableList */
def list = ['Groovy', 'rocks', 'the world', true] as ObservableList
list.addPropertyChangeListener(listener)

list << 'More text'
assert event instanceof ObservableList.ElementAddedEvent 
assert 4 == event.index
assert 'More text' == event.newValue

list.remove(3)
assert event instanceof ObservableList.ElementRemovedEvent
assert 3 == event.index

list[0] = 'Grails'
assert event instanceof ObservableList.ElementUpdatedEvent
assert 0 == event.index
assert 'Groovy' == event.oldValue
assert 'Grails' == event.newValue

list.addAll([42, 101])
assert event instanceof ObservableList.MultiElementAddedEvent
assert [42, 101] == event.values

list.removeAll([true, 'More text', 42, 101])
assert event instanceof ObservableList.MultiElementRemovedEvent
assert 3 == list.size()

list.clear()
assert event instanceof ObservableList.ElementClearedEvent
assert ['Grails', 'rocks', 'the world'] == event.values

event = null

// We can define a closure as a filter. The closure is
// executed for each element and if it returns true,
// the property change event is fired.
def strict = new ObservableList({ it.size() > 2 })
strict.addPropertyChangeListener(listener)
strict.addAll(['a', 'ab', 'abc', 'abcd'])
assert ['abc', 'abcd'] == event.values

/* ObservableMap */
event = null

// Extra property change listener to assign to a specific
// property instead of the whole map.
def propEvent
def propListener = { propEvent = it } as PropertyChangeListener

def map = [username: 'mrhaki', email: 'email@host.com', active: true] as ObservableMap
map.addPropertyChangeListener(listener)
map.addPropertyChangeListener("active", propListener)

map.location = "@work"
assert event instanceof ObservableMap.PropertyAddedEvent 
assert 'location' == event.propertyName
assert '@work' == event.newValue
assert !propEvent

map.active = false
assert event instanceof ObservableMap.PropertyUpdatedEvent 
assert propEvent instanceof ObservableMap.PropertyUpdatedEvent
assert true == propEvent.oldValue
assert false == propEvent.newValue
assert 'active' == event.propertyName

map.remove('active')
assert propEvent instanceof ObservableMap.PropertyRemovedEvent
assert 3 == map.size()

map.putAll([car: true, phone: '555-1234'])
assert event instanceof ObservableMap.MultiPropertyEvent 
assert event.events[0] instanceof ObservableMap.PropertyAddedEvent
assert 'car' == event.events[0].propertyName
assert true == event.events[0].newValue
assert event.events[1] instanceof ObservableMap.PropertyAddedEvent
assert 'phone' == event.events[1].propertyName
assert '555-1234' == event.events[1].newValue

map.clear()
assert event instanceof ObservableMap.PropertyClearedEvent
assert [username: 'mrhaki', car: true, phone: '555-1234', location: '@work', email: 'email@host.com'] == event.values

def strictMap = new ObservableMap({ name, value -> name ==~ /^a.*/ })
strictMap.addPropertyChangeListener(listener)
strictMap.putAll([a: 1, b: 2, c: 3])
assert 1 == event.events.size()
assert 'a' == event.events[0].propertyName
assert 1 == event.events[0].newValue
```

# 弹出 并 添加 List中的项

Groovy将`pop`和`push`方法添加到`List`类中。 使用`pop`方法，我们删除列表的最后一个元素。 使用`push`方法，我们在列表的末尾添加一个元素。

```groovy
def list = ['Groovy', 'is', 'great!']

// Remove last item from list
// with pop().
assert list.pop() == 'great!'
assert list == ['Groovy', 'is']

// Remove last item
// which is now 'is'.
list.pop()

// Add new item to end of
// the list (equivalent for add()).
list.push('rocks!')

assert list == ['Groovy', 'rocks!']
```

# 重新审视获取集合中项目的总和

前段时间我写了一篇博文[Groovy Goodness：获取集合中的项目总和](http://mrhaki.blogspot.dk/2009/12/groovy-goodness-getting-sum-of-items-in.html)。 今天，我在[Gr8Conf 2012](http://gr8conf.eu)上介绍了这个小主题，作为“Groovy Hidden Gems”会议的一部分。 其中一位与会者注意到我们计算“Person”对象总和的代码无效。 所以是时候重新审视这个话题了。

所提出的解决方案的问题在于，如果列表中有两个以上的元素，则代码会抛出异常。 `plus()`方法的返回方法是一个`BigDecimal`，因此Groovy尝试使用`Person`类型参数调用`BigDecimal`类上的`plus()`方法。 这不存在，因此例外。

要解决这个问题，我们必须从`plus（）`方法返回一个新的`Product`对象，其中包含`price`属性的总和。 代码现在是：

```groovy
class Product {
    String name
    BigDecimal price

    Product plus(Product other) {
        new Product(price: this.price + other.price)
    }
}
def products = [
    new Product(name: 'laptop', price: 999), 
    new Product(name: 'netbook', price: 395),
    new Product(name: 'iPad', price: 200)
]

assert 1594 == products.sum().price
```

编辑：我要感谢写评论的人。 当前的解决方案侧重于在类中实现`plus()`方法以获得求和值。 但我们也可以使用`sum()`的闭包。 在闭包中，我们定义属性来计算总和。 或者我们可以使用可选的spread运算符来获取所有产品的price属性并调用`sum()`方法。

```groovy
assert products.sum { it.price } == 1594
assert products.price.sum() == 1594
assert products*.price.sum() == 1594
```

# 从列表中取出和删除项目

使用List对象时，我们可以在Groovy中使用很多有用的方法。 从Groovy 1.8.1开始，我们可以使用`take()`和`drop()`方法。 使用`take()`方法，我们从List的开头获取项目。 我们将我们想要的项目数作为参数传递给方法。

要从List的开头删除项目，我们可以使用`drop()`方法。 在这里，我们将要删除的项目数作为参数传递给方法。 请记住原始列表没有改变，`drop()`方法的结果是一个新列表。

```groovy
def list = ['Simple', 'list', 'with', 5, 'items']

assert list.take(1) == ['Simple']
assert list.take(2) == ['Simple', 'list']
assert list.take(0) == []
// Whole list, because we take more items then the size of list
assert list.take(6) == ['Simple', 'list', 'with', 5, 'items']

assert list.drop(1) == ['list', 'with', 5, 'items']
assert list.drop(3) == [5, 'items']
assert list.drop(5) == []
assert list.drop(0) == ['Simple', 'list', 'with', 5, 'items']
assert list == ['Simple', 'list', 'with', 5, 'items']

// After reading Tim Yates' comment I have added 
// more samples showing drop() and take() also work on
// Maps, Iterators, CharSequences and arrays.
def array = ['Rock on!', 'Groovy baby!'] as String[]
assert array.take(1) == ['Rock on!'] as String[]
assert array.drop(1) == ['Groovy baby!'] as String[]

def range = 0..10
assert range.take(2) == [0,1]
assert range.take(4) == 0..3
assert range.drop(5) == 5..10

def map = [1: 'one', 2: 'two', 3: 'three']
assert map.take(2) == [1: 'one', 2: 'two']
assert map.drop(2) == [3: 'three']
assert map.drop(3) == [:]

def s = 'Hello Groovy world!'
assert s.take(5) == 'Hello'
assert s.drop(6) == 'Groovy world!'
```

# 使用List和Map作为构造函数

Groovy中的构造函数可以用经典的Java方法调用，但是我们也可以使用List或Map来创建对象。Groovy支持将List显式强制转换为带有`as`关键字的构造函数。或者，当Groovy查看变量的类型时，我们可以依赖隐式强制来自动将List转换为正确的构造函数调用。

```groovy
// Default constructor invocation:
def url1 = new URL('http', 'www.mrhaki.com', 80, '/')
assert 'http' == url1.protocol
assert 'www.mrhaki.com' == url1.host
assert 80 == url1.port
assert '/' == url1.file
assert '/' == url1.path

// Explicit coersion with as keyword:
def url2 = ['http', 'www.mrhaki.com', 80, '/'] as URL
assert 'http' == url1.protocol
assert 'www.mrhaki.com' == url2.host
assert 80 == url2.port
assert '/' == url2.file
assert '/' == url2.path

// Implicit coersion by type of variable:
URL url3 = ['http', 'www.mrhaki.com', 80, '/'] 
assert 'http' == url3.protocol
assert 'www.mrhaki.com' == url3.host
assert 80 == url3.port
assert '/' == url3.file
assert '/' == url3.path    
```
当我们使用GroovyBeans语法时，我们可以使用带有命名参数的Map来调用构造函数。但是我们也可以使用Groovy提供的显式和隐式强制转换。

```
// GroovyBean: Groovy creates a constructor
// that takes a map as parameter.
class Sample {
    Integer age 
    String name
}

def s1 = new Sample([age: 36, name: 'mrhaki'])
assert 36 == s1.age
assert 'mrhaki' == s1.name

// Explicit coersion with as keyword:
def s2 = [age: 36, name: 'mrhaki'] as Sample
assert 36 == s2.age
assert 'mrhaki' == s2.name

// Implicit coersion (by type of variable):
Sample s3 = [age: 36, name: 'mrhaki']
assert 36 == s3.age
assert 'mrhaki' == s3.name
```

# 检查配置属性是否在ConfigObject中设置

当我们使用`ConfigSlurper`来读取配置文件或脚本时，我们得到一个`ConfigObject`作为返回结果。 我们可以通过简单地引用属性名来获取配置属性的值。 `ConfigObject`实例将解析名称并返回值。 如果我们想检查属性是否有值，我们可以在条件中使用属性名称。 例如`app.active ? 'active' : 'non-active'`。 但是，[Groovy truth](http://mrhaki.blogspot.com/2009/08/groovy-goodness-tell-groovy-truth.html)在这里也有发言权。 如果未设置属性或者值返回`false`（考虑到Groovy事实），条件上下文中的`app.active`将返回`false`。

从Groovy 2.3开始，我们可以使用`isSet`方法在获取值之前检查配置属性是否可用。 通过这种方式，我们可以区分现有属性的不存在属性或“false”值。

```groovy
// Configuration script.
def config = '''
app {
    version = 0
    active = false
}
'''

// Read configuration.
def configuration = new ConfigSlurper().parse(config)

// Check values for app. configuration properties.
configuration.app.with {
    // Existing boolean property.
    // Is property value false or non-existing?
    assert !active
    // Answer: value is false.
    assert isSet('active')


    // Not existing boolean property.
    // Is property value false or non-existing?
    assert !enabled
    // Answer: non-existing.
    assert !isSet('enabled')


    // Non boolean property.
    assert !version
    assert isSet('version')
    assert version == 0
}
```

使用Groovy 2.3编写的代码。

# 将ConfigSlurper与配置脚本一起使用

[`ConfigSlurper`](http://groovy.codehaus.org/gapi/groovy/util/ConfigSlurper.html)类可用于使用配置信息解析Groovy脚本。 这样我们就可以使用真实脚本而不是属性文件来定义应用程序中的配置信息。 在[上一篇文章](http://mrhaki.blogspot.com/2009/08/grassroots-groovy-configuration-with.html)中，我们看到了如何使用Java代码中的`ConfigSlurper`，在这篇文章中我们关注的重点是 在Groovy代码中使用它。

使用`ConfigSlurper`，我们可以将Groovy脚本解析为[`ConfigObject`](http://groovy.codehaus.org/gapi/groovy/util/ConfigObject.html)。 `ConfigObject`是`LinkedHashMap`的子类，包含配置信息。 配置脚本包含使用点表示法或闭包定义的信息。 因为它是一个脚本，我们可以使用所有Groovy构造，或者使用我们想要的任何其他Groovy和Java类。

为了支持每个环境的不同配置设置（例如开发，测试和生产），我们可以在脚本中定义一个特殊的环境部分。 当我们创建一个新的`ConfigSlurper`实例并在构造函数中使用环境名称时，环境部分用于确定值。 如果我们不在构造函数中指定环境，则跳过环境部分。

好的，这是很多解释，让我们看一些代码：

```groovy
// 配置脚本为String，但也可以是URL，文件。
def mail = '''

// 点符号.
mail.hostname = 'localhost'  

// 范围封闭符号.
mail {  
    // Using Groovy constructs.
    ['user', 'password'].each { 
        this."${it}" = 'secret' 
    }
}

// 环境部分.
environments {
    dev {
        mail.hostname = 'local'
    }
    test {
        mail.hostname = 'test'
    }
    prod {
        mail.hostname = 'prod'
    }
}
'''

// 另一个配置脚本.
def app = '''
app {
    version = version()  // Use method in script.
}

// 定义构建版本信息的方法.
def version() {
    "1.0-${releasedate.format('yyyy_MM_dd')}"
}
'''

// 读取prod环境的邮件配置脚本.
def mailConfig = new ConfigSlurper('prod').parse(mail)

//我们可以将信息传递给配置
// setBinding方法
def appSlurper = new ConfigSlurper()
appSlurper.setBinding([releasedate: new Date(109, 9, 10)])
def appConfig = appSlurper.parse(app)

// 两种配置合并为一种。
def config = mailConfig.merge(appConfig)

assert 'prod' == config.mail.hostname
assert 'secret' == config.mail.user
assert 'secret' == config.mail.password
assert '1.0-2009_10_10' == config.app.version
```

# 将Date转换为java.sql.Timestamp

Groovy为我们可以在代码中使用的标准Java类添加了许多方法。 要将简单的`Date`对象转换为`java.sql.Timestamp`，我们可以简单地在`Date`对象上使用`toTimestamp()`方法。

```groovy
import static java.util.Calendar.*

// 创建具有特定年，月和日的日期对象。
def date = new Date()
date.clearTime()
date.set year: 2010, month: AUGUST, date: 10

// 转换为java.sql.Timestamp。
def sqlTimestamp = date.toTimestamp()
assert 'java.sql.Timestamp' == sqlTimestamp.class.name
assert '2010-08-10 00:00:00.0' == sqlTimestamp.toString()
```

# 从现有和设置属性值创建新Date或Calendar

从Groovy 2.2开始，我们可以从现有的`Date`和`Calendar`对象创建一个新的`Date`或`Calendar`对象，并直接更改属性值。 我们必须使用`copyWith()`方法，我们传递一个带有属性名称和值作为参数的map。 新创建的对象将具有原始对象的属性的旧值，并且将覆盖在map中设置的属性。

以下代码显示了新的`copyWith()`方法：

```groovy
import static java.util.Calendar.NOVEMBER

// 创建原始日期.
def date = new Date().clearTime()
date.set(year: 2013, month: NOVEMBER, date: 18)

//使用copyWith获取新的日期和
// immmediatelly设置年份到2014年。
def yearLater = date.copyWith(year: 2014)

assert yearLater.format('dd-MM-yyyy') == '18-11-2014'


// 也适用于日历。
def cal = Calendar.instance
cal.set(year: 2013, month: NOVEMBER, date: 10)

// 使用新的日期值创建新日历。
def newCalendar = cal.copyWith(date: 18)

assert newCalendar.format('dd-MM-yyyy') == '18-11-2013'
```

# 将Reader输入转换为Writer输出

使用Groovy，我们可以立即将来自`Reader`对象(如文件，URL或其他输入类型)的输入转换为`Writer`对象(如文件，URL或其他输出类型)。 Groovy将`transformLine(Writer，Closure)`和`transformChar(Writer，Closure)`方法添加到`Reader`类。 我们需要传递包含转换输出的`Writer`对象作为第一个参数。 第二个参数是一个闭包，其中包含需要应用的转换规则。

```groovy
def reader = new StringReader('''\
Groovy's support
for transforming reader input to writer output.
''')

def writer = new StringWriter()

reader.transformLine(writer) { line ->  
    if (line.matches(~/^Groovy.*/)) {
        line = '>>' + line.replaceAll('Groovy', 'GROOVY') + '<< '
    }
    line
}

def resultTransformLine = writer.toString()

reader = new StringReader(resultTransformLine)
writer = new StringWriter()
reader.transformChar(writer) { ch ->
    ch in ['\n', '\r'] ? '' : ch
}

assert writer.toString() == ">>GROOVY's support<< for transforming reader input to writer output."
```

# 使用连接参数从URL获取文本

很长一段时间，我们可以在Groovy中直接[从URL获取文本](http://mrhaki.blogspot.com/2009/10/groovy-goodness-reading-url-content.html)。 从Groovy 1.8.1开始，我们可以将参数设置为用于获取内容的底层`URLConnection`。 参数作为Map传递给`getText()`方法或者传递给URL的`newReader()`或`newInputStream()`方法。

我们可以设置以下参数：

- connectTimeout in milliseconds
- readTimeout in milliseconds
- useCaches
- allowUserInteraction
- requestProperties is a Map with general request properties

```groovy
// Contents of http://www.mrhaki.com/url.html:
// Simple test document
// for testing URL extensions
// in Groovy.

def url = "http://www.mrhaki.com/url.html".toURL()

// Simple Integer enhancement to make
// 10.seconds be 10 * 1000 ms.
Integer.metaClass.getSeconds = { ->
    delegate * 1000
}

// Get content of URL with parameters.
def content = url.getText(connectTimeout: 10.seconds, readTimeout: 10.seconds,
                          useCaches: true, allowUserInteraction: false,
                          requestProperties: ['User-Agent': 'Groovy Sample Script'])

assert content == '''\
Simple test document
for testing URL extensions
in Groovy.
'''

url.newReader(connectTimeout: 10.seconds, useCaches: true).withReader { reader ->
    assert reader.readLine() == 'Simple test document'
}
```

# 使用JsonBuilder和Pretty Print JSON Text构建JSON

Groovy 1.8增加了JSON支持。 我们可以使用`JsonBuilder`类构建JSON数据结构。 此类的功能与其他构建器类一样。 我们使用值定义一个层次结构，当我们查看String值时，它将转换为JSON输出。 我们注意到语法与`MarkupBuilder`的语法相同。

```groovy
import groovy.json.*

def json = new JsonBuilder()

json.message {
    header {
        from('mrhaki')  // parenthesis are optional
        to 'Groovy Users', 'Java Users'
    }
    body "Check out Groovy's gr8 JSON support."
}

assert json.toString() == '{"message":{"header":{"from":"mrhaki","to":["Groovy Users","Java Users"]},"body":"Check out Groovy\'s gr8 JSON support."}}'

// We can even pretty print the JSON output
def prettyJson = JsonOutput.prettyPrint(json.toString())
assert prettyJson == '''{
    "message": {
        "header": {
            "from": "mrhaki",
            "to": [
                "Groovy Users",
                "Java Users"
            ]
        },
        "body": "Check out Groovy's gr8 JSON support."
    }
}'''
```

# 使用JsonSlurper解析JSON

使用Groovy 1.8，我们可以使用`JsonSlurper`类解析JSON文本。 我们只需要将文本提供给`parseText()`方法，我们就可以将值映射到Maps和Lists。 然后获取内容非常简单：

```groovy
import groovy.json.*

def jsonText = '''
{
    "message": {
        "header": {
            "from": "mrhaki",
            "to": ["Groovy Users", "Java Users"]
        },
        "body": "Check out Groovy's gr8 JSON support."
    }
}       
'''

def json = new JsonSlurper().parseText(jsonText)

def header = json.message.header
assert header.from == 'mrhaki'
assert header.to[0] == 'Groovy Users'
assert header.to[1] == 'Java Users'
assert json.message.body == "Check out Groovy's gr8 JSON support."
```

# 使用StreamingJsonBuilder流式JSON

从Groovy 1.8开始，我们可以使用[JSONBuilder](http://mrhaki.blogspot.com/2011/04/groovy-goodness-build-json-with.html)来创建JSON数据结构。 使用Groovy 1.8.1，我们有一个`JsonBuilder`的变体，它不会在内存中创建数据结构，但会直接向JSON结构的一个编写器流：`StreamingJsonBuilder`。 这在我们不必更改结构并需要以内存有效的方式编写JSON的情况下非常有用。

```groovy
import groovy.json.*

def jsonWriter = new StringWriter()
def jsonBuilder = new StreamingJsonBuilder(jsonWriter)
jsonBuilder.message {
    header {
        from(author: 'mrhaki')  
        to 'Groovy Users', 'Java Users'
    }
    body "Check out Groovy's gr8 JSON support."
}
def json = jsonWriter.toString()
assert json == '{"message":{"header":{"from":{"author":"mrhaki"},"to":["Groovy Users","Java Users"]},"body":"Check out Groovy\'s gr8 JSON support."}}'

def prettyJson = JsonOutput.prettyPrint(json)
assert prettyJson == '''{
    "message": {
        "header": {
            "from": {
                "author": "mrhaki"
            },
            "to": [
                "Groovy Users",
                "Java Users"
            ]
        },
        "body": "Check out Groovy's gr8 JSON support."
    }
}'''


new StringWriter().withWriter { sw ->
    def builder = new StreamingJsonBuilder(sw)

    // Without root element.
    builder name: 'Groovy', supports: 'JSON'

    assert sw.toString() == '{"name":"Groovy","supports":"JSON"}'
}

new StringWriter().with { sw ->
    def builder = new StreamingJsonBuilder(sw)

    // Combine named parameters and closures.
    builder.user(name: 'mrhaki') {
        active true
    }
    
    assert sw.toString() == '{"user":{"name":"mrhaki","active":true}}'    
}

```

# 使用CliBuilder解析命令行参数

Groovy是一种创建脚本的好语言。 大多数情况下，如果我们调用脚本，我们会将参数传递给脚本。 这些参数在我们的脚本中以`String []`数组的形式提供。 例如，为了获得第一个参数，我们可以使用以下代码`s=args[0]`。 要在我们的脚本中进行真正的参数处理，我们使用Groovy的[CliBuilder](http://groovy.codehaus.org/gapi/groovy/util/CliBuilder.html)类。 本课程使用[Jakarta Commons CLI](http://commons.apache.org/cli)。 使用CliBuilder，我们可以定义参数选项并解析参数。 CliBuilder通过允许我们调用不存在的单字母方法来添加Groovyness，这些方法被转换为带有单字母快捷方式的参数选项。 此外，我们可以使用命名参数来定义用法文本，解析器实现，格式化程序实现和选项属性。

让我们看看CliBuilder的运作情况。 我们创建一个脚本来显示根据定义的参数格式化的日期。 如果我们没有定义日期，则假定当前日期。 此外，我们希望能够定义前缀文本，该文本在格式化日期之前添加（默认为空）。

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

# 正则表达式的匹配器

在之前的文章中，我们学习了如何创建`java.util.regex.Pattern`对象。 现在我们学习如何创建一个`java.util.regex.Matcher`对象，并用它来查找和匹配值。

在Groovy中，我们使用`=~`运算符（find运算符）来创建一个新的匹配器对象。 如果匹配器有任何匹配结果，我们可以通过调用匹配器对象上的方法来访问结果。 但是如果我们能够更容易地访问结果，Groovy就不会这样做。 Groovy增强了`Matcher`类，因此数据可以使用类似数组的语法。 如果我们在匹配器中使用组，则可以使用多维数组访问结果。 虽然`=~`运算符的结果是条件语句中的匹配器对象，但结果将转换为`Boolean`值。

我们可以使用第二个运算符，

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

# 使用正则表达式模式类

要在Groovy中定义正则表达式模式，我们可以对字符串使用波浪号（`~`）运算符。 结果是一个`java.util.regex.Pattern`对象。 定义模式的规则与我们在Java代码中执行的规则相同。 我们可以在`Pattern`对象上调用所有标准方法。 例如，我们可以创建一个匹配值的`Matcher`。 在下一篇博客文章中，我们看到Groovy如何有更好的快捷方式来定义`Matcher`来查找和匹配值。

```groovy
def single = ~'[ab]test\\d'
assert 'java.util.regex.Pattern' == single.class.name
 
def dubble = ~"string\$"
assert dubble instanceof java.util.regex.Pattern
 
// Groovy's String slashy syntax is very useful to
// define patterns, because we don't have to escape
// all those backslashes.
def slashy = ~/slashy \d+ value/
assert slashy instanceof java.util.regex.Pattern
 
// GString adds a negate() method which is mapped
// to the ~ operator.
def negateSlashy = /${'hello'}GString$/.negate()
assert negateSlashy instanceof java.util.regex.Pattern
def s = 'more'
def curlySlashy = ~"$s GString"
assert curlySlashy instanceof java.util.regex.Pattern
 
// Using Pattern.matcher() to create new java.util.regex.Matcher.
// In a next blog post we learn other ways to create
// Matchers in Groovy.
def testPattern = ~'t..t'
assert testPattern.matcher("test").matches()
 
// Groovy adds isCase() method to Pattern class.
// Easy for switch and grep statements.
def p = ~/\w+vy/
assert p.isCase('groovy')
 
switch ('groovy') {
case ~/java/: assert false; break;
case ~/gr\w{4}/: assert true; break;
default: assert false
}
 
// We can use flags in our expressions. In this sample
// we use the case insensitive flag (?i).
// And the grep method accepts Patterns.
def lang = ~/^(?i)gr.*/
def languages = ['java', 'Groovy', 'gRails']
assert ['Groovy', 'gRails'] == languages.grep(lang)

```

# 使用Groovy作为Git Hooks

Git支持钩子，这些脚本是在发生某些事件时触发的脚本。 这些脚本只是shell脚本，我们可以使用Groovy来运行这些脚本。 我们必须使用Git钩子脚本文件中的hash-bang(`#!`)头使Groovy成为脚本语言。 然后我们准备好使用Groovy作为Git钩子的脚本语言。

Git钩子放在我们项目的`.git/hooks`目录中。 我们创建了一个示例脚本，它将使用`growlnotify`来创建一个包含Git提交操作信息的通知消息。 `growlnotify`是Mac OSX向Growl发送消息的命令行工具。 其他操作系统还具有从命令行创建通知消息的工具。

我们必须在`.git/hooks`目录中创建`post-commit`文件。 该文件必须具有执行权限：`$ chmod +x post-commit`。 在文件的第一行，我们确保使用Groovy。 在脚本的其余部分中，我们使用Groovy来调用`git log`并获取有关提交的信息。 然后我们创建一个消息并使用正确的参数调用`growlnotify`，以便Growl可以显示消息。

```groovy
#!/usr/bin/env groovy

// Arguments for git log command.
def logArgs = ['max-count': '1', 'pretty': 'format:%an commited %s {%h}']

// Invoke git log command.
def gitLog = logArgs.inject(['git', 'log']) { cmd, k, v -> 
    cmd << "--$k=$v" 
}.execute()

// Get git log message to be used as notification message.
def message = gitLog.text

// Set icon and title for message.
def iconPath = '/Users/mrhaki/Pictures/git-icon-black.png'
def title = 'Git commit'

// Notify user of commit with growlnotify.
def notifyArgs = [message: message, title: title, image: iconPath]
notifyArgs.inject(['growlnotify']) { cmd, k, v ->
    cmd << "--$k" << v
}.execute()
```

一些脚本钩子文件采用参数。 我们可以在Groovy代码中使用该参数并编写代码来处理它。 在以下示例中，我们检查提交消息是否为空。 如果消息为空，我们返回的退出代码不是`0`。 Git将终止提交，直到脚本返回`0`。 我们编写了一个`commit-msg`钩子的简单实现：

```groovy
#!/usr/bin/env groovy

import static java.lang.System.*

// First argument is the name of the 
// temporary commit message file.
def msgFileName = args[0]

// Get the commit message file.
def msgFile = new File(msgFileName)

// Read commit message from file.
def commitMessage = msgFile.text

if (!commitMessage) {
    err.println 'Commit message is empty'
    exit 1
}

exit 0
```

# 更改XML结构

我们知道[读取XML](http://mrhaki.blogspot.com/2009/10/groovy-goodness-reading-xml.html) 和 [编写XML] (http://mrhaki.blogspot.com/2009/10/groovy-goodness-creating-xml-with.html)。 最近有人在研讨会期间询问是否可以轻松地更改XML结构。 我在[Groovy网站](http://groovy.codehaus.org/Updating+XML+with+XmlSlurper)上找到了答案。 事实证明我们可以简单地使用`XmlSlurper`，我们可以遍历结构来改变现有的节点值和属性。 我们使用`appendNode()`和`replaceNode()`来使用构建器语法处理节点。

```groovy
@Grapes(
    @Grab(group='xmlunit', module='xmlunit', version='1.3')
)
import org.custommonkey.xmlunit.*
import groovy.xml.*

def xml = '''
<todo>
    <item priority="2">
        <title>Look into GPars</title>
    </item>
    <item priority="1">
        <title>Start developing Griffon App</title>
    </item>
    <item priority="2">
        <title>Grails 1.4 M1</title>
    </item>
    <item priority="3">
        <title>GWT Sample</title>
    </item>
</todo>
'''

def todo = new XmlSlurper().parseText(xml)

// Change node values.
def items = todo.item.findAll { 
   it.@priority.toInteger() < 3 
}
items.each { item -> 
    item.title = "DO: " + item.title 
}

// Change attribute value.
def gpars= todo.item.find { 
    it.title =~ /.*GPars.*/ 
}
gpars.@priority = '1'

// Add extra item node.
todo.appendNode {
    item(priority: 2) {
        title 'Work on blog post'
    }
}

// Change node.
def grailsItem = todo.item.find { 
    it.title.toString().contains('Grails') 
}
grailsItem.replaceNode { node ->
    item(who: 'mrhaki', priority: node.@priority) {
        title 'Download Grails 1.4 M1'       
    }
}

// Remove node. Index value based on result directly after parsing.
// So here we remove the item about GWT.
todo.item[3].replaceNode {}

// Create output.
def newTodo = new StreamingMarkupBuilder().bind { 
    mkp.yield todo 
}.toString()

def expected = '''
<todo>
    <item priority="1">
        <title>DO: Look into GPars</title>
    </item>
    <item priority="1">
        <title>DO: Start developing Griffon App</title>
    </item>
    <item who="mrhaki" priority="2">
        <title>Download Grails 1.4 M1</title>
    </item>
    <item priority="2">
        <title>Work on blog post</title>
    </item>
</todo>
'''

// Check to see expected XML equals new todo XML.
XMLUnit.ignoreWhitespace = true
def difference = new Diff(newTodo, expected)
assert difference.similar()
```

# 漂亮的打印XML

简单地打印XML结构的最简单方法是使用`XmlUtil`类。 该类有一个`serialize()`方法，它为多个参数类型重载，如`String`，`GPathResult`和`Node`。 我们可以传递一个`OutputSteam`或`Writer`对象作为参数来编写相当格式化的XML。 如果我们不指定这些`serialize()`方法返回一个`String`值。

```groovy
import groovy.xml.*

def prettyXml = '''\<?xml version="1.0" encoding="UTF-8"?><languages>
  <language id="1">Groovy</language>
  <language id="2">Java</language>
  <language id="3">Scala</language>
</languages>
'''


// Pretty print a non-formatted XML String.
def xmlString = '<languages><language id="1">Groovy</language><language id="2">Java</language><language id="3">Scala</language></languages>'
assert XmlUtil.serialize(xmlString) == prettyXml

// Use Writer object as extra argument.
def xmlOutput = new StringWriter()
XmlUtil.serialize xmlString, xmlOutput
assert xmlOutput.toString() == prettyXml

// Pretty print a Node.
Node languagesNode = new XmlParser().parseText(xmlString)
assert XmlUtil.serialize(languagesNode) == prettyXml


// Pretty print a GPathResult.
def langagesResult = new XmlSlurper().parseText(xmlString)
assert XmlUtil.serialize(langagesResult) == prettyXml


// Pretty print org.w3c.dom.Element.
org.w3c.dom.Document doc = DOMBuilder.newInstance().parseText(xmlString)
org.w3c.dom.Element root = doc.documentElement
assert XmlUtil.serialize(root) == prettyXml


// Little trick to pretty format
// the result of StreamingMarkupBuilder.bind(). 
def languagesXml = {
    languages {
        language id: 1, 'Groovy'
        language id: 2, 'Java'
        language id: 3, 'Scala'
    }
}
def languagesBuilder = new StreamingMarkupBuilder()
assert XmlUtil.serialize(languagesBuilder.bind(languagesXml)) == prettyXml
```

如果我们已经有了一个`groovy.util.Node`对象，我们也可以使用`XmlNodePrinter`。 例如，如果我们使用`XmlParser`来解析XML，我们会得到一个`Node`对象。 我们创建了一个新的`XmlNodePrinter`实例，并使用`print()`方法输出带有子节点的节点。 如果我们不指定`Writer`对象，则输出将发送到`System.out`。

```groovy
import groovy.xml.*

// Get groovy.util.Node value.
def xmlString = '<languages><language id="1">Groovy</language><language id="2">Java</language><language id="3">Scala</language></languages>'
Node languages = new XmlParser().parseText(xmlString)


// Create output with all default settings.
def xmlOutput = new StringWriter()
def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput))
xmlNodePrinter.print(languages)

assert xmlOutput.toString() == '''\
<languages>
  <language id="1">
    Groovy
  </language>
  <language id="2">
    Java
  </language>
  <language id="3">
    Scala
  </language>
</languages>
'''


// Create output and set indent character
// one space.
// (can also by \t for tabs, or other characters)
xmlOutput = new StringWriter()
xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), " " /* indent */)
xmlNodePrinter.print(languages)

assert xmlOutput.toString() == '''\
<languages>
 <language id="1">
  Groovy
 </language>
 <language id="2">
  Java
 </language>
 <language id="3">
  Scala
 </language>
</languages>
'''


// Use properties preserveWhitespace,
// expandEmptyElements and quote to
// change the formatting.
xmlOutput = new StringWriter()
xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput))
xmlNodePrinter.with {
    preserveWhitespace = true
    expandEmptyElements = true
    quote = "'" // Use single quote for attributes
}
xmlNodePrinter.print(languages)

assert xmlOutput.toString() == """\
<languages>
  <language id='1'>Groovy</language>
  <language id='2'>Java</language>
  <language id='3'>Scala</language>
</languages>
"""
```

# 遍历目录

Groovy在版本1.7.2中将`traverse()`方法添加到`File`类。 我们可以使用此方法遍历目录树并调用闭包来处理文件和目录。 如果我们查看我们看到的文档，我们也可以传递一个包含许多可能选项的map来影响处理。

```groovy
import static groovy.io.FileType.*
import static groovy.io.FileVisitResult.*
 
def groovySrcDir = new File(System.env['GROOVY_HOME'], 'src/')
 
def countFilesAndDirs = 0
groovySrcDir.traverse {
  countFilesAndDirs++
}
println "Total files and directories in ${groovySrcDir.name}: $countFilesAndDirs"
 
def totalFileSize = 0
def groovyFileCount = 0
def sumFileSize = {
  totalFileSize += it.size()
  groovyFileCount++
}
def filterGroovyFiles = ~/.*\.groovy$/
groovySrcDir.traverse type: FILES, visit: sumFileSize, nameFilter: filterGroovyFiles
println "Total file size for $groovyFileCount Groovy source files is: $totalFileSize"
 
def countSmallFiles = 0
def postDirVisitor = {
  if (countSmallFiles > 0) {
    println "Found $countSmallFiles files with small filenames in ${it.name}"
  }
  countSmallFiles = 0
}

groovySrcDir.traverse(type: FILES, postDir: postDirVisitor, nameFilter: ~/.*\.groovy$/) {
  if (it.name.size() < 15) {
  countSmallFiles++
  }
}  
```

# 使用FileType处理文件或目录（或两者）

在Groovy中使用文件非常简单。 我们在`File`类中有很多有用的方法。 例如，我们可以为每个文件运行一个Closure，这个文件可以在一个带有`eachFile()`方法的目录中找到。 从Groovy 1.7.1开始，我们可以定义是否只想处理目录，文件或两者。 为此，我们必须将`FileType`常量传递给方法。 请参阅以下示例代码：

```groovy
import groovy.io.FileType
 
// First create sample dirs and files.
(1..3).each {
  new File("dir$it").mkdir()
}
(1..3).each {
  def file = new File("file$it")
  file << "Sample content for ${file.absolutePath}"
}
 
def currentDir = new File('.')
def dirs = []
currentDir.eachFile FileType.DIRECTORIES, {
  dirs << it.name
}
assert 'dir1,dir2,dir3' == dirs.join(',')
 
def files = []
currentDir.eachFile(FileType.FILES) {
  files << it.name
}
assert 'file1,file2,file3' == files.join(',')
 
def found = []
currentDir.eachFileMatch(FileType.ANY, ~/.*2/) {
  found << it.name
}
 
assert 'dir2,file2' == found.join(',')
```

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


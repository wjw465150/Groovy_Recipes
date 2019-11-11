## Groovy操纵集合秘籍

### [X] 遍历集合
遍历list对象  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
for (int i = 0; i < list.size(); i++) {
  System.out.println(list.get(i));
}
```
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
list.each{
   println it
}
```

### [X] collect方法--对集合每个元素进行运算后,得到一个新集合
例如:我们需要得到一个集合对象，它的每一个元素是list对象的每一个元素的两倍  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
def result = []
list.each{ result << it*2 }
```
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
def result = list.collect{it*2}
```
> 可以看到，只有一个语句行，就代替了上面的三个语句行。这就是我们Groovy风格的代码，同样的方法还有`collect(Collection collection, Closure closure)`，`collectAll(Closure closure)`和`collectAll(Collection collection, Closure closure)`。  

### [X] 过滤--筛选出符合条件的元素,生成一个新集合
例如:比如，我们想找出该对象中所有大于2的元素来  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
def result = []

list.each{
  if(it>2) {
    result << it
  }
}

println result
```  
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
def result = list.grep{ it > 2 }

println result
```  
> 是的，在Groovy语言中，我们对集合元素的过滤一般都使用`grep`方法，这样的代码更加具有Groovy风格一些。具有同样一些功能的方法还有`find(Closure closure)`，`findAll(Closure closure)`。  

### [X] every方法--每个集合元素是否符合某个条件
在Java语言中，对List的遍历方法是万能的，我们要做的一切功能都必须从遍历方法开始。比如，我们想知道list对象是否所有的元素都大于0  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
boolean isMoreThanZero = true;
for(int i in list) {
  if(i<=0) {
    isMoreThanZero = false;
    break;
  }
}
println isMoreThanZero
```  
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
def isMoreThanZero = list.every{ it>0 }
println isMoreThanZero
```  
> 同样类型的方法还有`any(Closure closure)`，`any()"和"every()`。

### [X] inject方法--inject()方法遍历集合，第一次将传递的值和集合元素传给闭包，将处理结果作为传递的值，和下一个集合元素传给闭包，依此类推
比如，我们想把list对象的所有元素相加起来，然后再和10求和  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
def count = 10
list.each{ count+=it }
println count
```  
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
def count = list.inject(10){result,it->
  result=result+it
};
println count
```  
> 类似这样的注入方法，Groovy语言还有集合方法`count(Object value)`。  

### [X] join方法
还有一个集合方法，对于字符串特别好用。比如，我们想把list对象的所有元素连接成一个字符串  
***java风格:***
```groovy
def list = [1, 2, 3, 4]
def result = ''
list.each{ result+=it }
println result
```  
***groovy风格:***
```groovy
def list = [1, 2, 3, 4]
def result = list.join()
println result
```  
  
都是一些集合方法配合闭包来使用而形成的Groovy语言风格的代码，写起来既快捷又简约，体现出Groovy语言作为动态语言的特点来。  

### [X] transpose方法--transpose()方法实际上就是数学中矩阵的转置，简单的来说就是行和列的交换
***groovy风格:***
```groovy
def list4 = [1, 1, 1]  
def list5 = [2, 2]  
assert [[1, 2], [1, 2]] == [list4, list5].transpose()  
```  
> 有一点需要注意的是，如果List的长度不一，则取最短的长度： 

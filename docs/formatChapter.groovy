// 先把英文章节的行格式化成" ##... chapter {#standard_chapter}"格式

def parten = ~/^#{2,}\s{1}.{1,}[^}]$/
def parten2 = ~/[a-zA-Z]{1}[a-zA-Z -\\’]{1,}/

File file = new File($/c:\WJW_E\hello_Groovy.md/$)
file.write("") 

def name=$/c:\WJW_E\白石-Markdown\Groovy\Groovy食谱1_2章节.md/$
String newLine;
new File(name).eachLine{line->
  if (parten.matcher(line.trim()).matches() ) {
    java.util.regex.Matcher matcher2 = parten2.matcher(line.trim())
    if (matcher2.find() ) {
      String newStr = line.trim().substring(matcher2.start(), matcher2.end()).trim().replaceAll("( )|(\\?)|(\')|(-)|(/)|(\\()|(\\))|(\\.)|(’)|(,)|(!)","_")
    
      println "${line} {#${newStr}}"
      file.append("${line} {#${newStr}}","UTF-8")
    } else {
      file.append("${line}\r\n","UTF-8")
    }
  } else {
    file.append("${line}\r\n","UTF-8")
  }
}
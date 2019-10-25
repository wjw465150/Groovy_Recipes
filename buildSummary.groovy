def parten = ~/^#{2,3}\s{1}.{1,}/
def parten2 = ~/[a-zA-Z]{1}[a-zA-Z -\\’]{1,}/


def name=$/c:\WJW_E\白石-Markdown\Groovy\Groovy食谱9_10章节.md/$
String newLine;
String[] twoHead
new File(name).eachLine{line->
  if (parten.matcher(line.trim()).matches() ) {
    twoHead = line.split(" \\{\\#")
    if(twoHead.length==2) {
      println "".padLeft((twoHead[0].count("#")-1)*2)+"* ["+twoHead[0].replaceAll("#","")+"](Groovy食谱9_10章节.md#"+twoHead[1].replaceAll("\\{#","").replaceAll("}",")")
    }
  }
}

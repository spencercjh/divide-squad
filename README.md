# 分队小程序

## 使用方法

**请准备好 JRE Java8+ 环境**

1. 准备数据，参考过往 Excel 文件；
2. 下载release中的jar包（或者从源码build jar包），执行1命令，注意观察控制台。

```shell
# 1
java -jar divide-squad-<version>-all.jar [-hV] -f=<ExcelFilePath> [-s=<SheetIndex>]
# 2 UNIX系统
chmod +x ./gradlew
gradlew build
# 3 Windows系统
./gradlew.bat build
```

## 已知且很难解决的问题

1. 没有类似于游戏匹配机制那样的随机性：同样的一拨人，分出来的队伍永远是一样的，除非人员、能力值上有变动；
2. 对每一位球员只能在一个位置（前锋、中场、边路、中位、门将）上进行分配；
3. 没有更多的数据维度去评价一个球员，仅仅只有一个数字；
4. 分配上只讲平均策略，没有任何权重的概念；
5. 封装到一个微信小程序中去供更多的人快捷使用。

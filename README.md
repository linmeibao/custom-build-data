
## 自定义构建测试数据工具

## TODO

- DML和DDL的序列化功能
- 项目打包，还有项目的-help和-config功能

### 主要功能

对mysql数据库批量构建模拟业务系统的测试数据，可用于系统压力测试、POC演示、BI报表展示、数据分析指标加工逻辑测试。
可自定义库表结构，自定义关联关系，支持复杂的数据结构生成。通过符合特定规则的JSON配置文件，转换成对数据库表的DDL操作和DML操作。

###  快速开始

### 配置文件示例

```
# 多线程配置
threadConfig:
  # 构建数据的线程数
  threadBuilderNumber: 5
  # 每次批量大小
  batchSize: 50
  # 是否开启单线程调试
  singleThreadDebug: false

# 数据库配置
dataSourceConfig:
  #数据源列表
  dataSourceInfos:
    # 数据源A
    - dbKey: ops
      driverClassName: com.mysql.jdbc.Driver
      jdbcUrl: jdbc:mysql://127.0.0.1:3306/xxops?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true
      username: root
      password: root
    # 数据源B
    - dbKey: txn
      driverClassName: com.mysql.jdbc.Driver
      jdbcUrl: jdbc:mysql://127.0.0.1:3306/xxtxn?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true
      username: root
      password: root
  # 是否开启自动删表建表DDL，开启此配置之后，每次构建数据之前都会把“dataDDLJsonFilePath”中的JSON配置解析成DDL语句进行删表之后再重建表
  autoDDl: false
  
  serializeDDL:
       generateDDL:false
      generateDML:false
       driverClassName: com.mysql.jdbc.Driver
       jdbcUrl: jdbc:mysql://127.0.0.1:3306/xxops?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true
       username: root
       password: root

# 设置构建多少组数据
buildNumber: 200

# DML配置文件路径
dataJsonFilePath: /Users/hsy/jrx/anyest3/model-auto-test/target/classes/dataconf/realtime_test_data_definition_test.json

# DDL配置文件路径(先删表,再建表)
dataDDLJsonFilePath: /Users/hsy/jrx/anyest3/model-auto-test/target/classes/test_ddl.json

# sql文件输出路径
sqlFileOutputPath: /Users/hsy/jrx/anyest3/model-auto-test/target/classes/

# 全局自增Id保存文件地址
globalAutoIncrementFilePath: /Users/hsy/jrx/anyest3/model-auto-test/target/classes/dataconf/increment.id
```

- 修改mysql数据库为自己的测试库
- 修改DML配置文件路径为/example的xxx.json（路径为绝对路径）
- 修改DDL配置文件路径为/example的xxx_ddl.json（路径为绝对路径）
- 修改sql文件输出路径
- 修改全局自增Id保存文件地址
- 执行 buildData -config=配置文件路径(绝对路径)
- 查看mysql数据库中，测试数据已经生成
- 查看sql文件输出路径，里面有生成的sql语句，可单独在其他mysql数据库直接执行同样也可生成测试数据

### 配置文件配置说明
TODO

- generateDDL设置为true，会读取serializeDDL中的所有表，将其序列化成DDL配置的json文件。
- generateDML设置为true，会读取serializeDDL中的所有表，将其序列化为DML配置的json文件

#### DDL配置说明
TODO

- 如果想要对已存在的数据库表进行测试数据生成，可设置dataSourceConfig.autoDDl为false。将表中NOT NULL的字段补充到DML配置中，可直接生成数据。


#### DML配置说明
TODO



##### 基本配置
基于tableInfo表编号“no”进行顺序生成数据

- `scenarios`场景集合。可配置多个场景，数据生成时随机选择一个场景执行。
- `scenarios`.`desc`场景描述。默认为空，选填
- `scenarios`.`tableInfos`表记录集合。一个表记录就是一条ISNERT语句或者UPDATE语句，默认为空，选填
- `tableInfos`.`desc`表记录描述。默认为空，选填
- `tableInfos`.`no`表编号。默认为空，必填
- `tableInfos`.`tableName`表名称。默认为空，必填
- `tableInfos`.`dbKey`数据源标识。默认为空，必填
- `tableInfos`.`conditionExpression`所有表记录对象可用，表数据是否进行生成的条件判断表达式，true则执行columns的生成逻辑，否则跳过当前表的数据生成。默认为空，选填，示例：
    ```
    ${1.cm_app_case.approve_rst} == "AR" 
    ${表编号.表名称.列名称} == "AR" 
    ```
- `tableInfos`.`operateMode`操作类型，INSERT或者UPDATE，决定最后生成的SQL语句。默认为空，必填
- `tableInfos`.`tableInStateDefinition`表数据生成时，中间状态定义，默认为空，选填，
- `tableInfos`.`tableInStateDefinition`.`tableFixedValues`表列名的固定值定义（在一对多数据时使用）示例：{"列名1": [ "值1", "值2"], "列名2": [ "值1", "值2"]}。根据当前1对多数据的行号取数组的下标值。默认为空，选填，
- `tableInfos`.`tableInStateDefinition`.`fixedPlaceholder`表列名固定值的替代值（在一对多数据时使用），当前1对多数据的行号超过tableFixedValues的最大下标值时使用该属性，示例：{"列名1": "替代值","列名2": "替代值"}。默认为空，选填。
- `tableInfos`.`tableInStateDefinition`.`tableDynamicValues`表列名的动态值定义（在一对多数据时使用）示例："列名1": ["valueRule": {"type": null}},{"valueRule": {"type": null}}]。根据当前1对多数据的行号取数组的下标值。默认为空，选填。
- `tableInfos`.`tableInStateDefinition`.`dynamicPlaceholder`表列名动态值的替代值（在一对多数据时使用），当前1对多数据的行号超过tableDynamicValues的最大下标值时使用该属性，示例：{"列名1": {"valueRule": {"type": null}}}。默认为空，选填。
- `tableInfos`.`columns`字段数据集合。默认为空，必填
- `tableInfos`.`columns`.`columnName`字段名称。默认为空，必填
- `tableInfos`.`columns`.`comment`字段说明。默认为空，必填
- `tableInfos`.`columns`.`dataType`字段数据类型。可选值：STRING、INTEGER、Boolean、LONG、DATE、DOUBLE、TIMESTAMP。默认为空，必填
- `tableInfos`.`columns`.`valueRule`字段数据的生成规则定义（下面专门做详细介绍）。默认为空，必填
- `tableInfos`.`wheres` where条件数据集合，（在UPDATE时使用）结构和columns一致，区别在于，columns作为字段生成，wheres作为where条件。默认为空，选填
- `tableInfos`.`relations`关联表记录对象集合。默认为空，必填
- `tableInfos`.`relations`.`desc`关联表记录对象描述。默认为空，选填
- `tableInfos`.`relations`.`relationType`关联表记录关联类型，可选值：ONE_TO_NNE、ONE_TO_MANY。默认为空，必填
- `tableInfos`.`relations`.`relationTableRowNum` 关联表数据生成数量，默认为空，选填，relationTableRowNum或者relationRowNumExpression二选一
- `tableInfos`.`relations`.`relationRowNumExpression` 关联表数据生成表达式，默认为空，必填默认为空，选填，relationTableRowNum或者relationRowNumExpression二选一
- `tableInfos`.`relations`.`relationTable`关联表记录对象，默认为空，必填

##### COLUMN配置说明
COLUMN生成规则说明，每个column都必须有一个valueRule的属性，valueRule根据type的不同，执行不同的字段生成逻辑：
    {
      "columnName": test_1,
      "comment": "测试字段",
      "dataType": "STRING",
      "valueRule": {
        "type": "NORMAL"
      }
    }
所有的valueRule都会有3个公共属性
defaultValue:默认值，不配置默认为null
type:规则类型，标识不同的规则计算
boolExpression:布尔表达式，为true使用计算逻辑，否则使用defaultValue的值,不配置时默认为true
```
默认规则:NORMAL,直接使用默认值,不会经过boolExpression判断
"valueRule": {
    "type": "NORMAL"
    "defaultValue":1
}

随机选择器:SELECTOR,如果boolExpression成立,则会随机在valueArray中选择一个值返回作为字段值,不成立直接返回defaultValue的值,也可以不使用boolExpression，直接随机返回一个valueArray的值
"valueRule": {
    "type": "SELECTOR"
    "defaultValue":1,
    "valueArray":[2,3,4],
    "boolExpression":"${表编号.表名称.列名称} > 10"
}

随机字符串:RANDOM_STRING,boolExpression同上,创建一个前缀为B字符串的19位随机字符串
"valueRule": {
    "type": "RANDOM_STRING"
    "prefix":"B"
}

随机金额:RANDOM_MONEY,boolExpression同上,compareExpression比较表达式,（有一个参数必须是当前计算字段，被比较的字段必须已经计算完成）两个字段进行比较计算,把当前生成的随机金额值不断的放入表达式中进行计算,当表达式成立时返回生成结果,不成立则不断循环生成结果,直到表达式成立,${2.表2.金额2}为当前表当前字段。不使用compareExpression时，随机生成一个金额
"valueRule": {
    "type": "RANDOM_MONEY"
    "compareExpression":"${2.表2.金额2} < ${1.表1.金额1}"
}

随机数字:RANDOM_NUMBER,boolExpression同上,随机生成一个32位的数字字符串
"valueRule": {
    "type": "RANDOM_NUMBER"
}

随机日期:RANDOM_DATE,boolExpression同上,compareExpression比较表达式,（有一个参数必须是当前计算字段，被比较的字段必须已经计算完成）两个字段进行比较计算,把当前生成的随机日期值不断的放入表达式中进行计算,当表达式成立时返回生成结果,不成立则不断循环生成结果,直到表达式成立,${2.表2.柔情哦2}为当前表当前字段。不使用compareExpression时，随机生成一个日期
"valueRule": {
    "type": "RANDOM_MONEY"
    "compareExpression":"${2.表2.日期2} < ${1.表1.日期1}"
}

随机手机号:RANDOM_MOBILE,boolExpression同上，随机生成一个电话号码
"valueRule": {
    "type": "RANDOM_MOBILE"
}

表内自增值:CONTINUOUS_VALUE,boolExpression同上,生成从1开始的表内自增值，表数据生成完成之后，自增值又从1开始（一般用于1对多数据，第一条数据字段值为1，第二条数据字段值为2）
"valueRule": {
    "type": "CONTINUOUS_VALUE"
}

身份证:ID_CARD",boolExpression同上，随机生成一个身份证号
"valueRule": {
    "type": "ID_CARD"
}

引用字段:REFERENCE,boolExpression同上，使用引用表达式refColumnExpression，直接引用一个当前表或者之前表已经计算好的字段值。format表示是否进行格式转换，默认为false,如果你的columnType为DOUBLE将会返回一个Double的值，fewDecimalPlaces保留几位小数，默认0。
"valueRule": {
    "type": "REFERENCE"
    "refColumnExpression":"${表编号.表名称.表字段}",
    "format":true,
    "fewDecimalPlaces":2
}

随机名字:RANDOM_NAME,boolExpression同上，随机生成一个名字
"valueRule": {
    "type": "RANDOM_NAME"
    "refColumnExpression":"${表编号.表名称.表字段}"
}

省份:PROVINCE,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成省份编号字符串
"valueRule": {
    "type": "PROVINCE"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

城市:CITY,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成城市编号字符串
"valueRule": {
    "type": "CITY"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

区县:DISTRICT,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成区县编号字符串
"valueRule": {
    "type": "DISTRICT"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

生日:BIRTH,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成生日字符串
"valueRule": {
    "type": "BIRTH"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

固定值规则:FIXED_VALUE,boolExpression同上，一般用于1对多数据，根据表数据当前对应的行数作为下标到取值，当前1对多数据的行号超过tableFixedValues的最大下标值时全部使用fixedPlaceholder属性作为替代值
"valueRule": {
    "type": "FIXED_VALUE"
}

动态值规则:DYNAMIC_VALUE,boolExpression同上，一般用于1对多数据，根据表数据当前对应的行数作为下标到取值，当前1对多数据的行号超过tableDynamicValues的最大下标值时全部使用dynamicPlaceholder属性作为替代值
"valueRule": {
    "type": "DYNAMIC_VALUE"
}

表达式计算:EXPRESSION,boolExpression同上，支持复杂的表达式计算，支持自定义函数调用，使用Aviator进行表达式计算
"valueRule": {
    "type": "EXPRESSION"
    "expression":"(${表编号.表名称.表字段} + ${表编号.表名称.表字段}) * 30"
}

全局自增值:GLOBAL_AUTO_INCREMENT，boolExpression同上，全局的自增值，每次数据生成完成之后，会把值写入到文件中进行保存，下次生成数据时再次进行读取。
"valueRule": {
    "type": "GLOBAL_AUTO_INCREMENT"
}
```
JSON结构展示
```
{
  "scenarios": [
    {
      "desc": null,
      "tableInfos": [
        {
          "desc": null,
          "no": null,
          "tableName": null,
          "dbKey": null,
          "conditionExpression": null,
          "operateMode": null,
          "tableInStateDefinition": {
            "tableFixedValues": {
              "列名1": [ "值1", "值2"],
              "列名2": [ "值1", "值2"]
            },
            "fixedPlaceholder": {
              "列名1": "替代值",
              "列名2": "替代值"
            },
            "tableDynamicValues": {
              "列名1": [
                {"valueRule": {"type": null}},
                {"valueRule": {"type": null}}
              ]
            },
            "dynamicPlaceholder": {
              "列名1": {"valueRule": {"type": null}}
            }
          },
          "columns": [
            {
              "columnName": null,
              "comment": null,
              "dataType": null,
              "valueRule": {
                "type": null
              }
            }
          ],
          "relations": [
            {
              "desc": null,
              "relationType": null,
              "relationTableRowNum": null,
              "relationTable": {
                "no": null,
                "tableName": null,
                "dbKey": null,
                "operateMode": null,
                "tableInStateDefinition": {
                  "tableFixedValues": {
                    "列名1": [ "值1", "值2"],
                    "列名2": [ "值1", "值2"]
                  },
                  "fixedPlaceholder": {
                    "列名1": "替代值",
                    "列名2": "替代值"
                  },
                  "tableDynamicValues": {
                    "列名1": [
                      {"valueRule": {"type": null}},
                      {"valueRule": {"type": null}}
                    ]
                  },
                  "dynamicPlaceholder": {
                    "列名1": {"valueRule": {"type": null}}
                  }
                },
                "columns": [
                  {
                    "columnName": null,
                    "comment": null,
                    "dataType": null,
                    "valueRule": {
                      "type": null
                    }
                  }
                ],
                "wheres": [
                  {
                    "columnName": null,
                    "comment": null,
                    "dataType": null,
                    "valueRule": {
                      "type": null
                    }
                  }
                ]
              }
            }
          ]
        }
      ]
    }
  ]
}
```

### 目录结构

```
.
├── README.md                       说明文档
├── logs                            日志目录
├── pom.xml                 
└── src                     
    ├── main
    │   ├── java
    │   │   └── jrx
    │   │       └── data
    │   └── resources               资源目录
    │       ├── dataconf            数据配置目录
    │       │   ├── data_readme.md
    │       │   ├── ddl.sql
    │       │   ├── increment.id
    │       │   └── realtime_test_data_definition.json  数据配置JSON文件
    │       ├── logback.xml         日志配置文件
    │       └── model-auto-test.yml 工程配置文件
    └── test                        测试目录
```


### 工程打包

TODO

### 使用方式

TODO


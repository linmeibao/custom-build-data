# 自定义构建测试数据工具

### 主要功能

对mysql数据库批量构建模拟业务流程的测试数据，可用于系统压力测试、POC演示、BI报表展示、数据分析指标加工逻辑测试。
可自定义库表结构，自定义关联关系，支持复杂的数据结构生成。通过符合特定规则的JSON配置文件，转换成对数据库表的DDL操作和DML操作。

### 目录结构

```
.
├── README.md
├── config
│   ├── custom-build-data.yml
│   ├── dataconf
│   │   ├── example
│   │   │   ├── insurance_business.json
│   │   │   └── insurance_business_ddl.json
│   │   └── increment.id
│   └── logback.xml
├── lib
├── logs
│   └── backup
└── sbin
    └── start.sh
```
- README.md 说明文档
- config 配置文件目录
- custom-build-data.yml 工程配置文件
- dataconf 数据配置目录
- example 数据配置示例目录（包含一个保险业务示例）
- increment.id 全局自增id文件存储
- logback.xml 日志配置文件
- lib 依赖包
- logs 日志目录
- sbin 执行脚本


### 快速开始

#### 配置文件示例
```
# 多线程配置
threadConfig:
  # 线程数
  threadBuilderNumber: 5
  # 每次批量大小
  batchSize: 10
  # 单线程调试
  singleThreadDebug: false

# 数据库配置
dataSourceConfig:
  dataSourceInfos:
    # 数据源A
    - dbKey: test
      driverClassName: com.mysql.jdbc.Driver
      jdbcUrl: jdbc:mysql://127.0.0.1:3308/ops?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true
      username: root
      password: root
  # 是否开启自动删表建表DDL
  autoDDl: true

# 序列化生成示例文件配置,从mysql数据库读取库表结构和数据序列化生成示例JSON
exampleConfig:
  # 示例文件输出文件路径
  generateExampleFilePath: /Users/hsy/nny/custom-build-data/target/custom-build-data-1.0.0/config/dataconf/example/
  # 是否生成DDl示例文件
  generateDDl: false
  # 是否生成DMl示例文件
  generateDMl: false
  # 数据源列表
  dataSourceInfos:
    - dbKey: test
      driverClassName: com.mysql.jdbc.Driver
      jdbcUrl: jdbc:mysql://127.0.0.1:3308/ops?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true
      username: root
      password: root

# 设置构建多少组数据
buildNumber: 1

# DML配置文件路径
dataJsonFilePath: /Users/hsy/nny/custom-build-data/target/custom-build-data-1.0.0/config/dataconf/example/insurance_business.json

# DDL配置文件路径
dataDDLJsonFilePath: /Users/hsy/nny/custom-build-data/target/custom-build-data-1.0.0/config/dataconf/example/insurance_business_ddl.json

# 自动生成的sql语句输出路径
sqlFileOutputFilePath: /Users/hsy/nny/custom-build-data/target/custom-build-data-1.0.0/

# 全局自增Id保存文件地址
globalAutoIncrementFilePath: /Users/hsy/nny/custom-build-data/target/custom-build-data-1.0.0/config/dataconf/increment.id
```

- 修改数据源配置中的mysql数据库为自己的测试库
- 修改DML配置文件路径为:部署目录/custom-build-data-1.0.0/config/dataconf/insurance_business.json（路径为绝对路径）
- 修改DDL配置文件路径为:部署目录/custom-build-data-1.0.0/config/dataconf/insurance_business.json(路径为绝对路径）
- 修改sql文件输出路径(路径为绝对路径）
- 修改全局自增Id保存文件地址(路径为绝对路径）
- 进入部署目录/custom-build-data-1.0.0目录，执行: sh sbin/start.sh config/custom-build-data.yml
- 查看mysql数据库中，测试数据已经生成
- 查看sql文件输出路径，里面有生成的sql语句，可单独在其他mysql数据库直接执行同样也可生成测试数据

### 配置文件详细说明

多线程配置 threadConfig
```
threadBuilderNumber: 构建数据线程数配置
batchSize: 每次批量执行的组数
singleThreadDebug: 是否开启单线程调试
```

数据库配置 dataSourceConfig
```
dataSourceInfos: 数据源列表（支持配置多个数据源）
  dbKey: 数据源标识，DML和DDL配置文件中会使用
  driverClassName: 驱动
  jdbcUrl: 数据库连接地址（加上rewriteBatchedStatements=true参数开启批量写，默认是关闭的）
  username: root
  password: root
autoDDl: 是否开启自动删表建表DDL
```

序列化生成示例文件配置 exampleConfig
```
从mysql数据库读取所有库表结构序列化生成示例JSON
generateExampleFilePath: 示例文件输出文件路径（绝对路径）
generateDDl: 是否生成DDl示例文件
generateDMl: 是否生成DMl示例文件
dataSourceInfos: 数据源列表（支持配置多个数据源，配置与dataSourceConfig中的dataSourceInfos一致）
```

构建多少组数据 buildNumber
```
buildNumber: 构建多少组数据，一个完整的业务流程为一组数据
```

DML配置文件路径 dataJsonFilePath
```
dataJsonFilePath: 路径为绝对路径，DML配置，可配置INSERT、UPDATE语句，多条SQL语句为一组数据
```

DML配置文件路径 dataDDLJsonFilePath
```
dataDDLJsonFilePath: 路径为绝对路径，DDL配置，自定义配置表结构，增加、删减字段便捷，只需要修改JSON文件即可
```

自动生成的sql语句输出路径 sqlFileOutputFilePath
```
sqlFileOutputFilePath: 路径为绝对路径，将DML配置文件中的INSERT、UPDATE业务逻辑生成SQL语句输出到文件中，可单独在其他mysql数据库直接执行同样也可生成测试数据，测试数据迁移便捷
```

全局自增Id保存文件地址 globalAutoIncrementFilePath
```
globalAutoIncrementFilePath:路径为绝对路径，保存全局的自增Id，每次生成数据时会加载文件中的Id值，数据生成完成之后，会将当前的Id值回写到文件中。
```


#### DDL配置说明
- 配置表名称、表字段、表索引、表主键，进行DDL的生成。
- 如果想要对已存在的数据库表进行测试数据生成，可设置dataSourceConfig.autoDDl为false。将表中NOT NULL的字段补充到DML配置中，可直接生成数据。

基础结构
```
columns：字段定义列表
dbKey：数据源Key，对应配置文件中的dbKey
indexInfos：索引定义
primary：主键定义
tabelName：表名称
no：表编号

  {
    "columns": [],
    "dbKey": "test",
    "indexInfos": [],
    "no": 1,
    "primary": [],
    "tableName": "insurance_customer_info"
  },
```

columns 字段定义列表
```
columnName：字段名称
columnType：字段类型
comment：字段注释
dataType：字段值类型（枚举值）
isNullStr：字段是否允许为空（NOT NULL | NULL）

  {
    "columnName": "cust_id",
    "columnType": "int(11)",
    "comment": "客户id",
    "dataType": "INTEGER",
    "isNullStr": "NOT NULL"
  },

dataType可选枚举值：
    STRING("STRING", "VARCHAR"),
    INTEGER("INTEGER", "INT"),
    Boolean("Boolean", "BOOLEAN"),
    LONG("LONG", "INTEGER"),
    DATE("DATE", "DATETIME"),
    DOUBLE("DOUBLE", "DECIMAL"),
    Currency("Currency", "VARCHAR"),
    DICTIONARY("DICTIONARY", "VARCHAR"),
    TIMESTAMP("TIMESTAMP", "TIMESTAMP"),
    Maps("Maps", "VARCHAR"),
    Lists("Lists", "VARCHAR"),
    ListItem("ListItem", "VARCHAR");
```

indexInfos 索引定义
```
columnName：列名称
indexName：索引名称

[
  {
    "columnName": "staff_id",
    "indexName": "staff_id"
  }
]
```

primary 主键定义
```
数组，支持联合主键
[
  "cust_id"
]
```

#### DML配置说明
- 基于现有表配置INSERT、UPDATE语句，支持生成1对多数据。

基础结构
```
scenarios：场景集合（数组，可配置多个场景，生成数据时会随机选择一个场景）
desc：场景描述
tableInfos：表数据生成定义

{
  "scenarios": [
    {
      "desc": "场景描述",
      "tableInfos": [
      ]
    }
  ]
}
```

tableInfos 表数据生成定义
```
desc：对表执行的INSERT或UPDATE操作描述
columns：SQL语句字段定义
wheres：SQL语句where条件的字段定义（UPDATE时使用）
relations：关联表数据生成定义
dbKey：数据源Key，对应配置文件中
conditionExpression：表数据是否进行生成的条件判断表达式，true则执行生成逻辑，否则跳过当前表操作的数据生成
no：表编号
operateMode：操作类型（INSERT|UPDATE）
tableName：执行SQL语句的表名称

no+tabelName 表编号+表名称为DML数据中的唯一标识，因为某个表在一个业务流程中，可能会有INSERT多次或者是UPDATE多次操作。

{
  "desc": "INSERTxxx记录",
  "columns": [],
  "wheres": [],
  "relations": [],
  "dbKey": "test",
  "no": 2,
  "conditionExpression":"$1.insurance_claims.claims_approve_result} == "approve""
  "operateMode": "INSERT",
  "tableName": "insurance_xxx_info"
}
```
relations 关联表数据生成定义
```
desc：关联表数据操作描述
relationType：关联类型（ONE_TO_MANY|ONE_TO_NNE）
relationRowNumExpression：关联表达式，表达式的结果作为关联数据生成的条数（用于1对N关联，需要生成多条数据时使用）。
relationTableRowNum：指定关联表数据生成条数。
relationTable：关联表数据生成详细定义

`注意`：relationRowNumExpression和relationTableRowNum二选一，不能同时使用

{
  "desc": "INSERT保险单记录",
  "relationType": "ONE_TO_MANY",
  "relationRowNumExpression": "${1.insurance_customer_info.policy_number}",
  "relationTableRowNum":1,
  "relationTable": {}
}
```

relationTable 关联表数据生成详细定义
```
结构与tableInfos的表数据生成定义基本一致。tableInStateDefinition特殊介绍。

tableInStateDefinition：用与1对N数据生成时中间状态数据使用。
tableFixedValues：已知当前表必须生成3行数据，p_type为列名称，每一行p_type的值都不一致，需要提前定义好。可使用该属性进行固定值的定义。支持多个数据列。

{
  "tableInStateDefinition": {
    "tableFixedValues": {
        "p_type": [
          "P001",
          "P002",
          "P003"
        ]
    }
  },
  "columns": [],
  "wheres": [],
  "dbKey": "test",
  "no": 2,
  "operateMode": "INSERT",
  "tableName": "insurance_policy"
}
```

columns SQL语句字段定义
```
columnName：字段名称
comment：字段注释
dataType：字段值数据类型
valueRule：字段值计算规则

{
  "columnName": "project_name",
  "comment": "保单项目名称",
  "dataType": "STRING",
  "valueRule": {}
}
```

valueRule 字段值计算规则
```
自定义表达式介绍：
${3.insurance_claims.claims_approve_result}
${表编号.表名称.表字段}

1. 表达式中使用的“表编号.表名称.表字段”都必须是已经计算完成的。
2. 计算是按照表编号顺序计算，一般配置时是从上至下，只能引用之前的INSERT或UPDATE中的字段，不能引用之后的。可以引用当前table中的字段，但必须也是计算完成的字段。
3. 比如示例中：${2.insurance_policy}表操作在配置中，available_amount字段的表达式中可以引用insured_amount，因为计算available_amount时，insured_amount字段已经计算完成了，值已经有了。
4. 比如示例中：${2.insurance_policy}表操作在配置中，cust_id字段的表达式中可以引用${1.insurance_customer_info.cust_id}，但不能引用${3.insurance_claims}中的任何一个字段。


-------

type：规则类型，类型不同的会执行不同的规则计算
boolExpression：布尔表达式，为true使用计算逻辑，否则使用defaultValue的值,不配置时默认为true
defaultValue：默认值，不配置默认为null

{
  "type":"RANDOM_MONEY",
  "boolExpression":"${3.insurance_claims.claims_approve_result} == "approve"",
  "defaultValue":0,
}

规则类型说明

默认规则:NORMAL
随机选择器:SELECTOR
随机字符串:RANDOM_STRING
随机金额:RANDOM_MONEY
随机数字:RANDOM_NUMBER
随机日期:RANDOM_DATE
随机手机号:RANDOM_MOBILE
表内自增值:CONTINUOUS_VALUE
身份证:ID_CARD
引用字段:REFERENCE
随机名字:RANDOM_NAME
省份:PROVINCE
城市:CITY
区县:DISTRICT
生日:BIRTH
固定值规则:FIXED_VALUE
动态值规则:DYNAMIC_VALUE
表达式计算:EXPRESSION
全局自增值:GLOBAL_AUTO_INCREMENT

> 默认规则:NORMAL,直接使用默认值,不会经过boolExpression判断
"valueRule": {
    "type": "NORMAL"
    "defaultValue":1
}

> 随机选择器:SELECTOR,如果boolExpression成立,则会随机在valueArray中选择一个值返回作为字段值,不成立直接返回defaultValue的值,也可以不使用boolExpression，直接随机返回一个valueArray的值
"valueRule": {
    "type": "SELECTOR"
    "valueArray":[2,3,4]
}

> 随机字符串:RANDOM_STRING,boolExpression同上,创建一个前缀为B字符串的19位随机字符串
"valueRule": {
    "type": "RANDOM_STRING"
    "prefix":"B"
}

> 随机金额:RANDOM_MONEY,boolExpression同上,compareExpression比较表达式,（有一个参数必须是当前计算字段，被比较的字段必须已经计算完成）两个字段进行比较计算,把当前生成的随机金额值不断的放入表达式中进行计算,当表达式成立时返回生成结果,不成立则不断循环生成结果,直到表达式成立,${2.表2.金额2}为当前表当前字段。不使用compareExpression时，随机生成一个金额
"valueRule": {
    "type": "RANDOM_MONEY"
    "compareExpression":"${2.表2.金额2} < ${1.表1.金额1}"
}

> 随机数字:RANDOM_NUMBER,boolExpression同上,随机生成一个32位的数字字符串
"valueRule": {
    "type": "RANDOM_NUMBER"
}

> 随机日期:RANDOM_DATE,boolExpression同上,compareExpression比较表达式,（有一个参数必须是当前计算字段，被比较的字段必须已经计算完成）两个字段进行比较计算,把当前生成的随机日期值不断的放入表达式中进行计算,当表达式成立时返回生成结果,不成立则不断循环生成结果,直到表达式成立,${2.表2.柔情哦2}为当前表当前字段。不使用compareExpression时，随机生成一个日期
"valueRule": {
    "type": "RANDOM_MONEY"
    "compareExpression":"${2.表2.日期2} < ${1.表1.日期1}"
}

> 随机手机号:RANDOM_MOBILE,boolExpression同上，随机生成一个电话号码
"valueRule": {
    "type": "RANDOM_MOBILE"
}

> 表内自增值:CONTINUOUS_VALUE,boolExpression同上,生成从1开始的表内自增值，表数据生成完成之后，自增值又从1开始（一般用于1对多数据，第一条数据字段值为1，第二条数据字段值为2）
"valueRule": {
    "type": "CONTINUOUS_VALUE"
}

> 身份证:ID_CARD",boolExpression同上，随机生成一个身份证号
"valueRule": {
    "type": "ID_CARD"
}

> 引用字段:REFERENCE,boolExpression同上，使用引用表达式refColumnExpression，直接引用一个当前表或者之前表已经计算好的字段值。format表示是否进行格式转换，默认为false,如果你的columnType为DOUBLE将会返回一个Double的值，fewDecimalPlaces保留几位小数，默认0。
"valueRule": {
    "type": "REFERENCE"
    "refColumnExpression":"${表编号.表名称.表字段}",
    "format":true,
    "fewDecimalPlaces":2
}

> 随机名字:RANDOM_NAME,boolExpression同上，随机生成一个名字
"valueRule": {
    "type": "RANDOM_NAME"
    "refColumnExpression":"${表编号.表名称.表字段}"
}

> 省份:PROVINCE,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成省份编号字符串
"valueRule": {
    "type": "PROVINCE"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

> 城市:CITY,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成城市编号字符串
"valueRule": {
    "type": "CITY"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

> 区县:DISTRICT,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成区县编号字符串
"valueRule": {
    "type": "DISTRICT"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

> 生日:BIRTH,boolExpression同上，使用引用表达式refColumnExpression传入身份证字段，生成生日字符串
"valueRule": {
    "type": "BIRTH"
    "refColumnExpression":"${表编号.表名称.身份证字段}"
}

> 固定值规则:FIXED_VALUE,boolExpression同上，用于1对多数据，根据表数据当前对应的行数作为下标到取值，当前1对多数据的行号超过tableFixedValues的最大下标值时全部使用fixedPlaceholder属性作为替代值
"valueRule": {
    "type": "FIXED_VALUE"
}

> 动态值规则:DYNAMIC_VALUE,boolExpression同上，用于1对多数据，根据表数据当前对应的行数作为下标到取值，当前1对多数据的行号超过tableDynamicValues的最大下标值时全部使用dynamicPlaceholder属性作为替代值
"valueRule": {
    "type": "DYNAMIC_VALUE"
}

> 表达式计算:EXPRESSION,boolExpression同上，支持复杂的表达式计算，支持自定义函数调用，使用Aviator进行表达式计算
"valueRule": {
    "type": "EXPRESSION"
    "expression":"(${表编号.表名称.表字段} + ${表编号.表名称.表字段}) * 30"
}

> 全局自增值:GLOBAL_AUTO_INCREMENT，boolExpression同上，全局的自增值，每次数据生成完成之后，会把值写入到文件中进行保存，下次生成数据时再次进行读取。
"valueRule": {
    "type": "GLOBAL_AUTO_INCREMENT"
}

```

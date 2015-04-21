# FAQ : よくある質問 #

### #{...} と ${...} の違いは？ ###
#{...} は java.sql.PreparedStatement のプレースホルダーとして扱われます。一方、${...} はステートメント生成前に単なる文字列として置換されます。SQL ステートメント中でプレースホルダーが使える場所は限られているので、この違いを理解しておくことは重要です。
例えば、テーブル名を指定するためにプレースホルダーを使用することはできません。
```
Map<String, Object> parms = new HashMap<String, Object>();
parms.put("table", "foo");
parms.put("criteria", 37);
List<Object> rows = mapper.generalSelect(parms);
```
```
<select id="generalSelect" parameterType="map">
  select * from ${table} where col1 = #{criteria}
</select>
```
実行時に MyBatis が上記のコードから生成するのは、次のような PreparedStatement になります。
```
select * from foo where col1 = ?
```
**重要**: ${...} （文字列置換）を使う場合は、SQLインジェクション攻撃への配慮を怠らないようにしてください。また、日付などの複雑な型は文字列置換では期待通りの動作にならない可能性があります。これらの理由から、可能な限り #{...} を使うようにしてください。

### LIKE 演算子を使ったクエリはどう書く？ ###
二つの方法があります。最初の（そして推奨される）方法は、Java のコード中で SQL のワイルドカードを付加するというものです。例えば次のように書くことができます。
```
String wildcardName = "%Smi%";
List<Name> names = mapper.selectLike(wildcardName);
```
```
<select id="selectLike">
  select * from foo where bar like #{value}
</select>
```
もう一つは、SQL の中でワイルドカードを連結する方法です。この方法は、適切にコーディングしないと SQL インジェクションが可能となるため、上記の方法に比べると安全性の面で劣ります。こちらも例を挙げておきます。
```
String wildcardName = "Smi";
List<Name> names = mapper.selectLike(wildcardName);
```
```
<select id="selectLike">
  select * from foo where bar like '%' || '${value}' || '%'
</select>
```
**重要**: 二番目の例では # ではなく $ を使っています。

### バッチ更新はどう書く？ ###
まず、単純な INSERT 文を実行するコードを書きます。
```
<insert id="insertName">
  insert into names (name) values (#{value})
</insert>
```
あとは Java のコードでバッチ処理を実行するだけです。
```
List<String> names = new ArrayList<String>();
names.add("Fred");
names.add("Barney");
names.add("Betty");
names.add("Wilma");

SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
try {
  NameMapper mapper = sqlSession.getMapper(NameMapper.class);
  for (String name : names) {
    mapper.insertName(name);
  }
  sqlSession.commit();
} finally {
  sqlSession.close();
}
```

### 自動採番された値はどうやって取得すれば良い？ ###
insert メソッドは **常に** int 型の値（挿入された行数）を返します。自動採番された値は insert メソッド実行時に、引数として渡したオブジェクトに設定されます。例えば、下記のようにして取得することができます。
```
<insert id="insertName" useGeneratedKeys="true" keyProperty="id">
  insert into names (name) values (#{name})
</insert>
```
```
Name name = new Name();
name.setName("Fred");

int rows = mapper.insertName(name);
System.out.println("rows inserted = " + rows);
System.out.println("generated key value = " + name.getId());
```

### Mapper のメソッドに複数の引数を渡すには？ ###
Java のリフレクションではメソッドの引数名を取得することができません。この問題を回避するため、MyBatis では引数に param1, param2... のような名前を付けています。
このデフォルトの引数名が気に入らなければ、 @Param アノテーションを使って明示的に変数名を宣言することもできます。
Mapper のメソッドを下記のように定義しておくと...
```
import org.apache.ibatis.annotations.Param;
public interface UserMapper {
   User selectUser(@Param("username") String username, @Param("hashedPassword") String hashedPassword);
}
```
XML では下記のようにして引数を参照することができます。
```
<select id=”selectUser” resultType=”User”>
  select id, username, hashedPassword
  from some_table
  where username = #{username}
  and hashedPassword = #{hashedPassword}
</select>
```
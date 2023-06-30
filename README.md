# demo-todo
This is a little project that provide a web server with several endpoints to manage 'todo's.

## How to build
```
mvn clean install
```

## How to launch

```
java -jar target/todo-1.0-SNAPSHOT-jar-with-dependencies.jar <port>
```

## Available endpoints

- /check : should response `220 OK` if the server is available.
```
curl -v http://127.0.0.1:6789/check -H "Authorization: Bearer 1234567"
```
- /categ/list : list all categories
 ```
curl http://127.0.0.1:6789/categ/list -H "Authorization: Bearer 1234567"
```
- /fill : insert some data
```
curl http://127.0.0.1:6789/fill -H "Authorization: Bearer 1234567"
```
- /reset : reset all data
```
curl http://127.0.0.1:6789/reset -H "Authorization: Bearer 1234567"
```
- /todo/add
```
curl -i -X POST \
   -H "Content-Type:application/json" \
   -H "Authorization:Bearer 1234567" \
   -d \
'{
  "categ": "MyCateg",
  "todo": "What I have to do!"
}' \
 'http://127.0.0.1:6789/todo/add'
```
- /todo/bulkAdd
```
curl -i -X POST \
   -H "Content-Type:application/json" \
   -H "Authorization:Bearer 1234567" \
   -d \
'[{
  "categ": "MyCateg",
  "todo": "What I have to do Bis!"
},
{
  "categ": "MyCateg",
  "todo": "Another one."
}]' \
 'http://127.0.0.1:6789/todo/bulkAdd'
```
- /todo/listAll : list all available todo
```
curl http://127.0.0.1:6789/todo/listAll -H "Authorization: Bearer 1234567"
```
- /todo/listOneCateg : List all todos from one category
```
curl http://127.0.0.1:6789/todo/listOneCateg -H "Categ: mycateg" -H "Authorization: Bearer 1234567"
```

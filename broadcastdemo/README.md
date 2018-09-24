# WebSocket Server Demo

本Demo来自[getting-started-with-netty-building-a-websocket-broadcast-server](https://leanjava.co/2018/03/14/getting-started-with-netty-building-a-websocket-broadcast-server/)

一些WebSocket的基础知识：[WebSocket 教程](http://www.ruanyifeng.com/blog/2017/05/websocket.html)

运行服务后，可在服务器控制台输入：

`ws = new WebSocket("ws://localhost:9080");` 创建一个新的实例


指定收到服务器数据后的回调函数
```JavaScript
ws.onmessage = function(event) {
  console.log("Received data: " + event.data);
};
```

`ws.send("Brave new world!");` 发送一个消息
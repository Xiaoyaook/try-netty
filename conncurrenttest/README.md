# 并发量测试

阻塞Eventloop会大幅度影响性能

尝试两种解决方法，

1. 如ServerBusinessThreadPoolHandler，手动创建线程池处理业务逻辑
2. 如Server中新建一个EventLoopGroup来负责处理业务逻辑
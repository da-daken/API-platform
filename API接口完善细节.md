## 项目完善

### 1. SDK的实例代码加上（已完成）

### 2. 提供SDK的依赖（文字显示再旁边）（半完成）

​		后续有前端的话和前端进行商量，在interfaceInfo表中加个sdk字段

### 3. 在网关层面把接口地址的前缀完成（已完成）

​	加个在interfaceInfo表里加一个host字段，在后端

### 4. 在用户调用接口的时候防止并发将调用接口的剩余数量超量使用（已完成）

​	在查询剩余数量的时候，要判断至少还有1的时候才能放行，若并发的时候，多个线程进到这个方法，比如10个线程，剩余数量还有5个，但是都判断到还剩5个，然后放行进行操作，结果-5个（目前解决办法：对方法加锁）

### 5. 找个前端把返回信息改成code + data + msg的格式（已完成）

### 6.用户登录（半完成）

​	接口已经完成，前端界面没有完成

### 7. 用户购买接口的使用次数（已完成）

 1. 前端要穿的参数

    {

    ​		"userId" ： "Long" ,

    ​		"interfaceInfoId" : "Long" , 

    ​		"leftNum" : "Integer"  

    ​	}

 2.  业务逻辑

    1. 用一个dto接收参数

    2. 先用userId和interfaceInfoId查是否存在之前的记录

    3. 还存在的话直接在基础上加上leftNum （update）

       update user_interface_info set leftNum = leftNum + #{leftNum} where userId = #{userId} and interfaceInfoId = #{interfaceInfoId}

    4. 不存在的话加上一条记录上去（insert）

       insert user_interface_info 

    5. 返回购买成功


### 8. 将网关中远程调用的接口改成异步接收 ( 不完成，有问题的 )

​	用 future 接收多线程调用 不能异步接收，因为要先获取用户是否合法再接收接口是否存在

### 9. 将get方法的接口调通

### 10. 添加一些接口进去，同时加入sdk

### 11. 将sdk发布出去，用户可以直接使用

### 12. 用 docker 部署项目




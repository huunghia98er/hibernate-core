
# Sử dụng L1 cache in Hibernate

![img.png](hibernate-core-L1-cache/img/img.png)

# L1 cache không hoạt động trong trường hợp sau.

Commit 2

![img_1.png](hibernate-core-L1-cache/img/img_1.png)

#  Flush cache trong hibernate - no transaction

![img_2.png](hibernate-core-L1-cache/img/img_2.png)

#  Flush cache trong hibernate - transaction
### Theo config mặc định trong hibernate `session.flush()` sẽ được thực thi trước `transaction.commit()`
### Hibernate có các flush mode sau:

- `ALWAYS` - luôn luôn flush trước khi query
- `AUTO` - mặc định, flush trước khi query nếu cần
- `COMMIT` - flush trước khi commit transaction
- `MANUAL` - chỉ flush khi gọi `session.flush()`

![img_4.png](hibernate-core-L1-cache/img/img_4.png)

#  Test trường hợp transaction commit nhưng không flush cache

### Tại điểm debug đầu tiên
![img_5.png](hibernate-core-L1-cache/img/img_5.png)
![img_7.png](hibernate-core-L1-cache/img/img_7.png)
![img_8.png](hibernate-core-L1-cache/img/img_8.png)
  - Object Session đang được sử dụng và có biến closed = false
  - Config flush mode = MANUAL và không gọi session.flush() trước khi commit transaction

### Tại điểm debug thứ hai
![img_6.png](hibernate-core-L1-cache/img/img_6.png)
![img_9.png](hibernate-core-L1-cache/img/img_9.png)
![img_10.png](hibernate-core-L1-cache/img/img_10.png)
  - Sau khi thoát khỏi hàm thì Object Session đã được đóng, biến closed = true

### Sau khi thoát ra khỏi controller
![img_11.png](hibernate-core-L1-cache/img/img_11.png)
  - Object Session đã được clear bởi GC



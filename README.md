# library-jpa-crud

## 1 часть
- создала класс модель User, таблицу users, класс DAOUser
- заменила класс Servlet на класс FrontControllerFilter



## 2 часть
- добавила колонку role в таблицу users, поле в класс User
- метод getRole, в методе register автоматически роль user в AuthService
- в методе register новому пользователю автоматически присваивается роль "пользователь"
- в AuthController при входе в атрибуты добавляем роль (role)
- чтобы сделать пользователя админом, нужно изменить таблицу users

http://localhost:8080/lab4_Vyshnikova/home
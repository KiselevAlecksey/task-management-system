MERGE INTO users AS target
USING (SELECT 'ivan@example.com' AS email, 'Иван Иванов' AS name, 'ivan_password' AS password, 'ADMIN' AS role UNION ALL
       SELECT 'petr@example.com', 'Петр Петров', 'petr_password', 'GUEST' UNION ALL
       SELECT 'anna@example.com', 'Анна Сидорова', 'anna_password', 'USER') AS source
ON target.email = source.email
WHEN MATCHED THEN
    UPDATE SET target.name = source.name, target.password = source.password, target.role = source.role
WHEN NOT MATCHED THEN
    INSERT (name, email, password, role)
    VALUES (source.name, source.email, source.password, source.role);

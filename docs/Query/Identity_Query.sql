--- update role
UPDATE user_roles
SET role_id = (
    SELECT id
    FROM roles
    WHERE name = 'TEACHER'
)
WHERE user_id = '8d6a1d47-3c72-4b18-9f5b-5b4aaf7f9d0c';

--- check user role
SELECT r.id,
       r.name,
       r.description
FROM user_roles ur
         JOIN roles r
              ON ur.role_id = r.id
WHERE ur.user_id = 'USER_UUID';
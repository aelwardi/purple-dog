-- 1. Vérifier l'admin existant
SELECT id, email, first_name, last_name, role, account_status 
FROM persons 
WHERE email LIKE '%admin%';

-- 2. Si le rôle n'est PAS exactement 'ADMIN', le corriger:
UPDATE persons 
SET role = 'ADMIN', 
    account_status = 'ACTIVE',
    email_verified = true,
    phone_verified = true
WHERE email = 'admin@purpledog.com';

-- 3. Vérifier que l'entrée dans la table admins existe
SELECT p.id, p.email, p.role, a.super_admin
FROM persons p
LEFT JOIN admins a ON p.id = a.id  
WHERE p.email = 'admin@purpledog.com';

-- 4. Si l'entrée admins n'existe pas, la créer:
-- INSERT INTO admins (id, super_admin) 
-- SELECT id, true FROM persons WHERE email = 'admin@purpledog.com'
-- ON DUPLICATE KEY UPDATE super_admin = true;

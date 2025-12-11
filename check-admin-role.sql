-- Vérifier le rôle de l'admin dans la base de données
SELECT 
    id,
    email,
    first_name,
    last_name,
    role,
    account_status
FROM persons 
WHERE email = 'admin@purpledog.com';

-- Si le rôle n'est pas ADMIN, le corriger:
-- UPDATE persons SET role = 'ADMIN' WHERE email = 'admin@purpledog.com';

INSERT INTO users_table (id, username, email, password, role)
VALUES (1000, 'main_admin', 'admin@mail.ru', '$2a$12$/EfhoNT97FtSt137XS.mSuDbqbXyRIzVkpAHoHN9RDPGJAkNMBL2q', 'ADMIN')
ON CONFLICT (id) DO NOTHING;        
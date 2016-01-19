psql -U postgres -f creaBD.sql
echo La contraseña es 1234
psql -f tablas.sql blufeedme_db root_blufeedme_db
psql -f InsertaBD.sql blufeedme_db root_blufeedme_db
pause

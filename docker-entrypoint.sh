#!/bin/sh
set -e

# Start the MySQL server
mysqld --user=mysql --datadir=/var/lib/mysql &

# Wait for the MySQL server to start up
echo "Waiting for MySQL to start up..."
while ! mysqladmin ping --silent; do
  sleep 1
done

# Execute the command specified in the CMD directive
exec "$@"

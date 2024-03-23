#!/bin/bash

# execute any pre-init scripts
for _script in /scripts/pre-init.d/*sh
do
  if [ -e "${_script}" ]; then
    echo "[info] pre-init.d - processing ${_script}"
    . "${_script}"
  fi
done

if [ -d "/run/mysqld" ]; then
  echo "[info] mysqld already present, skipping creation"
  chown -R mysql:mysql /run/mysqld
else
  echo "[info] mysqld not found, creating...."
  mkdir -p /run/mysqld
  chown -R mysql:mysql /run/mysqld
fi

if [ -d /var/lib/mysql/mysql ]; then
  echo "[info] MySQL directory already present, skipping creation"
  chown -R mysql:mysql /var/lib/mysql
else
  echo "[info] MySQL data directory not found, creating initial DBs"

  chown -R mysql:mysql /var/lib/mysql

  mysql_install_db --user=mysql --ldata=/var/lib/mysql > /dev/null

  if [ "${MYSQL_ROOT_PASSWORD}" = "" ]; then
    MYSQL_ROOT_PASSWORD=`pwgen 16 1`
    echo "[info] MySQL root Password: ${MYSQL_ROOT_PASSWORD}"
  fi

  MYSQL_DATABASE=${MYSQL_DATABASE:-""}
  MYSQL_USER=${MYSQL_USER:-""}
  MYSQL_PASSWORD=${MYSQL_PASSWORD:-""}

  tfile=`mktemp`
  if [ ! -f "${tfile}" ]; then
      return 1
  fi

  cat << EOF > ${tfile}
USE mysql;
FLUSH PRIVILEGES ;
GRANT ALL ON *.* TO 'root'@'%' identified by '${MYSQL_ROOT_PASSWORD}' WITH GRANT OPTION ;
GRANT ALL ON *.* TO 'root'@'localhost' identified by '${MYSQL_ROOT_PASSWORD}' WITH GRANT OPTION ;
SET PASSWORD FOR 'root'@'localhost'=PASSWORD('${MYSQL_ROOT_PASSWORD}') ;
DROP USER ''@'localhost' ;
DROP DATABASE IF EXISTS test ;
FLUSH PRIVILEGES ;
EOF

  if [ "${MYSQL_DATABASE}" != "" ]; then
    echo "[info] Creating database: ${MYSQL_DATABASE}"

    if [ "${MYSQL_CHARSET}" != "" ] && [ "${MYSQL_COLLATION}" != "" ]; then
      echo "[info] with character set [${MYSQL_CHARSET}] and collation [${MYSQL_COLLATION}]"
      echo "CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\` CHARACTER SET ${MYSQL_CHARSET} COLLATE ${MYSQL_COLLATION};" >> ${tfile}
    else
      echo "[info] with character set: 'utf8' and collation: 'utf8_general_ci'"
      echo "CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\` CHARACTER SET utf8 COLLATE utf8_general_ci;" >> ${tfile}
    fi

    if [ "${MYSQL_USER}" != "" ]; then
      echo "[i] Creating user: ${MYSQL_USER} with password ${MYSQL_PASSWORD}"
      echo "GRANT ALL ON \`${MYSQL_DATABASE}\`.* to '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';" >> ${tfile}
    fi
  fi

  /usr/bin/mysqld --user=mysql --bootstrap --verbose=0 --skip-name-resolve --skip-networking=0 < ${tfile}
  rm -f ${tfile}

  for _file in /docker-entrypoint-initdb.d/*; do
    case "${_file}" in
      *.sql)
        echo "$0: running ${_file}"
        /usr/bin/mysqld --user=mysql --bootstrap --verbose=0 --skip-name-resolve --skip-networking=0 < "${_file}"
        echo
        ;;

      *.sql.gz)
        echo "$0: running ${_file}"
        gunzip -c "${_file}" | /usr/bin/mysqld --user=mysql --bootstrap --verbose=0 --skip-name-resolve --skip-networking=0
        echo
        ;;

      *)
        echo "$0: ignoring or entrypoint initdb empty ${_file}"
        ;;
    esac
    echo
  done

  echo
  echo 'MySQL init process done. Ready for start up.'
  echo

  echo "exec /usr/bin/mysqld --user=mysql --console --skip-name-resolve --skip-networking=0" "$@"
fi

# execute any pre-exec scripts
for _script in /scripts/pre-exec.d/*sh
do
  if [ -e "${_script}" ]; then
    echo "[script] pre-exec.d - processing ${_script}"
    . ${_script}
  fi
done

exec /usr/bin/mysqld --user=mysql --console --skip-name-resolve --skip-networking=0 $@

## How to use this example of `sample-app`
### Step1: Create a database and an user to operate it
Run the following commands, where you have to wait for about **10sec** to destroy the database-server's container.

```bash
docker-compose up -d database-server
sleep 10
docker-compose down
```

### Step2: Create tables
In this example, the `UserRole` table and the `User` table consist of the following structure.

#### `UserRole` table
| Column name | Data type/Reference | Constraint                  | Default value |
| :----       | :----               | :----                       | :----         |
| id          | INT                 | PRIMARY KEY, AUTO_INCREMENT | -             |
| type        | VARCHAR(16)         | NOT NULL, UNIQUE            | 'Viewer'      |

#### `User` table
| Column name | Data type/Reference | Constraint                                       | Default value |
| :----       | :----               | :----                                            | :----         |
| id          | INT                 | PRIMARY KEY, AUTO_INCREMENT                      | -             |
| name        | VARCHAR(255)        | NOT NULL, NOT BLANK                              | -             |
| role        | ref: UserRole(id)   | FOREIGN KEY ON UPDATE CASCADE ON DELETE SET NULL | -             |

Therefore, you have to execute the following SQL statements to create the tables of the above structure.

```sql
CREATE TABLE UserRole (
  id   INT         AUTO_INCREMENT,
  type VARCHAR(16) NOT NULL UNIQUE DEFAULT 'Viewer',
  PRIMARY KEY (id)
) ;
```

```sql
CREATE TABLE User (
  id   INT          AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL CHECK (LENGTH(name) > 0),
  role INT,
  PRIMARY KEY (id),
  FOREIGN KEY (role) REFERENCES UserRole(id) ON UPDATE CASCADE ON DELETE SET NULL
) ;
```

Moreover, you have to also execute the following SQL statement to insert the records of user's role.

```sql
INSERT INTO UserRole(type) values ('Admin'), ('Editor'), (default) ;
```

In anticipation of the above work, I have created [`create-tables-for-sample-app.sql`](create-tables-for-sample-app.sql) with SQL statements.

In short, you will enter the following commands on your terminal.

```bash
docker-compose up -d database-server
sleep 3 # After waiting for few seconds...
cat create-tables-for-sample-app.sql | docker exec -i database-server /bin/bash -c 'cat - | sed -e "s|USE database|USE ${MYSQL_DATABASE}|" | mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD}'
```

### Step3: Copy source codes
Execute the following command to copy source codes to `maven/project` directory.

```bash
cp -rf sample-app ../maven/project/
```

For subsequent work, see Step6 of [README.md](../README.md) for detail.

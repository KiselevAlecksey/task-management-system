-- Table: tasks

CREATE TABLE IF NOT EXISTS tasks (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(32) NOT NULL,
  description VARCHAR(2000),
  status VARCHAR(32),
  priority VARCHAR(32),
  creator_id BIGINT NOT NULL,
  executor_id BIGINT NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
);

-- Table: comments

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content TEXT,
  creator_id BIGINT NOT NULL,
  task_id BIGINT NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT COMMENT_TASK_ID_FK FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);



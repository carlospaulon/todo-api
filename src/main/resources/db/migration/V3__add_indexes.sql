-- index to search tasks by user
CREATE INDEX idx_tasks_user_id ON tasks(user_id);

-- index to filter by status
CREATE INDEX idx_tasks_status ON tasks(status);

-- index to filter user and status
CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);

-- index created_at order
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

-- index username search
CREATE INDEX idx_users_username ON users(username);

-- index email search
CREATE INDEX idx_users_email ON users(email);
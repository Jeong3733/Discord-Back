INSERT INTO user (email_authenticated, birth, created_at, updated_at, profile_image, email, nickname, password, profile_message, role, user_status)
VALUES
    (FALSE, '1990-01-01 00:00:00', NOW(), NOW(), NULL, 'user1@example.com', 'nickname1', 'Password#1', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1991-02-02 00:00:00', NOW(), NOW(), NULL, 'user2@example.com', 'nickname2', 'Password#2', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1992-03-03 00:00:00', NOW(), NOW(), NULL, 'user3@example.com', 'nickname3', 'Password#3', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1993-04-04 00:00:00', NOW(), NOW(), NULL, 'user4@example.com', 'nickname4', 'Password#4', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1994-05-05 00:00:00', NOW(), NOW(), NULL, 'user5@example.com', 'nickname5', 'Password#5', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1995-06-06 00:00:00', NOW(), NOW(), NULL, 'user6@example.com', 'nickname6', 'Password#6', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1996-07-07 00:00:00', NOW(), NOW(), NULL, 'user7@example.com', 'nickname7', 'Password#7', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1997-08-08 00:00:00', NOW(), NOW(), NULL, 'user8@example.com', 'nickname8', 'Password#8', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1998-09-09 00:00:00', NOW(), NOW(), NULL, 'user9@example.com', 'nickname9', 'Password#9', NULL, 'USER', 'OFFLINE'),
    (FALSE, '1999-10-10 00:00:00', NOW(), NOW(), NULL, 'user10@example.com', 'nickname10', 'Password#10', NULL, 'USER', 'OFFLINE');

INSERT INTO server (created_at, updated_at, profile_image, description, name)
VALUES
    (NOW(), NOW(), NULL, 'Team 1 Description', 'Team1'),
    (NOW(), NOW(), NULL, 'Team 2 Description', 'Team2'),
    (NOW(), NOW(), NULL, 'Team 3 Description', 'Team3'),
    (NOW(), NOW(), NULL, 'Team 4 Description', 'Team4');

-- Team 1 Members (User ID: 1, 2, 3)
INSERT INTO user_server (created_at, server_id, user_id, user_status)
VALUES
    (NOW(), 1, 1, 'ONLINE'),
    (NOW(), 1, 2, 'OFFLINE'),
    (NOW(), 1, 3, 'ONLINE');

-- Team 2 Members (User ID: 4, 5, 6)
INSERT INTO user_server (created_at, server_id, user_id, user_status)
VALUES
    (NOW(), 2, 4, 'OFFLINE'),
    (NOW(), 2, 5, 'ONLINE'),
    (NOW(), 2, 6, 'OFFLINE');

-- Team 3 Members (User ID: 7, 8, 9)
INSERT INTO user_server (created_at, server_id, user_id, user_status)
VALUES
    (NOW(), 3, 7, 'ONLINE'),
    (NOW(), 3, 8, 'OFFLINE'),
    (NOW(), 3, 9, 'ONLINE');

-- Team 4 Members (User ID: 10, 1, 2)
INSERT INTO user_server (created_at, server_id, user_id, user_status)
VALUES
    (NOW(), 4, 10, 'OFFLINE'),
    (NOW(), 4, 1, 'ONLINE'),
    (NOW(), 4, 2, 'OFFLINE');




INSERT INTO users (username, password, role)
VALUES  ('testadmin', 'qwerTy12', 'ADMIN'),
		('testuser1', 'qwerTy12', 'USER'),
		('testuser2', 'qwerTy12', 'USER');

INSERT INTO urls (short_url, url, description, user_id, created_date, expiration_date, visit_count)
VALUES  ('testurl1', 'https://some_long_named_portal.com/', 'for test only', 1, now(), now() + interval '10 days', 1),
		('testurl2', 'https://some_long_named_portal.com/', 'for test only', 1, now(), now() + interval '10 days', 1),
		('testurl3', 'https://some_long_named_portal.com/', 'for test only', 2, now(), now() + interval '10 days', 1),
		('testurl4', 'https://some_long_named_portal.com/', 'for test only', 3, now(), now() + interval '10 days', 1);
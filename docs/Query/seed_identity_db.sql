-- Seed data for identity_db.
-- Run against database: identity_db
-- Password for seeded users: Password123

BEGIN;

-- Keep fixed IDs stable for sample accounts if this script is re-run.
DELETE FROM user_roles
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM user_profiles
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM user_mfa_settings
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM refresh_tokens
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM login_histories
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM user_security_logs
WHERE user_id IN (
    SELECT id FROM users
    WHERE email IN (
        'seed.student@st3p.test',
        'seed.teacher@st3p.test',
        'seed.admin@st3p.test',
        'seed.moderator@st3p.test',
        'seed.mentor@st3p.test'
    )
    AND id NOT IN (
        '00000000-0000-4000-8000-000000000001',
        '00000000-0000-4000-8000-000000000002',
        '00000000-0000-4000-8000-000000000003',
        '00000000-0000-4000-8000-000000000004',
        '00000000-0000-4000-8000-000000000005'
    )
);

DELETE FROM users
WHERE email IN (
    'seed.student@st3p.test',
    'seed.teacher@st3p.test',
    'seed.admin@st3p.test',
    'seed.moderator@st3p.test',
    'seed.mentor@st3p.test'
)
AND id NOT IN (
    '00000000-0000-4000-8000-000000000001',
    '00000000-0000-4000-8000-000000000002',
    '00000000-0000-4000-8000-000000000003',
    '00000000-0000-4000-8000-000000000004',
    '00000000-0000-4000-8000-000000000005'
);

INSERT INTO permissions (id, code, description) VALUES
    ('20000000-0000-4000-8000-000000000001', 'course.create', 'Create courses'),
    ('20000000-0000-4000-8000-000000000002', 'course.update', 'Update courses'),
    ('20000000-0000-4000-8000-000000000003', 'course.delete', 'Delete/archive courses'),
    ('20000000-0000-4000-8000-000000000004', 'user.manage', 'Manage users'),
    ('20000000-0000-4000-8000-000000000005', 'payment.refund', 'Refund payments'),
    ('20000000-0000-4000-8000-000000000006', 'course.approve', 'Approve or reject courses'),
    ('20000000-0000-4000-8000-000000000007', 'category.manage', 'Manage categories'),
    ('20000000-0000-4000-8000-000000000008', 'tag.manage', 'Manage tags'),
    ('20000000-0000-4000-8000-000000000009', 'review.reply', 'Reply to course reviews')
ON CONFLICT (code) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO roles (id, name, description) VALUES
    ('10000000-0000-4000-8000-000000000001', 'STUDENT', 'Student learner role'),
    ('10000000-0000-4000-8000-000000000002', 'TEACHER', 'Teacher course author role'),
    ('10000000-0000-4000-8000-000000000003', 'ADMIN', 'Platform administrator role'),
    ('10000000-0000-4000-8000-000000000004', 'MODERATOR', 'Community moderator role'),
    ('10000000-0000-4000-8000-000000000005', 'MENTOR', 'Learning mentor role')
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'course.create',
    'course.update',
    'review.reply'
)
WHERE r.name = 'TEACHER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'course.create',
    'course.update',
    'course.delete',
    'user.manage',
    'payment.refund',
    'course.approve',
    'category.manage',
    'tag.manage',
    'review.reply'
)
WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('review.reply')
WHERE r.name IN ('MODERATOR', 'MENTOR')
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO users (
    id,
    email,
    email_verified,
    last_login_at,
    password_hash,
    status,
    username,
    created_at,
    updated_at
) VALUES
    (
        '00000000-0000-4000-8000-000000000001',
        'seed.student@st3p.test',
        true,
        now() - interval '1 hour',
        '$2a$10$TDLcaqtjQNLO.gSPszlZKeGhDl3KME545k3boSNCRdAcGJhdigM7O',
        'ACTIVE',
        'seed_student',
        now() - interval '30 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000002',
        'seed.teacher@st3p.test',
        true,
        now() - interval '2 hours',
        '$2a$10$TDLcaqtjQNLO.gSPszlZKeGhDl3KME545k3boSNCRdAcGJhdigM7O',
        'ACTIVE',
        'seed_teacher',
        now() - interval '60 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000003',
        'seed.admin@st3p.test',
        true,
        now() - interval '30 minutes',
        '$2a$10$TDLcaqtjQNLO.gSPszlZKeGhDl3KME545k3boSNCRdAcGJhdigM7O',
        'ACTIVE',
        'seed_admin',
        now() - interval '90 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000004',
        'seed.moderator@st3p.test',
        true,
        now() - interval '4 hours',
        '$2a$10$TDLcaqtjQNLO.gSPszlZKeGhDl3KME545k3boSNCRdAcGJhdigM7O',
        'ACTIVE',
        'seed_moderator',
        now() - interval '45 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000005',
        'seed.mentor@st3p.test',
        true,
        now() - interval '6 hours',
        '$2a$10$TDLcaqtjQNLO.gSPszlZKeGhDl3KME545k3boSNCRdAcGJhdigM7O',
        'ACTIVE',
        'seed_mentor',
        now() - interval '50 days',
        now()
    )
ON CONFLICT (id) DO UPDATE
SET email = EXCLUDED.email,
    email_verified = EXCLUDED.email_verified,
    last_login_at = EXCLUDED.last_login_at,
    password_hash = EXCLUDED.password_hash,
    status = EXCLUDED.status,
    username = EXCLUDED.username,
    updated_at = now();

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-4000-8000-000000000001'::uuid, id FROM roles WHERE name = 'STUDENT'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-4000-8000-000000000002'::uuid, id FROM roles WHERE name = 'TEACHER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-4000-8000-000000000003'::uuid, id FROM roles WHERE name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-4000-8000-000000000004'::uuid, id FROM roles WHERE name = 'MODERATOR'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-4000-8000-000000000005'::uuid, id FROM roles WHERE name = 'MENTOR'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_profiles (
    user_id,
    avatar_url,
    bio,
    birth_date,
    country,
    english_level,
    full_name,
    public_id,
    timezone,
    created_at,
    updated_at
) VALUES
    (
        '00000000-0000-4000-8000-000000000001',
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=256&auto=format&fit=crop',
        'Seed student account for end-to-end learning tests.',
        '2001-04-12',
        'Vietnam',
        'INTERMEDIATE',
        'Seed Student',
        'pub_student_01',
        'Asia/Bangkok',
        now() - interval '30 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000002',
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=256&auto=format&fit=crop',
        'IELTS and academic English teacher.',
        '1988-09-20',
        'Vietnam',
        'ADVANCED',
        'Seed Teacher',
        'pub_teacher_01',
        'Asia/Bangkok',
        now() - interval '60 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000003',
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=256&auto=format&fit=crop',
        'Administrator for St3pLearn local test environment.',
        '1990-01-15',
        'Vietnam',
        'ADVANCED',
        'Seed Admin',
        'pub_admin_01',
        'Asia/Bangkok',
        now() - interval '90 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000004',
        null,
        'Community moderator seed account.',
        '1995-11-03',
        'Vietnam',
        'UPPER_INTERMEDIATE',
        'Seed Moderator',
        'pub_mod_01',
        'Asia/Bangkok',
        now() - interval '45 days',
        now()
    ),
    (
        '00000000-0000-4000-8000-000000000005',
        null,
        'Learning mentor seed account.',
        '1992-07-19',
        'Vietnam',
        'ADVANCED',
        'Seed Mentor',
        'pub_mentor_01',
        'Asia/Bangkok',
        now() - interval '50 days',
        now()
    )
ON CONFLICT (user_id) DO UPDATE
SET avatar_url = EXCLUDED.avatar_url,
    bio = EXCLUDED.bio,
    birth_date = EXCLUDED.birth_date,
    country = EXCLUDED.country,
    english_level = EXCLUDED.english_level,
    full_name = EXCLUDED.full_name,
    public_id = EXCLUDED.public_id,
    timezone = EXCLUDED.timezone,
    updated_at = now();

INSERT INTO user_mfa_settings (
    user_id,
    enabled,
    recovery_codes,
    secret_key,
    updated_at
) VALUES
    ('00000000-0000-4000-8000-000000000001', false, '[]'::jsonb, null, now()),
    ('00000000-0000-4000-8000-000000000002', false, '[]'::jsonb, null, now()),
    ('00000000-0000-4000-8000-000000000003', true, '["ADM-REC-001","ADM-REC-002"]'::jsonb, 'seed-admin-mfa-secret', now()),
    ('00000000-0000-4000-8000-000000000004', false, '[]'::jsonb, null, now()),
    ('00000000-0000-4000-8000-000000000005', false, '[]'::jsonb, null, now())
ON CONFLICT (user_id) DO UPDATE
SET enabled = EXCLUDED.enabled,
    recovery_codes = EXCLUDED.recovery_codes,
    secret_key = EXCLUDED.secret_key,
    updated_at = now();

INSERT INTO refresh_tokens (
    id,
    user_id,
    token_hash,
    expires_at,
    revoked_at,
    device_info,
    ip_address,
    created_at
) VALUES
    (
        '00000000-0000-4000-9000-000000000001',
        '00000000-0000-4000-8000-000000000001',
        'seed-revoked-refresh-token-hash-student',
        now() + interval '7 days',
        now() - interval '1 day',
        'Chrome on Windows',
        '127.0.0.1',
        now() - interval '2 days'
    ),
    (
        '00000000-0000-4000-9000-000000000002',
        '00000000-0000-4000-8000-000000000002',
        'seed-revoked-refresh-token-hash-teacher',
        now() + interval '7 days',
        now() - interval '1 day',
        'Edge on Windows',
        '127.0.0.1',
        now() - interval '2 days'
    )
ON CONFLICT (id) DO UPDATE
SET token_hash = EXCLUDED.token_hash,
    expires_at = EXCLUDED.expires_at,
    revoked_at = EXCLUDED.revoked_at,
    device_info = EXCLUDED.device_info,
    ip_address = EXCLUDED.ip_address;

INSERT INTO login_histories (
    id,
    user_id,
    login_at,
    ip_address,
    device_info,
    location,
    success
) VALUES
    ('00000000-0000-4001-9000-000000000001', '00000000-0000-4000-8000-000000000001', now() - interval '1 hour', '127.0.0.1', 'Chrome on Windows', 'Localhost', true),
    ('00000000-0000-4001-9000-000000000002', '00000000-0000-4000-8000-000000000002', now() - interval '2 hours', '127.0.0.1', 'Edge on Windows', 'Localhost', true),
    ('00000000-0000-4001-9000-000000000003', '00000000-0000-4000-8000-000000000003', now() - interval '30 minutes', '127.0.0.1', 'Chrome on Windows', 'Localhost', true),
    ('00000000-0000-4001-9000-000000000004', '00000000-0000-4000-8000-000000000001', now() - interval '10 days', '10.0.0.15', 'Unknown Browser', 'Unknown', false)
ON CONFLICT (id) DO UPDATE
SET login_at = EXCLUDED.login_at,
    ip_address = EXCLUDED.ip_address,
    device_info = EXCLUDED.device_info,
    location = EXCLUDED.location,
    success = EXCLUDED.success;

INSERT INTO user_security_logs (
    id,
    user_id,
    event_type,
    metadata,
    created_at
) VALUES
    ('00000000-0000-4002-9000-000000000001', '00000000-0000-4000-8000-000000000001', 'ACCOUNT_CREATED', '{"source":"seed","emailVerified":true}'::jsonb, now() - interval '30 days'),
    ('00000000-0000-4002-9000-000000000002', '00000000-0000-4000-8000-000000000002', 'ACCOUNT_CREATED', '{"source":"seed","emailVerified":true}'::jsonb, now() - interval '60 days'),
    ('00000000-0000-4002-9000-000000000003', '00000000-0000-4000-8000-000000000003', 'MFA_ENABLED', '{"source":"seed","method":"totp"}'::jsonb, now() - interval '20 days'),
    ('00000000-0000-4002-9000-000000000004', '00000000-0000-4000-8000-000000000001', 'LOGIN_FAILED', '{"ip":"10.0.0.15","reason":"bad_password"}'::jsonb, now() - interval '10 days')
ON CONFLICT (id) DO UPDATE
SET event_type = EXCLUDED.event_type,
    metadata = EXCLUDED.metadata,
    created_at = EXCLUDED.created_at;

COMMIT;

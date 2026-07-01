-- Active: 1782630393903@@127.0.0.1@5432@learning_db
-- Seed data for learning_db.
-- Run against database: learning_db
-- Cross-service IDs match seed_identity_db.sql and seed_catalog_db.sql.

BEGIN;

INSERT INTO course_replicas (id, status) VALUES
    ('40000000-0000-4000-8000-000000000001', 'PUBLISHED'),
    ('40000000-0000-4000-8000-000000000002', 'PUBLISHED'),
    ('40000000-0000-4000-8000-000000000003', 'PENDING_REVIEW'),
    ('40000000-0000-4000-8000-000000000004', 'DRAFT')
ON CONFLICT (id) DO UPDATE
SET status = EXCLUDED.status;

INSERT INTO enrollments (
    id,
    completed_at,
    course_id,
    enrolled_at,
    last_accessed_at,
    last_accessed_lesson_id,
    progress_percent,
    status,
    student_id
) VALUES
    (
        '50000000-0000-4000-8000-000000000001',
        null,
        '40000000-0000-4000-8000-000000000001',
        now() - interval '20 days',
        now() - interval '2 hours',
        '42000000-0000-4000-8000-000000000003',
        66.67,
        'ACTIVE',
        '00000000-0000-4000-8000-000000000001'
    ),
    (
        '50000000-0000-4000-8000-000000000002',
        now() - interval '3 days',
        '40000000-0000-4000-8000-000000000002',
        now() - interval '12 days',
        now() - interval '3 days',
        '42000000-0000-4000-8000-000000000004',
        100.00,
        'COMPLETED',
        '00000000-0000-4000-8000-000000000001'
    )
ON CONFLICT (student_id, course_id) DO UPDATE
SET completed_at = EXCLUDED.completed_at,
    enrolled_at = EXCLUDED.enrolled_at,
    last_accessed_at = EXCLUDED.last_accessed_at,
    last_accessed_lesson_id = EXCLUDED.last_accessed_lesson_id,
    progress_percent = EXCLUDED.progress_percent,
    status = EXCLUDED.status;

COMMIT;

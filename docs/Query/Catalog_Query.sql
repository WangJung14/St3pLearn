--- Create categories
INSERT INTO categories (id, name, slug) VALUES
                                            (gen_random_uuid(), 'Speaking', 'speaking'),
                                            (gen_random_uuid(), 'Listening', 'listening'),
                                            (gen_random_uuid(), 'Grammar', 'grammar'),
                                            (gen_random_uuid(), 'Vocabulary', 'vocabulary'),
                                            (gen_random_uuid(), 'Writing', 'writing');
--- Create tags
INSERT INTO tags (id, name) VALUES
                                (gen_random_uuid(), 'IELTS'),
                                (gen_random_uuid(), 'TOEIC'),
                                (gen_random_uuid(), 'TOEFL'),
                                (gen_random_uuid(), 'A1'),
                                (gen_random_uuid(), 'A2'),
                                (gen_random_uuid(), 'B1'),
                                (gen_random_uuid(), 'B2'),
                                (gen_random_uuid(), 'C1'),
                                (gen_random_uuid(), 'C2');
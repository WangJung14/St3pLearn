BEGIN;

WITH orders(ord, course_id, original_amount, discount_amount, status) AS (
  VALUES
    (1, '11000000-0000-0000-0000-000000000001'::uuid, 299000::numeric, 0::numeric, 'PAID'),
    (2, '11000000-0000-0000-0000-000000000002'::uuid, 399000::numeric, 50000::numeric, 'PAID'),
    (3, '11000000-0000-0000-0000-000000000003'::uuid, 549000::numeric, 0::numeric, 'PAID'),
    (4, '11000000-0000-0000-0000-000000000006'::uuid, 999000::numeric, 150000::numeric, 'PAID'),
    (5, '11000000-0000-0000-0000-000000000009'::uuid, 599000::numeric, 0::numeric, 'PAID'),
    (6, '11000000-0000-0000-0000-000000000011'::uuid, 0::numeric, 0::numeric, 'PAID'),
    (7, '11000000-0000-0000-0000-000000000007'::uuid, 749000::numeric, 0::numeric, 'FAILED'),
    (8, '11000000-0000-0000-0000-000000000008'::uuid, 649000::numeric, 0::numeric, 'PENDING_PAYMENT')
)
INSERT INTO payment_orders (id, student_id, course_id, order_number, original_amount, discount_amount, final_amount, currency, status, created_at, updated_at)
SELECT md5('seed-payment-order:' || ord)::uuid, 'd0ea3ced-fae0-41d5-a5cb-e4b468069cbb'::uuid,
       course_id, 'SEED-2026-' || LPAD(ord::text, 4, '0'), original_amount, discount_amount,
       original_amount - discount_amount, 'VND', status, NOW() - (10 - ord) * INTERVAL '2 day', NOW()
FROM orders
ON CONFLICT (order_number) DO UPDATE SET status = EXCLUDED.status, updated_at = NOW();

WITH orders(ord, status) AS (
  VALUES (1, 'SUCCESS'), (2, 'SUCCESS'), (3, 'SUCCESS'), (4, 'SUCCESS'),
         (5, 'SUCCESS'), (6, 'SUCCESS'), (7, 'FAILED'), (8, 'PENDING')
)
INSERT INTO payment_transactions (id, payment_order_id, gateway, gateway_transaction_id, amount, status, request_idempotency_key, created_at, completed_at)
SELECT md5('seed-payment-transaction:' || ord)::uuid, md5('seed-payment-order:' || ord)::uuid,
       CASE WHEN ord = 6 THEN 'FREE' ELSE 'VNPAY' END,
       CASE WHEN o.status IN ('SUCCESS', 'FAILED') THEN 'VNP-SEED-' || LPAD(o.ord::text, 6, '0') ELSE NULL END,
       po.final_amount, o.status, 'seed-idempotency-' || o.ord, po.created_at,
       CASE WHEN o.status = 'PENDING' THEN NULL ELSE po.created_at + INTERVAL '3 minute' END
FROM orders o JOIN payment_orders po ON po.id = md5('seed-payment-order:' || o.ord)::uuid
ON CONFLICT (request_idempotency_key) DO UPDATE SET status = EXCLUDED.status,
  gateway_transaction_id = EXCLUDED.gateway_transaction_id, completed_at = EXCLUDED.completed_at;

INSERT INTO invoices (id, payment_order_id, invoice_number, invoice_url, issue_date)
SELECT md5('seed-invoice:' || n)::uuid, md5('seed-payment-order:' || n)::uuid,
       'INV-2026-' || LPAD(n::text, 4, '0'), '/student/payments?invoice=INV-2026-' || LPAD(n::text, 4, '0'), NOW() - (8 - n) * INTERVAL '2 day'
FROM generate_series(1, 6) n
ON CONFLICT (invoice_number) DO NOTHING;

COMMIT;

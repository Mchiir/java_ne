INSERT INTO tariffs (
    id,
    version,
    meter_type,
    consumption_rate, -- FRW per 1 Meter cube of water (one jeryycan=20 littles, on jerrycan=25FRW)
    fixed_service_charge,
    vat_rate,
    penalty_rate,
    effective_from,
    active,
    created_at
) VALUES (
    gen_random_uuid(),
    1,
    'WATER',
    1250.00,
    500.00,
    18.00,
    2.00,
    '2026-01-01',
    true,
    NOW()
);
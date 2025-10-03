INSERT INTO creatures (creature_id, registration_code, name, species, type, rarity, level, age, health, experience, status, strength, intelligence, agility, temperament)
VALUES
    ('8a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', 'REG-AF45E78D', 'Spark', 'Electabuzz', 'ELECTRIC', 'RARE', 25, 3, 85, 4500, 'AVAILABLE', 70, 65, 80, 'FRIENDLY'),
    ('9b0c1d2e-3f4a-5b6c-7d8e-9f0a1b2c3d4e', 'REG-B2F19CA3', 'Ember', 'Charmeleon', 'FIRE', 'UNCOMMON', 18, 2, 75, 2800, 'AVAILABLE', 65, 60, 70, 'AGGRESSIVE'),
    ('0c1d2e3f-4a5b-6c7d-8e9f-0a1b2c3d4e5f', 'REG-C8D37E5B', 'Cascade', 'Vaporeon', 'WATER', 'RARE', 30, 4, 90, 5200, 'RESERVED', 75, 85, 65, 'DOCILE'),
    ('1d2e3f4a-5b6c-7d8e-9f0a-1b2c3d4e5f6a', 'REG-D9E48F6C', 'Terra', 'Torterra', 'GRASS', 'UNCOMMON', 22, 5, 95, 3800, 'AVAILABLE', 85, 55, 45, 'DOCILE'),
    ('2e3f4a5b-6c7d-8e9f-0a1b-2c3d4e5f6a7b', 'REG-E0F59G7D', 'Shadow', 'Gengar', 'GHOST', 'EPIC', 35, 7, 70, 6400, 'ADOPTION_PENDING', 60, 90, 80, 'PLAYFUL'),
    ('3f4a5b6c-7d8e-9f0a-1b2c-3d4e5f6a7b8c', 'REG-F1G60H8E', 'Mystic', 'Alakazam', 'PSYCHIC', 'EPIC', 40, 8, 65, 7800, 'RESERVED', 50, 100, 70, 'TIMID'),
    ('4a5b6c7d-8e9f-0a1b-2c3d-4e5f6a7b8c9d', 'REG-G2H71I9F', 'Boulder', 'Golem', 'ROCK', 'COMMON', 28, 12, 100, 4200, 'AVAILABLE', 95, 45, 35, 'AGGRESSIVE'),
    ('5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e', 'REG-H3I82J0G', 'Freeze', 'Glaceon', 'ICE', 'RARE', 32, 3, 80, 5600, 'AVAILABLE', 65, 75, 85, 'FRIENDLY'),
    ('6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f', 'REG-I4J93K1H', 'Wave', 'Oshawott', 'WATER', 'UNCOMMON', 15, 1, 70, 2000, 'ADOPTED', 55, 65, 75, 'FRIENDLY'),
    ('7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a', 'REG-J5K04L2I', 'Splash', 'Piplup', 'WATER', 'LEGENDARY', 20, 2, 85, 3500, 'UNAVAILABLE', 60, 75, 70, 'TIMID'),
    ('8d9e0f1a-2b3c-4d5e-6f7a-8b9c0d1e2f3a', 'REG-K6L15M3J', 'Blaze', 'Infernape', 'FIRE', 'RARE', 36, 4, 88, 6200, 'ADOPTED', 90, 70, 95, 'AGGRESSIVE'),
    ('9e0f1a2b-3c4d-5e6f-7a8b-9c0d1e2f3a4b', 'REG-L7M26N4K', 'Leaf', 'Serperior', 'GRASS', 'RARE', 34, 5, 82, 5800, 'ADOPTED', 70, 85, 90, 'DOCILE'),
    ('a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6', 'REG-M8N37O5L', 'Bolt', 'Zebstrika', 'ELECTRIC', 'UNCOMMON', 28, 3, 78, 4200, 'AVAILABLE', 75, 60, 85, 'FRIENDLY'),
    ('b2c3d4e5-f6a7-b8c9-d0e1-f2a3b4c5d6e7', 'REG-N9O48P6M', 'Psyche', 'Gothitelle', 'PSYCHIC', 'EPIC', 42, 6, 75, 7000, 'AVAILABLE', 55, 95, 65, 'TIMID'),
    ('c3d4e5f6-a7b8-c9d0-e1f2-a3b4c5d6e7f8', 'REG-O0P59Q7N', 'Phantom', 'Chandelure', 'GHOST', 'EPIC', 38, 5, 72, 6800, 'AVAILABLE', 60, 85, 60, 'PLAYFUL'),

    -- For POST Success
    ('d1e2f3a4-b5c6-d7e8-f9a0-b1c2d3e4f5a6', 'REG-P1Q60R8S', 'Aurora', 'Alolan Vulpix', 'ICE', 'RARE', 15, 1, 80, 2500, 'AVAILABLE', 55, 65, 80, 'FRIENDLY'),
-- For POST Exception (unavailable)
    ('e2f3a4b5-c6d7-e8f9-a0b1-c2d3e4f5a6b7', 'REG-Q2R71S9T', 'Quake', 'Groudon', 'GROUND', 'LEGENDARY', 70, 25, 100, 12000, 'UNAVAILABLE', 100, 90, 70, 'AGGRESSIVE'),
-- For PUT Success
    ('f3a4b5c6-d7e8-f9a0-b1c2-d3e4f5a6b7c8', 'REG-R3S82T0U', 'Wisp', 'Misdreavus', 'GHOST', 'UNCOMMON', 22, 3, 75, 3200, 'AVAILABLE', 60, 85, 70, 'TIMID'),
-- For PUT Exception
    ('a4b5c6d7-e8f9-a0b1-c2d3-e4f5a6b7c8d9', 'REG-S4T93U1V', 'Glow', 'Lanturn', 'WATER', 'UNCOMMON', 27, 4, 85, 4100, 'AVAILABLE', 65, 70, 60, 'DOCILE'),
-- For DELETE Success
    ('b5c6d7e8-f9a0-b1c2-d3e4-f5a6b7c8d9e0', 'REG-T5U04V2W', 'Breeze', 'Jumpluff', 'GRASS', 'COMMON', 24, 2, 70, 3800, 'AVAILABLE', 55, 70, 90, 'FRIENDLY'),
-- For DELETE Exception (will be marked as completed)
    ('c6d7e8f9-a0b1-c2d3-e4f5-a6b7c8d9e0f1', 'REG-U6V15W3X', 'Dusk', 'Umbreon', 'DARK', 'RARE', 30, 5, 90, 5400, 'AVAILABLE', 70, 85, 75, 'TIMID'),

-- wait for service marked for deletion
('ce2706bd-cb1b-45bf-bb1f-1012643fd921', 'REG-K0D60R8S', 'Deletion', 'Oshawott2', 'WATER', 'RARE', 15, 1, 80, 2500, 'AVAILABLE', 55, 65, 80, 'FRIENDLY');
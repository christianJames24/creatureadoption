DELETE FROM customer_phonenumbers;
DELETE FROM customers;

INSERT INTO customers (customer_id, first_name, last_name, email_address, contact_method_preference, street_address, city, province, country, postal_code)
VALUES
    ('6f8d2e53-9b4c-48a7-91fe-c508dde7817a', 'Burgh', 'Arty', 'gym.leader@castelia.com', 'EMAIL', '123 Gallery St', 'Castelia City', 'UNOVA', 'United States', 'U2X 1Y2'),
    ('a3b7c9d1-e5f0-4a2b-8c9d-0e1f2a3b4c5d', 'Roark', 'Toichi', 'mining.leader@oreburgh.com', 'PHONE', '456 Coal Ave', 'Jubilife City', 'SINNOH', 'Japan', 'S4B 1B3'),
    ('7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a', 'Elesa', 'Kamitsure', 'electrifying@nimbasagym.com', 'TEXT', '789 Runway Rd', 'Nimbasa Town', 'UNOVA', 'United States', 'U5K 0A1'),
    ('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', 'Maylene', 'Sumomo', 'fighting.spirit@veilstone.com', 'EMAIL', '101 Dojo Ln', 'Veilstone City', 'SINNOH', 'Japan', 'S2P 2G8'),
    ('8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b', 'N', 'Harmonia', 'natural.friend@plasma.com', 'PHONE', '202 Liberation Dr', 'Accumula Town', 'UNOVA', 'United States', 'U1P 5G4'),
    ('4c5d6e7f-8a9b-0c1d-2e3f-4a5b6c7d8e9f', 'Caitlin', 'Cattleya', 'psychic.dreams@elite4.com', 'TEXT', '303 Dream Blvd', 'Celestic Town', 'SINNOH', 'Japan', 'S5J 2R4'),
    ('0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', 'Drayden', 'Shaga', 'dragon.master@opelucid.com', 'EMAIL', '404 Fang Ct', 'Opelucid City', 'UNOVA', 'United States', 'U3C 0V8'),
    ('6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f', 'Candice', 'Suzuna', 'icy.trainer@snowpoint.ucom', 'PHONE', '505 Glacier Way', 'Snowpoint Temple', 'SINNOH', 'Japan', 'S1R 4P3'),
    ('5d6e7f8a-9b0c-1d2e-3f4a-5b6c7d8e9f0a', 'Brycen', 'Hachiku', 'icy.mask@icirrus.com', 'TEXT', '606 Frost Path', 'Icirrus Moor', 'UNOVA', 'United States', 'U3H 1S8'),
    ('1f2a3b4c-5d6e-7f8a-9b0c-1d2e3f4a5b6c', 'Volkner', 'Denzi', 'electrifying@sunyshore.com', 'EMAIL', '707 Beacon St', 'Sunyshore Lighthouse', 'SINNOH', 'Japan', 'S8W 1W5'),
    ('2f3a4b5c-6d7e-8f9a-0b1c-2d3e4f5a6b7c', 'Professor', 'Juniper', 'prof.juniper@unova-research.com', 'EMAIL', '123 Research Way', 'Nuvema Town', 'UNOVA', 'United States', 'U7Y 2X1'),
--     ('3f4a5b6c-7d8e-9f0a-1b2c-3d4e5f6a7b8d', 'osharina', 'scalchop', 'osharina@scallop.com', 'PHONE', '1 Champion Road', 'Pokémon League', 'unova', 'ocean', 'S0A 1C1'),
    ('3f4a5b6c-7d8e-9f0a-1b2c-3d4e5f6a7b8c', 'Cynthia', 'Shirona', 'champion@sinnoh-league.com', 'PHONE', '1 Champion Road', 'Pokémon League', 'SINNOH', 'Japan', 'S0A 1C1'),
-- For POST Success and DELETE Success path
    ('d4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9', 'Clay', 'Yadon', 'mining.leader@driftveil.com', 'PHONE', '808 Mine St', 'Driftveil City', 'UNOVA', 'United States', 'U4F 9D2'),
-- For POST Exception (will have max adoptions)
    ('e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0', 'Lenora', 'Aloe', 'normal.leader@nacrene.com', 'EMAIL', '909 Museum Ave', 'Nacrene City', 'UNOVA', 'United States', 'U2Y 7F4'),
-- For PUT Success and Exception
    ('f6a7b8c9-d0e1-f2a3-b4c5-d6e7f8a9b0c1', 'Gardenia', 'Natane', 'grass.leader@eterna.com', 'TEXT', '101 Forest Path', 'Eterna City', 'SINNOH', 'Japan', 'S3G 5H7'),
-- For DELETE Exception
    ('a7b8c9d0-e1f2-a3b4-c5d6-e7f8a9b0c1d2', 'Skyla', 'Fuuro', 'flying.leader@mistralton.com', 'EMAIL', '202 Runway Ave', 'Mistralton City', 'UNOVA', 'United States', 'U6A 9J4'),

-- wait for service marked for deletion
    ('65206907-3648-47d7-80d1-96e6f364b168', 'tester', 'customer', 'test@test.com', 'EMAIL', '203 Runway Ave', 'Mistralton City', 'UNOVA', 'United States', 'U6A 9J5');

INSERT INTO customer_phonenumbers (customer_id, type, number)
VALUES
    (1, 'MOBILE', '212-555-1234'),
    (1, 'HOME', '212-555-5678'),
    (2, 'MOBILE', '813-555-2345'),
    (3, 'WORK', '347-555-3456'),
    (3, 'MOBILE', '347-555-7890'),
    (4, 'HOME', '81-555-4567'),
    (5, 'MOBILE', '516-555-5678'),
    (6, 'WORK', '81-555-6789'),
    (7, 'MOBILE', '718-555-7890'),
    (8, 'HOME', '81-555-8901'),
    (9, 'MOBILE', '917-555-9012'),
    (10, 'WORK', '81-555-0123'),
    (11, 'MOBILE', '908-555-7777'),
    (12, 'MOBILE', '81-555-9999'),
    (13, 'MOBILE', '910-555-1111'),
    (14, 'WORK', '910-555-2222'),
    (15, 'MOBILE', '81-555-3333'),
    (16, 'WORK', '910-555-4444');
insert into service (id, name)
    select 1, 'insurance_vehicle_third_party_inquiry'
    WHERE NOT EXISTS (SELECT * FROM service);

insert into endpoint (id, name, disabled)
    select 1, 'sample1', false union
    select 2, 'sample2', false
    WHERE NOT EXISTS (SELECT * FROM endpoint);

insert into endpoint_api (id, endpoint_id, service_id, method, url, disabled)
    select 1, 1, 1, 'get', 'api/v1/provider/one/inquiry', false union
    select 2, 2, 1, 'get', 'api/v1/provider/two/inquiry', false
    WHERE NOT EXISTS (SELECT * FROM endpoint_api);

insert into endpoint_network (id, endpoint_id, host, port, disabled)
    select 1, 1, 'localhost', 9001, false union
    select 2, 2, 'localhost', 9002, false
    WHERE NOT EXISTS (SELECT * FROM endpoint_network);

insert into endpoint_client (id, endpoint_id, endpoint_network_id, limitation_in_tps, limitation_in_total, disabled)
    select 1, 1, 1, 3, 10, false union
    select 2, 2, 2, 1, 1, false
    WHERE NOT EXISTS (SELECT * FROM endpoint_client);

insert into merchant (id, name, disabled)
    select 1, 'm1', false union
    select 2, 'm2', false
    WHERE NOT EXISTS (SELECT * FROM merchant);

insert into merchant_client (merchant_id, endpoint_client_id, disabled)
    select 1, 1, false union
    select 1, 2, false union
    select 2, 1, false union
    select 2, 2, false
    WHERE NOT EXISTS (SELECT * FROM merchant);

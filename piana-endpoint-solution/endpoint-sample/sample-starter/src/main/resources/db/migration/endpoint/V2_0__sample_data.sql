insert into service (id, name)
    select 1, 'insurance_vehicle_third_party_inquiry'
    WHERE NOT EXISTS (SELECT * FROM service);

insert into endpoint (id, name)
    select 1, 'sample1' union
    select 2, 'sample2'
    WHERE NOT EXISTS (SELECT * FROM endpoint);

insert into endpoint_api (id, endpoint_id, service_id, method, url)
    select 1, 1, 1, 'get', 'api/v1/provider/one/inquiry' union
    select 2, 2, 1, 'get', 'api/v1/provider/two/inquiry'
    WHERE NOT EXISTS (SELECT * FROM endpoint_api);

insert into endpoint_network (id, endpoint_id, host, port)
    select 1, 1, 'localhost', 9001 union
    select 2, 2, 'localhost', 9002
    WHERE NOT EXISTS (SELECT * FROM endpoint_network);

insert into endpoint_client (id, endpoint_id, endpoint_network_id, limitation_in_tps, limitation_in_total)
    select 1, 1, 1, 3, 10 union
    select 2, 2, 2, 1, 1
    WHERE NOT EXISTS (SELECT * FROM endpoint_client);

insert into merchant (id, name)
    select 1, 'm1' union
    select 2, 'm2'
    WHERE NOT EXISTS (SELECT * FROM merchant);

insert into service_order_group (id, service_id, merchant_id, name)
    select 1, 1, 1, 'group1' union
    select 2, 1, 2, 'group2'
    WHERE NOT EXISTS (SELECT * FROM service_order_group);

insert into service_order (service_order_group_id, endpoint_id, orders)
    select 1, 1, 1 union
    select 1, 2, 2 union
    select 2, 2, 1 union
    select 2, 1, 2
    WHERE NOT EXISTS (SELECT * FROM service_order_group);

insert into merchant_client (merchant_id, endpoint_client_id)
    select 1, 1 union --
    select 1, 2 union
    select 2, 1 union
    select 2, 2
    WHERE NOT EXISTS (SELECT * FROM merchant);

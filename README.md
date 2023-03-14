# db2kafka

Example project of play with different types of load data from RDBMS (PostgreSQL) to Stream service (Kafka).

## Notes
### Create test table
```sql
create table TEST_DATA_200k as
SELECT generate_series(1,200000) AS id, md5(random()::text) AS descr;
alter table test_data_200k ALTER column id SET not null;
alter table test_data_200k add PRIMARY KEY(ID);
alter table test_data_200k add processed BOOLEAN DEFAULT FALSE;
```
### Start redpanda and web-ui (kafka replacement)

```shell
docker run --rm \
	--name=redpanda-1 \
	-v $(pwd)/redpanda-1:/var/opt/project \
	--privileged \
	--network=host \
	docker.redpanda.com/vectorized/redpanda:v22.3.11 \
	redpanda start --overprovisioned --smp 1  --memory 256M --reserve-memory 0M --node-id 0 --check=false \
	--kafka-addr INSIDE://0.0.0.0:29092,OUTSIDE://0.0.0.0:9092 \
	--advertise-kafka-addr INSIDE://redpanda-1:29092,OUTSIDE://192.168.50.185:9092
```

```shell
docker run --rm --network=host -e KAFKA_BROKERS=192.168.50.185:9092  docker.redpanda.com/vectorized/console:v2.2.0
```

---
This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Related Guides

- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL database using the reactive pattern
- Apache Kafka Client ([guide](https://quarkus.io/guides/kafka)): Connect to Apache Kafka with its native API

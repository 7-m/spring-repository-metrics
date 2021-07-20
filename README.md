# Getting Started

This project is a demonstration of recording Spring Repository Metrics. More about it here [blog link]()
## Running 
To run this app, do

```shell
$ ./gradlew bootRun
```
API Endpoints
- Create a person

```shell
curl --request POST \
  --url http://localhost:8080/person \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "elrich",
	"address": {
		"line1" : "silicon valley",
		"line2" : "san francisco"
	}
}'
```

- Get person by name
```shell
curl --request GET \
  --url http://localhost:8080/person-name/elrich
```

- Get person by ID
```shell
curl --request GET \
  --url http://localhost:8080/person/{id}
```
- Get Spring Repository metrics
```shell
curl --request GET \
  --url http://localhost:8080/repo-metrics
```

Note: Before GETing the metrics, hit any one of the CRUD API's atleast once to populate the `MeterRegistry` otherwise the metrics API will return 500.